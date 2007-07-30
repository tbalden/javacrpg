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
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.util.HashUtil;

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;

/**
 * partly billboard vegetation handler node.
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
	
	public void transformTrimeshesToQuads(Spatial child)
	{
		// TODO get down to the batchtri children, remove them and replace double tris with new quads, based on the average x/y/z coords of the
		// 6 vertices of the two triangle of one original leaf! (use draw code!)
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
	long timeCountStart = System.currentTimeMillis();
	
	public float windPower = 0.5f; 
	
	public static final float TIME_DIVIDER = 400;
	public static final long TIME_LIMIT = 0;
	
	float diffs[] = new float[5];
	float newDiffs[] = new float[5];
	
	public void draw2(Renderer r) {
		if (origTranslation == null) origTranslation = this.getLocalTranslation();
		passedTime = System.currentTimeMillis()-startTime;
		
		boolean doGrassMove = false;
		if (System.currentTimeMillis()-timeCountStart>TIME_LIMIT && J3DCore.CPU_ANIMATED_GRASS)			
		{
			doGrassMove = true;
			timeCountStart = System.currentTimeMillis();
		}
 
		if (children == null) {
			return;
		}
		
		float diff = 0;

		
		if (doGrassMove)	
		{
			// creating 5 diffs to look random 
			diff = 0.00059f*FastMath.sin(((passedTime/TIME_DIVIDER)*windPower)%FastMath.PI)*windPower;
			newDiffs[0] = diff;
			diff = 0.00059f*FastMath.sin((((passedTime+500)/TIME_DIVIDER)*windPower*(0.5f))%FastMath.PI)*windPower;
			newDiffs[1] = diff;
			diff = 0.00059f*FastMath.sin((((passedTime+500)/TIME_DIVIDER)*windPower*(0.6f))%FastMath.PI)*windPower;
			newDiffs[2] = diff;
			diff = 0.00059f*FastMath.sin((((passedTime+1000)/TIME_DIVIDER)*windPower*(0.8f))%FastMath.PI)*windPower;
			newDiffs[3] = diff;
			diff = 0.00059f*FastMath.sin((((passedTime+2000)/TIME_DIVIDER)*windPower*(0.7f))%FastMath.PI)*windPower;
			newDiffs[4] = diff;
		}
		if (newDiffs[0]==diffs[0]) {
			doGrassMove = false;			
		} else
		{
			diffs = newDiffs;
			newDiffs = new float[5];
		}
		
		Spatial child;
		for (int i = 0, cSize = children.size(); i < cSize; i++) {
			int whichDiff = 0;
			if (J3DCore.CPU_ANIMATED_GRASS) whichDiff = i%5;
			
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
				float distSquared = tmpVec.set(cam.getLocation())
				.subtractLocal(child.getWorldTranslation())
				.lengthSquared();
				if (distSquared <= viewDistance * viewDistance) 
				//if (distSquared<3*3 || HashUtil.mixPercentage(i,0,0)+10>(distSquared/(viewDistance*viewDistance))*100) 
				{
					// original code
					r.setCamera(cam);
					child.updateGeometricState(0.0f, false);
					child.onDraw(r);
					
					
					// animation
					
					{
						if (child instanceof Node)
						{
							Node n = (Node)child;
							ArrayList<Spatial> c2 = n.getChildren();
							int sCounter = 0;
							for (Spatial s:c2)
							{
								//if (s instanceof TriMesh) 
								{
									System.out.println("SPATIAL: "+s.getName()+" "+sCounter++);
									TriMesh q = (TriMesh)s;
									int l = q.getName().length();
									if (q.getName().charAt(l-1)==model.billboardPartNames[0].charAt(0))
									{
										//q.getWorldRotation().set(orient); // BILLBOARDING
										//q.getLocalRotation().set(new Quaternion());
										//q.setLocalTranslation(0, 0, 0);
									
									// if (true==true || !(doGrassMove) )
									// continue;
										// CPU computed grass moving

										for (int bc = 0; bc < q.getBatchCount(); bc++) {
											TriangleBatch b = q.getBatch(bc);
											FloatBuffer fb = b
													.getVertexBuffer();
											System.out.println("BATCH!!"+fb.capacity()+" "+bc);
											int maxFIndex = fb.capacity();
											int maxDoubleTri = maxFIndex/18;
											int fIndex = 0;
											for (int doubleTriIndex = 0; doubleTriIndex < maxDoubleTri; doubleTriIndex++) {
												for (int triIndex=0; triIndex<2; triIndex++)
												{
													for (int vectorIndex=0; vectorIndex<3; vectorIndex++)
													{
														for (int coord=0; coord<3;coord++)
														{															
															if (vectorIndex ==0 && coord==0)
															{
																fb.put(fIndex,0f);
															}
															if (vectorIndex ==0 && coord==1)
															{
																fb.put(fIndex,0f);
															}
															fIndex++;
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
	}
	
	
}
