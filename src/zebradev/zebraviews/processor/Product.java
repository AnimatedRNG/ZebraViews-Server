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

package zebradev.zebraviews.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Product extends ConcurrentLinkedQueue<TreeMap<String, Object>> {

	private static final long serialVersionUID = -7183752795726987983L;

	public Product() {
		this.add(new TreeMap<String, Object>());
	}
	
	public synchronized Object getTop(String attributeName) {
		return this.peek().get(attributeName);
	}
	
	public synchronized void putAllTop(TreeMap<String, Object> addMap) {
		this.peek().putAll(addMap);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void putTop(String attributeName, Object object) {
		
		TreeMap<String, Object> top = this.peek();
		
		if (top.containsKey(attributeName) && top.get(attributeName) instanceof ArrayList)
			((List<Object>) top.get(attributeName)).add(object); 
		else
			top.put(attributeName, object);
	}
	
	public synchronized void removeFromTop(String attributeName) {
		this.peek().remove(attributeName);
	}
	
	// Intensive process -- complete in separate thread
	@SuppressWarnings("unchecked")
	public TreeMap<String, Object> concatenate() {
		TreeMap<String, Object> currentMap = new TreeMap<String, Object>();
		for (Object a : this.toArray())
			for (Map.Entry<String, Object> entry : ((TreeMap<String, Object>) a).entrySet())
				currentMap.put(((TreeMap<String, Object>) a).get("name") + "_" + entry.getKey(), entry.getValue());
		return currentMap;
	}
}
