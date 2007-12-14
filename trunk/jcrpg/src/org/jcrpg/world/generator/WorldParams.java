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

package org.jcrpg.world.generator;

/**
 * World generation parameters.
 * @author illes
 *
 */
public class WorldParams {

	public int magnification = 100; 
	public int sizeX = 1;
	public int sizeY = 1;
	public int sizeZ = 1;
	
	/**
	 * 0-100 - bigger denser
	 */
	public int landDensity = 1;
	/**
	 * 0-100 - bigger bigger parts
	 */
	public int landMass = 1;
	
	public int randomSeed = 0;
	
	public String[] climates;
	public int[] climateSizeMuls;
	public String[] geos;
	public int geoNormalSize;

	/**
	 * World generation parameter constructor.
	 * @param magnification world magnification.
	 * @param sizeX world size. realSize is multiplied by magnification!
	 * @param sizeY world size. realSize is multiplied by magnification!
	 * @param sizeZ world size. realSize is multiplied by magnification!
	 * @param landDensity How dense the land mass should be.
	 * @param landMass Land mass relative to sea mass in percent.
	 * @param randomSeed Random seed - changes the whole world if differs.
	 * @param climates Every climate one time and in North-South order please! 
	 * @param climateSizeMuls Climate size multiplier
	 * @param geos Geography keys to use for generation.
	 */
	public WorldParams(int magnification, int sizeX, int sizeY, int sizeZ, int landDensity, int landMass, int randomSeed, String[] climates, int[] climateSizeMuls,String[] geos, int geoNormalSize) {
		super();
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.landDensity = landDensity;
		this.landMass = landMass;
		this.randomSeed = randomSeed;
		this.climates = climates;
		this.climateSizeMuls = climateSizeMuls;
		this.geos = geos;
		this.geoNormalSize = geoNormalSize;
		if (this.geoNormalSize<10) this.geoNormalSize = 10;
	}

	
	
	
}