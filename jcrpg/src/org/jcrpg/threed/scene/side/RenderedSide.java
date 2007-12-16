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
	public static final int RS_CLIMATEDEPENDENT = 5;
	
	
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
