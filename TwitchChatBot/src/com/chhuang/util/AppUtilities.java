package com.chhuang.util;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AppUtilities {
	
	private static JFrame jfrmMemory;
	private static JLabel lblMemory;
	
	public static void showMemory(){
		
		if(jfrmMemory == null){
			
	 		jfrmMemory = new JFrame("Memory");
	 		jfrmMemory.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	 		jfrmMemory.setLayout(new BorderLayout());
	 		jfrmMemory.setSize(200, 200);
	 		jfrmMemory.setResizable(false);
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
	 		jfrmMemory.getContentPane().add(lblMemory, BorderLayout.CENTER);
	 		jfrmMemory.getContentPane().add(btn, BorderLayout.SOUTH);
	 		
	 	}
	 	jfrmMemory.setVisible(true);
	}
}
