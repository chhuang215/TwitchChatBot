package com.chhuang.irc;

import javax.swing.JTextArea;

public class DisplayService {

	private String nick, channel;
	private String message;
	private JTextArea display;

	
	public DisplayService(Object displayObj, String nick, String channel) {
		this.nick = nick;
		this.channel = channel;
		message = "";
		if(displayObj instanceof JTextArea){
			display = (JTextArea)displayObj;
		}
	}
	
	public void output(String msg){
		
		if(msg.startsWith(":") && msg.contains("PRIVMSG") && !msg.contains("jtv")){
			msg = parseNormalPrivmsg(msg);
		}else{
			message = "";
		}
		
		display.append(msg + "\n");
		display.setCaretPosition(display.getDocument().getLength());
	}
	
	private String parseNormalPrivmsg(String msg){
		String nick = msg.substring(msg.indexOf(":") + 1, msg.indexOf("!"));
		String beforeMessage = " PRIVMSG " + channel + " :";
		message = msg.substring(msg.lastIndexOf(beforeMessage) + beforeMessage.length());
		
		return nick + ": " + message;
	}
	
	public String getMessage(){
		return message;
	}
}
