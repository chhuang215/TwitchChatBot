package com.chhuang.channels;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.chhuang.irc.ManageUI;

@SuppressWarnings("serial")
public class ChannelManageUI extends ManageUI {
	
	private ChannelManager channelManager;
	
	/**
	 * @param _channelManager
	 */
	public ChannelManageUI(ChannelManager _channelManager) {
		super("Channels");
		this.channelManager = _channelManager;
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - getWidth(), 0);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				channelManager.saveChannels();
			}
		});
		
		initializeList();
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	@Override
	protected void resetList() {
		dlm.clear();
		if(!channelManager.getChannels().isEmpty()){
			for (String channel : channelManager.getChannels()){
				dlm.addElement(channel);
			}
			lst.clearSelection();
		}
	}
	
	@Override
	public void add() {
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
	
	@Override
	public void remove() {
		int index = lst.getSelectedIndex();
		if(index >= 0){
			int choice = JOptionPane.showOptionDialog(this, "Remove " + channelManager.getSelectedChannel(index) + "?", "Remove channel", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Yes","Cancel"}, "Cancel");
			if(choice == 0){
				channelManager.removeChannel(index);
				resetList();
			}
		}
	}
	
	@Override
	public void edit() {
		int index = -1;
		if((index = lst.getSelectedIndex()) >= 0){
			String currentSelectedChannel = lst.getSelectedValue();
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
