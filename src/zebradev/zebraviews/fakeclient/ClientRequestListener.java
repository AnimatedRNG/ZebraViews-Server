package zebradev.zebraviews.fakeclient;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ClientRequestListener extends Listener {
	
	@Override
	public void connected(Connection connection) {
		Log.info("Client connected to " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void disconnected(Connection connection) {
		Log.info("Client disconnected from " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void received(Connection connection, Object object) {
		Log.info("Client received object from " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void idle(Connection connection) {
		Log.info("Client idle on " + connection.getRemoteAddressTCP());
	}
}
