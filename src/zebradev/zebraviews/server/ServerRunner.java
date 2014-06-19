package zebradev.zebraviews.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/*
 * Runner class; handles the server's GUI.
 * 
 * Remember to prefix commands run at a client level with "/client "
 * (i.e "/client LOGIN username password"). Server-level commands are prefixed with "/server".
 * If you just want to leave a note, do not prefix your message.
 * 
*/

public class ServerRunner extends JPanel implements ActionListener {

	private static final long serialVersionUID = 5287650080137085404L;
	
	private JTextField serverText;
    private JTextPane textPane;
    private StyledDocument doc;
    private BufferedWriter logWriter;
    
    public final static Dimension WINDOW_DIMENSIONS = new Dimension(800, 500);
    public final static String greeting = "Running server GUI. Type \"/server start\" to begin.";

	public ServerRunner() {
		super(new BorderLayout());
		this.setPreferredSize(ServerRunner.WINDOW_DIMENSIONS);
		
		serverText = new JTextField(80);
        serverText.addActionListener(this);
        
        try {
			logWriter = new BufferedWriter(new FileWriter("ZebraViews-Server_" + 
			new SimpleDateFormat("yyyyMMddhhmm").format(new Date()) + ".log"));
		} catch (IOException e) {
			System.out.println("\nUnable to make log file\n");
			e.printStackTrace();
		}
        
        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(this.getPreferredSize().width, this.getPreferredSize().height - 30));
		doc = textPane.getStyledDocument();
		addStylesToDocument(doc);
		textPane.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textPane);

        add(scrollPane, BorderLayout.NORTH);
        
        add(serverText, BorderLayout.LINE_START);
        
        try {
			doc.insertString(doc.getLength(), greeting, doc.getStyle("comment"));
		} catch (BadLocationException e) {
			System.out.println("\nCannot display input\n");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initGUI();
			}
		});
	}
	
	private static void initGUI() {
		JFrame frame = new JFrame("ZebraViews-Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.add(new ServerRunner());
        
        frame.pack();
        frame.setVisible(true);
	}
	
	protected void addStylesToDocument(StyledDocument doc) {
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);
 
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
 
        Style s = doc.addStyle("client", regular);
        StyleConstants.setItalic(s, true);
        StyleConstants.setForeground(s, Color.BLUE);
 
        s = doc.addStyle("server", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.MAGENTA);
        
        s = doc.addStyle("comment", regular);
        StyleConstants.setForeground(s, Color.GREEN);
        
        // Adding more later
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			String text = serverText.getText();
			Style style;
			if (text.length() >= 7 && text.substring(0, 7).equals("/server"))
				style = doc.getStyle("server");
			else if (text.length() >= 7 && text.substring(0, 7).equals("/client"))
				style = doc.getStyle("client");
			else
			{
				style = doc.getStyle("comment");
				text = "//" + text;
			}
			doc.insertString(doc.getLength(), text += '\n', style);
			logWriter.write(text);
			logWriter.flush();
		} catch (BadLocationException e) {
			System.out.println("\nCannot display input\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("\nIO error\n");
			e.printStackTrace();
		}
        serverText.setText("");
	}

}
