package com.chhuang.ircconnect;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
import javax.swing.text.DefaultCaret;

import com.chhuang.accounts.*;

public class TwitchChatBot{

	public static final String TWITCH_SERVER = "irc.twitch.tv";
	public static final String CRAPPY_BOT = "CrappyBot";
	
	private boolean loggedIn;
	
	private String nick;
	private String channel;
	private String pass;
	private Bot bot;
	private Vocabulary vocab;
	private AccountManager accountManager;
	
	private SendListener sendListener;
	private WindowListener windowListener;
		
	private JFrame frame;
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
	
	private BufferedWriter writer;
	private BufferedReader reader;
	
	private Thread incoming;
	private Socket socket;
	
	public TwitchChatBot(){
		nick = null;
		channel = null;
		loggedIn = false;
		
		vocab = new Vocabulary();
		accountManager = new AccountManager();
		bot = new Bot(vocab);
		windowListener = new WindowListener();
		sendListener = new SendListener();
		
		frame = new JFrame("Twitch Chat");
		frame.setSize(650,550);
		frame.setLayout(new BorderLayout());
		frame.addWindowListener(windowListener);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		pTextBox = new JPanel(new BorderLayout(4,2));
		
		display = new JTextArea();
		display.setEditable(false);
		display.setLineWrap(true);

		DefaultCaret caret = (DefaultCaret)display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scrollPane = new JScrollPane(display);
		
		txtInput = new JTextField();
		txtInput.addActionListener(sendListener);
		
		btn = new JButton("Enter");
		btn.addActionListener(sendListener);

		pTextBox.add(txtInput, BorderLayout.CENTER);
		pTextBox.add(btn, BorderLayout.EAST);
		
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		
		miLogin = new JMenuItem("Login");
		miLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					login();
				} catch (Exception e){
					e.printStackTrace();
				}
				
			}
		});
		
		miDisconnect = new JMenuItem("Disconnect");
		miDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					disconnect();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		miDisconnect.setEnabled(false);
		
		miAccounts = new JMenuItem("Accounts");
		miAccounts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				accountManager.setVisible(true);
			}
		});
		
		miVocabulary = new JMenuItem("Vocab");
		miVocabulary.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				vocab.show(true);
			}
		});
		
		menu.add(miLogin);
		menu.add(miDisconnect);
		menu.add(miVocabulary);
		menu.add(miAccounts);
		menuBar.add(menu);
		
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(pTextBox,BorderLayout.SOUTH);
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		
	}
	
	public String getPrvtMsg(String line){
		String beforeMsg = "PRIVMSG " + channel + " :";
		int index = line.lastIndexOf(beforeMsg);
		index = index + beforeMsg.length();
		return line.substring(index);
	}
	
	public void sendMsg(String msg){
		if(msg != null && !msg.trim().equals("")){
			if(msg.startsWith("!")){
				String command = (msg.substring(1)).trim();
			}else{
				sendPrvtMsg(msg);
			}
		}
		txtInput.setText(null);
	}
	
	public void sendPrvtMsg(String msg){
		String onto = "PRIVMSG " + channel + " :" + msg +"\r\n";
		try {
			writer.write(onto);
			writer.flush();
			display.append(nick +"(me): " + msg +"\n");
		} catch (IOException i) {i.printStackTrace();}
	}
	
	public void connect() throws Exception{
		
		connectToTwitch();
		
		String line = null;
		writer.write("PASS " + pass + "\r\n");
		writer.write("NICK " + nick + "\r\n");
		writer.flush();
		display.append("\nAuthenticating " + nick +" ...\n\n");
		while((line = reader.readLine()) != null){
			display.append(line+"\r\n");
			if (line.indexOf("376") >= 0){
				break;
			}
			else if(line.indexOf("Login unsuccessful") >= 0){
				return;
			}
		}
		
		display.append("\nAuthenticate Success\n");
		
		loggedIn = true;
		incoming = new Thread(new Incoming());
		incoming.start();
		
		connectToChannel();
	}
	
	public void connectToTwitch() throws Exception{
		if(!(socket instanceof Socket) || !socket.isConnected()){
			socket = new Socket(TWITCH_SERVER, 6667);
			display.append("Successfully connected to " + socket.getInetAddress().getHostName() + "-" +socket.getInetAddress().getHostAddress() + "\n");
			
			
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}else{
			display.append("Currently connected to: " + socket.getInetAddress().getHostAddress());
		}
		
	
	}
	
	public void connectToChannel() throws Exception{
		
		writer.write("JOIN " + channel +"\r\n");
		writer.flush();	

		display.append("\nCONNECTED to "+ channel +" as " + nick + "!\n\n");
		frame.setTitle(frame.getTitle() + " - " + channel);
	}
	
	public void disconnect() throws Exception{
		if(loggedIn){
			String command = "PART " + channel + "\r\n";
			writer.write(command);
			writer.flush();
			display.append(command);
					
			loggedIn = false;
		}
	}
	
	public void login() throws Exception{	
		if(!loggedIn){
			
			LoginGUI login = new LoginGUI(frame, accountManager.getAccounts());
			if(login.valid){
				nick = login.getNick();
				pass = login.getPass();
				channel = login.getChannel();
				login = null;
				connect();
			}
			
			login = null;
			
		}
	}
	
	public void start() throws Exception{
		frame.setVisible(true);
	}
	
	
	private class Incoming implements Runnable{

		public void run() {
			String line = null;
			try {
				System.out.println("BEFORE WHILE!");
				line = reader.readLine();
				while(!Thread.currentThread().isInterrupted() && line != null){
					
					System.out.println(line);
					/*--AVOID DISCONNECTION--*/
					if (line.toLowerCase().startsWith("PING ")){
						writer.write("PONG " + line.substring(5)+"\r\n");
						writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
						writer.flush();
					}
					/*-----------------------*/
					
					if(line.contains("PRIVMSG") && !line.contains("jtv")){
						int index = line.indexOf(":") + 1;
						int endIndex = line.indexOf("!");
						String name = line.substring(index, endIndex);
						String msg = getPrvtMsg(line);
						display.append(name + ": " + msg +"\r\n");
						String response = bot.generateOutput(msg);
						sendMsg(response);
					}else{
						display.append(line +"\n");
					}
					System.out.println("where does it stop?");
					line = reader.readLine();
					System.out.println("where does it stop tho?");
				}
			} catch (IOException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				System.out.println("SOCKET DISCONNECT!");

			} 
			JOptionPane.showMessageDialog(frame, "*******Disconnect from " + channel +"********\n");
		}
	}
	
	private class WindowListener extends WindowAdapter{

		public void windowClosing(WindowEvent e){

			/* Terminates the program */
			frame.setVisible(false);
			frame.dispose();
			System.exit(0);
		
		}
	}
	
	private class SendListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String input = txtInput.getText();
			sendMsg(input);
		}
	}
}
