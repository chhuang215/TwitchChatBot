package com.chhuang.irc;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.chhuang.display.ServerMessageDisplay;

@SuppressWarnings("serial")
public class MessageBoxUI extends JFrame {

	private ServerMessageDisplay serverMessageDisplay;
	private JScrollPane scrollPane;
	
	public MessageBoxUI(){
		setTitle("Incoming");
		setSize(550,600);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		serverMessageDisplay = new ServerMessageDisplay();
		
		scrollPane = new JScrollPane(serverMessageDisplay.getDisplayPane());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	public ServerMessageDisplay getDisplay(){
		return serverMessageDisplay;
	}
	
}
