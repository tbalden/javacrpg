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

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.GeoTileLoader.TiledTerrainBlockAndPassNode;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.SharedNode;
import com.jme.scene.TriMesh;
import com.jme.scene.state.RenderState;

/**
 * Model geometry batch that can hold together a lot of similar texture state trimeshes in one batch mesh
 * using GeometryBatchMesh.
 * @author illes
 *
 */
public class ModelGeometryBatch extends GeometryBatchMesh<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> {
	private static final long serialVersionUID = 0L;
	
	public Model model;
	public J3DCore core;
	public Node parent = new Node();
	public String key = null;
	
	public TriMesh nullmesh = new TriMesh();
	// TODO create a cache cleaning way!!! in GeometryBatchHelper maybe, check if the model is used at all...
	// till then it fastens up thing much, so keep it!
	public static HashMap<Object, TriMesh> cache = new HashMap<Object, TriMesh>();
	
	/**
	 * Returning the model's mesh for creating batchInstance copy of it.
	 * @param m
	 * @param n
	 * @return The trimesh to commit into the batch.
	 */
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
				System.out.println("THIS SHOULDNT RUN!!!");
				return nullmesh;
				//GeoTileLoader loader = core.modelLoader.geoTileLoader;
				//return loader.loadNodeOriginal(n);
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
	
	private TiledTerrainBlockAndPassNode getTiledBlockData(Model m,NodePlaceholder n,boolean splatNodeNeeded)
	{
		return core.modelLoader.geoTileLoader.loadNodeOriginal(n,splatNodeNeeded);
	}

	public static HashMap<String,Node> sharedParentCache = new HashMap<String, Node>();

	/**
	 * Special constructor for use with billboard part vegetation node - the node will provide
	 * the trunk triMesh that is not foliage part - in constructor only used for texture state retrieval.
	 * @param core
	 * @param m
	 * @param placeHolder
	 * @param veg
	 */
	public ModelGeometryBatch(J3DCore core, Model m, NodePlaceholder placeHolder, BillboardPartVegetation veg) {
		model = m;
		this.core = core;
		TriMesh mesh = null;
		TiledTerrainBlockAndPassNode data = null;
		// getting trunk mesh TriMesh for geometryBatch's base mesh.
		mesh = ((SharedMesh)(((Node)((Node)veg.foliagelessModelSpatial).getChild(0)).getChild(0))).getTarget();
		// storing the billboard parent mesh for addItem use.

		String parentKey = m.getId(placeHolder);
		parentKey+= placeHolder.neighborCubeData==null?"":placeHolder.neighborCubeData.getTextureKeyPartForBatch();
		Node parentOrig = sharedParentCache.get(parentKey);
		if (parentOrig==null)
		{
			if (data==null || data.passNode==null)
			{
				parentOrig = new Node();
				parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_TEXTURE));
				if (m.type == Model.PARTLYBILLBOARDMODEL) {
					//parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
					parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_LIGHT));
				}
				sharedParentCache.put(parentKey,parentOrig);
			} else
			{
				System.out.println("PASSNODE...");
				parentOrig = new Node();
				//parentOrig = data.passNode;//.attachChild(parentOrig);
				parentOrig.attachChild(data.passNode);
				this.copyTextureCoordinates(0, 1, 1);
				data.passNode.attachChild(this);
				this.updateRenderState();
				//parentOrig.setRenderState(data.passNode.getRenderState(RenderState.RS_TEXTURE));
				if (m.type == Model.SIMPLEMODEL) {
					//parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
					//parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_LIGHT));
				}
				//sharedParentCache.put(parentKey,parentOrig);
			}
		}
		if (data==null || data.passNode==null)
		{
			parent = new SharedNode("s"+parentOrig.getName(),parentOrig);
			parent.setLocalTranslation(placeHolder.getLocalTranslation());
			parent.attachChild(this);
			parent.setModelBound(new BoundingBox());
			parent.updateModelBound();
		} else
		{
			parent = parentOrig;
		}
		
	}
	

	/**
	 * 
	 * @param core
	 * @param m The initial model for which we initialize it.
	 * @param placeHolder Initial placeholder.
	 */
	public ModelGeometryBatch(J3DCore core, Model m, NodePlaceholder placeHolder) {
		model = m;
		this.core = core;
		TriMesh mesh = null;
		TiledTerrainBlockAndPassNode data = null;
		if (m.type == Model.SIMPLEMODEL && ((SimpleModel)m).generatedGroundModel)
		{
			data = getTiledBlockData(m,placeHolder,true);
			mesh = data.block;
		} else
		{
			mesh = getModelMesh(m,placeHolder);
		}
		 

		String parentKey = m.getId(placeHolder);
		parentKey+= placeHolder.neighborCubeData==null?"":placeHolder.neighborCubeData.getTextureKeyPartForBatch();
		Node parentOrig = sharedParentCache.get(parentKey);
		if (parentOrig==null)
		{
			if (data==null || data.passNode==null)
			{
				parentOrig = new Node();
				parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_TEXTURE));
				if (m.type == Model.SIMPLEMODEL) {
					//parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
					parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_LIGHT));
				}
				sharedParentCache.put(parentKey,parentOrig);
			} else
			{
				System.out.println("PASSNODE...");
				parentOrig = new Node();
				//parentOrig = data.passNode;//.attachChild(parentOrig);
				parentOrig.attachChild(data.passNode);
				this.copyTextureCoordinates(0, 1, 1);
				data.passNode.attachChild(this);
				this.updateRenderState();
				//parentOrig.setRenderState(data.passNode.getRenderState(RenderState.RS_TEXTURE));
				if (m.type == Model.SIMPLEMODEL) {
					//parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
					//parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_LIGHT));
				}
				//sharedParentCache.put(parentKey,parentOrig);
			}
		}
		if (data==null || data.passNode==null)
		{
			parent = new SharedNode("s"+parentOrig.getName(),parentOrig);
			parent.setLocalTranslation(placeHolder.getLocalTranslation());
			parent.attachChild(this);
			parent.setModelBound(new BoundingBox());
			parent.updateModelBound();
		} else
		{
			parent = parentOrig;
		}
		//setVBOInfo(new VBOInfo(true));
	}
	
	/**
	 * Returns a unique key for the model type so that reuse of batchInstances in nonVisible list can work.
	 * @param place
	 * @return
	 */
	public String getModelKey(NodePlaceholder place)
	{
		String key = "-";
		if (model.type == Model.SIMPLEMODEL) 
		{
			if (((SimpleModel)place.model).getTexture(place)!=null) 
			{
				
				key = ((SimpleModel)place.model).getId(place)+((SimpleModel)model).generatedGroundModel+  ( ((SimpleModel)model).generatedGroundModel? (place.cube.cube.cornerHeights!=null?place.cube.cube.cornerHeights.hashCode():"___") : "");
			} else
			{
				key = ((SimpleModel)place.model).getId(place);
			}
		}
		else if (model.type == Model.PARTLYBILLBOARDMODEL)
		{
			key = ((SimpleModel)place.model).getId(place);
		}
		else
		{
		}
		return key;
		
	}
	public static long sumBuildMatricesTime = 0;
	public void addItem(NodePlaceholder placeholder)
	{
		addItem(placeholder, null);
	}
	
	public boolean updateNeeded = false;
	public boolean isUpdateNeededAndSwitchIt()
	{
		if (updateNeeded)
		{
			updateNeeded = false;
			return true;
		}
		return false;
	}
	
	/**
	 * Adding a new item to the geomBatch parametered by the placeholder.
	 * @param placeholder
	 * @param triMesh sub TriMesh - means a multi trimesh model display, using multiple visibleSets/nonVisible sets.
	 * Nodeplaceholder multiBatchinstance will be initialized and filled with the for-mesh-created batchInstance .
	 */
	public void addItem(NodePlaceholder placeholder,TriMesh triMesh)
	{
		updateNeeded = true;
		String key = getModelKey(placeholder)+(triMesh!=null?triMesh.getName():"");
		ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> nVSet = notVisible.get(key);
		ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> vSet = visible.get(key);
		if (vSet==null)
		{
			vSet = new ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
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
			if (triMesh!=null)
			{
				// multi batch instance needed
				if (placeholder.multiBatchInstance==null) placeholder.multiBatchInstance = new HashMap<String, Object>();
				placeholder.multiBatchInstance.put(key, instance);
			}
			placeholder.modelGeomBatchInstance = instance;
			nVSet.remove(instance);
			vSet.add(instance);
			return;
		} else
		{
			long t0 = System.currentTimeMillis();
			TriMesh quad = null;
			TiledTerrainBlockAndPassNode data = null;
			if (placeholder.model.type == Model.SIMPLEMODEL && ((SimpleModel)placeholder.model).generatedGroundModel)
			{
				data = getTiledBlockData(placeholder.model,placeholder,false);
				quad = data.block;
			} else
			if (placeholder.model.type == Model.PARTLYBILLBOARDMODEL)
			{
				quad = triMesh;//
			} else
			{
				quad = getModelMesh(placeholder.model,placeholder);
			}
			
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
			
			if (triMesh!=null)
			{
				// multi batch instance needed
				if (placeholder.multiBatchInstance==null) placeholder.multiBatchInstance = new HashMap<String, Object>();
				placeholder.multiBatchInstance.put(key, instance);
			}
			placeholder.modelGeomBatchInstance = instance;
			addInstance(instance);
			vSet.add(instance);
			sumBuildMatricesTime+=System.currentTimeMillis()-t0;
		}
			
	}
	
	public HashMap<String, ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>> notVisible = new HashMap<String, ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>>();
	public HashMap<String, ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>> visible = new HashMap<String, ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>>();
	
	public void removeItem(NodePlaceholder placeholder)
	{
		removeItem(placeholder, null);
	}
	/**
	 * 
	 * @param placeholder
	 * @param triMesh If specified it means a model with multiple batch instances is displayed with
	 * multiple keys based on trimesh name. Removal will be executed with the sub triMesh's batchInstance
	 */
	@SuppressWarnings("unchecked")
	public void removeItem(NodePlaceholder placeholder,TriMesh triMesh)
	{
		
		GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.modelGeomBatchInstance; 
		String key = getModelKey(placeholder)+(triMesh!=null?triMesh.getName():"");
		if (triMesh!=null)
		{
			// multi batch instance needed
			if (placeholder.multiBatchInstance==null) return;
			instance = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.multiBatchInstance.get(key);
		}
		if (instance!=null) {
			instance.getAttributes().setVisible(false);
			ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> nVSet = notVisible.get(key);
			ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> vSet = visible.get(key);
			if (nVSet==null)
			{
				nVSet = new ArrayList<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>>();
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
			
			if (triMesh!=null) 
			{
				// if trimesh based detailed model we use multiBatchInstance map
				placeholder.multiBatchInstance.remove(key);
//				removeInstance((GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.multiBatchInstance.get(key));
				
			} else
			{
				//removeInstance((GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)placeholder.modelGeomBatchInstance);
			}
			placeholder.modelGeomBatchInstance = null;
		}
	}
	
	public void clearAll()
	{
		visible.clear();
		notVisible.clear();
	}
	
	
}