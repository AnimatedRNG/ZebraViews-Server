package zebradev.zebraviews.server;

import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigManager extends Hashtable<String, String> {

	private static final long serialVersionUID = 8468298958064604185L;
	
	public ConfigManager(String fileName, String elementName) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(RequestManager.CONFIG_FILE, new ConfigHandler(this, elementName));
	}
	
	private class ConfigHandler extends DefaultHandler {
		
		private Hashtable<String, String> config;
		private String elementName;
		
		ConfigHandler(Hashtable<String, String> config, String elementName) {
			this.config = config;
			this.elementName = elementName;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (!qName.equals(this.elementName))
				throw new SAXException("Incorrect XML file");
			for (int a = 0; a < attributes.getLength(); a++)
				config.put(attributes.getQName(a), attributes.getValue(a));
		}
	}
}
