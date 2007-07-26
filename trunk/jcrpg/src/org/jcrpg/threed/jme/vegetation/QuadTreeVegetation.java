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
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.AbstractCamera;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Copyright by MrCoder at jme
 */
public class QuadTreeVegetation extends AbstractVegetation {
	private class QuadTreeTargetData {
		public Spatial target;

		public Spatial lodTarget;

		public BoundingVolume bounding = new BoundingBox(new Vector3f(0, 0, 0),
				1.0f, 1.0f, 1.0f);

		public int nrObjects = 0;

		public ArrayList<QuadTreeSpatialData> targetPosition = new ArrayList<QuadTreeSpatialData>();

		public HashMap<String, ArrayList<QuadTreeSpatialData>> buckets = new HashMap<String, ArrayList<QuadTreeSpatialData>>();

		public QuadTreeNode topNode;
	}

	private class QuadTreeSpatialData {
		public Vector3f targetTranslation;

		public Vector3f targetScale;

		public Quaternion targetRotation;
	}

	private class QuadTreeNode {
		public static final int TYPE_NODE = 0;

		public static final int TYPE_LEAF = 1;

		public int quadTreeType = QuadTreeNode.TYPE_NODE;

		public BoundingBox bounding;

		public ArrayList<QuadTreeSpatialData> positions;

		public QuadTreeNode parent;

		public QuadTreeNode[] children = new QuadTreeNode[4];

		public float minY;

		public float maxY;
	}

	private final int QUADTREE_DEPTH = 7;

	private ArrayList<QuadTreeTargetData> targetSpatials = new ArrayList<QuadTreeTargetData>();

	private int totalNrObjects = 0;

	private Camera frustumCamera;

	private Vector3f tmpVec = new Vector3f();

	public QuadTreeVegetation(String string, Camera cam, float viewDistance) {
		super(string, null, cam, viewDistance);

		DisplaySystem display = DisplaySystem.getDisplaySystem();
		frustumCamera = new CheckCam(display.getWidth(), display.getHeight());

		frustumCamera.setFrustumPerspective(45.0f, (float) display.getWidth()
				/ (float) display.getHeight(), 1f, this.viewDistance);
		frustumCamera.setParallelProjection(false);
		frustumCamera.update();
	}

	private QuadTreeTargetData addTarget(Spatial target, Spatial lodTarget) {
		target.setCullMode(Spatial.CULL_NEVER);

		QuadTreeTargetData quadTreeTargetData = new QuadTreeTargetData();
		quadTreeTargetData.target = target;
		attachChild(target);

		if (target instanceof TriMesh) {
			TriMesh mesh = (TriMesh) target;
			quadTreeTargetData.bounding.computeFromPoints(mesh
					.getVertexBuffer(0));
		}

		if (lodTarget != null) {
			quadTreeTargetData.lodTarget = lodTarget;
			attachChild(lodTarget);
		}

		targetSpatials.add(quadTreeTargetData);

		return quadTreeTargetData;
	}

	public void addVegetationObject(Spatial target, Vector3f translation,
			Vector3f scale, Quaternion rotation) {
		QuadTreeTargetData quadTreeTargetData = null;
		for (int i = 0; i < targetSpatials.size(); i++) {
			Spatial spatialTarget = targetSpatials.get(i).target;
			if (spatialTarget.equals(target)) {
				quadTreeTargetData = targetSpatials.get(i);
				break;
			}
		}
		if (quadTreeTargetData == null) {
			quadTreeTargetData = addTarget(target, null);
		}
		QuadTreeSpatialData quadTreeSpatialData = new QuadTreeSpatialData();
		quadTreeSpatialData.targetTranslation = translation;
		quadTreeSpatialData.targetScale = scale;
		quadTreeSpatialData.targetRotation = rotation;

		quadTreeTargetData.targetPosition.add(quadTreeSpatialData);
		quadTreeTargetData.nrObjects++;
		totalNrObjects++;
	}

	public void setup() {
		createQuadTrees();
		System.out.println("Vegetation count: " + getTotalNrObjects());

		updateRenderState();
		updateGeometricState(0.0f, true);

		setRenderQueueMode(com.jme.renderer.Renderer.QUEUE_SKIP);
		lockMeshes();
		lockShadows();
	}

	public void createQuadTrees() {
		for (int index = 0; index < targetSpatials.size(); index++) {
			QuadTreeTargetData quadTreeTargetData = targetSpatials.get(index);

			ArrayList<QuadTreeSpatialData> targetPosition = quadTreeTargetData.targetPosition;

			int nrObjects = quadTreeTargetData.nrObjects;

			// find outer bounds
			float minX = Float.MAX_VALUE;
			float maxX = Float.MIN_VALUE;
			float minY = Float.MAX_VALUE;
			float maxY = Float.MIN_VALUE;
			float minZ = Float.MAX_VALUE;
			float maxZ = Float.MIN_VALUE;
			for (int i = 0; i < nrObjects; i++) {
				QuadTreeSpatialData quadTreeSpatialData = targetPosition.get(i);
				Vector3f translation = quadTreeSpatialData.targetTranslation;

				if (translation.x < minX) {
					minX = translation.x;
				} else if (translation.x > maxX) {
					maxX = translation.x;
				}
				if (translation.y < minY) {
					minY = translation.y;
				} else if (translation.y > maxY) {
					maxY = translation.y;
				}
				if (translation.z < minZ) {
					minZ = translation.z;
				} else if (translation.z > maxZ) {
					maxZ = translation.z;
				}
			}

			// sort into quadtree
			Vector3f boundingExtents = new Vector3f(
					(maxX - minX) / 2.0f + 1.0f, (maxY - minY) / 2.0f + 1.0f,
					(maxZ - minZ) / 2.0f + 1.0f);
			Vector3f boundingOrigin = new Vector3f((maxX + minX) / 2.0f,
					(maxY + minY) / 2.0f, (maxZ + minZ) / 2.0f);
			quadTreeTargetData.topNode = new QuadTreeNode();
			quadTreeTargetData.topNode.bounding = new BoundingBox(
					boundingOrigin, boundingExtents.x, boundingExtents.y,
					boundingExtents.z);

			for (int i = nrObjects - 1; i >= 0; i--) {
				QuadTreeSpatialData quadTreeSpatialData = targetPosition
						.remove(i);

				sortIntoQuad(quadTreeTargetData.topNode, quadTreeSpatialData,
						QUADTREE_DEPTH - 1);
			}
		}
	}

	private void sortIntoQuad(QuadTreeNode parentNode,
			QuadTreeSpatialData quadTreeSpatialData, int depth) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				BoundingBox parentBounding = parentNode.bounding;
				Vector3f parentCenter = parentBounding.getCenter();

				float xCenter = parentCenter.x
						+ (parentBounding.xExtent * i * 2 - parentBounding.xExtent)
						/ 2.0f;
				float zCenter = parentCenter.z
						+ (parentBounding.zExtent * j * 2 - parentBounding.zExtent)
						/ 2.0f;
				float xExtent = parentBounding.xExtent / 2.0f;
				float zExtent = parentBounding.zExtent / 2.0f;

				if (isInside(xCenter, zCenter, xExtent, zExtent,
						quadTreeSpatialData.targetTranslation)) {

					QuadTreeNode childNode = parentNode.children[j * 2 + i];
					if (childNode == null) {
						childNode = new QuadTreeNode();
						Vector3f boundingOrigin = new Vector3f(xCenter, 0,
								zCenter);
						childNode.bounding = new BoundingBox(boundingOrigin,
								xExtent, 0, zExtent);
						parentNode.children[j * 2 + i] = childNode;

						childNode.minY = childNode.maxY = quadTreeSpatialData.targetTranslation.y;
					} else {
						if (quadTreeSpatialData.targetTranslation.y < childNode.minY) {
							childNode.minY = quadTreeSpatialData.targetTranslation.y;
						} else if (quadTreeSpatialData.targetTranslation.y > childNode.maxY) {
							childNode.maxY = quadTreeSpatialData.targetTranslation.y;
						}

						childNode.bounding.getCenter().y = (childNode.maxY + childNode.minY) / 2.0f;
						childNode.bounding.yExtent = (childNode.maxY - childNode.minY) / 2.0f;
					}

					if (depth > 0) {
						sortIntoQuad(childNode, quadTreeSpatialData, depth - 1);
					} else {
						if (childNode.positions == null) {
							childNode.positions = new ArrayList<QuadTreeSpatialData>();

							childNode.bounding.getCenter().y = quadTreeSpatialData.targetTranslation.y;
							childNode.bounding.yExtent = 1;

							childNode.minY = childNode.maxY = quadTreeSpatialData.targetTranslation.y;
						} else {
							if (quadTreeSpatialData.targetTranslation.y < childNode.minY) {
								childNode.minY = quadTreeSpatialData.targetTranslation.y;
							} else if (quadTreeSpatialData.targetTranslation.y > childNode.maxY) {
								childNode.maxY = quadTreeSpatialData.targetTranslation.y;
							}

							childNode.bounding.getCenter().y = (childNode.maxY + childNode.minY) / 2.0f;
							childNode.bounding.yExtent = (childNode.maxY - childNode.minY) / 2.0f;
						}
						childNode.positions.add(quadTreeSpatialData);
						childNode.quadTreeType = QuadTreeNode.TYPE_LEAF;
					}

					return;
				}
			}
		}
	}

	private boolean isInside(float xCenter, float zCenter, float xExtent,
			float zExtent, Vector3f position) {
		return position.x >= xCenter - xExtent
				&& position.x < xCenter + xExtent
				&& position.z >= zCenter - zExtent
				&& position.z < zCenter + zExtent;
	}

	public void draw(Renderer r) {
		r.renderQueue();

		frustumCamera.getLocation().set(cam.getLocation());
		frustumCamera.getLeft().set(cam.getLeft());
		frustumCamera.getUp().set(cam.getUp());
		frustumCamera.getDirection().set(cam.getDirection());
		frustumCamera.update();
		drawQuadTree(r);
	}

	public void drawQuadTree(Renderer r) {
		int savedPlaneState = cam.getPlaneState();

		for (int index = 0; index < targetSpatials.size(); index++) {
			QuadTreeTargetData quadTreeTargetData = targetSpatials.get(index);

			Spatial target = quadTreeTargetData.target;
			checkQuadTreeAndDraw(quadTreeTargetData.topNode, target, r);
		}

		cam.setPlaneState(savedPlaneState);
	}

	private void checkQuadTreeAndDraw(QuadTreeNode parentNode, Spatial target,
			Renderer r) {
		frustumCamera.setPlaneState(0);
		parentNode.bounding.setCheckPlane(0);
		int checkState = frustumCamera.contains(parentNode.bounding);
		if (checkState == Camera.OUTSIDE_FRUSTUM) {
			return;
		} else if (checkState == Camera.INSIDE_FRUSTUM
				|| parentNode.quadTreeType == QuadTreeNode.TYPE_LEAF) {
			Vector3f camLocation = frustumCamera.getLocation();
			drawSubQuadTree(parentNode, camLocation, target, r);
		} else if (checkState == Camera.INTERSECTS_FRUSTUM
				&& parentNode.quadTreeType == QuadTreeNode.TYPE_NODE) {
			for (int i = 0; i < 4; i++) {
				QuadTreeNode childNode = parentNode.children[i];
				if (childNode != null) {
					checkQuadTreeAndDraw(childNode, target, r);
				}
			}
		}
	}

	private BoundingVolume tmpBounding = new BoundingBox(new Vector3f(0, 0, 0),
			1.0f, 1.0f, 1.0f);

	private void drawSubQuadTree(QuadTreeNode parentNode, Vector3f camLocation,
			Spatial target, Renderer r) {

		if (parentNode.quadTreeType == QuadTreeNode.TYPE_NODE) {
			for (int i = 0; i < 4; i++) {
				QuadTreeNode childNode = parentNode.children[i];
				if (childNode != null) {
					drawSubQuadTree(childNode, camLocation, target, r);
				}
			}
			return;
		}

		ArrayList<QuadTreeSpatialData> targetPosition = parentNode.positions;
		int nrObjects = targetPosition.size();
		for (int i = 0; i < nrObjects; i++) {
			Vector3f translation = targetPosition.get(i).targetTranslation;

			float distSquared = tmpVec.set(camLocation).subtractLocal(
					translation).lengthSquared();
			if (distSquared > viewDistance * viewDistance) {
				continue;
			}

			frustumCamera.setPlaneState(0);
			tmpBounding.getCenter().set(translation);
			if (frustumCamera.contains(tmpBounding) != Camera.OUTSIDE_FRUSTUM) {
				target.getWorldTranslation().set(translation);
				target.getWorldScale().set(targetPosition.get(i).targetScale);
				target.getWorldRotation().set(
						targetPosition.get(i).targetRotation);
				target.draw(r);
			}
		}
	}

	public void updateWorldBound() {
		// super.updateWorldBound();
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

	private class CheckCam extends AbstractCamera {
		private int width;

		private int height;

		public CheckCam(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public Matrix4f getProjectionMatrix() {
			return null;
		}

		public Matrix4f getModelViewMatrix() {
			return null;
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}

		public void onViewPortChange() {
		}

		public void resize(int width, int height) {
		}

		public void apply() {
			// TODO Auto-generated method stub
			
		}
	}
}