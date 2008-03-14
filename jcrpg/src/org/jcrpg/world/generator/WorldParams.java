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
	
	/**
	 * The overall height ratio of the world geography (0 - 0.1 ,,, 2 ,,, parameter used for multiplication) .
	 */
	public float heightRatio = 1f;
	
	/**
	 * Makes all things different :) .
	 */
	public int randomSeed = 0;
	
	public String[] climates;
	public int[] climateSizeMuls;
	
	public String foundationGeo;
	
	public String[] geos;
	public int[] geoLikenessValues;

	public String[] additionalGeos;
	public int[] additionalGeoLikenessValues;
	
	public int geoNormalSize;

	public WorldParams()
	{
		super();
	}
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
	public WorldParams(int magnification, int sizeX, int sizeY, int sizeZ, String foundationGeo, int landDensity, int landMass, int randomSeed, String[] climates, int[] climateSizeMuls,String[] geos,int[] geoLikenessValues,String[] additionalGeos,int[] additionalGeoLikenessValues, int geoNormalSize) {
		super();
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.foundationGeo = foundationGeo;
		this.landDensity = landDensity;
		this.landMass = landMass;
		this.randomSeed = randomSeed;
		this.climates = climates;
		this.climateSizeMuls = climateSizeMuls;
		this.geos = geos;
		this.geoLikenessValues = geoLikenessValues;
		this.additionalGeos = additionalGeos;
		this.additionalGeoLikenessValues = additionalGeoLikenessValues;
		this.setGeoNormalSize(geoNormalSize);
		
	}

	/**
	 * @return the magnification
	 */
	public int getMagnification() {
		return magnification;
	}

	/**
	 * @param magnification the magnification to set
	 */
	public void setMagnification(int magnification) {
		this.magnification = magnification;
	}

	/**
	 * @return the sizeX
	 */
	public int getSizeX() {
		return sizeX;
	}

	/**
	 * @param sizeX the sizeX to set
	 */
	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	/**
	 * @return the sizeY
	 */
	public int getSizeY() {
		return sizeY;
	}

	/**
	 * @param sizeY the sizeY to set
	 */
	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	/**
	 * @return the sizeZ
	 */
	public int getSizeZ() {
		return sizeZ;
	}

	/**
	 * @param sizeZ the sizeZ to set
	 */
	public void setSizeZ(int sizeZ) {
		this.sizeZ = sizeZ;
	}

	/**
	 * @return the landDensity
	 */
	public int getLandDensity() {
		return landDensity;
	}

	/**
	 * @param landDensity the landDensity to set
	 */
	public void setLandDensity(int landDensity) {
		this.landDensity = landDensity;
	}

	/**
	 * @return the landMass
	 */
	public int getLandMass() {
		return landMass;
	}

	/**
	 * @param landMass the landMass to set
	 */
	public void setLandMass(int landMass) {
		this.landMass = landMass;
	}

	/**
	 * @return the heightRatio
	 */
	public float getHeightRatio() {
		return heightRatio;
	}

	/**
	 * @param heightRatio the heightRatio to set
	 */
	public void setHeightRatio(float heightRatio) {
		this.heightRatio = heightRatio;
	}

	/**
	 * @return the randomSeed
	 */
	public int getRandomSeed() {
		return randomSeed;
	}

	/**
	 * @param randomSeed the randomSeed to set
	 */
	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	/**
	 * @return the climates
	 */
	public String[] getClimates() {
		return climates;
	}

	/**
	 * @param climates the climates to set
	 */
	public void setClimates(String[] climates) {
		this.climates = climates;
	}

	/**
	 * @return the climateSizeMuls
	 */
	public int[] getClimateSizeMuls() {
		return climateSizeMuls;
	}

	/**
	 * @param climateSizeMuls the climateSizeMuls to set
	 */
	public void setClimateSizeMuls(int[] climateSizeMuls) {
		this.climateSizeMuls = climateSizeMuls;
	}

	/**
	 * @return the foundationGeo
	 */
	public String getFoundationGeo() {
		return foundationGeo;
	}

	/**
	 * @param foundationGeo the foundationGeo to set
	 */
	public void setFoundationGeo(String foundationGeo) {
		this.foundationGeo = foundationGeo;
	}

	/**
	 * @return the geos
	 */
	public String[] getGeos() {
		return geos;
	}

	/**
	 * @param geos the geos to set
	 */
	public void setGeos(String[] geos) {
		this.geos = geos;
	}

	/**
	 * @return the geoLikenessValues
	 */
	public int[] getGeoLikenessValues() {
		return geoLikenessValues;
	}

	/**
	 * @param geoLikenessValues the geoLikenessValues to set
	 */
	public void setGeoLikenessValues(int[] geoLikenessValues) {
		this.geoLikenessValues = geoLikenessValues;
	}

	/**
	 * @return the additionalGeos
	 */
	public String[] getAdditionalGeos() {
		return additionalGeos;
	}

	/**
	 * @param additionalGeos the additionalGeos to set
	 */
	public void setAdditionalGeos(String[] additionalGeos) {
		this.additionalGeos = additionalGeos;
	}

	/**
	 * @return the additionalGeoLikenessValues
	 */
	public int[] getAdditionalGeoLikenessValues() {
		return additionalGeoLikenessValues;
	}

	/**
	 * @param additionalGeoLikenessValues the additionalGeoLikenessValues to set
	 */
	public void setAdditionalGeoLikenessValues(int[] additionalGeoLikenessValues) {
		this.additionalGeoLikenessValues = additionalGeoLikenessValues;
	}

	/**
	 * @return the geoNormalSize
	 */
	public int getGeoNormalSize() {
		return geoNormalSize;
	}

	/**
	 * @param geoNormalSize the geoNormalSize to set
	 */
	public void setGeoNormalSize(int geoNormalSize) {
		this.geoNormalSize = geoNormalSize;
		if (this.geoNormalSize<10) this.geoNormalSize = 10;
	}

	
	
	
}
