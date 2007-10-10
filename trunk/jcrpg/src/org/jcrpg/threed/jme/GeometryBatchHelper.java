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
import org.jcrpg.threed.VegetationSetup;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.scene.TriMesh;

public class GeometryBatchHelper {

	static HashMap<String, ModelGeometryBatch> modelBatchMap = new HashMap<String, ModelGeometryBatch>();
	static HashMap<String, TrimeshGeometryBatch> trimeshBatchMap = new HashMap<String, TrimeshGeometryBatch>();
	J3DCore core;
	
	public GeometryBatchHelper(J3DCore core)
	{
		this.core = core;		
	}
	
	public static int SIMPLE_MODEL_BATCHED_SPACE_SIZE = 2;
	public static int QUAD_MODEL_BATCHED_SPACE_SIZE = 10;
	public static int TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE = 10;
	
	/**
	 * Returns Grouping key for batch objects. The key unifies a group of nodes into a batch.
	 * @param internal
	 * @param m
	 * @param place
	 * @return Key.
	 */
	private String getKey(boolean internal, Model m, NodePlaceholder place, boolean farView)
	{
		float viewMul = 1;
		float yLevelMul = 1;
		if (farView) {
			viewMul = J3DCore.FARVIEW_GAP * 4f;
			yLevelMul = 4;
		}
    	String key = m.type+m.id+internal+(farView||place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP);
    	if (m.type==Model.SIMPLEMODEL) { // grouping based on coordinate units
    		SimpleModel sm = (SimpleModel)m;
    		if (sm.textureName!=null)
    		{
    			key = m.type+sm.textureName+internal+(farView||place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP);
    		}
    		if (sm.xGeomBatchSize==-1) 
    		{
    			if (sm.yGeomBatchSize==-1) 
    			{
    				key+=((place.cube.cube.x/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+""+((place.cube.cube.z/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+""+(place.cube.cube.y/(SIMPLE_MODEL_BATCHED_SPACE_SIZE*yLevelMul));
    			}
    			else
    			{
    				key+=((place.cube.cube.x/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+""+((place.cube.cube.z/SIMPLE_MODEL_BATCHED_SPACE_SIZE)/viewMul)+""+(place.cube.cube.y/(sm.yGeomBatchSize*yLevelMul));
    			}
    		} else
    		{
    			if (sm.yGeomBatchSize==-1) 
    			{
    				key+=((place.cube.cube.x/sm.xGeomBatchSize)/viewMul)+""+((place.cube.cube.z/sm.xGeomBatchSize)/viewMul)+""+(place.cube.cube.y/(SIMPLE_MODEL_BATCHED_SPACE_SIZE*yLevelMul));
    			}
    			else
    			{
    				key+=((place.cube.cube.x/sm.xGeomBatchSize)/viewMul)+""+((place.cube.cube.z/sm.xGeomBatchSize)/viewMul)+""+(place.cube.cube.y/(sm.yGeomBatchSize*yLevelMul));
    			}
    		}
    	} else
    	if (m.type==Model.TEXTURESTATEVEGETATION)
    	{
    		key+=(place.cube.cube.x/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+place.cube.cube.y/yLevelMul;
    	} else
    	{   // other models only by Y // quad
    		key+=(place.cube.cube.x/QUAD_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/QUAD_MODEL_BATCHED_SPACE_SIZE)+""+place.cube.cube.y/yLevelMul;
    	}
    	return key+farView;
	}
    /**
     * Use geometry instancing to create a mesh containing a number of box
     * instances
     * @param farView tells if this item must be magnified for farview.
     */
    public void addItem(boolean internal, Model m, NodePlaceholder place, boolean farView) {
    	String key = getKey(internal, m, place, farView);

    	if (m.type!=Model.TEXTURESTATEVEGETATION) {
	    	ModelGeometryBatch batch = modelBatchMap.get(key);
	    	if (batch==null)
	    	{
	    		batch = new ModelGeometryBatch(core,m);
	    		if (m.type == Model.QUADMODEL && ((QuadModel)m).waterQuad)
	    		{
	    			J3DCore.waterEffectRenderPass.setWaterEffectOnSpatial(batch);
	    		}
	    		if (internal)
	    		{
	    			core.intRootNode.attachChild(batch.parent);
	    			core.intRootNode.updateRenderState();
	    		} else
	    		{
	    			core.extRootNode.attachChild(batch.parent);
	    			core.extRootNode.updateRenderState();
	    		}
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
	    		TriMesh tri = VegetationSetup.getVegTrimesh(place, place.cube, core, (TextureStateVegetationModel)m, 0, 0, 0f, 100f);
	    		batch = new TrimeshGeometryBatch(m.id,core,tri);
	    		if (internal)
	    		{
		    		batch.animated = false; // inside no wind
	    			core.intRootNode.attachChild(batch.parent);
	    			core.intRootNode.updateRenderState();
	    		} else
	    		{
		    		batch.animated = true && J3DCore.ANIMATED_GRASS; // animate wind only outside
	    			core.extRootNode.attachChild(batch.parent);
	    			core.extRootNode.updateRenderState();
	    		}
	    		trimeshBatchMap.put(key, batch);
	    		batch.lockTransforms();
	    		batch.lockShadows();
	    	}
    		int quadQuantity = ((TextureStateVegetationModel)m).quadQuantity*(J3DCore.DOUBLE_GRASS?2:1);

    		for (int k=0; k<quadQuantity; k++)
    		{
    			for (int j=0; j<quadQuantity; j++) {
    				TriMesh tri;
    				if (place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP) 
    				{
    					tri = VegetationSetup.getVegTrimesh(place,place.cube, core, (TextureStateVegetationModel)m, k, j,0f, 100f);
    				} else
    				{
    					float heightPercent = 0;
    					float variationCutter = 160f;
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
    					tri = VegetationSetup.getVegTrimesh(place,place.cube, core, (TextureStateVegetationModel)m, k, j,2f*heightPercent,variationCutter);
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
    		if (place.cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP) return; // on steep, no vegetation
    		// texture state vegetation, trimesh
    		TrimeshGeometryBatch batch = trimeshBatchMap.get(key);
    		if (batch!=null)
    		{
    			batch.removeItem(place);
    		}
    	}
    }
    public void updateAll()
    {
    	System.out.println(" -------- UPDATE ALL "+modelBatchMap.size()+ " "+trimeshBatchMap.size());
    	{
	    	HashSet<ModelGeometryBatch> removables = new HashSet<ModelGeometryBatch>();
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
	    	HashSet<TrimeshGeometryBatch> removables = new HashSet<TrimeshGeometryBatch>();
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
					if (batch.avarageTranslation.distanceSquared(core.getCamera().getLocation())>J3DCore.RENDER_GRASS_DISTANCE*J3DCore.RENDER_GRASS_DISTANCE*4)
					{
						batch.setCullMode(TriMesh.CULL_ALWAYS);
						batch.updateRenderState();
					} else
					{
						batch.setCullMode(TriMesh.CULL_DYNAMIC);
						batch.updateRenderState();
					}
				}
	    	}
	    	trimeshBatchMap.values().removeAll(removables);
    	}
    }
    

}
