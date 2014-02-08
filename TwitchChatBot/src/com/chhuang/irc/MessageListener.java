package com.chhuang.irc;

import com.chhuang.display.ChatDisplayService;
import com.chhuang.display.MessageDisplayService;

public class MessageListener {
	
	private ChatDisplayService chatDisplay;
	private MessageDisplayService messageDisplay;
		
	public MessageListener(){
		
	}
	
	public MessageListener(ChatDisplayService display){
		chatDisplay = display;
	}
	
	public MessageListener(MessageDisplayService display){
		messageDisplay = display;
	}
	
	public MessageListener(ChatDisplayService cds, MessageDisplayService mds){
		chatDisplay = cds;
		messageDisplay = mds;
	}
	
	public void output(String msg){
		if(msg.contains(" JOIN ") || msg.contains((" PART "))){
			messageDisplay.output(msg);
		}else if (msg.contains(" 353 ")){
			
		}		
		else{
			chatDisplay.output(msg);
		}
	}
	
	public void write(){
		
	}
}
