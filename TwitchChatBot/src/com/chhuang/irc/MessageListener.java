package com.chhuang.irc;

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
		outputThread.start();
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
	
	public void kill(){
		outputThread.interrupt();
	}
	
	private class OutputThread implements Runnable{

		@Override
		public synchronized void run() {
			String msg;
			try {
				while(!Thread.currentThread().isInterrupted()){
					while(!messageQueue.isEmpty()){
						try{
							msg = messageQueue.removeFirst();
							if(msg.contains(" JOIN ") || msg.contains((" PART "))){
								messageDisplay.output(msg);
							}
							else if(msg.contains(":USERCOLOR")){
								chatDisplay.setNextNickColor((msg.split(" "))[5]);
							}
							else if(msg.contains(":SPECIALUSER")){
								
							}
							else if(msg.contains(":EMOTESET")){
								
							}
							else{
								chatDisplay.output(msg);
							}
						}catch(NoSuchElementException e){
							chatDisplay.output("NOSUCHELEMTNEXCEPTION HAHA\n");
							continue;
						}
					}
					
					Thread.sleep(11);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}
}
