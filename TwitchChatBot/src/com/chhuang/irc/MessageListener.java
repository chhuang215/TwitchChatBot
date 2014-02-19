package com.chhuang.irc;

import java.io.BufferedWriter;
import java.io.IOException;


import com.chhuang.display.ChatDisplay;
import com.chhuang.display.ServerMessageDisplay;

public class MessageListener {
	
	private ChatDisplay chatDisplay;
	private ServerMessageDisplay messageDisplay;
	private BufferedWriter writer;
	
	public MessageListener(){
		
	}
	
	public MessageListener(ChatDisplay display){
		chatDisplay = display;
	}
	
	public MessageListener(ServerMessageDisplay display){
		messageDisplay = display;
	}
	
	public MessageListener(ChatDisplay cds, ServerMessageDisplay mds){
		chatDisplay = cds;
		messageDisplay = mds;
	}
	
	public void output(String msg){
		if(msg.contains(" JOIN ") || msg.contains((" PART "))){
			messageDisplay.output(msg);
		}
		else if (msg.contains(" 353 ")){
			
		}		
		else{
			chatDisplay.output(msg);
		}
	}
	
	public void write(String msg) throws IOException{
		if(!isEmpty(msg)){
			writer.write(msg + "\r\n");
			writer.flush();
			chatDisplay.output(msg);
		}	
	}
	
	public void setWriter(BufferedWriter bw){
		writer = bw;
	}	
	
	private boolean isEmpty(String str){
		return str.trim().equals("");
	}
}
