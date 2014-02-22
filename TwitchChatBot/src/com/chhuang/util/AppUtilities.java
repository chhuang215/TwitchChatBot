package com.chhuang.util;

import java.awt.Frame;

public class AppUtilities {
	
	private static MemoryUI mem;
	
	public static void showMemory(){
		if(mem == null)
			mem = new MemoryUI();
		mem.setVisible(true);
		mem.setState(Frame.NORMAL);
	}
}
