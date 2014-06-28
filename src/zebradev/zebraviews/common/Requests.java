package zebradev.zebraviews.common;

import java.util.TreeMap;

public enum Requests {

	LOGIN ("login"), SIGNUP ("signup"), PRODUCT_SEARCH ("product_search"),
	UPC ("upc"), ISBN ("isbn"), EAN ("ean"), JAN ("jan"),
	LOGIN_RESPONSE ("login_response"), STATUS_SUCCESS ("SUCCESS"),
	STATUS_FAILURE ("FAILURE"), SIGNUP_RESPONSE ("signup_response"),
	SEARCH_RESPONSE_IMMEDIATE ("search_response_immediate"), 
	SEARCH_RESPONSE_INITIAL ("search_response_initial"), /*Add fail reasons here*/
	REVIEW_RESPONSE ("review_response"); // A lot more....
	
	public final String value;
	
	Requests(String val) {
		value = val;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	/**
	*  Lazy method of generating requests.
	*
	*  @param	params	 Correlates to each key-object pair.
	*/
	public static TreeMap<String, Object> generateRequest(Object... params) {
		if (params.length % 2 != 0)
			throw new RuntimeException("Unable to generate request, improper params provided");
		
		TreeMap<String, Object> request = new TreeMap<String, Object>();
		
		for (int a = 0; a < params.length; a += 2)
			request.put((String) params[a], params[a+1]);
		
		return request;
	}
}