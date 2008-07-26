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

package org.jcrpg.world.place;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.dialect.Dialect;
import org.jcrpg.world.ai.dialect.DialectTool;
import org.jcrpg.world.ai.flora.FloraCube;
import org.jcrpg.world.ai.flora.FloraDescription;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.generator.GeneratedPartRuleSet;
import org.jcrpg.world.place.water.River;
import org.jcrpg.world.time.Time;

public class Geography extends Place implements Surface {
	
		
	public GeneratedPartRuleSet ruleSet = new GeneratedPartRuleSet(this.getClass().getSimpleName());
	
	
	/**
	 * Population height offset.
	 */
	public int populationPlus = 0;
	
	/**
	 * Color for the maps.
	 */
	public byte[] colorBytes = new byte[] {(byte)100,(byte)145,(byte)100};
	
	public int worldGroundLevel, worldHeight, blockSize, worldRealHeight;
	
	/**
	 * Determines if this geography should be used in the first place for get outside geoheight call.
	 */
	public boolean returnsGeoOutsideHeight = true;

	public static final String TYPE_GEO = "GEO";
	public static final SideSubType SUBTYPE_STEEP = new Climbing(TYPE_GEO+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_ROCK_BLOCK = new NotPassable(TYPE_GEO+"_GROUND_ROCK");
	public static final SideSubType SUBTYPE_ROCK_BLOCK_VISIBLE = new NotPassable(TYPE_GEO+"_GROUND_ROCK_VISIBLE");
	public static final SideSubType SUBTYPE_ROCK_SIDE = new NotPassable(TYPE_GEO+"_GROUND_ROCK_SIDE");
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_GEO+"_GROUND");
	public static final SideSubType SUBTYPE_GROUND_HELPER = new NotPassable(TYPE_GEO+"_GROUND_HELPER");
	public static final SideSubType SUBTYPE_INTERSECT = new Climbing(TYPE_GEO+"_GROUND_INTERSECT");
	public static final SideSubType SUBTYPE_CORNER = new Climbing(TYPE_GEO+"_GROUND_CORNER");
	public static final SideSubType SUBTYPE_INTERSECT_EMPTY = new Climbing(TYPE_GEO+"_GROUND_INTERSECT_EMPTY");
	public static final SideSubType SUBTYPE_INTERSECT_BLOCK = new GroundSubType(TYPE_GEO+"_GROUND_INTERSECT_BLOCK");
	public static final SideSubType SUBTYPE_ROCK_DOWNSIDE = new GroundSubType(TYPE_GEO+"_ROCK_DOWNSIDE");

	
	static Side[] ROCK_VISIBLE = {new Side(TYPE_GEO,SUBTYPE_ROCK_BLOCK_VISIBLE)};
	static Side[] ROCK = {new Side(TYPE_GEO,SUBTYPE_ROCK_BLOCK)};
	public static Side[] GROUND = {new Side(TYPE_GEO,SUBTYPE_GROUND)};
	public static Side[] GROUND_HELPER = {new Side(TYPE_GEO,SUBTYPE_GROUND_HELPER)};
	static Side[] STEEP = {new Side(TYPE_GEO,SUBTYPE_STEEP)};
	static Side[] INTERSECT = {new Side(TYPE_GEO,SUBTYPE_INTERSECT)};
	static Side[] CORNER = {new Side(TYPE_GEO,SUBTYPE_CORNER)};
	public static Side[] I_EMPTY = {new Side(TYPE_GEO,SUBTYPE_INTERSECT_EMPTY)};
	public static Side[] BLOCK = {new Side(TYPE_GEO,SUBTYPE_INTERSECT_BLOCK)};
	public static Side[] INTERNAL_ROCK_SIDE = null;//{new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK_SIDE)};
	static Side[] ROCK_DOWNSIDE = {new Side(TYPE_GEO,SUBTYPE_ROCK_DOWNSIDE)};
	
	static Side[][] GEO_ROCK = new Side[][] { null, null, null,null,null,ROCK };
	static Side[][] GEO_ROCK_VISIBLE = new Side[][] { null, null, null,null,null,ROCK_VISIBLE };
	static Side[][] GEO_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] GEO_INTERSECT_NORTH = new Side[][] { INTERSECT, I_EMPTY, I_EMPTY,I_EMPTY,BLOCK,GROUND };
	static Side[][] GEO_INTERSECT_EAST = new Side[][] { I_EMPTY, INTERSECT, I_EMPTY,I_EMPTY,BLOCK,GROUND };
	static Side[][] GEO_INTERSECT_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, INTERSECT,I_EMPTY,BLOCK,GROUND };
	static Side[][] GEO_INTERSECT_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,INTERSECT,BLOCK,GROUND };
	static Side[][] GEO_CORNER_NORTH = new Side[][] { CORNER, I_EMPTY, I_EMPTY,I_EMPTY,null,GROUND};
	static Side[][] GEO_CORNER_EAST = new Side[][] { I_EMPTY, CORNER, I_EMPTY,I_EMPTY,null,GROUND};
	static Side[][] GEO_CORNER_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, CORNER,I_EMPTY,null,GROUND};
	static Side[][] GEO_CORNER_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,CORNER,null,GROUND };
	static Side[][] GEO_STEEP_NORTH = new Side[][] { STEEP, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,BLOCK,GROUND };
	static Side[][] GEO_STEEP_EAST = new Side[][] { I_EMPTY, STEEP, I_EMPTY,INTERNAL_ROCK_SIDE,BLOCK,GROUND };
	static Side[][] GEO_STEEP_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STEEP,I_EMPTY,BLOCK,GROUND };
	static Side[][] GEO_STEEP_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STEEP,BLOCK,GROUND };

/*	static Side[][] GEO_INTERSECT_NORTH = new Side[][] { INTERSECT, I_EMPTY, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_INTERSECT_EAST = new Side[][] { I_EMPTY, INTERSECT, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_INTERSECT_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, INTERSECT,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_INTERSECT_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,INTERSECT,BLOCK,BLOCK };
	static Side[][] GEO_CORNER_NORTH = new Side[][] { CORNER, I_EMPTY, I_EMPTY,I_EMPTY,null,null};
	static Side[][] GEO_CORNER_EAST = new Side[][] { I_EMPTY, CORNER, I_EMPTY,I_EMPTY,null,null};
	static Side[][] GEO_CORNER_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, CORNER,I_EMPTY,null,null};
	static Side[][] GEO_CORNER_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,CORNER,null,null };
	static Side[][] GEO_STEEP_NORTH = new Side[][] { STEEP, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_STEEP_EAST = new Side[][] { I_EMPTY, STEEP, I_EMPTY,INTERNAL_ROCK_SIDE,BLOCK,BLOCK };
	static Side[][] GEO_STEEP_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STEEP,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_STEEP_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STEEP,BLOCK,BLOCK };

*/
	static Side[][] GEO_ROCK_FARVIEW = new Side[][] { null, null, null,null,null,ROCK_DOWNSIDE };
	
	
	public static final int K_UNDEFINED = -2, K_EMPTY = -1, K_STEEP_NORTH = 0, K_STEEP_SOUTH = 2, K_STEEP_EAST = 1, K_STEEP_WEST = 3;
	public static final int K_ROCK_BLOCK = 4, K_NORMAL_GROUND = 5;
	public static final int K_INTERSECT_NORTH = 6, K_INTERSECT_EAST = 7, K_INTERSECT_SOUTH = 8, K_INTERSECT_WEST = 9;
	public static final int K_CORNER_SOUTH = 10, K_CORNER_NORTH = 11, K_CORNER_WEST = 12, K_CORNER_EAST = 13;

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int NORTH_EAST = 4, SOUTH_EAST = 5, SOUTH_WEST = 6, NORTH_WEST = 7;

	public static final int C_NORMAL = 8, C_HALF = 9;
	public static final int P_EQUAL = 0, P_GREATER = 1, P_LESSER = 2;
	public static final int P_GE = 3, P_LE = 4;


	
	public static HashMap<Long, float[]> quickCubeKindCache = new HashMap<Long, float[]>();
	
	public static HashMap<Integer, Cube> hmKindCube = new HashMap<Integer, Cube>();
	public static HashMap<Integer, Cube> hmKindCube_FARVIEW = new HashMap<Integer, Cube>();
	static {
		hmKindCube.put(K_EMPTY, null);
		hmKindCube.put(K_NORMAL_GROUND, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_ROCK_BLOCK, new Cube(null,0,GEO_ROCK,0,0,0));
		hmKindCube.put(K_STEEP_NORTH, new Cube(null,GEO_STEEP_NORTH,0,0,0,0));
		hmKindCube.put(K_STEEP_EAST, new Cube(null,GEO_STEEP_EAST,0,0,0,1));
		hmKindCube.put(K_STEEP_SOUTH, new Cube(null,GEO_STEEP_SOUTH,0,0,0,2));
		hmKindCube.put(K_STEEP_WEST, new Cube(null,GEO_STEEP_WEST,0,0,0,3));

		/*hmKindCube.put(K_STEEP_NORTH, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_STEEP_EAST, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_STEEP_SOUTH, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_STEEP_WEST, new Cube(null,GEO_GROUND,0,0,0));*/
		
		hmKindCube.put(K_INTERSECT_SOUTH, new Cube(null,GEO_INTERSECT_SOUTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_NORTH, new Cube(null,GEO_INTERSECT_NORTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_WEST, new Cube(null,GEO_INTERSECT_WEST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_EAST, new Cube(null,GEO_INTERSECT_EAST,0,0,0,J3DCore.BOTTOM));

		hmKindCube.put(K_CORNER_SOUTH, new Cube(null,GEO_CORNER_SOUTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_NORTH, new Cube(null,GEO_CORNER_NORTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_WEST, new Cube(null,GEO_CORNER_WEST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_EAST, new Cube(null,GEO_CORNER_EAST,0,0,0,J3DCore.BOTTOM));

		/*hmKindCube.put(K_INTERSECT_SOUTH, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_INTERSECT_NORTH, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_INTERSECT_WEST, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_INTERSECT_EAST, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_CORNER_SOUTH, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_CORNER_NORTH, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_CORNER_WEST, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_CORNER_EAST, new Cube(null,GEO_GROUND,0,0,0));*/
		
		//hmKindCube_FARVIEW.put(K_NORMAL_GROUND, new Cube(null,GEO_ROCK_FARVIEW,0,0,0));
		//hmKindCube.put(K_ROCK_BLOCK, new Cube(null,0,EMPTY,0,0,0));
		/*hmKindCube_FARVIEW.put(K_STEEP_NORTH, new Cube(null,GEO_CORNER_NORTH,0,0,0,0));
		hmKindCube_FARVIEW.put(K_STEEP_EAST, new Cube(null,GEO_CORNER_EAST,0,0,0,0));
		hmKindCube_FARVIEW.put(K_STEEP_SOUTH, new Cube(null,GEO_CORNER_SOUTH,0,0,0,0));
		hmKindCube_FARVIEW.put(K_STEEP_WEST, new Cube(null,GEO_CORNER_WEST,0,0,0,0));
		hmKindCube_FARVIEW.put(K_INTERSECT_SOUTH, new Cube(null,GEO_INTERSECT_SOUTH,0,0,0,0));
		hmKindCube_FARVIEW.put(K_INTERSECT_NORTH, new Cube(null,GEO_INTERSECT_NORTH,0,0,0,0));
		hmKindCube_FARVIEW.put(K_INTERSECT_WEST, new Cube(null,GEO_INTERSECT_WEST,0,0,0,0));
		hmKindCube_FARVIEW.put(K_INTERSECT_EAST, new Cube(null,GEO_INTERSECT_EAST,0,0,0,0));*/

	}
	public Geography(String id, Place parent, PlaceLocator loc)
	{
		super(id,parent, loc);
		blockSize = 40;
	}
	public Geography(String id, Place parent,PlaceLocator loc,int worldGroundLevel, int worldHeight, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id,parent, loc);
		worldRealHeight = worldHeight - worldGroundLevel;
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.worldRealHeight = worldHeight - worldGroundLevel;
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("MOUNTAIN SIZE = "+worldRealHeight+ " --- "+worldGroundLevel/magnification+" - "+ origoY);
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		//this.groundLevel = groundLevel;
		this.worldGroundLevel=worldGroundLevel;
		this.worldHeight = worldHeight;
		this.blockSize = magnification;
		if (fillBoundaries)
			setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
		else
			setBoundaries(new WorldSizeBitBoundaries(magnification,(World)parent));
	}

	@Override
	public boolean generateModel() {
		return false;
	}

	@Override
	public boolean loadModelFromFile() {
		return false;
	}

	
	
	public Cube getFloraCube(int worldX, int worldY, int worldZ, CubeClimateConditions conditions, Time time, boolean onSteep)
	{
		World w = (World)getRoot();
		Cube floraCube = null;
		FloraCube fC = w.getFloraContainer().getFlora(worldX,worldY,worldZ,this.getClass(), conditions, time, onSteep);
		for (FloraDescription fd : fC.descriptions)
		{
			if (floraCube==null) {
				floraCube = fd.instanciateCube(w, worldX,worldY,worldZ);				
			}
			else {
				floraCube = new Cube(floraCube,fd.cubicForm,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
			}
		}
		//if (floraCube!=null) floraCube
		return floraCube;
	}
	
	/**
	 * Get the Cube object from hashmaps that represents a kind depending on farView.
	 * @param kind
	 * @param farView
	 * @return The Cube.
	 */
	public Cube getCubeObject(int kind, boolean farView)
	{
		Cube c;
		if (farView)
		{
			c = hmKindCube_FARVIEW.get(kind);
			if (c==null) c = hmKindCube.get(kind);
		} else
		{
			c = hmKindCube.get(kind);
		}
		if (c!=null)
			c.needsFurtherMerge = true; // this is normal geography cube - if economy is using Geography as base class this will help economic cube to render further
			// if not a real economic cube (world.getCube will interpret this.
		return c;
	}

	
	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		float[] kind = getCubeKind(key, worldX, worldY, worldZ, farView);
		Cube c = getCubeObject((int)kind[4], farView);
		if (c==null) return null;
		c = c.copy(this);
		c.x = worldX;
		c.y = worldY;
		c.z = worldZ;
		c.cornerHeights = kind;
		c.middleHeight = (kind[0]+kind[1]+kind[2]+kind[3])/4f;
		c.geoCubeKind = (int)kind[4];
		return c;
	}
	
	/**
	 * Tells the geo hashed generic size on a given block - usable for example Mountain - Cave relation.
	 * @param blockSize
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return The X and Z size of the used area in the block.
	 */
	public int[] getBlocksGenericSize(int blockSize, int worldX, int worldZ)
	{
		int realSizeX = blockSize-1 - (int)( (getGeographyHashPercentage(worldX/blockSize, 0, worldZ/blockSize)/50d)*(blockSize/2) );
		int realSizeZ = blockSize-1 - (int)( (getGeographyHashPercentage(worldZ/blockSize, 0, worldX/blockSize)/50d)*(blockSize/2) );
		realSizeX-=realSizeX%2;
		realSizeZ-=realSizeZ%2;
		return new int[]{realSizeX,realSizeZ};
	}
	
	
	/**
	 * 0 "==" , 1 ">" , 2 "<"
	 * @param Y
	 * @param directionYs
	 * @return
	 */
	public int[][] evaluate(int Y, int[] directionYs)
	{
		int[][] ret = new int[P_LE+1][C_HALF+1];
		int countEqual = 0, countGreater = 0, countLess = 0,countGreaterEq = 0, countLessEq = 0;
		int countEqual2 = 0, countGreater2 = 0, countLess2 = 0,countGreaterEq2 = 0, countLessEq2 = 0;
		for (int i=0; i<directionYs.length; i++)
		{
			if (Y==directionYs[i])
			{
				if (i<4)
				{
					countEqual++;
					ret[P_EQUAL][i] = 1;
				} else
				{
					countEqual2++;
					ret[P_EQUAL][i] = 1;
				}
			}
			if (Y>directionYs[i])
			{
				if (i<4)
				{
					countGreater++;
					ret[P_GREATER][i] = 1;
				} else
				{
					countGreater2++;
					ret[P_GREATER][i] = 1;
				}
			}
			if (Y<directionYs[i])
			{
				if (i<4)
				{
					countLess++;
					ret[P_LESSER][i] = 1;
				} else
				{
					countLess2++;
					ret[P_LESSER][i] = 1;
				}
			}
			if (Y>=directionYs[i])
			{
				if (i<4)
				{
					countGreaterEq++;
					ret[P_GE][i] = 1;
				} else
				{
					countGreaterEq2++;
					ret[P_GE][i] = 1;
				}
			}
			if (Y<=directionYs[i])
			{
				if (i<4)
				{
					countLessEq++;
					ret[P_LE][i] = 1;
				} else
				{
					countLessEq2++;
					ret[P_LE][i] = 1;
				}
			}
		
		}
		ret[P_EQUAL][C_NORMAL] = countEqual;
		ret[P_GREATER][C_NORMAL] = countGreater;
		ret[P_LESSER][C_NORMAL] = countLess;
		ret[P_GE][C_NORMAL] = countGreaterEq;
		ret[P_LE][C_NORMAL] = countLessEq;
		ret[P_EQUAL][C_HALF] = countEqual2;
		ret[P_GREATER][C_HALF] = countGreater2;
		ret[P_LESSER][C_HALF] = countLess2;
		ret[P_GE][C_HALF] = countGreaterEq2;
		ret[P_LE][C_HALF] = countLessEq2;
		return ret;
		
	}
	
	
	/**
	 * Checks if the geography height is modified by special elements (river or economics).
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param farView
	 * @return null if not modified, the height relative to worldgroundlevel if modified
	 */
	public Integer overrideHeightForException(int worldX, int worldY, int worldZ, boolean farView)
	{
		World w = (World)getRoot();
		
		// economics...
		ArrayList<Object> list = w.economyContainer.treeLocator.getElements(worldX, worldY, worldZ);
		// check if there is possible economic here...
		if (list!=null) 
		{
			// yes there is...
			for (Object o:list)
			{
				Economic e = (Economic)o;
				{
					// get the limits of the population/economic
					int limitXMin = e.origoX-2;
					int limitXMax = e.origoX+e.sizeX+2;
					int limitZMin = e.origoZ-2;
					int limitZMax = e.origoZ+e.sizeZ+2;
					// check if we are inside the population...
					if (worldX>=limitXMin && worldX<=limitXMax)
					{
						if (worldZ>=limitZMin && worldZ<=limitZMax)
						{
							// check for grouped boundary... (population)
							if (e.getBoundaries() instanceof GroupedBoundaries)
							{
								// yes grouped. let's check the nearby elements one by one...
								ArrayList<Object> eo = ((GroupedBoundaries)e.getBoundaries()).locator.getElements(worldX, e.origoY, worldZ);
								if (eo!=null)
								for (Object o2:eo)
								{
									if (o2 instanceof Boundaries)
									{
										Boundaries p = (Boundaries)o2;
										if (p.boundaryPlace!=null) {
											if (p.boundaryPlace.overrideGeoHeight())
											{
												if (p.isInside(worldX, p.boundaryPlace.origoY, worldZ))
												{
													// yes we are inside...override height.
													return p.boundaryPlace.origoY-worldGroundLevel;
												}
											}
										}
									}
								}
							}
							else
							if (e.getBoundaries().isInside(worldX, e.origoY, worldZ))
							{
								//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("override: "+( ((Economic)o).origoY-worldGroundLevel ));
								return e.origoY-worldGroundLevel;
							}
						}						
					}
				}
			}
		}
		// let's check for waters here...
		for (Water geo:((World)getRoot()).waters.values())
		{
			if (this!=geo)
			{
				if (geo instanceof River && geo.boundaries.isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					if (geo.isWaterPoint(worldX, geo.worldGroundLevel, worldZ, farView)) return 0;
				}
			}
		}
		return null;
	}

	
	
	
	int lastWorldX = -9999, lastWorldZ = -9999; 
	int[] lastHeight;
	
	public int[] getPointHeight(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView)
	{
		if (lastWorldX==worldX && lastWorldZ==worldZ)
		{
			return lastHeight;
		}
		lastWorldX = worldX; lastWorldZ = worldZ;
		if (!boundaries.isInside(worldX, worldGroundLevel, worldZ))
		{
			lastHeight = getPointHeightOutside(worldX, worldZ, farView);
		} else
		{
			lastHeight = getPointHeightInside(x, z, sizeX, sizeZ, worldX, worldZ, farView);
		}
		return lastHeight;
	}
	protected int[] getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView)
	{
		return new int[]{0,0};
	}
	
	
	int s_lastWorldX = -9999, s_lastWorldZ = -9999;
	SurfaceHeightAndType[] s_lastType = null; 
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ, boolean farView) {
		if (s_lastWorldX==worldX && s_lastWorldZ==worldZ)
		{
			return s_lastType;
		} else
		{
			s_lastWorldX = worldX; s_lastWorldZ = worldZ;
		}
		int[] values = calculateTransformedCoordinates(worldX, worldGroundLevel, worldZ);
		//int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldZ);
		int realSizeX = values[0];
		//int realSizeY = values[1];
		int realSizeZ = values[2];
		int relX = values[3];
		//int relY = values[4];
		int relZ = values[5];

		int Y = getPointHeight(relX, relZ, realSizeX, realSizeZ,worldX,worldZ, farView)[0];
		float[] kindArray = getCubeKind(-1, worldX, Y, worldZ,  farView);
		int kind = (int)kindArray[4];
		if (kind>=0 && kind<=4)
		{
			s_lastType =  new SurfaceHeightAndType[]{new SurfaceHeightAndType(this,worldGroundLevel+Y,true,kind)};
		} else
		if (kind>=6)
		{
			s_lastType = new SurfaceHeightAndType[]{new SurfaceHeightAndType(this,worldGroundLevel+Y,false,J3DCore.BOTTOM)};
		}
		s_lastType = new SurfaceHeightAndType[]{new SurfaceHeightAndType(this,worldGroundLevel+Y,true,SurfaceHeightAndType.NOT_STEEP)};
		return s_lastType;
	}
	
	/**
	 * return 0 realSizeX, 1 realsizeY, 2 realSizeZ, 3 relX, 4 relY, 5 relZ 
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public int[] calculateTransformedCoordinates(int worldX, int worldY, int worldZ)
	{
		// 0 realSizeX, 1 realsizeY, 2 realSizeZ, 3 relX, 4 relY, 5 relZ 
		return new int[]{blockSize,worldRealHeight,blockSize,worldX%blockSize,worldY-worldGroundLevel,worldZ%blockSize};
	}

	/**
	 * Get Cube Kind from other outside geographies than the current geography.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public float[] getCubeKindOutside(long key, int worldX, int worldY, int worldZ, boolean farView)
	{
		for (Geography geo:((World)getRoot()).geographies.values())
		{
			if (this!=geo)
			{
				if (geo.boundaries.isInside(worldX, worldY, worldZ))
				{
					return geo.getCubeKind(key, worldX,worldY,worldZ, farView);
				}
			}
		}
		return emptyKind;
	}
	private static float[] emptyKind = new float[]{0,0,0,0,K_EMPTY};

	/**
	 * Gets the cubekind of a coordinate based on the height and the height of neighbor points.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public float[] getCubeKind(long key, int worldX, int worldY, int worldZ, boolean farView)
	{
		if (numericId!=0) 
		{
			Long keyNew = numericId*(farView?2:1);
			if (key==-1)
			{
				keyNew += Boundaries.getKey(worldX, worldY, worldZ);
			} else
			{
				keyNew+=key;
			}
			float[] cachedKind = quickCubeKindCache.get(keyNew);
			if (cachedKind!=null) 
			{
				//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("CUBE CACHE USED!");
				return cachedKind;
			}
			float[] kind = getCubeKindNoCache(worldX, worldY, worldZ,  farView);
			if (quickCubeKindCache.size()>5)
			{
				quickCubeKindCache.clear();
			}
			quickCubeKindCache.put(keyNew, kind);
			return kind;
		} else
		{
			// no right unique numbericId, use no cache 
			return getCubeKindNoCache(worldX, worldY, worldZ,  farView);
		}
	}
	
	
	
	private float[] getCubeKindNoCache(int worldX, int worldY, int worldZ, boolean farView)
	{
		float[] tmpCornerHeightsAndKind = new float[9]; 
		int[] values = calculateTransformedCoordinates(worldX, worldY, worldZ);
		//int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldZ);
		int realSizeX = values[0];
		//int realSizeY = values[1];
		int realSizeZ = values[2];
		int relX = values[3];
		int relY = values[4];
		int relZ = values[5];
		
		
		int FARVIEW_GAP = farView?J3DCore.FARVIEW_GAP:1;

		int[] aY = getPointHeight(relX, relZ, realSizeX, realSizeZ,worldX,worldZ, farView);
		int Y = aY[0]/FARVIEW_GAP;
		relY /= FARVIEW_GAP;
		
		float hNE = 0f;
		float hNW = 0f;
		float hSE = 0f;
		float hSW = 0f;
		
		int[] aYNorth = getPointHeight(relX, relZ+FARVIEW_GAP, realSizeX, realSizeZ,worldX,shrinkToWorld(worldZ+FARVIEW_GAP), farView);
		int[] aYNorthEast = getPointHeight(relX+FARVIEW_GAP, relZ+FARVIEW_GAP, realSizeX, realSizeZ,shrinkToWorld(worldX+FARVIEW_GAP),shrinkToWorld(worldZ+FARVIEW_GAP), farView);
		int[] aYNorthWest = getPointHeight(relX-FARVIEW_GAP, relZ+FARVIEW_GAP, realSizeX, realSizeZ,shrinkToWorld(worldX-FARVIEW_GAP),shrinkToWorld(worldZ+FARVIEW_GAP), farView);
		int[] aYSouth = getPointHeight(relX, relZ-FARVIEW_GAP, realSizeX, realSizeZ,worldX,shrinkToWorld(worldZ-FARVIEW_GAP), farView);
		int[] aYSouthEast = getPointHeight(relX+FARVIEW_GAP, relZ-FARVIEW_GAP, realSizeX, realSizeZ,shrinkToWorld(worldX+FARVIEW_GAP),shrinkToWorld(worldZ-FARVIEW_GAP), farView);
		int[] aYSouthWest = getPointHeight(relX-FARVIEW_GAP, relZ-FARVIEW_GAP, realSizeX, realSizeZ,shrinkToWorld(worldX-FARVIEW_GAP),shrinkToWorld(worldZ-FARVIEW_GAP), farView);
		int[] aYWest = getPointHeight(relX-FARVIEW_GAP, relZ, realSizeX, realSizeZ,shrinkToWorld(worldX-FARVIEW_GAP),worldZ, farView);
		int[] aYEast = getPointHeight(relX+FARVIEW_GAP, relZ, realSizeX, realSizeZ,shrinkToWorld(worldX+FARVIEW_GAP),worldZ, farView);

		int YNorth = aYNorth[0]/FARVIEW_GAP;
		int YNorthEast = aYNorthEast[0]/FARVIEW_GAP;
		int YNorthWest = aYNorthWest[0]/FARVIEW_GAP;
		int YSouth = aYSouth[0]/FARVIEW_GAP;
		int YSouthEast = aYSouthEast[0]/FARVIEW_GAP;
		int YSouthWest = aYSouthWest[0]/FARVIEW_GAP;
		int YWest = aYWest[0]/FARVIEW_GAP;
		int YEast = aYEast[0]/FARVIEW_GAP;

		//if (aY[1]==0)
		{
			
			/*if (aYNorth[1]==1)
			{
				tmpCornerHeightsAndKind[0] = YNorth - Y;
			}
			if (aYSouth[1]==1)
			{
				tmpCornerHeightsAndKind[2] = YSouth- Y;
			}
			if (aYEast[1]==1)
			{
				tmpCornerHeightsAndKind[1] = YEast - Y;
			}
			if (aYWest[1]==1)
			{
				tmpCornerHeightsAndKind[3] = YWest- Y;
			}*/
			
			/*if (aYNorthEast[1]==1)
			{
				// override detected...
				hNE = YNorthEast - Y;
			} else
			{
				hNE = ((Y + YNorth + YNorthEast + YEast)/4f) - Y;
			}
			if (aYNorthWest[1]==1)
			{
				// override detected...
				hNW = YNorthWest - Y;
			} else
			{
				hNW = ((Y + YNorth + YNorthWest + YWest)/4f) - Y;
			}
			if (aYSouthWest[1]==1)
			{
				// override detected...
				hSW = YSouthWest - Y;
			} else
			{
				hSW = ((Y + YSouth + YSouthWest + YWest)/4f) - Y;
			}
			if (aYSouthEast[1]==1)
			{
				// override detected...
				hSE = YSouthEast - Y;
			} else
			{
				hSE = ((Y + YSouth + YSouthEast + YEast)/4f) - Y;
			}*/
			//System.out.println(" "+hNE+" "+hNW+" "+hSW+" "+hSE);
			hNE = ((Y + YNorth + YNorthEast + YEast)/4f) - Y;
			hNW = ((Y + YNorth + YNorthWest + YWest)/4f) - Y;
			hSW = ((Y + YSouth + YSouthWest + YWest)/4f) - Y;
			hSE = ((Y + YSouth + YSouthEast + YEast)/4f) - Y;
		}
	
		tmpCornerHeightsAndKind[0] = hNW;
		tmpCornerHeightsAndKind[1] = hNE;
		tmpCornerHeightsAndKind[2] = hSW;
		tmpCornerHeightsAndKind[3] = hSE;
		
		int K_STEEP_EAST = Geography.K_STEEP_EAST;
		int K_STEEP_SOUTH = Geography.K_STEEP_SOUTH;
		int K_STEEP_WEST = Geography.K_STEEP_WEST;
		int K_STEEP_NORTH = Geography.K_STEEP_NORTH;
		
		//if (this instanceof Plain) if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("-- RELY - "+relY+" - "+Y);
		int[][] eval = evaluate(Y, new int[]{YNorth,YEast,YSouth,YWest,YNorthEast, YSouthEast, YSouthWest, YNorthWest});
		if (Y==relY) 
		{

			if (eval[P_LESSER][C_NORMAL]==3)
			{
				if (eval[P_GE][NORTH]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_NORTH;
					return tmpCornerHeightsAndKind;
				}
					
				if (eval[P_GE][EAST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_EAST;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_GE][SOUTH]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_SOUTH;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_GE][WEST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_WEST;
					return tmpCornerHeightsAndKind;
				}
			}
			if (eval[P_LESSER][C_NORMAL]==1)
			{
				if (eval[P_LESSER][NORTH]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_SOUTH;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_LESSER][EAST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_WEST;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_LESSER][SOUTH]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_NORTH;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_LESSER][WEST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_EAST;
					return tmpCornerHeightsAndKind;
				}
			}
			if (eval[P_LESSER][C_NORMAL]==4)
			{
				int per = HashUtil.mixPercentage(worldX, worldY, worldZ);
				if (per<25)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_NORTH;
					return tmpCornerHeightsAndKind;
				}
				if (per<50)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_EAST;
					return tmpCornerHeightsAndKind;
				}
				if (per<75)
				{
					tmpCornerHeightsAndKind[4] = K_STEEP_SOUTH;
					return tmpCornerHeightsAndKind;
				}
				tmpCornerHeightsAndKind[4] = K_STEEP_WEST;
				return tmpCornerHeightsAndKind;
			}
			
			if (eval[P_LESSER][C_NORMAL]==2)
			{
				if (eval[P_LESSER][NORTH]==1 && eval[P_LESSER][EAST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_CORNER_SOUTH;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_LESSER][NORTH]==1 && eval[P_LESSER][WEST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_CORNER_EAST;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_LESSER][SOUTH]==1 && eval[P_LESSER][EAST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_CORNER_WEST;
					return tmpCornerHeightsAndKind;
				}
				if (eval[P_LESSER][SOUTH]==1 && eval[P_LESSER][WEST]==1)
				{
					tmpCornerHeightsAndKind[4] = K_CORNER_NORTH;
					return tmpCornerHeightsAndKind;
				}
			}

			
			
			// one half side is bigger
			
			if (eval[P_LESSER][C_HALF]==1)// && eval[P_EQUAL][C_HALF]==3)
			{
				if (eval[P_LESSER][NORTH_EAST]==1)
				{
					if (eval[P_EQUAL][NORTH]==1 && eval[P_EQUAL][EAST]==1)
					{
						tmpCornerHeightsAndKind[4] = K_INTERSECT_WEST;
						return tmpCornerHeightsAndKind;
					}
				}
				if (eval[P_LESSER][NORTH_WEST]==1) // good
				{
					if (eval[P_EQUAL][NORTH]==1 && eval[P_EQUAL][WEST]==1)
					{
						tmpCornerHeightsAndKind[4] = K_INTERSECT_SOUTH;
						return tmpCornerHeightsAndKind;
					}
				}
				if (eval[P_LESSER][SOUTH_EAST]==1)
				{
					if (eval[P_EQUAL][SOUTH]==1 && eval[P_EQUAL][EAST]==1)
					{
						tmpCornerHeightsAndKind[4] = K_INTERSECT_NORTH;
						return tmpCornerHeightsAndKind;
					}
				}
				if (eval[P_LESSER][SOUTH_WEST]==1) // good
				{
					if (eval[P_EQUAL][SOUTH]==1 && eval[P_EQUAL][WEST]==1)
					{
						tmpCornerHeightsAndKind[4] = K_INTERSECT_EAST;
						return tmpCornerHeightsAndKind;
					}
				}
				
				
			}
			
			tmpCornerHeightsAndKind[4] = K_NORMAL_GROUND;
			return tmpCornerHeightsAndKind;
		}
		// checking if there are lower parts on neighbor cubes that would make an empty cube visible - if so place a rock block instead
		if (Y>relY && (relY-1>=YNorth || relY-1>=YSouth|| relY-1>=YWest || relY-1>=YEast)) 
		{
			tmpCornerHeightsAndKind[4] = K_ROCK_BLOCK;
			return tmpCornerHeightsAndKind;
		}
		if (Y>relY && (relY==YNorth || relY==YSouth|| relY==YWest || relY==YEast)) 
		{
			int i=0;
			if (relY==YNorth+1) i++;
			if (relY==YWest+1) i++;
			if (relY==YSouth+1) i++;
			if (relY==YEast+1) i++;
			if (i<=1)
			{
				tmpCornerHeightsAndKind[4] = K_ROCK_BLOCK;
				return tmpCornerHeightsAndKind;
			}
		}
		{
			tmpCornerHeightsAndKind[4] = K_EMPTY;
			return tmpCornerHeightsAndKind;
		}
	}
	
	public boolean isAlgorithmicallyInside(int worldX, int worldY, int worldZ)
	{
		return true;
	}
	
	public GeneratedPartRuleSet getRuleSet() {
		return ruleSet;
	}
	
	public String getName(Dialect dialect,int worldX, int worldZ)
	{
		return this.getClass().getSimpleName()+" of "+ getCoreName(dialect, worldX, worldZ);
	}
	
	public String getCoreName(Dialect dialect,int worldX, int worldZ)
	{
		int x = worldX/blockSize;
		int z = worldZ/blockSize;
		return DialectTool.getName(dialect, this.getClass().getSimpleName(), (int)(x+z+numericId), this.getClass(), this);
	}
}
