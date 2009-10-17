/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.threed.scene.model;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.util.HashUtil;


public class SimpleModel extends Model {

	
	/**
	 * The atlas texture name.
	 */
	public String atlasTextureName = "";
	public String atlasTextureNameNormal = null;
	public String atlasTextureNameSpec = null;
	public String atlasTextureNameHeight = null;

	/**
	 * Tells if this model is wanting to use atlas texture technique.
	 */
	public boolean useAtlasTexture = false;
	/**
	 * Number of All subtextures in the atlas_texture
	 */
	public int atlasSize = 3;

	/**
	 * Which Atlas texture is the normal texture in the row.
	 */
	public int atlasNormalId= 0;
	/**
	 * Which Atlas texture is the normal texture in the row.
	 */
	public int atlasSecTextureId = 1;
	/**
	 * Which Atlas texture is the steep texture in the row.
	 */
	public int atlasSteepId = 2;
	
	public String modelName, textureName;
	public String steepTextureName = null;
	public String secTextureName = null;
	
	public String normalMapTexture = null;
	public String specMapTexture = null;
	public String heightMapTexture = null;

	public String steepNormalMapTexture = null;
	public String steepSpecMapTexture = null;
	public String steepHeightMapTexture = null;
	
	public String secNormalMapTexture = null;
	public String secSpecMapTexture = null;
	public String secHeightMapTexture = null;
	
	public boolean mipMap = true;
	public int xGeomBatchSize = -1;
	public int yGeomBatchSize = -1;
	
	public boolean generatedGroundModel = false;
	
	public String steepId = null;
	public String secId = null;
	
	public SimpleModel(String modelName, String textureName)
	{
		type = SIMPLEMODEL;
		this.id = modelName+atlasTextureName+textureName+mipMap;;
		this.modelName = modelName;
		this.textureName = textureName;
		
	}
	public SimpleModel(String modelName, String textureName, boolean mipMap)
	{
		type = SIMPLEMODEL;
		this.id = modelName+atlasTextureName+textureName+mipMap;
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
				this.steepId = modelName+mipMap+atlasTextureName+steepTextureName;
			}
			return steepId;
		}
		if (J3DCore.SETTINGS.SECONDARY_TEXTURES && secTextureName!=null && HashUtil.mixPercentage(place.cube.cube.x,place.cube.cube.z,0)<secTextChance)
		{
			if (secId==null)
			{
				this.secId = modelName+mipMap+atlasTextureName+secTextureName;
			}
			return secId;
		}
			
		return id;
	}
	
	String[] textureArray, steepTextureArray, secTextureArray;
	
	public String[] getTexture(NodePlaceholder place)
	{
		if (textureArray==null)
		{
			if (useAtlasTexture)
			{
				textureArray = new String[]{textureName,atlasTextureNameNormal, atlasTextureNameHeight,atlasTextureNameSpec};
			} else
			{
				textureArray = new String[]{textureName,normalMapTexture, heightMapTexture,specMapTexture};
			}
		}
		if (steepTextureArray==null)
		{
			if (useAtlasTexture)
			{
				steepTextureArray = new String[]{steepTextureName,atlasTextureNameNormal, atlasTextureNameHeight,atlasTextureNameSpec};
			} else
			{
				steepTextureArray = new String[]{steepTextureName,steepNormalMapTexture, steepHeightMapTexture,steepSpecMapTexture};
			}
		}
		if (secTextureArray==null)
		{
			if (useAtlasTexture)
			{
				secTextureArray = new String[]{secTextureName,atlasTextureNameNormal, atlasTextureNameHeight,atlasTextureNameSpec};
			} else
			{
				secTextureArray = new String[]{secTextureName,secNormalMapTexture, secHeightMapTexture,secSpecMapTexture};
			}
		}
		
		if (!generatedGroundModel || steepTextureName==null)
		{
			return textureArray;
		}
		if (place.cube.cube.angleRatio>steepRatio)
		{
			//System.out.println("AR: "+place.cube.cube.angleRatio+" "+steepTextureName);
			return steepTextureArray;
		}
		if (J3DCore.SETTINGS.SECONDARY_TEXTURES && secTextureName!=null && HashUtil.mixPercentage(place.cube.cube.x,place.cube.cube.z,0)<secTextChance)
			return secTextureArray;
		return textureArray;
	}
	
	
	public String getBlendTextureKey(NodePlaceholder place)
	{
		return textureName;
	}
	
}
