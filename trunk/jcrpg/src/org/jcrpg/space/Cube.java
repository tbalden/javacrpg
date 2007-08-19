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

package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.SurfaceHeightAndType;


public class Cube extends ChangingImpl {



	public Side[] n, e, s, w, top, bottom;
	
	public int steepDirection = SurfaceHeightAndType.NOT_STEEP;
	
	public long lastChangeTimeStamp = System.currentTimeMillis();
	
	public boolean overwrite = false;
	
	
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
	}

	public Cube(Cube c1, Cube c2, int x, int y, int z, int steepDir) {
		steepDirection = steepDir;
		this.parent = c1.parent;
		this.x = x;
		this.y = y;
		this.z = z;
		for (int i=0; i<sides.length; i++)
		{
			Side[] sides1 = c1.sides[i];
			Side[] sides2 = c2.sides[i];
			Side[] merged = new Side[(sides1==null?0:sides1.length)+(sides2==null?0:sides2.length)];
			if (sides1==null || c2.overwrite)
			{
				merged = sides2;
			} else
			if (sides2== null || c1.overwrite)
			{
				merged = sides1;
			} else
			for (int j=0; j<merged.length; j++)
			{
				if (j<sides1.length)
					merged[j] =sides1[j];
				else
					merged[j] = sides2[j-sides1.length];
			}
			this.sides[i] = merged;
		}
		//System.out.println(" MERGED CUBE == "+this);
	}
	
	public Cube(Place parent, Side[][] sides, int x, int y, int z) {
		this(parent,sides,x,y,z,SurfaceHeightAndType.NOT_STEEP);
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
		return "Cube: "+ x+" "+y+" "+z+" "+r;
	}
	
	public Cube getNeighbour(int direction)
	{
		Object[] o = J3DCore.directionAnglesAndTranslations.get(new Integer(direction));
		int[] f = (int[])o[1];
		Cube n = parent.getCube(x+f[0], y+f[1], z+f[2]);
		//if (n==null) System.out.println(this+" : "+parent.id+" "+direction+" NEIGHBOUR = "+n);
		return n;
	}
	
	public Cube copy(Place newParent)
	{
		return new Cube(newParent,sides,x,y,z,steepDirection);
	}
	
}
