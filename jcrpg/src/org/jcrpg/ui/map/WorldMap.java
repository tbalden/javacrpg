/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.ui.map;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.place.water.River;
import org.jcrpg.world.time.Time;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BillboardNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jmex.awt.swingui.ImageGraphics;

public class WorldMap {
	public Quad mapQuad = new Quad("WORLD_MAP",2f,2f);
	public BillboardNode bbMap;
	public Sphere mapSphere = new Sphere("WORLD_MAP_SPHERE",new Vector3f(0,0,0),10,10,0.5f);
	public Image oceanImage;
	public Image positionImage; 
	public Image climateImage;
	public Image geoImage;
	public byte[] positionImageSet;
	
	public int[][] map;
	
	public Texture oceanTex, posTex, climateTex, geoTex;
	public TextureState baseTexState, posTexState, climateTexState, geoTexState;

	public World world;
	
	public int NOTHING = 0;
	public int WATER = 1;
	
	public static int PIXELS_PER_BLOCK = 9;
	public static int PIXELS_PER_BLOCK_OVERLAP = 11;
	
	// 2
	// 4
	// 8
	// 16
	// 32
	// 64
	// 128
	public static HashSet<Quad> updatedQuads = new HashSet<Quad>();
	
	public void registerQuad(Quad q)
	{
		updatedQuads.add(q);
	}
	
	static boolean[][] GROUND = new boolean[][] 
	  	                                    {
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true },
	  		{ true, true, true, true, true,true, true, true, true }
	  	                                    }
	  	;

	static boolean[][] OVERLAP = new boolean[][] 
		  	                                    {
				{ false, false, true, true, false, true,true, false, true, false, false},
		  		{ false, true, true, true, true, true,true, true, true, true , false},
		  		{ false, true, true, true, true, true,true, true, true, true , false},
		  		{ true, true, true, true, true, true,true, true, true, true , false},
		  		{ false, true, true, true, true, true,true, true, true, true , true},
		  		{ true, true, true, true, true, true,true, true, true, true , true},
		  		{ true, true, true, true, true, true,true, true, true, true , true},
		  		{ false, true, true, true, true, true,true, true, true, true , false},
		  		{ false, true, true, true, true, true,true, true, true, true , true},
		  		{ false, true, true, true, true, true,true, true, true, true , false},
				{ false, false, true, true, false, true,true, false, true, false, false},
		  	                                    }
	;
	
	static boolean[][] CLIMATE = new boolean[][] 
		  	                                    {
		  		{ true, false, true, false, true, false, true, false, true },
		  		{ false, true, true, true, true,true, true, true, false },
		  		{ true, true, true, false, true, false, true, true, true },
		  		{ false, true, false, true, true,true, false, true, false },
		  		{ true, true, true, false, true, false, true, true, true },
		  		{ false, true, false, true, true,true, false, true, false },
		  		{ true, true, true, false, true, false, true, true, true },
		  		{ false, true, true, true, true,true, true, true, false },
		  		{ true, false, true, false, true, false, true, false, true }
		  	                                    }
		  	;
	public static boolean[][] CITY = new boolean[][] 
		  	                                    {
		  		{ false, false, false, false, false,false, false, false, false },
		  		{ false, false, true, true, true,false, false, false, false },
		  		{ false, true, true, true, true,true, false, false, false },
		  		{ false, true, true, true, true,true, false, false, false },
		  		{ false, true, true, true, true,true, false, false, false },
		  		{ false, false, true, true, true,false, false, false, false },
		  		{ false, false, false, false, false,false, false, false, false },
		  		{ false, false, false, false, false,false, false, false, false },
		  		{ false, false, false, false, false,false, false, false, false }
		  	                                    }
		  	;
	static boolean[][] POSITION = new boolean[][] 
	  	                                    {
	  		{ true, false, false, false, true,false, false, false, true },
	  		{ false, false, false, false, false,false, false, false, false },
	  		{ false, false, false, false, false,false, false, false, false },
	  		{ false, false, false, false, false,false, false, false, false },
	  		{ true, false, false, false, false,false, false, false, true },
	  		{ false, false, false, false, false,false, false, false, false },
	  		{ false, false, false, false, false,false, false, false, false },
	  		{ false, false, false, false, false,false, false, false, false },
	  		{ true, false, false, false, true,false, false, false, true },
	  	                                    }
	  	;

	static boolean[][] ROAD_NORTH = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, true,true, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;
		
	static boolean[][] ROAD_SOUTH = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, false, true,true, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false }
			  	                                    }
			  	;
	static boolean[][] ROAD_WEST = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, true, false,false, false, false, false },
			  		{ true, true, true, true, true, false, false, false, false },
			  		{ true, true, true, false, true,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;
	static boolean[][] ROAD_EAST = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, true, true, true, false, true},
			  		{ false, false, false, false, true, true, true, true, true},
			  		{ false, false, false, false, false,false, false, true, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;
	
	static boolean[][][] ROADS = new boolean[][][] {ROAD_NORTH, ROAD_EAST, ROAD_SOUTH, ROAD_WEST};

	
	static boolean[][] RIVER_NORTH = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, true,true, false, false, false },
			  		{ false, false, false, true, true,true, false, false, false },
			  		{ false, false, false, true, true,true, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;
		
	static boolean[][] RIVER_SOUTH = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false },
			  		{ false, false, false, true, true,true, false, false, false },
			  		{ false, false, false, true, true,true, false, false, false },
			  		{ false, false, false, true, true,true, false, false, false },
			  		{ false, false, false, true, true,false, false, false, false }
			  	                                    }
			  	;
	static boolean[][] RIVER_WEST = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, true, true, true, false,false, false, false, false },
			  		{ true, true, true, true, true, false, false, false, false },
			  		{ true, true, true, false, true,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;
	static boolean[][] RIVER_EAST = new boolean[][] 
			  	                                    {
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, true, true, true, false, true},
			  		{ false, false, false, false, true, true, true, true, true},
			  		{ false, false, false, false, false,true, true, true, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;

	static boolean[][][] RIVERS = new boolean[][][] {RIVER_NORTH, RIVER_EAST, RIVER_SOUTH, RIVER_WEST};
	
	
	
		
	public void paintPattern(byte[] color, byte alpha, byte[] map, int X, int Y, int sizeX, boolean[][] pattern, boolean vary, boolean zoomed)
	{
		
		for (int x = 0; x<PIXELS_PER_BLOCK; x++)
		{
			for (int z = 0; z<PIXELS_PER_BLOCK; z++)
			{
				int patternX = 0, patternY = 0;
				if (pattern.length<PIXELS_PER_BLOCK)
				{
					if (zoomed)
					{
						patternY = (PIXELS_PER_BLOCK-1-z)/2;
						patternX = x/2;
					} else
					{
						patternY = (PIXELS_PER_BLOCK-1-z)%(PIXELS_PER_BLOCK/2);
						patternX = x%(PIXELS_PER_BLOCK/2);
					}
					//System.out.println("--"+patternY+" / "+patternX);
				} else
				{
					patternY = PIXELS_PER_BLOCK-1-z;
					patternX = x;
				}
				if (pattern[patternY][patternX]) {
				int disp = 4 * (X*PIXELS_PER_BLOCK + x + (Y * PIXELS_PER_BLOCK * PIXELS_PER_BLOCK + z * PIXELS_PER_BLOCK)* sizeX);
				for (int i=0; i<color.length; i++)
				{
					if (vary)

						{
						map[disp+i] = (byte)Math.min(color[i]+2*x,255);
						} else map[disp+i] = color[i];
				}
				map[disp+3] = alpha;}
			}
		}
		
	}

	public void paintPattern(byte[] map, int X, int Y, int sizeX, ColorRGBA[][] pattern, boolean zoomed)
	{
		
		for (int x = 0; x<PIXELS_PER_BLOCK; x++)
		{
			for (int z = 0; z<PIXELS_PER_BLOCK; z++)
			{
				int patternX = 0, patternY = 0;
				if (pattern.length<PIXELS_PER_BLOCK)
				{
					if (zoomed)
					{
						patternY = (PIXELS_PER_BLOCK-1-z)/2;
						patternX = x/2;
					} else
					{
						patternY = (PIXELS_PER_BLOCK-1-z)%(PIXELS_PER_BLOCK/2);
						patternX = x%(PIXELS_PER_BLOCK/2);
					}
					//System.out.println("--"+patternY+" / "+patternX);
				} else
				{
					patternY = PIXELS_PER_BLOCK-1-z;
					patternX = x;
				}
				if (pattern[patternY][patternX]!=TR_COL || pattern[patternY][patternX].a>0f) {
					int disp = 4 * (X*PIXELS_PER_BLOCK + x + (Y * PIXELS_PER_BLOCK * PIXELS_PER_BLOCK + z * PIXELS_PER_BLOCK)* sizeX);
					for (int i=0; i<4; i++)
					{
							map[disp+i] = (byte)((int)((pattern[patternY][patternX].getColorArray()[i]*255f)));
					}
				}
			}
		}
		
	}
	

	public static ColorRGBA TR_COL = new ColorRGBA(0,0,0,0);

	public void paintOverlapPattern(byte[] color, byte alpha, byte[] map, int X, int Y, int sizeX, boolean[][] pattern, boolean vary, boolean zoomed)
	{
		for (int x = 0; x<PIXELS_PER_BLOCK_OVERLAP; x++)
		{
			for (int z = 0; z<PIXELS_PER_BLOCK_OVERLAP; z++)
			{
				int patternX = 0, patternY = 0;
				if (pattern.length<PIXELS_PER_BLOCK_OVERLAP)
				{
					if (zoomed)
					{
						patternY = (PIXELS_PER_BLOCK_OVERLAP-1-z)/2;
						patternX = x/2;
					} else
					{
						patternY = (PIXELS_PER_BLOCK_OVERLAP-1-z)%PIXELS_PER_BLOCK_OVERLAP;
						patternX = x%PIXELS_PER_BLOCK_OVERLAP;
					}
				} else
				{
					patternY = PIXELS_PER_BLOCK_OVERLAP-1-z;
					patternX = x;
				}
				
				if (pattern[patternY][patternX]) 
				{
					int disp = 4 * (X*PIXELS_PER_BLOCK + x - 1 + (Y * PIXELS_PER_BLOCK * PIXELS_PER_BLOCK + (z - 1)* PIXELS_PER_BLOCK)* sizeX);
					try {
					for (int i=0; i<color.length; i++)
					{
						if (vary)
	
							{
							map[disp+i] = (byte)Math.min(color[i]+2*x,255);
							} else map[disp+i] = color[i];
					}
					map[disp+3] = alpha;
					} catch (ArrayIndexOutOfBoundsException ex){}
				}
			}
		}
	}
	
	ArrayList<LabelDesc> labels = new ArrayList<LabelDesc>();
	HashSet<String> towns = new HashSet<String>();

	public WorldMap(World w) {
		world = w;
	
		map = new int[w.sizeZ][w.sizeX];
		byte[] mapImage = new byte[w.sizeZ*w.sizeX*4 * PIXELS_PER_BLOCK* PIXELS_PER_BLOCK];
		//byte[] climateImage = new byte[w.sizeZ*w.sizeX*4 * PIXELS_PER_BLOCK* PIXELS_PER_BLOCK];
		byte[] geoImageSet = new byte[w.sizeZ*w.sizeX*4 * PIXELS_PER_BLOCK* PIXELS_PER_BLOCK];
		positionImageSet = new byte[w.sizeZ*w.sizeX*4 * PIXELS_PER_BLOCK* PIXELS_PER_BLOCK];
		Collection<Geography> geos = world.geographies.values();
		for (int z = 0; z<w.sizeZ;z++)
		{
			for (int x=0; x<w.sizeX; x++)
			{
				map[z][x] = NOTHING;
				mapImage[((z*w.sizeX*PIXELS_PER_BLOCK*PIXELS_PER_BLOCK)+x*PIXELS_PER_BLOCK)*4+1] = (byte)150;
				mapImage[((z*w.sizeX*PIXELS_PER_BLOCK*PIXELS_PER_BLOCK)+x*PIXELS_PER_BLOCK)*4+0] = (byte)50;
				boolean oceanWater = false;
				boolean riverWater = false;
				ArrayList<River> rivers = new ArrayList<River>();
				boolean ecoFound = false;
				Economic ecoObj = null;
				int wx = x*w.magnification;
				int wz = z*w.magnification;
				boolean foundRoad = w.economyContainer.roadNetwork.getBoundaries().isInside(wx, w.getSeaLevel(1), wz);;
				for (Water water :w.waters.values())
				{
					if (water instanceof Ocean)
					{
						oceanWater = ((Ocean)water).isWaterPointSpecial(x*((Ocean)water).magnification, ((Ocean)water).worldGroundLevel, z*((Ocean)water).magnification, false, false);
						{
							System.out.print(".");
							ClimateBelt belt = world.getClimate().getCubeClimate(new Time(), x*w.magnification+3, 0, z*w.magnification+3, false).getBelt();
							//paintPattern(belt.colorBytes, (byte)255, climateImage, x, z, w.sizeX, CLIMATE,false,false);
							paintPattern(belt.colorBytes, (byte)255, mapImage, x, z, w.sizeX, CLIMATE,false,false);
							
							
							
							{
								Population p = null;// w.economyContainer.getPopulationAtBlock(wx, w.getSeaLevel(1), wz);
								if (p==null)
								{
									p = w.economyContainer.getPopulationAtBlock(wx+ w.magnification/2, w.getSeaLevel(1), wz+ w.magnification/2);
								}
								ArrayList<Object> economics =null;
								
								if (p==null)
								{
									economics = w.economyContainer.treeLocator.getElements(wx, w.getSeaLevel(1), wz);
									ArrayList<Object> economics1 = w.economyContainer.treeLocator.getElements(wx+w.magnification/2, w.getSeaLevel(1), wz);
									ArrayList<Object> economics2 = w.economyContainer.treeLocator.getElements(wx, w.getSeaLevel(1), wz+w.magnification/2);
									ArrayList<Object> economics3 = w.economyContainer.treeLocator.getElements(wx+w.magnification/2, w.getSeaLevel(1), wz+w.magnification/2);
									if (economics==null)
									{
										economics = economics1;
										economics1 = null;
									}
									if (economics==null)
									{
										economics = economics2;
										economics2 = null;
									}
									if (economics==null)
									{
										economics = economics3;
										economics3 = null;
									}
									if (economics1!=null) economics.addAll(economics1);
									if (economics2!=null) economics.addAll(economics2);
									if (economics3!=null) economics.addAll(economics3);
								}
								
								if (p!=null) {
									if (economics==null) economics = new ArrayList<Object>();
									economics.add(p);
								}
								
								if (economics!=null)
								{
									for (Object o:economics)
									{
										Economic e = ((Economic)o);
										if (p!=null)
										{
											e = p;
										} else
										if (
												!e.isWorldMapVisible() ||
												(e.origoX>wx+w.magnification)
												||
												(e.origoX+e.sizeX-1<wx)
												||
												(e.origoZ>wz+w.magnification)
												||
												(e.origoZ+e.sizeZ-1<wz)
										) 
										{
											continue;
										}
										ecoObj = e;
										ecoFound = true;
										byte[] cB = ecoObj.getMapColor();
										BlockPattern pattern = ecoObj.getMapPattern();
										boolean[][] bytePattern = pattern==null?CITY:pattern.PATTERN==null?CITY:pattern.PATTERN;
										ColorRGBA[][] colorPattern = pattern.COLORED_PATTERN;
										if (colorPattern!=null)
										{
											paintPattern(geoImageSet, x, z, w.sizeX, colorPattern, true);
										} else
										{
											paintPattern(cB, (byte)255,geoImageSet, x, z, w.sizeX,  bytePattern,false,true);
										}		
										break;
									}
									
								}
								
								for (Geography g:geos)
								{
									if (g.isWorldMapTinter() && g.getBoundaries().isInside(wx, g.worldGroundLevel, wz))
									{
										paintPattern(g.colorBytes, (byte)100,geoImageSet, x, z, w.sizeX, GROUND, true,false);
										break;
									}
								}
							}
						}
						if (oceanWater)
						{
							map[z][x] = (map[z][x]^WATER);
							System.out.print("W");
							paintOverlapPattern(new byte[]{0,0,100}, (byte)255, mapImage, x, z, w.sizeX, OVERLAP, false,true);
							paintOverlapPattern(new byte[]{10,10,100}, (byte)190, geoImageSet, x, z, w.sizeX, OVERLAP,false,true);
						} 
					} else
					if (water instanceof River) // TODO river into a new image for not overwriting geo things!!
					{
						
						riverWater = ((River)water).isWaterBlock(wx, world.worldGroundLevel, wz);
						if (riverWater) rivers.add((River)water);
					}


				}
				// paint found things...
				// 1st river
				if (riverWater) {
					
					for (River water:rivers )
					{
						boolean[] directions = ((River)water).getWorldSizeFlowDirections().getFlowDirections(wx, w.getSeaLevel(1), wz);
						for (int i=J3DCore.NORTH; i<=J3DCore.WEST;i++)
						{
							if (directions[i])
							{
								paintPattern(new byte[]{30,30,(byte)200}, (byte)190, geoImageSet, x, z, w.sizeX, RIVERS[i],false,true);
							}
						}
					}
					System.out.print("!");
				}
				// then roads...
				if (foundRoad)
				{
					boolean[] directions = w.economyContainer.roadNetwork.getWorldSizeFlowDirections().getFlowDirections(wx, w.getSeaLevel(1), wz);
					for (int i=J3DCore.NORTH; i<=J3DCore.WEST;i++)
					{
						if (directions[i])
						{
							paintPattern(new byte[]{(byte)130,(byte)50,(byte)45}, (byte)255,geoImageSet, x, z, w.sizeX, ROADS[i],false,true);
						}
					}
					
				}
				// populations last!
				if (ecoFound) {
					byte[] cB = ecoObj.getMapColor();
					BlockPattern pattern = ecoObj.getMapPattern();
					boolean[][] bytePattern = pattern==null?CITY:pattern.PATTERN==null?CITY:pattern.PATTERN;
					ColorRGBA[][] colorPattern = pattern.COLORED_PATTERN;

					if (ecoObj instanceof Population)
					{
						String townName = ((Population)ecoObj).town.foundationName;
						{
							towns.add(townName);
							LabelDesc desc = new LabelDesc();
							desc.text = townName;
							desc.scale1 = ((Population)ecoObj).town.getSize();
							desc.x = x;
							desc.z = z;
							desc.type1 = ((Population)ecoObj).foundationName;
							try{desc.type2 = ((Population)ecoObj).owner.description.getName();}catch (NullPointerException npe){}
							
							labels.add(desc);
						}
					}
					if (colorPattern!=null)
					{
						paintPattern(geoImageSet, x, z, w.sizeX, colorPattern, true);
					} else
					{
						paintPattern(cB, (byte)255,geoImageSet, x, z, w.sizeX,  bytePattern,false,true);
					}		
				}

				mapImage[((z*w.sizeX*PIXELS_PER_BLOCK*PIXELS_PER_BLOCK)+x*PIXELS_PER_BLOCK)*4+3] = (byte)150;
				
				
			}
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("");
		}
		oceanImage = new Image();
		oceanImage.setFormat(Image.Format.RGBA8);
		oceanImage.setHeight(w.sizeZ*PIXELS_PER_BLOCK);
		oceanImage.setWidth(w.sizeX*PIXELS_PER_BLOCK);
		ByteBuffer buffer = ByteBuffer.wrap(mapImage);
		oceanImage.setData(buffer);
		
		positionImage = new Image();
		positionImage.setFormat(Image.Format.RGBA8);
		positionImage.setHeight(w.sizeZ*PIXELS_PER_BLOCK);
		positionImage.setWidth(w.sizeX*PIXELS_PER_BLOCK);
		ByteBuffer buffer2 = ByteBuffer.wrap(positionImageSet);
		positionImage.setData(buffer2);
		
		geoImage = new Image();
		geoImage.setFormat(Image.Format.RGBA8);
		geoImage.setHeight(w.sizeZ*PIXELS_PER_BLOCK);
		geoImage.setWidth(w.sizeX*PIXELS_PER_BLOCK);
		ByteBuffer buffer3 = ByteBuffer.wrap(geoImageSet);
		geoImage.setData(buffer3);
		update(0, 0, 0);
	}
	
	int lastCx = -1 , lastCy = -1 , lastCz = -1;
	
	public ByteBuffer posBuffer = null;

	
	public static HashMap<Integer, Color> colorCache = new HashMap<Integer, Color>();
	public void paintPoint(ImageGraphics set, int x, int y, int r, int g, int b, int a)
	{
		a = 255;
		r = 255;
		int k = (r<<24)+(g<<16)+(b<<8)+a;
		Color c = colorCache.get(k);
		if (c==null)
		{
			c = new Color(Math.max(0, Math.min(r,255)),Math.max(0, Math.min(g,255)),Math.max(0, Math.min(b,255)),a);
			colorCache.put(k, c);
		} 
		set.setColor(c);
		set.drawRect(x, y, 0, 0);
	}
	
	public void update(int cx, int cy, int cz)
	{
		
		//System.out.println(cx+" "+cz);
		
		/*Collection<Geography> geos = world.geographies.values();
		int wx = cx * 40;
		int wz = cz * 40;
		for (Geography g:geos)
		if (g.getBoundaries().isInside(wx, g.worldGroundLevel, wz))
		{
			System.out.println(g.getClass());
		}*/		

		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest(""+Math.min(world.sizeZ,++cz)+" "+Math.max(0,--cx));
		
		if (cx==lastCx && cz==lastCz) return;
		lastCx = cx;
		lastCz = cz;
		cz = Math.min(world.sizeZ,cz);
		cx = Math.max(0,cx);
		boolean newInstance = false;
		if (positionGraphics==null)
		{
			newInstance = true;
			positionGraphics = ImageGraphics.createInstance(world.sizeX*PIXELS_PER_BLOCK, world.sizeZ*PIXELS_PER_BLOCK, 0);
		}
		positionGraphics.setBackground(new Color(0,0,0,0));
		positionGraphics.clearRect(0, 0, positionGraphics.getImage().getWidth(), positionGraphics.getImage().getHeight());
		
		Jcrpg.LOGGER.info("UPDATE: "+cx+" "+cz);
		int dotSize = PIXELS_PER_BLOCK;
		{
			for (int i=0; i<=dotSize; i++)
			{
				for (int j=0; j<=dotSize; j++)
				{
					try {
						if (i<=1 || i >= dotSize-1 || j<=1 || j >= dotSize-1)
						{
							int red = (int)((((dotSize-Math.abs(j))*1d/dotSize)*((dotSize-Math.abs(i))*1d/dotSize)*355));
							paintPoint(positionGraphics, (cx)*PIXELS_PER_BLOCK+j, (cz*PIXELS_PER_BLOCK)+i, red , 0, 0, 255);
						}
					} catch (ArrayIndexOutOfBoundsException aiex)
					{				
					}
				}
			}
		}
		try {
			paintPoint(positionGraphics, cx*PIXELS_PER_BLOCK+2, cz*PIXELS_PER_BLOCK+2, 255 , 0, 0, 255);
		} catch (ArrayIndexOutOfBoundsException aiex){
			
			aiex.printStackTrace();
		}
		
		if (newInstance)
		{
			posTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
			posTex = new Texture2D();
			posTex.setMagnificationFilter( Texture.MagnificationFilter.NearestNeighbor);
			posTex.setMinificationFilter(Texture.MinificationFilter.NearestNeighborNoMipMaps);
			posTex.setImage(positionGraphics.getImage());
			posTexState.setTexture(posTex);
			posTexState.apply();
		}else
		{	positionGraphics.update(posTex,false);
			posTexState.apply();
		}
		for (Quad q:updatedQuads)
		{
			if (q.getRenderState(RenderState.RS_TEXTURE)!=posTexState) {
				q.setRenderState(posTexState);
			}
			q.updateRenderState();
		}
	}
	public ImageGraphics positionGraphics;

	public TextureState[] getMapTextures()
	{
		oceanTex = new Texture2D();
		oceanTex.setImage(oceanImage);
		oceanTex.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		oceanTex.setMinificationFilter(Texture.MinificationFilter.BilinearNoMipMaps);
		geoTex = new Texture2D();
		geoTex.setImage(geoImage);
		geoTex.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		geoTex.setMinificationFilter(Texture.MinificationFilter.BilinearNoMipMaps);
		
		baseTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		baseTexState.setTexture(oceanTex);
		geoTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		geoTexState.setTexture(geoTex);
		
		return new TextureState[]{baseTexState,null,geoTexState};
	}

	
	public class LabelDesc
	{
		public int x,z;
		public String text;
		public int scale1, scale2;
		public String type1, type2;
	}
	
	public class LabelContainer
	{
		public ArrayList<LabelDesc> towns;
	}
	
	LabelContainer container = new LabelContainer();
	
	/**
	 * @return name lists of labels on map.
	 */
	public LabelContainer getLabels()
	{
		container.towns = labels;
		return container;
	}

	
}
