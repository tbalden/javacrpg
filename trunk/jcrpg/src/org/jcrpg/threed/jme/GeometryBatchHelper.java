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
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.jcrpg.threed.standing.J3DStandingEngine;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.scene.Node;
import com.jme.scene.TriMesh;

public class GeometryBatchHelper {

	public static HashMap<String, ModelGeometryBatch> modelBatchMap = new HashMap<String, ModelGeometryBatch>();
	public static HashMap<String, TrimeshGeometryBatch> trimeshBatchMap = new HashMap<String, TrimeshGeometryBatch>();
	J3DCore core;
	
	public GeometryBatchHelper(J3DCore core)
	{
		this.core = core;		
	}
	
	public static int SIMPLE_MODEL_BATCHED_SPACE_SIZE = 6;
	public static int QUAD_MODEL_BATCHED_SPACE_SIZE = 6;
	public static int TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE = 6;
	
	
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
    	String key = m.type+m.id+internal+(farView||place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP);
    	if (m.type==Model.SIMPLEMODEL) { // grouping based on coordinate units
    		SimpleModel sm = (SimpleModel)m;
    		if (sm.textureName!=null)
    		{
    			if (sm.generatedGroundModel)
    			{
    				key = m.type+sm.getTexture(place)+internal+(farView||place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP);
    				if (place.neighborCubeData==null)
    					place.neighborCubeData = GeoTileLoader.getNeighborCubes(place);
    				key+=place.neighborCubeData.getTextureKeyPartForBatch();
    			} else
    			{
    				key = m.type+sm.getTexture(place)+internal+(farView||place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP);
    			}
    		}
    		if (sm.xGeomBatchSize==-1) 
    		{
    			if (sm.yGeomBatchSize==-1) 
    			{
    				key+=((place.cube.cube.x/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+((place.cube.cube.z/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+"_"+(place.cube.cube.y/(SIMPLE_MODEL_BATCHED_SPACE_SIZE*yLevelMul));
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
    		key+=(place.cube.cube.x/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+place.cube.cube.y/yLevelMul;
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
	    			sEngine.intRootNode.attachChild(batch.parent);
	    			//sEngine.intRootNode.updateRenderState();
	    		} else
	    		{
	    			sEngine.extRootNode.attachChild(batch.parent);
	    			//sEngine.extRootNode.updateRenderState();
	    		}
    			batch.parent.setCullMode(Node.CULL_NEVER); // set culling to NEVER for the first rendering...
    			J3DStandingEngine.newNodesToSetCullingDynamic.add(batch.parent); // adding it to newly placed nodes
	    		modelBatchMap.put(key, batch);
	    		batch.lockTransforms();
	    		batch.lockShadows();
	    	}
	    	batch.addItem(place);
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
    			J3DStandingEngine.newNodesToSetCullingDynamic.add(batch.parent); // adding it to newly placed nodes
	    		trimeshBatchMap.put(key, batch);
	    		batch.lockTransforms();
	    		batch.lockShadows();
	    	}
    		int quadQuantity = ((TextureStateVegetationModel)m).quadQuantity*(J3DCore.DOUBLE_GRASS?2:1);
    		boolean sparse = true;
    		int counter = 0;
			float variationCutter = 160f;
			float NW = 0;
			float NE = 0;
			float SW = 0;
			float SE = 0;
			float xPerc = 0;
			float zPerc = 0;
			float heightPercent;
			float[] cornerHeights = null;
			if (place.cube.cube.cornerHeights!=null)
			{	
				cornerHeights = place.cube.cube.cornerHeights;
				NW = place.cube.cube.cornerHeights[0];
				NE = place.cube.cube.cornerHeights[1];
				SW = place.cube.cube.cornerHeights[2];
				SE = place.cube.cube.cornerHeights[3];
				
			}
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
					if (cornerHeights!=null)
					{
						// NORTH - SOUTH -> Z
						// WEST _ EAST -> X
/*						zPerc = ((j*1f)/quadQuantity);
						xPerc = 1f-((k*1f)/quadQuantity);
						heightPercent = 
							(
							( NW * ((     xPerc  +      zPerc) / 2f) ) +
							( NE * ((1f - xPerc  +      zPerc) / 2f) ) +
							( SW * ((     xPerc  + 1f - zPerc) / 2f) ) +
							( SE * ((1f - xPerc  + 1f - zPerc) / 2f) )
							)
							;*/
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
	    		batch.removeItem(place);
	    	}
    	} else
    	if (m.type==Model.TEXTURESTATEVEGETATION) {
    		//if (place.cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP) return; // on steep, no vegetation
    		// texture state vegetation, trimesh
    		TrimeshGeometryBatch batch = trimeshBatchMap.get(key);
    		if (batch!=null)
    		{
    			batch.removeItem(place);
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
    			batch.parent.lockMeshes();
    		}
    	}
    }
    public void unlockAll()
    {
    	if (!locking) return;
    	for (TrimeshGeometryBatch batch: trimeshBatchMap.values()) {
    		
    		if (batch.parent!=null)
    		{
    			batch.parent.unlockMeshes();
    		}
    	}
    }
    HashSet<TrimeshGeometryBatch> trimeshRemovables = new HashSet<TrimeshGeometryBatch>();
    HashSet<ModelGeometryBatch> modelRemovables = new HashSet<ModelGeometryBatch>();

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
	    			removableFlag = false;
	    		}
				if (removableFlag) {
					batch.parent.removeFromParent();
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
	    			removableFlag = false;
	    		}
				if (removableFlag) {
					batch.parent.removeFromParent();
					core.removeSolidColorQuadsRecoursive(batch.parent);
					removables.add(batch);
				} else
				{
					if (batch.model!=null && batch.model.alwaysRenderBatch) continue;
					//if (batch.parent.getCullMode()!=TriMesh.CULL_NEVER) 
					{
						if (batch.parent.getWorldTranslation().add(batch.avarageTranslation).distanceSquared(core.getCamera().getLocation())>J3DCore.RENDER_GRASS_DISTANCE*J3DCore.RENDER_GRASS_DISTANCE*4)
						{
							batch.parent.setCullMode(TriMesh.CULL_ALWAYS);
							batch.parent.updateRenderState();
						} else
						{
							batch.parent.setCullMode(TriMesh.CULL_DYNAMIC);
							batch.parent.updateRenderState();
						}
					}
				}
	    	}
	    	trimeshBatchMap.values().removeAll(removables);
    	}
    }
    

    public void clearAll()    
    {
    	modelBatchMap.clear();
    	trimeshBatchMap.clear();    	
    }
}
