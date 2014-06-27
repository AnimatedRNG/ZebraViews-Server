package zebradev.zebraviews.common;

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
}