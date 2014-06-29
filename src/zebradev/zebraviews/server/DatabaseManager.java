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
	public static final String DB_URL = "jdbc:mysql://localhost:3306/mysql";
	public static final String ROOT = "root";
	public static final String PASS = "";
	
	public static final String LOGIN_QUERY = "SELECT password, details FROM login WHERE username=:NAME";

	public DatabaseManager(TreeMap<String, Object> obj) {
		this.object = obj;
	}
	
	// Returns true if the user is authorized, otherwise false
	public boolean login() {
		String username = ((String) object.get("username"));
		String password = ((String) object.get("password"));
		
		Sql2o sql2o = new Sql2o(DB_URL, ROOT, PASS);
		
		List<LoginTask> tasks = null;
		try(Connection con = sql2o.open()) {
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
		String username = ((String) object.get("username"));
		String password = ((String) object.get("password"));
		
		return false;
		// Return result from database
	}
	
	private class LoginTask {
		public String password;
		public String details;
	}
}
