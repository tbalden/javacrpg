package org.jcrpg.world;

public class Engine implements Runnable {

	boolean exit = false;

	public void run() {
		System.out.println("ENGINE STARTED");
		while (!exit)
		{
		}
		System.out.println("ENGINE TERMINTATED");
	}
	
	public void exit()
	{
		System.out.println("ENGINE TERMINATING");
		exit = true;
	}

	

}
