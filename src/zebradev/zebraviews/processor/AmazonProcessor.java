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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
        params.put("ItemId", ITEM_ID);
        params.put("ResponseGroup", "Large");
        params.put("AssociateTag", "zebra02a-20");

        String requestURL = helper.sign(params);		
		return requestURL;
	}
	
	public String fetchItem(String requestUrl, String itemTag)
	{
        String item = null;
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            Node itemNode = doc.getElementsByTagName(itemTag).item(0);
            item = itemNode.getTextContent();
        }
        
        catch (Exception e)
        {
        	throw new RuntimeException(e);
        }
        
        return item;
    }
    
    public String constructProductReviewUrl(String asin)
    {
    	return "http://www.amazon.com/product-reviews/"+asin+"/";
    }

	@Override
	protected void onExecute(Product product) {
		// TODO Auto-generated method stub
		
	}

}