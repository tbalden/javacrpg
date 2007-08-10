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

package org.jcrpg.world.place;

public class BoundaryUtils {

	
	public static Boundaries createCubicBoundaries(int magnification,int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception
	{
		Boundaries b = new Boundaries(magnification);
		for (int x=0; x<sizeX;x++)
			for (int y=0; y<sizeY;y++)
				for (int z=0; z<sizeZ;z++)
				{
					b.addCube(magnification,origoX+x, origoY+y, origoZ+z);
					//if (x==0 || x==sizeX-1 && y==0 || y==sizeY-1 && z==0 || z==sizeZ-1)
						//b.addLimiterCube(magnification,origoX+x, origoY+y, origoZ+z);
				}
		return b;
			
	}
	
	
}
