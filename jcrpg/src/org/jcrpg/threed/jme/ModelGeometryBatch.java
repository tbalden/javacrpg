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

import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;

public class ModelGeometryBatch extends GeometryBatchMesh<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> {
	private static final long serialVersionUID = 0L;
	
	public Model model;
	public J3DCore core;
	public Node parent = new Node();
	
	public TriMesh nullmesh = new TriMesh();
	private TriMesh getModelMesh(Model m)
	{
		if (m.type == Model.QUADMODEL) {
			return (TriMesh)core.modelLoader.loadQuadModelNode((QuadModel)model, false).getChild(0);
		} else
		if (m.type == Model.SIMPLEMODEL)
		{
			return (TriMesh)core.modelLoader.loadNodeOriginal((SimpleModel)model, false).getChild(0);			
		} else
		{
			return nullmesh;
		}
	}

	static HashMap<String,Node> sharedParentCache = new HashMap<String, Node>();
	
	public ModelGeometryBatch(J3DCore core, Model m) {
		model = m;
		this.core = core;
		TriMesh quad = getModelMesh(m);

		Node parentOrig = sharedParentCache.get(m.id);
		if (parentOrig==null)
		{
			parentOrig = new Node();
			parentOrig.setRenderState(quad.getRenderState(RenderState.RS_TEXTURE));
			if (m.type == Model.SIMPLEMODEL) {
				parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
			}
			sharedParentCache.put(m.id,parentOrig);
		}
		parent = new SharedNode("s"+parentOrig.getName(),parentOrig);
		parent.attachChild(this);
		parent.updateModelBound();
		
		parent.attachChild(this);
		parent.updateModelBound();
	}
	public void addItem(NodePlaceholder placeholder)
	{
		TriMesh quad = getModelMesh(placeholder.model);
		quad.setLocalTranslation(placeholder.getLocalTranslation());
		//quad.setDefaultColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		quad.setLocalRotation(placeholder.getLocalRotation());
		quad.setLocalScale(placeholder.getLocalScale());
		
		if (notVisible.size()>0)
		{
			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = notVisible.iterator().next();
			instance.getAttributes().setTranslation(placeholder.getLocalTranslation());
			instance.getAttributes().setRotation(placeholder.getLocalRotation());
			instance.getAttributes().setScale(placeholder.getLocalScale());
			instance.getAttributes().setVisible(true);
			instance.getAttributes().buildMatrices();
			placeholder.batchInstance = instance;
			notVisible.remove(instance);
			visible.add(instance);
			return;
		}
			
				// Add a Box instance (batch and attributes)
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = new GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>(quad, 
				 new GeometryBatchInstanceAttributes(quad));
		placeholder.batchInstance = instance;
		addInstance(instance);
		visible.add(instance);
	}
	
	public HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> notVisible = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
	public HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> visible = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
	public void removeItem(NodePlaceholder placeholder)
	{
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.batchInstance;
		if (instance!=null) {
			instance.getAttributes().setVisible(false);
			visible.remove(instance);
			notVisible.add(instance);
			if (visible.size()>0)
			{
				instance.getAttributes().setTranslation(visible.iterator().next().getAttributes().getTranslation());
			}
			
			//removeInstance((GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.batchInstance);
			placeholder.batchInstance = null;
		}
	}
	
	
}