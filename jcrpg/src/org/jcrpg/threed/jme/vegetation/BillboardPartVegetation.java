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

package org.jcrpg.threed.jme.vegetation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.ModelPool.PoolItemContainer;
import org.jcrpg.threed.jme.TrimeshGeometryBatch;
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.util.HashUtil;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;

/**
 * Partly billboard vegetation handler node. 
 * Removes model specified trimesh batch and replaces it with billboarded quads.
 * @author pali
 */
public class BillboardPartVegetation extends Node implements PooledNode {

	boolean NO_BATCH_GEOMETRY = false;

	public org.jcrpg.threed.ModelPool.PoolItemContainer cont;

	public PoolItemContainer getPooledContainer() {
		return cont;
	}

	public void setPooledContainer(PoolItemContainer cont) {
		this.cont = cont;
	}
	
	public PartlyBillboardModel model;
	public static HashMap<String, Quad> quadCache = new HashMap<String, Quad>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2149957857609187916L;
	
	public J3DCore core;
	public Camera cam;
	public float viewDistance;
	private Vector3f tmpVec = new Vector3f();
	
	public TrimeshGeometryBatch batch;
	
	boolean horRotated = false;
	boolean internal = false;
	
	public BillboardPartVegetation(J3DCore core, Camera cam, float viewDistance, PartlyBillboardModel model, boolean horRotated, boolean internal) {
		this.core = core;
		this.cam = cam;
		this.viewDistance = viewDistance;
		this.model = model;
		this.horRotated = horRotated;
		this.internal = internal;
		
	}
	
	public class QuadParams
	{
		public String name;
		public TextureState[] states;
		public String key; 
		public float xSize, ySize, x, y, z;
		public QuadParams(String name, TextureState[] states, String key, float xSize, float ySize, float x, float y,
				float z) {
			super();
			this.name = name;
			this.states = states;
			this.key = key;
			this.xSize = xSize;
			this.ySize = ySize;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
	}
	
	public HashSet<QuadParams> quads = new HashSet<QuadParams>();
	public HashSet<SharedMesh> quadMeshes = new HashSet<SharedMesh>();
	
	public void fillBillboardQuadsRotated(boolean rotated)
	{
		if (NO_BATCH_GEOMETRY)
		{
			for (SharedMesh mesh:quadMeshes)
			{
				mesh.removeFromParent();
			}
		} else
		{
			if (this.parent!=null) {
				this.parent.removeFromParent();
				if (batch!=null)
				{
					batch.parent.removeFromParent();
				}
				batch = null;
				this.detachAllChildren();
			}
		}
		for (QuadParams quadParams: quads)
		{
			SharedMesh mesh = createQuad(rotated, quadParams.name, quadParams.states, quadParams.key, quadParams.xSize, quadParams.ySize, quadParams.x, quadParams.y, quadParams.z);
			if (NO_BATCH_GEOMETRY) 
			{
				mesh.setName("---");
				this.attachChild(mesh);
			}
		}
		if (!NO_BATCH_GEOMETRY) {
			this.attachChild(batch.parent);
			batch.parent.getWorldRotation().set(new Quaternion());
			batch.parent.setLocalRotation(new Quaternion());
			batch.getWorldRotation().set(new Quaternion());
			batch.setLocalRotation(new Quaternion());
		}
	}
	
	public Quad targetQuad = null;
	/**
	 * Transforming the model's trimesh batchs to quads.
	 * @param child
	 */
	public void transformTrimeshesToQuads(Spatial child) {
		// get down to the batchtri children, remove them and replace
		// double tris with new quads, based on the average x/y/z coords of the
		// 4 vertices of the two triangle of one original leaf!

		// billboard world rotation calc
		Vector3f look = cam.getDirection().negate();
		// coopt loc for our left direction:
		Vector3f left1 = cam.getLeft().negate();
		Quaternion orient = new Quaternion();
		orient.fromAxes(left1, cam.getUp(), look);
		Matrix3f orient1 = new Matrix3f();
		orient1.fromAxes(left1, cam.getUp(), look);
		
		int added = 0;
		HashSet<TriMesh> removed = new HashSet<TriMesh>();
		TextureState[] states = core.modelLoader.loadTextureStates(model.billboardPartTextures);
		for (int i=0; i<states.length; i++) {
			Texture t1 = states[i].getTexture();
			t1.setApply(Texture.AM_MODULATE);
			t1.setCombineFuncRGB(Texture.ACF_MODULATE);
			t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
			t1.setCombineOp0RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
			t1.setCombineSrc1RGB(Texture.ACS_TEXTURE);
			t1.setCombineOp1RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
			t1.setCombineScaleRGB(1.0f);
		}
		

		if (child != null) {
			{
				{
					if ((child.getType() & Node.NODE) != 0) {
						Node n = (Node) child;
						ArrayList<Spatial> c2 = n.getChildren();
						//int sCounter = 0;
						for (Spatial s2 : c2) {
							ArrayList<Spatial> spatials = new ArrayList<Spatial>();
							Node removeFromNow = null;
							if (s2 instanceof Node)
							{
								for (Spatial s:((Node)s2).getChildren()) 
								{
									spatials.add(s);
								}
								removeFromNow = (Node)s2;
							} else
							{
								spatials.add(s2);
							}
							
							for (Spatial s:spatials) {
								 //if ((s.getType() & s.TRIMESH) != 0)
								{
									//System.out.println("SPATIAL: " + s.getName());
											//+ " " + sCounter++);
									TriMesh q = (TriMesh) s;
									int l = q.getName().length();
									String key = ""+q.getName().charAt(l - 1);
									if (model.removedParts.contains(key)) 
									{
										if (model.LOD>=1 || !J3DCore.DETAILED_TREES)
											removed.add(q);
									}
									if (model.partNameToTextureCount.containsKey(key)) 
									{
	
										for (int bc = 0; bc < q.getBatchCount(); bc++) {
											TriangleBatch b = q.getBatch(bc);
											FloatBuffer fb = b.getVertexBuffer();
											//System.out.println("BATCH!!"
												//	+ fb.capacity() + " " + bc);
											int maxFIndex = fb.capacity();
											int maxDoubleTri = maxFIndex / 18;
											int fIndex = 0;
											float sumLodX = 0, sumLodY = 0, sumLodZ = 0;
											for (int doubleTriIndex = 0; doubleTriIndex < maxDoubleTri; doubleTriIndex++) {
												// one quad generation for each doubleTriIndex
												float sumX =0, sumY =0, sumZ =0;
												float xDiff = 0;float yDiff = 0; float zDiff = 0;
												int counter = 0;
												for (int triIndex = 0; triIndex < 2; triIndex++) {
													for (int vectorIndex = 0; vectorIndex < 3; vectorIndex++) {
														for (int coord = 0; coord < 3; coord++) {
															if (coord == 0) {
																float x = fb.get(fIndex);
																if (triIndex==0)
																{
																	if (vectorIndex==0)
																	{
																		xDiff = x;
																	}
																	else if (vectorIndex==1)
																	{
																		xDiff = FastMath.abs(xDiff-x);
																	}
																}
																sumX+= x;
															}
															if (coord == 1) {
																float y = fb.get(fIndex); 
																if (triIndex==0)
																{
																	if (vectorIndex==0)
																	{
																		yDiff = y;
																	}
																	else if (vectorIndex==1)
																	{
																		yDiff = FastMath.abs(yDiff-y);
																	}
																}
																sumY+= y;
															}
															if (coord == 2) {
																float z = fb.get(fIndex);
																if (triIndex==0)
																{
																	if (vectorIndex==0)
																	{
																		zDiff = z;
																	}
																	else if (vectorIndex==1)
																	{
																		zDiff = FastMath.abs(zDiff-z);
																	}
																}
																sumZ+= z;
															}
															fIndex++;
														}
														counter++;
													}
												}
												if (model.LOD==3 && added<1)
												{
													if (doubleTriIndex<maxDoubleTri-1)
													{
														sumLodX+=sumX/counter;
														sumLodY+=sumY/counter;
														sumLodZ+=sumZ/counter;
													} else {
														float x = sumLodX/(maxDoubleTri-1);
														float y = sumLodY/(maxDoubleTri-1);
														float z = sumLodZ/(maxDoubleTri-1);
														float xSize = ((xDiff+yDiff+zDiff)/2f*(4f))*model.quadXSizeMultiplier;
														float ySize = ((xDiff+yDiff+zDiff)/2f)*(4f)*model.quadYSizeMultiplier;
														storeQuadParams(q.getName(),states,key,xSize,ySize,x,y,z);
														added++;
													}
												} else
												if (model.LOD==2 && added<1)
												{
													if (doubleTriIndex<maxDoubleTri-1)
													{
														sumLodX+=sumX/counter;
														sumLodY+=sumY/counter;
														sumLodZ+=sumZ/counter;
													} else {
														float x = sumLodX/(maxDoubleTri-1);
														float y = sumLodY/(maxDoubleTri-1);
														float z = sumLodZ/(maxDoubleTri-1);
														float xSize = ((xDiff+yDiff+zDiff)/2f)*2f;
														float ySize = ((xDiff+yDiff+zDiff)/2f)*2f;
														float xSizeM = xSize*model.quadXSizeMultiplier;
														float ySizeM = ySize*model.quadYSizeMultiplier;
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x-xSize/5,y-ySize/5,z-ySize/5);
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x-xSize/5,y+ySize/5,z-ySize/5);
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y-ySize/5,z-ySize/5);
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y+ySize/5,z-ySize/5);
														
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x-xSize/5,y-ySize/5,z+ySize/5);
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x-xSize/5,y+ySize/5,z+ySize/5);
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y-ySize/5,z+ySize/5);
														storeQuadParams(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y+ySize/5,z+ySize/5);
														added+=8;
													}
												} else
												if (J3DCore.DETAILED_TREE_FOLIAGE && model.LOD==0 || J3DCore.DETAILED_TREE_FOLIAGE && HashUtil.mixPercentage(doubleTriIndex,0,0)%8>model.LOD+1 || !J3DCore.DETAILED_TREE_FOLIAGE && HashUtil.mixPercentage(doubleTriIndex,0,0)%8>2) 
												{
													float x = sumX/counter;
													float y = sumY/counter;
													float z = sumZ/counter;
													float xSize = ((xDiff+yDiff+zDiff)/2f*(1+model.LOD/2f))*model.quadXSizeMultiplier;
													float ySize = ((xDiff+yDiff+zDiff)/2f)*(1+model.LOD/2f)*model.quadYSizeMultiplier;
													storeQuadParams(q.getName(),states,key,xSize,ySize,x+HashUtil.mixPercentage(doubleTriIndex, 0, 0)/5000f,y-HashUtil.mixPercentage(doubleTriIndex, 0, 0)/5000f,z+HashUtil.mixPercentage(doubleTriIndex, 0, 0)/5000f);
													added++;
												}
											}
										}
										removed.add(q);
									}
								}
							}
							if (removeFromNow!=null) {
								for (TriMesh t:removed)
									removeFromNow.detachChild(t);
							}
						}
						for (TriMesh t:removed)
							n.detachChild(t);
					}
					
				}
				
			}
		}
		fillBillboardQuadsRotated(horRotated);
	}
	
	/**
	 * Create a real quad for the tree from the abstract data upon constructing the tree.
	 * @param rotated
	 * @param name
	 * @param states
	 * @param key
	 * @param xSize
	 * @param ySize
	 * @param x
	 * @param y
	 * @param z
	 * @return The quad (or null if batched).
	 */
	private SharedMesh createQuad(boolean rotated, String name,TextureState[] states,String key, float xSize, float ySize, float x, float y, float z)
	{
		String qkey = model.id+rotated+xSize+ySize+internal;
		Quad targetQuad = quadCache.get(qkey);
		if (targetQuad == null)
		{
			targetQuad = new Quad(name,xSize,ySize);
			if (model.quadLightStateOff)
			{
				if (!internal) {
					targetQuad.setLightCombineMode(LightState.OFF); // if this is set off, all sides of the tree equally lit
					J3DCore.hmSolidColorSpatials.put(targetQuad,targetQuad);
				}
			}
			targetQuad.setSolidColor(new ColorRGBA(1,1,1,1));
			targetQuad.setRenderState(states[model.partNameToTextureCount.get(key).intValue()]);
			targetQuad.setLocalRotation(new Quaternion());
			TriangleBatch tBatch = targetQuad.getBatch(0);
			// swapping X,Z
			if (rotated) {
				FloatBuffer buff = tBatch.getVertexBuffer();
				for (int i=0; i<4; i++)
				{
					float cX = -1;
					float cZ = -1;
					for (int j=0; j<3; j++) {
						if (j==0) {
							cX = buff.get(i*3+j);
						} else
						if (j==2)
						{
							cZ = buff.get(i*3+j);
						}
					}
					buff.put(i*3 , -cZ);
					buff.put(i*3 + 2 , -cX);
				}
			}
			
			quadCache.put(qkey, targetQuad);
		}
		if (!NO_BATCH_GEOMETRY) {
			if (batch==null)
			{
				batch = new TrimeshGeometryBatch(model.id,core,targetQuad,internal);
				batch.animated = !internal && J3DCore.ANIMATED_TREES && model.windAnimation;
				batch.setName("---");
				batch.parent.setName("---");
			}
			targetQuad.setLocalTranslation(x, z, -y);
			targetQuad.setLocalRotation(new Quaternion());
			targetQuad.getWorldRotation().set(new Quaternion());
			batch.addItem(null, targetQuad);
		}
		if (NO_BATCH_GEOMETRY) {
			SharedMesh quad = new SharedMesh("s"+name,targetQuad);
			quad.setLocalTranslation(x, y, z);
			return quad;
		} else
		{
			return null;
		}
		
	}
	/**
	 * Storing a replacer quad's abstract description for the model for later addition to the tree (in form of quad or batchgeom).
	 * @param name
	 * @param states
	 * @param key
	 * @param xSize
	 * @param ySize
	 * @param x
	 * @param y
	 * @param z
	 */
	private void storeQuadParams(String name,TextureState[] states,String key, float xSize, float ySize, float x, float y, float z)
	{
		quads.add(new QuadParams(name,states,key,xSize,ySize,x,y,z));
	}
	
	@Override
	public int attachChild(Spatial child) {
		if (!"---".equals(child.getName())) 
		{
			transformTrimeshesToQuads(child);
		}
		return super.attachChild(child);
	}

	@Override
	public int attachChildAt(Spatial child, int index) {
		transformTrimeshesToQuads(child);
		return super.attachChildAt(child, index);
	}



	boolean windSwitch = true;
	Vector3f origTranslation = null;
	long passedTime = 0;
	float timeCounter = 0;
	long startTime = System.currentTimeMillis();
	
	public float windPower = 0.5f; 
	
	public static final float TIME_DIVIDER = 400;
	public static final long TIME_LIMIT = 0;
	
	float diffs[] = new float[5];
	float newDiffs[] = new float[5];
	
	public void draw(Renderer r) {
		boolean doGrassMove = false;
		Quaternion orient = null;
		if (NO_BATCH_GEOMETRY) {
			if (origTranslation == null)
				origTranslation = this.getLocalTranslation();
			
			long additionalTime = Math.min(System.currentTimeMillis() - startTime,32);
			passedTime += additionalTime;
			startTime= System.currentTimeMillis();
	
			
			if (J3DCore.ANIMATED_GRASS && model.windAnimation) {
				doGrassMove = true;
			}
	
			if (children == null) {
				return;
			}
	
			float diff = 0;
	
			if (doGrassMove) {
				// creating 5 diffs to look random
				diff = 0.059f * FastMath.sin(((passedTime / TIME_DIVIDER) * windPower)) * windPower;
				newDiffs[0] = diff;
				diff = 0.059f * FastMath.sin((((passedTime + 500) / TIME_DIVIDER) * windPower * (0.5f)))
						* windPower;
				newDiffs[1] = diff;
				diff = 0.059f * FastMath.sin((((passedTime + 500) / TIME_DIVIDER) * windPower * (0.6f)))
						* windPower;
				newDiffs[2] = diff;
				diff = 0.059f * FastMath.sin((((passedTime + 1000) / TIME_DIVIDER) * windPower * (0.8f)))
						* windPower;
				newDiffs[3] = diff;
				diff = 0.059f * FastMath.sin((((passedTime + 2000) / TIME_DIVIDER) * windPower * (0.7f)))
						* windPower;
				newDiffs[4] = diff;
			}
			diffs = newDiffs;
	
			Vector3f look = cam.getDirection().negate();
			Vector3f left1 = cam.getLeft().negate();
			orient = new Quaternion();
			orient.fromAxes(left1, cam.getUp(), look);
	
			
		}
		Spatial child;
		for (int i = 0, cSize = children.size(); i < cSize; i++) {
			int whichDiff = 0;
			if (J3DCore.ANIMATED_GRASS)
				whichDiff = i % 5;

			child = children.get(i);

			// billboard world rotation calc

			if (child != null) {
				float distSquared = tmpVec.set(cam.getLocation()).subtractLocal(child.getWorldTranslation())
						.lengthSquared();
				if (!J3DCore.LOD_VEGETATION || distSquared <= viewDistance * viewDistance)
				// 
				{

					
					// original code
					r.setCamera(cam);
					child.updateGeometricState(0.0f, false);
					child.onDraw(r);

					if (NO_BATCH_GEOMETRY) {
					
						// animation
	
						if (child.getType() == Node.NODE) {
							Node n = (Node) child;
							ArrayList<Spatial> c2 = n.getChildren();
							int sCounter = 0;
							for (Spatial s : c2) {
								if ( (s.getType() & Node.TRIMESH)!=0)
								{
									// System.out.println("SPATIAL: "+s.getName()+"
									// "+sCounter++);
									sCounter++;
									TriMesh q = (TriMesh) s;
									// if (q.getType() == Quad.Qinstanceof Quad)
	
									String qname = q.getName();
									int l = 0;
									if (qname != null)
										l = qname.length();
									if (l != 0 && q.getName().charAt(l - 1) == model.billboardPartNames[0].charAt(0)) {
										q.getWorldRotation().set(orient);
	
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
											if (fIndex<3 || fIndex>=9 && fIndex<12) {
												int mul = 1;
												if (FastMath.floor(fIndex / 3) == 3)
													mul = -1;
												if (fIndex % 3 == 0) {
													//float f = fb.get(fIndex);
													if (!f2_1Read) {
														f2_1 = fb.get(fIndex + 3 * mul);
														f2_1Read = true;
													}
													fb.put(fIndex, f2_1 + diffs[whichDiff]);
												}
												if (fIndex % 3 == 2) {
													//float f = fb.get(fIndex);
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
		}
	}
	
	
}
