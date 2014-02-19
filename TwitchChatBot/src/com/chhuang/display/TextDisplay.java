package com.chhuang.display;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public abstract class TextDisplay {
	
	public static final int DEFAULT_MAX_LINES = 300;
	protected LinkedList<String> messageQueue;
	protected int max_lines;
	protected int currentNumOfLines;
	protected JTextPane jtpDisplay;
	protected StyledDocument docDisplay;
	protected SimpleDateFormat sdf;
	protected String now;
	
	public TextDisplay(){
		setDisplayPane(new JTextPane());
		sdf = new SimpleDateFormat("[HH:mm:ss] ");
		messageQueue = new LinkedList<String>();
		max_lines = DEFAULT_MAX_LINES;
		currentNumOfLines = 0;
	}
	
	public void maximumLineFormat() throws BadLocationException{
		if(currentNumOfLines > max_lines){
			docDisplay.remove(0, jtpDisplay.getText().indexOf("\n"));
		}
	}
	
	public void reset(){
		try {
			docDisplay.remove(0, docDisplay.getLength());
			currentNumOfLines = 0;
			messageQueue.clear();
		} catch (BadLocationException e) {
			e.printStackTrace();
		};
	}
	
	public synchronized void output(String line){
		try {
			line = line.trim();
			if(!line.isEmpty()){
				maximumLineFormat();
				
				now = sdf.format(Calendar.getInstance().getTime());
				
				outputMessages(now);
				outputMessages(line);
				outputMessages("\n");
				
				currentNumOfLines++;
			}
		} catch (BadLocationException e) {e.printStackTrace();}
	}	
	
	public void setDisplayPane(JTextPane jtp){
		jtpDisplay = jtp;
		docDisplay = jtpDisplay.getStyledDocument();
	}
	
	public JTextPane getDisplayPane(){
		return jtpDisplay;
	}
	
	protected abstract void initializeDisplay();
	
	public abstract void outputMessages(String line) throws BadLocationException;
}
