package com.chhuang.bot;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Vocabulary implements ActionListener{
	
	public static final String VOCAB_FILE = "vocab";
	
	private ArrayList<PhraseCollection> phrases;
	private String nameOfUser;
	
	private JFrame jfrm;
	private JPanel jpLists;
	private JPanel jpLabels;
	
	private JList<String> lstKeys;
	private JList<String> lstPhrases;
	private JLabel lblKeys;
	private JLabel lblPhrases;
	
	private JMenuBar mbMenu;
	private JMenu mKeyOptions;
	private JMenuItem miAddkey;
	private JMenuItem miRemovekey;
	
	private JMenu mFile;
	private JMenuItem miSave;
	private JMenuItem miLoad;
	
	private JMenu mPhraseOptions;
	private JMenuItem miAddphrase;
	private JMenuItem miRemovephrase;
	
	public Vocabulary(){
		jfrm = new JFrame("CrappyBotVocab");
		
		jfrm.setSize(500, 500);
		jfrm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jfrm.setLayout(new BorderLayout());
		jfrm.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				save();
			}
		});
		
		phrases = new ArrayList<PhraseCollection>();
		
		mbMenu = new JMenuBar();
		jfrm.setJMenuBar(mbMenu);
		 
		/*MENU File*/
		mFile = new JMenu("File");
		miSave = new JMenuItem("Save");
		miSave.addActionListener(this);
		miLoad = new JMenuItem("Load");
		miLoad.addActionListener(this);
		mFile.add(miSave);
		mFile.add(miLoad);
		mbMenu.add(mFile);
		
		/*MENU Key Options*/
		mKeyOptions = new JMenu("Key Options");
		miAddkey = new JMenuItem("Add Key");
		miAddkey.addActionListener(this);
		miRemovekey = new JMenuItem("Remove Key");
		miRemovekey.addActionListener(this);
		mKeyOptions.add(miAddkey);
		mKeyOptions.add(miRemovekey);
		mbMenu.add(mKeyOptions);	

		/*MENU Phrase Options*/
		mPhraseOptions = new JMenu("Phrase Options");
		miAddphrase = new JMenuItem("Add Phrase");
		miAddphrase.addActionListener(this);
		miRemovephrase = new JMenuItem("Remove Phrase");
		miRemovephrase.addActionListener(this);
		mPhraseOptions.add(miAddphrase);
		mPhraseOptions.add(miRemovephrase);
		mbMenu.add(mPhraseOptions);
		
		lblKeys = new JLabel("Keys");
		lblKeys.setHorizontalAlignment(SwingUtilities.CENTER);
		lblPhrases = new JLabel("Phrases");
		lblPhrases.setHorizontalAlignment(SwingUtilities.CENTER);
	
		
		lstKeys = new JList<String>(new DefaultListModel<String>());
		lstKeys.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstKeys.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(!arg0.getValueIsAdjusting()){
					int index = lstKeys.getSelectedIndex();
					if(index >= 0){
						DefaultListModel<String> dlm = (DefaultListModel<String>)phrases.get(index).getJListPhrases().getModel();
						lstPhrases.setModel(dlm);
					}
				}
			}
		});
		
		lstPhrases = new JList<String>();
		
		//jtp = new JTabbedPane();
		//jtp.setPreferredSize(new Dimension(400,400));
		//jfrm.getContentPane().add(jtp);
		jpLists = new JPanel(new GridLayout(1,2,6,5));
		jpLists.add(new JScrollPane(lstKeys));
		jpLists.add(new JScrollPane(lstPhrases));
		
		jpLabels = new JPanel(new GridLayout(1,2,2,2));
		jpLabels.add(lblKeys);
		jpLabels.add(lblPhrases);
		
		
		jfrm.getContentPane().add(jpLabels, BorderLayout.NORTH);
		jfrm.getContentPane().add(jpLists, BorderLayout.CENTER);
		
		load();
	}
	
	public String getPhrase(String userInput){
		if(userInput.toLowerCase().contains("my name is")){
			int index = userInput.toLowerCase().indexOf("my");
			userInput = (String)userInput.subSequence(index, userInput.length());
			String splitUI[] = userInput.split(" ");
			nameOfUser = splitUI[3];
		}
		
		String output = searchPhrases(userInput);

		if(output.toLowerCase().contains("*name*")){
			output = output.replace("*name*", nameOfUser);
		}
		if(output.equals("")){
			return "";
		}else{
			return output;
		}
	}
	
	public String searchPhrases(String userInput){
		userInput = userInput.toLowerCase();
		Random r = new Random();
		ArrayList<PhraseCollection> matchingKeys = new ArrayList<PhraseCollection>();
		if(phrases.size() != 0){
			for(int index = 0; index < phrases.size(); index++){
				if(userInput.contains(phrases.get(index).getKey())){
					matchingKeys.add(phrases.get(index));
				}
			}
		}
		else{
			return "";
		}
		
		if(matchingKeys.size() > 0){
			return matchingKeys.get(r.nextInt(matchingKeys.size())).getPhrase();
		}
		
		return "";
	}
	
	public void addKey(String key){
		key = key.toLowerCase();
		if(searchForKey(key) == -1){
			PhraseCollection phraseCollection = new PhraseCollection(key);
			DefaultListModel<String> dlm = (DefaultListModel<String>)lstKeys.getModel();
			dlm.addElement(key);
			phrases.add(phraseCollection);
		}
	}
	
	public void removeKey(){
		DefaultListModel<String> dlm = (DefaultListModel<String>)lstKeys.getModel();
		int index = lstKeys.getSelectedIndex();
		phrases.remove(index);
		dlm.remove(index);
	}
	
	public void addPhrase(String key, String phrase){
		key = key.toLowerCase();
		int index = searchForKey(key);
		if(index != -1){
			phrases.get(index).addPhrase(phrase);
		}
	}
	
	public void addPhrase(String phrase){
		int index = lstKeys.getSelectedIndex();
		phrases.get(index).addPhrase(phrase);
	}
	
	public void removePhrase(){
		int index = lstKeys.getSelectedIndex();
		phrases.get(index).removePhrase(lstPhrases.getSelectedIndex());
	}
	
	public int searchForKey(String key){
		for (int i = 0; i < phrases.size(); i++){
			if(phrases.get(i).getKey().equalsIgnoreCase(key)){
				return i;
			}
		}
		return -1;
	}

	
	public void show(boolean showMe){
		if(showMe){
			jfrm.setVisible(true);
		}else{
			jfrm.setVisible(false);
		}
	}
	
	public void reset(){
		
		DefaultListModel<String> dlm  = (DefaultListModel<String>)lstKeys.getModel();
		dlm.clear();
		
		//jtp.removeAll();
		
		for(int i = 0; i < phrases.size(); i++){
			//jtp.addTab(phrases.get(i).getKey(), phrases.get(i).getJListPhrases());
			dlm.addElement(phrases.get(i).getKey());
		}
	}
	
	public boolean save(){
		try{
			File f = new File(VOCAB_FILE);
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(phrases);
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
	public boolean load(){
		try{
			File f = new File(VOCAB_FILE);
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			phrases = (ArrayList<PhraseCollection>)ois.readObject();
			reset();
			ois.close();
			fis.close();
			return true;
			
		} catch(FileNotFoundException e){
			System.out.println("File Not Found Exception");	
			e.printStackTrace();
			return false;
		} catch(ClassNotFoundException e){
			System.out.println("Class Not Found Exception");
			e.printStackTrace();
			return false;
		} catch(IOException e){
			System.out.println("IOException");
			e.printStackTrace();
			return false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if(command.equalsIgnoreCase("Add Key")){
			String key = JOptionPane.showInputDialog(jfrm,"Please enter key: ");
			addKey(key);
		}
		else if(command.equalsIgnoreCase("Remove Key")){
			removeKey();
		}
		else if(command.equalsIgnoreCase("Add Phrase")){
			String phrase = JOptionPane.showInputDialog(jfrm,"Please enter phrase: ");
			addPhrase(phrase);;
		}
		else if(command.equalsIgnoreCase("Remove Phrase")){
			removePhrase();
		}
		else if(command.equalsIgnoreCase("Save")){
			save();
		}
		else if(command.equalsIgnoreCase("Load")){
			load();
		}
	}
	

}
