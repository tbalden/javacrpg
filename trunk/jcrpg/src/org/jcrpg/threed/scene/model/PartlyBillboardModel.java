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

package org.jcrpg.threed.scene.model;

import java.util.HashMap;
import java.util.HashSet;

public class PartlyBillboardModel extends SimpleModel {

	public String id;
	public String[] billboardPartNames = new String[0];
	public String[] billboardPartTextures = new String[0];
	public String[] removedPartNames = new String[0];
	
	/**
	 * Level of detail, use 0,1,2 values. (0 most detailed, 1 quads removed, 2 very low quad number)
	 */
	public int LOD=0;
	
	/**
	 * Tells how much the quads are to be multiplied in size deviating from calculated size.
	 */
	public float quadXSizeMultiplier = 1;
	public float quadYSizeMultiplier = 1;
	
	public boolean windAnimation = true;
	
	/**
	 * Tells if light state should be off for the quads. If off, they will be handled by lightpower of J3dcore orbiters. (hsSolidColorQuads).
	 */
	public boolean quadLightStateOff = true;
	
	public HashMap<String, Integer> partNameToTextureCount = new HashMap<String, Integer>();
	public HashSet<String> removedParts = new HashSet<String>();
	
	
	/**
	 * Constructor of model.
	 * @param modelName The name (path) of the model file.
	 * @param billboardPartNames Billboarded partnames define which part of the model is to be replaced with quads.
	 * @param removedPartNames if LOD 1 or 2, removedParts will be not added to the new node of this model.
	 * @param billboardPartTextures Texture names for the billboard quads.
	 * @param LOD Level of detail, use 0,1,2 values. (0 most detailed, 1 quads removed, 2 very low quad number).
	 * @param mipMap Tells if mipmapping is needed.
	 */
	public PartlyBillboardModel(String id,String modelName, String[] billboardPartNames, String[] removedPartNames, String[] billboardPartTextures, int LOD, boolean mipMap) {
		super(modelName, null, mipMap);
		this.billboardPartNames = billboardPartNames;
		this.billboardPartTextures = billboardPartTextures;
		this.removedPartNames = billboardPartNames;
		int c = 0;
		this.LOD = Math.max(Math.min(LOD,2),0); // between 0 and 2
		for (String n:billboardPartNames)
		{
			partNameToTextureCount.put(n,c++);
		}
		for (String n:removedPartNames)
		{
			removedParts.add(n);
		}
		this.id = id;
	}

	/**
	 * Constructor of model with no mipmap specification possibility.
	 * @param modelName The name (path) of the model file.
	 * @param billboardPartNames Billboarded partnames define which part of the model is to be replaced with quads.
	 * @param removedPartNames if LOD 1 or 2, removedParts will be not added to the new node of this model.
	 * @param billboardPartTextures Texture names for the billboard quads.
	 * @param LOD Level of detail, use 0,1,2 values. (0 most detailed, 1 quads removed, 2 very low quad number).
	 */
	public PartlyBillboardModel(String id, String modelName, String[] billboardPartNames, String[] removedPartNames, String[] billboardPartTextures,int LOD) {
		this(id,modelName,billboardPartNames,billboardPartTextures,removedPartNames,LOD,false);
	}

}
