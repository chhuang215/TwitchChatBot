package com.chhuang.channels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class ChannelOnlineThread implements Runnable{
	public final static String URL_PREFIX = "http://api.justin.tv/api/stream/list.json?jsonp=&channel=";
	
	private Channel channel;
	
	public ChannelOnlineThread(Channel ch){
		channel = ch;
	}
	
	public void run() {
		try {
			URL url = new URL(URL_PREFIX + channel.getChannel().substring(1));
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			if(!br.readLine().equals("[]")){
				channel.setOnline(true);
				return;
			}
			channel.setOnline(false);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
