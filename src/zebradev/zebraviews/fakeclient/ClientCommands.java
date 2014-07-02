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

package zebradev.zebraviews.fakeclient;

public enum ClientCommands {

	RUN_MACRO ("macro"), CONNECT ("connect"), DISCONNECT ("disconnect"),
	SEND ("send"), IDLE ("idle"), LOGIN ("login"), SIGNUP ("signup"), PRODUCT_SEARCH ("product_search");
	
	public String value;
	
	ClientCommands(String val) {
		value = val;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
