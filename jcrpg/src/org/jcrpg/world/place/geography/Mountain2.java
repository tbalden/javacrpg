/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.world.place.geography;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.ClimbingVertical;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Geography;


public class Mountain2 extends Geography {

	public static final String TYPE_MOUNTAIN = "MOUNTAIN";
	public static final SideSubType SUBTYPE_STEEP = new ClimbingVertical(TYPE_MOUNTAIN+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_ROCK = new NotPassable(TYPE_MOUNTAIN+"_GROUND_ROCK");
	public static final SideSubType SUBTYPE_GROUND = new NotPassable(TYPE_MOUNTAIN+"_GROUND");
	public static final SideSubType SUBTYPE_TREE = new SideSubType(TYPE_MOUNTAIN+"_TREE");

	static Side[] ROCK = {new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK)};
	static Side[] GROUND = {new Side(TYPE_MOUNTAIN,SUBTYPE_GROUND)};
	static Side[] NORMAL_TREE = {new Side(TYPE_MOUNTAIN,SUBTYPE_GROUND),new Side(TYPE_MOUNTAIN,SUBTYPE_TREE)};
	static Side[] STEEP = {new Side(TYPE_MOUNTAIN,SUBTYPE_STEEP)};
	
	static Side[][] MOUNTAIN_ROCK = new Side[][] { null, null, null,null,null,ROCK };
	static Side[][] MOUNTAIN_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] GROUND_NORMAL_TREE = new Side[][] { null, null, null,null,null,NORMAL_TREE };
	static Side[][] STEEP_NORTH = new Side[][] { STEEP, null, null,null,null,null };
	static Side[][] STEEP_EAST = new Side[][] { null, STEEP, null,null,null,null };
	static Side[][] STEEP_SOUTH = new Side[][] { null, null, STEEP,null,null,null };
	static Side[][] STEEP_WEST = new Side[][] { null, null, null,STEEP,null,null };


	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	
	public Mountain2(String id, Place parent, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}

	

	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		int relX = worldX-origoX*magnification;
		int relY = worldY-origoY*magnification;
		int relZ = worldZ-origoZ*magnification;
		int remainingX = sizeX*magnification-relX;
		int remainingY = sizeY*magnification-relY;
		int remaningZ = sizeZ*magnification-relZ;
		int realSizeX = sizeX*magnification-1;
		int realSizeY = sizeY*magnification;
		int realSizeZ = sizeZ*magnification-1;
		
		//Z = r*r - ( (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
		
		int x1 = realSizeX / 2;
		int z1 = realSizeZ / 2;
		
		int x2 = relX;
		int z2 = relZ;
		
		int r = realSizeX / 2 + realSizeX / 8;
		
		int Y = r*r - ( (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1) );
		
		if (Y/(10*realSizeX)>=relY) 
		{
			return new Cube(this,MOUNTAIN_ROCK,worldX,worldY,worldZ);
		}
		if (true) return null;
		
		
		if (relX==0 && relY==0 && (relZ==0 ||relZ==realSizeZ) || relX==realSizeX && relY==0 && (relZ==0 ||relZ==realSizeZ))
		{
			return new Cube(this,MOUNTAIN_GROUND,worldX,worldY,worldZ);
		}
/*		if (relZ==0 && relY==0|| relZ==realSizeZ && relY==0)
		{
			return new Cube(this,MOUNTAIN_ROCK,worldX,worldY,worldZ);
			
		}*/
		
		System.out.println("MOUNTAIN GETC: "+relX+" "+relY+" "+relZ+" L: "+remainingX+" "+remainingY+" "+remaningZ);
		
		int proportionateXSizeOnLevelY = realSizeX - (int)(realSizeX * ((relY*1d)/(realSizeY)));
		int proportionateZSizeOnLevelY = realSizeZ - (int)(realSizeZ * ((relY*1d)/(realSizeY)));
		int gapX = ((realSizeX) - proportionateXSizeOnLevelY)/2;
		int gapZ = ((realSizeZ) - proportionateZSizeOnLevelY)/2;
		int proportionateXSizeOnLevelYNext = realSizeX - (int)(realSizeX * (((relY+1)*1d)/(realSizeY)));
		int proportionateZSizeOnLevelYNext = realSizeZ - (int)(realSizeZ * (((relY+1)*1d)/(realSizeY)));
		int gapXNext = ((realSizeX) - proportionateXSizeOnLevelYNext)/2;
		int gapZNext = ((realSizeZ) - proportionateZSizeOnLevelYNext)/2;
		
		
		boolean returnCube = false;
		Side[][] returnSteep = null;
		// NORMAL
		if (relX>=gapX && relX<=gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relX==gapX && gapXNext!=gapX) returnSteep = STEEP_NORTH;
		}
		if (relX<=realSizeX-gapX && relX>=realSizeX-gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relX==realSizeX-gapX && gapXNext!=gapX) returnSteep = STEEP_SOUTH;
		}
		if (relZ>=gapZ && relZ<=gapZNext && relX>gapX &&  relX<realSizeX-gapX)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relZ==gapZ && gapZNext!=gapZ) returnSteep = STEEP_WEST;
		}
		if (relZ<=realSizeZ-gapZ && relZ>=realSizeZ-gapZNext && relX>gapX && relX<realSizeX-gapX)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relZ==realSizeZ-gapZ && gapZNext!=gapZ) returnSteep = STEEP_EAST;
		}
		if (!returnCube) return null;
		//boolean cubeAbove = getCube( worldX,  worldY+1,  worldZ)!=null;
		Side[][] s = returnSteep!=null?returnSteep:MOUNTAIN_ROCK;
		Cube c = null;
		c = new Cube(this,s,worldX,worldY,worldZ);
		return c;
	}
	
	

}
