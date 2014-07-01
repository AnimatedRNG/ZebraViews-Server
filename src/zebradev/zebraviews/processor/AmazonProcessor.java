package zebradev.zebraviews.processor;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import zebradev.zebraviews.common.SignedRequestsHelper;


public class AmazonProcessor
{
	private String AWS_ACCESS_KEY_ID;
	private String AWS_SECRET_KEY;
	private String ENDPOINT;
	private String ITEM_ID;

	public AmazonProcessor(String accessKey, String secretKey, String endPoint, String itemID)
	{
		AWS_ACCESS_KEY_ID = accessKey;
		AWS_SECRET_KEY = secretKey;
		ENDPOINT = endPoint;
		ITEM_ID = itemID;		
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

}