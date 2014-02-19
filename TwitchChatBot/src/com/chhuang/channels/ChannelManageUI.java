package com.chhuang.channels;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListCellRenderer;
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
	
	public static final String CHANNEL_MANAGER_UI_TITLE = "Channels";
	
	private ChannelManager channelManager;
	private JList<String> lstOnline;
	private DefaultListModel<String> dlmOnline;

	/**
	 * @param cm
	 */
	public ChannelManageUI(ChannelManager cm) {
		super(CHANNEL_MANAGER_UI_TITLE);
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
		
		initializeMainList();
		
		/*----Online status display----*/
		lstOnline = new JList<String>(new DefaultListModel<String>());
		lstOnline.setOpaque(false);
		((DefaultListCellRenderer)lstOnline.getCellRenderer()).setHorizontalAlignment(JLabel.RIGHT);;
		lstOnline.setPrototypeCellValue("LOADING");
		dlmOnline = (DefaultListModel<String>) lstOnline.getModel();
		
		((JLabel)lstOnline.getCellRenderer()).setOpaque(false);;
		
		JScrollPane jspOnlineLst = new JScrollPane(lstOnline);
		jspOnlineLst.getViewport().setOpaque(false);
		jspOnlineLst.setBorder(null);
		/*-----------------------------*/
		
		getContentPane().add(scrollPaneMainLst, BorderLayout.CENTER);
		getContentPane().add(jspOnlineLst, BorderLayout.WEST);
		
	}

	@Override
	protected void initializeButtons() {
		super.initializeButtons();
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		panelButtons.add(btnRefresh);
	}
	
	public void displayOnlineStatus() {
		dlmOnline.clear();
		for(Channel ch : channelManager.getChannels()){
			if(ch.isOnline()){
				dlmOnline.addElement("ONLINE");
			} else {
				dlmOnline.addElement("      ");
			}
		}
	}

	@Override
	protected void resetList() {
		dlmMainList.clear();
		if(!channelManager.getChannels().isEmpty()){
			for (Channel channel : channelManager.getChannels()){
				dlmMainList.addElement(channel.getChannel());
			}
			lstMain.clearSelection();
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
				displayOnlineStatus();
				resetList();	
			}
		}
	}
	
	@Override
	public void remove() {
		int index = lstMain.getSelectedIndex();
		if(index >= 0){
			int choice = JOptionPane.showOptionDialog(this, "Remove " + channelManager.getSelectedChannel(index).getChannel() + "?", "Remove channel", JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE, null,new String[] {"Yes","Cancel"}, "Cancel");
			if(choice == 0){
				channelManager.removeChannel(index);
				channelManager.saveChannels();
				displayOnlineStatus();
				resetList();
			}
		}
	}
	
	@Override
	public void edit() {
		int index = -1;
		if((index = lstMain.getSelectedIndex()) >= 0){
			String currentSelectedChannel = lstMain.getSelectedValue();
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
					displayOnlineStatus();
					resetList();
				}
			}
		}	
	}
	
	public void refresh(){
		setTitle(getTitle() + " - Refreshing...");
		channelManager.checkOnlineAll();
		displayOnlineStatus();
		resetList();
		setTitle(CHANNEL_MANAGER_UI_TITLE);
	}	
}
