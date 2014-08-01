package zebradev.zebraviews.processor;

import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import zebradev.zebraviews.common.ConfigManager;
import zebradev.zebraviews.common.Requests;

import com.esotericsoftware.minlog.Log;

public class ProsperentProcessor extends Processor{
	private String prosperentkey = "";
	
	public ProsperentProcessor(Product product) {
		this.setProduct(product);
		ConfigManager config = null;
		try {
			config = new ConfigManager("config/prosperent_config.xml", "Prosperent");
		} catch (Exception e) {
			Log.error("Error reading config file!", e);
			return;
		}
		this.prosperentkey = config.get("prosperent_key");
	}

	public String fetchItem(String requestUrl, String itemTag) throws Exception
	{
        String item = null;
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(requestUrl);
        Node fetchNode = doc.getElementsByTagName(itemTag).item(0);
        item = fetchNode.getTextContent();
        
        return item;
    }
	
	public String constructRequestUrl() {
		String upc = (String) this.getProduct().getTop("product_code");
		String requestUrl = "http://api.prosperent.com/api/search?api_key="+prosperentkey+"&query="+upc+"&limit=1";
		return requestUrl;
	}
	
	@Override
	protected void onExecute(Product product) throws ProcessingException {
		if (((String) this.getProduct().getTop("product_type")).toUpperCase().equals("ISBN"))
			throw new ProcessingException("ProsperentProcessor", Requests.ESSENTIAL_BOTH,
				"Failed to fetch category and name", null);

		String requestUrl = constructRequestUrl();
		String name = "";
		String description = "";
		String price = "";

		try {
			name = fetchItem(requestUrl, "keyword");
		} catch (Exception e) {
			throw new ProcessingException("ProsperentProcessor", Requests.ESSENTIAL_BOTH,
				"Failed to fetch name", e);
		}

		try {
			description = fetchItem(requestUrl, "description");
		} catch (Exception e) {
			Log.warn("ProsperentProcessor", "Failed to fetch description");
		}
		
		try {
			price = fetchItem(requestUrl, "price");
		} catch (Exception e) {
			Log.warn("BestBuyProcessor", "Failed to fetch price");
		}	
	
		product.putTop("product_name", name);
		TreeMap<String, Object> prosperentOtherInfo = new TreeMap<String, Object>();
		prosperentOtherInfo.put("name", "ProsperentProcessor_initial");
		prosperentOtherInfo.put("description", description);
		prosperentOtherInfo.put("price", price);
		product.add(prosperentOtherInfo);
		
		throw new ProcessingException("ProsperentProcessor", Requests.ESSENTIAL_CATEGORY,
				"Failed to fetch category", null);
	}
}