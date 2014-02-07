package com.chhuang.irc;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class MessageBoxUI extends JFrame {
	
	private JTextPane jtpDisplay;
	private JScrollPane scrollPane;
	
	public MessageBoxUI(){
		setTitle("Incoming");
		setSize(450,600);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initializeDisplay();
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	public void initializeDisplay(){
		jtpDisplay = new JTextPane();
		jtpDisplay.setEditable(false);
		jtpDisplay.setBackground(Color.DARK_GRAY);
		
		StyledDocument doc = jtpDisplay.getStyledDocument();
		Style defaultStyle = doc.addStyle("default", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setFontSize(defaultStyle, 13);
		StyleConstants.setForeground(defaultStyle, Color.WHITE);
		StyleConstants.setFontFamily(defaultStyle, "Arial Unicode MS");
		scrollPane = new JScrollPane(jtpDisplay);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	public JTextPane getDisplayPane(){
		return jtpDisplay;
	}
	
}
