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