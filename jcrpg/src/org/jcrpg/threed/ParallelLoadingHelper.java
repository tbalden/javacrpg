/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.threed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.threed.scene.RenderedCube;

public class ParallelLoadingHelper {
	/**
	 * continuous rendering's running threads array. should contain 1 at normal load.
	 */
	public ArrayList<RenderedAreaThread> runningThreads = new ArrayList<RenderedAreaThread>();

	private boolean loadRenderedAreaParallel = false;
	
	public HashSet<RenderedCube>[] areaResult = null;
	
	private HashMap<Long, RenderedCube> hmCurrentCubesForSafeRender = new HashMap<Long, RenderedCube>();

	
	@SuppressWarnings("unchecked")
	public void backupCurrentCubesForSafeRender(HashMap<Long, RenderedCube> hmCurrentCubes)
	{
		synchronized (mutex) {
			hmCurrentCubesForSafeRender = (HashMap<Long, RenderedCube>)hmCurrentCubes.clone();
		}
	}
	
	public HashMap<Long, RenderedCube> getBackupCurrentCubes()
	{
		synchronized (mutex) {
			return hmCurrentCubesForSafeRender;
		}		
	}
	
	public static Object mutex = new Object();
	
	public boolean isParallelRenderingRunning()
	{
		synchronized (mutex) {
			return loadRenderedAreaParallel;
		}
	}
	
	public void startParallelRendering()
	{
		synchronized (mutex) {
			loadRenderedAreaParallel = true;
		}
	}
	public void endParallelRendering()
	{
		synchronized (mutex) {
			loadRenderedAreaParallel = false;
		}
	}
	
	public void haltAllRender()
	{
		for (RenderedAreaThread t:runningThreads)
		{
			t.halt = true;
			if (t.isAlive())
			{
				
				if (t.engine.renderedArea.isInProcess) 
				{
					t.engine.renderedArea.haltCurrentProcess = true;
				}
			}
		}
		endParallelRendering();
		runningThreads.clear();

	}

}
