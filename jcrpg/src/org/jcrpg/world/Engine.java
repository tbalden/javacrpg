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

import org.jcrpg.world.time.Time;

public class Engine implements Runnable {

	boolean exit = false;
	boolean pause = true;
	Time worldMeanTime = null;
	
	//Engine
	
	public boolean timeChanged = false;
	
	public void run() {
		System.out.println("ENGINE STARTED");
		while (!exit)
		{
			try{Thread.sleep(10);}catch (Exception ex){}
			if (!pause) {
				worldMeanTime.tick(10);
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

	public void setPause(boolean pause) {
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

	

}
