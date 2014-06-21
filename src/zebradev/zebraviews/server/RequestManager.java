package zebradev.zebraviews.server;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class RequestManager implements Runnable {

	public final static String CONFIG_FILE = "config/config.xml"; 
	private ConfigManager handler;
	
	public RequestManager() throws IOException, ParserConfigurationException, SAXException {
		this.handler = new ConfigManager(CONFIG_FILE, "ZebraViews");
	}
	
	@Override
	public void run() {
		// Add networking loop here
	}

}
