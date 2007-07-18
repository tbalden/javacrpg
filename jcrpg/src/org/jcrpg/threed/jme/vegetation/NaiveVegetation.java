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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedMesh;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;

public class NaiveVegetation extends AbstractVegetation {
	private Vector3f tmpVec = new Vector3f();

	public NaiveVegetation(String string, Camera cam, float viewDistance) {
		super(string, cam, viewDistance);
	}

	public void addVegetationObject(Spatial target, Vector3f translation,
			Vector3f scale, Quaternion rotation) {
		if ((target.getType() & SceneElement.NODE) != 0) {
			SharedNode node = new SharedNode("SharedNode", (Node) target);
			node.setLocalTranslation(translation);
			node.setLocalScale(scale);
			node.setLocalRotation(rotation);
			this.attachChild(node);
		} else if ((target.getType() & SceneElement.TRIMESH) != 0) {
			SharedMesh node = new SharedMesh("SharedMesh", (TriMesh) target);
			node.setLocalTranslation(translation);
			node.setLocalScale(scale);
			node.setLocalRotation(rotation);
			this.attachChild(node);
		}
	}

	public void draw(Renderer r) {
		if (children == null) {
			return;
		}
		Spatial child;
		for (int i = 0, cSize = children.size(); i < cSize; i++) {
			child = children.get(i);
			if (child != null) {
				float distSquared = tmpVec.set(cam.getLocation())
						.subtractLocal(child.getLocalTranslation())
						.lengthSquared();
				if (distSquared <= viewDistance * viewDistance) {
					child.onDraw(r);
				}
			}
		}
	}
}