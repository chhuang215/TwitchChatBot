package com.chhuang.display;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public abstract class DisplayService {
	public static final int DEFAULT_MAX_LINES = 450;
	protected int currentNumOfLines = 0;
	protected JTextPane display;
	protected StyledDocument doc;
	protected int max_lines;
	
	public DisplayService(JTextPane jtpDisplay){
		display = jtpDisplay;
		doc = display.getStyledDocument();
		max_lines = DEFAULT_MAX_LINES;
	}
	
	public void maximumLineFormat() throws BadLocationException{
		if(currentNumOfLines > max_lines){
			doc.remove(0, display.getText().indexOf("\n"));
		}
	}
	
	public boolean isEmptyString(String str){
		return (str == null || str.equals(""));
	}
	public abstract void output(String line);
	
}
