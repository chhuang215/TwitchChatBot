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
	protected JTextPane display;
	protected StyledDocument doc;
	protected SimpleDateFormat sdf;
	protected String now;
	
	public TextDisplay(){
		setDisplayPane(new JTextPane());
		sdf = new SimpleDateFormat("[HH:mm:ss] ");
		max_lines = DEFAULT_MAX_LINES;
		currentNumOfLines = 0;
	}
	
	
	public void maximumLineFormat() throws BadLocationException{
		if(currentNumOfLines > max_lines){
			doc.remove(0, display.getText().indexOf("\n"));
		}
	}
	
	public void reset(){
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		};
	}
	
	public void setDisplayPane(JTextPane jtp){
		display = jtp;
		doc = display.getStyledDocument();
	}
	
	public JTextPane getDisplayPane(){
		return display;
	}
	
	public void output(String line){
		
		try {
			if(!line.isEmpty()){
				maximumLineFormat();
				now = sdf.format(Calendar.getInstance().getTime());
				doc.insertString(doc.getLength(), now, doc.getStyle("default"));
				
				outputMessages(line);
				
				doc.insertString(doc.getLength(), "\n", doc.getStyle("default"));
				currentNumOfLines++;
			}
		
		} catch (BadLocationException e) {e.printStackTrace();}
	}	
	
	protected abstract void initializeDisplay();
	

	public abstract void outputMessages(String line) throws BadLocationException;
	
}
