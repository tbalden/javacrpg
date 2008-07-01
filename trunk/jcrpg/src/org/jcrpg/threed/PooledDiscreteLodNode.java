/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.threed;

import org.jcrpg.threed.ModelPool.PoolItemContainer;

import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.lod.DiscreteLodNode;

/**
 * Helper node extension for DiscreteLODNode pooling.
 * @author pali
 */
public class PooledDiscreteLodNode extends DiscreteLodNode implements PooledNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PoolItemContainer cont;
	
	public PooledDiscreteLodNode(String string, DistanceSwitchModel dsm) {
		super(string,dsm);
	}

	public PoolItemContainer getPooledContainer() {
		return cont;
	}

	public void setPooledContainer(PoolItemContainer cont) {
		this.cont = cont;
	}

	
}
