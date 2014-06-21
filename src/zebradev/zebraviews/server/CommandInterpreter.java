package zebradev.zebraviews.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class CommandInterpreter {

	private static StyledDocument doc;
	private static BufferedWriter log;
	
	public static final String START_RESPONSE = "Starting server....";
	
	public CommandInterpreter(StyledDocument doc, BufferedWriter log) {
		CommandInterpreter.doc = doc;
		CommandInterpreter.log = log;
	}
	
	public void interpret(String input) {
		if (input.charAt(1) == 's')
		{
			String command = input.substring(8);
			if (command.equals(ServerCommands.START.toString()))
			{
				CommandInterpreter.standardLog(START_RESPONSE);
				try {
					new Thread(new RequestManager()).start();
				} catch (Exception e) {
					CommandInterpreter.standardLog("Config files error!");
					CommandInterpreter.standardLog("Stopping server....");
					
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					CommandInterpreter.standardLog(errors.toString());
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
	
	public static synchronized void standardLog(String text) {
		try {
			ServerRunner.log(doc, log, text, "regular");
		} catch (BadLocationException e) {
			System.out.println("\nCannot display input\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("\nIO error\n");
			e.printStackTrace();
		}
	}
}
