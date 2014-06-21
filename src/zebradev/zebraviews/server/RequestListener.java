package zebradev.zebraviews.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class RequestListener extends Listener {

	@Override
	public void connected(Connection connection) {
		Log.info("Connected to " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void disconnected(Connection connection) {
		Log.info("Disconnected from " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void received(Connection connection, Object object) {
		Log.info("Received object from " + connection.getRemoteAddressTCP());
	}
	
	@Override
	public void idle(Connection connection) {
		Log.info("Idle on " + connection.getRemoteAddressTCP());
	}
}
