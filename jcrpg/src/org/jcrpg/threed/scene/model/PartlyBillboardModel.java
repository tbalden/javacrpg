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

public class PartlyBillboardModel extends SimpleModel {

	public String[] billboardPartNames = new String[0];
	public String[] billboardPartTextures = new String[0];
	public int LOD=0;
	
	public HashMap<String, Integer> partNameToTextureCount = new HashMap<String, Integer>();
	
	
	public PartlyBillboardModel(String modelName, String[] billboardPartNames, String[] billboardPartTextures, int LOD, boolean mipMap) {
		super(modelName, null, mipMap);
		this.billboardPartNames = billboardPartNames;
		this.billboardPartTextures = billboardPartTextures;
		int c = 0;
		this.LOD = Math.max(Math.min(LOD,2),0); // between 0 and 2
		for (String n:billboardPartNames)
		{
			partNameToTextureCount.put(n,c++);
		}
	}

	public PartlyBillboardModel(String modelName, String[] billboardPartNames,String[] billboardPartTextures, int LOD) {
		this(modelName,billboardPartNames,billboardPartTextures,LOD,false);
	}

}
