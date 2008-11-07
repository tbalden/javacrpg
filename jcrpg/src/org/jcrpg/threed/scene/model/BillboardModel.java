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

package org.jcrpg.threed.scene.model;


public class BillboardModel extends SimpleModel {

	public BillboardModel(String modelName, String textureName) {
		super(modelName, textureName,false);
		elevateOnSteep = true;
	}
	public BillboardModel(String modelName, String textureName,boolean mipMap) {
		super(modelName, textureName,mipMap);
		elevateOnSteep = true;
	}

}
