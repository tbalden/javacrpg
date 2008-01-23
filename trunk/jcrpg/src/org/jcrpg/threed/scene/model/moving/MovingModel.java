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

package org.jcrpg.threed.scene.model.moving;

import org.jcrpg.threed.scene.model.SimpleModel;

/**
 * Mobile models base
 * @author illes
 *
 */
public class MovingModel extends SimpleModel {

	public String[] additionalModels = null;
	public String animation = null;

	public MovingModel(String modelName, String animation, String[] additionalModels, String textureName, boolean mipMap)
	{
		super(modelName,textureName,mipMap);
		type = MOVINGMODEL;
		this.animation = animation;
		this.id = modelName+textureName+mipMap;
		this.modelName = modelName;
		this.textureName = textureName;
		this.additionalModels = additionalModels;
		this.batchEnabled = false;
		this.shadowCaster = true;
		
	}

}
