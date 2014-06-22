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

import zebradev.zebraviews.fakeclient.ClientCommands;
import zebradev.zebraviews.fakeclient.FakeClientManager;

import com.esotericsoftware.minlog.Log;

public class CommandInterpreter {

	private ServerManager serverManager;
	private FakeClientManager clientManager;
	
	public void interpret(String input) {
		
		if (input.length() < 8)
		{
			Log.error("Missing arguments");
			return;
		}
		String command = input.substring(8);
		
		if (input.charAt(1) == 's')
		{
			if (command.equals(ServerCommands.START.toString()))
			{
				try {
					if (serverManager != null)
					{
						Log.warn("Server already running!");
						return;
					}
					this.serverManager = new ServerManager();
				} catch (Exception e) {
					Log.error("Exception occurred initializing server!", e);
					Log.info("Stopping server....");
					return;
				}
			}
			else if (command.equals(ServerCommands.STOP.toString()))
			{
				if (this.serverManager != null)
				{
					Log.info("Stopping server....");
					this.serverManager.stop();
					this.serverManager = null;
					Log.info("Server stopped");
				}
				else
				{
					Log.warn("Server not running!");
					return;
				}
			}
			// Handle other possible responses in if/else blocks
		}
		else
		{
			if (command.equals(ClientCommands.CONNECT.toString()))
			{
				try {
					if (clientManager != null)
					{
						Log.warn("Client already running!");
						return;
					}
					this.clientManager = new FakeClientManager();
				} catch (Exception e) {
					Log.error("Exception occurred connecting client!", e);
					Log.warn("Connection was not established");
					return;
				}
			}
			else if (command.equals(ClientCommands.DISCONNECT.toString()))
			{
				if (this.clientManager != null)
				{
					this.clientManager.stop();
					this.clientManager = null;
					Log.info("Client disconnected");
				}
				else
				{
					Log.warn("Client not connected!");
					return;
				}
			}
		}
	}
}
