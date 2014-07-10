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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import zebradev.zebraviews.common.ConfigManager;
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
        Node itemNode = doc.getElementsByTagName(itemTag).item(0);
        item = itemNode.getTextContent();
        
        return item;
    }
    
    public String constructProductReviewUrl(String asin)
    {
    	return "http://www.amazon.com/product-reviews/"+asin+"/";
    }

	@Override
	protected void onExecute(Product product) throws ProcessingException
	{
		List<String> prices = new ArrayList<String>();
		String title = "";
		String description = "";
		String asin = "";
		String reviewsUrl = "";
		String requestUrl = this.constructRequestUrl();
		Double averageRating = 0.0;
		String price = "";
		
		try
		{
		title = fetchItem(requestUrl, "Title");
		} 
		catch (Exception e)
		{
			Log.warn("AmazonProcessor", "Failed to fetch title");
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
			asin = ((String) this.getProduct().getTop("product_type")).toUpperCase();
		}
		catch (Exception e)
		{
			throw new ProcessingException("AmazonProcessor", "Failed to fetch ASIN", e);
		}
		try 
		{
			price = fetchItem(requestUrl, "SalePrice");
			if (price != null)    
			{
				price = price.substring(price.indexOf('$') + 1);
				prices.add(price);
			}
		
			price = fetchItem(requestUrl, "LowestNewPrice");
			if (price != null)    
			{
				price = price.substring(price.indexOf('$') + 1);
				prices.add(price);
			}
			
			price = fetchItem(requestUrl, "LowestUsedPrice");
			if (price != null)    
			{
				price = price.substring(price.indexOf('$') + 1);
				prices.add(price);
			}
		
			price = fetchItem(requestUrl, "ListPrice");
			if (price != null)    
			{
				price = price.substring(price.indexOf('$') + 1);
				prices.add(price);
			}
		}
		catch (Exception e) 
		{
		}
				
		if (prices != null)    
			{
			product.putTop("price", prices);
			}
		product.putTop("product_name", title);
		product.putTop("asin", asin);
		        
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