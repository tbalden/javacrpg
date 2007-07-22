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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MrCoder
 */
public class HashMapVegetation extends AbstractVegetation {
	private class HashMapTargetData {
		public Spatial target;

		public Spatial lodTarget;

		public BoundingVolume bounding = new BoundingBox(new Vector3f(0, 0, 0),
				1.0f, 1.0f, 1.0f);

		public int nrObjects = 0;

		public ArrayList<HashMapSpatialData> targetPosition = new ArrayList<HashMapSpatialData>();

		public HashMap<String, ArrayList<HashMapSpatialData>> buckets = new HashMap<String, ArrayList<HashMapSpatialData>>();
	}

	private class HashMapSpatialData {
		public Vector3f targetTranslation;

		public Vector3f targetScale;

		public Quaternion targetRotation;
	}

	private ArrayList<HashMapTargetData> targetSpatials = new ArrayList<HashMapTargetData>();

	private int totalNrObjects = 0;

	private Vector3f tmpVec = new Vector3f();

	private int xoffsets[] = { -1, 0, 1, -1, 0, 1, -1, 0, 1 };

	private int zoffsets[] = { -1, -1, -1, 0, 0, 0, 1, 1, 1 };

	public HashMapVegetation(String string, Camera cam, float viewDistance) {
		super(string, cam, viewDistance);
	}

	private HashMapTargetData addTarget(Spatial target, Spatial lodTarget) {
		target.setCullMode(Spatial.CULL_NEVER);

		HashMapTargetData hashMapTargetData = new HashMapTargetData();
		hashMapTargetData.target = target;
		attachChild(target);

		if (target instanceof TriMesh) {
			TriMesh mesh = (TriMesh) target;
			hashMapTargetData.bounding.computeFromPoints(mesh
					.getVertexBuffer(0));
		}

		if (lodTarget != null) {
			hashMapTargetData.lodTarget = lodTarget;
			attachChild(lodTarget);
		}

		targetSpatials.add(hashMapTargetData);

		return hashMapTargetData;
	}

	public void addVegetationObject(Spatial target, Vector3f translation,
			Vector3f scale, Quaternion rotation) {
		HashMapTargetData hashMapTargetData = null;
		for (int i = 0; i < targetSpatials.size(); i++) {
			Spatial spatialTarget = targetSpatials.get(i).target;
			if (spatialTarget.equals(target)) {
				hashMapTargetData = targetSpatials.get(i);
				break;
			}
		}
		if (hashMapTargetData == null) {
			hashMapTargetData = addTarget(target, null);
		}
		HashMapSpatialData hashMapSpatialData = new HashMapSpatialData();
		hashMapSpatialData.targetTranslation = translation;
		hashMapSpatialData.targetScale = scale;
		hashMapSpatialData.targetRotation = rotation;

		hashMapTargetData.targetPosition.add(hashMapSpatialData);
		hashMapTargetData.nrObjects++;
		totalNrObjects++;
	}

	public void setup() {
		sortBuckets();
		System.out.println("Vegetation count: " + getTotalNrObjects());

		updateRenderState();
		updateGeometricState(0.0f, true);

		setRenderQueueMode(com.jme.renderer.Renderer.QUEUE_SKIP);
		lockMeshes();
		lockShadows();
	}

	public void sortBuckets() {
		for (int index = 0; index < targetSpatials.size(); index++) {
			HashMapTargetData hashMapTargetData = targetSpatials.get(index);

			ArrayList<HashMapSpatialData> targetPosition = hashMapTargetData.targetPosition;

			int nrObjects = hashMapTargetData.nrObjects;
			for (int i = nrObjects - 1; i >= 0; i--) {
				HashMapSpatialData hashMapSpatialData = targetPosition
						.remove(i);
				Vector3f translation = hashMapSpatialData.targetTranslation;

				int x = Math.round(translation.x / viewDistance);
				int z = Math.round(translation.z / viewDistance);
				String key = x + "_" + z;

				if (hashMapTargetData.buckets.containsKey(key)) {
					ArrayList<HashMapSpatialData> positions = hashMapTargetData.buckets
							.get(key);
					positions.add(hashMapSpatialData);
				} else {
					ArrayList<HashMapSpatialData> positions = new ArrayList<HashMapSpatialData>();
					positions.add(hashMapSpatialData);
					hashMapTargetData.buckets.put(key, positions);
				}
			}
		}
	}

	public void draw(Renderer r) {
		r.renderQueue();

		drawBuckets(r);
	}

	public void drawBuckets(Renderer r) {
		int savedPlaneState = cam.getPlaneState();

		Vector3f camLocation = cam.getLocation();

		for (int index = 0; index < targetSpatials.size(); index++) {
			HashMapTargetData hashMapTargetData = targetSpatials.get(index);

			Spatial target = hashMapTargetData.target;
			BoundingVolume bounding = hashMapTargetData.bounding;

			int x = Math.round(camLocation.x / viewDistance);
			int z = Math.round(camLocation.z / viewDistance);

			for (int bucketCount = 0; bucketCount < 9; bucketCount++) {
				int xx = x + xoffsets[bucketCount];
				int zz = z + zoffsets[bucketCount];
				String key = xx + "_" + zz;

				if (!hashMapTargetData.buckets.containsKey(key)) {
					continue;
				}

				ArrayList<HashMapSpatialData> targetPosition = hashMapTargetData.buckets
						.get(key);

				int nrObjects = targetPosition.size();
				for (int i = 0; i < nrObjects; i++) {
					Vector3f translation = targetPosition.get(i).targetTranslation;

					float distSquared = tmpVec.set(camLocation).subtractLocal(
							translation).lengthSquared();
					if (distSquared > viewDistance * viewDistance) {
						continue;
					}

					cam.setPlaneState(0);
					bounding.getCenter().set(translation);
					if (cam.contains(bounding) != Camera.OUTSIDE_FRUSTUM) {
						target.getWorldTranslation().set(translation);
						target.getWorldScale().set(
								targetPosition.get(i).targetScale);
						target.getWorldRotation().set(
								targetPosition.get(i).targetRotation);
						target.draw(r);
					}
				}
			}
		}

		cam.setPlaneState(savedPlaneState);
	}

	public void updateWorldBound() {
		//        super.updateWorldBound();
	}

	public void onDraw(Renderer r) {
		int cm = getCullMode();
		if (cm == CULL_ALWAYS) {
			return;
		}

		draw(r);
	}

	public int getTotalNrObjects() {
		return totalNrObjects;
	}
}
