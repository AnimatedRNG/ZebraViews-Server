package zebradev.zebraviews.server;

import java.util.TreeMap;

public class DatabaseManager {

	private TreeMap<String, Object> object;

	public DatabaseManager(TreeMap<String, Object> obj) {
		this.object = obj;
	}
	
	// Returns true if the user is authorized, otherwise false
	public boolean login() {
		String username = ((String) object.get("username"));
		String password = ((String) object.get("password"));
		
		return false;
		// Return result from database
	}

	// Returns true if the user is authorized, otherwise false
	public boolean signup() {
		String username = ((String) object.get("username"));
		String password = ((String) object.get("password"));
		
		return false;
		// Return result from database
	}
}
