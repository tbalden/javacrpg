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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.GeoTileLoader;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.VegetationSetup;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.jcrpg.threed.standing.J3DStandingEngine;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.system.DisplaySystem;

/**
 * The class used by J3DStandingEngine to append/remove geometryBatches for the models that can
 * use this feature like SimpleModels and Billboard vegetation/grass. It has universal
 * addItem/removeItem that will handle the details such as which extension of geomBatch to use.
 * It will also generate coordinate based keys that hold together several-cubes-sized areas as
 * one batch with the addition of other key parameters as texture/model id etc. Check getKey().
 * UpdateAll handles removal of batches.
 * @author illes
 *
 */
public class GeometryBatchHelper {

	public HashMap<String, ModelGeometryBatch> modelBatchMap = new HashMap<String, ModelGeometryBatch>();
	public HashMap<String, TrimeshGeometryBatch> trimeshBatchMap = new HashMap<String, TrimeshGeometryBatch>();
	J3DCore core;
	
	public GeometryBatchHelper(J3DCore core)
	{
		this.core = core;		
	}
	
	public static int SIMPLE_MODEL_BATCHED_SPACE_SIZE = 10;
	public static int QUAD_MODEL_BATCHED_SPACE_SIZE = 6;
	public static int TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE = 10;
	public static int PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE = 10;
	public static int PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE_Y = 12;
	
	/**
	 * Special key creator for billboard part vegetation's foliage atlas texture.
	 * @param internal
	 * @param m
	 * @param place
	 * @param farView
	 * @return
	 */
	private String getBillboardVegetationAtlasKey(boolean internal, Model m, NodePlaceholder place, boolean farView)
	{
		int viewMul = 1;
		int yLevelMul = 1;
		if (farView) {
			viewMul = 2;
			yLevelMul = J3DCore.FARVIEW_GAP;
		}
		String key = ((PartlyBillboardModel)m).atlasTextureName+internal;
		key+=((place.cube.cube.x/PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+((place.cube.cube.z/PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+(place.cube.cube.y/(PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE_Y));
    	return key;
	}
	
	/**
	 * Returns Grouping key for batch objects. The key unifies a group of nodes into a batch.
	 * @param internal
	 * @param m
	 * @param place
	 * @return Key.
	 */
	private String getKey(boolean internal, Model m, NodePlaceholder place, boolean farView)
	{
		int viewMul = 1;
		int yLevelMul = 1;
		if (farView) {
			viewMul = 2;
			yLevelMul = J3DCore.FARVIEW_GAP;
		}
    	String key = m.type+m.id+internal+(farView);
    	if (m.type==Model.SIMPLEMODEL|| m.type==Model.PARTLYBILLBOARDMODEL) { // grouping based on coordinate units
    		SimpleModel sm = (SimpleModel)m;
    		if (sm.textureName!=null)
    		{
    			if (sm.generatedGroundModel)
    			{
    				
    				key = m.type+sm.getTexture(place)+internal+(farView);
    				if (place.neighborCubeData==null)
    					place.neighborCubeData = GeoTileLoader.getNeighborCubes(place);
    				key+=place.neighborCubeData.getTextureKeyPartForBatch();
    				if (sm.useAtlasTexture && place.neighborCubeData.getTextureKeyPartForBatch()==null)
    				{
    					// atlas texture for the model is needed and no blending / splatting enabled, so
    					// replace the key with the common atlas texture name to batch 
    					// common atlas textured tiles into one batch...
    					key = m.type+sm.atlasTextureName+internal+(farView);
    				}
    				
    			} else
    			{
    				key = m.type+sm.getTexture(place)+internal+(farView);
    			}
    		}
    		if (true ||sm.xGeomBatchSize==-1) 
    		{
    			if (true ||sm.yGeomBatchSize==-1) 
    			{
    				if (m.type==Model.PARTLYBILLBOARDMODEL)
    				{
    					key+=((place.cube.cube.x/PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+((place.cube.cube.z/PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+(place.cube.cube.y/(PARTYLBILLBOARD_MODEL_BATCHED_SPACE_SIZE_Y));
    				} else
    				{
    					key+=((place.cube.cube.x/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+((place.cube.cube.z/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+(place.cube.cube.y/(SIMPLE_MODEL_BATCHED_SPACE_SIZE*yLevelMul));
    				}
    			}
    			else
    			{
    				key+=((place.cube.cube.x/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+((place.cube.cube.z/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+(place.cube.cube.y/(sm.yGeomBatchSize*yLevelMul));
    			}
    		} else
    		{
    			if (sm.yGeomBatchSize==-1) 
    			{
    				key+=((place.cube.cube.x/sm.xGeomBatchSize)/viewMul)+"_"+((place.cube.cube.z/sm.xGeomBatchSize)/viewMul)+"_"+(place.cube.cube.y/(SIMPLE_MODEL_BATCHED_SPACE_SIZE*yLevelMul));
    			}
    			else
    			{
    				key+=((place.cube.cube.x/sm.xGeomBatchSize)/viewMul)+"_"+((place.cube.cube.z/sm.xGeomBatchSize)/viewMul)+"_"+(place.cube.cube.y/(sm.yGeomBatchSize*yLevelMul));
    			}
    		}
    	} else
    	if (m.type==Model.TEXTURESTATEVEGETATION)
    	{
    		if (((TextureStateVegetationModel)m).atlasTexture)
    		{
    			key = ((TextureStateVegetationModel)m).textureNames[0]+(place.cube.cube.x/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+place.cube.cube.y/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE;//yLevelMul;
    		} else
    		{
    			key+=(place.cube.cube.x/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+place.cube.cube.y/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE;//yLevelMul;
    		}
    	} else
    	{   // other models only by Y // quad
    		key+=(int)(place.cube.cube.x/QUAD_MODEL_BATCHED_SPACE_SIZE)+""+(int)(place.cube.cube.z/QUAD_MODEL_BATCHED_SPACE_SIZE)+""+(int)place.cube.cube.y/yLevelMul;
    	}
    	
    	return key+farView;
	}
	
    /**
     * Use geometry instancing to create a mesh containing a number of box
     * instances
     * @param farView tells if this item must be magnified for farview.
     */
    public void addItem(J3DStandingEngine sEngine, boolean internal, Model m, NodePlaceholder place, boolean farView) {
    	String key = getKey(internal, m, place, farView);

    	if (m.type!=Model.TEXTURESTATEVEGETATION) {
	    	ModelGeometryBatch batch = modelBatchMap.get(key);
	    	if (batch==null)
	    	{
	    		batch = new ModelGeometryBatch(core,m,place);
	    		batch.key = key;
	    		if (m.type == Model.QUADMODEL && ((QuadModel)m).waterQuad)
	    		{
	    			J3DCore.waterEffectRenderPass.setWaterEffectOnSpatial(batch);
	    		}
	    		if (internal)
	    		{
	    			sEngine.intWaterRefNode.attachChild(batch.parent);
	    			//sEngine.intRootNode.updateRenderState();
	    		} else
	    		{
	    			sEngine.extWaterRefNode.attachChild(batch.parent);
	    			//sEngine.extRootNode.updateRenderState();
	    		}
    			batch.parent.setCullMode(Node.CULL_NEVER); // set culling to NEVER for the first rendering...
    			core.gameState.getCurrentStandingEngine().newNodesToSetCullingDynamic.add(batch.parent); // adding it to newly placed nodes
	    		modelBatchMap.put(key, batch);
	    		if (locking)
	    		{
	    			batch.lockTransforms();
	    			batch.lockShadows();
	    		}
	    		
	    	}
	    	if ( (batch.getLocks()&Node.LOCKED_BOUNDS)>0)
	    	{
	    		batch.unlockBranch();
		    	batch.unlockBounds();
		    	batch.unlockMeshes();
	    	}
	    	batch.addItem(place);
	    	batch.updateGeometricState(0f, true); // TODO why is it working only if put here?
	    } else
    	if (m.type==Model.TEXTURESTATEVEGETATION) {
    		//if (place.cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP) return; // on steep, no vegetation
    		// texture state vegetation, trimesh
    		TrimeshGeometryBatch batch = trimeshBatchMap.get(key);
	    	if (batch==null)
	    	{
	    		TriMesh tri = VegetationSetup.getVegTrimesh(internal,place, place.cube, core, (TextureStateVegetationModel)m, 0, 0, 0f, 100f);
	    		batch = new TrimeshGeometryBatch(m.id,core,tri,internal,place);
	    		batch.model = m;
	    		batch.key = key;
	    		if (internal)
	    		{
	    			batch.setAnimated(false,internal); // inside no wind
	    			sEngine.intRootNode.attachChild(batch.parent);
	    			//sEngine.intRootNode.updateRenderState();
	    		} else
	    		{
	    			batch.setAnimated(J3DCore.ANIMATED_GRASS && m.windAnimation,internal); // animate wind only outside
	    			sEngine.extRootNode.attachChild(batch.parent);
	    			//sEngine.extRootNode.updateRenderState();
	    		}
    			batch.parent.setCullMode(Node.CULL_NEVER); // set culling to NEVER for the first rendering...
    			core.gameState.getCurrentStandingEngine().newNodesToSetCullingDynamic.add(batch.parent); // adding it to newly placed nodes
	    		trimeshBatchMap.put(key, batch);
	    		batch.lockTransforms();
	    		batch.lockShadows();
	    	}
	    	if ( (batch.getLocks()&Node.LOCKED_BOUNDS)>0)
	    	{
	    		batch.unlockBranch();
		    	batch.unlockBounds();
	    	}
	    	
    		int quadQuantity = ((TextureStateVegetationModel)m).quadQuantity*(J3DCore.DOUBLE_GRASS?2:1);
    		boolean sparse = true;
    		int counter = 0;
			float variationCutter = 160f;
			float heightPercent;
			float[] cornerHeights = null;
    		for (int k=0; k<quadQuantity; k++)
    		{
    			if (sparse && counter>0) {
    				if (HashUtil.mixPercentage(place.cube.cube.x+k, place.cube.cube.y, place.cube.cube.z)<20) continue;
    			}
    			for (int j=0; j<quadQuantity; j++) {
    				if (sparse) {
    					if (HashUtil.mixPercentage(place.cube.cube.x+k+j, place.cube.cube.y, place.cube.cube.z)<60) continue;
    				}
    				counter++;
    				TriMesh tri;
    				cornerHeights = place.cube.cube.cornerHeights;
					if (cornerHeights!=null)
					{
						// NORTH - SOUTH -> Z
						// WEST _ EAST -> X
						tri = VegetationSetup.getVegTrimesh(internal, place,place.cube, core, (TextureStateVegetationModel)m, k, j,cornerHeights,variationCutter);
						
					}
					else
					if (place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP) 
    				{
    					tri = VegetationSetup.getVegTrimesh(internal,place,place.cube, core, (TextureStateVegetationModel)m, k, j,0f, 100f);
    				} else
    				{
    					heightPercent = 0;
    					variationCutter = 160f;	

    					{
    					
	    					if (place.cube.cube.steepDirection==J3DCore.SOUTH)
	    					{
	    						heightPercent = (j*1f)/quadQuantity;
	    						
	    					}
	    					if (place.cube.cube.steepDirection==J3DCore.NORTH)
	    					{
	    						heightPercent = ((quadQuantity-j)*1f)/quadQuantity;
	    					}
	    					if (place.cube.cube.steepDirection==J3DCore.WEST)
	    					{
	    						heightPercent = (k*1f)/quadQuantity;
	    					}
	    					if (place.cube.cube.steepDirection==J3DCore.EAST)
	    					{
	    						heightPercent = ((quadQuantity-k)*1f)/quadQuantity;
	    					}
	    					heightPercent*=0.9;
	    					tri = VegetationSetup.getVegTrimesh(internal, place,place.cube, core, (TextureStateVegetationModel)m, k, j,2f*heightPercent,variationCutter);
    					}
    				}
					
    	    		batch.addItem(place,tri);
    	    		
    			}
    		}
    	}
    }
    public void removeItem(boolean internal, Model m, NodePlaceholder place, boolean farView)
    {
    	String key = getKey(internal, m, place, farView);
    	if (m.type!=Model.TEXTURESTATEVEGETATION) {
	     	ModelGeometryBatch batch = modelBatchMap.get(key);
	    	if (batch!=null)
	    	{
		    	/*if ( (batch.getLocks()&Node.LOCKED_BOUNDS)>0)
		    	{
		    		batch.unlockBranch();
			    	batch.unlockBounds();
			    	batch.unlockMeshes();
		    	}*/
	    		batch.removeItem(place);
	    	}
    	} else
    	if (m.type==Model.TEXTURESTATEVEGETATION) {
    		//if (place.cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP) return; // on steep, no vegetation
    		// texture state vegetation, trimesh
    		TrimeshGeometryBatch batch = trimeshBatchMap.get(key);
    		if (batch!=null)
    		{
    	    	/*if ( (batch.getLocks()&Node.LOCKED_BOUNDS)>0)
    	    	{
    	    		batch.unlockBranch();
    		    	batch.unlockBounds();
    		    	batch.unlockMeshes();
    	    	}*/
    			batch.removeItem(place);
    		}
    	}
    }

    public void removeBillboardVegetationItem(boolean internal, Model m, NodePlaceholder place, boolean farView, BillboardPartVegetation vegetationNode)
    {
    	String key = getKey(internal, m, place, farView);
    	if (m.type==Model.PARTLYBILLBOARDMODEL) {
    		// modelGeomBatch part
    		{
		     	ModelGeometryBatch batch = modelBatchMap.get(key);
		    	if (batch!=null)
		    	{
	    	    	/*if ( (batch.getLocks()&batch.LOCKED_BOUNDS)>0)
	    	    	{
	    	    		batch.unlockBranch();
	    		    	batch.unlockBounds();
	    		    	batch.unlockMeshes();
	    	    	}*/
		    		
			    	for (Spatial s:((Node)(vegetationNode.foliagelessModelSpatial)).getChildren())
			    	{
			    		if (s instanceof Node)
			    		{
			    			for (Spatial sh:((Node)s).getChildren())
			    			{
			    				if (sh instanceof SharedMesh)
			    				{
			    					TriMesh mesh = ((SharedMesh)sh).getTarget();
			    					batch.removeItem(place,mesh);
			    				}
			    			}
			    		}
			    	}
			    	place.modelGeomBatchInstance = null;
		    		//batch.removeItem(place);
		    	}
    		}
    		// trimeshGeomBatch part
    		{
    			PartlyBillboardModel pbm = ((PartlyBillboardModel)m);
    			if (pbm.atlasTexture)
    			{
    				key = getBillboardVegetationAtlasKey(internal, m, place, farView);
    			}
        		TrimeshGeometryBatch batch = trimeshBatchMap.get(key);
        		if (batch!=null)
        		{
        	    	/*if ( (batch.getLocks()&Node.LOCKED_BOUNDS)>0)
        	    	{
        	    		batch.unlockBranch();
        		    	batch.unlockBounds();
        		    	batch.unlockMeshes();
        	    	}*/
        			batch.removeItem(place);
        			place.trimeshGeomBatchInstance = null;
        		}
    		}
    	}
    }

    
    public void addBillboardVegetationItem(J3DStandingEngine sEngine, boolean internal, Model m, NodePlaceholder place, boolean farView, BillboardPartVegetation vegetationNode) {
    	String key = getKey(internal, m, place, farView);

    	if (m.type!=Model.TEXTURESTATEVEGETATION) {
    		// modelGeomBatch trunk part
    		{
		    	ModelGeometryBatch batch = modelBatchMap.get(key);
		    	if (batch==null)
		    	{
		    		batch = new ModelGeometryBatch(core,m,place,vegetationNode);
		    		batch.key = key;
		    		if (internal)
		    		{
		    			sEngine.intWaterRefNode.attachChild(batch.parent);
		    			//sEngine.intRootNode.updateRenderState();
		    		} else
		    		{
		    			sEngine.extWaterRefNode.attachChild(batch.parent);
		    			//sEngine.extRootNode.updateRenderState();
		    		}
	    			batch.parent.setCullMode(Node.CULL_NEVER); // set culling to NEVER for the first rendering...
	    			core.gameState.getCurrentStandingEngine().newNodesToSetCullingDynamic.add(batch.parent); // adding it to newly placed nodes
		    		modelBatchMap.put(key, batch);
		    		if (locking)
		    		{
		    			batch.lockTransforms();
		    			batch.lockShadows();
		    		}
		    	}
		    	if ( (batch.getLocks()&Node.LOCKED_BOUNDS)>0)
		    	{
		    		batch.unlockBranch();
			    	batch.unlockBounds();
			    	batch.unlockMeshes();
		    	}
		    	for (Spatial s:((Node)(vegetationNode.foliagelessModelSpatial)).getChildren())
		    	{
		    		if (s instanceof Node)
		    		{
		    			for (Spatial sh:((Node)s).getChildren())
		    			{
		    				if (sh instanceof SharedMesh)
		    				{
		    					TriMesh mesh = ((SharedMesh)sh).getTarget();
		    					batch.addItem(place,mesh);
		    				}
		    			}
		    		}
		    	}
		    	batch.updateGeometricState(0f, true); // TODO why is it working only if put here?
	    	
    		}	    	
    		
    		// timeshGeomBatch part (foliage)
    		{
    			PartlyBillboardModel pbm = ((PartlyBillboardModel)m);
    			if (pbm.atlasTexture)
    			{
    				key = getBillboardVegetationAtlasKey(internal, m, place, farView);
    				System.out.println("ATLAS VEG: "+key+" "+m.id);

    			}
    			TrimeshGeometryBatch batch = trimeshBatchMap.get(key);
    	    	if (batch==null)
    	    	{
    	    		TriMesh tri = vegetationNode.containedFoliageMeshes.iterator().next(); 
    	    			
    	    			//VegetationSetup.getVegTrimesh(internal,place, place.cube, core, (TextureStateVegetationModel)m, 0, 0, 0f, 100f);
    	    		batch = new TrimeshGeometryBatch(m.id,core,tri,internal,place);
    	    		batch.model = m;
    	    		batch.key = key;
    	    		if (internal)
    	    		{
    	    			batch.setAnimated(false,internal); // inside no wind
    	    			sEngine.intWaterRefNode.attachChild(batch.parent);
    	    			//sEngine.intRootNode.updateRenderState();
    	    		} else
    	    		{
    	    			batch.setAnimated(J3DCore.ANIMATED_GRASS && m.windAnimation,internal); // animate wind only outside
    	    			sEngine.extWaterRefNode.attachChild(batch.parent);
    	    			//sEngine.extRootNode.updateRenderState();
    	    		}
        			batch.parent.setCullMode(Node.CULL_NEVER); // set culling to NEVER for the first rendering...
        			core.gameState.getCurrentStandingEngine().newNodesToSetCullingDynamic.add(batch.parent); // adding it to newly placed nodes
    	    		trimeshBatchMap.put(key, batch);
    	    		batch.lockTransforms();
    	    		batch.lockShadows();
    	    	}
    	    	if ( (batch.getLocks()&Node.LOCKED_BOUNDS)>0)
    	    	{
    	    		batch.unlockBranch();
    		    	batch.unlockBounds();
    	    	}
    	    	
    	    	for (TriMesh tri:vegetationNode.containedFoliageMeshes)
    	    	{
    	    		batch.addItem(place,tri,true);
    	    	}

    		}
	    	
    	}
    }
    
    
    
    boolean locking = true;
    public void lockAll()
    {
    	if (!locking) return;
    	for (TrimeshGeometryBatch batch: trimeshBatchMap.values()) {
    		
    		if (batch.parent!=null)
    		{
    			if ((batch.getLocks()&Node.LOCKED_BOUNDS)>0) {continue;}
    			//batch.lockMeshes(); // XXX you shouldn't lock meshes of trimesh , billboard goes wrong
    			batch.updateModelBound();
    			batch.updateRenderState();
    			batch.lockBounds();
    			batch.lockBranch();
    		}
    	}
    	for (ModelGeometryBatch batch: modelBatchMap.values())
    	{
    		if ((batch.getLocks()&Node.LOCKED_BOUNDS)>0) {continue;}
    		//batch.updateGeometricState(0f, true);
	    	batch.lockBounds();
	    	batch.lockMeshes();
    		batch.lockBranch();
    	}
    }
    public void unlockAll()
    {
    	if (!locking) return;
    	/*for (TrimeshGeometryBatch batch: trimeshBatchMap.values()) {
    		
    		if (batch.parent!=null)
    		{
    			batch.unlockMeshes();
    			batch.unlockBounds();
    			batch.unlockBranch();
    		}
    	}*/

    }
    public void unlockAllPlus()
    {
    	if (!locking) return;
    	for (TrimeshGeometryBatch batch: trimeshBatchMap.values()) {
    		
    		if (batch.parent!=null)
    		{
    			batch.unlockBranch();
    			batch.unlockMeshes();
    			batch.unlockBounds();
    			batch.unlockTransforms();
    		}
    	}
    	for (ModelGeometryBatch batch: modelBatchMap.values()) {
    		
    		if (batch.parent!=null)
    		{
    			batch.unlockBranch();
    			batch.unlockMeshes();
    			batch.unlockTransforms();
    			batch.unlockBounds();
    		}
    	}

    }

    HashSet<TrimeshGeometryBatch> trimeshRemovables = new HashSet<TrimeshGeometryBatch>();
    HashSet<ModelGeometryBatch> modelRemovables = new HashSet<ModelGeometryBatch>();

    /**
     * Updates all batches, removes removable (empty ones).
     */
    public void updateAll()
    {
    	Jcrpg.LOGGER.info(" -------- UPDATE ALL "+modelBatchMap.size()+ " "+trimeshBatchMap.size());
    	{
	    	HashSet<ModelGeometryBatch> removables = modelRemovables;
	    	modelRemovables.clear();
	    	for (ModelGeometryBatch batch: modelBatchMap.values())
	    	{
	    		boolean removableFlag = true;
	    		if (batch.visible.size()!=0)
	    		{
	    			if (J3DCore.SHADOWS)
	    			{
		    			if (batch.model.type==Model.PARTLYBILLBOARDMODEL || batch.model.shadowCaster)
		    			{
		    				boolean found = false;
		    				for (HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> h:batch.visible.values())
		    				{
		    					for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> i:h)
		    					{
		    						Vector3f pos = batch.parent.getWorldTranslation().add(i.getAttributes().getTranslation());
		    						if (pos.distance(core.getCamera().getLocation())<J3DCore.RENDER_SHADOW_DISTANCE)
		    						{
		    							core.sPass.addOccluder(batch);
		    							found = true;
		    						}
		    					}
		    				}
		    				if (!found)
		    				{
		    					core.sPass.removeOccluder(batch);
		    				}
		    				
		    			}
		    			// look for models that goes texturized with shadow...
		    			if (batch.model.type==Model.SIMPLEMODEL && ((SimpleModel)batch.model).generatedGroundModel)
		    			{
		    				boolean found = false;
		    				for (HashSet<GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>> h:batch.visible.values())
		    				{
		    					for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> i:h)
		    					{
		    						Vector3f pos = batch.parent.getWorldTranslation().add(i.getAttributes().getTranslation());
		    						if (pos.distance(core.getCamera().getLocation())<J3DCore.RENDER_SHADOW_DISTANCE)
		    						{
				    					if (!core.sPass.contains(batch))
				    					{
				    						core.sPass.add(batch);
				    					}
		    							found = true;
		    						}
		    					}
		    				}		    				
			    			if (!found)
			    			{
			    				core.sPass.remove(batch);
			    			}
		    				
		    			}
	    			}
	    			removableFlag = false;
	    			if (batch.updateNeeded)
	    			{
	    				//batch.updateGeometricState(0f, true);
	    				if (J3DCore.VBO_ENABLED)
	    				{
	    					if (batch.getVBOInfo(0)!=null)
		    				{
		    					DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(batch.getVertexBuffer(0));
		    				}
		    				VBOInfo v = new VBOInfo(true);
		    				batch.setVBOInfo(v);
	    				}
	    			}
	    		} else
	    		{
	    			if (batch.isUpdateNeededAndSwitchIt())
	    			{
	    				batch.parent.updateRenderState();
	    			}
	    		}
				if (removableFlag) {
					
    				if (J3DCore.VBO_ENABLED)
    				{
	    				if (batch.getVBOInfo(0)!=null)
	    				{
	    					DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(batch.getVertexBuffer(0));
	    				}
    				}

					batch.parent.removeFromParent();
					if (J3DCore.SHADOWS)
					{
						core.sPass.remove(batch);
						core.sPass.removeOccluder(batch);
					}
					core.removeSolidColorQuadsRecoursive(batch.parent);
					
					removables.add(batch);
				}
	    	}
	    	modelBatchMap.values().removeAll(removables);
    	}
    	{
	    	HashSet<TrimeshGeometryBatch> removables = trimeshRemovables;
	    	removables.clear();
	    	for (TrimeshGeometryBatch batch: trimeshBatchMap.values())
	    	{
	    		boolean removableFlag = true;
	    		if (batch.visible.size()!=0)
	    		{
	    			if (J3DCore.SHADOWS)
	    			{
		    			if (batch.model.type==Model.PARTLYBILLBOARDMODEL)
		    			{
		    				boolean found = false;
		    				for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> i:batch.visible)
		    				{
	    						Vector3f pos = batch.parent.getWorldTranslation().add(i.getAttributes().getTranslation());
	    						if (pos.distance(core.getCamera().getLocation())<J3DCore.RENDER_SHADOW_DISTANCE)
	    						{
	    							core.sPass.addOccluder(batch);
	    							found = true;
	    						}
		    				}
		    				if (!found)
		    				{
		    					core.sPass.removeOccluder(batch);	
		    				}
		    			}
	    			}
	    			if (batch.isUpdateNeededAndSwitchIt())
	    			{
	    				/*if (batch.getVBOInfo(0)!=null)
	    					DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(batch.getVertexBuffer(0));
	    				VBOInfo v = new VBOInfo(true);
	    				v.setVBOVertexEnabled(false);
	    				batch.setVBOInfo(v);*/
	    				
	    			}
	    			removableFlag = false;
	    		}
				if (removableFlag) {
    				
    				if (J3DCore.VBO_ENABLED)
    				{
						if (batch.getVBOInfo(0)!=null)
	    				{
	    					DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(batch.getVertexBuffer(0));
	    				}
    				}
    				
					batch.parent.removeFromParent();
					if (J3DCore.SHADOWS)
					{
						core.sPass.remove(batch);
						core.sPass.removeOccluder(batch);
					}
					core.removeSolidColorQuadsRecoursive(batch.parent);
					removables.add(batch);
				} else
				{
					//if (batch.model!=null && (batch.model.alwaysRenderBatch || batch.model.type==Model.PARTLYBILLBOARDMODEL)) continue;
					//if (batch.parent.getCullMode()!=TriMesh.CULL_NEVER) 
					{
						//float dist = batch.parent.getLocalTranslation().distance(core.getCamera().getLocation());
						// TODO bad distance is setting cull always here... TODO make a boundary edges based calc instead
						/*if (dist>J3DCore.RENDER_GRASS_DISTANCE*2)
						{
							//System.out.println("CULLING "+batch.parent.getLocalTranslation()+ " " + core.getCamera().getLocation());
							
							batch.parent.setCullMode(TriMesh.CULL_ALWAYS);
							batch.parent.updateRenderState();
						} else
						{
							batch.parent.setCullMode(TriMesh.CULL_DYNAMIC);
							batch.parent.updateRenderState();
						}*/
					}
				}
	    	}
	    	trimeshBatchMap.values().removeAll(removables);
    	}
    }
    

    /**
     * clearing batch maps and all.
     */
    public void clearAll()    
    {
    	modelBatchMap.clear();
    	trimeshBatchMap.clear();    	
    }
}
