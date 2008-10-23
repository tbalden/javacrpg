/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

/**
 * Simple animation models like Doors, chests etc.
 * @author illes
 *
 */

public class StaticTriggeredModel extends SimpleModel {

	public String[] additionalModels = null;
	public TriggeredAnimDescription animation = null;
		
	
	public boolean animatedModel = false;

	public StaticTriggeredModel(String modelName, TriggeredAnimDescription animation, String[] additionalModels, String textureName, boolean mipMap)
	{
		super(modelName,textureName,mipMap);
		type = STATICTRIGGEREDMODEL;
		if (animation!=null) animatedModel = true;
		this.animation = animation;
		this.id = modelName+textureName+mipMap;
		this.modelName = modelName;
		this.textureName = textureName;
		this.additionalModels = additionalModels;
		this.batchEnabled = false;
		this.shadowCaster = true;
		
	}

}
