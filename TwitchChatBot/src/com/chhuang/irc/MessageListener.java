package com.chhuang.irc;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;



import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.chhuang.display.ChatDisplay;
import com.chhuang.display.ServerMessageDisplay;

public class MessageListener {
	
	private ChatDisplay chatDisplay;
	private ServerMessageDisplay messageDisplay;
	private BufferedWriter writer;
	private LinkedList<String> messageQueue;
	private Thread outputThread;
	
	public MessageListener(){
		messageQueue = new LinkedList<String>();
		outputThread = new Thread(new OutputThread());
	}
	
	public MessageListener(ChatDisplay display){
		this();
		chatDisplay = display;
	}
	
	public MessageListener(ServerMessageDisplay display){
		this();
		messageDisplay = display;
	}
	
	public MessageListener(ChatDisplay cds, ServerMessageDisplay mds){
		this();
		chatDisplay = cds;
		messageDisplay = mds;
	}
	
	public synchronized void output(String msg){
		messageQueue.addLast(msg);
	
		if(!outputThread.isAlive()){
			outputThread.start();
		}
	}
	
	public void write(String msg) throws IOException{
		msg = msg.trim();
		if(!msg.isEmpty()){
			writer.write(msg + "\r\n");
			writer.flush();
			chatDisplay.output(msg);
		}	
	}
	
	public void setWriter(BufferedWriter bw){
		writer = bw;
	}	
	
	private class OutputThread implements Runnable{

		@Override
		public synchronized void run() {
			String msg;
			while(true){
				while(!messageQueue.isEmpty()){
					try{
						msg = messageQueue.removeFirst();
					
						if(msg.contains(" JOIN ") || msg.contains((" PART "))){
							messageDisplay.output(msg);
						}
						else if(msg.contains(":USERCOLOR")){
							String[] extract = msg.split(" ");
							chatDisplay.setNextNickColor(Color.decode(extract[5]));
						}
						else if(msg.contains(":SPECIALUSER")){
							
						}
						else if(msg.contains(":EMOTESET")){
							
						}
						else if (msg.contains(" 353 ")){
							
						}		
						else{
							chatDisplay.output(msg);
						}
					}catch(NoSuchElementException e){
						chatDisplay.output("NOSUCHELEMTNEXCEPTION HAHA\n");
						continue;
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
