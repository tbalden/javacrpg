/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.world;

import java.util.Random;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.time.Time;

/**
 * The core parallel thread for time related and state related things. Don not use this
 * for modifying 3d things!
 * @author illes
 *
 */
public class Engine implements Runnable {

		
	boolean exit = false;
	boolean pause = true;
	Time worldMeanTime = null;
	
	/**
	 * set this true if you want time passing not initializing turns.
	 */
	boolean debugOnlyTime = false;
	
	
	/**
	 * Tells how many turns will take until a full economy
	 * update will be run in EconomyContainer.
	 */
	public static int TURNS_PER_ECONOMY_UPDATE = 100;
	
	/**
	 * Tells if do environment's time has come. 
	 */
	private boolean doEnvironmentNeeded = false;
	
	//Engine
	
	/**
	 * Ticks per new second.
	 */
	public static int TICK_SECONDS = 100;
	// light DEBUG:
	//public static int TICK_SECONDS = 500;
	
	public static int TICK_SECONDS_CAMPING = 500;
	
	public static int SECONDS_PER_TURN = 1000;
	/**
	 * Tells around how many seconds (this is randomly calculated half part) till a new
	 * do environment should be executed (sounds and such only, not gameplay events!)  
	 */
	public static int SECONDS_PER_ENVIRONMENT = 800; 
	
	
	private boolean timeChanged = false;
	private boolean turnCome = false;
	private boolean economyUpdateCome = false;
	private boolean doEconomyUpdate = false;
	
	private boolean camping = false;
	public boolean campingStarted = false;
	public boolean campingFinished = false;
	
	
	public int secondsLeftForTurn = SECONDS_PER_TURN;
	public int secondsLeftForEnvironment = SECONDS_PER_ENVIRONMENT;
	public int turnsLeftForEconomyUpdate = TURNS_PER_ECONOMY_UPDATE;
	
	private boolean turnInterruptedByPlayerInteraction = false;
	
	public int numberOfTurn = 0;
	
	public static Object mutex = new Object();
	
	private transient static Random random = new Random();
	
	public static Random getTrueRandom()
	{
		if (random==null) random = new Random();
		return random;
	}
	
	public void run() {
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ENGINE STARTED");
		while (!exit)
		{
			try{Thread.sleep(1000);}catch (Exception ex){}
			if (!pause && !outOfGameSystemPause) {
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("### TICK-TACK ###");
				int secondsPast = camping?TICK_SECONDS_CAMPING:TICK_SECONDS;
				worldMeanTime.tick(secondsPast);
				
				if (!debugOnlyTime)
				{
					secondsLeftForTurn-=secondsPast;
					secondsLeftForEnvironment-=secondsPast+getTrueRandom().nextInt(secondsPast); // this is random
					if (secondsLeftForTurn<=0)
					{
						synchronized (mutex) {
							Jcrpg.LOGGER.info("NEW TURN FOR AI STARTED... pause");
							secondsLeftForTurn = SECONDS_PER_TURN;
							turnsLeftForEconomyUpdate-=1;
							setPause(true);
							turnCome = true;
							numberOfTurn++;
							if (turnsLeftForEconomyUpdate<=0)
							{
								Jcrpg.LOGGER.info("NEW ECONOMY TURN!!");
								economyUpdateCome = true;
								turnsLeftForEconomyUpdate = TURNS_PER_ECONOMY_UPDATE;
							}
						}
					}
					if (secondsLeftForEnvironment<=0)
					{
						synchronized (mutex) {
							Jcrpg.LOGGER.info("NEW ENVIRONMENT");
							secondsLeftForEnvironment = SECONDS_PER_ENVIRONMENT;
							if (!camping) {
								doEnvironmentNeeded = true;
							}
						}
					}
				}
				setTimeChanged(true);
			}
		}
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ENGINE TERMINTATED");
	}
	
	public void exit()
	{
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ENGINE TERMINATING");
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
	 * Should be called when a turn processing is finished.
	 */
	public void turnFinishedForAI()
	{
		turnCome = false;
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("TURN COME DONE.");
		if (!turnInterruptedByPlayerInteraction)
		{
			 if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("NO INTERRUPTION...");
			 // no interruption happened, economy update can be done right now... because no turnFinishedForPlayer
			 // will happen.
			 if (economyUpdateCome) doEconomyUpdate = true;
		} else
		{
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("INTERRUPTION...");
			turnInterruptedByPlayerInteraction = false;
		}
	}
	
	public void turnFinishedForPlayer()
	{
		// continue with interrupted AI things... (j3dcore doUpdate will call ecology.)
		turnCome = true;
	}
	
	public void ecologyTurnFinished()
	{
		if (!economyUpdateCome)
		{
			setPause(false);
		}
	}
	
	public void economyUpdateFinished()
	{
		setPause(false);
		doEconomyUpdate = false;
		economyUpdateCome = false;
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("TURN ENDED (Economy), UNPAUSE.");
	}

	public synchronized void setPause(boolean pause) {
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("---### PAUSE = "+pause);
		this.pause = pause;
	}
	
	private boolean outOfGameSystemPause = false;
	
	public synchronized boolean isOutOfGameSystemPause()
	{
		return outOfGameSystemPause;
	}
	public synchronized void pauseForRendering()
	{
		outOfGameSystemPause = true;
	}
	public synchronized void unpauseAfterRendering()
	{
		outOfGameSystemPause = false;
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
	public synchronized boolean hasTimeChanged()
	{
		return timeChanged;
	}

	/**
	 * Current turn's number.
	 * @return
	 */
	public int getNumberOfTurn() {
		return numberOfTurn;
	}

	public void setNumberOfTurn(int numberOfTurn) {
		this.numberOfTurn = numberOfTurn;
	}
	
	public boolean checkEconomyUpdateNeeded()
	{
		return doEconomyUpdate;
	}

	/**
	 * Call this when ecology turn is still being calculated, but user interaction is being done,
	 * and after it turn will continue.
	 */
	public void ecologyTurnInterruptedByPlayerInteraction()
	{
		turnInterruptedByPlayerInteraction = true;
	}
	
	public boolean isEnvironmentUpdateNeeded()
	{
		return doEnvironmentNeeded;
	}
	
	public void environemntUpdateDone()
	{
		doEnvironmentNeeded = false;
	}
	
	public void startCamping()
	{
		camping = true;
		campingFinished = false;
		J3DCore.getInstance().gameState.player.theFragment.fragmentState.isCamping = true;
		campingStarted = true;
	}
	public void endCamping()
	{
		camping = false;
		campingStarted = false;
		J3DCore.getInstance().gameState.player.theFragment.fragmentState.isCamping = false;
		campingFinished = true;
	}
	public boolean isCamping()
	{
		return camping;
	}
	
	public int getBaseTimeSeed()
	{
		long seed = HashUtil.WORLD_RANDOM_SEED+ getNumberOfTurn()+ getWorldMeanTime().getTimeInInt();
		return (int)seed;
	}

}
