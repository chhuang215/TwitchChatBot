package com.chhuang.util;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class MemoryUI extends JFrame{

	private JLabel lblMemory;
	
	public MemoryUI() {
		super("Memory");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
 		setSize(200, 200);
 		setResizable(false);
 		lblMemory = new JLabel();
 		lblMemory.setHorizontalAlignment(JLabel.CENTER);
 		lblMemory.setFont(new Font("Arial", Font.PLAIN, 20));
 		JButton btn = new JButton("Show");
 		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Runtime r = Runtime.getRuntime();
				long total = r.totalMemory()/1024 ;
				long max = r.maxMemory()/1024;
				long free = r.freeMemory()/1024;
				long used = (r.totalMemory() - r.freeMemory())/1024;		
				
				lblMemory.setText("<html>Max: " + max + " KB<br>" +
							"Total: " + total + " KB<br>" +
							"Free: " + free + " KB<br>" + 
							"Used: " + used + " KB</html>");
			}
		});
 		
 		getContentPane().add(lblMemory, BorderLayout.CENTER);
 		getContentPane().add(btn, BorderLayout.SOUTH);
	}

}
