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

package org.jcrpg.world.place;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;

/**
 * Bit based indicator for N-E-S-W direction flow switch for X,Y,Z described blocks.
 * @author pali
 *
 */
public class WorldSizeFlowDirections {

	byte[] bytes;
	int gWX, gWY, gWZ;
	int magnification;
	
	public WorldSizeFlowDirections(int magnification, World w) {
		int wX = w.sizeX;
		int wY = w.sizeY;
		int wZ = w.sizeZ;
		this.magnification = magnification;
		int wMag = w.magnification;
		int gMag = magnification;
		gWX = (wX*wMag)/gMag;
		gWY = (wY*wMag)/gMag;
		gWZ = (wZ*wMag)/gMag;
		
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(""+gWX+" "+gWY+" "+gWZ+" "+(int)((gWX*gWY*gWZ)/8));
		// negative int on too big world size!! add more byte arrays!
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("WorldSizeFlowDirection (gWX*gWY*gWZ*4)/8="+(gWX*gWY*gWZ*4)/8); // TODO mem calculation exception if too high: 1000 magnification / 10 geo magnification in a 100x100 world would eat 200MB!
		bytes = new byte[(gWX*gWY*gWZ*4)/8];
	}

	byte[] placeBitMap = new byte[] {1,2,4,8,16,32,64,(byte)128};
	byte[] placeBitMapReverse = new byte[] {(byte)254,(byte)253,(byte)251,(byte)247,(byte)239,(byte)223,(byte)191,(byte)127};
	
	public void setCubeFlowDirection(int x, int y, int z, int direction, boolean value) throws Exception {
		int base = (direction + x*4 + y*4*gWX + z*(4*gWY*gWX));
		int place = base%8;
		int byteCount = base/8;
		byte b = bytes[byteCount];
		if (value)
		{
			place = placeBitMap[place];
			b = (byte)((byte)b|(byte)place); // setting the bit
		} else
		{
			place = placeBitMapReverse[place];
			b = (byte)((byte)b&(byte)place); // null out the bit
		}
		bytes[byteCount] = b;
	}

	/**
	 * N-E-S-W true or false depending on water is flowing in the given direction.
	 * @param absoluteX
	 * @param absoluteY
	 * @param absoluteZ
	 * @return four long boolean array telling direction flow or not
	 */
	public boolean[] getFlowDirections(int absoluteX, int absoluteY, int absoluteZ) {
		boolean[] flows = new boolean[4];
		int x = absoluteX / magnification;
		int y = absoluteY / magnification;
		int z = absoluteZ / magnification;
		int placeBase = (x*4 + y*4*gWX + z*(4*gWY*gWX));
		for (int i=0; i<4; i++) { 
			int place = (i + placeBase)%8;
			place = placeBitMap[place];
			
			int byteCount = (i + placeBase)/8;
			byte b = bytes[byteCount];
			flows[i] = (b&place)==place;
		}
		return flows;
	}

}
