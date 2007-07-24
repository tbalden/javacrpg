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
import org.jcrpg.util.HashUtil;

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
import com.jme.scene.state.LightState;

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
			target.setLightCombineMode(LightState.INHERIT);
			node.setLightCombineMode(LightState.INHERIT);
			this.attachChild(node);
		} else if ((target.getType() & SceneElement.TRIMESH) != 0) {
			SharedMesh node = new SharedMesh("SharedMesh", (TriMesh) target);
			node.setLocalTranslation(translation);
			node.setLocalScale(scale);
			node.setLocalRotation(rotation);
			target.setLightCombineMode(LightState.INHERIT);
			node.setLightCombineMode(LightState.INHERIT);
			Node n = new Node();			
			n.attachChild(node);
			n.setLightCombineMode(LightState.INHERIT);
			
			this.attachChild(n);
		}
	}
	
	boolean windSwitch = true;
	Vector3f origTranslation = null;
	long passedTime = System.currentTimeMillis();
	long sysTimer = System.currentTimeMillis();
	
	public float windPower = 6f; 

	public void draw(Renderer r) {
		if (origTranslation == null) origTranslation = this.getLocalTranslation();
		passedTime = System.currentTimeMillis()-sysTimer;
		if (windSwitch)
        {
			if (passedTime>500/windPower) {
				setLocalTranslation(origTranslation.add(new Vector3f(0.009f*(HashUtil.mixPercentage((int)sysTimer,0,0)/1000f)*windPower,0,0.009f*(HashUtil.mixPercentage((int)sysTimer+100,0,0)/1000f)*windPower)));
				sysTimer = System.currentTimeMillis();
				windSwitch = false;
			}
        }
		else
		{
			if (passedTime>500/windPower) {
				setLocalTranslation(origTranslation.subtract(new Vector3f(0,0,0)));
				sysTimer = System.currentTimeMillis();
				windSwitch = true;
			}
		}
		if (children == null) {
			return;
		}
		Spatial child;
		for (int i = 0, cSize = children.size(); i < cSize; i++) {
			child = children.get(i);
			if (child != null) {
				//child.updateRenderState();
		        //worldRotation.fromRotationMatrix(orient);
				//Matrix3f rot = new Matrix3f();
				//rot.fromAxes(cam.getLeft(),cam.getUp(),cam.getDirection());
				//child.setLocalRotation(rot);//BOTTOM);
				float distSquared = tmpVec.set(cam.getLocation())
						.subtractLocal(child.getLocalTranslation())
						.lengthSquared();
				if (distSquared <= viewDistance * viewDistance) {
					r.setCamera(cam);
					child.onDraw(r);
				}
			}
		}
	}

	public float getWindPower() {
		return windPower;
	}

	public void setWindPower(float windPower) {
		this.windPower = windPower;
	}
}