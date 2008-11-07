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
	public static final byte OPEN_PART = 4;
	public static final byte DOOR_HORI = 8;
	public static final byte DOOR_VERT = 16;
	public static final byte GROUND_TYPE_1 = 32;
	public static final byte GROUND_TYPE_2 = 64;
	public static final byte GROUND_TYPE_3 = (byte)128;

	public static final byte GROUND_TYPE_CLEAR = 255-128-64-32;

	public static final byte WALL_HORI_NEG = (byte)254;
	public static final byte WALL_VERT_NEG = (byte)253;
	public static final byte OPEN_PART_NEG = (byte)251;
	
	public static byte[][] getLabyrinth(int seed, int sizeX, int sizeY, boolean allClosed)
	{
		if (sizeX<0 || sizeY<0) return new byte[10][10];
		byte[][] r = new byte[sizeX][sizeY];
		
		recoursiveDivision(seed, 0,(int)(sizeX/Math.exp(sizeX/50f)), r, 0, 0, sizeX, sizeY,allClosed);
		
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
	public static void recoursiveDivision(int seed, int level, int maxLevel, byte[][] map, int origoX, int origoY, int endX, int endY, boolean allClosed)
	{
		int sizeX = endX-origoX;
		int sizeY = endY-origoY;
		if (sizeX<3 || sizeY<3) return;
		int hash1 = HashUtil.mix(seed, origoX+origoY, sizeX+sizeY);
		
		boolean open = false;
		if (hash1%3==1 && !allClosed)
		{
			open = true;
		}
		
		for (int x=origoX; x<endX; x++)
		{
			for (int y=origoY; y<endY; y++)
			{
				byte ground = GROUND_TYPE_1;
				if (sizeX>5||sizeY>5)
				{
					ground = GROUND_TYPE_3;
				} 
				if (sizeX>4||sizeY>4)
				{
					ground = GROUND_TYPE_2;
				} 
				map[x][y] = (byte)(map[x][y] & GROUND_TYPE_CLEAR); // clearing prev ground type
				if (open)
				{
					map[x][y] = (byte)(map[x][y] | OPEN_PART | ground);
				} else
				{
					map[x][y] = (byte)(map[x][y] & OPEN_PART_NEG | ground);
				}
			}
		}
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
		boolean door = true;
		if (gapOnWallWest)
		{
			int gapPosX = origoX+hash1%divX;
			int gapPosY = origoY+divY;
			//System.out.println("GapW:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, true,door);
		}
		if (gapOnWallEast)
		{
			int gapPosX = origoX+divX+hash1%(sizeX-divX);
			int gapPosY = origoY+divY;
			//System.out.println("GapE:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, true,door);
		}
		if (gapOnWallNorth)
		{
			int gapPosX = origoX+divX;
			int gapPosY = origoY+hash2%divY;
			//System.out.println("GapN:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, false,door);
		}
		if (gapOnWallSouth)
		{
			int gapPosX = origoX+divX;
			int gapPosY = origoY+divY+hash2%(sizeY-divY);
			//System.out.println("GapS:  X: "+gapPosX+" Y: "+gapPosY);
			removeWall(map, gapPosX, gapPosY, false,door);
		}
		if (level<maxLevel)
		{
			recoursiveDivision(seed, level+1, maxLevel, map, origoX, origoY, origoX+divX, origoY+divY,allClosed);
			recoursiveDivision(seed, level+1, maxLevel, map, origoX+divX, origoY, endX, origoY+divY,allClosed);
			recoursiveDivision(seed, level+1, maxLevel, map, origoX+divX, origoY+divY, endX, endY,allClosed);
			recoursiveDivision(seed, level+1, maxLevel, map, origoX, origoY+divY, origoX+divX, endY,allClosed);
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

	public static void removeWall(byte[][] map, int origoX, int origoY, boolean horizontal, boolean door)
	{
			if (horizontal)
			{
				map[origoX][origoY] = (byte)(map[origoX][origoY] & WALL_HORI_NEG);
				if (door) map[origoX][origoY] = (byte)(map[origoX][origoY] | DOOR_HORI);
			} else
			{
				map[origoX][origoY] = (byte)(map[origoX][origoY] & WALL_VERT_NEG);
				if (door) map[origoX][origoY] = (byte)(map[origoX][origoY] | DOOR_VERT);
			}
	}
	
	public static void main(String[] args)
	{
		long t0 = System.currentTimeMillis(); 
		byte[][] b = getLabyrinth(1, 20, 20, false);
		System.out.println(System.currentTimeMillis()-t0);
		for (int y=b[0].length-1; y>=0; y--)
		{
			for (int x=0; x<b.length; x++)
			{
				boolean horiDoor = (b[x][y]&DOOR_HORI)>0;
				boolean vertDoor = (b[x][y]&DOOR_VERT)>0;
				boolean horiWall = (b[x][y]&WALL_HORI)>0;
				boolean vertWall = (b[x][y]&WALL_VERT)>0;
				boolean openArea = (b[x][y]&OPEN_PART)>0;

				if (vertDoor) System.out.print("I");
				else
				if (vertWall) System.out.print("|");
				else
				if (horiWall || horiDoor)
				{
					if (openArea)
						System.out.print("X");
					else
						System.out.print("_");
				}
				else
				{
					if (openArea)
					System.out.print("o");
					else
					System.out.print(" ");
				}


				if (horiDoor) System.out.print("=");
				else
				if (horiWall) System.out.print("_");
				else
				{
					if (openArea)
						System.out.print("o");
						else
						System.out.print(" ");
				}

				//System.out.print(((b[x][y]&WALL_HORI)>0)?(((b[x][y]&WALL_VERT)>0)?"T\"":"\"\""):(((b[x][y]&WALL_VERT)>0)?(((b[x][y]&OPEN_PART)>0)?(horiDoor?"|=":"| "):(horiDoor?"|#":"|X")):(((b[x][y]&OPEN_PART)>0)?(vertDoor?(horiDoor?"I=":"I "):"  "):(vertDoor?(horiDoor?"I#":"IX"):"XX"))));
			}
			System.out.println("XX");
			
		}
	}
}
