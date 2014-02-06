package com.chhuang.accounts;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class AccountManagerUI extends JFrame{
	
	public static final Font DEFAULT_FONT = new Font("Arial",Font.PLAIN,15);
	
	private AccountManager accountManager;
	private DefaultListModel<String> dlm;
	private JList<String> lstNicks;
	private JScrollPane scrollPane;
	private JTextField lblPass;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnEdit;
	private JPanel panelButtons;
	private JPanel panelAccountInfo;
	
	public AccountManagerUI(AccountManager accManage) {
		this.setTitle("Accounts");
		setSize(330,720);
		setLayout(new BorderLayout(5,5));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				accountManager.saveAccounts();
			}
		});
		
		this.accountManager = accManage;
		
		initializeNickList();
		
		initializeButtons();		
		
		lblPass = new JTextField(":");
		lblPass.setFont(DEFAULT_FONT);
		lblPass.setHorizontalAlignment(SwingUtilities.CENTER);
		lblPass.setBorder(null);
		lblPass.setOpaque(false);
		lblPass.setEditable(false);
		
		panelButtons = new JPanel(new GridLayout(1, 3, 2, 2));
		panelButtons.add(btnAdd); 
		panelButtons.add(btnRemove);
		panelButtons.add(btnEdit);
		
		panelAccountInfo = new JPanel(new BorderLayout(5,5));
		panelAccountInfo.add(scrollPane, BorderLayout.CENTER);
		panelAccountInfo.add(lblPass, BorderLayout.SOUTH);
	
		this.getContentPane().add(panelAccountInfo, BorderLayout.CENTER);
		this.getContentPane().add(panelButtons, BorderLayout.SOUTH);
		
	}

	public void initializeButtons(){
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addAccount();
			}
		});
		
		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAccount();
			}
		});
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editAccount();
			}
		});
	}
	
	public void removeAccount(){
		int index = lstNicks.getSelectedIndex();
		if(index >= 0){
			int choice = JOptionPane.showOptionDialog(this, "Remove " + accountManager.getSelectedAccount(index).getNick() + "?", "Remove account", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Yes","Cancel"}, "Cancel");
			if(choice == 0){
				accountManager.removeAccount(index);
				resetList();
			}
		}
	}
	
	public void editAccount(){
		int index = -1;
		if((index = lstNicks.getSelectedIndex()) >= 0){
			Account acc = accountManager.getSelectedAccount(index);
			
			JPanel p = new JPanel();
			JPanel pLabels = new JPanel();
			JPanel pInputs = new JPanel();
			
			pLabels.setLayout(new GridLayout(2,1));
			pInputs.setLayout(new GridLayout(2,1));
			
			JLabel lblNick = new JLabel("Nickname: ", JLabel.RIGHT);
			JLabel lblOath = new JLabel("Pass: ", JLabel.RIGHT);
			JTextField txtNick = new JTextField(acc.getNick(),20);
			JTextField txtOauth = new JTextField(acc.getPass(),26);
			pLabels.add(lblNick);
			pLabels.add(lblOath);
			pInputs.add(txtNick);
			pInputs.add(txtOauth);
			
			p.add(pLabels);
			p.add(pInputs);
			
			int choice = JOptionPane.showOptionDialog(this, p, "Editing - " + acc.getNick(), JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Edit", "Cancel"}, "Edit");
			
			if(choice == 0){
				String nick = txtNick.getText();
				String oauth = txtOauth.getText(); 
				if (nick != null && !nick.trim().equals("")){
					nick = nick.trim();
					oauth = oauth.trim();
					accountManager.editAccount(index, nick, oauth);
					
					resetList();
				}
			}
		}
	}

	public void addAccount() {
		JPanel p = new JPanel();
		JPanel pLabels = new JPanel();
		JPanel pInputs = new JPanel();
		
		pLabels.setLayout(new GridLayout(2,1));
		pInputs.setLayout(new GridLayout(2,1));
		
		JLabel lblNick = new JLabel("Nickname: ", JLabel.RIGHT);
		JLabel lblOath = new JLabel("Pass: ", JLabel.RIGHT);
		JTextField txtNick = new JTextField(20);
		JTextField txtOauth = new JTextField(26);
		pLabels.add(lblNick);
		pLabels.add(lblOath);
		pInputs.add(txtNick);
		pInputs.add(txtOauth);
		
		p.add(pLabels);
		p.add(pInputs);

		int choice = JOptionPane.showOptionDialog(this, p, "Add account", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Add"}, "Add");
		
		if(choice == 0){
			String nick = txtNick.getText();
			String oauth = txtOauth.getText(); 
			
			if (nick != null && !nick.trim().equals("")){
				nick = nick.trim();
				oauth = oauth.trim();
				accountManager.addAccount(nick, oauth);
				resetList();
			}
		}
	}

	public void resetList(){
		dlm.clear();
		if(!accountManager.getAccounts().isEmpty()){
			for(Account acc : accountManager.getAccounts()){
				dlm.addElement(acc.getNick());
			}
			lstNicks.clearSelection();
		}
	}
	
	public void initializeNickList(){
		dlm = new DefaultListModel<String>();
		lstNicks = new JList<String>(dlm);
		lstNicks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstNicks.setFont(DEFAULT_FONT);
		resetList();
		
		lstNicks.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(!arg0.getValueIsAdjusting()){
					int index = lstNicks.getSelectedIndex();
					if(index >= 0){
						lblPass.setText(accountManager.getSelectedAccount(index).getPass());
					}else{
						lblPass.setText("");;
					}
				}
			}
		});
		
		lstNicks.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2){
					editAccount();
				}
			}
		});
		
		scrollPane = new JScrollPane(lstNicks);
	}
}
