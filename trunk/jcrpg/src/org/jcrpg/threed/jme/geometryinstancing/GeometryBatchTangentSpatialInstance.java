package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.FloatBuffer;

import com.jme.scene.Geometry;
import com.jme.scene.batch.GeomBatch;

/**
 * <code>GeometryBatchTangentSpatialInstance</code> extends <code>GeometryBatchSpatialInstance</code>
 * and adds tangent buffers to an instance.
 *
 * @author Patrik Lindegran
 */
public class GeometryBatchTangentSpatialInstance<A extends GeometryBatchInstanceAttributes> extends GeometryBatchSpatialInstance<A> {
	private FloatBuffer tangentBufDst;
	private FloatBuffer tangentBufSrc;
	
    public GeometryBatchTangentSpatialInstance(Geometry mesh, FloatBuffer tangentBufSrc, A attributes) {
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
    
    protected void commitNormals(GeomBatch batch) {
    	super.commitNormals(batch);
    	commitTangents();    	
    }
}