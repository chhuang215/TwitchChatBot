package com.chhuang.irc;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class DisplayService {

	public static final int MAX_LINES = 450;
	
	private int lines = 0;
	private String myNick, channel;
	private String message;

	private JTextPane display;
	private StyledDocument doc;
	
	public DisplayService(Object displayObj, String nick, String channel) {
		this.myNick = nick;
		this.channel = channel;
		message = "";
		if(displayObj instanceof JTextPane){
			display = (JTextPane)displayObj;
			doc = display.getStyledDocument();
		}
	}
	
	public void output(String msg){
		try{
			if(msg != null && !msg.equals("")){
				message = "";
				
				if(msg.startsWith(":") && msg.contains("PRIVMSG") && !msg.contains("jtv")){
					outputPrivmsg(msg);
					
				}else if(msg.startsWith("PRIVMSG ")){
					outputPrivmsg(this.myNick + "(me)", msg.substring(msg.indexOf(":") + 1));
				}else{
					doc.insertString(doc.getLength(), msg, doc.getStyle("default"));
				}
				
				doc.insertString(doc.getLength(), "\n", doc.getStyle("default"));
				lines++;
				if(lines > MAX_LINES){
					doc.remove(0, display.getText().indexOf("\n"));
				}
			}
		}catch(BadLocationException e){e.printStackTrace();};
	}
	
	/**
	 * Overloading outputPrivmsg
	 * @param msg
	 */
	private void outputPrivmsg(String msg){
		String nick = msg.substring(msg.indexOf(":") + 1, msg.indexOf("!"));
		String beforeMessage = " PRIVMSG " + channel + " :";
		message = msg.substring(msg.lastIndexOf(beforeMessage) + beforeMessage.length());
		
		outputPrivmsg(nick, message);
	}
	
	/**
	 * Overloading outputPrivmsg
	 * @param nick
	 * @param msg
	 */
	private void outputPrivmsg(String nick, String msg){
		try {
			doc.insertString(doc.getLength(), nick + ": ", doc.getStyle("names"));
			doc.insertString(doc.getLength(), msg, doc.getStyle("messages"));
		} catch (BadLocationException e) {
			System.out.println("UNABLE TO INSERT STRING!");
			e.printStackTrace();
		}
	}
	
	public String getMessage(){
		return message;
	}
}
