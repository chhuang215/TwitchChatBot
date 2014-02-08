package com.chhuang.irc;
import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.chhuang.accounts.*;
import com.chhuang.channels.ChannelManager;

@SuppressWarnings("serial")
public class MainChatBoxUI extends JFrame implements ActionListener{

	public static final String TWITCH_SERVER = "irc.twitch.tv";
	public static final String DEFAULT_TITLE = "TWITCH CHAT";
	
	private Client client;
	
	private AccountManager accountManager;
	private ChannelManager channelManager;
	private MessageBoxUI msbUI;
	
	private JPanel panelTextBox;
	private JPanel panelMainPane;
	private JTextField txtInput;
	private JTextPane jtpChatDisplay;
	
	private JButton btnEnter;
	private JScrollPane scrollPaneDISPLAY;
	
	private JMenuBar menuBar;
	private JMenu jmMenu;
	private JMenu jmCrappyBot;
	private JMenuItem miLogin;
	private JMenuItem miDisconnect;
	private JMenuItem miVocabulary;
	private JMenuItem miAccounts;
	private JMenuItem miChannels;
	private JMenuItem miIncoming;
	
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
				if(client != null && client.getVocab() != null){
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
		
		miLogin = new JMenuItem("Login");
		miLogin.addActionListener(this);
		
		miDisconnect = new JMenuItem("Disconnect");
		miDisconnect.addActionListener(this);
		miDisconnect.setEnabled(false);
		
		miAccounts = new JMenuItem("Accounts");
		miAccounts.addActionListener(this);
		
		miVocabulary = new JMenuItem("Vocab");
		miVocabulary.addActionListener(this);
		
		miChannels = new JMenuItem("Channels");
		miChannels.addActionListener(this);
		
		miIncoming = new JMenuItem("Server Messages");
		miIncoming.addActionListener(this);
		
		jmMenu.add(miLogin);
		jmMenu.add(miDisconnect);
		jmMenu.add(miChannels);
		jmMenu.add(miAccounts);
		jmMenu.add(miIncoming);
		
		jmCrappyBot.add(miVocabulary);

		menuBar.add(jmMenu);
		menuBar.add(jmCrappyBot);
		
		/*
		 * -----------MAIN PANEL--------------
		 */
		initializeChatDisplayTextPane();

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
		
		/*JButton btnMemoryTesting = new JButton("SHOW YOURSELF!");
		btnMemoryTesting.setActionCommand("memory");
		btnMemoryTesting.addActionListener(this);*/
		
		/*----------*/
		
		//lblMemory = new JLabel();
		//JPanel panelMemory = new JPanel(new GridLayout(3,1,2,2));
		
		//panelMemory.add(btnMemoryTesting);
		//panelMemory.add(lblMemory);
		
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

	private void initializeChatDisplayTextPane() {
		
		jtpChatDisplay = new JTextPane();
		jtpChatDisplay.setEditable(false);
		jtpChatDisplay.setBackground(Color.LIGHT_GRAY);
		
		StyledDocument doc = jtpChatDisplay.getStyledDocument();
		
		Style defaultStyle = doc.addStyle("default", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setFontSize(defaultStyle, 13);
		StyleConstants.setForeground(defaultStyle, Color.DARK_GRAY);
		StyleConstants.setFontFamily(defaultStyle, "Arial Unicode MS");

		Style styleNames = doc.addStyle("names", defaultStyle);
		StyleConstants.setForeground(styleNames, Color.BLUE);
		
		Style messages = doc.addStyle("messages", defaultStyle);
		StyleConstants.setForeground(messages, Color.BLACK);
		
		DefaultCaret caret = (DefaultCaret)jtpChatDisplay.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scrollPaneDISPLAY = new JScrollPane(jtpChatDisplay);
		scrollPaneDISPLAY.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneDISPLAY.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);;
	}

	public void login() {	

		LoginGUI login = new LoginGUI(this, accountManager.getAccounts(), channelManager.getChannels());
		if(login.isValid()){			
			
			String nick = login.getNick();
			String pass = login.getPass();
			String channel = login.getChannel();
			
			setTitle("Connecting...");
			client = new Client(nick, pass, channel, jtpChatDisplay, msbUI.getDisplayPane());
			client.connectToServer(Client.DEFAULT_SERVER, Client.DEFAULT_PORT);
			
			client.connectToChannel();			
			
			txtInput.setEnabled(true);
			setTitle(DEFAULT_TITLE + " " +client.getChannel());
		}
		login = null;
	}
	
	public void checkConnected(){
		if(client != null && client.isConnected()){
			miLogin.setEnabled(false);
			miDisconnect.setEnabled(true);
			txtInput.setEnabled(true);
		} else {
			miLogin.setEnabled(true);
			miDisconnect.setEnabled(false);
			txtInput.setEnabled(false);
			miVocabulary.setEnabled(false);
		}
	}
	
	private class WindowListener extends WindowAdapter{

		public void windowClosing(WindowEvent e){
			/* Terminates the program */
			try {
				if(client != null){
					setTitle("Disconnecting...");
					client.disconnectFromServer();
					client = null;
				}
			} catch (IOException | InterruptedException e1) {e1.printStackTrace();}
			
			dispose();
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		if(actionCommand.equalsIgnoreCase("send")){
			String input = txtInput.getText();
			if(client != null){
				client.write("PRIVMSG " + client.getChannel() + " :" +input);
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
				client.disconnectFromServer();
			
				JOptionPane.showMessageDialog(getContentPane(), "*******Disconnect from " + client.getChannel() +"********\n");
				client = null;
				setTitle(DEFAULT_TITLE);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "ERROR: Client not found.");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
		} 
		
		else if(actionCommand.equalsIgnoreCase("accounts")){
			accountManager.showUI();;
		} 
		
		else if(actionCommand.equalsIgnoreCase("vocab")){
			client.getVocab().show(true);
		} 
		
		else if(actionCommand.equalsIgnoreCase("channels")){
			channelManager.showUI();
		}
		
		else if(actionCommand.equalsIgnoreCase("Server Messages")){
			msbUI.setVisible(true);
		}
		
		/*else if(actionCommand.equalsIgnoreCase("memory")){
			Runtime r = Runtime.getRuntime();
			long total = r.totalMemory()/1024 ;
			long max = r.maxMemory()/1024;
			long free = r.freeMemory()/1024;
			long used = (r.totalMemory() - r.freeMemory())/1024;		
			
			lblMemory.setText("<html>Max: " + max + " KB<br>" +
						"Total: " + total + " KB<br>" +
						"Free: " + free + " KB<br>" + 
						"Used: " + used + " KB</html>");
		
		}*/
	}	
}
