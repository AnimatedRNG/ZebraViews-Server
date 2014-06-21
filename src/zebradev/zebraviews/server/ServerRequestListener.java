package zebradev.zebraviews.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ServerRequestListener extends Listener {

	@Override
	public void connected(Connection connection) {
		Log.info("Server connected to " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void disconnected(Connection connection) {
		Log.info("Server disconnected from " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void received(Connection connection, Object object) {
		Log.info("Server received object from " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void idle(Connection connection) {
		Log.info("Server idle on " + connection.getRemoteAddressTCP());
	}
}
