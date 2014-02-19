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
		jtpDisplay.setEditable(false);
		jtpDisplay.setBackground(Color.DARK_GRAY);
		
		Style defaultStyle = docDisplay.addStyle("default", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setFontSize(defaultStyle, 12);
		StyleConstants.setForeground(defaultStyle, Color.WHITE);
		StyleConstants.setFontFamily(defaultStyle, "Arial Unicode MS");
		
		DefaultCaret caret = (DefaultCaret)jtpDisplay.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	@Override
	public void outputMessages(String line) throws BadLocationException {
		if (jtpDisplay.getRootPane().getParent().isVisible()){
			docDisplay.insertString(docDisplay.getLength(), line, docDisplay.getStyle("default"));
		}
		else{
			messageQueue.addLast(line);
			if(messageQueue.size() > max_lines){
				messageQueue.removeFirst();
			}
		}
	}
	
	public void showQueueMessages() throws BadLocationException, InterruptedException{
		while(!messageQueue.isEmpty()){
			docDisplay.insertString(docDisplay.getLength(), messageQueue.removeFirst(), docDisplay.getStyle("default"));
		}
	}
}
