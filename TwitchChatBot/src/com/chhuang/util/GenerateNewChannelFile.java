package com.chhuang.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.chhuang.channels.Channel;

public class GenerateNewChannelFile {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		
		ArrayList<String> channelString = new ArrayList<String>();
		ArrayList<Channel> channels = new ArrayList<Channel>();
		
		try {
			File file = new File("channels");
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			channelString = (ArrayList<String>) ois.readObject();
			
			ois.close();
			fis.close();
			
		} catch (IOException | ClassNotFoundException e){
			System.out.println("NO FILE FOUND [channels]!");
		}
		
		for(String ch : channelString){
			channels.add(new Channel(ch));
		}
		
		try{
			File f = new File("channels.new");
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(channels);
			oos.close();
			fos.close();
			System.out.println("NEW FILE CREATED [channels.new]");
		} catch (IOException e){
			System.out.println("IOException");
			e.printStackTrace();
		}
		
	}
}
