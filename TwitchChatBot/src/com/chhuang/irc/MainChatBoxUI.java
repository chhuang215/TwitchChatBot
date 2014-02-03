package com.chhuang.irc;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.DefaultCaret;

import com.chhuang.accounts.*;
import com.chhuang.bot.*;

public class MainChatBoxUI extends JFrame implements ActionListener{

	public static final String TWITCH_SERVER = "irc.twitch.tv";
	
	public static final String DEFAULT_TITLE = "TWITCH CHAT";
	
	private Server server;

	private Bot bot;
	private Vocabulary vocab;
	
	private AccountManager accountManager;
	private AccountManagerUI accountUI;
	
	private JPanel pTextBox;
	private JTextField txtInput;
	private JTextArea display;
	private JButton btn;
	private JScrollPane scrollPane;
	
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem miLogin;
	private JMenuItem miDisconnect;
	private JMenuItem miVocabulary;
	private JMenuItem miAccounts;
	
	public MainChatBoxUI(){
		
		accountManager = new AccountManager();
		vocab = new Vocabulary();
		
		setTitle(DEFAULT_TITLE);
		setSize(660,555);
		setLayout(new BorderLayout());
		addWindowListener(new WindowListener());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		pTextBox = new JPanel(new BorderLayout(4,2));
		
		/*--TextArea : DISPLAY--*/
		display = new JTextArea();
		display.setEditable(false);
		display.setLineWrap(true);
		display.setFont(new Font("Arial Unicode MS", Font.PLAIN,13));
		display.setText("Welcome to TwitchChatBot!\n");

		DefaultCaret caret = (DefaultCaret)display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scrollPane = new JScrollPane(display);
		/*---------------------*/
		
		/*--TextField0--*/
		txtInput = new JTextField();
		txtInput.setActionCommand("send");
		txtInput.addActionListener(this);
		/*--------------*/
		
		
		btn = new JButton("Enter");
		btn.setActionCommand("send");
		btn.addActionListener(this);

		pTextBox.add(txtInput, BorderLayout.CENTER);
		pTextBox.add(btn, BorderLayout.EAST);
		
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menu.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent arg0) {
				if(server != null){
					checkConnected();
				}
			}
			
			@Override
			public void menuDeselected(MenuEvent arg0) {
				
			}
			
			@Override
			public void menuCanceled(MenuEvent arg0) {
				
			}
		});
		
		miLogin = new JMenuItem("Login");
		miLogin.addActionListener(this);
		
		miDisconnect = new JMenuItem("Disconnect");
		miDisconnect.addActionListener(this);
		miDisconnect.setEnabled(false);
		
		miAccounts = new JMenuItem("Accounts");
		miAccounts.addActionListener(this);
		
		miVocabulary = new JMenuItem("Vocab");
		miVocabulary.addActionListener(this);
		
		menu.add(miLogin);
		menu.add(miDisconnect);
		menu.add(miVocabulary);
		menu.add(miAccounts);
		menuBar.add(menu);
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(pTextBox,BorderLayout.SOUTH);
		getContentPane().add(menuBar, BorderLayout.NORTH);

		setVisible(true);
	}

	public void login() {	

		LoginGUI login = new LoginGUI(this, accountManager.getAccounts());
		if(login.valid){
			
			String nick = login.getNick();
			String pass = login.getPass();
			String channel = login.getChannel();
			login = null;
			
			//connect
			server = new Server(nick, pass, new DisplayService(display, nick, channel));
			server.connectToChannel(channel);
			if(nick.equalsIgnoreCase(Server.CRAPPY_BOT)){
				server.insertBot(new Bot(vocab));
			}
		}
		
		login = null;
	
	}
	
	public void checkConnected(){
		if(server.connected){
			miLogin.setEnabled(false);
			miDisconnect.setEnabled(true);
		} else {
			miLogin.setEnabled(true);
			miDisconnect.setEnabled(false);
		}
	}
	
	private class WindowListener extends WindowAdapter{

		public void windowClosing(WindowEvent e){
			/* Terminates the program */
			try {
			if(server != null)
				server.disconncetFromServer();
			} catch (IOException e1) {e1.printStackTrace();}
			
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}
	

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if(actionCommand.equalsIgnoreCase("send")){
			String input = txtInput.getText();
			server.write("PRIVMSG " + server.getChannel() + " :" +input);
			txtInput.setText("");
		} else if(actionCommand.equalsIgnoreCase("login")){
			try{
				login();
			} catch (Exception e1){
				e1.printStackTrace();
			}
		} else if(actionCommand.equalsIgnoreCase("disconnect")){
			try {
				server.disconncetFromServer();
				JOptionPane.showMessageDialog(getContentPane(), "*******Disconnect from " + server.getChannel() +"********\n");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if(actionCommand.equalsIgnoreCase("accounts")){
			if(accountUI == null){
				accountUI = new AccountManagerUI(accountManager);
			}
			accountUI.setVisible(true);
		} else if(actionCommand.equalsIgnoreCase("vocab")){
			vocab.show(true);
		}		
	}
	
	/*
	private class Incoming implements Runnable{

		public void run() {
			String line = null;
			try {				
				while(!Thread.currentThread().isInterrupted() && ((line = reader.readLine()) != null)){
					
					/*--AVOID DISCONNECTION--
					if (line.toLowerCase().startsWith("PING ")){
						writer.write("PONG " + line.substring(5)+"\r\n");
						writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
						writer.flush();
					}
					/*-----------------------
					
					if(!loggedIn){
						if (line.indexOf("001") >= 0){
							display.append("\nAuthenticate Success\n");
							setLogin(true);
						}
						else if(line.indexOf("Login unsuccessful") >= 0){
							return;
						}
					}
					
					if(line.contains("PRIVMSG") && !line.contains("jtv")){
						int index = line.indexOf(":") + 1;
						int endIndex = line.indexOf("!");
						String name = line.substring(index, endIndex);
						String msg = getPrvtMsg(line);
						display.append(name + ": " + msg +"\r\n");

						if(nick.equalsIgnoreCase(CRAPPY_BOT)){
							String response = bot.generateOutput(msg);
							sendMsg(response);
						}
						
					}else{
						display.append(line +"\n");
					}
				}
			} catch (IOException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				System.out.println("SOCKET DISCONNECT!");

			} 
			JOptionPane.showMessageDialog(getRootPane(), "*******Disconnect from " + channel +"********\n");
		}
	}
	*/
	
}
