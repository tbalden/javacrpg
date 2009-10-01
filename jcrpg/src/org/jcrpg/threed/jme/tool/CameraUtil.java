/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.threed.jme.tool;

import org.jcrpg.threed.input.action.CKeyAction;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;


/**
 * Utility for programming camera movement/direction.
 * @author illes
 *
 */
public class CameraUtil {

	
	
	public CameraProgram currentProgram = null;
	
	public void startProgram(CameraProgram program)
	{
		if (currentProgram!=null) currentProgram.stop();
		currentProgram = program;
		currentProgram.start();
	}
	
	public void update(float timePerFrame)
	{
		if (currentProgram!=null) 
		{
			if (currentProgram.update(timePerFrame))
			{
				currentProgram = null;
			}
		}
	}
	
	
}
