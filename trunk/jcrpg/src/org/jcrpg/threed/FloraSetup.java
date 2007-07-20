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

import java.nio.FloatBuffer;

import org.jcrpg.threed.jme.vegetation.AbstractVegetation;
import org.jcrpg.threed.jme.vegetation.NaiveVegetation;
import org.jcrpg.threed.jme.vegetation.QuadTreeVegetation;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.TextureStateModel;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.BatchMesh;
import com.jme.scene.BillboardNode;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.QuadBatch;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

public class FloraSetup {

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
		as.setDstFunction(AlphaState.DB_ONE_MINUS_DST_ALPHA);
		as.setReference(0.0f);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
	}


	public static Node createVegetation2(RenderedCube c, Camera cam,
			TextureState ts, TextureStateModel tm) {
		FloatBuffer texCoords = BufferUtils.createFloatBuffer(new float[] { 0,
				1, 0, 0, 1, 1, 1, 0, 2, 1, 2, 0 });
		QuadBatch qbQuads = new QuadBatch();
		qbQuads.setMode(QuadBatch.QUADS);
		qbQuads.setVertexBuffer(BufferUtils.createFloatBuffer(getVertsQuad(0,
				0, 0, tm.quadSizeX, tm.quadSizeY, tm.quadQuantity)));
		//qbQuads.setTextureBuffer(texCoords, 0);
		// 1 quad, specified in counter clockwise order.
		qbQuads.setIndexBuffer(BufferUtils.createIntBuffer(new int[] { 0, 1, 3,
				2 }));

		BatchMesh mesh = new BatchMesh("batches", qbQuads);

		// we set a cull state to hide the back of our batches, "proving" they
		// are camera facing.
		// CullState cull = display.getRenderer().createCullState();
		// cull.setCullMode(CullState.CS_BACK);
		// mesh.setRenderState(cull);

		mesh.setRenderState(ts);

		mesh.updateRenderState();
		Node r = new Node();
		r.attachChild(mesh);
		return r;
	}

	static Vector3f[] getVertsQuad(int x, int y, int z, float QUAD_SIZE_X,
			float QUAD_SIZE_Y, int QUAD_QUANTITY) {
		float ySize = QUAD_SIZE_Y;
		float xSize = QUAD_SIZE_X;
		Vector3f[] verts = new Vector3f[QUAD_QUANTITY * QUAD_QUANTITY * 4];
		for (float localX = 0; localX < QUAD_QUANTITY; localX++) {

			for (float localZ = 0; localZ < QUAD_QUANTITY; localZ++) {
				float cx = x * J3DCore.CUBE_EDGE_SIZE * 1f + localX
						* (J3DCore.CUBE_EDGE_SIZE / QUAD_QUANTITY)
						+ HashUtil.mixPercentage((int) localX, 0, (int) localZ)
						/ 1000f;
				float cy = y * J3DCore.CUBE_EDGE_SIZE;
				float cz = z
						* 1f
						* J3DCore.CUBE_EDGE_SIZE
						+ localZ
						* (J3DCore.CUBE_EDGE_SIZE / QUAD_QUANTITY)
						+ HashUtil.mixPercentage((int) localX + 2, 0,
								(int) localZ) / 1000f;
				;
				verts[(int) (localX * QUAD_QUANTITY * 4 + localZ * 4)] = new Vector3f(
						cx, cy, cz);
				verts[(int) (localX * QUAD_QUANTITY * 4 + localZ * 4 + 1)] = new Vector3f(
						cx + xSize, cy, cz);
				verts[(int) (localX * QUAD_QUANTITY * 4 + localZ * 4 + 2)] = new Vector3f(
						cx + xSize, cy + ySize, cz);
				verts[(int) (localX * QUAD_QUANTITY * 4 + localZ * 4 + 3)] = new Vector3f(
						cx + xSize / 10f, cy + ySize + ySize / 5f, cz);
				// System.out.println("QUAD VECT "+(int)(localX*10+localZ*4)+"
				// "+localX+" "+localZ+" "+verts[(int)(localX*10+localZ*3)]);
			}
		}
		return verts;
	}
	  
	
	public static Node createVegetation(RenderedCube c, Camera cam, TextureState[] ts, TextureStateModel tm) {
		
		// Load the vegetation class of your choice
		AbstractVegetation vegetation = new NaiveVegetation("vegetation",
				cam, 500.0f);

		//vegetation.setCullMode(Spatial.CULL_DYNAMIC);
		vegetation.initialize();
		
		int steepDirection = c.cube.steepDirection;

		// Load placeholder models for vegetation
		Quad[] quads = new Quad[ts.length];
		for (int i=0; i<ts.length; i++){ 
			Quad quad = new Quad("grassQuad",tm.quadSizeX,tm.quadSizeY);
			quad.setModelBound(new BoundingBox());
			quad.updateModelBound();
			quad.setRenderState(ts[i]==null?default_ts:ts[i]);
			quad.setRenderState(as);
			quads[i] = quad;
			//BillboardNode model3 = new BillboardNode();
			//model3.attachChild(quad);
			//model3.setAlignment(BillboardNode.SCREEN_ALIGNED);
		}
		

		// Place the darn models
		for (int i = 0; i < tm.quadQuantity; i++) {
			for (int j = 0; j < tm.quadQuantity; j++) {
				float x = i * tm.quadSeparation + (HashUtil.mixPercentage((int)i, c.cube.x+c.cube.y+c.cube.z, (int)j)/150f) - (100/150f/2f);
				float z = j * tm.quadSeparation + (HashUtil.mixPercentage((int)i+1, c.cube.x+c.cube.y+c.cube.z, (int)j)/150f) - (100/150f/2f);
				x = Math.min(x, J3DCore.CUBE_EDGE_SIZE - tm.quadSeparation/4f);
				z = Math.min(z, J3DCore.CUBE_EDGE_SIZE - tm.quadSeparation/4f);
				x = Math.max(x,  + tm.quadSeparation/4f);
				z = Math.max(z,  + tm.quadSeparation/4f);

				// find height
				float height = -0.f;//tb.getHeight(x, z);
				if (Float.isNaN(height)) {
					height = -0.f;
				}
				
				/*if (steepDirection!=SurfaceHeightAndType.NOT_STEEP)
				{
					int[] vector = J3DCore.moveTranslations.get(new Integer(steepDirection));
					height+= 0.55f + ( (tm.quadQuantity-1-i) * (J3DCore.CUBE_EDGE_SIZE/(tm.quadQuantity))) * vector[0];
					height+= 0.55f + ( (j-1) * (J3DCore.CUBE_EDGE_SIZE/(tm.quadQuantity))) * vector[2];
				}*/
				
				// adding CUBE_EDGE_SIZE halfs, and half of the quad to height, to display properly
				Vector3f translation = new Vector3f(x - J3DCore.CUBE_EDGE_SIZE/2, -z + J3DCore.CUBE_EDGE_SIZE/2, height + tm.quadSizeY/2);

				// find scale
				float scaleValue = 1.0f+(HashUtil.mixPercentage((int)i, c.cube.x+c.cube.y+c.cube.z, (int)j)/150f) - (100/150f/2f);
				Vector3f scale = new Vector3f(scaleValue, scaleValue,
						scaleValue);

				// find rotation
				Vector3f normalY = new Vector3f(0,0,1f);//tb.getSurfaceNormal(x, z, null);
				if (normalY == null) {
					normalY = Vector3f.UNIT_Y;
				}
				Vector3f normalX = normalY.cross(Vector3f.UNIT_X);
				Vector3f normalZ = normalY.cross(normalX);
				normalX = normalY.cross(normalZ);
				Quaternion rotation = new Quaternion();
				rotation.fromAxes(normalX, normalY, normalZ);
				rotation.multLocal(new Quaternion(new float[]{0, HashUtil.mixPercentage((int)i, c.cube.x+c.cube.y+c.cube.z, (int)j)*3.6f ,0}));

				// add from two diff view same quad, to be nicely displayed
				//vegetation.addVegetationObject(quad, translation, scale,
					//	rotation.add(J3DCore.qE));
				vegetation.addVegetationObject(quads[HashUtil.mix(c.cube.x,c.cube.y,c.cube.z)%quads.length], translation, scale,
						rotation);
			}
		}

		vegetation.setModelBound(new BoundingBox(new Vector3f(0,0,0),0,0,0));
		vegetation.updateModelBound();
		
		vegetation.setup();
		vegetation.setRenderState(as);

		return vegetation;
	}

}
