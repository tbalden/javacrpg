/*
 * Copyright (c) 2003-2007 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;

/**
 * <code>GeometryBatchMesh</code> is a container class for <code>GeometryInstances</code>.
 *
 * @author Patrik Lindegrén
 */

public class GeometryBatchMesh<T extends GeometryBatchSpatialInstance<?>> extends TriMesh {
	private static final long serialVersionUID = 0L;
	
    protected ArrayList<T> instances;
    private int nVerts;
    private int nIndices;
    private AABB modelBound;
    
    private boolean commit = false;
    
    public boolean reconstruct = false;
	
	public GeometryBatchMesh() {
		init();
    }
	
	public GeometryBatchMesh(String name) {
		super(name);
		init();
    }
	
	private void init() {
		instances = new ArrayList<T>(1);
        modelBound = new AABB();
        getBatch(0).setModelBound(new BoundingBox());
	}
	
	public int getNumInstances() {
        if(instances == null) {
            return 0;
        }            
        return instances.size();
    }
	
    public void clearInstances() {
        instances.clear();
        modelBound.reset();
        nVerts = 0;
        nIndices = 0;
    }

    public void addInstance(T geometryInstance) {
        if (geometryInstance == null) {
            return;
        }
        instances.add(geometryInstance);
        nIndices += geometryInstance.getNumIndices();
        nVerts += geometryInstance.getNumVerts();
        reconstruct = true;
    }

    public void removeInstance(T geometryInstance) {
        if (instances.remove(geometryInstance)) {
            nIndices -= geometryInstance.getNumIndices();
            nVerts -= geometryInstance.getNumVerts();
        }
        reconstruct = true;
    }

    public int getNumVertices() {
        return nVerts;
    }

    public int getNumIndices() {
        return nIndices;
    }

    public ArrayList<T> getInstances() {
        return instances;
    }
    
    private void updateBound() {
    	if (commit) {
	    	modelBound.reset();
	        for (T instance : instances) {
	            if( instance.getAttributes().isVisible() ) {
	            	modelBound.mergeLocal(instance.getModelBound());
	        	}
	        }
	        modelBound.getBoundingBox((BoundingBox)getBatch(0).getModelBound());
    	}
    }
	
    /**
     * Calculates AABBs for all instances and then
     * Calculate the AABB for the whole batch
     */ 
    public void preCommit() {
    	if (reconstruct) {
    		createBuffers();		// TODO: Maybe have an integer value for the number of texture units?
    	}
        for (T instance : instances) {
            commit = instance.preCommit(reconstruct) || commit;
        }
    	reconstruct = false;
        updateBound();
    }
    
    public void commit(TriangleBatch batch) {
    	if (commit) {
	    	rewindBuffers(batch);
	        for (T instance : instances) {
	            instance.commit(batch, reconstruct);
	        }
    	}
    }
    
    public void onDraw(Renderer r) {
    	if (getCullMode() == SceneElement.CULL_ALWAYS) {
    		return;
    	}
    	preCommit();
    	super.onDraw(r);
    }
    
    public void draw(Renderer r) {
    	commit(getBatch(0));
    	super.draw(r);
    }
    
    /*******************************************************************
     * Buffers
     *******************************************************************/
    
    public void createIndexBuffer() {
    	getBatch(0).setIndexBuffer(BufferUtils.createIntBuffer(getNumIndices()));
    }
    
    public void createVertexBuffer() {
    	getBatch(0).setVertexBuffer(BufferUtils.createVector3Buffer(getNumVertices()));
    }
    
    public void createNormalBuffer() {
    	getBatch(0).setNormalBuffer(BufferUtils.createVector3Buffer(getNumVertices()));
    }
    
    public void createColorBuffer() {
    	getBatch(0).setColorBuffer(BufferUtils.createFloatBuffer  (getNumVertices() * 4));
	}
    
    public void createTextureBuffer(int textureUnit) {
    	getBatch(0).setTextureBuffer(BufferUtils.createVector2Buffer(getNumVertices()), textureUnit);
    }
    
    /**
     * Create the buffers
     */	
	public void createBuffers() {
		createIndexBuffer();
    	createVertexBuffer();
    	createNormalBuffer();
    	createColorBuffer();
    	createTextureBuffer(0);
    }
    
    /**
     * Rewind a Buffer if it exists 
     * Could a function like this be a part of the batch?
     */
    private void rewindBuffer(Buffer buf) {
        if (buf != null) {
            buf.rewind();
        }
    }

    /**
     * Rewind all buffers in a batch 
     * Could a function like this be a part of the batch?
     */
    public void rewindBuffers(TriangleBatch batch) {
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