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

package org.jcrpg.space;

import java.util.ArrayList;
import java.util.Set;

import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.physical.Tumbling;
import org.jcrpg.world.place.World;

public class CubeInterpreter 
{
	
	
	public class MovementInterpretationResult
	{
		public boolean possible = false;
		public ArrayList<Class<? extends SkillBase>> skillNeeded = new ArrayList<Class<? extends SkillBase>>();
		
		/**
		 * The difficulty to make the given movement
		 */
		public int difficulty = 0;
		/**
		 * means player cannot stand on that ground, more movement check needed
		 */
		public boolean meansFalling = false;
		/**
		 * retult place is steep
		 */
		public boolean meansOnSteepCube = false;
		/**
		 * on steeps you might get to up or down too aside the normal displacement wanted
		 */
		public int additionalVerticalDelta = 0;
		
		public int worldX, worldY, worldZ, relX, relY, relZ;
		
	}
	
	World w;
	int centerX, centerY, centerZ;
	int relX, relY, relZ;
	
	public CubeInterpreter(World w, int worldX, int worldY, int worldZ, int relX, int relY, int relZ)
	{
		this.w = w;
		centerX = worldX;
		centerY = worldY;
		centerZ = worldZ;
		this.relX = relX;
		this.relY = relY;
		this.relZ = relZ;
	}
	
	private boolean isDirectionBlockedInCube(Cube cube, int direction)
	{
		if (cube==null) return false;
		
		Side[] sides = cube.getSide(direction);
		if (sides==null) return false;
		
		return hasSideOfInstance(sides, J3DCore.notPassable);
	}
	private boolean isDirectionBlockedToCube(Cube cube, int direction)
	{
		if (cube==null) return false;
		
		Side[] sides = cube.getSide(J3DCore.oppositeDirections.get(direction));
		if (sides==null) return false;

		return hasSideOfInstance(sides, J3DCore.notPassable);
	}
	
	private void prepareResult(Cube cube, int[] coords, int[] relCoords, MovementInterpretationResult result)
	{
		if (cube==null)
		{
			result.possible = true;
			result.meansFalling = true;
		} else
		if (hasSideOfInstance(cube.getSide(J3DCore.BOTTOM), J3DCore.notWalkable))
		{
			result.possible = false;
		} else
		{
			result.possible = true;
			Integer[] nextCubeSteepDirections = hasSideOfInstanceInAnyDir(cube, J3DCore.climbers);
			if (nextCubeSteepDirections != null) {
				result.meansOnSteepCube = true;
			} else {
				result.meansOnSteepCube = false;
			}
		}
		result.worldX = coords[0];
		result.worldY = coords[1]+result.additionalVerticalDelta;
		result.worldZ = coords[2];
		result.relX = relCoords[0];
		result.relY = relCoords[1]+result.additionalVerticalDelta;
		result.relZ = relCoords[2];
		
	}

	private Integer directionOfClimbingInCubeInDirection(Cube cube, int direction)
	{
		if (cube==null) return null;
		
		Integer[] currentCubeSteepDirections = hasSideOfInstanceInAnyDir(cube,
				J3DCore.climbers);
		if (currentCubeSteepDirections != null)
		{
			for (int steepDir : currentCubeSteepDirections) {
				if (steepDir == J3DCore.oppositeDirections.get(
						new Integer(direction)).intValue()) {
					return J3DCore.TOP;
				}
				if (steepDir == direction) {
					return J3DCore.BOTTOM;
				}
			}
		}
		return null;
	}
	
	/**
	 * Tells if any of a set of sides is of a set of sideSubTypes.
	 * 
	 * @param sides
	 * @param classNames
	 * @return
	 */
	public boolean hasSideOfInstance(Side[] sides,
			Set<Class<? extends SideSubType>> classNames) {
		if (sides != null)
			for (int i = 0; i < sides.length; i++) {
				if (sides[i] != null) {
					// Jcrpg.LOGGER.info("SIDE SUBTYPE: "+sides[i].subtype.
					// getClass().getCanonicalName());

					if (classNames.contains(sides[i].subtype.getClass())) {
						return true;
					}
				}
			}
		return false;

	}

	/**
	 * Tells if the cube has any side of a set of sideSubTypes.
	 * 
	 * @param c
	 * @param classNames
	 * @return
	 */
	public Integer[] hasSideOfInstanceInAnyDir(Cube c,
			Set<Class<? extends SideSubType>> classNames) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int j = 0; j < c.sides.length; j++) {
			Side[] sides = c.sides[j];
			if (sides != null)
				for (int i = 0; i < sides.length; i++) {
					if (sides[i] != null) {
						// Jcrpg.LOGGER.info("SIDE SUBTYPE: "+sides[i].subtype.
						// getClass().getCanonicalName());
						if (classNames.contains(sides[i].subtype.getClass())) {
							list.add(j);
						}
					}
				}
		}
		if (list.size() == 0)
			return null;
		return (Integer[]) list.toArray(new Integer[0]);

	}

	boolean DEBUG = false;
	
	public MovementInterpretationResult interpret(int direction)
	{
		MovementInterpretationResult result = new MovementInterpretationResult();

		int[] center = new int[]{centerX, centerY, centerZ};
		int[] posInDirection = J3DCore.calcMovement(center, direction, true);
	
		int[] relCenter = new int[]{relX, relY, relZ};
		int[] relPosInDirection = J3DCore.calcMovement(relCenter, direction, false);
		
		Cube cubeInCenter = w.getCube(-1, center[0], center[1], center[2], false);
		if (DEBUG) System.out.println("cubeInCenter: "+cubeInCenter);
		
		Cube cubeInDirection = w.getCube(-1, posInDirection[0], posInDirection[1], posInDirection[2], false);
		if (DEBUG) System.out.println("cubeInDirection: "+cubeInDirection);
		
		Cube cubeInDirectionBelow = null;
		Cube cubeInDirectionAbove = null;
		if (direction!=J3DCore.TOP)
		{
			cubeInDirectionAbove = w.getCube(-1, posInDirection[0], posInDirection[1]+1, posInDirection[2], false);
		}
		if (direction!=J3DCore.BOTTOM)
		{
			cubeInDirectionBelow = w.getCube(-1, posInDirection[0], posInDirection[1]-1, posInDirection[2], false);
		}
		
		if (direction==J3DCore.TOP)
		{
			// check flying capability, or ClimbingVertical 
			
		} else
		if (direction==J3DCore.BOTTOM)
		{
			// check flying capability, or ClimbingVertical 
			boolean blockedIn = isDirectionBlockedInCube(cubeInCenter, direction);
			boolean blockedTo = isDirectionBlockedToCube(cubeInDirection, direction);
			if (!blockedIn && !blockedTo)
			{
				prepareResult(cubeInDirection, posInDirection, relPosInDirection, result);
			} else
			{
				if (DEBUG) System.out.println("Blocked simple downward.");
			}
		} else
		{
			boolean blockedIn = isDirectionBlockedInCube(cubeInCenter, direction);
			boolean blockedTo = isDirectionBlockedToCube(cubeInDirection, direction);
			prepareResult(cubeInDirection, posInDirection, relPosInDirection, result);
			if (!result.possible || blockedIn || blockedTo)
			{
				if (DEBUG) System.out.println("Blocked simple forward.");
				Integer steepLeadsTo = directionOfClimbingInCubeInDirection(cubeInCenter, direction);
				if (steepLeadsTo!=null && steepLeadsTo==J3DCore.TOP)
				{
					if (DEBUG) System.out.println("Checking for steep climb...");
					blockedTo = isDirectionBlockedToCube(cubeInDirectionAbove, direction);
					if (DEBUG) System.out.println("cubeInDirectionAbove: "+cubeInDirectionAbove);
					if (!blockedTo)
					{
						result.additionalVerticalDelta = +1;
						// way is not blocked climbing on steep in direction to a level higher..
						prepareResult(cubeInDirectionAbove, posInDirection, relPosInDirection, result);
					} else
					{
						result.possible = false;
					}
				} else
				{	// No steep leading up, check climbing possible...
					
					if (DEBUG) System.out.println("## Checking for climbing with skill...");
					// check if climbing up is possible from original cube.
					blockedIn = isDirectionBlockedInCube(cubeInCenter, J3DCore.TOP);
					// check if cube above is not blocked moving into it.
					blockedTo = isDirectionBlockedToCube(cubeInDirectionAbove, direction);
					//System.out.println("cubeInCenter: "+cubeInCenter);
					//System.out.println("cubeInDirection: "+cubeInDirection);
					if (DEBUG) System.out.println("cubeInDirectionAbove: "+cubeInDirectionAbove);
					if (!blockedIn && !blockedTo)
					{
						result.additionalVerticalDelta = +1;
						prepareResult(cubeInDirectionAbove, posInDirection, relPosInDirection, result);
						result.skillNeeded.add(org.jcrpg.world.ai.abs.skill.physical.Climbing.class);
						result.skillNeeded.add(Tumbling.class);
						// TODO difficulty counting is too simple here..
						result.difficulty = 30;
					} else
					result.possible = false;
				}
			}
		}
		return result;
	}

}
