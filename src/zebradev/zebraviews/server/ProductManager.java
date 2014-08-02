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

import java.util.ArrayList;
import java.util.TreeMap;

import zebradev.zebraviews.common.ConfigManager;
import zebradev.zebraviews.processor.Product;

import com.esotericsoftware.minlog.Log;

public class ProductManager implements Runnable {
	
	public static int MAX_THREADS;
	public static long BASIC_INFO_TIMEOUT = 5000;

	private Product product;
	
	public ProductManager(TreeMap<String, Object> request) {
		this.product = new Product();
		this.product.putAllTop(request);
		this.product.putTop("name", "top");
		this.product.putTop("stage", "basic-info");
		this.product.putTop("product_name", new ArrayList<Object>());
		this.product.putTop("category", new ArrayList<Object>());
		this.product.putTop("average_rating", new ArrayList<Object>());
		this.product.putTop("price", new ArrayList<Object>());
		
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
		BasicInfoManager basicInfo = new BasicInfoManager(this.product);
		basicInfo.startProcessing();
	}
}
