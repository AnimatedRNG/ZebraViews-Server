package zebradev.zebraviews.server;

import com.esotericsoftware.minlog.Log;

public class CommandInterpreter {

	private RequestManager manager;
	
	public static final String START_RESPONSE = "Starting server....";
	
	public void interpret(String input) {
		if (input.charAt(1) == 's')
		{
			String command = input.substring(8);
			if (command.equals(ServerCommands.START.toString()))
			{
				try {
					if (manager != null)
					{
						Log.warn("Server already running!");
						return;
					}
					Log.info(START_RESPONSE);
					this.manager = new RequestManager();
					new Thread(manager).start();
				} catch (Exception e) {
					Log.error("Config files error!", e);
					Log.info("Stopping server....");
					return;
				}
			}
			else if (command.equals(ServerCommands.STOP.toString()))
			{
				if (this.manager != null && !this.manager.stop)
				{
					Log.info("Stopping server....");
					this.manager.stop();
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
			// Fake client handling
		}
	}
}
