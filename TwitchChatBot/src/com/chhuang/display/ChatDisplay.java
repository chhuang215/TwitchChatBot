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
	
	private String myNick,currentConnectedChannel, lastChatMessage;
	
	public ChatDisplay(){
		super();
		initializeDisplay();
		lastChatMessage = "";
	}
	
	protected void initializeDisplay(){
		jtpDisplay.setEditable(false);
		jtpDisplay.setBackground(Color.LIGHT_GRAY);
		
		Style defaultStyle = docDisplay.addStyle("default", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setFontSize(defaultStyle, 13);
		StyleConstants.setForeground(defaultStyle, Color.DARK_GRAY);
		StyleConstants.setFontFamily(defaultStyle, "Arial Unicode MS");

		docDisplay.addStyle("names", defaultStyle);
		
		Style messages = docDisplay.addStyle("messages", defaultStyle);
		StyleConstants.setForeground(messages, Color.BLACK);
		
		DefaultCaret caret = (DefaultCaret)jtpDisplay.getCaret();
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
				docDisplay.insertString(docDisplay.getLength(), line, docDisplay.getStyle("default"));
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
			docDisplay.insertString(docDisplay.getLength(), nick + ": ", docDisplay.getStyle("names"));
			docDisplay.insertString(docDisplay.getLength(), msg, docDisplay.getStyle("messages"));
		} catch (BadLocationException e) {
			System.out.println("UNABLE TO INSERT STRING!");
			e.printStackTrace();
		}
	}

	public void setNextNickColor(Color color){
		Style user = docDisplay.getStyle("names");
		StyleConstants.setForeground(user, color);
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
