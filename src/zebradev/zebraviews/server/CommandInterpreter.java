package zebradev.zebraviews.server;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

public class CommandInterpreter {

	private StyledDocument doc;
	private BufferedWriter log;
	
	public static final String START_RESPONSE = "Starting server....";
	
	public CommandInterpreter(StyledDocument doc, BufferedWriter log) {
		this.doc = doc;
		this.log = log;
	}
	
	public void interpret(String input) throws BadLocationException, IOException {
		String styleName = "regular";
		if (input.charAt(1) == 's')
		{
			String command = input.substring(8);
			if (command.equals(ServerCommands.START.toString()))
			{
				ServerRunner.log(this.doc, this.log, START_RESPONSE, styleName);
			}
			// Handle other possible responses in if/else blocks
		}
		else
		{
			// Fake client handling
		}
	}
}
