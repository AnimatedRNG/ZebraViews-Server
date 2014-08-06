package zebradev.zebraviews.processor;

import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

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
        
        JSONObject jsonResponse = JSONRequest.getRequest(requestUrl);
		JSONArray jsonParsedResponse = (JSONArray) jsonResponse.get("data");
		JSONObject jsonResponseObject = (JSONObject) jsonParsedResponse.get(0);
		item = jsonResponseObject.get(itemTag).toString();
             
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
		String category = "";
		Boolean categoryFailed = false;

		try {
			category = fetchItem(requestUrl, "category");
		} catch (Exception e) {
			categoryFailed = true;
		}
		
		try {
			name = fetchItem(requestUrl, "keyword");
		} catch (Exception e) {
			if (categoryFailed) {
				throw new ProcessingException("ProsperentProcessor", Requests.ESSENTIAL_BOTH,
					"Failed to fetch category and name", e);
			}
			
			throw new ProcessingException("ProsperentProcessor", Requests.ESSENTIAL_NAME,
					"Failed to fetch name", e);
		}
		
		if (categoryFailed) {
			throw new ProcessingException("ProsperentProcessor", Requests.ESSENTIAL_CATEGORY,
					"Failed to fetch category", null);
		}

		try {
			description = fetchItem(requestUrl, "description");
		} catch (Exception e) {
			Log.warn("ProsperentProcessor", "Failed to fetch description");
		}
		
		try {
			price = fetchItem(requestUrl, "price");
		} catch (Exception e) {
			Log.warn("ProsperentProcessor", "Failed to fetch price");
		}	
	
		product.putTop("product_name", name);
		product.putTop("category", category);
		TreeMap<String, Object> prosperentOtherInfo = new TreeMap<String, Object>();
		prosperentOtherInfo.put("name", "ProsperentProcessor_initial");
		prosperentOtherInfo.put("description", description);
		prosperentOtherInfo.put("price", price);
		product.add(prosperentOtherInfo);
	}
}