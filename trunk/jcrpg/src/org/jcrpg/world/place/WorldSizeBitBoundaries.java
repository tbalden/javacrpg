/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

/**
 * Bit based boundary - positions are recorded as bits in a series of byte arrays.
 * @author pali
 *
 */
public class WorldSizeBitBoundaries extends Boundaries {

	byte[] bytes;
	int gWX, gWY, gWZ;
	
	
	public WorldSizeBitBoundaries(int magnification, World w) {
		super(magnification);
		int wX = w.sizeX;
		int wY = w.sizeY;
		int wZ = w.sizeZ;
		int wMag = w.magnification;
		int gMag = magnification;
		gWX = (wX*wMag)/gMag;
		gWY = (wY*wMag)/gMag;
		gWZ = (wZ*wMag)/gMag;
		
		//System.out.println(""+gWX+" "+gWY+" "+gWZ+" "+(int)((gWX*gWY*gWZ)/8));
		// negative int on too big world size!! add more byte arrays!
		System.out.println("(gWX*gWY*gWZ)/8="+(gWX*gWY*gWZ)/8); // TODO mem calculation exception if too high: 1000 magnification / 10 geo magnification in a 100x100 world would eat 200MB!
		bytes = new byte[(gWX*gWY*gWZ)/8];
	}

	byte[] placeBitMap = new byte[] {1,2,4,8,16,32,64,(byte)128};
	
	@Override
	public void addCube(int magnification, int x, int y, int z) throws Exception {
		int base = (x + y*gWX + z*(gWY*gWX));
		int place = base%8;
		place = placeBitMap[place];
		int byteCount = base/8;
		byte b = bytes[byteCount];
		b = (byte)((byte)b|(byte)place);
		bytes[byteCount] = b;
	}

	@Override
	public boolean isInside(int absoluteX, int absoluteY, int absoluteZ) {
		int x = absoluteX / magnification;
		int y = absoluteY / magnification;
		int z = absoluteZ / magnification;
		int base = (x + y*gWX + z*(gWY*gWX));
		int place = base%8;
		place = placeBitMap[place];
		int byteCount = base/8;
		byte b = bytes[byteCount];
		return (b&place)==place;
	}

}
