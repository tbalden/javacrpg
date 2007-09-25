package org.jcrpg.threed.jme.geometryinstancing.instance;

import org.jcrpg.threed.jme.geometryinstancing.AABB;

import com.jme.scene.batch.GeomBatch;

/**
 * <code>GeometryInstance</code> uses <code>GeometryInstanceAttributes</code>
 * to define an instance of object in world space.
 *
 * @author Patrik Lindegrén
 */
public abstract class GeometryInstance<T extends GeometryInstanceAttributes> {
    protected T attributes;

    public abstract boolean preCommit(boolean force);
    
    public abstract void commit(GeomBatch batch, boolean force);

    public abstract int getNumIndices();

    public abstract int getNumVerts();
    
    public abstract AABB getModelBound();

    public GeometryInstance(T attributes) {
        this.attributes = attributes;
    }

    public T getAttributes() {
        return attributes;
    }
}