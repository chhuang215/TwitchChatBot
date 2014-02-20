package com.chhuang.twitchirc;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chhuang.accounts.Account;
import com.chhuang.channels.Channel;

public class Login {
	
	public static final int LOGIN_CLICKED = 0;

	private ArrayList<Account> accounts;
	private ArrayList<Channel> channels;
	
	private boolean valid = false;
	private int choice;
	private String nick;
	private String pass;
	private String channel;

	private JLabel lblNick;
	private JLabel lblCh;
	private JComboBox<String> jcbNicks;
	private JComboBox<String> jcbChannel;

	public Login(JFrame mainFrame, ArrayList<Account> accounts, ArrayList<Channel> channels) {
		this.accounts =  accounts;
		this.channels = channels;
		choice = JOptionPane.showOptionDialog(mainFrame, getPane(), "Login", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Login", "Cancel"}, "Login");
		if(choice == LOGIN_CLICKED) login();
		
	}
	
	public void login(){

		/*Get selected account*/
		Account selectedAcc = accounts.get(jcbNicks.getSelectedIndex());
		nick = selectedAcc.getNick();
		pass = selectedAcc.getPass();
		
		/*Get selected channel*/
		channel = channels.get(jcbChannel.getSelectedIndex()).getChannel();
		
		valid = true;
	}
	
	private JPanel getPane(){
		
		JPanel mainPane = new JPanel();
		JPanel pLabels = new JPanel();
		JPanel pInputs = new JPanel();
		
		pLabels.setLayout(new GridLayout(2,1, 2, 10));
		pInputs.setLayout(new GridLayout(2,1,1, 2));
		
		lblNick = new JLabel("Nickname: ", JLabel.RIGHT);
		lblCh = new JLabel("Channel: ", JLabel.RIGHT);
		
		String[] strNicks = new String[accounts.size()];
		for(int i = 0; i < accounts.size(); i++){
			strNicks[i] = accounts.get(i).getNick();
		}

		String[] strChannels = new String[channels.size()];
		for(int i = 0; i < channels.size(); i++){
			strChannels[i] = channels.get(i).getChannel();
		}
		
		jcbNicks = new JComboBox<String>(strNicks);
		jcbChannel = new JComboBox<String>(strChannels);

		pLabels.add(lblNick);
		pLabels.add(lblCh);
		pInputs.add(jcbNicks);
		pInputs.add(jcbChannel);
		
		mainPane.add(pLabels);
		mainPane.add(pInputs);
		
		return mainPane;
	}
	
	public String getNick(){
		return nick;
	}
	
	public String getPass(){
		return pass;
	}
	
	public String getChannel(){
		return channel;
	}
	
	public boolean isValid(){
		return valid;
	}

}
