package com.chhuang.irc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ChannelManager {

	public static final String CHANNEL_FILE_NAME = "channels";
	
	private ArrayList<String> channels;
	
	public ChannelManager() {
		initializeChannels();
	}
	
	private void initializeChannels(){
		channels = new ArrayList<String>();
		if(!loadChannels()){
			channels.add("#gemhuang2151992");
		}
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
			channels = (ArrayList<String>) ois.readObject();
			
			ois.close();
			fis.close();
			return true;
			
		} catch (IOException | ClassNotFoundException e){
			System.out.println("NO FILE FOUND, NEW FILE [channels] CREATED!");
		 	return false;
		}

		
	}
}
