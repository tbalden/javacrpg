package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.TiledTerrainBlock;
import org.jcrpg.threed.jme.TiledTerrainBlockUnbuffered;

import com.jme.renderer.Renderer;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;

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
    //private AABB modelBound;
    
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
        //modelBound = new AABB();
        //setModelBound(new BoundingBox());
	}
	
	public int getNumInstances() {
        if(instances == null) {
            return 0;
        }            
        return instances.size();
    }
	
    public void clearInstances() {
        instances.clear();
        //modelBound.reset();
        nVerts = 0;
        nIndices = 0;
    }

    public void addInstance(T geometryInstance) {
        if (geometryInstance == null) {
            return;
        }
        synchronized (instances)
        {
	        instances.add(geometryInstance);
	        nIndices += geometryInstance.getNumIndices();
	        nVerts += geometryInstance.getNumVerts();
	        reconstruct = true;
	        /*if (geometryInstance.mesh instanceof TiledTerrainBlockUnbuffered)
	        {
	        	// releasing the buffers to pool for making it available for other tiledTerrainBlocks! saving memory :)
	        	releaseBatchExact(((TiledTerrainBlockUnbuffered)geometryInstance.mesh).getBatch(0),false);
	        	((TiledTerrainBlockUnbuffered)geometryInstance.mesh).releaseExtraBuffers();
	        }*/
        }
    }

    public void removeInstance(T geometryInstance) {
        synchronized (instances)
        {
	        if (instances.remove(geometryInstance)) {
	            nIndices -= geometryInstance.getNumIndices();
	            nVerts -= geometryInstance.getNumVerts();
	        }
	        reconstruct = true;
        }
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
    /*
    private void updateBound() {
    	if (commit) {
            synchronized (instances)
            {
            	
            	((BoundingBox)getModelBound()).
		    
	    		modelBound.reset();
		        for (T instance : instances) {
		            if( instance.getAttributes().isVisible() ) {
		            	modelBound.mergeLocal(instance.getModelBound());
		        	}
		        }
		        modelBound.getBoundingBox((BoundingBox)getModelBound());
            }
    	}
    }*/
	
    /**
     * Calculates AABBs for all instances and then
     * Calculate the AABB for the whole batch
     */ 
    public void preCommit() {
    	preCommitStart = System.currentTimeMillis();
    	if (reconstruct) {
    		if (J3DCore.SETTINGS.VBO_ENABLED)
    		{
				TriMesh b = this;
				if (b.getVBOInfo()!=null)
				{
					//System.out.println("CLEARING VBO");
					if (b.getVBOInfo().isVBOVertexEnabled())
						DisplaySystem.getDisplaySystem().getRenderer().deleteVBO( b.getVertexBuffer());
					if (b.getVBOInfo().isVBOTextureEnabled())
					{
						if (b.getTextureCoords(0)!=null)
							DisplaySystem.getDisplaySystem().getRenderer().deleteVBO( b.getTextureCoords(0).coords);
					}
					if (b.getVBOInfo().isVBONormalEnabled())
						DisplaySystem.getDisplaySystem().getRenderer().deleteVBO( b.getNormalBuffer());
					if (b.getVBOInfo().isVBOColorEnabled())
						DisplaySystem.getDisplaySystem().getRenderer().deleteVBO( b.getColorBuffer());
					if (b.getVBOInfo().isVBOIndexEnabled())
						DisplaySystem.getDisplaySystem().getRenderer().deleteVBO( b.getIndexBuffer());
					b.setVBOInfo(null);
				}
    		}
    		createBuffers();		// TODO: Maybe have an integer value for the number of texture units?
    	}
    	synchronized (instances)
    	{
	        for (T instance : instances) {
	            commit = instance.preCommit(reconstruct) || commit;
	        }
	    	reconstruct = false;
    	}
    	preCommitTime+=System.currentTimeMillis() - preCommitStart;
    }
    
    public void commit(TriMesh batch) {
    	if (commit) {
        	commitStart = System.currentTimeMillis();
	        synchronized (instances)
	        {
	        	rewindBuffers(batch);
		        for (T instance : instances) {
		            instance.commit(batch, reconstruct);
		        }
	        }
	        //updateBound();
	    	commitTime+=System.currentTimeMillis() - commitStart;
	    	commit = false;
    	}

    }
    
    public static long preCommitTime = 0;
    public static long commitTime = 0;
    public long preCommitStart = 0;
    public long commitStart = 0;
    
    public static boolean GLOBAL_CAN_COMMIT = true;
    
    public void onDraw(Renderer r) {
    	if (getCullHint() == CullHint.Always) {
    		return;
    	}
    	if (GLOBAL_CAN_COMMIT) preCommit();
    	super.onDraw(r);
    }
    
    /**
     * Forcing rebuild before draw.
     */
    public void rebuild()
    {
    	preCommit();
    	commit(this);
    }
    
    public void draw(Renderer r) {
    	if (GLOBAL_CAN_COMMIT) commit(this);
    	super.draw(r);
    }
    
    /*******************************************************************
     * Buffers
     *******************************************************************/
    
    
    public void createIndexBuffer() {
    	IntBuffer buff = getIndexBuffer();
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
    	setIndexBuffer(BufferPool.getIntBuffer(getNumIndices()));
    	//setIndexBuffer(BufferUtils.createIntBuffer(getNumIndices()));
    }
    
    public void createVertexBuffer() {
    	FloatBuffer buff = getVertexBuffer();
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
    	setVertexBuffer(BufferPool.getVector3Buffer(getNumVertices()));
    	//setVertexBuffer(BufferUtils.createVector3Buffer(getNumVertices()));
    }
    
    public void createNormalBuffer() {
    	FloatBuffer buff = getNormalBuffer();
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
    	setNormalBuffer(BufferPool.getVector3Buffer(getNumVertices()));
    	//setNormalBuffer(BufferUtils.createVector3Buffer(getNumVertices()));
    }
    
    public void createColorBuffer() {
    	FloatBuffer buff = getColorBuffer();
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
    	setColorBuffer(BufferPool.getFloatBuffer(getNumVertices() * 4));
    	//setColorBuffer(BufferUtils.createFloatBuffer  (getNumVertices() * 4));
	}
    
    public void createTextureBuffer(int textureUnit) {
    	FloatBuffer buff = getTextureCoords(0)!=null?getTextureCoords(0).coords:null;
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
    	buff = BufferPool.getVector2Buffer(getNumVertices());
    	if (getTextureCoords(0)==null)
    	{
    		setTextureCoords(new TexCoords(buff), 0);
    	} else
    	{
    		getTextureCoords(0).coords = buff;
    	}
    	
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
    public void rewindBuffers(TriMesh batch) {
        rewindBuffer(batch.getIndexBuffer());
        rewindBuffer(batch.getVertexBuffer());
        rewindBuffer(batch.getColorBuffer());
        rewindBuffer(batch.getNormalBuffer());
        ArrayList<TexCoords> textureBuffers = batch.getTextureCoords();
        for (TexCoords textureBuffer : textureBuffers) {
        	if (textureBuffer!=null)
        		rewindBuffer(textureBuffer.coords);
		}
	}
    
    public void releaseBatch(TriMesh batch)
    {
        BufferPool.releaseIntBuffer(batch.getIndexBuffer());
        batch.setIndexBuffer(null);
        BufferPool.releaseVector3Buffer(batch.getVertexBuffer());
        batch.setVertexBuffer(null);
        BufferPool.releaseVector3Buffer(batch.getNormalBuffer());
        batch.setNormalBuffer(null);
        BufferPool.releaseFloatBuffer(batch.getColorBuffer());
        batch.setColorBuffer(null);
        
        ArrayList<TexCoords> textureBuffers = batch.getTextureCoords();
        for (TexCoords textureBuffer : textureBuffers) {
        	if (textureBuffer!=null)
        		BufferPool.releaseVector2Buffer(textureBuffer.coords);
		}
        batch.clearTextureBuffers();
    	batch.removeFromParent();
    	
    }

    public static void releaseBatchExact(TriMesh batch, boolean clearAndRemove)
    {
    	if (batch.getIndexBuffer()!=TiledTerrainBlock.COMMON_INDEX_BUFFER)
    	{
    		// shouldn't release only non-common-buffer
    		ExactBufferPool.releaseIntBuffer(batch.getIndexBuffer());
    	}
    	batch.setIndexBuffer(null);
        ExactBufferPool.releaseVector3Buffer(batch.getVertexBuffer());
        batch.setVertexBuffer(null);
        ExactBufferPool.releaseVector3Buffer(batch.getNormalBuffer());
        batch.setNormalBuffer(null);
        ExactBufferPool.releaseFloatBuffer(batch.getColorBuffer());
        batch.setColorBuffer(null);
        
        ArrayList<TexCoords> textureBuffers = batch.getTextureCoords();
        for (TexCoords textureBuffer : textureBuffers) {
        	if (textureBuffer!=null)
        		ExactBufferPool.releaseVector2Buffer(textureBuffer.coords);
		}
        if (clearAndRemove)
        {
        	batch.clearTextureBuffers();
        	batch.removeFromParent();
        }
    }
    
    public void releaseInstanceRelatedOnCleanUp()
    {
    	ArrayList<T> removables = new ArrayList<T>();
    	removables.addAll(getInstances());
       	for (T t:removables)
       	{
       		removeInstance(t);
       	}
      	for (T t:removables)
    	{
    		if (t instanceof GeometryBatchSpatialInstance)
    		{
    			GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes> t2 = (GeometryBatchSpatialInstance<GeometryBatchInstanceAttributes>)t;
    			if (t2.mesh instanceof TiledTerrainBlock && !(t2.mesh instanceof TiledTerrainBlockUnbuffered))
    			{
    				//System.out.println("### RELEASING GEOTILE!");
    				if (((TiledTerrainBlock)t2.mesh)!=null)
    				{
    					releaseBatchExact(((TiledTerrainBlock)t2.mesh),true);
    				}
    				((TiledTerrainBlock)t2.mesh).releaseExtraBuffers();
     			}
    		}
    	}
    }

    public void releaseBuffersOnCleanUp()
    {
    	releaseInstanceRelatedOnCleanUp();
    	
    	{
	        BufferPool.releaseIntBuffer(getIndexBuffer());
	        setIndexBuffer(null);
	        BufferPool.releaseVector3Buffer(getVertexBuffer());
	        setVertexBuffer(null);
	        BufferPool.releaseVector3Buffer(getNormalBuffer());
	        setNormalBuffer(null);
	        BufferPool.releaseFloatBuffer(getColorBuffer());
	        setColorBuffer(null);
	        
	        ArrayList<TexCoords> textureBuffers = getTextureCoords();
	        for (TexCoords textureBuffer : textureBuffers) {
	        	if (textureBuffer!=null)
	        		BufferPool.releaseVector2Buffer(textureBuffer.coords);
			}
	        clearTextureBuffers();
	    	removeFromParent();
    	}
}
}