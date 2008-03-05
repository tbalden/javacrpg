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

import org.jcrpg.threed.J3DCore;

public class QuadModel extends Model {

	public float sizeX;
	public float sizeY;
	public String textureName;
	// bump/normal map texture
	public String dot3TextureName;
	// tells if bump map texture needs transformation with SobelImageFilter from grayscale.
	public boolean transformToNormal = false;
	public boolean waterQuad = false;

	public QuadModel(String textureName) {
		super();
		type = QUADMODEL;
		this.sizeX = J3DCore.CUBE_EDGE_SIZE;
		this.sizeY = J3DCore.CUBE_EDGE_SIZE;
		this.textureName = textureName;
		setId();
	}

	public QuadModel(String textureName,float sizeX, float sizeY) {
		super();
		type = QUADMODEL;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.textureName = textureName;
		setId();
	}
	public QuadModel(String textureName,String dot3TextureName, float sizeX, float sizeY, boolean normalMap) {
		super();
		type = QUADMODEL;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.textureName = textureName;
		this.dot3TextureName = dot3TextureName;
		this.transformToNormal = normalMap;
		setId();
	}
	private void setId()
	{
		this.id = textureName+dot3TextureName+transformToNormal+sizeX+sizeY;
	}
}