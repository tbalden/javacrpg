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

public class TextureStateModel extends Model{

	public String[] textureName;
	public float quadSizeX = 0.9f;
	public float quadSizeY = 0.5f;
	public int quadQuantity = 3;
	public float quadSeparation = 0.64f;

	public TextureStateModel(String[] textureName, float quadSizeX, float quadSizeY, int quadQuantity, float quadSeparation) {
		super();
		this.textureName = textureName;
		this.quadSizeX = quadSizeX;
		this.quadSizeY = quadSizeY;
		this.quadQuantity = quadQuantity;
		this.quadSeparation = quadSeparation;
	}

	public TextureStateModel(String[] textureName) {
		super();
		this.textureName = textureName;
	}
	
}
