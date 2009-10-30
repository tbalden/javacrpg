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

package org.jcrpg.threed.jme;

import java.util.ArrayList;

import org.jcrpg.space.Cube;
import org.jcrpg.threed.scene.RenderedCube;

import com.jme.math.Vector3f;

public class RenderedCubePool {


	static ArrayList<RenderedCube> vector3List = new ArrayList<RenderedCube>();
	
	static int counter = 0;
	static int release = 0;
	public static RenderedCube getInstance(Cube c, int x, int y, int z)
	{
		RenderedCube rc;
		synchronized (vector3List) {
			if (vector3List.size()>0)
			{
				rc = vector3List.remove(0);
			} else
			{
				counter++;
				rc = new RenderedCube();
			}
		} 
		rc.cube = c;
		rc.renderedX = x;
		rc.renderedY = y;
		rc.renderedZ = z;
		return rc;
	
	}
	public static void release(RenderedCube vec)
	{
		release++;
		vec.clear();
		synchronized (vector3List) {
			vector3List.add(vec);
			
		}
	}
	
	public static String getState()
	{
		return "SIZE:  "+vector3List.size()+" NEW COUNT:"+counter+" REL: "+release;
	}
}
