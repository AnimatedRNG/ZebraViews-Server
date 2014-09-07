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

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import zebradev.zebraviews.common.ConfigManager;
import zebradev.zebraviews.common.Requests;
import zebradev.zebraviews.server.ServerManager;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.minlog.Log;

public class ClientManager {
	private ConfigManager serverConfig;
	private ConfigManager clientConfig;
	
	private Client zebraViewsClient;
	public boolean stop;
	
	public final static String CONFIG_FILE = "config/client_config.xml";
	public final static String CONFIG_ELEMENT = "Client";
	public final static int writeBufferSize = 16384;
	public final static int objectBufferSize = 8192;
	
	public ClientManager(Listener listener) throws IOException, ParserConfigurationException, SAXException {
		ConfigManager serverCfg = new ConfigManager(ServerManager.CONFIG_FILE, ServerManager.CONFIG_ELEMENT);
		ConfigManager clientCfg = new ConfigManager(ClientManager.CONFIG_FILE, ClientManager.CONFIG_ELEMENT);
		
		this.init(listener, serverCfg, clientCfg);
	}
	
	public ClientManager(Listener listener, String serverConfigFile, String clientConfigFile)
			throws IOException, ParserConfigurationException, SAXException {
		
		String serverFile = (serverConfigFile == null) ? ServerManager.CONFIG_FILE : serverConfigFile;
		String clientFile = (clientConfigFile == null) ? ClientManager.CONFIG_FILE : clientConfigFile;
		
		ConfigManager serverCfg = new ConfigManager(serverFile, ServerManager.CONFIG_ELEMENT);
		ConfigManager clientCfg = new ConfigManager(clientFile, ClientManager.CONFIG_ELEMENT);
		
		this.init(listener, serverCfg, clientCfg);
	}
	
	public ClientManager(Listener listener, InputStream serverConfigFile, InputStream clientConfigFile)
			throws IOException, ParserConfigurationException, SAXException {
		ConfigManager serverCfg = new ConfigManager(serverConfigFile, ServerManager.CONFIG_ELEMENT);
		ConfigManager clientCfg = new ConfigManager(clientConfigFile, ClientManager.CONFIG_ELEMENT);
		
		this.init(listener, serverCfg, clientCfg);
	}
	
	public void init(Listener listener, ConfigManager serverConfig, ConfigManager clientConfig)
			throws IOException, ParserConfigurationException, SAXException {
		this.serverConfig = serverConfig;
		this.clientConfig = clientConfig;
		
		int port = Integer.parseInt(serverConfig.get("port"));
		String ip = serverConfig.get("server_ip");
		int timeout = Integer.parseInt(clientConfig.get("timeout"));
		
		this.zebraViewsClient = new Client(writeBufferSize, objectBufferSize);
		new Thread(zebraViewsClient).start();
		
		this.zebraViewsClient.connect(timeout, ip, port);
		
		this.zebraViewsClient.addListener(new ThreadedListener(listener));
		
		// Remove this once we add our serializer
	    Kryo kryo = this.zebraViewsClient.getKryo();
	    kryo.register(java.util.TreeMap.class);
	    kryo.register(zebradev.zebraviews.processor.Product.class);
	    kryo.register(java.util.ArrayList.class);
	}
	
	public synchronized void login(String username, String password) {
		
		TreeMap<String, Object> loginRequest = Requests.generateRequest
				("type", Requests.LOGIN.value, "username", username, "password", password, "details", "");
		
		Log.info("Client logging in with username " + username + " and password " + password);
		
		this.zebraViewsClient.sendTCP(loginRequest);
	}
	
	public synchronized void signup(String username, String password) {
		
		TreeMap<String, Object> signupRequest = Requests.generateRequest
				("type", Requests.SIGNUP.value, "username", username, "password", password, "details", "");
		
		Log.info("Client signing up in with username " + username + " and password " + password);
		
		this.zebraViewsClient.sendTCP(signupRequest);
	}
	
	public synchronized void sendProductSearchRequest(String productType, String productCode) {
		TreeMap<String, Object> searchRequest = Requests.generateRequest
				("type", Requests.PRODUCT_SEARCH.value, "product_type", productType,
						"product_code", productCode, "allergen_list", "");
		
		Log.info("Client searching for product " + productCode + " of type " + productType);
		
		this.zebraViewsClient.sendTCP(searchRequest);
	}
	
	public synchronized void stop() {
		this.stop = true;
		this.zebraViewsClient.stop();
	}
}
