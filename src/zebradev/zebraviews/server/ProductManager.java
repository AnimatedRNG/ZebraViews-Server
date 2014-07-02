//	This file is part of ZebraViews.
//
//	Copyright 2014 AnimatedJuzz <kazasrinivas3@gmail.com>
//
//	ZebraViews is free software: you can redistribute it and/or modify
//	under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//
//	ZebraViews is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with ZebraViews.  If not, see <http://www.gnu.org/licenses/>.

package zebradev.zebraviews.server;

import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import zebradev.zebraviews.common.ConfigManager;
import zebradev.zebraviews.common.Requests;
import zebradev.zebraviews.processor.AmazonProcessor;
import zebradev.zebraviews.processor.Processor;
import zebradev.zebraviews.processor.Product;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class ProductManager implements Runnable {
	
	public static int MAX_THREADS;
	public static long BASIC_INFO_TIMEOUT = 5000;

	private Product product;
	
	public ProductManager(TreeMap<String, Object> request) {
		this.product = new Product();
		this.product.putAllTop(request);
		this.product.putTop("stage", "basic-info");
		
		ConfigManager productManagerConfig;
		try {
			productManagerConfig = 
					new ConfigManager("config/product_manager_config.xml", "product_manager");
		} catch (Exception e) {
			Log.error("Error reading config file!", e);
			return;
		}
		
		ProductManager.MAX_THREADS = Integer.parseInt(productManagerConfig.get("max_threads"));
		ProductManager.BASIC_INFO_TIMEOUT = Long.parseLong(productManagerConfig.get("basic_info_timeout"));
	}
	
	@Override
	public void run() {
		ExecutorService executor = null;
		executor = Executors.newFixedThreadPool(ProductManager.MAX_THREADS);
		
		// Add more basic info executors later
		Processor amazon = new AmazonProcessor(this.product);
		executor.execute(amazon);
		
		executor.shutdown();
		
		boolean finished;
		try {
			finished = executor.awaitTermination(BASIC_INFO_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			this.fail("Basic info mining interrupted ", e);
			return;
		}
		
		if (!finished)
		{
			this.fail("Basic info mining taking too long", null);
			return;
		}
		
		this.product.putTop("type", Requests.SEARCH_RESPONSE_INITIAL.value);
		this.product.putTop("status", "SUCCESS");
		this.product.putTop("fail_reason", "");
		
		Connection originConnection = (Connection) this.product.getTop("originConnection");
		this.product.removeFromTop("originConnection");
		
		originConnection.sendTCP(this.product);
		
		this.product.putTop("originConnection", originConnection);
		Log.info(originConnection.toString(), "Basic info sent successfully");
	}
	
	public void fail(String reason, Exception e) {
		Log.error(reason, e);
		
		this.product.putTop("type", Requests.SEARCH_RESPONSE_INITIAL.value);
		this.product.putTop("status", "FAILURE");
		this.product.putTop("fail_reason", reason);
		
		Connection originConnection = (Connection) this.product.getTop("originConnection");
		this.product.removeFromTop("originConnection");
		originConnection.sendTCP(this.product);
	}
}
