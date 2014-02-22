package com.chhuang.accounts;

import java.io.Serializable;

public class Account implements Serializable, Comparable<Account>{

	private static final long serialVersionUID = 8490559367632667836L;
	private String nick;
	private String pass;
	public static final String CRAPPY_BOT = "CrappyBot";
	
	public Account(){
		nick = "";
		pass = "";
	}
	
	public Account(String nick, String pass){
		this.nick = nick;
		this.pass = pass;
	}
	
	public void setNick(String nick){
		this.nick = nick;
	}
	
	public String getNick(){
		return this.nick;
	}
	
	public void setPass(String pass){
		this.pass = pass;
	}
	
	public String getPass(){
		return this.pass;
	}

	@Override
	public int compareTo(Account o) {
		
		return this.getNick().compareTo(o.getNick());
	}
}
