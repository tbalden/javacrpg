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

import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.light.Light;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;

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
	public Vector3f avarageTranslation = null;
	
	
	public TriMesh nullmesh = new TriMesh();
	
	public TrimeshGeometryBatch(J3DCore core, TriMesh trimesh) {
		this.core = core;
		parent.setRenderState(trimesh.getRenderState(RenderState.RS_TEXTURE));
		parent.setRenderState(trimesh.getRenderState(RenderState.RS_MATERIAL));
		parent.setLightCombineMode(LightState.OFF);
		core.hmSolidColorSpatials.put(parent, parent);
		parent.attachChild(this);
		parent.updateModelBound();
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
			
				// Add a Box instance (batch and attributes)
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
	
	Vector3f lastLook, lastLeft;
	
	@Override
	public void onDraw(Renderer r) {
		Vector3f look = core.getCamera().getDirection().negate();
		Vector3f left1 = core.getCamera().getLeft().negate();
		
		boolean needsUpdate = true;
		if (lastLeft!=null)
		{
			if (look.distanceSquared(lastLook)==0 && left1.distanceSquared(lastLeft)==0)
			{
				needsUpdate = false;
			}
		} else
		{
			lastLook = new Vector3f();
			lastLeft = new Vector3f();
		}
		lastLook.set(look);
		lastLeft.set(left1);
		
		if (needsUpdate) {
			Quaternion orient = new Quaternion();
			orient.fromAxes(left1, core.getCamera().getUp(), look);
			for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> geoInstance:visible)
			{
				geoInstance.getAttributes().setRotation(orient);
				geoInstance.getAttributes().buildMatrices();
			}
		}
		super.onDraw(r);
	}
	
	
	
	
}