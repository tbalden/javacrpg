/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.threed.jme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.GeoTileLoader.TiledTerrainBlockAndPassNode;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchMesh;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.TriMesh;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

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
		super("ModelGeometryBatchBB "+m.id+" "+(idCounter++));
		model = m;
		this.core = core;
		TriMesh mesh = null;
		TiledTerrainBlockAndPassNode data = null;
		// getting trunk mesh TriMesh for geometryBatch's base mesh.
		mesh = ((SharedMesh)(((Node)((Node)veg.foliagelessModelSpatial).getChild(0)).getChild(0))).getTarget();
		// storing the billboard parent mesh for addItem use.

		String parentKey = m.getId(placeHolder);
		parentKey+= placeHolder.neighborCubeData==null?"":placeHolder.neighborCubeData.getTextureKeyPartForBatch();
		Node parentOrig = null;//sharedParentCache.get(parentKey);
		if (parentOrig==null)
		{
			if (data==null || data.passNode==null)
			{
				parentOrig = new Node();
				parentOrig.setRenderState(mesh.getRenderState(StateType.Texture));
				if (m.type == Model.PARTLYBILLBOARDMODEL) {
					//parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
					parentOrig.setRenderState(mesh.getRenderState(StateType.Light));
				}
				//sharedParentCache.put(parentKey,parentOrig);
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
			parent = parentOrig;//new SharedNode("s"+parentOrig.getName(),parentOrig);
			parent.setLocalTranslation(placeHolder.getLocalTranslation());
			parent.attachChild(this);
			parent.setModelBound(new BoundingBox());
			parent.updateModelBound();
		} else
		{
			parent = parentOrig;
		}
		
	}
    static private GLSLShaderObjectsState so;
    static private GLSLShaderObjectsState so_point;
    private String currentShaderStr = "org/jcrpg/threed/jme/effects/shader/normalmap/parallax";//parallax";

   public void reloadShader() {
		GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem()
				.getRenderer().createGLSLShaderObjectsState();
		try {
			testShader.load(ModelGeometryBatch.class.getClassLoader()
					.getResource(currentShaderStr + ".vert"),
					ModelGeometryBatch.class.getClassLoader().getResource(
							currentShaderStr + ".frag"));
			testShader.apply();
			DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
		} catch (JmeException e) {
			Jcrpg.LOGGER.log(Level.WARNING, "Failed to reload shader", e);
			e.printStackTrace();
			return;
		}
		if (so == null) {
			so = DisplaySystem.getDisplaySystem().getRenderer()
					.createGLSLShaderObjectsState();

			// Check is GLSL is supported on current hardware.
			if (!GLSLShaderObjectsState.isSupported()) {
				Jcrpg.LOGGER
						.severe("Your graphics card does not support GLSL programs, and thus cannot run this test.");
				return;
			}
		}
		if (so_point == null) {
			so_point = DisplaySystem.getDisplaySystem().getRenderer()
					.createGLSLShaderObjectsState();

			// Check is GLSL is supported on current hardware.
			if (!GLSLShaderObjectsState.isSupported()) {
				Jcrpg.LOGGER
						.severe("Your graphics card does not support GLSL programs, and thus cannot run this test.");
				return;
			}
		}

		if (J3DCore.SETTINGS.NORMALMAP_DETAILED)
		{
			so.load(ModelGeometryBatch.class.getClassLoader().getResource(
					currentShaderStr + "2.vert"), ModelGeometryBatch.class
					.getClassLoader().getResource(currentShaderStr + "2.frag"));
			so_point.load(ModelGeometryBatch.class.getClassLoader().getResource(
					currentShaderStr + "2.vert"),
					ModelGeometryBatch.class.getClassLoader().getResource(
							currentShaderStr + "2.frag"));
		} else
		{
			so.load(ModelGeometryBatch.class.getClassLoader().getResource(
					currentShaderStr + ".vert"), ModelGeometryBatch.class
					.getClassLoader().getResource(currentShaderStr + ".frag"));
			so_point.load(ModelGeometryBatch.class.getClassLoader().getResource(
					currentShaderStr + "_pointlight.vert"),
					ModelGeometryBatch.class.getClassLoader().getResource(
							currentShaderStr + "_pointlight.frag"));
		}
		so.setUniform("baseMap", 0);
		so.setUniform("normalMap", 1);
		so.setUniform("specularMap", 2);
		so.setUniform("heightMap", 3);
		so.setUniform("heightValue", 0.005f);
		so.setUniform("numberOfLights", 1);

		so_point.setUniform("baseMap", 0);
		so_point.setUniform("normalMap", 1);
		so_point.setUniform("specularMap", 2);
		so_point.setUniform("heightMap", 3);
		so_point.setUniform("heightValue", 0.02f);

		Jcrpg.LOGGER.info("Shader reloaded...");
	}
   
   static int idCounter = 0;
   
	/**
	 * 
	 * @param core
	 * @param m The initial model for which we initialize it.
	 * @param placeHolder Initial placeholder.
	 */
	public ModelGeometryBatch(J3DCore core, Model m, NodePlaceholder placeHolder) {
		super("ModelGeometryBatch "+m.id+" "+(idCounter++));
		model = m;
		this.core = core;
		TriMesh mesh = null;
		TiledTerrainBlockAndPassNode data = null;
		if (m.type == Model.SIMPLEMODEL && ((SimpleModel)m).generatedGroundModel)
		{
			data = getTiledBlockData(m,placeHolder,true);
			mesh = data.block;
			this.setRenderState(core.cs_back);
		} else
		{
			mesh = getModelMesh(m,placeHolder);
		}
		 

		String parentKey = m.getId(placeHolder);
		parentKey+= placeHolder.neighborCubeData==null?"":placeHolder.neighborCubeData.getTextureKeyPartForBatch();
		Node parentOrig = null;//sharedParentCache.get(parentKey);
		if (parentOrig==null)
		{
			if (data==null || data.passNode==null)
			{
				parentOrig = new Node("MGB"+instanceCounter++);
				TextureState ts = (TextureState)mesh.getRenderState(RenderState.RS_TEXTURE);
				parentOrig.setRenderState(ts);
				
				if (m.type == Model.SIMPLEMODEL) {
					//parentOrig.setRenderState(quad.getRenderState(RenderState.RS_MATERIAL));
					parentOrig.setRenderState(mesh.getRenderState(RenderState.RS_LIGHT));
					SimpleModel sm = (SimpleModel)m;
					if (J3DCore.SETTINGS.NORMALMAP_ENABLED)
					{
						if (sm.normalMapTexture!=null)
						{
							if (ts.getNumberOfSetTextures()==1) 
							{
								if (so==null) reloadShader();
						        // Normal map
						        Texture normalMap = TextureManager.loadTexture( sm.normalMapTexture,
						        		//"Pillar_Nor.png",
						                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear,
						                Image.Format.GuessNoCompression, 0.0f, true);
						        normalMap.setWrap(Texture.WrapMode.Repeat);
						        ts.setTexture(normalMap, 1);
						        
						        // Spec Map
						        if (sm.specMapTexture!=null)
						        {
							        Texture specMap = TextureManager.loadTexture( sm.specMapTexture,
							        		//"Pillar_Spec.png",
					                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
					        		specMap.setWrap(Texture.WrapMode.Repeat);
					        		ts.setTexture(specMap, 2);
						        }
						        if (sm.heightMapTexture!=null)
						        {
							        Texture heightMap = TextureManager.loadTexture( sm.heightMapTexture,
							        		//"Pillar_Spec.png",
					                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
					        		heightMap.setWrap(Texture.WrapMode.Repeat);
					        		ts.setTexture(heightMap, 3);
						        }
							}
							{
				        		if (placeHolder.cube.cube.internalCube)
				        		{
				        			parentOrig.setRenderState(so_point);
				        		} else
				        		{
				        			parentOrig.setRenderState(so);
				        		}
							}
							parentOrig.setRenderState(J3DCore.ms);
						}
					}
				}
				//sharedParentCache.put(parentKey,parentOrig);
			} else
			{
				System.out.println("PASSNODE...");
				parentOrig = new Node("MGB"+instanceCounter++);
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
			parent = parentOrig;//new SharedNode("sMGB_"+(instanceCounter++)+"_"+parentOrig.getName(),parentOrig);
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
	static int instanceCounter = 0;
	
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

			// TODO add randomization here!
			// call instance special function that modifies coordinates slightly based on coordinates / placement
			if (placeholder.model instanceof PartlyBillboardModel)
			{
				instance.setSeed(true, placeholder.cube.cube.x+placeholder.cube.cube.y+placeholder.cube.cube.z);
			} else
			{
				instance.setSeed(false, 0);
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
			TriMesh meshData = null;
			TiledTerrainBlockAndPassNode data = null;
			if (placeholder.model.type == Model.SIMPLEMODEL && ((SimpleModel)placeholder.model).generatedGroundModel)
			{
				data = getTiledBlockData(placeholder.model,placeholder,false);
				meshData = data.block;
			} else
			if (placeholder.model.type == Model.PARTLYBILLBOARDMODEL)
			{
				meshData = triMesh;//
			} else
			{
				meshData = getModelMesh(placeholder.model,placeholder);
			}
			
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ADDING"+placeholder.model.id+quad.getName());
			meshData.setLocalTranslation(placeholder.getLocalTranslation().subtract(parent.getLocalTranslation()));
			//quad.setLocalTranslation(placeholder.getLocalTranslation());
			//quad.setDefaultColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			if (!(placeholder.model instanceof SimpleModel) || placeholder.model instanceof SimpleModel && !((SimpleModel)placeholder.model).generatedGroundModel)
			{
				meshData.setLocalRotation(placeholder.getLocalRotation());
			} else
			{
				meshData.getLocalTranslation().addLocal(new Vector3f(-1f,0,-1f));
			}
			
			if (placeholder.farView)
			{
				Vector3f scale = new Vector3f(placeholder.getLocalScale());
				meshData.setLocalScale(scale);
			} else
			{
				meshData.setLocalScale(placeholder.getLocalScale());
			}
			
			// Add a Box instance (batch and attributes)
			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instance = new GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>(meshData, 
					 new GeometryBatchInstanceAttributes(meshData));
			
			if (placeholder.model instanceof PartlyBillboardModel)
			{
				instance.setSeed(true, placeholder.cube.cube.x+placeholder.cube.cube.y+placeholder.cube.cube.z);
			} 
			
			// TODO add randomization here!
			// call instance special function that modifies coordinates slightly based on coordinates / placement
			
			
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