package com.chhuang.bot;

import java.io.Serializable;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JList;


public class PhraseCollection implements Serializable{
	
	private static final long serialVersionUID = -7678063543612420813L;
	private String key;
	private DefaultListModel<String> dlm;
	private JList<String> lstPhrases;
	
	public PhraseCollection(String key){
		this.key = key;
		dlm = new DefaultListModel<String>();
		lstPhrases = new JList<String>(dlm);
	}
	
	public String getPhrase(){
		Random r = new Random();
		if (dlm.size() != 0){
			return dlm.get(r.nextInt(dlm.size()));
		}
		return "";
	}
	
	public JList<String> getJListPhrases(){
		return lstPhrases;
	}
	
	public void addPhrase(String phrase){
		dlm.addElement(phrase);
	}
	
	public void removePhrase(){
		int index = lstPhrases.getSelectedIndex();
		if(index >= 0){
			dlm.remove(index);
		}
	}
	
	public String getKey(){
		return key;
	}
}
