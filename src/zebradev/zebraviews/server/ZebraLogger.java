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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

public class ZebraLogger extends Logger {
	
	public static StyledDocument DOC;
	public static BufferedWriter LOG;
	
	@Override
	public synchronized void log(int level, String category, String message, Throwable ex)
	{
		Style style = null;
		
		switch (level) {
		case Log.LEVEL_ERROR: style = ZebraLogger.DOC.getStyle("error"); break;
		case Log.LEVEL_WARN: style = ZebraLogger.DOC.getStyle("warning"); break;
		case Log.LEVEL_INFO: style = ZebraLogger.DOC.getStyle("info"); break;
		case Log.LEVEL_DEBUG: style = ZebraLogger.DOC.getStyle("debug"); break;
		case Log.LEVEL_TRACE: style = ZebraLogger.DOC.getStyle("trace"); break;
		}
		
		if (category != null)
		{
			if (category.equals("server"))
				style = ZebraLogger.DOC.getStyle("server");
			else if (category.equals("client"))
				style = ZebraLogger.DOC.getStyle("client");
			else if (category.equals("comment"))
				style = ZebraLogger.DOC.getStyle("comment");
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
			ZebraLogger.DOC.insertString(ZebraLogger.DOC.getLength(), text, style);
			ZebraLogger.LOG.write(text);
			ZebraLogger.LOG.flush();
		} catch (BadLocationException e) {
			System.out.println("\nCannot display output\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("\nIO error\n");
			e.printStackTrace();
		}
	}
}
