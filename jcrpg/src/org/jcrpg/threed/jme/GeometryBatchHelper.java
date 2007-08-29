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
import org.jcrpg.threed.VegetationSetup;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchInstanceAttributes;
import org.jcrpg.threed.jme.geometryinstancing.GeometryBatchSpatialInstance;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.scene.TriMesh;

public class GeometryBatchHelper {

	static HashMap<String, ModelGeometryBatch> modelBatchMap = new HashMap<String, ModelGeometryBatch>();
	static HashMap<String, TrimeshGeometryBatch> trimeshBatchMap = new HashMap<String, TrimeshGeometryBatch>();
	static J3DCore core;
	
	public GeometryBatchHelper(J3DCore core)
	{
		this.core = core;		
	}
	
	public static int SIMPLE_MODEL_BATCHED_SPACE_SIZE = 2;
	public static int QUAD_MODEL_BATCHED_SPACE_SIZE = 8;
	public static int TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE = 8;
	
	/**
	 * Returns Grouping key for batch objects
	 * @param internal
	 * @param m
	 * @param place
	 * @return Key.
	 */
	private String getKey(boolean internal, Model m, NodePlaceholder place)
	{
    	String key = m.type+m.id+internal+(place.cube.cube.steepDirection==SurfaceHeightAndType.NOT_STEEP);
    	if (m.type==Model.SIMPLEMODEL) { // grouping based on coordinate units
    		key+=(place.cube.cube.x/SIMPLE_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/SIMPLE_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.y/SIMPLE_MODEL_BATCHED_SPACE_SIZE);
    	} else
    	if (m.type==Model.TEXTURESTATEVEGETATION)
    	{
    		key+=(place.cube.cube.x/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/TEXSTATEVEG_MODEL_BATCHED_SPACE_SIZE)+""+place.cube.cube.y;
    	} else
    	{   // other models only by Y
    		key+=(place.cube.cube.x/QUAD_MODEL_BATCHED_SPACE_SIZE)+""+(place.cube.cube.z/QUAD_MODEL_BATCHED_SPACE_SIZE)+""+place.cube.cube.y;
    	}
    	return key;
	}
    /**
     * Use geometry instancing to create a mesh containing a number of box
     * instances
     */
    public void addItem(boolean internal, Model m, NodePlaceholder place) {
    	String key = getKey(internal, m, place);

    	if (m.type!=m.TEXTURESTATEVEGETATION) {
	    	ModelGeometryBatch batch = modelBatchMap.get(key);
	    	if (batch==null)
	    	{
	    		batch = new ModelGeometryBatch(core,m);
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
	    	}
	    	batch.addItem(place);
    	} else
    	if (m.type==Model.TEXTURESTATEVEGETATION) {
    		// texture state vegetation, trimesh
    		TrimeshGeometryBatch batch = trimeshBatchMap.get(key);
	    	if (batch==null)
	    	{
	    		TriMesh tri = VegetationSetup.getVegTrimesh(place, place.cube, core, (TextureStateVegetationModel)m, 0, 0);
	    		batch = new TrimeshGeometryBatch(core,tri);
	    		if (internal)
	    		{
	    			core.intRootNode.attachChild(batch.parent);
	    			core.intRootNode.updateRenderState();
	    		} else
	    		{
	    			core.extRootNode.attachChild(batch.parent);
	    			core.extRootNode.updateRenderState();
	    		}
	    		trimeshBatchMap.put(key, batch);
	    	}
    		int quadQuantity = ((TextureStateVegetationModel)m).quadQuantity*(J3DCore.DOUBLE_GRASS?2:1);

    		for (int k=0; k<quadQuantity; k++)
    		{
    			for (int j=0; j<quadQuantity; j++) {
    	    		TriMesh tri = VegetationSetup.getVegTrimesh(place,place.cube, core, (TextureStateVegetationModel)m, k, j);
    	    		batch.addItem(place,tri);
    	    		
    			}
    		}
	    	
    		
    	}
    }
    public void removeItem(boolean internal, Model m, NodePlaceholder place)
    {
    	String key = getKey(internal, m, place);
    	if (m.type!=m.TEXTURESTATEVEGETATION) {
	     	ModelGeometryBatch batch = modelBatchMap.get(key);
	    	if (batch!=null)
	    	{
	    		batch.removeItem(place);
	    	}
    	} else
    	if (m.type==Model.TEXTURESTATEVEGETATION) {
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
    	{
	    	HashSet<ModelGeometryBatch> removables = new HashSet<ModelGeometryBatch>();
	    	for (ModelGeometryBatch batch: modelBatchMap.values())
	    	{
	    		boolean removableFlag = true;
				for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instanceEn : batch.getInstances()) {
					if (instanceEn.getAttributes().isVisible())
					{
						removableFlag = false;
						break;
					}
				}
				if (removableFlag) {
					batch.parent.removeFromParent();
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
				for (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> instanceEn : batch.getInstances()) {
					if (instanceEn.getAttributes().isVisible())
					{
						removableFlag = false;
						break;
					}
				}
				if (removableFlag) {
					batch.parent.removeFromParent();
					removables.add(batch);
				}
	    	}
	    	trimeshBatchMap.values().removeAll(removables);
    	}
    }
    

}
