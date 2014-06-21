package zebradev.zebraviews.server;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class ServerManager {

	public final static String CONFIG_FILE = "config/config.xml"; 
	private ConfigManager handler;
	private Server zebraViewsServer;
	public boolean stop;
	
	public ServerManager() throws IOException, ParserConfigurationException, SAXException {
		this.handler = new ConfigManager(CONFIG_FILE, "ZebraViews");
		int port = Integer.parseInt(handler.get("port"));
		
		this.zebraViewsServer = new Server();
	    new Thread(zebraViewsServer).start();
	    zebraViewsServer.bind(port);
	    
	    zebraViewsServer.addListener(new ThreadedListener(new RequestListener()));
	    
	    Log.info("Server started on port " + port);
	}

	public synchronized void stop() {
		this.stop = true;
		this.zebraViewsServer.stop();
	}
}
