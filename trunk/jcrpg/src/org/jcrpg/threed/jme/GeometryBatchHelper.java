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

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.scene.model.QuadModel;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.geometryinstancing.GeometryBatchInstance;
import com.jme.scene.geometryinstancing.GeometryBatchInstanceAttributes;
import com.jme.scene.geometryinstancing.instance.GeometryBatchCreator;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

public class GeometryBatchHelper {

	static HashMap<String, GeometryBatchCreator> creatorMap = new HashMap<String, GeometryBatchCreator>();
	static J3DCore core;
	static HashMap<String, TriMesh> parentMeshMap = new HashMap<String, TriMesh>();
	
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
    	TriMesh quad = (TriMesh)core.modelLoader.loadQuadModelNode(m, false).getChild(0);

    	GeometryBatchCreator geometryBatchCreator = creatorMap.get(m.id);
        // The batch geometry creator
    	if (geometryBatchCreator==null) {
    		geometryBatchCreator = new GeometryBatchCreator();
    		creatorMap.put(m.id, geometryBatchCreator);
    	}
    	TriMesh mesh = parentMeshMap.get(m.id+internal);
    	if (mesh==null)
    	{
    		mesh = new TriMesh();
    		Node parentNode = new Node();
    		parentNode.attachChild(mesh);
    		
    		parentNode.setRenderState(quad.getRenderState(RenderState.RS_TEXTURE));
    		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
            ms.setColorMaterial(MaterialState.CM_AMBIENT_AND_DIFFUSE);
            parentNode.setRenderState(ms);
            
            if (internal)
    		{
    			core.intRootNode.attachChild(parentNode);
    			core.intRootNode.updateRenderState();
    		} else
    		{
    			core.extRootNode.attachChild(parentNode);
    			core.extRootNode.updateRenderState();
    		}
    		parentMeshMap.put(m.id+internal, mesh);
    	}

        GeometryBatchInstanceAttributes attributes =
            new GeometryBatchInstanceAttributes(
            		place.getLocalTranslation(),
                    // Translation
                    place.getLocalScale(),
                    // Scale
                    place.getLocalRotation().mult(new Vector3f(1,1,1),new Vector3f(1,1,1)),
                    // Rotation
                    new ColorRGBA(1.0f,
                                  1.0f, 1.0f,
                                  1.0f));    // Color

	    // Box instance (batch and attributes)
	    GeometryBatchInstance instance =
	            new GeometryBatchInstance(quad.getBatch(0), attributes);
    	
        geometryBatchCreator.addInstance(instance);

        TriangleBatch batch = mesh.getBatch(0);
        batch.setModelBound(new BoundingBox());

        // Create the batch's buffers
        batch.setIndexBuffer(BufferUtils.createIntBuffer(
                geometryBatchCreator.getNumIndices()));
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setTextureBuffer(BufferUtils.createVector2Buffer(
                geometryBatchCreator.getNumVertices()), 0);
        batch.setColorBuffer(BufferUtils.createFloatBuffer(
                geometryBatchCreator.getNumVertices() * 4));

        // Commit the instances to the mesh batch
        geometryBatchCreator.commit(batch);
        batch.updateModelBound();
        place.batchInstance = instance;
    }
    
    private TriangleBatch updateBuffers = new TriangleBatch();
    /**
     * Use geometry instancing to create a mesh containing a number of box
     * instances
     */
    public void removeItem(boolean internal, QuadModel m, NodePlaceholder place) {

    	if (place.batchInstance==null) return;
    	GeometryBatchCreator geometryBatchCreator = creatorMap.get(m.id);
    	TriMesh mesh = parentMeshMap.get(m.id+internal);
    	geometryBatchCreator.removeInstance(place.batchInstance);
        TriangleBatch batch = mesh.getBatch(0);
    	rewindBatchBuffers(batch);
        // Create the batch's buffers
        batch.setIndexBuffer(BufferUtils.createIntBuffer(
                geometryBatchCreator.getNumIndices()));
        batch.setVertexBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setNormalBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        batch.setTextureBuffer(BufferUtils.createVector2Buffer(
                geometryBatchCreator.getNumVertices()), 0);
        batch.setColorBuffer(BufferUtils.createFloatBuffer(
                geometryBatchCreator.getNumVertices() * 4));
        // Commit the instances to the mesh batch
        updateBuffers.setVertexBuffer(batch.getVertexBuffer());
        updateBuffers.setColorBuffer(batch.getColorBuffer());
        updateBuffers.setNormalBuffer(batch.getNormalBuffer());
        updateBuffers.setTextureBuffer(batch.getTextureBuffer(0), 0);
        geometryBatchCreator.commit(updateBuffers);
        
        batch.updateModelBound();
    }
    
    /**
     * Rewind a Buffer if it exists Could a function like this be a part of the
     * batch?
     */
    private void rewindBuffer(Buffer buf) {
        if (buf != null) {
            buf.rewind();
        }
    }

    /**
     * Rewind all buffers in a batch Could a function like this be a part of the
     * batch?
     */
    public void rewindBatchBuffers(TriangleBatch batch) {
        rewindBuffer(batch.getIndexBuffer());
        rewindBuffer(batch.getVertexBuffer());
        rewindBuffer(batch.getColorBuffer());
        rewindBuffer(batch.getNormalBuffer());
        ArrayList<FloatBuffer> textureBuffers = batch.getTextureBuffers();
        for (FloatBuffer textureBuffer : textureBuffers) {
            rewindBuffer(textureBuffer);
		}
	}
}
