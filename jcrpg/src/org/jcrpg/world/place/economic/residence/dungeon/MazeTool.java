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
package org.jcrpg.world.place.economic.residence.dungeon;

import org.jcrpg.util.HashUtil;

/**
 * 
 * @author illes
 */
public class MazeTool {

	
	public static final byte GAP = 0;
	public static final byte WALL_HORI = 1;
	public static final byte WALL_VERT = 2;
	public static final byte WALL_HORI_NEG = (byte)254;
	public static final byte WALL_VERT_NEG = (byte)253;
	
	public static byte[][] getLabyrinth(int seed, int sizeX, int sizeY, int[][][] entranceGaps)
	{
		
		byte[][] r = new byte[sizeX][sizeY];
		
		recoursiveDivision(seed, 0,(int)(sizeX/Math.exp(sizeX/50f)), r, 0, 0, sizeX, sizeY);
		
		return r;
		
	}

	/**
	 * http://en.wikipedia.org/wiki/Maze_generation_algorithm - recursive division algo.
	 * @param seed
	 * @param level
	 * @param maxLevel
	 * @param map
	 * @param origoX
	 * @param origoY
	 * @param endX
	 * @param endY
	 */
	public static void recoursiveDivision(int seed, int level, int maxLevel, byte[][] map, int origoX, int origoY, int endX, int endY)
	{
		int sizeX = endX-origoX;
		int sizeY = endY-origoY;
		if (sizeX<3 || sizeY<3) return;
		int hash1 = HashUtil.mix(seed, origoX+origoY, sizeX+sizeY);
		int hash2 = HashUtil.mix(seed+1, origoX+origoY, sizeX+sizeY);
		int divX = Math.min(Math.max(2,hash1%sizeX),sizeX-2);
		int divY = Math.min(Math.max(2,hash2%sizeY),sizeY-2);
		//System.out.println("L: "+level+" DIVX: "+divX+" DIVY: "+divY);
		

		drawWallInLine(map, origoX+divX, origoY, sizeY, false);
		drawWallInLine(map, origoX, origoY+divY, sizeX, true);

		int nonGapWall = hash1%4;
		boolean gapOnWallNorth = nonGapWall!=0;
		boolean gapOnWallSouth = nonGapWall!=1;
		boolean gapOnWallWest = nonGapWall!=2;
		boolean gapOnWallEast = nonGapWall!=3;
		int gapCount = 0;
		if (gapOnWallWest)
		{
			int gapPosX = origoX+hash1%divX;
			int gapPosY = origoY+divY;
			//System.out.println("GapW:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, true);
		}
		if (gapOnWallEast)
		{
			int gapPosX = origoX+divX+hash1%(sizeX-divX);
			int gapPosY = origoY+divY;
			//System.out.println("GapE:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, true);
		}
		if (gapOnWallNorth)
		{
			int gapPosX = origoX+divX;
			int gapPosY = origoY+hash2%divY;
			//System.out.println("GapN:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, false);
		}
		if (gapOnWallSouth)
		{
			int gapPosX = origoX+divX;
			int gapPosY = origoY+divY+hash2%(sizeY-divY);
			//System.out.println("GapS:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, false);
		}
		if (level<maxLevel)
		{
			recoursiveDivision(seed, level+1, maxLevel, map, origoX, origoY, origoX+divX, origoY+divY);
			recoursiveDivision(seed, level+1, maxLevel, map, origoX+divX, origoY, endX, origoY+divY);
			recoursiveDivision(seed, level+1, maxLevel, map, origoX+divX, origoY+divY, endX, endY);
			recoursiveDivision(seed, level+1, maxLevel, map, origoX, origoY+divY, origoX+divX, endY);
		}
		
	}
	public static void drawWallInLine(byte[][] map, int origoX, int origoY, int size, boolean horizontal)
	{
		for (int i=0; i<size; i++)
		{
			if (horizontal)
			{
				map[origoX+i][origoY] = (byte)(map[origoX+i][origoY] | WALL_HORI);
			} else
			{
				map[origoX][origoY+i] = (byte)(map[origoX][origoY+i] | WALL_VERT);
			}
		}
	}

	public static void removeWall(byte[][] map, int origoX, int origoY, boolean horizontal)
	{
			if (horizontal)
			{
				map[origoX][origoY] = (byte)(map[origoX][origoY] & WALL_HORI_NEG);
			} else
			{
				map[origoX][origoY] = (byte)(map[origoX][origoY] & WALL_VERT_NEG);
			}
	}
	
	public static void main(String[] args)
	{
		long t0 = System.currentTimeMillis(); 
		byte[][] b = getLabyrinth(1, 20, 20, null);
		System.out.println(System.currentTimeMillis()-t0);
		for (int y=0; y<b[0].length; y++)
		{
			for (int x=0; x<b.length; x++)
			{
				System.out.print(((b[x][y]&WALL_HORI)>0)?(((b[x][y]&WALL_VERT)>0)?"T\"":"\"\""):(((b[x][y]&WALL_VERT)>0)?"| ":"  "));
			}
			System.out.println("X");
			
		}
	}
}
