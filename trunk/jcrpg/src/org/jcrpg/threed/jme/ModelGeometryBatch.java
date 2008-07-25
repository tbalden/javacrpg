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

import org.jcrpg.threed.GeoTileLoader;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.scene.RenderedCube;
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
	// TODO create a cache cleaning way!!! in GeometryBatchHelper maybe, check if the model is used at all...
	// till then it fastens up thing much, so keep it!
	public  static HashMap<Object, TriMesh> cache = new HashMap<Object, TriMesh>();
	private TriMesh getModelMesh(Model m,NodePlaceholder n)
	{
		if (m.type == Model.QUADMODEL) {
			TriMesh mesh = cache.get(m);
			if (cache.get(m)==null){
				mesh = (TriMesh)core.modelLoader.loadQuadModelNode((QuadModel)m, false).getChild(0);
				cache.put(m, mesh);
			}
			return mesh; 	
		} else
		if (m.type == Model.SIMPLEMODEL)
		{
			if (((SimpleModel)m).generatedGroundModel)
			{
				GeoTileLoader loader = core.modelLoader.geoTileLoader;
				return loader.loadNodeOriginal((SimpleModel)m, n.cube);
			} else
			{
				TriMesh mesh = cache.get(m);
				if (cache.get(m)==null){
					mesh = (TriMesh)core.modelLoader.loadNodeOriginal((SimpleModel)m, false).getChild(0);
					cache.put(m, mesh);
				}
				return mesh;
			}
		} else
		{
			return nullmesh;
		}
	}

	public static HashMap<String,Node> sharedParentCache = new HashMap<String, Node>();
	
	public ModelGeometryBatch(J3DCore core, Model m, NodePlaceholder placeHolder) {
		model = m;
		this.core = core;
		TriMesh mesh = getModelMesh(m,placeHolder);

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
		parent.setLocalTranslation(placeHolder.getLocalTranslation());
		parent.attachChild(this);
		parent.updateModelBound();
	}
	
	public String getModelKey(Model model, RenderedCube c)
	{
		String key = "-";
		if (model.type == Model.SIMPLEMODEL) 
		{
			if (((SimpleModel)model).textureName!=null) 
			{				
				key = ((SimpleModel)model).id+((SimpleModel)model).generatedGroundModel+  ( ((SimpleModel)model).generatedGroundModel? (c.cube.cornerHeights!=null?c.cube.cornerHeights.hashCode():"___") : "");
			}
		} else
		{
		}
		return key;
		
	}
	public static long sumBuildMatricesTime = 0; 
	public void addItem(NodePlaceholder placeholder)
	{
		
		String key = getModelKey(placeholder.model, placeholder.cube);
		HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> nVSet = notVisible.get(key);
		HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> vSet = visible.get(key);
		if (vSet==null)
		{
			vSet = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
			visible.put(key, vSet);
		}
		if (nVSet!=null && nVSet.size()>0)
		{
			long t0 = System.currentTimeMillis();
			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = nVSet.iterator().next();
			instance.getAttributes().setTranslation(placeholder.getLocalTranslation().subtract(parent.getLocalTranslation()));
			if (!(placeholder.model instanceof SimpleModel) || placeholder.model instanceof SimpleModel && !((SimpleModel)placeholder.model).generatedGroundModel)
			{
				instance.getAttributes().setRotation(placeholder.getLocalRotation());
			} else
			{
				instance.getAttributes().getTranslation().addLocal(new Vector3f(-1f,0,-1f));
			}

			sumBuildMatricesTime+=System.currentTimeMillis()-t0;
			if (placeholder.farView)
			{
				Vector3f scale = new Vector3f(placeholder.getLocalScale());
				instance.getAttributes().setScale(scale);
			} else
			{
				instance.getAttributes().setScale(placeholder.getLocalScale());
			}
			instance.getAttributes().setVisible(true);
			instance.getAttributes().buildMatrices();
			placeholder.batchInstance = instance;
			nVSet.remove(instance);
			vSet.add(instance);
			return;
		} else
		{
			long t0 = System.currentTimeMillis();
			TriMesh quad = getModelMesh(placeholder.model,placeholder);
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("ADDING"+placeholder.model.id+quad.getName());
			quad.setLocalTranslation(placeholder.getLocalTranslation().subtract(parent.getLocalTranslation()));
			//quad.setLocalTranslation(placeholder.getLocalTranslation());
			//quad.setDefaultColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			if (!(placeholder.model instanceof SimpleModel) || placeholder.model instanceof SimpleModel && !((SimpleModel)placeholder.model).generatedGroundModel)
			{
				quad.setLocalRotation(placeholder.getLocalRotation());
			} else
			{
				quad.getLocalTranslation().addLocal(new Vector3f(-1f,0,-1f));
			}
			
			if (placeholder.farView)
			{
				Vector3f scale = new Vector3f(placeholder.getLocalScale());
				quad.setLocalScale(scale);
			} else
			{
				quad.setLocalScale(placeholder.getLocalScale());
			}
			
			// Add a Box instance (batch and attributes)
			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = new GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>(quad, 
					 new GeometryBatchInstanceAttributes(quad));
			placeholder.batchInstance = instance;
			addInstance(instance);
			vSet.add(instance);
			sumBuildMatricesTime+=System.currentTimeMillis()-t0;
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
			String key = getModelKey(placeholder.model,placeholder.cube);
			HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> nVSet = notVisible.get(key);
			HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> vSet = visible.get(key);
			if (nVSet==null)
			{
				nVSet = new HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
				notVisible.put(key, nVSet);
			}
			if (vSet!=null) {
				vSet.remove(instance);
				if (vSet.size()==0)
				{
					visible.remove(key);
				}
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