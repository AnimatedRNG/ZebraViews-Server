package zebradev.zebraviews.fakeclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import zebradev.zebraviews.server.ConfigManager;
import zebradev.zebraviews.server.ServerManager;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

public class FakeClientManager {
	private ConfigManager serverConfig;
	private ConfigManager clientConfig;
	
	private Client zebraViewsClient;
	public boolean stop;
	
	public final static String CONFIG_FILE = "config/client_config.xml";
	public final static String CONFIG_ELEMENT = "Client";
	
	public FakeClientManager() throws IOException, ParserConfigurationException, SAXException {
		this.serverConfig = new ConfigManager(ServerManager.CONFIG_FILE,
				ServerManager.CONFIG_ELEMENT);
		this.clientConfig = new ConfigManager(FakeClientManager.CONFIG_FILE,
				FakeClientManager.CONFIG_ELEMENT);
		
		int port = Integer.parseInt(serverConfig.get("port"));
		String ip = serverConfig.get("server_ip");
		int timeout = Integer.parseInt(clientConfig.get("timeout"));
		
		this.zebraViewsClient = new Client();
		new Thread(zebraViewsClient).start();
		
		this.zebraViewsClient.connect(timeout, ip, port);
		
		this.zebraViewsClient.addListener(new ThreadedListener(new ClientRequestListener()));
	}
	
	public synchronized void stop() {
		this.stop = true;
		this.zebraViewsClient.stop();
	}
}
