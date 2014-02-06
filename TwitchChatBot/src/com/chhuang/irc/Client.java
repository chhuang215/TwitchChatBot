package com.chhuang.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.chhuang.bot.Bot;

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
	
	private boolean connected = false; 
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private DisplayService display;
	private Bot bot;
	
	private String channel;
	
	private Thread incoming;
	
	public Client(String nick, String pass, DisplayService displayService) {
		this.display = displayService;
		bot = null;
		connectToServer(DEFAULT_SERVER, DEFAULT_PORT, nick, pass);		
		
	} 

	public void connectToServer(String hostname, int port, String nick, String pass){
		try {
			socket = new Socket(hostname, port);
			
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")); 
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
			write("PASS " + pass);
			write("NICK " + nick);
			
			incoming = new Thread(new Incoming());
			incoming.start();
			
		} catch (IOException e) {
			e.printStackTrace();
			display.output("NOT ABLE TO CONNECT TO " + hostname + "/" + port);
		}
		
	}
	
	public void disconnectFromServer() throws IOException, InterruptedException{
		
		write("QUIT");

		incoming.join();
		
		reader.close();
		writer.close();
		socket.close();

		connected = false;
	}
	
	public void connectToChannel(String channel){
		
		this.channel = channel.toLowerCase();

		write("JOIN " + channel);
	}
	
	/*public void disconnectFromCurrentChannel() throws IOException{
		write("PART " + channel);
	}*/
	
	public void write(String msg){
		
		try {
			
			if(msg != null && !msg.trim().equals("")){	
				writer.write(msg + "\r\n");
				writer.flush();
				display.output(msg);
			}
			
		} catch (IOException e) {
			display.output("*You are not connected to server*");
			display.output(msg);
		}
	}
	
	public void insertBot(Bot bot){
		this.bot = bot;
		bot.setChannel(channel);
	}
	
	public String getChannel(){
		return channel;
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	private class Incoming implements Runnable {

		public void run() {
			String line = null;
			try {				
				while(!Thread.currentThread().isInterrupted() && ((line = reader.readLine()) != null)){
					
					display.output(line);
					
					/*--AVOID DISCONNECTION--*/
					if (line.startsWith("PING ")){
						write("PONG " + line.substring(5));
						//write("PRIVMSG " + channel + " :I got pinged!");
					}
					/*-----------------------*/
					
					if(!connected){
						if(line.indexOf("001") >= 0){
							display.output("Authenticate Success!");
							connected = true;
						}
						else if(line.indexOf("Login unsuccessful") >= 0){
							display.output("Login unsuccessful");
							return;
						}
					}
					
					if(bot != null && !display.getMessage().equals("")){
						write(bot.generateOutput(display.getMessage()));
					}
				}
				
				display.output("**DISCONNECTED**");
			} catch (IOException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				System.out.println("SOCKET DISCONNECT!");
				connected = false;
			} 
		}
	}
}
