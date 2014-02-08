package com.chhuang.display;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class MessageDisplayService extends DisplayService {

	public MessageDisplayService(JTextPane jtpDisplay) {
		super(jtpDisplay);
		max_lines = 133;
		
	}

	@Override
	public void output(String line) {
		if (display.getRootPane().getParent().isVisible()){
			try {
				if(!isEmptyString(line)){
					maximumLineFormat();
					doc.insertString(doc.getLength(), line + "\n", doc.getStyle("default"));
		
					currentNumOfLines++;	
				}
				
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}
