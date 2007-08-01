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
import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
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
public class BillboardPartVegetation extends Node {

	
	public PartlyBillboardModel model;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2149957857609187916L;
	
	public J3DCore core;
	public Camera cam;
	public float viewDistance;
	private Vector3f tmpVec = new Vector3f();

	public BillboardPartVegetation(J3DCore core, Camera cam, float viewDistance, PartlyBillboardModel model) {
		this.core = core;
		this.cam = cam;
		this.viewDistance = viewDistance;
		this.model = model;
	}
	
	public Quad targetQuad = null; 
	
	public void transformTrimeshesToQuads(Spatial child) {
		// TODO get down to the batchtri children, remove them and replace
		// double tris with new quads, based on the average x/y/z coords of the
		// 6 vertices of the two triangle of one original leaf! (use draw code!)

		// billboard world rotation calc
		Vector3f look = cam.getDirection().negate();
		// coopt loc for our left direction:
		Vector3f left1 = cam.getLeft().negate();
		Quaternion orient = new Quaternion();
		orient.fromAxes(left1, cam.getUp(), look);
		Matrix3f orient1 = new Matrix3f();
		orient1.fromAxes(left1, cam.getUp(), look);
		
		HashSet<TriMesh> added = new HashSet<TriMesh>();
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
					if (child instanceof Node) {
						Node n = (Node) child;
						ArrayList<Spatial> c2 = n.getChildren();
						int sCounter = 0;
						for (Spatial s : c2) {
							// if (s instanceof TriMesh)
							{
								System.out.println("SPATIAL: " + s.getName()
										+ " " + sCounter++);
								TriMesh q = (TriMesh) s;
								int l = q.getName().length();
								String key = ""+q.getName().charAt(l - 1);
								if (model.removedParts.contains(key)) 
								{
									if (model.LOD>=1)
										removed.add(q);
								}
								if (model.partNameToTextureCount.containsKey(key)) 
								{

									for (int bc = 0; bc < q.getBatchCount(); bc++) {
										TriangleBatch b = q.getBatch(bc);
										FloatBuffer fb = b.getVertexBuffer();
										System.out.println("BATCH!!"
												+ fb.capacity() + " " + bc);
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
											if (model.LOD==3 && added.size()<1)
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
													SharedMesh quad = createQuad(q.getName(),states,key,xSize,ySize,x,y,z);
													added.add(quad);
												}
											} else
											if (model.LOD==2 && added.size()<1)
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
													SharedMesh quad1_1 = createQuad(q.getName(),states,key,xSizeM,ySizeM,x-xSize/5,y-ySize/5,z-ySize/5);
													SharedMesh quad2_1 = createQuad(q.getName(),states,key,xSize,ySizeM,x-xSize/5,y+ySize/5,z-ySize/5);
													SharedMesh quad3_1 = createQuad(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y-ySize/5,z-ySize/5);
													SharedMesh quad4_1 = createQuad(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y+ySize/5,z-ySize/5);
													
													SharedMesh quad1_2 = createQuad(q.getName(),states,key,xSizeM,ySizeM,x-xSize/5,y-ySize/5,z+ySize/5);
													SharedMesh quad2_2 = createQuad(q.getName(),states,key,xSizeM,ySizeM,x-xSize/5,y+ySize/5,z+ySize/5);
													SharedMesh quad3_2 = createQuad(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y-ySize/5,z+ySize/5);
													SharedMesh quad4_2 = createQuad(q.getName(),states,key,xSizeM,ySizeM,x+xSize/5,y+ySize/5,z+ySize/5);
													
													added.add(quad1_1);
													added.add(quad2_1);
													added.add(quad3_1);
													added.add(quad4_1);
													added.add(quad1_2);
													added.add(quad2_2);
													added.add(quad3_2);
													added.add(quad4_2);
												}
											} else
											if (model.LOD==0 || HashUtil.mixPercentage(doubleTriIndex,0,0)%6>model.LOD+1) 
											{
												float x = sumX/counter;
												float y = sumY/counter;
												float z = sumZ/counter;
												float xSize = ((xDiff+yDiff+zDiff)/2f*(1+model.LOD/2f))*model.quadXSizeMultiplier;
												float ySize = ((xDiff+yDiff+zDiff)/2f)*(1+model.LOD/2f)*model.quadXSizeMultiplier;
												SharedMesh quad = createQuad(q.getName(),states,key,xSize,ySize,x,y,z);
												added.add(quad);
											}
										}
									}
									removed.add(q);
								}

							}
						}
						for (TriMesh t:removed)
							n.detachChild(t);
						for (TriMesh q:added)
							n.attachChild(q);
					}
					
				}
				
			}
		}
		
	}
	private SharedMesh createQuad(String name,TextureState[] states,String key, float xSize, float ySize, float x, float y, float z)
	{
		if (targetQuad==null) {
			targetQuad = new Quad(name,xSize,ySize);
			targetQuad.setRenderState(states[model.partNameToTextureCount.get(key).intValue()]);
			if (model.quadLightStateOff)
			{
				targetQuad.setLightCombineMode(LightState.OFF); // if this is set off, all sides of the tree equally lit
				J3DCore.hsSolidColorQuads.put(targetQuad,targetQuad);
			}
			targetQuad.setSolidColor(new ColorRGBA(1,1,1,1));
		}
		SharedMesh quad = new SharedMesh("s"+name,targetQuad);
		quad.setLocalTranslation(x, y, z);
		return quad;
		
	}
	
	@Override
	public int attachChild(Spatial child) {
		transformTrimeshesToQuads(child);
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
		if (origTranslation == null)
			origTranslation = this.getLocalTranslation();
		
		long additionalTime = Math.min(System.currentTimeMillis() - startTime,32);
		passedTime += additionalTime;
		startTime= System.currentTimeMillis();

		boolean doGrassMove = false;
		if (J3DCore.CPU_ANIMATED_GRASS) {
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
			Matrix3f orient1 = new Matrix3f();
			orient1.fromAxes(left1, cam.getUp(), look);

			if (child != null) {
				float distSquared = tmpVec.set(cam.getLocation()).subtractLocal(child.getWorldTranslation())
						.lengthSquared();
				if (distSquared <= viewDistance * viewDistance)
				// 
				{

					// original code
					r.setCamera(cam);
					child.updateGeometricState(0.0f, false);
					child.onDraw(r);

					// animation

					if (child.getType() == Node.NODE) {
						Node n = (Node) child;
						ArrayList<Spatial> c2 = n.getChildren();
						int sCounter = 0;
						for (Spatial s : c2) {
							// if (s instanceof TriMesh)
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
