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

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.scene.model.QuadModel;

public class GeometryBatchHelper {

	static HashMap<String, QuadModelGeometryBatch> batchMap = new HashMap<String, QuadModelGeometryBatch>();
	static J3DCore core;
	
	public GeometryBatchHelper(J3DCore core)
	{
		this.core = core;		
	}
	
    /**
     * Use geometry instancing to create a mesh containing a number of box
     * instances
     */
    public void addItem(boolean internal, QuadModel m, NodePlaceholder place) {
        // A box that will be instantiated
    	QuadModelGeometryBatch batch = batchMap.get(m.id+internal);
    	if (batch==null)
    	{
    		batch = new QuadModelGeometryBatch(core,m);
    		if (internal)
    		{
    			core.intRootNode.attachChild(batch.parent);
    			core.intRootNode.updateRenderState();
    		} else
    		{
    			core.extRootNode.attachChild(batch.parent);
    			core.extRootNode.updateRenderState();
    		}
    		batchMap.put(m.id+internal, batch);
    	}
    	batch.addItem(place);
    }
    public void removeItem(boolean internal, QuadModel m, NodePlaceholder place)
    {
    	QuadModelGeometryBatch batch = batchMap.get(m.id+internal);
    	if (batch!=null)
    	{
    		batch.removeItem(place);
    	}
    }
    public void updateAll()
    {
    	for (QuadModelGeometryBatch batch: batchMap.values())
    	{
    		batch.preCommit();
    		//batch.setModelBound(new BoundingBox(new Vector3f(core.getCamera().getLocation()),100,0.2f,100));
    		//batch.preCommit();
    		//batch.setLightCombineMode(LightState.COMBINE_FIRST);
    		//batch.setModelBound(null);//new BoundingBox());
    		//batch.setCullMode(Node.CULL_NEVER);
    		//batch.parent.setCullMode(Node.CULL_NEVER);
    	}
    }
    

}
