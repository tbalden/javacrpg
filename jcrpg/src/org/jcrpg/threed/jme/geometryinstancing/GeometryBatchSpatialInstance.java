package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jcrpg.threed.jme.TiledTerrainBlockUnbuffered;
import org.jcrpg.threed.jme.geometryinstancing.instance.GeometryInstance;

import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;

/**
 * <code>GeometryBatchSpatialInstance</code> uses <code>GeometryBatchInstanceAttributes</code>
 * to define an instance of object in world space. Uses Geometry as source
 * data for the instance.
 *
 * @author Paul Illes - removed tmpVertexBuffer, using the original vertex buffer instead, saving memory
 * @author Patrik Lindegran
 */
public class GeometryBatchSpatialInstance<A extends GeometryBatchInstanceAttributes> extends GeometryInstance<A> {
	public Geometry mesh;
	protected AABB modelBound;
	
	protected GeomBatch instanceBatch = null;
	
	protected boolean transformed = false;
	private boolean updateVerts = false;
	private boolean updateIndices = true;
	
	protected boolean forceUpdate = false;
	
	public GeometryBatchSpatialInstance(Geometry mesh, A attributes) {
        super(attributes);
        this.mesh = mesh;
        instanceBatch = mesh.getBatch(0);
        modelBound = new AABB();
    }
	
	/** Update Mesh needs to be called when the mesh is changed (does not support a change of the number of vertices) */
	public void updateMesh() {
		forceUpdate = true;
	}

    /** Vector used to store and calculate world transformations */
    protected Vector3f worldVector = new Vector3f();
    
    protected boolean wantCommit() {
    	return updateVerts || updateIndices || attributes.isColorChanged();
    }
    
    /** Calculate vertices if batch or attributes has changed */
    public boolean preCommit(boolean forceUpdate) {
    	if (forceUpdate) {
    		this.forceUpdate = true;
    		this.updateIndices = true;
    	}
    	
    	if ((forceUpdate || attributes.isTransformed())) {
    		updateVerts = true;
    		// removed tmpVertBuffer filling from here - Paul
    		attributes.setTransformed(false);
     	}
    	return wantCommit();
    }
    
    /**
     * Uses the instanceAttributes to transform the instanceBatch into world
     * coordinates. The transformed instance batch is added to the batch.
     *
     * @param batch
     */
    public void commit(GeomBatch batch, boolean force) {
        if (mesh instanceof TiledTerrainBlockUnbuffered)
        {
        	// the tricky part for tiled terrain blocks -- reallocate from pool/rebuild buffers if not present (were released) 
        	TiledTerrainBlockUnbuffered ttbu = (TiledTerrainBlockUnbuffered)mesh;
       		ttbu.rebuildBuffers();
        }
        int indexStart = commitVertices(batch);
        commitIndices(batch, indexStart);
        commitNormals(batch);
        commitTextureCoords(batch);
        commitColors(batch);
        
        /*if (mesh instanceof TiledTerrainBlockUnbuffered)
        {
        	// releasing the buffers for other TTBUs to use - saving helluva lotsa memory...
        	GeometryBatchMesh.releaseBatchExact(((TiledTerrainBlockUnbuffered)mesh).getBatch(0),false);
        	((TiledTerrainBlockUnbuffered)mesh).releaseExtraBuffers();
        }*/

        if(attributes.isVisible()) {
        	transformed = false;
        	forceUpdate = false;
            attributes.setColorChanged(false);
        }
    }
    
    protected void skipBuffer(Buffer bufferSrc, Buffer bufferDst) {
    	if( bufferSrc == null || bufferDst == null ) {
    		return;
    	}
    	bufferDst.position(bufferDst.position() + bufferSrc.capacity());
    }
    
    protected void skipBuffer(int size, Buffer bufferDst) {
    	if( bufferDst == null ) {
    		return;
    	}
    	bufferDst.position(bufferDst.position() + size);
    }
    
    private int commitVertices(FloatBuffer vertBufSrc, FloatBuffer vertBufDst) {
    	if( vertBufSrc == null || vertBufDst == null ) {
    		return 0;
    	}
    	int indexStart = vertBufDst.position() / 3;
    	// filling from original vertexbuffer here at commit time instead of preCommit tmpVertexBuffer - Paul
		modelBound.reset();
		if(attributes.isVisible()) {	    			
			transformed = true;
            vertBufSrc.rewind();		            
            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
                worldVector.set(vertBufSrc.get(), vertBufSrc.get(),
                                vertBufSrc.get());
                attributes.getWorldMatrix().mult(worldVector, worldVector);
                modelBound.expand(worldVector);
                vertBufDst.put(worldVector.x);
                vertBufDst.put(worldVector.y);
                vertBufDst.put(worldVector.z);
            }
		} else {
			for (int i = 0; i < vertBufSrc.capacity(); i++) {
				vertBufDst.put(0.0f);
            }
		}
		
        return indexStart;
    }
    
    protected int commitVertices(GeomBatch batch) {
    	// Vertex buffer
    	FloatBuffer vertBufSrc = instanceBatch.getVertexBuffer();
       	if (updateVerts) {
            updateVerts = false;
            return commitVertices(vertBufSrc, batch.getVertexBuffer());
    	}
    	skipBuffer(vertBufSrc, batch.getVertexBuffer());
        return 0;
    }
    
    protected void commitIndices(GeomBatch batch, int indexStart) {
    	if (!(instanceBatch instanceof TriangleBatch && batch instanceof TriangleBatch)) {
    		return;
    	}
    	// Index buffer        
    	if (updateIndices) {
    		IntBuffer indexBufSrc = ((TriangleBatch)instanceBatch).getIndexBuffer();
            IntBuffer indexBufDst = ((TriangleBatch)batch).getIndexBuffer();
            if (indexBufSrc != null && indexBufDst != null) {
        		updateIndices = false;
	            indexBufSrc.rewind();
	            for (int i = 0; i < getNumIndices(); i++) {
	                indexBufDst.put(indexStart + indexBufSrc.get());
	            }
        	} 
        } else {
    		skipBuffer(((TriangleBatch)instanceBatch).getIndexBuffer(), ((TriangleBatch)batch).getIndexBuffer());
    	}
    }
    
    protected void commitNormals(FloatBuffer normalBufSrc, FloatBuffer normalBufDst) {
    	if(normalBufSrc == null || normalBufDst == null) {
    		return;
    	}
    	normalBufSrc.rewind();
        for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
            worldVector.set(normalBufSrc.get(), normalBufSrc.get(),
                            normalBufSrc.get());
            attributes.getNormalMatrix().mult(worldVector, worldVector);
            worldVector.normalizeLocal();
            normalBufDst.put(worldVector.x);
            normalBufDst.put(worldVector.y);
            normalBufDst.put(worldVector.z);
        }
    }
    
    protected void commitNormals(GeomBatch batch) {
    	// Normal buffer
    	if ((forceUpdate || transformed) && attributes.isVisible()) {
    		commitNormals(instanceBatch.getNormalBuffer(), batch.getNormalBuffer());
    	} else {
    		skipBuffer(instanceBatch.getNormalBuffer(), batch.getNormalBuffer());
    	}
    }
        
    protected void commitTextureCoords(GeomBatch batch) {
    	// Texture buffers
        for (int i = 0; i < 8; i++) {
            FloatBuffer texBufSrc = instanceBatch.getTextureBuffer(i);
            FloatBuffer texBufDst = batch.getTextureBuffer(i);
            if (texBufSrc != null && texBufDst != null) {
            	if (forceUpdate && attributes.isVisible()) {
            		texBufSrc.rewind();
                    texBufDst.put(texBufSrc);
            	} else {
            		skipBuffer(texBufSrc, texBufDst);
            	}
            }
        }
    }
    
	protected void commitColors(GeomBatch batch) {
	 // Color buffer
	    FloatBuffer colorBufSrc = instanceBatch.getColorBuffer();
	    FloatBuffer colorBufDst = batch.getColorBuffer();
	    if (colorBufSrc != null && colorBufDst != null) {
	    	if ((forceUpdate || attributes.isColorChanged()) && attributes.isVisible()) {
	            colorBufSrc.rewind();
	            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
	                colorBufDst.put(colorBufSrc.get() * attributes.getColor().r);
	                colorBufDst.put(colorBufSrc.get() * attributes.getColor().g);
	                colorBufDst.put(colorBufSrc.get() * attributes.getColor().b);
	                colorBufDst.put(colorBufSrc.get() * attributes.getColor().a);
	            }
	    	} else {
	    		skipBuffer(colorBufSrc, colorBufDst);
	    	}
	        
	    } else if (colorBufDst != null) {
	    	if ((forceUpdate || attributes.isColorChanged()) && attributes.isVisible()) {
	            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
	                colorBufDst.put(attributes.getColor().r);
	                colorBufDst.put(attributes.getColor().g);
	                colorBufDst.put(attributes.getColor().b);
	                colorBufDst.put(attributes.getColor().a);
	            }
	    	} else {
	    		skipBuffer(instanceBatch.getVertexCount() * 4, colorBufDst);
	    	}
	    }
	}

    public int getNumIndices() {
        if (instanceBatch == null) {
            return 0;
        }
        if ((instanceBatch.getType() & TriangleBatch.TRIANGLEBATCH)>0) {
        	return ((TriangleBatch)instanceBatch).getMaxIndex();        	
        }
        return 0;
    }

    public int getNumVerts() {
        if (instanceBatch == null) {
            return 0;
        }
        return instanceBatch.getVertexCount();
    }

	public AABB getModelBound() {
		return modelBound;
	}
}