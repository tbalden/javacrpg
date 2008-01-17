/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.world.generator.program.algorithm;

import java.util.Collection;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.generator.GenProgram;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.place.FlowGeography;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;

/**
 * Algorithm to generate flowing things from one point to another
 * @author pali
 *
 */
public class GenAlgoFlow extends GenAlgoBase {
	
	
	public static String GEN_TYPE_NAME = "Flow"; 
	/**
	 * The geography to put flowing pattern into.
	 */
	public Geography flow;
	/**
	 * Geographies where flow can start.
	 */
	public Collection<Geography> starters;
	/**
	 * Geographies where flow can end near.
	 */
	public Collection<Geography> enders;

	/**
	 * Geos where flow cannot go
	 */
	public Collection<Geography> blockers;
	
	/**
	 * the likeness that a starter geo is used to actually start a flow
	 */
	public int startLikeness = 50;
	
	public GenAlgoFlow(GenAlgoFlowParams params)
	{
		// TODO instanciated geos use
	}

	public GenAlgoFlow(Geography flow, Collection<Geography> starters, Collection<Geography> enders, Collection<Geography> blockers, int startLikeness)
	{
		this.flow = flow;
		this.starters = starters;
		this.enders = enders;
		this.blockers = blockers;
		this.startLikeness = startLikeness;
	}

	@Override
	public void runGeneration(GenProgram program) {
		WorldParams params = program.params;
		int wMag = params.magnification;
		int wX = params.sizeX;
		//int wY = params.sizeY;
		int wZ = params.sizeZ;
		int gMag = params.geoNormalSize;
		int gWX = (wX*wMag)/gMag;
		//int gWY = (wY*wMag)/gMag;
		int gWZ = (wZ*wMag)/gMag;
		for (int x=0; x<gWX; x++)
		{
			for (int z=0; z<gWZ;z++)
			{
				if (HashUtil.mixPercentage(x, 0, z)>startLikeness)
				{
					continue;
				}
				for (Geography g:starters)
				{
					if (g.getBoundaries().isInside(x*gMag, g.worldGroundLevel, z*gMag))
					{
						doFlow(program,flow,x,z);
						break;
					}
				}
			}
		}
	}
	
	protected void doFlow(GenProgram program, Geography geo, int x, int z)
	{
		System.out.println("STARTING A FLOW..."+x+" "+z);
		WorldParams params = program.params;
		World world = program.generator.world;
		//int wMag = params.magnification;
		//int wX = params.sizeX;
		//int wY = params.sizeY;
		//int wZ = params.sizeZ;
		int gMag = params.geoNormalSize;
		/*int gWX = (wX*wMag)/gMag;
		int gWY = (wY*wMag)/gMag;
		int gWZ = (wZ*wMag)/gMag;*/

		int cX = x;
		int cZ = z;
		int direction =	(HashUtil.mix(cX, 0, cZ)*4)/100;
		int backDir = direction==0?1:direction==1?0:direction==2?3:2;
		while (true)
		{
			if (HashUtil.mix(cX, 1, cZ)<10)
			{
				System.out.println("BENDING...");
				// let's take a bend
				int newDirection = (HashUtil.mix(cX, 0, cZ)*4)/100;
				if (newDirection==backDir) // cannot go back
					newDirection++;
				direction=newDirection%4;
				backDir = direction==0?1:direction==1?0:direction==2?3:2;
				System.out.println("NEW DIR = "+direction+" - back: "+backDir);
			}
			
			boolean go = false;
			int nX = cX, nZ = cZ;
			int turnCounter = 0;
			boolean endingFound = false;
			while (!go && turnCounter<=3) {
				nX = cX; nZ = cZ;
				if (direction == 0)
				{
					nX++;
				} else
				if (direction == 1)
				{
					nX--;
				} else
				if (direction == 2)
				{
					nZ++;				
				}
				if (direction == 3)
				{
					nZ--;	
				}
				nZ = world.shrinkToWorld(nZ*gMag)/gMag;
				nX = world.shrinkToWorld(nX*gMag)/gMag;
				boolean recalcNeed = false;
				for (Geography g:blockers)
				{
					if (g.getBoundaries().isInside(nX*gMag, g.worldGroundLevel, nZ*gMag))
					{
						direction+=2;
						turnCounter++;
						direction%=4;
						if (direction==backDir)
						{
							direction++;
							turnCounter++;							
						}
						recalcNeed = true;
						break;
					}
				}
				for (Geography g:starters)
				{
					if (g.getBoundaries().isInside(nX*gMag, g.worldGroundLevel, nZ*gMag))
					{
						direction+=2;
						turnCounter++;
						direction%=4;
						if (direction==backDir)
						{
							direction++;
							turnCounter++;							
						}
						recalcNeed = true;
						break;
					}
				}
				for (Geography g:enders)
				{
					if (g.isAlgorithmicallyInside(nX*gMag, g.worldGroundLevel, nZ*gMag))
					{
						endingFound = true;
						break;
					}
				}
				if (!recalcNeed) go = true; //else System.out.println("RECALC");
			}
			// set new values into old variables
			cZ = nZ;
			cX = nX;
			backDir = direction==0?1:direction==1?0:direction==2?3:2;
			// draw the flow into the flowgeo
			if (turnCounter<=3)
			{
				System.out.println("FLOWING : "+direction+" cx, cz: "+cX+" "+cZ);
				try {
					geo.getBoundaries().addCube(gMag, cX, world.getSeaLevel(gMag), cZ);
					geo.getBoundaries().addCube(gMag, cX, world.getSeaLevel(gMag)-1, cZ);
					if (geo instanceof FlowGeography)
					{	
						FlowGeography flowGeo = (FlowGeography)geo;
						// TODO direction dependent set!
						flowGeo.getWorldSizeFlowDirections().setCubeFlowDirection(cX, world.getSeaLevel(gMag), cZ, J3DCore.NORTH, true);
						flowGeo.getWorldSizeFlowDirections().setCubeFlowDirection(cX, world.getSeaLevel(gMag), cZ, J3DCore.WEST, true);
						flowGeo.getWorldSizeFlowDirections().setCubeFlowDirection(cX, world.getSeaLevel(gMag)-1, cZ, J3DCore.NORTH, true);
						flowGeo.getWorldSizeFlowDirections().setCubeFlowDirection(cX, world.getSeaLevel(gMag)-1, cZ, J3DCore.WEST, true);
					}
				} catch (Exception ex)
				{
					ex.printStackTrace();
					break;
				}
			}
			if (turnCounter>3 || endingFound) break;
				
		}
	}
	
	

}
