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

import org.jcrpg.threed.J3DCore;

public class QuadModel extends Model {

	public float sizeX;
	public float sizeY;
	public String textureName;
	// bump/normal map texture
	public String dot3TextureName;
	// tells if bump map texture needs transformation with SobelImageFilter from grayscale.
	public boolean transformToNormal = false;

	public QuadModel(String textureName) {
		super();
		this.sizeX = J3DCore.CUBE_EDGE_SIZE;
		this.sizeY = J3DCore.CUBE_EDGE_SIZE;
		this.textureName = textureName;
		type = QUADMODEL;
		setId();
	}

	public QuadModel(String textureName,float sizeX, float sizeY) {
		super();
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.textureName = textureName;
		setId();
	}
	public QuadModel(String textureName,String dot3TextureName, float sizeX, float sizeY, boolean normalMap) {
		super();
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
