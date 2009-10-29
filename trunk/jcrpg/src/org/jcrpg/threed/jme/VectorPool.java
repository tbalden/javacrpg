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

import com.jme.math.Vector3f;

public class VectorPool {

	static ArrayList<Vector3f> vector3List = new ArrayList<Vector3f>();
	
	public static Vector3f getVector3f()
	{
		synchronized (vector3List)
		{
			if (vector3List.size()>0)
				return vector3List.remove(0);
			return new Vector3f();
		}
	}
	public static Vector3f getVector3f(float x, float y, float z)
	{
		synchronized (vector3List)
		{
			Vector3f r;
			if (vector3List.size()>0)
				r = vector3List.remove(0);
			r = new Vector3f();
			r.set(x, y, z);
			return r;
		}
	}
	public static void releaseVector3f(Vector3f vec)
	{
		synchronized (vector3List) {
			vector3List.add(vec);
		}
	}
	
}
