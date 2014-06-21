package zebradev.zebraviews.server;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigManager extends HashMap<String, String> {

	private static final long serialVersionUID = 8468298958064604185L;
	private String elementName;
	
	public ConfigManager(String fileName, String elementName) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(RequestManager.CONFIG_FILE, new ConfigHandler(this));
		this.elementName = elementName;
	}
	
	private class ConfigHandler extends DefaultHandler {
		
		private HashMap<String, String> config;
		
		ConfigHandler(HashMap<String, String> config) {
			this.config = config;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (!qName.equals(elementName))
				throw new SAXException("Incorrect XML file");
			for (int a = 0; a < attributes.getLength(); a++)
				config.put(attributes.getQName(a), attributes.getValue(a));
		}
	}
}
