/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.world;

import java.util.Random;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.world.time.Time;

public class Engine implements Runnable {

	boolean exit = false;
	boolean pause = true;
	Time worldMeanTime = null;
	
	public boolean doEnvironmentNeeded = false;
	
	//Engine
	
	public static int TICK_SECONDS = 10;
	
	public static int SECONDS_PER_TURN = 100; 
	public static int SECONDS_PER_ENVIRONMENT = 160; 
	
	public boolean timeChanged = false;
	public boolean turnCome = false;
	public int ticksLeftForTurn = SECONDS_PER_TURN;
	public int ticksLeftForEnvironment = SECONDS_PER_ENVIRONMENT;
	
	
	public long numberOfTurn = 0;
	
	public static Object mutex = new Object();
	
	private transient static Random random = new Random();
	
	public static Random getTrueRandom()
	{
		if (random==null) random = new Random();
		return random;
	}
	
	public void run() {
		System.out.println("ENGINE STARTED");
		while (!exit)
		{
			try{Thread.sleep(1000);}catch (Exception ex){}
			if (!pause) {
				worldMeanTime.tick(TICK_SECONDS);
				ticksLeftForTurn-=TICK_SECONDS;
				ticksLeftForEnvironment-=TICK_SECONDS+getTrueRandom().nextInt(TICK_SECONDS); // this is random
				if (ticksLeftForTurn<=0)
				{
					synchronized (mutex) {
						Jcrpg.LOGGER.info("NEW TURN FOR AI STARTED... pause");
						ticksLeftForTurn = SECONDS_PER_TURN;
						pause = true;
						turnCome = true;
						numberOfTurn++;
					}
				}
				if (ticksLeftForEnvironment<=0)
				{
					synchronized (mutex) {
						Jcrpg.LOGGER.info("NEW ENVIRONMENT");
						ticksLeftForEnvironment = SECONDS_PER_ENVIRONMENT;
						doEnvironmentNeeded = true;
					}
				}
				setTimeChanged(true);
			}
		}
		System.out.println("ENGINE TERMINTATED");
	}
	
	public void exit()
	{
		System.out.println("ENGINE TERMINATING");
		exit = true;
	}

	public boolean isPause() {
		return pause;
	}
	
	/**
	 * Tells if turn for AI has come.
	 * @return true if turn.
	 */
	public boolean turnComes()
	{
		return turnCome;
	}
	/**
	 * Should be called when a turn processing is finished, sets pause off.
	 */
	public void turnFinishedForAI()
	{
		turnCome = false;
		System.out.println("TURN COME DONE.");
	}
	
	public void turnFinishedForPlayer()
	{
		pause = false;
		turnCome = true;
		System.out.println("TURN ENDED, UNPAUSE.");
	}

	public synchronized void setPause(boolean pause) {
		this.pause = pause;
	}

	public Time getWorldMeanTime() {
		return worldMeanTime;
	}

	public void setWorldMeanTime(Time worldMeanTime) {
		this.worldMeanTime = worldMeanTime;
	}
	
	public synchronized void setTimeChanged(boolean state) {
		timeChanged = state;
	}

	/**
	 * Current turn's number.
	 * @return
	 */
	public long getNumberOfTurn() {
		return numberOfTurn;
	}

	public void setNumberOfTurn(long numberOfTurn) {
		this.numberOfTurn = numberOfTurn;
	}

	

}
