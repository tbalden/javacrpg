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

package org.jcrpg.threed.jme;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.util.HashUtil;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.VertexProgramState;
import com.jme.system.DisplaySystem;

/**
 * Trimesh GeomBatch mesh, especially for grass and such things.
 * @author illes
 *
 */
public class TrimeshGeometryBatch extends GeometryBatchMesh<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> {
	private static final long serialVersionUID = 0L;
	
	public Model model;
	public J3DCore core;
	public Node parent = new Node();
	/**
	 * Tells average of the translations of the instances.
	 */
	public Vector3f avarageTranslation = null;
	/**
	 * Set this on putting on scene if horizontal rotation is needed. Used in onDraw billboarding.
	 */
	public Quaternion horizontalRotation = null;
	
	public boolean animated = false;
	
	
	public TriMesh nullmesh = new TriMesh();
	String shaderDirectory = "./data/shaders/";
	String shaderName = "bbGrass";

	private GLSLShaderObjectsState createShader(String shaderDirectory, String shaderName) {

		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		GLSLShaderObjectsState shader;		
		shader = display.getRenderer().createGLSLShaderObjectsState();
		try {
			/*shader.load(getClass().getClassLoader().getResource(shaderDirectory + shaderName + ".vert"),
				 		getClass().getClassLoader().getResource(shaderDirectory + shaderName + ".frag"));*/
			shader.load(new File(shaderDirectory + shaderName + ".vert").toURI().toURL(),
						new File(shaderDirectory + shaderName + ".frag").toURI().toURL());
			
		} catch (Exception e) {
		}
		shader.setEnabled(true);
		return shader;
	}
	static GLSLShaderObjectsState gl = null;
	static VertexProgramState vp = null;
	FragmentProgramState fp = null;
	
	static boolean vertexShader = true;
	static HashMap<String,Node> sharedParentCache = new HashMap<String, Node>();
	
	public TrimeshGeometryBatch(String id, J3DCore core, TriMesh trimesh) {
		this.core = core;
		Node parentOrig = sharedParentCache.get(id);
		if (parentOrig==null)
		{
			parentOrig = new Node();
			parentOrig.setRenderState(trimesh.getRenderState(RenderState.RS_TEXTURE));
			parentOrig.setRenderState(trimesh.getRenderState(RenderState.RS_MATERIAL));
			//parentOrig.setRenderState(core.f)
			parentOrig.setLightCombineMode(LightState.OFF);
			sharedParentCache.put(id,parentOrig);
		}
		parent = new SharedNode("s"+parentOrig.getName(),parentOrig);
		parent.attachChild(this);
		parent.updateModelBound();
		J3DCore.hmSolidColorSpatials.put(parent, parent);

        if (vertexShader && vp==null)
        { 
        	vp = DisplaySystem.getDisplaySystem().getRenderer().createVertexProgramState();
            try {vp.load(new File(
                    "./data/shaders/bbGrass2.vp").toURI().toURL());} catch (Exception ex){}
            vp.setEnabled(true);
            if (!vp.isSupported())
            {
            	System.out.println("!!!!!!! NO VP !!!!!!!");
            }
        }
        if (vertexShader && fp==null)
        {
        	fp = DisplaySystem.getDisplaySystem().getRenderer().createFragmentProgramState();
            try {fp.load(new File(
                    "./data/shaders/bbGrass2.fp").toURI().toURL());} catch (Exception ex){}
            fp.setEnabled(true);
            if (!fp.isSupported())
            {
            	System.out.println("!!!!!!! NO FP !!!!!!!");
            }
            
        }
        if (vertexShader) {
        	this.setRenderState(core.fs_external);
        	this.setRenderState(vp);
        	this.setRenderState(fp);
        }
        
		/*if (gl==null) {
			gl = createShader(shaderDirectory, shaderName);
			if (gl.isSupported())
			{
				System.out.println("!!!!!!! NO GL !!!!!!!");
			}
			gl.setUniform("FadeOutStart", 30f);
			gl.setUniform("FadeOutDist", 50f);
			gl.setUniform("Time", 0f);
			gl.setUniform("camPos", core.getCamera().getLocation());
			this.setRenderState(gl);
		}*/
	
	}
	
	/**
	 * Refreshes avarage translation with added node's translation.
	 * @param trans Vector3f.
	 */
	private void calcAvarageTranslation(Vector3f trans)
	{
		if (avarageTranslation==null)
		{
			avarageTranslation = trans;
		}
		else
		{
			int num = getInstances().size();
			float x = avarageTranslation.x*num;
			float y = avarageTranslation.y*num;
			float z = avarageTranslation.z*num;
			avarageTranslation.set((x+trans.x)/(num+1), (y+trans.y)/(num+1), (z+trans.z)/(num+1));
		}
	}
	public HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> notVisible = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
	public HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> visible = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
	
	public void addItem(NodePlaceholder placeholder, TriMesh trimesh)
	{
		
		if (notVisible.size()>0)
		{
			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = notVisible.iterator().next();
			instance.getAttributes().setTranslation(trimesh.getLocalTranslation());
			instance.getAttributes().setRotation(trimesh.getLocalRotation());
			instance.getAttributes().setScale(trimesh.getLocalScale());
			instance.getAttributes().setVisible(true);
			instance.getAttributes().buildMatrices();
			
			if (placeholder!=null) {

				HashSet instances = (HashSet)placeholder.batchInstance;
				if (instances==null)
				{
					instances = new HashSet();
					placeholder.batchInstance = instances;
				}
				instances.add(instance);
			}

			calcAvarageTranslation(trimesh.getLocalTranslation());				
			notVisible.remove(instance);
			visible.add(instance);
			return;
		}
			
		// Add a Trimesh instance (batch and attributes)
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = new GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>(trimesh,
				 new GeometryBatchInstanceAttributes(trimesh));
		addInstance(instance);
		
		if (placeholder!=null) {
			HashSet instances = (HashSet)placeholder.batchInstance;
			if (instances==null)
			{
				instances = new HashSet();
				placeholder.batchInstance = instances;
			}
			instances.add(instance);
		}
		
		calcAvarageTranslation(trimesh.getLocalTranslation());				
		visible.add(instance);
		return;
	}
	public void removeItem(NodePlaceholder placeholder)
	{
		HashSet instances = (HashSet)placeholder.batchInstance;
		if (instances!=null)
		{
			for (Object instance:instances)
			{
				GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> geoInstance = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)instance;
				
				if (geoInstance!=null) {
					geoInstance.getAttributes().setVisible(false); // switching off visibility
					visible.remove(geoInstance);
					notVisible.add(geoInstance);
					/*if (visible.size()>0)
					{
						geoInstance.getAttributes().setTranslation(visible.iterator().next().getAttributes().getTranslation());
					}*/
				}
			}
		}
		placeholder.batchInstance = null;
	}
	
	
	// Animation, billboarding
	
	Vector3f lastLook, lastLeft, lastLoc;

	float diffs[] = new float[5];
	float newDiffs[] = new float[5];
	boolean windSwitch = true;
	Vector3f origTranslation = null;
	long passedTime = 0;
	float timeCounter = 0;
	long startTime = System.currentTimeMillis();
	public float windPower = 0.5f; 
	int whichDiff = -1;
	
	public static final float TIME_DIVIDER = 400;
	public static final long TIME_LIMIT = 0;
	Quaternion orient = null;
	
	
	@Override
	public void onDraw(Renderer r) {
		Vector3f look = core.getCamera().getDirection().negate();
		Vector3f left1 = core.getCamera().getLeft().negate();
		Vector3f loc = core.getCamera().getLocation();
		
		// calculating needsUpdate of rotation
		boolean needsUpdate = true;
		if (lastLeft!=null)
		{
			if (look.distanceSquared(lastLook)==0 && left1.distanceSquared(lastLeft)==0 && loc.distanceSquared(lastLoc)==0)
			{
				needsUpdate = false;
			}
		} else
		{
			lastLoc = new Vector3f();
			lastLook = new Vector3f();
			lastLeft = new Vector3f();
		}
		lastLoc.set(loc);
		lastLook.set(look);
		lastLeft.set(left1);
		
		if (needsUpdate) {
			orient = new Quaternion();
			orient.fromAxes(left1, core.getCamera().getUp(), look);

			// reseting orientation of parent, and self
			Quaternion q = new Quaternion();
			parent.setLocalRotation(q);
			getWorldRotation().set(q);
			
			if (horizontalRotation!=null) {
				// needs horizontal rotation
				
				
				// biggest hack of billboard rotation in the world comes here...by experimentation, it needs although rotated quads  put into this batch!!
				// check BillboardPartVegetation fill() method how it is done.
				
				// fetching horizontal rotation direction:
				Integer direction = new Integer(0);
				for (Entry<Integer, Quaternion> qH : J3DCore.horizontalRotationsReal.entrySet())
				{
					if (qH.getValue().equals(horizontalRotation))
					{
						direction = qH.getKey();
					}
				}
				
				// if west or east the trick must be done to correct the rotation quaternion
				if (direction.intValue() == J3DCore.WEST) {
					

					if (core.viewDirection == J3DCore.SOUTH) {
						float temp = orient.y;
						orient.y = orient.w;
						orient.w = temp;
						orient.y = -orient.y;
					} else if (core.viewDirection == J3DCore.NORTH) {
						float temp = orient.x;
						orient.x = orient.z;
						orient.z = temp;
					} else if (core.viewDirection == J3DCore.EAST) {
						float temp = orient.x;
						orient.x = orient.z;
						orient.z = temp;
						temp = orient.y;
						orient.y = -orient.w;
						orient.w = temp;
					}else if (core.viewDirection == J3DCore.WEST) {
						float temp = orient.x;
						orient.x = orient.z;
						orient.z = temp;
						temp = orient.y;
						orient.y = orient.w;
						orient.w = -temp;
					} 
				} else if (direction.intValue() == J3DCore.EAST) {
					if (core.viewDirection == J3DCore.SOUTH) {
						float temp = orient.y;
						orient.y = orient.w;
						orient.w = -temp;
						orient.y = -orient.y;
					} else if (core.viewDirection == J3DCore.NORTH) {
						float temp = orient.x;
						orient.x = orient.z;
						orient.z = -temp;
					} else if (core.viewDirection == J3DCore.EAST) {
						float temp = orient.y;
						orient.y = orient.w;
						orient.w = temp;
						orient.w = -orient.w;
					} else if (core.viewDirection == J3DCore.WEST) {
						float temp = orient.x;
						orient.x = orient.z;
						orient.z = temp;
						orient.z = -orient.z;
					}
				} else if (direction.intValue() == J3DCore.NORTH) {
					// nothing to do, it's correct
				} else if (direction.intValue() == J3DCore.SOUTH) {
					if (core.viewDirection == J3DCore.SOUTH) {
						orient.y = -orient.y;
						orient.w = -orient.w;
					} else if (core.viewDirection == J3DCore.NORTH) {
						orient.w = -orient.w;
						orient.y = -orient.y;
					} else if (core.viewDirection == J3DCore.EAST) {
						orient.w = -orient.w;
						orient.y = -orient.y;
					} else if (core.viewDirection == J3DCore.WEST) {
						orient.w = -orient.w;
						orient.y = -orient.y;
					}
				}

				// rotation the whole trimesh to position in the cube:
				setLocalRotation(horizontalRotation);
			} else {
				//Matrix4f horRot = new Matrix4f();
				// horizontalRotation.toRotationMatrix(horRot);
	    		/*
				 * vp.setParameter(horRot.getColumn(0), 1);
				 * vp.setParameter(horRot.getColumn(1), 2);
				 * vp.setParameter(horRot.getColumn(2), 3);
				 * vp.setParameter(horRot.getColumn(3), 4);
				 */
				setLocalRotation(q);
			}
			if (vertexShader) {
				float dist = this.getWorldTranslation().add(avarageTranslation).distance(core.getCamera().getLocation());
				float start = 2*J3DCore.VIEW_DISTANCE/3;
				fp.setParameter(new float[]{1.0f-( Math.max(0, dist-start)/(start) * 1.4f),0,0,0}, 1);
			}
			
			
			for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> geoInstance:visible)
			{
				geoInstance.getAttributes().setRotation(orient);
				geoInstance.getAttributes().buildMatrices();
			}
		}

		
		if (vertexShader) {
			if (core.extRootNode.equals(parent.getParent()) || core.extRootNode.equals(parent.getParent().getParent())|| parent.getParent().getParent()!=null && core.extRootNode.equals(parent.getParent().getParent().getParent())) {
				fp.setParameter(new float[]{core.fs_external.getColor().r,core.fs_external.getColor().g,core.fs_external.getColor().b,core.fs_external.getColor().a}, 0);
			} else
			{
				fp.setParameter(new float[]{core.fs_internal.getColor().r,core.fs_internal.getColor().g,core.fs_internal.getColor().b,core.fs_internal.getColor().a}, 0);
			}
		}
		
		long additionalTime = Math.min(System.currentTimeMillis() - startTime,32);
		passedTime += additionalTime;
		startTime= System.currentTimeMillis();

		boolean doGrassMove = false;
		if (animated) {
			doGrassMove = true;
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
			diffs = newDiffs;
			if (whichDiff==-1) {
				whichDiff = HashUtil.mixPercentage((int)this.getWorldTranslation().x,(int)this.getWorldTranslation().y,(int)this.getWorldTranslation().z)%5;
			}
			
			if (vertexShader) {
	    		vp.setParameter(new float[]{diffs[whichDiff],diffs[whichDiff],0,0}, 0);
			}
			else
			if (true==false)
			{
	
				FloatBuffer fb = null;
				for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> geoInstance:visible)
				{
					TriangleBatch b = geoInstance.mesh.getBatch(0);
					//TriangleBatch b = this.getBatch(0);
					if (fb==null) {
						fb = b.getVertexBuffer();
						if (fb!=null)
						for (int fIndex = 0; fIndex < fb.capacity(); fIndex++) {
							boolean f2_1Read = false;
							boolean f2_2Read = false;
							float f2_1 = 0;
							float f2_2 = 0;
							if (fIndex%12<3 || fIndex%12>=9 && fIndex%12<12) {
								int mul = 1;
								if (FastMath.floor(fIndex%12 / 3) == 3)
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
					} else
					{
						b.setVertexBuffer(fb);
					}
					geoInstance.preCommit(true); // update vertices
				}
			}
		}
		super.onDraw(r);
	}
	
}