package com.chhuang.irc;

import java.io.Serializable;

public class Channel implements Serializable{

	private String channel;
	
	public Channel() {
		channel = "";
	}
	
	public Channel(String channel){
		this.channel = channel;
	}
	
	public void setChannel(String channel){
		this.channel = channel;
	}

	public String getChannel(){
		return channel;
	}
}
