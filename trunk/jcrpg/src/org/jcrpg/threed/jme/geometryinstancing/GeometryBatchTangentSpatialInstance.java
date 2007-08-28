package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.FloatBuffer;

import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;

/**
 * <code>GeometryBatchTangentSpatialInstance</code> uses a <code>GeometryBatchInstanceAttributes</code>
 * to define an instance of object in world space. Uses TriangleBatch as source
 * data for the instance, instead of GeomBatch which does not have an index
 * buffer.
 *
 * @author Patrik Lindegrén
 */
public class GeometryBatchTangentSpatialInstance<A extends GeometryBatchInstanceAttributes> extends GeometryBatchSpatialInstance<A> {
	private FloatBuffer tangentBufDst;
	private FloatBuffer tangentBufSrc;
	
    public GeometryBatchTangentSpatialInstance(TriMesh mesh, FloatBuffer tangentBufSrc, A attributes) {
        super(mesh, attributes);
        this.tangentBufSrc = tangentBufSrc;
    }    
    
    public void setTangentBuffer(FloatBuffer tangentBufDst) {
		this.tangentBufDst = tangentBufDst;
	}
    
    protected void commitTangents() {
    	if (tangentBufSrc != null && tangentBufDst != null) {
        	if (transformed && attributes.isVisible()) {
        		tangentBufSrc.rewind();
	            for (int i = 0; i < instanceBatch.getVertexCount(); i++) {
	                worldVector.set(tangentBufSrc.get(), tangentBufSrc.get(), tangentBufSrc.get());
	                attributes.getNormalMatrix().mult(worldVector, worldVector);
	                worldVector.normalizeLocal();
	                tangentBufDst.put(worldVector.x);
	                tangentBufDst.put(worldVector.y);
	                tangentBufDst.put(worldVector.z);
	            }
        	} else {
        		skipBuffer(tangentBufSrc, tangentBufDst);
        	}
        }
    }
    
    protected void commitNormals(TriangleBatch batch) {
    	super.commitNormals(batch);
    	commitTangents();
    }
}