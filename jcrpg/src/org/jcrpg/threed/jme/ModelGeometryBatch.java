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

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.TriMesh;
import com.jme.scene.state.RenderState;

public class ModelGeometryBatch extends GeometryBatchMesh<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> {
	private static final long serialVersionUID = 0L;
	
	public Model model;
	public J3DCore core;
	public Node parent = new Node();
	public String key = null;
	
	public TriMesh nullmesh = new TriMesh();
	private TriMesh getModelMesh(Model m)
	{
		if (m.type == Model.QUADMODEL) {
			return (TriMesh)core.modelLoader.loadQuadModelNode((QuadModel)m, false).getChild(0);
		} else
		if (m.type == Model.SIMPLEMODEL)
		{
			return (TriMesh)core.modelLoader.loadNodeOriginal((SimpleModel)m, false).getChild(0);			
		} else
		{
			return nullmesh;
		}
	}

	public static HashMap<String,Node> sharedParentCache = new HashMap<String, Node>();
	
	public ModelGeometryBatch(J3DCore core, Model m) {
		model = m;
		this.core = core;
		TriMesh mesh = getModelMesh(m);

		Node parentOrig = sharedParentCache.get(m.id);
		if (parentOrig==null)
		{
			parentOrig = new Node();
			parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_TEXTURE));
			if (m.type == Model.SIMPLEMODEL) {
				//parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
				parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_LIGHT));
			}
			sharedParentCache.put(m.id,parentOrig);
		}
		parent = new SharedNode("s"+parentOrig.getName(),parentOrig);
		parent.attachChild(this);
		parent.updateModelBound();
	}
	
	public String getModelKey(Model model)
	{
		String key = "-";
		if (model.type == Model.SIMPLEMODEL) 
		{
			if (((SimpleModel)model).textureName!=null) 
			{				
				key = ((SimpleModel)model).id;
			}
		} else
		{
		}
		return key;
		
	}
	public static long sumBuildMatricesTime = 0; 
	public void addItem(NodePlaceholder placeholder)
	{
		
		String key = getModelKey(placeholder.model);
		HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> nVSet = notVisible.get(key);
		HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> vSet = visible.get(key);
		if (vSet==null)
		{
			vSet = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
			visible.put(key, vSet);
		}
		if (nVSet!=null && nVSet.size()>0)
		{
			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = nVSet.iterator().next();
			instance.getAttributes().setTranslation(placeholder.getLocalTranslation());
			instance.getAttributes().setRotation(placeholder.getLocalRotation());
			if (placeholder.farView)
			{
				Vector3f scale = new Vector3f(placeholder.getLocalScale());
				instance.getAttributes().setScale(scale);
			} else
			{
				instance.getAttributes().setScale(placeholder.getLocalScale());
			}
			instance.getAttributes().setVisible(true);
			long t0 = System.currentTimeMillis();
			instance.getAttributes().buildMatrices();
			placeholder.batchInstance = instance;
			nVSet.remove(instance);
			vSet.add(instance);
			sumBuildMatricesTime+=System.currentTimeMillis()-t0;
			return;
		} else
		{
			TriMesh quad = getModelMesh(placeholder.model);
			//System.out.println("ADDING"+placeholder.model.id+quad.getName());
			quad.setLocalTranslation(placeholder.getLocalTranslation());
			//quad.setDefaultColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			quad.setLocalRotation(placeholder.getLocalRotation());
			if (placeholder.farView)
			{
				Vector3f scale = new Vector3f(placeholder.getLocalScale());
				quad.setLocalScale(scale);
			} else
			{
				quad.setLocalScale(placeholder.getLocalScale());
			}
			
			long t0 = System.currentTimeMillis();
			// Add a Box instance (batch and attributes)
			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = new GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>(quad, 
					 new GeometryBatchInstanceAttributes(quad));
			placeholder.batchInstance = instance;
			addInstance(instance);
			sumBuildMatricesTime+=System.currentTimeMillis()-t0;
			vSet.add(instance);
		}
			
	}
	
	public HashMap<String, HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>> notVisible = new HashMap<String, HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>>();
	public HashMap<String, HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>> visible = new HashMap<String, HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>>();
	
	@SuppressWarnings("unchecked")
	public void removeItem(NodePlaceholder placeholder)
	{
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.batchInstance;
		if (instance!=null) {
			instance.getAttributes().setVisible(false);
			String key = getModelKey(placeholder.model);
			HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> nVSet = notVisible.get(key);
			HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> vSet = visible.get(key);
			if (nVSet==null)
			{
				nVSet = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
				notVisible.put(key, nVSet);
			}
			vSet.remove(instance);
			if (vSet.size()==0)
			{
				visible.remove(key);
			}
			nVSet.add(instance);
			/*if (visible.size()>0)
			{
				instance.getAttributes().setTranslation(visible.iterator().next().getAttributes().getTranslation());
			}*/
			
			//removeInstance((GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.batchInstance);
			placeholder.batchInstance = null;
		}
	}
	
	
}