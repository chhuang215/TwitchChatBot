package com.chhuang.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextPane;

import com.chhuang.bot.Bot;
import com.chhuang.display.ChatDisplay;
import com.chhuang.display.ServerMessageDisplay;

public class Client {
	//public static ArrayList<Server> running_clients = new ArrayList<Client>(); FUTURE

	public static final int DEFAULT_PORT = 6667;
	public static final String DEFAULT_SERVER = "irc.twitch.tv";
	public static final String CRAPPY_BOT = "CrappyBot";
	public static final String[] IRC_COMMANDS = {"ADMIN", "AWAY", "CNOTICE", "CPRIVMSG", "CONNECT", "DIE", "ENCAP","ERROR","HELP"
		,"INFO","INVITE","ISON", "JOIN", "KICK","KILL","KNOCK","LINKS","LIST","LUSERS","MODE","MOTD","NAMES","NAMESX","NICK","NOTICE"
		,"OPER","PART","PASS","PING","PONG","PRIVMSG","QUIT","REHASH","RESTART","RULES","SERVER","SERVICE","SERVLIST","SQUERY","SQUIT"
		,"SETNAME","SILENCE","STATS","SUMMON","TIME","TOPIC","TRACE","UHNAMES","USER","USERHOST","USERIP","USERS","VERSION","WALLOPS"
		,"WATCH","WHO","WHOIS","WHOWAS"};
	
	private boolean connectedToServer = false;
	private boolean connectedToChannel = false;
	private boolean botMode = false;
	
	private String nick;
	private String pass;
	private String channel;
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private ChatDisplay chatDisplay;
	private ServerMessageDisplay messageDisplay;
	private MessageListener ml;
	private Bot bot;
	
	private Thread incoming;
	
	
	public Client(String nick, String pass, String channel, JTextPane jtpChatDisplay, JTextPane jtpMessageDisplay) {
		this.nick = nick;
		this.pass = pass;
		this.channel = channel;
		chatDisplay = new ChatDisplay(jtpChatDisplay);
		chatDisplay.setUserNick(nick);
		chatDisplay.setCurrentChannel(channel);
		messageDisplay = new ServerMessageDisplay(jtpMessageDisplay);
		chatDisplay.reset();
		
		ml = new MessageListener(chatDisplay, messageDisplay);
		if(nick.equalsIgnoreCase(CRAPPY_BOT)){
			insertBot(new Bot());	
		}
	} 

	public void connectToServer(String hostname, int port) throws UnknownHostException, IOException{
		socket = new Socket(hostname, port);
				
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")); 
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"), 6000);
		
		ml.setWriter(writer);
		
		write("PASS " + pass);
		write("NICK " + nick);
		
		incoming = new Thread(new Incoming());
		incoming.start();
	
	}
	
	public void disconnectFromServer() throws IOException, InterruptedException{

		write("QUIT");
		
		incoming.join();

		connectedToServer = false;
		connectedToChannel = false;
		botMode = false;
		messageDisplay.reset();
		System.gc();
	}
	
	public void connectToChannel(){
		connectToChannel(channel);
	}
	
	public void connectToChannel(String channel){
		
		this.channel = channel.toLowerCase();
		write("JOIN " + channel);
	}
	
	public void write(String msg){		
		try {		
			ml.write(msg);
		} catch (IOException e) {
			chatDisplay.output("*You are not connected to server*");
			chatDisplay.output(msg);
		}
	}
	
	public void insertBot(Bot bot){
		this.bot = bot;
		bot.setChannel(channel);
		botMode = true;
	}
	
	public String getChannel(){
		return channel;
	}
	
	public boolean isConnected(){
		return connectedToServer && connectedToChannel;
	}
	
	public boolean isConnectedToServer(){
		return connectedToServer;
	}
	
	public Bot getBot(){
		return bot;
	}
	
	public boolean isBotMode(){
		return botMode;
	}
	
	private class Incoming implements Runnable {

		public void run() {
			String line = null;
			try {		
				/*vCONTINUE READING FROM SOCKETv*/
				while(!Thread.currentThread().isInterrupted() && ((line = reader.readLine()) != null)){
					//checkMemory();
					ml.output(line);
					
					/*--AVOID DISCONNECTION--*/
					if (line.startsWith("PING ")){
						write("PONG " + line.substring(5));
					}
					/*-----------------------*/
					
					if(!isConnected()){
						if(line.indexOf("001") >= 0){
							chatDisplay.output("Authenticate Success!");
							connectedToServer = true;
						} else if(line.indexOf("353") >= 0 ){
							chatDisplay.output("Connected to " + channel);
							connectedToChannel = true;
						} else if(line.indexOf("Login unsuccessful") >= 0){
							chatDisplay.output("Login unsuccessful");
							break;
						}
						continue;
					}
					
					if(botMode && !chatDisplay.getLastChatMessage().equals("")){
						write(bot.generateOutput(chatDisplay.getLastChatMessage()));
					}
				}
				/*^CONTINUE READING FROM SOCKET^*/
				
				// Disconnect, close all the buffers and socket
				chatDisplay.output("**DISCONNECTED**");
				
				reader.close();
				writer.close();
				socket.close();
				
			} catch (IOException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				System.out.println("UNEXPECTED DISCONNECTION!");
				connectedToServer = false;
				connectedToChannel = false;
			} 
		}
	}
}
