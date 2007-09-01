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

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.util.HashUtil;

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
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
	Matrix4f m4f = new Matrix4f();
	Matrix3f m3f = new Matrix3f();
	
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
            //vp.setParameter(lightPosition, 8); // TODO
            try {vp.load(new File(
                    "./data/shaders/bbGrass2.vp").toURI().toURL());} catch (Exception ex){}
            vp.setEnabled(true);
            if (!vp.isSupported())
            {
            	System.out.println("!!!!!!! NO VP !!!!!!!");
            }
            
            
            //m4f.s
            
            Matrix3f m3f = new Matrix3f();
            
    		Vector3f look = core.getCamera().getDirection().negate();
    		Vector3f left1 = core.getCamera().getLeft().negate();
    		Vector3f loc = core.getCamera().getLocation();
    		Quaternion orient = new Quaternion();
    		orient.fromAxes(left1, core.getCamera().getUp(), look);
    		
    		m4f.setRotationQuaternion(orient);
    		//vp.setParameter(new float[]{1f,0,0,0}, 5);
    		/*vp.setParameter(new float[]{40,0,0,0}, 11);
    		vp.setParameter(new float[]{0,0,0,0}, 12);
    		vp.setParameter(new float[]{0,0,0,0}, 13);*/
        }
        if (vertexShader) parent.setRenderState(vp);
        
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
					if (visible.size()>0)
					{
						geoInstance.getAttributes().setTranslation(visible.iterator().next().getAttributes().getTranslation());
					}
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
			parent.getWorldRotation().set(q); 
			parent.setLocalRotation(q);
			getWorldRotation().set(q);
			
			if (horizontalRotation!=null) {
				// needs horizontal rotation
				setLocalRotation(horizontalRotation);
				// mult orient, it's a must:
				orient.multLocal(horizontalRotation);
			} else {
				setLocalRotation(q);
			}
			
			
			for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> geoInstance:visible)
			{
				geoInstance.getAttributes().setRotation(orient);
				geoInstance.getAttributes().buildMatrices();
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