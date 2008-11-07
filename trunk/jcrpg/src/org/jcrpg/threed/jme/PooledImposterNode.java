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

import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.ModelPool.PoolItemContainer;

import com.jme.scene.ImposterNode;

public class PooledImposterNode extends ImposterNode implements PooledNode {
	
	public PooledImposterNode() {
		super();
	}

	public PooledImposterNode(String arg0, float arg1, int arg2, int arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public org.jcrpg.threed.ModelPool.PoolItemContainer cont;

	public PoolItemContainer getPooledContainer() {
		return cont;
	}

	public void setPooledContainer(PoolItemContainer cont) {
		this.cont = cont;
	}

	public void update(NodePlaceholder place) {
		// TODO Auto-generated method stub
		
	}

}
