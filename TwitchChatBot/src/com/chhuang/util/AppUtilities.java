package com.chhuang.util;

public class AppUtilities {
	
	private static MemoryUI mem;
	
	public static void showMemory(){
		if(mem == null)
			mem = new MemoryUI();
		mem.setVisible(true);		
	 	
	}
}
