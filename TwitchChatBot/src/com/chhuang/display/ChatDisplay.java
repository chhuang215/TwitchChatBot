package com.chhuang.display;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

/**
 * @author chhuang
 * The main display functionality
 */
public class ChatDisplay extends TextDisplay{
	
	private String myNick, currentConnectedChannel, lastChatMessage;
	
	public ChatDisplay(JTextPane displayObj) {
		super(displayObj);
		lastChatMessage = "";
	}
	
	/**
	 * Output the line onto the display
	 * @param line
	 */
	public void output(String line){
		try{
			if(!isEmptyString(line)){
				lastChatMessage = "";
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
		String beforeMessage = " PRIVMSG " + currentConnectedChannel + " :";
		lastChatMessage = msg.substring(msg.lastIndexOf(beforeMessage) + beforeMessage.length());
		
		outputPrivmsg(nick, lastChatMessage);
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

	/**
	 * 
	 * @param nick
	 */
	public void setUserNick(String nick){
		myNick = nick;
	}
	
	/**
	 * 
	 * @param channel
	 */
	public void setCurrentChannel(String channel){
		currentConnectedChannel = channel;
	}
	
	/**
	 * 
	 * @return String lastChatMessage
	 */
	public String getLastChatMessage(){
		return lastChatMessage;
	}
}
