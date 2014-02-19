package com.chhuang.display;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * @author chhuang
 * The main display functionality
 */
public class ChatDisplay extends TextDisplay{
	
	private String myNick, currentConnectedChannel, lastChatMessage;
	
	public ChatDisplay(){
		super();
		initializeDisplay();
		lastChatMessage = "";
	}
	
	protected void initializeDisplay(){
		display.setEditable(false);
		display.setBackground(Color.LIGHT_GRAY);
		
		Style defaultStyle = doc.addStyle("default", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setFontSize(defaultStyle, 13);
		StyleConstants.setForeground(defaultStyle, Color.DARK_GRAY);
		StyleConstants.setFontFamily(defaultStyle, "Arial Unicode MS");

		Style styleNames = doc.addStyle("names", defaultStyle);
		StyleConstants.setForeground(styleNames, Color.BLUE);
		
		Style messages = doc.addStyle("messages", defaultStyle);
		StyleConstants.setForeground(messages, Color.BLACK);
		
		DefaultCaret caret = (DefaultCaret)display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	/**
	 * Output the line onto the display
	 * @param line
	 */
	public void outputMessages(String line){
		try{
			lastChatMessage = "";
			if(line.startsWith(":") && line.contains("PRIVMSG ") && !line.contains("jtv")){
				outputPrivmsg(line);
			} else if(line.startsWith("PRIVMSG ")){
				outputPrivmsg(this.myNick + "(me)", line.substring(line.indexOf(":") + 1));
			} else{
				doc.insertString(doc.getLength(), line, doc.getStyle("default"));
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
	 * @param nick
	 */
	public void setUserNick(String nick){
		myNick = nick;
	}
	
	/**
	 * @param channel
	 */
	public void setCurrentChannel(String channel){
		currentConnectedChannel = channel;
	}
	
	/** 
	 * @return String lastChatMessage
	 */
	public String getLastChatMessage(){
		return lastChatMessage;
	}
}
