/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.SurfaceHeightAndType;


public class Cube extends ChangingImpl {

	public String climateId;
	public int geoCubeKind = Geography.K_UNDEFINED;
	public boolean canContain = false; 

	public Side[] n, e, s, w, top, bottom;
	
	/**
	 * Which steep direction the cube has.
	 */
	public int steepDirection = SurfaceHeightAndType.NOT_STEEP;
	
	public long lastChangeTimeStamp = System.currentTimeMillis();
	
	/**
	 * Tells that this Cube should overwrite other cubes of same type (by Geo/Eco/Politics)
	 */
	public boolean overwrite = false;
	/**
	 * Tells the power of the Cube for overwrite. If one cube's overwrite power is higher, it will
	 * be the one that is used.
	 */
	public int overwritePower = 1;
	/**
	 * Tells if Cube should be displayed only if there is another of same type
	 */
	public boolean onlyIfOverlaps = false;
	
	/**
	 * If true it is a cube of an internal part.
	 */
	public boolean internalCube = false;
	
	/**
	 * If true it is underwater.
	 */
	public boolean waterCube = false;
	
	public Side[][] sides = {n,e,s,w,top,bottom};
	
	public int x,y,z;
	
	public Place parent;

	public Cube(Place parent, Side[] sides, int x, int y, int z, int steepDir) {
		steepDirection = steepDir;
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.z = z;
		for (int i=0; i<sides.length; i++)
		{
			this.sides[i] = new Side[] {sides[i]};
		}
		fillSideFields();
	}
	public void fillSideFields()
	{
		for (int i=0; i<sides.length; i++)
		{
			if (i==0) n = this.sides[i];
			if (i==1) e = this.sides[i];
			if (i==2) s = this.sides[i];
			if (i==3) w = this.sides[i];
			if (i==4) top = this.sides[i];
			if (i==5) bottom = this.sides[i];
		}
	}
	
	public void merge(Cube c2, int x, int y, int z, int steepDir) {
		if (this.climateId==null)
			this.climateId = c2.climateId;
		else {
			//this.climateId = this.climateId;
		}
		
		if (this.geoCubeKind==Geography.K_UNDEFINED)
			this.geoCubeKind = c2.geoCubeKind;
		else {
			//this.geoCubeKind = this.geoCubeKind;
		}

		int steep = this.steepDirection;
		if (this.steepDirection==SurfaceHeightAndType.NOT_STEEP || this.steepDirection == J3DCore.BOTTOM || this.steepDirection == J3DCore.TOP)
		{
			if (c2.steepDirection == J3DCore.BOTTOM || c2.steepDirection == J3DCore.TOP)
			{
				c2.steepDirection = SurfaceHeightAndType.NOT_STEEP;
			} else {
				steep = c2.steepDirection;
			}
		}
		int newSteepDirection = steep;
		if (c2.overwrite && c2.overwritePower>=this.overwritePower)
		{
			steepDirection = c2.steepDirection;
		}
		if (this.overwrite && this.overwritePower>=c2.overwritePower)
		{
			newSteepDirection = this.steepDirection;
		}

		this.x = x;
		this.y = y;
		this.z = z;
		boolean newOnlyIfOverlaps = false;
		if (this.canContain || c2.canContain)
		{
			this.canContain = true;
		} else
		{
			this.canContain = false;
		}
		if (this.overwrite || c2.overwrite) 
		{
			this.overwrite = true;
			if (this.onlyIfOverlaps || c2.onlyIfOverlaps) 
			{
				newOnlyIfOverlaps = true;
			}
		}
		if (this.internalCube || c2.internalCube)
		{
			internalCube = true;
		}
		int newOverwritePower = Math.max(this.overwritePower, c2.overwritePower);

		for (int i=0; i<sides.length; i++)
		{
			Side[] sides1 = this.sides[i];
			Side[] sides2 = c2.sides[i];
			Side[] merged = new Side[(sides1==null?0:sides1.length)+(sides2==null?0:sides2.length)];
			if (sides1==null || c2.overwrite && c2.overwritePower>=this.overwritePower)
			{				
				if (c2.onlyIfOverlaps) {
					if (!(c2.overwrite && c2.overwritePower>=this.overwritePower))
						merged = sides1==null?sides2:null;
					else
						merged = sides2;
				}
				else {
					merged = sides2;
				}
			} else
			if (sides2==null || this.overwrite && this.overwritePower>=c2.overwritePower)
			{				
				if (this.onlyIfOverlaps) {
					if (!(this.overwrite && this.overwritePower>=c2.overwritePower))
						merged = sides2==null?sides1:null;
					else
						merged = sides1;
				}
				else {
					merged = sides1;
				}
			} else
			{
				for (int j=0; j<merged.length; j++)
				{
					
					if (j<sides1.length)
						merged[j] =sides1[j];
					else
						merged[j] = sides2[j-sides1.length];
				}
			}
			internalCube = this.internalCube||c2.internalCube;
			this.sides[i] = merged;
		}
		fillSideFields();
		
		onlyIfOverlaps = newOnlyIfOverlaps;
		overwritePower = newOverwritePower;
		steepDirection = newSteepDirection;
		
	}

	public Cube(Cube c1, Cube c2, int x, int y, int z, int steepDir) {
		if (c1.climateId==null)
			this.climateId = c2.climateId;
		else
			this.climateId = c1.climateId;
		
		if (c1.geoCubeKind==Geography.K_UNDEFINED)
			this.geoCubeKind = c2.geoCubeKind;
		else
			this.geoCubeKind = c1.geoCubeKind;

		int steep = c1.steepDirection;
		if (c1.steepDirection==SurfaceHeightAndType.NOT_STEEP || c1.steepDirection == J3DCore.BOTTOM || c1.steepDirection == J3DCore.TOP)
		{
			if (c2.steepDirection == J3DCore.BOTTOM || c2.steepDirection == J3DCore.TOP)
			{
				c2.steepDirection = SurfaceHeightAndType.NOT_STEEP;
			} else {
				steep = c2.steepDirection;
			}
		}
		steepDirection = steep;
		if (c2.overwrite && c2.overwritePower>=c1.overwritePower)
		{
			steepDirection = c2.steepDirection;
		}
		if (c1.overwrite && c1.overwritePower>=c2.overwritePower)
		{
			steepDirection = c1.steepDirection;
		}
		this.parent = c1.parent;
		this.x = x;
		this.y = y;
		this.z = z;
		if (c1.canContain || c2.canContain)
		{
			this.canContain = true;
		} else
		{
			this.canContain = false;
		}
		if (c1.overwrite || c2.overwrite) 
		{
			this.overwrite = true;
			if (c1.onlyIfOverlaps || c2.onlyIfOverlaps) 
			{
				this.onlyIfOverlaps = true;
			}
		}
		if (c1.internalCube || c2.internalCube)
		{
			internalCube = true;
		}
		this.overwritePower = Math.max(c1.overwritePower, c2.overwritePower);

		for (int i=0; i<sides.length; i++)
		{
			Side[] sides1 = c1.sides[i];
			Side[] sides2 = c2.sides[i];
			Side[] merged = new Side[(sides1==null?0:sides1.length)+(sides2==null?0:sides2.length)];
			if (sides1==null || c2.overwrite && c2.overwritePower>=c1.overwritePower)
			{				
				if (c2.onlyIfOverlaps) {
					if (!(c2.overwrite && c2.overwritePower>=c1.overwritePower))
						merged = sides1==null?sides2:null;
					else
						merged = sides2;
				}
				else {
					merged = sides2;
				}
			} else
			if (sides2==null || c1.overwrite && c1.overwritePower>=c2.overwritePower)
			{				
				if (c1.onlyIfOverlaps) {
					if (!(c1.overwrite && c1.overwritePower>=c2.overwritePower))
						merged = sides2==null?sides1:null;
					else
						merged = sides1;
				}
				else {
					merged = sides1;
				}
			} else
			{
				for (int j=0; j<merged.length; j++)
				{
					
					if (j<sides1.length)
						merged[j] =sides1[j];
					else
						merged[j] = sides2[j-sides1.length];
				}
			}
			internalCube = c1.internalCube||c2.internalCube;
			this.sides[i] = merged;
		}
		fillSideFields();
		
	}
	
	public Cube(Place parent, Side[][] sides, int x, int y, int z) {
		this(parent,sides,x,y,z,SurfaceHeightAndType.NOT_STEEP);
	}
	public Cube(Place parent, int overwritePower, Side[][] sides, int x, int y, int z ) {
		this(parent,sides,x,y,z,SurfaceHeightAndType.NOT_STEEP);
		this.overwritePower = overwritePower;
	}
	
	public Cube(Place parent, Side[][] sides, int x, int y, int z, int steepDir) {
		steepDirection = steepDir;
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.z = z;
		for (int i=0; i<sides.length; i++)
		{
			this.sides[i] = sides[i];
		}
		fillSideFields();
	}
	
	public Side[] getSide(int sideId)
	{
		return sides[sideId];
	}
	
	public boolean hasSideOfType(int sideId, String sideType)
	{
		if (sides[sideId]!=null)
		for (int i=0; i<sides[sideId].length; i++)
		{
			if (sides[sideId][i].type==sideType)
			{
				return true;
			}
		}
		return false;
	}
	
	public String toString()
	{
		String r = "";
		for (int s=0; s<6; s++)
		for (int i=0; i<(sides[s]==null?0:sides[s].length); i++)
		{
			r+=sides[s][i]==null?"-":"S("+s+")"+sides[s][i].type+sides[s][i].subtype.getClass().getSimpleName()+" "+sides[s][i].subtype.id+" - ";
		}
		return "Cube: "+ x+" "+y+" "+z+" "+r+" -- "+overwritePower+" - "+onlyIfOverlaps;
	}
	
	public Cube getNeighbour(int direction)
	{
		Object[] o = J3DCore.directionAnglesAndTranslations.get(new Integer(direction));
		int[] f = (int[])o[1];
		Cube n = parent.getCube(-1, x+f[0], y+f[1], z+f[2], false);
		//if (n==null) System.out.println(this+" : "+parent.id+" "+direction+" NEIGHBOUR = "+n);
		return n;
	}
	
	public Cube copy(Place newParent)
	{
		Cube c = new Cube(newParent,sides,x,y,z,steepDirection);
		c.internalCube = internalCube;
		c.overwrite = overwrite;
		c.overwritePower = overwritePower;
		c.onlyIfOverlaps = onlyIfOverlaps;
		c.climateId = climateId;
		c.geoCubeKind = geoCubeKind;
		return c;
	}
	
}
