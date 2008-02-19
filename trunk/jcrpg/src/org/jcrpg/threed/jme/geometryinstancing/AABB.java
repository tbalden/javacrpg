package org.jcrpg.threed.jme.geometryinstancing;

import java.nio.FloatBuffer;

import com.jme.bounding.BoundingBox;
import com.jme.math.*;
import com.jme.util.geom.BufferUtils;

/**
 * <code>AABB/code> is an axis aligned bounding box, that is easy to <code>expand</code> using vertices. 
 * @author Patrik Lindegran
 */
public class AABB {
	private static final long serialVersionUID = 1L;
	
	public Vector3f min;
	public Vector3f max;
	
	public AABB() {
		min = new Vector3f();
		max = new Vector3f();
		reset();
	}
	
	public AABB(Vector3f min, Vector3f max) {
		this.min = new Vector3f(min);
		this.max = new Vector3f(max);
	}
	
	public void set(Vector3f min, Vector3f max) {
		this.min.set(min);
		this.max.set(max);
	}
	
	public void set(BoundingBox bb) {
		if (bb == null)
            return;
		min.x = bb.getCenter().x - bb.xExtent;
		min.y = bb.getCenter().y - bb.yExtent;
		min.z = bb.getCenter().z - bb.zExtent;
		
		max.x = bb.getCenter().x + bb.xExtent;
		max.y = bb.getCenter().y + bb.yExtent;
		max.z = bb.getCenter().z + bb.zExtent;
	}
	
	public void reset() {
		min.set(Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY);
		max.set(Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY);
	}
	
	public void expand(Vector3f pos) {
		if (pos == null)
            return;
		min(min, pos);
		max(max, pos);
	}

	private void max(Vector3f max, Vector3f point) {
		if (min == null || max == null)
            return;
		max.x = Math.max(max.x, point.x);
		max.y = Math.max(max.y, point.y);
		max.z = Math.max(max.z, point.z);
	}

	private void min(Vector3f min, Vector3f point) {
		if (min == null || max == null)
            return;
		min.x = Math.min(min.x, point.x);
		min.y = Math.min(min.y, point.y);
		min.z = Math.min(min.z, point.z);
	}
	
	public void mergeLocal(AABB aabb) {
		if (aabb == null)
            return;
		
		expand(aabb.min);
		expand(aabb.max);
	}
	
	private Vector3f compVector = new Vector3f();
	
	public void expand(FloatBuffer points) {
        if (points == null)
            return;

        points.rewind();
        if (points.remaining() <= 2) // we need at least a 3 float vector
            return;

        for (int i = 1, len = points.remaining() / 3; i < len; i++) {
            BufferUtils.populateFromBuffer(compVector, points, i);
            expand(compVector);
        }
    }
	
	public void getBoundingBox(BoundingBox boundingBox) {
		if (boundingBox == null)
            return;
		boundingBox.xExtent = (max.x - min.x) / 2f;
		boundingBox.yExtent = (max.y - min.y) / 2f;
		boundingBox.zExtent = (max.z - min.z) / 2f;
		
		boundingBox.getCenter().set(min.x + boundingBox.xExtent,
								    min.y + boundingBox.yExtent,
								    min.z + boundingBox.zExtent);
	}
	
	public void getCenter(Vector3f center) {
		if (center == null)
            return;
		center.set(min.x + (max.x - min.x) / 2f, 
				   min.y + (max.y - min.y) / 2f, 
				   min.z + (max.z - min.z) / 2f);
	}
	
	public void getExtent(Vector3f extent) {
		if (extent == null)
            return;
		extent.set((max.x - min.x) / 2f, 
				   (max.y - min.y) / 2f, 
				   (max.z - min.z) / 2f);
	}
}
