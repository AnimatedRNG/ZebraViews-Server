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

package zebradev.zebraviews.common;

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
		saxParser.parse(fileName, new ConfigHandler(this, elementName));
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
