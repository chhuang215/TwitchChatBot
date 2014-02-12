package com.chhuang.display;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class ServerMessageDisplay extends TextDisplay {

	public ServerMessageDisplay(){
		super();
		initializeDisplay();
		max_lines = 133;
	}
	
	@Override
	protected void initializeDisplay() {
		display.setEditable(false);
		display.setBackground(Color.DARK_GRAY);
		
		Style defaultStyle = doc.addStyle("default", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setFontSize(defaultStyle, 12);
		StyleConstants.setForeground(defaultStyle, Color.WHITE);
		StyleConstants.setFontFamily(defaultStyle, "Arial Unicode MS");
		
		DefaultCaret caret = (DefaultCaret)display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	@Override
	public void outputMessages(String line) throws BadLocationException {

		if (display.getRootPane().getParent().isVisible()){
			doc.insertString(doc.getLength(), line, doc.getStyle("default"));

		}
		
		else{
			messageQueue.addLast(now + line);
			if(messageQueue.size() > max_lines){
				messageQueue.removeFirst();
			}
		}
	}
	
	public void showQueueMessages() throws BadLocationException, InterruptedException{
		while(!messageQueue.isEmpty()){
			doc.insertString(doc.getLength(), messageQueue.removeFirst() + "\n", doc.getStyle("default"));
		}	
	}
}
