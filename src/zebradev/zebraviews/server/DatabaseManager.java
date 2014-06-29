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

import java.util.List;
import java.util.TreeMap;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.esotericsoftware.minlog.Log;

public class DatabaseManager {

	private TreeMap<String, Object> object;
	private Sql2o sql2o;
	public static final String DB_URL = "jdbc:mysql://localhost:3306/mysql";
	public static final String ROOT = "root";
	public static final String PASS = "";
	
	public static final String LOGIN_QUERY = "SELECT password, details FROM login WHERE username=:NAME";
	public static final String SIGNUP_QUERY = "INSERT INTO login VALUES (:username, :password, :details)";
	
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
	
	// Returns true if the user is authorized, otherwise false
	public boolean login() {
		String username = ((String) object.get("username")).toLowerCase();
		String password = ((String) object.get("password"));
		
		if (!this.verifyValidity(username, password, ""))
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
		
		if (!this.verifyValidity(username, password, details))
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
	
	private boolean verifyValidity(String username, String password, String details) {
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
	
	private class LoginTask {
		private String password;
		private String details;
	}
}
