package org.jcrpg.threed.jme.geometryinstancing;

import org.jcrpg.threed.jme.geometryinstancing.instance.GeometryInstanceAttributes;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;

/**
 * <code>GeometryBatchInstanceAttributes</code> specifies the attributes for a
 * <code>GeometryBatchInstance</code>
 *
 * @author Patrik Lindegran
 */
public class GeometryBatchInstanceAttributes extends GeometryInstanceAttributes {
    protected ColorRGBA color;
    private boolean colorChanged;
    
    public GeometryBatchInstanceAttributes(TriMesh mesh) {
    	this(new Vector3f(mesh.getLocalTranslation()), 
    		 new Vector3f(mesh.getLocalScale()), 
    		 new Quaternion(mesh.getLocalRotation()), true, 
    		 new ColorRGBA(mesh.getDefaultColor()));
    }
    
    public GeometryBatchInstanceAttributes(GeometryBatchInstanceAttributes attributes) {
    	super(attributes);
    	this.color = attributes.color;
    }

    public GeometryBatchInstanceAttributes(Vector3f translation, Vector3f scale, Quaternion rotation, boolean visible, ColorRGBA color) {
        super(translation, scale, rotation, visible);
        this.color = color;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }

	public boolean isColorChanged() {
		return colorChanged;
	}

	public void setColorChanged(boolean colorChanged) {
		this.colorChanged = colorChanged;
	}
}