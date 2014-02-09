package com.chhuang.display;

import java.util.LinkedList;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class ServerMessageDisplay extends TextDisplay {

	
	public ServerMessageDisplay(JTextPane jtpDisplay) {
		super(jtpDisplay);
		max_lines = 133;
		messageQueue = new LinkedList<String>();
		
	}

	@Override
	public void output(String line) {
		if(!isEmptyString(line)){
			if (display.getRootPane().getParent().isVisible()){
				try {
					
					maximumLineFormat();
					doc.insertString(doc.getLength(), line + "\n", doc.getStyle("default"));
		
					currentNumOfLines++;	
					
					
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			
			else{
				messageQueue.add(line);
				if(messageQueue.size() > max_lines){
					messageQueue.removeLast();
				}
			}
		}
	}
	
	public void showQueueMessages() throws BadLocationException{
		while(!messageQueue.isEmpty()){
			doc.insertString(doc.getLength(), messageQueue.removeLast() + "\n", doc.getStyle("default"));
		}
		
	}
}
