/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.threed;

import java.util.HashMap;

import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.Model;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.geometryinstancing.GeometryBatchInstance;
import com.jme.util.export.Savable;

public class NodePlaceholder {

	public Model model;
	public RenderedCube cube;
	public PooledNode realNode;
	public Object batchInstance;
	
	public Quaternion localRotation;
	/** helper separate horizontalRotation from RenderedHashRotatedSide, use it with trimeshGeomBatch only: */
	public Quaternion horizontalRotation;
	public Vector3f localScale, localTranslation;
	public HashMap<String, Savable> userData = new HashMap<String, Savable>();
	
	public Quaternion getLocalRotation() {
		
		return localRotation;
	}
	public Vector3f getLocalScale() {
		
		return localScale;
	}
	public Vector3f getLocalTranslation() {
		
		return localTranslation;
	}
	
	public Savable getUserData(String key) {
		
		return userData.get(key);
	}
	
	public Savable removeUserData(String key) {
		
		return userData.remove(key);
	}
	
	public void setLocalRotation(Quaternion quaternion) {
		
		localRotation = quaternion;
	}
	
	public void setLocalScale(float localScale) {
		
		this.localScale = new Vector3f(localScale,localScale,localScale);
	}
	
	public void setLocalScale(Vector3f localScale) {
		
		this.localScale = localScale;
	}
	
	public void setLocalTranslation(float x, float y, float z) {
		
		localTranslation = new Vector3f(x,y,z);
	}
	
	public void setLocalTranslation(Vector3f localTranslation) {
		
		this.localTranslation = localTranslation;
	}
	
	public void setUserData(String key, Savable data) {
		
		userData.put(key, data);
	}
	
	
}
