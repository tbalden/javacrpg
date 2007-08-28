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
package org.jcrpg.threed.jme.geometryinstancing.instance;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * <code>GeometryInstanceAttributes</code> specifies the attributes for a
 * <code>GeometryInstance</code>.
 *
 * @author Patrik Lindegrén
 */
public class GeometryInstanceAttributes {
    protected Vector3f scale;       // Scale
    protected Quaternion rotation;  // Rotation
    protected Vector3f translation; // Translation
    protected Matrix4f mtNormal;	// Normal matrix (scale, rotation)
    protected Matrix4f mtWorld;		// Local to world matrix (scale, rotation, translation)
    private boolean transformed;	// Has the attributes changed
    private boolean visible;		// Is the object visible

    public GeometryInstanceAttributes(GeometryInstanceAttributes attributes) {
    	this(attributes.translation, attributes.scale, attributes.rotation, attributes.visible);
    }
    
    public GeometryInstanceAttributes(Vector3f translation, Vector3f scale, Quaternion rotation, boolean visible) {
        this.scale = scale;
        this.rotation = rotation;
        this.translation = translation;
        this.visible = visible;
        mtWorld = new Matrix4f();
        mtNormal = new Matrix4f();
        buildMatrices();
    }

    /**
     * Vector used to store and calculate rotation in degrees Not needed when
     * radian rotation is implemented in Matrix4f
     */

    /** <code>buildMatrices</code> updates the world and rotation matrix */
    public void buildMatrices() {
        // Scale (temporarily use mtWorld as storage)
        mtWorld.loadIdentity();
        mtWorld.m00 = scale.x;
        mtWorld.m11 = scale.y;
        mtWorld.m22 = scale.z;

        // Build rotation matrix (temporarily use mtNormal as storage)
        mtNormal.loadIdentity();
        mtNormal.setRotationQuaternion(rotation);

        // Build normal matrix (scale * rotation)
        mtNormal.multLocal(mtWorld);

        // Build world matrix (scale * rotation + translation)
        mtWorld.set(mtNormal);
        mtWorld.setTranslation(translation);
    	transformed = true;
	}

    public Vector3f getScale() {
        return scale;
    }

    /**
     * After using the <code>setScale</code> function, user needs to call the
     * <code>buildMatrices</code> function
     *
     * @param scale
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    /**
     * After using the <code>setTranslation</code> function, user needs to call
     * the <code>buildMatrices</code> function
     *
     * @param translation
     */
    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    /**
     * After using the <code>setRotation</code> function, user needs to call the
     * <code>buildMatrices</code> function
     *
     * @param rotation
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public Matrix4f getWorldMatrix() {
        return mtWorld;
    }

    public Matrix4f getNormalMatrix() {
        return mtNormal;
    }

	public boolean isTransformed() {
		return transformed;
	}

	public void setTransformed(boolean transformed) {
		this.transformed = transformed;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if( this.visible != visible ) {
			this.transformed = true;	// Changed
			this.visible = visible;
		}
		
	}
}