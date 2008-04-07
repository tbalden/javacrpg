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
		b.limitXMin = origoX; b.limitXMax= origoX+sizeX;
		b.limitYMin = origoY; b.limitYMax = origoY+sizeY;
		b.limitZMin = origoZ; b.limitZMax = origoZ+sizeZ;
		b.calcLimits();
		return b;
			
	}
	
	
}
