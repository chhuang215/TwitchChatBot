package com.chhuang.channels;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

@SuppressWarnings("serial")
public class ChannelManagerUI extends JFrame {
	
	public static final Font DEFAULT_FONT = new Font("Arial",Font.PLAIN,15);
	
	private ChannelManager channelManager;
	
	private DefaultListModel<String> dlm;
	private JList<String> lstChannels;
	private JScrollPane scrollPane;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnEdit;
	private JPanel panelButtons;
	
	public ChannelManagerUI(ChannelManager _channelManager) {
		setTitle("Channels");
		setLocation(1036, 0);
		setSize(330,720);
		setLayout(new BorderLayout(5,5));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				channelManager.saveChannels();
			}
		});
		this.channelManager = _channelManager;
		
		initializeChannelList();
		
		initializeButtons();
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(panelButtons, BorderLayout.SOUTH);
		
		
	}
	
	private void initializeButtons(){
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addChannel();
			}
		});
		
		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeChannel();
			}
		});
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editChannel();
			}
		});
		
		panelButtons = new JPanel(new GridLayout(1,3,2,2));
		panelButtons.add(btnAdd);
		panelButtons.add(btnRemove);
		panelButtons.add(btnEdit);
		
	}
	
	private void initializeChannelList(){
		dlm = new DefaultListModel<String>();
		lstChannels = new JList<String>(dlm);
		lstChannels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstChannels.setFont(DEFAULT_FONT);
		
		resetList();
		
		lstChannels.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					editChannel();
				}
			}
		});
		
		scrollPane = new JScrollPane(lstChannels);
	}

	private void resetList(){
		dlm.clear();
		if(!channelManager.getChannels().isEmpty()){
			for (String channel : channelManager.getChannels()){
				dlm.addElement(channel);
			}
			lstChannels.clearSelection();
		}
	}
	
	public void addChannel(){
		JPanel panel = new JPanel(new FlowLayout());
		
		JLabel lblCh = new JLabel("Channel: #", JLabel.RIGHT);
		
		JTextField txtCh = new JTextField(20);
		
		panel.add(lblCh);
		panel.add(txtCh);
		
		int choice = JOptionPane.showOptionDialog(this, panel, "Add Channel", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Add", "Cancel"}, "Add");
		
		if(choice == 0){
			String channel = txtCh.getText();
			
			if(channel != null && !channel.trim().equals("")){
				channel = channel.trim().toLowerCase();
				channelManager.addChannel(channel);
				resetList();
			}
		}
		
	}
	
	public void removeChannel(){
		int index = lstChannels.getSelectedIndex();
		if(index >= 0){
			int choice = JOptionPane.showOptionDialog(this, "Remove " + channelManager.getSelectedChannel(index) + "?", "Remove channel", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Yes","Cancel"}, "Cancel");
			if(choice == 0){
				channelManager.removeChannel(index);
				resetList();
			}
		}
	}
	
	private void editChannel(){
		int index = -1;
		if((index = lstChannels.getSelectedIndex()) >= 0){
			String currentSelectedChannel = lstChannels.getSelectedValue();
			while(currentSelectedChannel.startsWith("#")){
				currentSelectedChannel = currentSelectedChannel.substring(1);
			}
			JPanel panel = new JPanel(new FlowLayout());
			JLabel lblCh = new JLabel("Channel: #", JLabel.RIGHT);
			JTextField txtCh = new JTextField(currentSelectedChannel, 20);
			
			panel.add(lblCh);
			panel.add(txtCh);
			
			int choice = JOptionPane.showOptionDialog(this, panel, "Editing - " + currentSelectedChannel, JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Edit", "Cancel"}, "Edit");
			
			if (choice == 0){
				String editedChannel = txtCh.getText().trim().toLowerCase();
				if(editedChannel != null && !editedChannel.equalsIgnoreCase(currentSelectedChannel) && !editedChannel.equals("")){
					channelManager.editChannel(index, editedChannel);
					resetList();
				}
			}
		}	
	}
}
