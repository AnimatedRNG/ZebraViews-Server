package zebradev.zebraviews.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import zebradev.zebraviews.common.ConfigManager;
import zebradev.zebraviews.common.Requests;

import com.esotericsoftware.minlog.Log;

public class BestBuyProcessor extends Processor{

		private String bestBuyKey = "";
		
	public BestBuyProcessor(Product product) {
		this.setProduct(product);
		ConfigManager config = null;
		try {
			config = new ConfigManager("config/bestbuy_config.xml", "BestBuy");
		} catch (Exception e) {
			Log.error("Error reading config file!", e);
			return;
		}
		this.bestBuyKey = config.get("bestbuy_key");
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
		String requestURL = "http://api.remix.bestbuy.com/v1/products(upc=" + upc + "*)" +
			"?show=name,customerReviewAverage,regularPrice,salePrice,longDescription,department,includedItemList.includedItem&" +
			"apiKey=" + this.bestBuyKey;
		return requestURL;
	}
	
	@Override
	protected void onExecute(Product product) throws ProcessingException {
		if(bestBuyKey.equals("")||bestBuyKey==null)
			throw new ProcessingException("BestBuyProcessor", Requests.ESSENTIAL_BOTH,
					"Failed to fetch API Key", null);
		
		if (!((String) this.getProduct().getTop("product_type")).toUpperCase().equals("UPC"))
			throw new ProcessingException("BestBuyProcessor", Requests.ESSENTIAL_BOTH,
				"Failed to fetch category and name", null);

		TreeMap<String, String> prices = new TreeMap<String, String>();
		String requestUrl = constructRequestUrl();
		String name = "";
		String description = "";
		Double averageRating = 0.0;
		String includedItemList = "";
		String category = "";
		Boolean categoryFailed = false;
		
		try {
			category = fetchItem(requestUrl, "department");
		} catch (Exception e) {
			categoryFailed = true;
			
		}
		try {
			name = fetchItem(requestUrl, "name");
		} catch (Exception e) {
			if (categoryFailed) {
				throw new ProcessingException("BestBuyProcessor", Requests.ESSENTIAL_BOTH,
					"Failed to fetch category and name", e);
			}
			
			throw new ProcessingException("BestBuyProcessor", Requests.ESSENTIAL_NAME,
					"Failed to fetch name", e);
		}
		
		if (categoryFailed) {
			throw new ProcessingException("BestBuyProcessor", Requests.ESSENTIAL_CATEGORY,
					"Failed to fetch category", null);
		}
		
		try {
			description = fetchItem(requestUrl, "longDescription");
		} catch (Exception e) {
			Log.warn("BestBuyProcessor", "Failed to fetch description");
		}
		
		try {
			averageRating = Double.parseDouble(fetchItem(requestUrl, "customerReviewAverage"));
		} catch (Exception e) {
			Log.warn("BestBuyProcessor", "Failed to fetch average rating");
		}
		try
		{
			String salePrice = fetchItem(requestUrl, "salePrice");
			if (salePrice != null)    
				prices.put("Sale price", "$" + salePrice);
		}
		
		catch (Exception e) 
		{
		}
		
		try
		{
			String regPrice = fetchItem(requestUrl, "regularPrice");
			if (regPrice != null)    
				prices.put("Regular price", "$" + regPrice);
		}
		
		catch (Exception e) 
		{
		}
	
		product.putTop("price", prices);
		product.putTop("product_name", name);
		product.putTop("category", category);
		TreeMap<String, Object> bestBuyOtherInfo = new TreeMap<String, Object>();
		bestBuyOtherInfo.put("name", "BestBuyProcessor_initial");
		bestBuyOtherInfo.put("description", description);
		if (averageRating != 0.0)
		   product.putTop("average_rating", averageRating);
		product.add(bestBuyOtherInfo);
	}
}