package zebradev.zebraviews.processor;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import zebradev.zebraviews.common.SignedRequestsHelper;


public class ServerTester {
	private static final String AWS_ACCESS_KEY_ID = "AKIAJP2DY23T4QRM3KOA";

	private static final String AWS_SECRET_KEY = "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";
	
	private static final String ENDPOINT = "ecs.amazonaws.com";
	
	private static final String ITEM_ID = "0545010225";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		    
		    //public static void main(String[] args) {
		        /*
		         * Set up the signed requests helper 
		         */
		        SignedRequestsHelper helper;
		        try {
		            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		        } catch (Exception e) {
		            e.printStackTrace();
		            return;
		        }
		        
		        String requestUrl = null;
		        String title = null;
		        String description = null;
		        String asin = null;
		        String reviewsUrl = null;

		        /* The helper can sign requests in two forms - map form and string form */
		        
		        /*
		         * Here is an example in map form, where the request parameters are stored in a map.
		         */
		        System.out.println("Map form example:");
		        Map<String, String> params = new HashMap<String, String>();
		        params.put("Service", "AWSECommerceService");
		        params.put("Version", "2013-08-01");
		        params.put("Operation", "ItemLookup");
		        params.put("ItemId", ITEM_ID);
		        params.put("ResponseGroup", "Large");
		        params.put("AssociateTag", "zebra02a-20");

		        requestUrl = helper.sign(params);
		        System.out.println("Signed Request is \"" + requestUrl + "\"");

		        title = fetchItem(requestUrl, "Title");
		        System.out.println("Signed Title is \"" + title + "\"");
		        System.out.println();
		        
		        description = fetchItem(requestUrl, "Content");
		        System.out.println("Signed Description is \"" + description + "\"");

		        asin = fetchItem(requestUrl, "ASIN");
		        System.out.println("Signed ASIN is \"" + asin + "\"");
		        
		        reviewsUrl = constructProductReviewURL(asin);
		        System.out.println(reviewsUrl);
	}
		       		        
		    private static String fetchItem(String requestUrl, String tag) {
		        String item = null;
		        try {
		            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		            DocumentBuilder db = dbf.newDocumentBuilder();
		            Document doc = db.parse(requestUrl);
		            Node itemNode = doc.getElementsByTagName(tag).item(0);
		            item = itemNode.getTextContent();
		        } catch (Exception e) {
		        	throw new RuntimeException(e);
		        }
		        return item;
		    }
		    
		    private static String constructProductReviewURL(String asin)
		    {
		    	return "http://www.amazon.com/product-reviews/"+asin+"/";
		    }
}
