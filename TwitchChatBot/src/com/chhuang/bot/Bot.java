package com.chhuang.bot;

public class Bot {
	private Vocabulary vocab;
	private String channel;
	
	public Bot(Vocabulary vocab){
		this.vocab = vocab;
	}
	
	public String generateOutput(String userInput){
		String output = vocab.getPhrase(userInput);
		
		if(output.equals("")){
			return "";
		}else{
			return "PRIVMSG " + channel +" :" + output;
		}
			
	
	}
	
	public void setChannel(String channel){
		this.channel = channel;
	}
}
