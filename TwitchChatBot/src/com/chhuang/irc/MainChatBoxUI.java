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
import com.chhuang.bot.*;

@SuppressWarnings("serial")
public class MainChatBoxUI extends JFrame implements ActionListener{

	public static final String TWITCH_SERVER = "irc.twitch.tv";
	
	public static final String DEFAULT_TITLE = "TWITCH CHAT";
	
	private Server server;

	private Vocabulary vocab;
	
	private AccountManager accountManager;
	private AccountManagerUI accountUI;
	
	private JPanel panelTextBox;
	private JPanel panelMainPane;
	private JTextField txtInput;
	//private JTextArea displayOLD;
	private JTextPane display;
	
	private JButton btn;
	private JScrollPane scrollPaneDISPLAY;
	
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
		
		panelTextBox = new JPanel(new BorderLayout(4,2));
		
		initializeDisplayPane();		
		
		/*--TextArea : DISPLAY--
		display = new JTextArea();
		display.setEditable(false);
		display.setLineWrap(true);
		display.setFont(new Font("Arial Unicode MS", Font.PLAIN,13));
		display.setBackground(Color.DARK_GRAY);
		display.setForeground(Color.CYAN);
		display.setText("Welcome to TwitchChatBot!\n");
		
		DefaultCaret caret = (DefaultCaret)display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scrollPane = new JScrollPane(display);
		---------------------*/
		
		/*--TextField0--*/
		txtInput = new JTextField();
		txtInput.setActionCommand("send");
		txtInput.addActionListener(this);
		txtInput.setEnabled(false);
		/*--------------*/
		
		/*--Button--*/
		btn = new JButton("Enter");
		btn.setActionCommand("send");
		btn.addActionListener(this);
		/*----------*/
		
		
		panelTextBox.add(txtInput, BorderLayout.CENTER);
		panelTextBox.add(btn, BorderLayout.EAST);
		
		panelMainPane = new JPanel(new BorderLayout());
		panelMainPane.add(scrollPaneDISPLAY, BorderLayout.CENTER);
		panelMainPane.add(panelTextBox,BorderLayout.SOUTH);
		
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
		
		setJMenuBar(menuBar);
		getContentPane().add(panelMainPane, BorderLayout.CENTER);
		
		setVisible(true);
	}

	private void initializeDisplayPane() {
		
		display = new JTextPane();
		display.setEditable(false);
		display.setBackground(Color.LIGHT_GRAY);
		
		StyledDocument doc = display.getStyledDocument();
		
		Style defaultStyle = doc.addStyle("default", StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setFontSize(defaultStyle, 13);
		StyleConstants.setForeground(defaultStyle, Color.DARK_GRAY);
		StyleConstants.setFontFamily(defaultStyle, "Arial Unicode MS");

		Style styleNames = doc.addStyle("names", defaultStyle);
		StyleConstants.setForeground(styleNames, Color.BLUE);
		
		Style messages = doc.addStyle("messages", defaultStyle);
		StyleConstants.setForeground(messages, Color.BLACK);
		
		DefaultCaret caret = (DefaultCaret)display.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scrollPaneDISPLAY = new JScrollPane(display);
		scrollPaneDISPLAY.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneDISPLAY.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);;
	}

	public void login() {	

		LoginGUI login = new LoginGUI(this, accountManager.getAccounts());
		if(login.isValid()){
			
			display.setText("");
			
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
			
			txtInput.setEnabled(true);
		}
		
		login = null;
	
	}
	
	public void checkConnected(){
		if(server.isConnected()){
			miLogin.setEnabled(false);
			miDisconnect.setEnabled(true);
			txtInput.setEnabled(true);
		} else {
			miLogin.setEnabled(true);
			miDisconnect.setEnabled(false);
			txtInput.setEnabled(false);
		}
	}
	
	private class WindowListener extends WindowAdapter{

		public void windowClosing(WindowEvent e){
			/* Terminates the program */
			try {
				if(server != null){
					setTitle("Disconnecting...");
					server.disconncetFromServer();
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
			if(server != null){
				server.write("PRIVMSG " + server.getChannel() + " :" +input);
			}
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
}
