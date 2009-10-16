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

package org.jcrpg.ui;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.geometryinstancing.BufferPool;
import org.jcrpg.threed.jme.geometryinstancing.ExactBufferPool;

public class SimpleKeyEvents implements KeyListener {

	public boolean handleKey(String key) {
		if (key.equals("torch"))
		{
			J3DCore.getInstance().switchPlayerTorchLight();
			return true;
		}
		if (key.equals("poolInfo"))
		{
			
			System.out.println(BufferPool.getBufferInfo()+" "+ExactBufferPool.getBufferInfo());
			J3DCore.getInstance().switchPoolInfo();
			return true;
		}		/*if (key.equals("torch"))
		{
			return true;
		}*/
		return false;
	}

}