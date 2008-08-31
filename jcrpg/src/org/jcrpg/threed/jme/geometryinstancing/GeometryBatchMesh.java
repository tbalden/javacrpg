package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.jcrpg.threed.jme.TiledTerrainBlock;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;

/**
 * <code>GeometryBatchMesh</code> is a container class for <code>GeometryInstances</code>, which
 * contains the batch created from the instances.
 *
 * @author Patrik Lindegran
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
    	IntBuffer buff = getBatch(0).getIndexBuffer();
    	if (buff!=null && buff.capacity()>0)
    	{
    		/*if (buff.capacity()>=getNumIndices())
    		{ // XXX it doesnt work to use the index buffer with fewer indices!! i've commented this out.
    			buff.clear();
    			buff.limit(getNumIndices());
    			buff.rewind();
    			return;
    		} else*/
    		{
    			BufferPool.releaseIntBuffer(buff);
    		}
    	}
    	getBatch(0).setIndexBuffer(BufferPool.getIntBuffer(getNumIndices()));
    	//getBatch(0).setIndexBuffer(BufferUtils.createIntBuffer(getNumIndices()));
    }
    
    public void createVertexBuffer() {
    	FloatBuffer buff = getBatch(0).getVertexBuffer();
    	if (buff!=null && buff.capacity()>0)
    	{
    		if (buff.capacity()>=getNumVertices()*3)
    		{
    			buff.clear();
    			buff.limit(getNumVertices()*3);
    			buff.rewind();
    			return;
    		} else
    		{
    			BufferPool.releaseVector3Buffer(buff);
    		}
    	}
    	getBatch(0).setVertexBuffer(BufferPool.getVector3Buffer(getNumVertices()));
    	//getBatch(0).setVertexBuffer(BufferUtils.createVector3Buffer(getNumVertices()));
    }
    
    public void createNormalBuffer() {
    	FloatBuffer buff = getBatch(0).getNormalBuffer();
    	if (buff!=null && buff.capacity()>0)
    	{
    		if (buff.capacity()>=getNumVertices()*3)
    		{
    			buff.clear();
    			buff.limit(getNumVertices()*3);
    			buff.rewind();
    			return;
    		} else
    		{
    			BufferPool.releaseVector3Buffer(buff);
    		}
    	}
    	getBatch(0).setNormalBuffer(BufferPool.getVector3Buffer(getNumVertices()));
    	//getBatch(0).setNormalBuffer(BufferUtils.createVector3Buffer(getNumVertices()));
    }
    
    public void createColorBuffer() {
    	FloatBuffer buff = getBatch(0).getColorBuffer();
    	if (buff!=null && buff.capacity()>0)
    	{
    		if (buff.capacity()>=getNumVertices()*4)
    		{
    			buff.clear();
    			buff.limit(getNumVertices()*4);
    			buff.rewind();
    			return;
    		} else
    		{
    			BufferPool.releaseFloatBuffer(buff);
    		}
    	}
    	getBatch(0).setColorBuffer(BufferPool.getFloatBuffer(getNumVertices() * 4));
    	//getBatch(0).setColorBuffer(BufferUtils.createFloatBuffer  (getNumVertices() * 4));
	}
    
    public void createTextureBuffer(int textureUnit) {
    	FloatBuffer buff = getBatch(0).getTextureBuffer(textureUnit);
    	if (buff!=null && buff.capacity()>0)
    	{
    		if (buff.capacity()>=getNumVertices()*2)
    		{
    			buff.clear();
    			buff.limit(getNumVertices()*2);
    			buff.rewind();
    			return;
    		} else
    		{
    			BufferPool.releaseVector2Buffer(buff);
    		}
    	}
    	getBatch(0).setTextureBuffer(BufferPool.getVector2Buffer(getNumVertices()), textureUnit);
    	
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
    
    public void releaseBatch(TriangleBatch batch)
    {
        BufferPool.releaseIntBuffer(batch.getIndexBuffer());
        batch.setIndexBuffer(null);
        BufferPool.releaseVector3Buffer(batch.getVertexBuffer());
        batch.setVertexBuffer(null);
        BufferPool.releaseVector3Buffer(batch.getNormalBuffer());
        batch.setNormalBuffer(null);
        BufferPool.releaseFloatBuffer(batch.getColorBuffer());
        batch.setColorBuffer(null);
        
        ArrayList<FloatBuffer> textureBuffers = batch.getTextureBuffers();
        for (FloatBuffer textureBuffer : textureBuffers) {
            BufferPool.releaseVector2Buffer(textureBuffer);
		}
        batch.clearTextureBuffers();
    	batch.removeFromParent();
    	
    }

    public void releaseBatchExact(TriangleBatch batch)
    {
        ExactBufferPool.releaseIntBuffer(batch.getIndexBuffer());
        batch.setIndexBuffer(null);
        ExactBufferPool.releaseVector3Buffer(batch.getVertexBuffer());
        batch.setVertexBuffer(null);
        ExactBufferPool.releaseVector3Buffer(batch.getNormalBuffer());
        batch.setNormalBuffer(null);
        ExactBufferPool.releaseFloatBuffer(batch.getColorBuffer());
        batch.setColorBuffer(null);
        
        ArrayList<FloatBuffer> textureBuffers = batch.getTextureBuffers();
        for (FloatBuffer textureBuffer : textureBuffers) {
        	ExactBufferPool.releaseVector2Buffer(textureBuffer);
		}
        batch.clearTextureBuffers();
    	batch.removeFromParent();
    	
    }

    public void releaseBuffersOnCleanUp()
    {
    	for (T t:getInstances())
    	{
    		if (t instanceof GeometryBatchSpatialInstance)
    		{
    			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> t2 = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)t;
    			if (t2.mesh instanceof TiledTerrainBlock)
    			{
    				//System.out.println("### RELEASING GEOTILE!");
    				releaseBatchExact(((TiledTerrainBlock)t2.mesh).getBatch(0));
    				((TiledTerrainBlock)t2.mesh).releaseExtraBuffers();
    			}
    		}
    	}
    	
    	if (getBatchCount()>0)
    	{
	        BufferPool.releaseIntBuffer(getBatch(0).getIndexBuffer());
	        getBatch(0).setIndexBuffer(null);
	        BufferPool.releaseVector3Buffer(getBatch(0).getVertexBuffer());
	        getBatch(0).setVertexBuffer(null);
	        BufferPool.releaseVector3Buffer(getBatch(0).getNormalBuffer());
	        getBatch(0).setNormalBuffer(null);
	        BufferPool.releaseFloatBuffer(getBatch(0).getColorBuffer());
	        getBatch(0).setColorBuffer(null);
	        
	        ArrayList<FloatBuffer> textureBuffers = getBatch(0).getTextureBuffers();
	        for (FloatBuffer textureBuffer : textureBuffers) {
	            BufferPool.releaseVector2Buffer(textureBuffer);
			}
	        getBatch(0).clearTextureBuffers();
	    	getBatch(0).removeFromParent();
    	}
}
}