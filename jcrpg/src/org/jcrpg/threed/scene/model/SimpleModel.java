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

package org.jcrpg.threed.scene.model;

import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.util.HashUtil;


public class SimpleModel extends Model {

	public String modelName, textureName;
	public String steepTextureName = null;
	public String secTextureName = null;
	public boolean mipMap = true;
	public int xGeomBatchSize = -1;
	public int yGeomBatchSize = -1;
	
	public boolean generatedGroundModel = false;
	
	public String steepId = null;
	public String secId = null;
	
	public SimpleModel(String modelName, String textureName)
	{
		type = SIMPLEMODEL;
		this.id = modelName+textureName+mipMap;;
		this.modelName = modelName;
		this.textureName = textureName;
		
	}
	public SimpleModel(String modelName, String textureName, boolean mipMap)
	{
		type = SIMPLEMODEL;
		this.id = modelName+textureName+mipMap;
		this.modelName = modelName;
		this.textureName = textureName;
		this.mipMap = mipMap;
		
	}
	
	public float steepRatio = 0.8f;
	public int secTextChance = 50;
	
	@Override
	public String getId(NodePlaceholder place) {
		
		if (!generatedGroundModel || steepTextureName==null)
		{
			return super.getId(place);
		}
		if (place.cube.cube.angleRatio>steepRatio)
		{
			if (steepId==null)
			{
				this.steepId = modelName+mipMap+steepTextureName;
			}
			return steepId;
		}
		if (secTextureName!=null && HashUtil.mixPercentage(place.cube.cube.x,place.cube.cube.z,0)<secTextChance)
		{
			if (secId==null)
			{
				this.secId = modelName+mipMap+secTextureName;
			}
			return secId;
		}
			
		return id;
	}
	
	public String getTexture(NodePlaceholder place)
	{
		if (!generatedGroundModel || steepTextureName==null)
		{
			return textureName;
		}
		if (place.cube.cube.angleRatio>steepRatio)
		{
			//System.out.println("AR: "+place.cube.cube.angleRatio+" "+steepTextureName);
			return steepTextureName;
		}
		if (secTextureName!=null && HashUtil.mixPercentage(place.cube.cube.x,place.cube.cube.z,0)<secTextChance)
			return secTextureName;
		return textureName;
	}
	
	public String getBlendTextureKey(NodePlaceholder place)
	{
		return textureName;
	}
	
}
