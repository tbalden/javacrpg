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
package org.jcrpg.threed.scene.model.effect;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.jme.program.EffectNode;

public class EffectProgram {

	Class<? extends EffectNode> visualForm;

	public static int PT_MOVE_FROM_SOURCE_TO_TARGET = 0;

	public int programType = PT_MOVE_FROM_SOURCE_TO_TARGET;
	
	public EffectProgram(Class<? extends EffectNode> visualForm)
	{
		this.visualForm = visualForm;
	}
	
	public EffectNode get3DVisualization()
	{
		try {
			return visualForm.newInstance();
		} catch (Exception ex)
		{
			Jcrpg.LOGGER.warning(ex.toString());
			ex.printStackTrace();
			return null;
		}
	}
	
}
