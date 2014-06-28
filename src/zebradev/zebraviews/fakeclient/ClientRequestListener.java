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

package zebradev.zebraviews.fakeclient;

import java.util.Map;
import java.util.TreeMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ClientRequestListener extends Listener {
	
	@Override
	public void connected(Connection connection) {
		Log.info("Client connected to " + connection);
	}
	
	@Override
	public void disconnected(Connection connection) {
		Log.info("Client disconnected from " + connection);
	}
	
	@Override
	public void received(Connection connection, Object object) {
		Log.info("Client received object from " + connection);
		
		if (object instanceof TreeMap)
		{
			@SuppressWarnings("unchecked")
			TreeMap<String, Object> request = (TreeMap<String, Object>) object;
			for(Map.Entry<String, Object> entry : request.entrySet())
				Log.info("<" + entry.getKey() + ", " + entry.getValue() + ">");
		}
	}
	
	@Override
	public void idle(Connection connection) {
		//Log.info("Client idle on " + connection);
	}
}
