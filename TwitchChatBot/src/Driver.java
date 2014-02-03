import javax.swing.SwingUtilities;

import com.chhuang.irc.MainChatBoxUI;


public class Driver {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainChatBoxUI();
			}
		});
	}
}
