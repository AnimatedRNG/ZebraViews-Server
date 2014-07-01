package zebradev.zebraviews.processor;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Product extends ConcurrentLinkedQueue<TreeMap<String, Object>> {

	private static final long serialVersionUID = -7183752795726987983L;

	public Product() {
		this.add(new TreeMap<String, Object>());
	}
	
	public synchronized void putTop(String attributeName, Object object) {
		this.peek().put(attributeName, object);
	}
	
	public synchronized Object getTop(String attributeName) {
		return this.peek().get(attributeName);
	}
	
	// Intensive process -- complete in separate thread
	@SuppressWarnings("unchecked")
	public TreeMap<String, Object> concatenate() {
		TreeMap<String, Object> currentMap = new TreeMap<String, Object>();
		for (Object a : this.toArray())
			currentMap.putAll((TreeMap<String, Object>) a);
		return currentMap;
	}
}
