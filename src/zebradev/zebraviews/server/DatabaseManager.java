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

package zebradev.zebraviews.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import zebradev.zebraviews.processor.Product;

import com.esotericsoftware.minlog.Log;

public class DatabaseManager {

	private TreeMap<String, Object> object;
	private Sql2o sql2o;
	public static final String DB_URL = "jdbc:mysql://localhost:3306/mysql";
	public static final String ROOT = "root";
	public static final String PASS = "";
	
	public static final String LOGIN_QUERY = "SELECT password, details FROM login WHERE username=:NAME";
	public static final String SIGNUP_QUERY = "INSERT INTO login VALUES (:username, :password, :details)";
	public static final String CACHE_STORE_QUERY = "INSERT INTO CachedSearches (ProductCode, Details, Product) VALUES (?,?,?)";
	public static final String CACHE_SEARCH_QUERY = "Select Product FROM CachedSearches WHERE ProductCode = ?";
	 
	public static final int USERNAME_MIN_LENGTH = 4;
	public static final int USERNAME_MAX_LENGTH = 30;
	
	public static final int PASSWORD_MIN_LENGTH = 8;
	public static final int PASSWORD_MAX_LENGTH = 30;
	
	public static final int DETAILS_MAX_LENGTH = 20;

	public DatabaseManager(TreeMap<String, Object> obj) {
		this.object = obj;
		
		try {
			this.sql2o = new Sql2o(DB_URL, ROOT, PASS);
		} catch (Exception e) {
			Log.error("Error initializing database", e);
		}
	}
	
	public DatabaseManager() {
	}
	
	// In database the timestamp is of data type timestamp and will update automatically
	public synchronized void cacheProduct(String productCode, String details, Product prod) {
		byte[] binaryProduct = null;
		try {
			binaryProduct = Serializer.serialize(prod);
		} catch (IOException e) {
			Log.error("Error serializing product object");
		}
		PreparedStatement stmt = null;
		
		java.sql.Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URL, ROOT, PASS);
		
			stmt = conn.prepareStatement(CACHE_STORE_QUERY);
			stmt.setString(1, productCode);
			stmt.setString(2, details);
			stmt.setBytes(3, binaryProduct);
			stmt.executeUpdate();
		} catch (SQLException e) {
			Log.error("Error caching information");
		}
		
	}
	
	public synchronized Product cacheSearch(String productCode) {
		java.sql.Connection conn = null;
		PreparedStatement stmt2 = null;
		Product retrievedProduct = null;
		try {
			conn = DriverManager.getConnection(DB_URL, ROOT, PASS);
			stmt2 = conn.prepareStatement(CACHE_SEARCH_QUERY);
			byte[] retrievedBytes = null;
			stmt2.setString(1, productCode);
			ResultSet rs = stmt2.executeQuery();
			while (rs.next()) {
				retrievedBytes = rs.getBytes("Product");
			}
			retrievedProduct = (Product) Serializer.deserialize(retrievedBytes);
		} catch (SQLException | ClassNotFoundException | IOException e) {
			Log.error("Error retrieving product");
		}
		return retrievedProduct;
	}
	
	// Returns true if the user is authorized, otherwise false
	public boolean login() {
		String username = ((String) object.get("username")).toLowerCase();
		String password = ((String) object.get("password"));
		
		if (!DatabaseManager.verifyValidity(username, password, ""))
			return false;
		
		List<LoginTask> tasks = null;
		try {
			Connection con = this.sql2o.open();
			tasks = con.createQuery(LOGIN_QUERY)
					.addParameter("NAME", username).executeAndFetch(LoginTask.class);
		} catch (Exception e) {
			Log.error("Database error", e);
			return false;
		}
		
		if (tasks.size() == 0)
			return false;
		else if (tasks.size() == 1)
		{
			if (password.equals(tasks.get(0).password))
				return true;
			else
				return false;
		} else {
			Log.error("Multiple accounts with the same username!");
			return false;
		}
	}

	// Returns true if the user is signed up, otherwise false
	public boolean signup() {
		String username = ((String) object.get("username")).toLowerCase();
		String password = ((String) object.get("password"));
		String details = ((String) object.get("details"));
		
		if (!DatabaseManager.verifyValidity(username, password, details))
			return false;
		
		List<LoginTask> tasks = null;
		try {
			Connection con = this.sql2o.open();
			tasks = con.createQuery(LOGIN_QUERY)
					.addParameter("NAME", username).executeAndFetch(LoginTask.class);
			if (tasks.size() > 0)
			{
				Log.warn("Username " + username + "already exists in database");
				return false;
			}
			
			con.createQuery(SIGNUP_QUERY)
			.addParameter("username", username)
			.addParameter("password", password)
			.addParameter("details", details)
			.executeUpdate();
			
			return true;
			
		} catch (Exception e) {
			Log.error("Database error", e);
			return false;
		}
	}
	
	public static boolean verifyValidity(String username, String password, String details) {
		if (username.length() <= USERNAME_MAX_LENGTH && username.length() >= USERNAME_MIN_LENGTH)
		{
			if (password.length() <= PASSWORD_MAX_LENGTH && password.length() >= PASSWORD_MIN_LENGTH)
			{
				if (details.length() <= DETAILS_MAX_LENGTH)
					return true;
			}
		}
		return false;
	}
	
	private static class Serializer {
		
		public static byte[] serialize(Object obj) throws IOException {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(obj);
		    return out.toByteArray();
		}
		
		public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		    ByteArrayInputStream in = new ByteArrayInputStream(data);
		    ObjectInputStream is = new ObjectInputStream(in);
		    return is.readObject();
		}
	}

	private class LoginTask {
		private String password;
		private String details;
	}
}
