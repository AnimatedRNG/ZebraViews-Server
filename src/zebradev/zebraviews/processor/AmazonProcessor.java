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

package zebradev.zebraviews.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import zebradev.zebraviews.common.ConfigManager;
import zebradev.zebraviews.common.Requests;
import zebradev.zebraviews.common.SignedRequestsHelper;

import com.esotericsoftware.minlog.Log;

public class AmazonProcessor extends Processor
{
	private String AWS_ACCESS_KEY_ID;
	private String AWS_SECRET_KEY;
	private String ENDPOINT;
	private String ITEM_ID;

	public AmazonProcessor(Product product) {
		this.setProduct(product);
		ConfigManager config = null;
		try {
			config = new ConfigManager("config/amazon_config.xml", "Amazon");
		} catch (Exception e) {
			Log.error("Error reading config file!", e);
			return;
		}
		this.AWS_ACCESS_KEY_ID = config.get("aws_access_key_id");
		this.AWS_SECRET_KEY = config.get("aws_secret_key");
		this.ENDPOINT = config.get("endpoint");
		this.ITEM_ID = (String) this.getProduct().getTop("product_code");
	}

	public String getAccessKey()
	{
		return AWS_ACCESS_KEY_ID;
	}

	public String getSecretKey()
	{
		return AWS_SECRET_KEY;
	}	
	
	public String getEndpoint()
	{
		return ENDPOINT;
	}	
	
	public String getItemID()
	{
		return ITEM_ID;
	}		
	
	public String constructRequestUrl()
	{
        SignedRequestsHelper helper;
        try
        {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
            return "product not available";
        }
		        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2013-08-01");
        params.put("Operation", "ItemLookup");
        params.put("IdType", ((String) this.getProduct().getTop("product_type")).toUpperCase());
        params.put("ItemId", ITEM_ID);
        if (!(((String) this.getProduct().getTop("product_type")).toUpperCase().equals("ASIN")))
        	params.put("SearchIndex", "All");
        params.put("ResponseGroup", "Large");
        params.put("AssociateTag", "zebra02a-20");

        String requestURL = helper.sign(params);
        return requestURL;
	}
	
	public String fetchItem(String requestUrl, String itemTag) throws Exception
	{
        String item = null;
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(requestUrl);
        Element itemNode = (Element) doc.getElementsByTagName("Item").item(0);
        Node fetchNode = itemNode.getElementsByTagName(itemTag).item(0);
        item = fetchNode.getTextContent();
        
        return item;
    }
    
    public String constructProductReviewUrl(String asin)
    {
    	return "http://www.amazon.com/product-reviews/"+asin+"/";
    }

	@Override
	protected void onExecute(Product product) throws ProcessingException
	{
		TreeMap<String, String> prices = new TreeMap<String, String>();
		boolean categoryFailed = false;
		String title = "";
		String description = "";
		String asin = "";
		String reviewsUrl = "";
		String requestUrl = this.constructRequestUrl();
		Double averageRating = 0.0;
		String salePrice = null;
		String listPrice = null;
		String usedPrice = null;
		String newPrice = null;
		String category = "";
		
		try {
			category = fetchItem(requestUrl, "ProductGroup");
		} catch (Exception e) {
			categoryFailed = true;
		}
		
		try
		{
		title = fetchItem(requestUrl, "Title");
		} 
		catch (Exception e)
		{
			if (categoryFailed) {
				throw new ProcessingException("AmazonProcessor", Requests.ESSENTIAL_BOTH,
					"Failed to fetch category and name", e);
			}
			
			throw new ProcessingException("AmazonProcessor", Requests.ESSENTIAL_NAME,
					"Failed to fetch name", e);
		}
		
		if (categoryFailed) {
			throw new ProcessingException("AmazonProcessor", Requests.ESSENTIAL_CATEGORY,
					"Failed to fetch category", null);
		}
		
		try
		{
		description = fetchItem(requestUrl, "Content");
		}
		catch (Exception e)
		{
			Log.warn("AmazonProcessor", "Failed to fetch description");
		}
		
		try
		{
		if (!(((String) this.getProduct().getTop("product_type")).toUpperCase()).equals("ASIN"))
			asin = fetchItem(requestUrl, "ASIN");
		else
			asin = ITEM_ID;
		}
		catch (Exception e)
		{
			throw new ProcessingException("AmazonProcessor", Requests.ESSENTIAL_BOTH,
					"Failed to fetch ASIN", e);
		}
		try 
		{
			salePrice = fetchItem(requestUrl, "SalePrice");
			if (salePrice != null)    
				if (!salePrice.equals("Too low to display"))
					prices.put("Sale price", salePrice.substring(salePrice.indexOf("$")));
		}
		
		catch (Exception e) 
		{
		}
		
		try
		{
			newPrice = fetchItem(requestUrl, "LowestNewPrice");
			if (newPrice != null)    
				if (!newPrice.equals("Too low to display"))
					prices.put("New price", newPrice.substring(newPrice.indexOf("$")));
		}
		
		catch (Exception e) 
		{
		}
		
		try
		{
			usedPrice = fetchItem(requestUrl, "LowestUsedPrice");
			if (usedPrice != null)    
				if (!usedPrice.equals("Too low to display"))
					prices.put("Used price", usedPrice.substring(usedPrice.indexOf("$")));
		}
		catch (Exception e) 
		{
		}
		
		try
		{
			listPrice = fetchItem(requestUrl, "ListPrice");
			if (listPrice != null)    
				if (!listPrice.equals("Too low to display"))
					prices.put("List price", listPrice.substring(listPrice.indexOf("$")));
		}
		catch (Exception e) 
		{
		}
				
		product.putTop("price", prices);
		product.putTop("product_name", title);
		product.putTop("asin", asin);
		product.putTop("category", category);
		        
		reviewsUrl = this.constructProductReviewUrl(asin);
		
		try 
		{
			org.jsoup.nodes.Document doc = Jsoup.connect(reviewsUrl).get();
				averageRating = Double.parseDouble((doc.select("span.asinReviewsSummary span[class^=sWSprite s_star] span").first().text()).substring(0,2));
		    
		} 
		catch (Exception e) {
			Log.warn("Jsoup preliminary scraping failed", e);
		}

		TreeMap<String, Object> amazonOtherInfo = new TreeMap<String, Object>();
		amazonOtherInfo.put("name", "AmazonProcessor_initial");
		amazonOtherInfo.put("description", description);
		if (averageRating != 0.0)
		   product.putTop("average rating", averageRating);
		product.add(amazonOtherInfo);
	}
}