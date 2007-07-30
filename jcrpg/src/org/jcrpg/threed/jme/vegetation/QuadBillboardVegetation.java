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

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;

import com.jme.math.FastMath;
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
import com.jme.scene.batch.TriangleBatch;

/**
 * Fully billboarded quad vegetation.
 * 
 * @author pali
 * 
 */
public class QuadBillboardVegetation extends AbstractVegetation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3942670677329376647L;

	private Vector3f tmpVec = new Vector3f();

	public QuadBillboardVegetation(String string, J3DCore core, Camera cam, float viewDistance) {
		super(string, core, cam, viewDistance);
	}

	public void addVegetationObject(Spatial target, Vector3f translation, Vector3f scale, Quaternion rotation) {
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

	boolean windSwitch = true;

	Vector3f origTranslation = null;

	long passedTime = 0;

	float timeCounter = 0;

	long startTime = System.currentTimeMillis();

	long timeCountStart = System.currentTimeMillis();

	public float windPower = 0.5f;

	public static final float TIME_DIVIDER = 400;

	public static final long TIME_LIMIT = 0;

	float diffs[] = new float[5];

	float newDiffs[] = new float[5];

	public void draw(Renderer r) {
		if (origTranslation == null)
			origTranslation = this.getLocalTranslation();
		passedTime = System.currentTimeMillis() - startTime;

		boolean doGrassMove = false;
		if (System.currentTimeMillis() - timeCountStart > TIME_LIMIT && J3DCore.CPU_ANIMATED_GRASS) {
			doGrassMove = true;
			timeCountStart = System.currentTimeMillis();
		}

		if (children == null) {
			return;
		}

		float diff = 0;

		if (doGrassMove) {
			// creating 5 diffs to look random
			diff = 0.059f * FastMath.sin(((passedTime / TIME_DIVIDER) * windPower) % FastMath.PI) * windPower;
			newDiffs[0] = diff;
			diff = 0.059f * FastMath.sin((((passedTime + 500) / TIME_DIVIDER) * windPower * (0.5f)) % FastMath.PI)
					* windPower;
			newDiffs[1] = diff;
			diff = 0.059f * FastMath.sin((((passedTime + 500) / TIME_DIVIDER) * windPower * (0.6f)) % FastMath.PI)
					* windPower;
			newDiffs[2] = diff;
			diff = 0.059f * FastMath.sin((((passedTime + 1000) / TIME_DIVIDER) * windPower * (0.8f)) % FastMath.PI)
					* windPower;
			newDiffs[3] = diff;
			diff = 0.059f * FastMath.sin((((passedTime + 2000) / TIME_DIVIDER) * windPower * (0.7f)) % FastMath.PI)
					* windPower;
			newDiffs[4] = diff;
		}
		if (newDiffs[0] == diffs[0]) {
			doGrassMove = false;
		} else {
			diffs = newDiffs;
			newDiffs = new float[5];
		}

		Spatial child;
		for (int i = 0, cSize = children.size(); i < cSize; i++) {
			int whichDiff = 0;
			if (J3DCore.CPU_ANIMATED_GRASS)
				whichDiff = i % 5;

			child = children.get(i);

			// billboard world rotation calc
			Vector3f look = cam.getDirection().negate();
			// coopt loc for our left direction:
			Vector3f left1 = cam.getLeft().negate();
			Quaternion orient = new Quaternion();
			orient.fromAxes(left1, cam.getUp(), look);

			if (child != null) {
				float distSquared = tmpVec.set(cam.getLocation()).subtractLocal(child.getWorldTranslation())
						.lengthSquared();
				if (distSquared <= viewDistance * viewDistance)
					if (distSquared < 3 * 3
							|| HashUtil.mixPercentage(i, 0, 0) + 10 > (distSquared / (viewDistance * viewDistance)) * 100) {
						// original code
						r.setCamera(cam);
						child.updateGeometricState(0.0f, false);
						child.onDraw(r);

						// animation

						if (child.getType() == Node.NODE) {
							Node n = (Node) child;
							ArrayList<Spatial> c2 = n.getChildren();
							for (Spatial s : c2) {
								// if (s instanceof TriMesh)
								{
									TriMesh q = (TriMesh) s;
									q.getWorldRotation().set(orient); // BILLBOARDING

									if (!(doGrassMove))
										continue;
									// CPU computed grass moving
									TriangleBatch b = q.getBatch(0);
									FloatBuffer fb = b.getVertexBuffer();
									for (int fIndex = 0; fIndex < 4 * 3; fIndex++) {
										boolean f2_1Read = false;
										boolean f2_2Read = false;
										float f2_1 = 0;
										float f2_2 = 0;
										if (FastMath.floor(fIndex / 3) == 0 || FastMath.floor(fIndex / 3) == 3) {
											int mul = 1;
											if (FastMath.floor(fIndex / 3) == 3)
												mul = -1;
											if (fIndex % 3 == 0) {
												// float f = fb.get(fIndex);
												if (!f2_1Read) {
													f2_1 = fb.get(fIndex + 3 * mul);
													f2_1Read = true;
												}
												fb.put(fIndex, f2_1 + diffs[whichDiff]);
											}
											if (fIndex % 3 == 2) {
												// float f = fb.get(fIndex);
												if (!f2_2Read) {
													f2_2 = fb.get(fIndex + 3 * mul);
													f2_2Read = true;
												}
												fb.put(fIndex, f2_2 + diffs[whichDiff]);
											}
										}
									}

								}
							}
						}

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