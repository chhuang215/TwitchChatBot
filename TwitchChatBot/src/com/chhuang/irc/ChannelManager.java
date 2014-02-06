package com.chhuang.irc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ChannelManager {

	public static final String CHANNEL_FILE_NAME = "channels";
	
	private ArrayList<Channel> channels;
	
	public ChannelManager() {
		initializeChannels();
	}
	
	private void initializeChannels(){
		channels = new ArrayList<Channel>();
		if(!loadChannels()){
			channels.add(new Channel("#gemhuang2151992"));
		}
	}
	
	public void saveChannels(){
		
	}

	public boolean loadChannels(){
		try {
			File file = new File(CHANNEL_FILE_NAME);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			channels = (ArrayList<Channel>) ois.readObject();
			
			ois.close();
			fis.close();
			return true;
			
		} catch (IOException e){
			e.printStackTrace();
		 	return false;
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
}
