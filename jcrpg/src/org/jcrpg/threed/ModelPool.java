/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.threed;

import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.Model;

public class ModelPool {

	public J3DCore core;
	
	public class PoolItemContainer {
		public HashSet<PooledNode> used = new HashSet<PooledNode>();
		public HashSet<PooledNode> notUsed = new HashSet<PooledNode>();
	}
	
	public ModelPool(J3DCore core)
	{
		this.core = core;
	}

	public static HashMap<String, PoolItemContainer> pool = new HashMap<String, PoolItemContainer>();
	
	public PooledNode getModel(RenderedCube rc, Model model)
	{
		PoolItemContainer cont = pool.get(model.id);
		if (cont==null)
		{
			cont = new PoolItemContainer(); 
			pool.put(model.id, cont);
		} else {
			synchronized (cont.notUsed) {
				if (cont.notUsed.iterator().hasNext())
				{
					//System.out.println("++ FROM POOL MODEL!"+model.id);
					PooledNode n = cont.notUsed.iterator().next();
					cont.notUsed.remove(n);
					cont.used.add(n);
					return n;
				}
			}
		}
		PooledNode n = core.modelLoader.loadObject(rc, model);
		//System.out.println("LOADING MODEL!"+model.id);
		n.setPooledContainer(cont);
		cont.used.add(n);
		return n;
	}
	
	public void releaseNode(PooledNode n)
	{
		n.getPooledContainer().used.remove(n);
		n.getPooledContainer().used.add(n);
	}
	
	public PooledNode[] loadObjects(RenderedCube cube,Model[] objects,boolean fakeLoadForCacheMaint)
	{
		if (fakeLoadForCacheMaint) return null; 
		PooledNode[] retNodes = new PooledNode[objects.length];
		int count = 0;
		for (Model objModel : objects)
		{
			retNodes[count++]=getModel(cube, objModel);
		}
		return retNodes;
		
	}
	
}
