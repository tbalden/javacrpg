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
import com.jme.math.Vector3f;
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
	
	public void addItem(NodePlaceholder placeholder, TriMesh trimesh)
	{
		for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance : getInstances()) {
			if (!instance.getAttributes().isVisible())
			{
				instance.getAttributes().setTranslation(trimesh.getLocalTranslation());
				instance.getAttributes().setRotation(trimesh.getLocalRotation());
				instance.getAttributes().setScale(trimesh.getLocalScale());
				instance.getAttributes().setVisible(true);
				instance.getAttributes().buildMatrices();

				HashSet instances = (HashSet)placeholder.batchInstance;
				if (instances==null)
				{
					instances = new HashSet();
					placeholder.batchInstance = instances;
				}
				instances.add(instance);

				calcAvarageTranslation(trimesh.getLocalTranslation());				
				return;
			}
		}
			
				// Add a Box instance (batch and attributes)
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = new GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>(trimesh, 
				 new GeometryBatchInstanceAttributes(trimesh));
		addInstance(instance);
		
		HashSet instances = (HashSet)placeholder.batchInstance;
		if (instances==null)
		{
			instances = new HashSet();
			placeholder.batchInstance = instances;
		}
		instances.add(instance);
		
		calcAvarageTranslation(trimesh.getLocalTranslation());				
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
					for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instanceEn : getInstances()) {
						if (instanceEn.getAttributes().isVisible())
						{
							geoInstance.getAttributes().setTranslation(instanceEn.getAttributes().getTranslation());
						}
					}
				}
			}
		}
		placeholder.batchInstance = null;
	}
	
	
}