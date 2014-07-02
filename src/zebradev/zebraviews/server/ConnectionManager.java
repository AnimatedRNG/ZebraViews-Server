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

import java.util.TreeMap;

import zebradev.zebraviews.common.Requests;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class ConnectionManager {

	private boolean loggedIn;
	private Connection connection;
	public static String greeting;
	private ProductManager productManager;
	private Thread productThread;
	
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
			Log.error(this.connection.toString(),
					"Request type not specified");
			return;
		}
		
		if (type.equals(Requests.LOGIN.value))
		{
			this.loggedIn = new DatabaseManager(obj).login();
			if (this.loggedIn)
				Log.info(this.connection.toString(),
						"Server successfully authenticated user " + obj.get("username"));
			else
				Log.info(this.connection.toString(),
						"Server did not authenticate user " + obj.get("username"));
			String status = (this.loggedIn) ? Requests.STATUS_SUCCESS.value : Requests.STATUS_FAILURE.value;
			this.connection.sendTCP(Requests.generateRequest("type", Requests.LOGIN_RESPONSE.value,
					"status", status, "message", ConnectionManager.greeting));
		}
		else if (type.equals(Requests.SIGNUP.value))
		{
			if (loggedIn) loggedIn = false;
			boolean signedUp = new DatabaseManager(obj).signup();
			if (signedUp)
				Log.info(this.connection.toString(),
						"Server successfully signed up user " + obj.get("username"));
			else
				Log.info(this.connection.toString(),
						"Server did not sign up user " + obj.get("username"));
			String status = (signedUp) ? Requests.STATUS_SUCCESS.value : Requests.STATUS_FAILURE.value;
			this.connection.sendTCP(Requests.generateRequest("type", Requests.SIGNUP_RESPONSE.value,
					"status", status));
		}
		else if (type.equals(Requests.PRODUCT_SEARCH.value))
		{
			if (!loggedIn)
			{
				Log.warn(this.connection.toString(),
						"Client not logged in. Rejecting product search request.");
				return;
			}
			
			if (!obj.containsKey("product_type") || !obj.containsKey("product_code")
					|| !obj.containsKey("allergen_list"))
			{
				Log.warn(this.connection.toString(),
						"Invalid product search, missing arguments!");
				return;
			}
			
			this.productManager = new ProductManager(obj);
			this.productThread = new Thread(this.productManager);
			this.productThread.start();
			
			Log.info(this.connection.toString(),
					"Client launched product search for product " + 
					obj.get("product_code") + " reported type " + obj.get("product_type"));
			
			TreeMap<String, Object> immediateResponse = Requests.generateRequest("type",
					Requests.SEARCH_RESPONSE_IMMEDIATE.value);
			this.connection.sendTCP(immediateResponse);
		}
		else
			Log.error(this.connection.toString(), "Invalid request " + obj.get("type"));
	}
}
