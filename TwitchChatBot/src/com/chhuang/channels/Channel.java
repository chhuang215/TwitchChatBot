package com.chhuang.channels;

import java.io.Serializable;

public class Channel implements Serializable, Comparable<Channel>{

	private static final long serialVersionUID = -7265713389141238271L;
	private String channel;
	private boolean online = false;

	public Channel(String ch){
		channel = ch;
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
	
	public void setOnline(boolean on){
		online = on;
	}

	@Override
	public int compareTo(Channel o) {
		
		return this.getChannel().compareTo(o.getChannel());
	}
}
