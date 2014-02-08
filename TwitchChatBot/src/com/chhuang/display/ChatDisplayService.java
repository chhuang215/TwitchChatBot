package com.chhuang.display;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class ChatDisplayService extends DisplayService{
	
	private String myNick, channel, message;
	
	public ChatDisplayService(JTextPane displayObj, String nick, String channel) {
		super(displayObj);
		this.myNick = nick;
		this.channel = channel;
		message = "";
	}
	
	public void output(String line){
		try{
			if(!isEmptyString(line)){
				message = "";
				maximumLineFormat();
				if(line.startsWith(":") && line.contains("PRIVMSG ") && !line.contains("jtv")){
					outputPrivmsg(line);
					
				}else if(line.startsWith("PRIVMSG ")){
					outputPrivmsg(this.myNick + "(me)", line.substring(line.indexOf(":") + 1));
				}else{
					doc.insertString(doc.getLength(), line, doc.getStyle("default"));
				}
				
				doc.insertString(doc.getLength(), "\n", doc.getStyle("default"));
				currentNumOfLines++;
				
			}
		}catch(BadLocationException e){e.printStackTrace();}
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
