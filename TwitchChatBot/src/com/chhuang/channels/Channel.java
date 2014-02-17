package com.chhuang.channels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class Channel implements Serializable{

	private static final long serialVersionUID = -7265713389141238271L;
	public final static String URL_PREFIX = "http://api.justin.tv/api/stream/list.json?jsonp=&channel=";
	private String channel;
	private boolean online = false;

	public Channel(String ch){
		channel = ch;
	}
	
	public synchronized void checkOnline(){
		new Thread(new CheckOnlineThread()).start();;
	}
	
	public void setChannel(String ch){
		channel = ch;
	}
	
	public String getChannel(){
		return channel;
	}
	
	public boolean isOnline(){
		return online;
	}

	private class CheckOnlineThread implements Runnable{
		@Override
		public void run() {
			try {
				URL url = new URL(URL_PREFIX + channel.substring(1));
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				if(!br.readLine().equals("[]")){
					online = true;
					return;
				}
				online = false;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
