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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zebradev.zebraviews.fakeclient.ClientCommands;
import zebradev.zebraviews.fakeclient.ClientManager;
import zebradev.zebraviews.fakeclient.ClientRequestListener;

import com.esotericsoftware.minlog.Log;

public class CommandInterpreter {

	private ServerManager serverManager;
	private ClientManager clientManager;
	
	public void interpret(String input) {
		
		if (input.length() < 8)
		{
			Log.error("Missing arguments");
			return;
		}
		
		String command = input.substring(8, this.getNextWordIndex(7, input));
		List<String> allArgs = this.getAllArguments(7, input);
		
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
					this.clientManager = null;
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
			if (command.equals(ClientCommands.RUN_MACRO.toString()))
			{
				try {
					@SuppressWarnings("resource")
					BufferedReader macroReader = new BufferedReader(
							new FileReader("config/macro.config"));
					
					String line;
					while ((line = macroReader.readLine()) != null && (line.length() > 7))
					{
						if (line.substring(8, this.getNextWordIndex(7, line))
								.equals(ClientCommands.RUN_MACRO.toString()))
						{
							Log.error("Macro is recursive!");
							return;
						}
						try {
							Log.info("macro", line);
							this.interpret(line);
						} catch (Exception e) {
							Log.error("Exception running macro", e);
						}
					}
					
					macroReader.close();
				} catch (IOException a) {
					Log.error("IO error", a);
					return;
				}
			}
			if (command.equals(ClientCommands.CONNECT.toString()))
			{
				try {
					if (clientManager != null)
					{
						Log.warn("Client already running!");
						return;
					}
					this.clientManager = new ClientManager(new ClientRequestListener());
				} catch (Exception e) {
					Log.error("Exception occurred connecting client!", e);
					Log.warn("Connection was not established");
					return;
				}
			}
			else 
			{
				if (clientManager == null)
				{
					Log.warn("Client not connected!");
					return;
				}
				if (command.equals(ClientCommands.DISCONNECT.toString()))
				{
					this.clientManager.stop();
					this.clientManager = null;
					Log.info("Client disconnected");
				}
				else if (command.equals(ClientCommands.LOGIN.toString()))
				{
					if (allArgs.size() != 3)
					{
						Log.error("Incorrect number of arguments");
						return;
					}
					
					this.clientManager.login(allArgs.get(1), allArgs.get(2));
				}
				else if (command.equals(ClientCommands.SIGNUP.toString()))
				{
					if (allArgs.size() != 3)
					{
						Log.error("Incorrect number of arguments");
						return;
					}
					
					this.clientManager.signup(allArgs.get(1), allArgs.get(2));
				}
				else if (command.equals(ClientCommands.PRODUCT_SEARCH.toString())) {
					if (allArgs.size() != 3)
					{
						Log.error("Incorrect number of arguments");
						return;
					}
					
					this.clientManager.sendProductSearchRequest(allArgs.get(1), allArgs.get(2));
				}
			}
		}
	}
	
	private int getNextWordIndex(int spaceIndex, String string) {
		int index = spaceIndex + 1;
		for (; index < string.length() && string.charAt(index) != ' '; index++);
		return index;
	}
	
	// Gets all command arguments and command name
	private List<String> getAllArguments(int firstSpaceIndex, String input) {
		List<String> args = new ArrayList<String>();
		int newIndex;
		for (int index = firstSpaceIndex; index < input.length() - 1; index = newIndex)
		{
			newIndex = this.getNextWordIndex(index, input);
			String nextWord = input.substring(index + 1, newIndex);
			args.add(nextWord);
		}
		return args;
	}
}
