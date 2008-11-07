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

package org.jcrpg.threed.jme.vegetation;

import org.jcrpg.threed.J3DCore;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

public abstract class AbstractVegetation extends Node {
	protected Camera cam;
	protected J3DCore core;	

	protected float viewDistance;

	public AbstractVegetation(String string, J3DCore core, Camera cam, float viewDistance) {
		super(string);
		this.cam = cam;
		this.core = core;
		this.viewDistance = viewDistance;
	}

	public void initialize() {
	}

	public abstract void addVegetationObject(Spatial target,
			Vector3f translation, Vector3f scale, Quaternion rotation);

	public void setup() {
	}
}