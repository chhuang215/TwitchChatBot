import javax.swing.SwingUtilities;

import com.chhuang.ircconnect.TwitchChatBot;


public class Driver {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TwitchChatBot irc = new TwitchChatBot();
				try {
					irc.start();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		});
	}
}
