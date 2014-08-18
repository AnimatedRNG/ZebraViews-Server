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
	private String awsAccessKey;
	private String awsSecretKey;
	private String endpoint;
	private String itemID;
	private boolean useAsin;

	public AmazonProcessor(Product product) {
		this.setProduct(product);
		ConfigManager config = null;
		try {
			config = new ConfigManager("config/amazon_config.xml", "Amazon");
		} catch (Exception e) {
			Log.error("Error reading config file!", e);
			return;
		}
		this.awsAccessKey = config.get("aws_access_key_id");
		this.awsSecretKey = config.get("aws_secret_key");
		this.endpoint = config.get("endpoint");
		this.itemID = (String) this.getProduct().getTop("product_code");
	}

	public String getAccessKey()
	{
		return awsAccessKey;
	}

	public String getSecretKey()
	{
		return awsSecretKey;
	}	
	
	public String getEndpoint()
	{
		return endpoint;
	}	
	
	public String getItemID()
	{
		return itemID;
	}		
	
	public String constructRequestUrl() throws Exception
	{
        SignedRequestsHelper helper;
        try
        {
            helper = SignedRequestsHelper.getInstance(endpoint, awsAccessKey, awsSecretKey);
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
            return "product not available";
        }
		
        String productType = ((String) this.getProduct().getTop("product_type")).toUpperCase();
        
        if (productType.equals(Requests.UPC_A.value) || productType.equals(Requests.UPC_E.value))
        	productType = productType.substring(0, 3);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Version", "2013-08-01");
        params.put("Operation", "ItemLookup");
        params.put("IdType", productType);
        params.put("ItemId", itemID);
        if (!(((String) this.getProduct().getTop("product_type")).toUpperCase().equals("ASIN")))
        	params.put("SearchIndex", "All");
        params.put("ResponseGroup", "Large");
        params.put("AssociateTag", "zebra02a-20");
        params.put("Sort", "relevancerank");

        String requestURL = helper.sign(params);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(requestURL);
        int numberOfItems = doc.getElementsByTagName("Item").getLength();
        if (numberOfItems != 1)
        	useAsin = true;
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
		if(awsAccessKey.equals("")||awsAccessKey==null||awsSecretKey.equals("")||awsSecretKey==null)
			throw new ProcessingException("AmazonProcessor", Requests.ESSENTIAL_BOTH,
					"Failed to fetch API Key(s)", null);
		
		TreeMap<String, String> prices = new TreeMap<String, String>();
		boolean categoryFailed = false;
		String title = "";
		String description = "";
		String asin = "";
		String reviewsUrl = "";
		String requestUrl = "";

		try
		{
			requestUrl = this.constructRequestUrl();
		}
		
		catch (Exception e) {
			throw new ProcessingException("AmazonProcessor", Requests.ESSENTIAL_BOTH,
					"Failed to construct request URL", e);
		}
		Double averageRating = 0.0;
		String salePrice = null;
		String listPrice = null;
		String usedPrice = null;
		String newPrice = null;
		String category = "";
		
		if (useAsin) {

			try 
			{
				org.jsoup.nodes.Document doc = Jsoup.connect("http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=" + itemID).get();
				String productLink = doc.select(".newaps [href]").first().toString();
				asin = productLink.substring(productLink.indexOf("/dp/")+4, productLink.indexOf("dp/") + 13);
			} 
			catch (Exception e) {
				Log.warn("Jsoup preliminary scraping failed", e);
			}
			
			SignedRequestsHelper helper = null;
	        try
	        {
	            helper = SignedRequestsHelper.getInstance(endpoint, awsAccessKey, awsSecretKey);
	        }
	        
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        
	        Map<String, String> params = new HashMap<String, String>();
	        params.put("Service", "AWSECommerceService");
	        params.put("Version", "2013-08-01");
	        params.put("Operation", "ItemLookup");
	        params.put("IdType", "ASIN");
	        params.put("ItemId", asin);
	        params.put("ResponseGroup", "Large");
	        params.put("AssociateTag", "zebra02a-20");
	        params.put("Sort", "relevancerank");
	        requestUrl = helper.sign(params);
		}
				
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
			
			if (!useAsin) {	
			if (!(((String) this.getProduct().getTop("product_type")).toUpperCase()).equals("ASIN"))
				asin = fetchItem(requestUrl, "ASIN");
			else
				asin = itemID;
			}
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
					averageRating = Double.parseDouble((doc.select("span.asinReviewsSummary span[class^=sWSprite s_star] span").first().text()).substring(0,3));
			    
			} 
			catch (Exception e) {
				Log.warn("Jsoup preliminary scraping failed", e);
			}
			
			TreeMap<String, Object> amazonOtherInfo = new TreeMap<String, Object>();
			amazonOtherInfo.put("name", "AmazonProcessor_initial");
			amazonOtherInfo.put("description", Jsoup.parse(description).text());
			if (averageRating != 0.0)
			   product.putTop("average_rating", averageRating);
			product.add(amazonOtherInfo);
	}
}
