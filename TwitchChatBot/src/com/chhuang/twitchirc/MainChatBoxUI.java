package com.chhuang.twitchirc;
import java.awt.BorderLayout;
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
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.BadLocationException;

import com.chhuang.accounts.*;
import com.chhuang.channels.ChannelManager;
import com.chhuang.display.ChatDisplay;
import com.chhuang.util.AppUtilities;

@SuppressWarnings("serial")
public class MainChatBoxUI extends JFrame implements ActionListener{

	public static final String DEFAULT_TITLE = "TWITCH CHAT";
	
	private Client client;
	
	private AccountManager accountManager;
	private ChannelManager channelManager;
	private MessageBoxUI msbUI;
	private ChatDisplay chatDisplay;
	
	private JPanel panelTextBox;
	private JPanel panelMainPane;
	private JTextField txtInput;
	
	private JButton btnEnter;
	private JScrollPane scrollPaneDISPLAY;
	
	private JMenuBar menuBar;
	private JMenu jmMenu;
	private JMenu jmTools;
	private JMenu jmCrappyBot;
	private JMenuItem miLogin;
	private JMenuItem miDisconnect;
	private JMenuItem miChangeChannel;
	private JMenuItem miVocabulary;
	private JMenuItem miAccounts;
	private JMenuItem miChannels;
	private JMenuItem miIncoming;
	private JMenuItem miMemory;
	
	public MainChatBoxUI(){
		setTitle(DEFAULT_TITLE);
		setSize(660,555);
		setLayout(new BorderLayout());
		addWindowListener(new WindowListener());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		accountManager = new AccountManager();
		channelManager = new ChannelManager();
		msbUI = new MessageBoxUI();
		msbUI.setLocationRelativeTo(this);
		
		menuBar = new JMenuBar();
		jmMenu = new JMenu("Menu");
		jmMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent arg0) {
				checkConnected();
			}
			@Override
			public void menuDeselected(MenuEvent arg0) {}
			@Override
			public void menuCanceled(MenuEvent arg0) {}
		});
		
		jmCrappyBot = new JMenu("CrappyBot");
		jmCrappyBot.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent arg0) {
				if(client != null && client.isBotMode()){
					miVocabulary.setEnabled(true);
					checkConnected();
				}else{
					miVocabulary.setEnabled(false);
				}
			}
			@Override
			public void menuDeselected(MenuEvent arg0) {}
			@Override
			public void menuCanceled(MenuEvent arg0) {}
		});
		
		jmTools = new JMenu("Tools");
		
		miLogin = new JMenuItem("Login");
		miLogin.addActionListener(this);
		
		miDisconnect = new JMenuItem("Disconnect");
		miDisconnect.addActionListener(this);
		miDisconnect.setEnabled(false);
		
		miChangeChannel = new JMenuItem("Change channel");
		miChangeChannel.addActionListener(this);
		miChangeChannel.setEnabled(false);
		
		miAccounts = new JMenuItem("Accounts");
		miAccounts.addActionListener(this);
		
		miVocabulary = new JMenuItem("Show Vocab");
		miVocabulary.setActionCommand("vocab");
		miVocabulary.addActionListener(this);
		
		miChannels = new JMenuItem("Channels");
		miChannels.addActionListener(this);
		
		miIncoming = new JMenuItem("Server Messages");
		miIncoming.addActionListener(this);
		
		miMemory = new JMenuItem("memory");
		miMemory.addActionListener(this);
		
		jmMenu.add(miLogin);
		jmMenu.add(miChangeChannel);
		jmMenu.add(miDisconnect);
		jmMenu.add(miChannels);
		jmMenu.add(miAccounts);
		
		jmTools.add(miIncoming);
		jmTools.add(miMemory);
		
		jmCrappyBot.add(miVocabulary);
		
		menuBar.add(jmMenu);
		menuBar.add(jmTools);
		menuBar.add(jmCrappyBot);
		
		/*
		 * -----------MAIN PANEL--------------
		 */
		chatDisplay = new ChatDisplay();
		
		scrollPaneDISPLAY = new JScrollPane(chatDisplay.getDisplayPane());
		scrollPaneDISPLAY.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneDISPLAY.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);;

		/*--TextField0--*/
		txtInput = new JTextField();
		txtInput.setActionCommand("send");
		txtInput.addActionListener(this);
		txtInput.setEnabled(false);
		/*--------------*/
		
		/*--Button--*/
		btnEnter = new JButton("Enter");
		btnEnter.setActionCommand("send");
		btnEnter.addActionListener(this);
		/*----------*/
		
		panelTextBox = new JPanel(new BorderLayout(4,2));
		panelTextBox.add(txtInput, BorderLayout.CENTER);
		panelTextBox.add(btnEnter, BorderLayout.EAST);
		
		panelMainPane = new JPanel(new BorderLayout());
		panelMainPane.add(scrollPaneDISPLAY, BorderLayout.CENTER);
		panelMainPane.add(panelTextBox,BorderLayout.SOUTH);
		
		/*
		 * -----------------------------------
		 */
		
		setJMenuBar(menuBar);
		getContentPane().add(panelMainPane, BorderLayout.CENTER);

		setVisible(true);
	}

	public void login() {
		Login login = new Login(this, accountManager.getAccounts(), channelManager.getChannels());
		if(login.isValid()){			
			try{
			String nick = login.getNick();
			String pass = login.getPass();
			String channel = login.getChannel();
			
			setTitle("Connecting...");
			client = new Client(nick, pass, channel, chatDisplay, msbUI.getDisplay());
			client.connectToServer(Client.DEFAULT_SERVER, Client.DEFAULT_PORT);
			client.connectToChannel();			
			
			txtInput.setEnabled(true);
			setTitle(DEFAULT_TITLE + " " + client.getChannel());
			} catch(Exception e){
				e.printStackTrace();
				chatDisplay.reset();
				chatDisplay.output("NOT ABLE TO CONNECT TO " + Client.DEFAULT_SERVER + "/" + Client.DEFAULT_PORT);
				login = null;
				return;
			}
		}
		login = null;
	}
	
	public void checkConnected(){
		if(client != null && client.isConnected()){
			miLogin.setEnabled(false);
			//TODO miChangeChannel.setEnabled(true);
			miDisconnect.setEnabled(true);
			txtInput.setEnabled(true);
		} else {
			miLogin.setEnabled(true);
			//TODO miChangeChannel.setEnabled(false);
			miDisconnect.setEnabled(false);
			txtInput.setEnabled(false);
			miVocabulary.setEnabled(false);
		}
	}
	
	private class WindowListener extends WindowAdapter{

		public void windowClosing(WindowEvent e){
			/* Terminates the program */
			try {
				if(client != null && client.isConnected()){
					setTitle("Disconnecting...");
					client.disconnectFromServer();
					client = null;
				}
			} catch (IOException | InterruptedException e1) {e1.printStackTrace(); System.exit(0);}
			
			dispose();
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if(actionCommand.equalsIgnoreCase("send")){
			String input = txtInput.getText();
			if(client != null){
				client.write("PRIVMSG " + client.getChannel() + " :" + input);
				//client.write(input);
			}
			txtInput.setText("");
		} 
		
		else if(actionCommand.equalsIgnoreCase("login")){
			try{
				login();
			} catch (Exception e1){
				e1.printStackTrace();
			}
		} 
		
		else if(actionCommand.equalsIgnoreCase("disconnect")){
			try {
				setTitle(getTitle() + " Disconnecting...");
				client.disconnectFromServer();
				setTitle(DEFAULT_TITLE);
				client = null;
				
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "ERROR: Client not found.");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} 
		
		else if(actionCommand.equalsIgnoreCase("accounts")){
			accountManager.showUI();
		} 
		
		else if(actionCommand.equalsIgnoreCase("vocab")){
			client.getBot().getVocab().showUI();
		} 
		
		else if(actionCommand.equalsIgnoreCase("channels")){
			channelManager.showUI();
		}
		
		else if(actionCommand.equalsIgnoreCase("change channel")){
			//TODO show option pane of channel list
		}
		else if(actionCommand.equalsIgnoreCase("Server Messages")){
			msbUI.setVisible(true);
			try {
				msbUI.getDisplay().showQueueMessages();
			} catch (BadLocationException | InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		else if(actionCommand.equalsIgnoreCase("memory")){
			AppUtilities.showMemory();
		}
	}	
}
