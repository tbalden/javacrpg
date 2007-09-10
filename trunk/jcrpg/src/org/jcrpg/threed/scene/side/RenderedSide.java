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

package org.jcrpg.threed.scene.side;

import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.SimpleModel;


public class RenderedSide {

	public static final int RS_SIDE = 0;
	public static final int RS_HASHROTATED = 1;
	public static final int RS_HASHALTERED = 2;
	public static final int RS_TOPSIDE = 3;
	public static final int RS_CONTINUOUS = 4;
	
	
	public int type = RS_SIDE;
	
	
	public Model[] objects;
	
	public RenderedCube parent = null;
	
	public RenderedSide(Model[] objects)
	{
		this.objects = objects;
	}
	public RenderedSide(String modelName, String textureName)
	{
		objects = new SimpleModel[] {new SimpleModel(modelName,textureName)};
	}
	
	
	
	
	public RenderedCube getParent() {
		return parent;
	}
	public void setParent(RenderedCube parent) {
		this.parent = parent;
	}
	
	
	
}
