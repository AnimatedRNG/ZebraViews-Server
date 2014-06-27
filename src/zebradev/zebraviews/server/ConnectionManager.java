package zebradev.zebraviews.server;

import java.util.TreeMap;

import zebradev.zebraviews.common.Requests;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class ConnectionManager {

	private boolean loggedIn;
	private Connection connection;
	
	public ConnectionManager(Connection connection) {
		this.loggedIn = false;
		this.connection = connection;
	}
	
	public synchronized void received(Object object) {
		if (!(object instanceof TreeMap))
		{
			Log.error("Invalid object " + object);
			return;
		}
		
		@SuppressWarnings("unchecked")
		TreeMap<String, Object> obj = (TreeMap<String, Object>) object;
		
		// In case we need the IP for blacklisting purposes
		obj.put("originConnection", this.connection);
		
		String type = (String) obj.get("type");
		
		if (type == null)
		{
			Log.error("Request type not specified");
			return;
		}
		
		if (type.equals(Requests.LOGIN.value))
			this.loggedIn = new DatabaseManager(obj).login();
		else if (type.equals(Requests.SIGNUP.value))
		{
			if (loggedIn) loggedIn = false;
			new DatabaseManager(obj).signup();
		}
		else
			Log.error("Invalid request " + obj.get("type"));
	}
}
