/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.threed;

import org.jcrpg.threed.jme.vegetation.AbstractVegetation;
import org.jcrpg.threed.jme.vegetation.NaiveVegetation;
import org.jcrpg.threed.jme.vegetation.QuadTreeVegetation;
import org.jcrpg.util.HashUtil;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

public class FloraSetup {
	private static final float vegetationSeparation = 0.6f;

	private static final int vegetationCountX = 3;

	private static final int vegetationCountZ = 3;
	
	private static final float quadSizeX = 0.4f, quadSizeY = 0.8f;

	static TextureState default_ts; 
	static AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
	static {
		Texture qtexture = TextureManager.loadTexture("./data/textures/low/"+"grass1.png",Texture.MM_LINEAR,
	            Texture.FM_LINEAR);
		//qtexture.setWrap(Texture.WM_WRAP_S_WRAP_T);
		qtexture.setApply(Texture.AM_REPLACE);
		//qtexture.setFilter(Texture.FM_LINEAR);
		//qtexture.setMipmapState(Texture.MM_LINEAR_LINEAR);
		default_ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		default_ts.setTexture(qtexture);
		default_ts.setEnabled(true);
		

	    as.setEnabled(true);
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as.setReference(0.0f);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
	}

	
	public static Node createVegetation(Camera cam, int sx, int sy, int sz, TextureState ts) {
		
		// Load the vegetation class of your choice
		AbstractVegetation vegetation = new NaiveVegetation("vegetation",
				cam, 500.0f);

		//vegetation.setCullMode(Spatial.CULL_DYNAMIC);
		vegetation.initialize();

		// Load placeholder models for vegetation
		TriMesh model1 = new Box("box", new Vector3f(-0.1f, -0.1f, -0.1f), new Vector3f(
				0.1f, 0.1f, 0.1f));
		model1.setModelBound(new BoundingBox());
		model1.updateModelBound();
		TriMesh model2 = new Pyramid("pyramid", 0.1f, 0.1f);
		model2.setModelBound(new BoundingBox());
		model2.updateModelBound();
		
		Quad model3 = new Quad("grassQuad",quadSizeX,quadSizeY);
		model3.setModelBound(new BoundingBox(new Vector3f(0,0,0),0,0,0));
		model3.updateModelBound();
		model3.setRenderState(ts==null?default_ts:ts);
		model3.setRenderState(as);

		// Place the darn models
		for (int i = 0; i < vegetationCountX; i++) {
			for (int j = 0; j < vegetationCountZ; j++) {
				float x = i * vegetationSeparation + HashUtil.mixPercentage((int)i, sx+sy+sz, (int)j)/300f;
				float z = j * vegetationSeparation + HashUtil.mixPercentage((int)i+1, sx+sy+sz, (int)j)/300f;

				// find height
				float height = -0.9f;//tb.getHeight(x, z);
				if (Float.isNaN(height)) {
					height = -0.9f;
				}
				
				x+=sx*J3DCore.CUBE_EDGE_SIZE;
				z+=sz*J3DCore.CUBE_EDGE_SIZE;

				height+=sy*J3DCore.CUBE_EDGE_SIZE;
				
				Vector3f translation = new Vector3f(x, height, z);

				// find scale
				float scaleValue = 1.0f;
				Vector3f scale = new Vector3f(scaleValue, scaleValue,
						scaleValue);

				// find rotation
				Vector3f normalY = new Vector3f(0,1f,0);//tb.getSurfaceNormal(x, z, null);
				if (normalY == null) {
					normalY = Vector3f.UNIT_Y;
				}
				Vector3f normalX = normalY.cross(Vector3f.UNIT_X);
				Vector3f normalZ = normalY.cross(normalX);
				normalX = normalY.cross(normalZ);
				Quaternion rotation = new Quaternion();
				rotation.fromAxes(normalX, normalY, normalZ);

				// just mix the models
				if ((i + j) % 2 == 0) {
					vegetation.addVegetationObject(model3, translation, scale,
							rotation);
				} else {
					vegetation.addVegetationObject(model3, translation, scale,
							rotation);
				}
			}
		}

		vegetation.setModelBound(new BoundingBox(new Vector3f(0,0,0),0,0,0));
		vegetation.updateModelBound();
		
		vegetation.setup();

		return vegetation;
	}

}
