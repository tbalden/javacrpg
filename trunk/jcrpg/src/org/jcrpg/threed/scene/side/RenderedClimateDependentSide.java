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
package org.jcrpg.threed.scene.side;

import java.util.HashMap;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.util.HashUtil;


/**
 * coordinate hash altered objects
 * @author pali
 *
 */
public class RenderedClimateDependentSide extends RenderedSide {

	public boolean scaleFix = false;
	
	HashMap<String, Model[]> hmModels = new HashMap<String, Model[]>();
	
	Model[] fallback = null;
	
	/**
	 * @param objects Objects always rendered.
	 */
	public RenderedClimateDependentSide(Model[] objects, Model[] fallback, HashMap<String, Model[]>hmModels)
	{
		super(objects);
		this.hmModels = hmModels;
		this.fallback = fallback;
		type = RS_CLIMATEDEPENDENT;
	}
	
	public Model[] getRenderedModels(String climateId)
	{
		Model[] models = hmModels.get(climateId);
		if (models==null) return fallback;
		return models;
	}
	
	public float scale(int x,int y,int z)
	{
		if (scaleFix) return 1f;
		return 1f+(HashUtil.mix(x, y, z)%100)*0.003f;
	}
}
