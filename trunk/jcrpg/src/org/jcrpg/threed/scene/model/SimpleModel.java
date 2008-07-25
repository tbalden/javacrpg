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


public class SimpleModel extends Model {

	public String modelName, textureName;
	public boolean mipMap = true;
	public int xGeomBatchSize = -1;
	public int yGeomBatchSize = -1;
	
	public boolean generatedGroundModel = false;
	
	
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
}
