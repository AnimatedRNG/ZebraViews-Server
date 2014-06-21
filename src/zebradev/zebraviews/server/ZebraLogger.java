package zebradev.zebraviews.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

public class ZebraLogger extends Logger {

	@Override
	public synchronized void log(int level, String category, String message, Throwable ex)
	{
		Style style = null;
		
		switch (level) {
		case Log.LEVEL_ERROR: style = ServerRunner.DOC.getStyle("error"); break;
		case Log.LEVEL_WARN: style = ServerRunner.DOC.getStyle("warning"); break;
		case Log.LEVEL_INFO: style = ServerRunner.DOC.getStyle("info"); break;
		case Log.LEVEL_DEBUG: style = ServerRunner.DOC.getStyle("debug"); break;
		case Log.LEVEL_TRACE: style = ServerRunner.DOC.getStyle("trace"); break;
		}
		
		if (category != null)
		{
			if (category.equals("server"))
				style = ServerRunner.DOC.getStyle("server");
			else if (category.equals("client"))
				style = ServerRunner.DOC.getStyle("client");
			else if (category.equals("comment"))
				style = ServerRunner.DOC.getStyle("comment");
		}
		
		String timeStamp = new SimpleDateFormat("HH:mm:ss-yyyy/MM/dd").format(Calendar.getInstance().getTime());
		StringBuilder builder = new StringBuilder();
		builder.append(timeStamp);
		builder.append(": ");
		builder.append("-- ");
		builder.append(style.getName().toUpperCase());
		builder.append(" -- [");
		if (category != null)
			builder.append(category);
		else
			builder.append("ZebraViews");
		builder.append("] ");
		builder.append(message);
		
		if (ex != null)
		{
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			builder.append("\n" + errors.toString());
		}
		
		builder.append("\n");
		String text = builder.toString();
		
		try {
			ServerRunner.DOC.insertString(ServerRunner.DOC.getLength(), text, style);
			ServerRunner.LOG.write(text);
			ServerRunner.LOG.flush();
		} catch (BadLocationException e) {
			System.out.println("\nCannot display output\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("\nIO error\n");
			e.printStackTrace();
		}
	}
}
