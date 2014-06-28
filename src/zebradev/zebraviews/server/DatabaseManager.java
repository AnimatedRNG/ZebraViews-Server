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
