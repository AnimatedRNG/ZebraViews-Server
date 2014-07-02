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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import zebradev.zebraviews.common.ConfigManager;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class ServerManager {

	public final static String CONFIG_FILE = "config/server_config.xml";
	public final static String CONFIG_ELEMENT = "Server";
	private ConfigManager handler;
	private Server zebraViewsServer;
	public boolean stop;
	
	public ServerManager() throws IOException, ParserConfigurationException, SAXException {
		this.handler = new ConfigManager(CONFIG_FILE, CONFIG_ELEMENT);
		int port = Integer.parseInt(handler.get("port"));
		
		this.zebraViewsServer = new Server();
	    new Thread(zebraViewsServer).start();
	    zebraViewsServer.bind(port);
	    
	    zebraViewsServer.addListener(new ThreadedListener(new ServerRequestListener()));
	    
	    // Remove this once we add our serializer
	    Kryo kryo = this.zebraViewsServer.getKryo();
	    kryo.register(java.util.TreeMap.class);
	    kryo.register(zebradev.zebraviews.processor.Product.class);
	    kryo.register(java.util.ArrayList.class);
	    
	    Log.info("Server started on port " + port);
	}

	public synchronized void stop() {
		this.stop = true;
		this.zebraViewsServer.stop();
	}
}
