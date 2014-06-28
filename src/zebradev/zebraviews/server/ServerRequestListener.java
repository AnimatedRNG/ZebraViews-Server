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

import java.util.Hashtable;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ServerRequestListener extends Listener {

	private Hashtable<Connection, ConnectionManager> connections;
	
	public ServerRequestListener() {
		this.connections = new Hashtable<Connection, ConnectionManager>();
	}
	
	@Override
	public void connected(Connection connection) {
		Log.info("Server connected to " + connection);
		this.connections.put(connection, new ConnectionManager(connection));
	}
	
	@Override
	public void disconnected(Connection connection) {
		Log.info("Server disconnected from " + connection);
		this.connections.remove(connection);
	}
	
	@Override
	public void received(Connection connection, Object object) {
		Log.info("Server received object from " + connection);
		
		if (object instanceof FrameworkMessage.KeepAlive)
			return;
		
		this.connections.get(connection).received(object);
		
		if (this.connections.containsKey(connection))
			Log.debug("Server already connected to this client");
	}
	
	@Override
	public void idle(Connection connection) {
		Log.info("Server idle on " + connection);
	}
}
