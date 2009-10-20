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

import java.util.HashSet;
import java.util.logging.Logger;

import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.standing.J3DStandingEngine;
import org.jcrpg.world.place.World;

public class RenderedAreaThread extends Thread
{
	public Logger logger = Logger.getLogger(RenderedAreaThread.class.getName());

	J3DStandingEngine engine = null;
	World world;
	int x,y,z;
	int rX,rY,rZ;
	public HashSet<RenderedCube>[] areaResult = null;
	

	public RenderedAreaThread(J3DStandingEngine engine, World world, int rX, int rY, int rZ, int x, int y, int z)
	{
		if (J3DCore.LOGGING()) logger.fine("NEW RENDER: "+x+" "+y+" "+z);
		if (J3DCore.LOGGING()) logger.finest("LAST RENDER: "+engine.lastRenderX+" "+engine.lastRenderY+" "+engine.lastRenderZ);
		this.engine = engine;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rX = rX;
		this.rY = rY;
		this.rZ = rZ;
		engine.backupCurrentCubesForSafeRender();
		if (!engine.parallelLoadingHelper.isParallelRenderingRunning())
		{
			engine.uiBase.hud.sr.setVisibility(true, "LOAD");
		}
		setPriority(1);
	}

	@Override
	public void run() {
		if (engine.parallelLoadingHelper.isParallelRenderingRunning()) return;
		engine.parallelLoadingHelper.startParallelRendering();
		engine.parallelLoadingHelper.areaResult = null;			
		areaResult = engine.render(rX,rY,rZ,
				x, y, z, false,true);
		if (!halt)
		{
			engine.parallelLoadingHelper.areaResult = areaResult;
			areaResult = null;
		} else
		{
			engine.parallelLoadingHelper.endParallelRendering();
			halt = false;
		}
	}
	
	public boolean halt = false;
}

