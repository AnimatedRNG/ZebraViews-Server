package zebradev.zebraviews.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import zebradev.zebraviews.common.Requests;
import zebradev.zebraviews.processor.AmazonProcessor;
import zebradev.zebraviews.processor.Processor;
import zebradev.zebraviews.processor.Product;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class BasicInfoManager {

	private Product product;
	
	public BasicInfoManager(Product product) {
		this.product = product;
	}
	
	public void startProcessing() {
		ExecutorService executor = null;
		executor = Executors.newFixedThreadPool(ProductManager.MAX_THREADS);
		
		List<Processor> processors = this.getProcessors();
		
		long startTime = System.nanoTime();
		
		for (Processor processor : processors)
			executor.execute(processor);
		
		executor.shutdown();
		
		boolean finished;
		try {
			finished = executor.awaitTermination(ProductManager.BASIC_INFO_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			this.fail("Basic info mining interrupted ", e);
			return;
		}
		
		if (!finished)
		{
			this.fail("Basic info mining taking too long", null);
			return;
		}
		else
		{
			Log.info("Basic info mining took " + 
					(System.nanoTime()-startTime) / (Math.pow(10, 9)) + " seconds");
		}
		
		boolean succeeded = false;
		for (Processor processor : processors)
		{
			if (!processor.failed)
				succeeded = true;
		}
		
		if (!succeeded)
			this.fail("Basic info mining failed to find basic info", new RuntimeException());
		
		this.product.putTop("type", Requests.SEARCH_RESPONSE_INITIAL.value);
		this.product.putTop("status", "SUCCESS");
		this.product.putTop("fail_reason", "");
		
		success();
	}
	
	private List<Processor> getProcessors() {
		List<Processor> processorList = new ArrayList<Processor>();
		processorList.add(new AmazonProcessor(this.product));
		return processorList;
	}
	
	private void success() {
		Connection originConnection = (Connection) this.product.getTop("originConnection");
		this.product.removeFromTop("originConnection");
		
		originConnection.sendTCP(this.product);
		
		this.product.putTop("originConnection", originConnection);
		Log.info(originConnection.toString(), "Basic info sent successfully");
	}
	
	private void fail(String reason, Exception e) {
		Log.error(reason, e);
		
		this.product.putTop("type", Requests.SEARCH_RESPONSE_INITIAL.value);
		this.product.putTop("status", "FAILURE");
		this.product.putTop("fail_reason", reason);
		
		Connection originConnection = (Connection) this.product.getTop("originConnection");
		this.product.removeFromTop("originConnection");
		originConnection.sendTCP(this.product);
	}
}
