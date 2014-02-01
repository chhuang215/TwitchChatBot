package com.chhuang.ircconnect;

public class Bot {
	private Vocabulary vocab;
	
	public Bot(Vocabulary vocab){
		this.vocab = vocab;
	}
	
	public String generateOutput(String userInput){
		return vocab.getPhrase(userInput);
	}
}
