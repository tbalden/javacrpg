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

public class TextureStateVegetationModel extends Model{

	public String[] textureNames;
	public float quadSizeX = 0.9f;
	public float quadSizeY = 0.5f;
	public int quadQuantity = 3;
	public float quadSeparation = 0.64f;
	public boolean atlasTexture = false;
	public int atlasSize = 1;
	public int atlasId = 0;

	public TextureStateVegetationModel(String[] textureName, float quadSizeX, float quadSizeY, int quadQuantity, float quadSeparation) {
		super();
		this.type = TEXTURESTATEVEGETATION;
		this.textureNames = textureName;
		this.quadSizeX = quadSizeX;
		this.quadSizeY = quadSizeY;
		this.quadQuantity = quadQuantity;
		this.quadSeparation = quadSeparation;
		this.id = getKey();
	}

	public TextureStateVegetationModel(String[] textureName) {
		super();
		this.type = TEXTURESTATEVEGETATION;
		this.textureNames = textureName;
		this.id = getKey();
	}
	
	String key = null;
	
	public String getKey()
	{
		if (key==null)
		{
			for (String tn:textureNames)
			{
				key+=tn;
			}
			if (atlasTexture) key+=atlasId;
		}
		return key;
	}
	
	public void updateKey()
	{
		for (String tn:textureNames)
		{
			key+=tn;
		}
		if (atlasTexture) key+=atlasId;
	}
	
}
