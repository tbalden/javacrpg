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

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.light.Light;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;

public class TrimeshGeometryBatch extends GeometryBatchMesh<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> {
	private static final long serialVersionUID = 0L;
	
	public Model model;
	public J3DCore core;
	public Node parent = new Node();
	
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
	public void addItem(TriMesh trimesh)
	{
		for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance : getInstances()) {
			if (!instance.getAttributes().isVisible())
			{
				instance.getAttributes().setTranslation(trimesh.getLocalTranslation());
				instance.getAttributes().setRotation(trimesh.getLocalRotation());
				instance.getAttributes().setScale(trimesh.getLocalScale());
				instance.getAttributes().setVisible(true);
				instance.getAttributes().buildMatrices();
				return;
			}
		}
			
				// Add a Box instance (batch and attributes)
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = new GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>(trimesh, 
				 new GeometryBatchInstanceAttributes(trimesh));
		addInstance(instance);
	}
	/*public void removeItem(NodePlaceholder placeholder)
	{
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.batchInstance;
		if (instance!=null) {
			instance.getAttributes().setVisible(false);
			for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instanceEn : getInstances()) {
				if (instanceEn.getAttributes().isVisible())
				{
					instance.getAttributes().setTranslation(instanceEn.getAttributes().getTranslation());
				}
			}
			
			//removeInstance((GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.batchInstance);
			placeholder.batchInstance = null;
		}
	}*/
	
	
}