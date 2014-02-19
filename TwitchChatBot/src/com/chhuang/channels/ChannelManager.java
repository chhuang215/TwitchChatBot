package com.chhuang.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChannelManager implements Comparator<Channel>{

	public static final String CHANNEL_FILE_NAME = "channels";
	
	private ArrayList<Channel> channels;
	private ChannelManageUI ui;
	
	public ChannelManager() {
		initializeChannels();
	}
	
	public void showUI(){
		if(ui == null)
			ui = new ChannelManageUI(this);

		ui.displayOnlineStatus();
		ui.setVisible(true);
	}
	
	private void initializeChannels(){
		if(!loadChannels()){
			channels = new ArrayList<Channel>();
		}
		
		checkOnlineAll();
	}	
	
	public void checkOnline(int index){
		Channel ch = channels.get(index);
		checkOnline(ch);
	}
	
	public void checkOnline(Channel ch){
		try {
			Thread thread = new Thread(new ChannelOnlineThread(ch));
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void checkOnlineAll(){
		if(channels.isEmpty()) return;
		try {
			Thread threads[] = new Thread[channels.size()];
			for(int i = 0; i < threads.length; i++){
				threads[i] = new Thread(new ChannelOnlineThread(channels.get(i)));
				threads[i].start();
			}
			
			for(Thread t : threads){
				if(t.isAlive())
					t.join();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int addChannel(String channel){
		if(!channel.trim().isEmpty()){
			channel = channel.trim().toLowerCase();
			
			while(channel.startsWith("#")){
				channel = channel.substring(1);
			}
			
			channel = "#" + channel;
			
			for(Channel ch : channels){
				if(ch.getChannel().equalsIgnoreCase(channel)){
					return 1;
				}
			}

			Channel newCh = new Channel(channel);
			checkOnline(newCh);
			channels.add(newCh);
			Collections.sort(channels, this);
			return 0;

		}
		return -1;
	}

	public void removeChannel(int index){
		if(index >= 0){
			channels.remove(index);
			Collections.sort(channels, this);
		}
	}
	
	public void editChannel(int index, String channel){
		if(index >=0){
			channel = channel.trim().toLowerCase();
			while(channel.startsWith("#")){
				channel = channel.substring(1);
			}
			channels.get(index).setChannel("#" + channel);
			checkOnline(index);
			Collections.sort(channels, this);
		}
	}

	public Channel getSelectedChannel(int index){
		return channels.get(index);
	}

	public Channel getSelectedChannel(String ch){	
		return getSelectedChannel(channels.indexOf(ch));
	}

	public ArrayList<Channel> getChannels(){
		return channels;
	}

	public boolean saveChannels(){
		try{
			File f = new File(CHANNEL_FILE_NAME);
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(channels);
			oos.close();
			fos.close();
			return true;
			
		} catch (IOException e){
			System.out.println("IOException");
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean loadChannels(){
		try {
			
			File file = new File(CHANNEL_FILE_NAME);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			channels = (ArrayList<Channel>) ois.readObject();
			
			ois.close();
			fis.close();
			return true;
			
		} catch (IOException | ClassNotFoundException e){
			System.out.println("NO FILE FOUND, NEW FILE [channels] CREATED!");
		 	return false;
		}

	}

	@Override
	public int compare(Channel c1, Channel c2) {
		return c1.getChannel().compareToIgnoreCase(c2.getChannel());
	}
}