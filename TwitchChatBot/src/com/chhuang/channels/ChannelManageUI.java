package com.chhuang.channels;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.chhuang.irc.ManageUI;

@SuppressWarnings("serial")
public class ChannelManageUI extends ManageUI {
	
	private ChannelManager channelManager;
	private JList<String> lstOnline;

	/**
	 * @param cm
	 */
	public ChannelManageUI(ChannelManager cm) {
		super("Channels");
		this.channelManager = cm;
		initializeUI();
	}
	
	private void initializeUI(){
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - getWidth(), 0);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				channelManager.saveChannels();
			}
		});		
		
		lstOnline = new JList<String>(new DefaultListModel<String>());
		lstOnline.setOpaque(false);	
		
		((JLabel)lstOnline.getCellRenderer()).setOpaque(false);;
		
		JScrollPane jspOnlineLst = new JScrollPane(lstOnline);
		jspOnlineLst.getViewport().setOpaque(false);
		jspOnlineLst.setBorder(null);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String title = getTitle();
				setTitle(title + " - Refreshing...");
				for(Channel ch : channelManager.getChannels()){
					ch.checkOnline();
				}
				checkOnline();
				resetList();
				setTitle(title);
			}
		});
		
		initializeList();
		
		panelButtons.add(btnRefresh);
		getContentPane().add(scrollPaneMainLst, BorderLayout.CENTER);
		getContentPane().add(jspOnlineLst, BorderLayout.WEST);
		
	}

	public void checkOnline() {
		DefaultListModel<String> dlm = (DefaultListModel<String>) lstOnline.getModel();
		dlm.clear();
		for(Channel ch : channelManager.getChannels()){
			if(ch.isOnline()){
				dlm.addElement("ONLINE");
			} else {
				dlm.addElement("      ");
			}
		}
	}

	@Override
	protected void resetList() {
		dlm.clear();
		if(!channelManager.getChannels().isEmpty()){
			for (Channel channel : channelManager.getChannels()){
				dlm.addElement(channel.getChannel());
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
			String channel = txtCh.getText().trim();
			
			if(!channel.isEmpty()){
				channel = channel.toLowerCase();
				if(channelManager.addChannel(channel) != 0){
					JOptionPane.showMessageDialog(this, "Channel already exists!");	
				}else
					channelManager.saveChannels();
				checkOnline();
				resetList();	
			}
		}
	}
	
	@Override
	public void remove() {
		int index = lst.getSelectedIndex();
		if(index >= 0){
			int choice = JOptionPane.showOptionDialog(this, "Remove " + channelManager.getSelectedChannel(index).getChannel() + "?", "Remove channel", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Yes","Cancel"}, "Cancel");
			if(choice == 0){
				channelManager.removeChannel(index);
				channelManager.saveChannels();
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
					channelManager.saveChannels();
					resetList();
				}
			}
		}	
	}
}
