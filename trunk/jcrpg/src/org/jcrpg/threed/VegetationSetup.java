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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.jme.TrimeshGeometryBatch;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

public class VegetationSetup {

	static AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
	
	static {

	    as.setEnabled(true);
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as.setReference(0.3f); // the grass needs a higher value for bugfixing blueness of grass blended!
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
	}


	//private static final float[] lightPosition = { -0.8f, 0.8f, 0.8f, 0.0f };
	
	
	public static HashMap<String, Node[]> quadCache = new HashMap<String, Node[]>();
	
	/**
	 * Returns a vegetation TriMesh, rotated, translated in worldspace, to be usable with TrimeshGeometryBatch as
	 * added item.
	 * @param c cube for world coords
	 * @param core Core
	 * @param tm model
	 * @param k relative coordinate X inside the cube
	 * @param j relative coordinate Y inside the cube
	 * @param heightDiff percentage of height in a Cube for the given trimesh. (0f-1f)
	 * @param variationCutter the bigger this value the lass random deviation in position. (100f default)
	 * @return
	 */
	public static TriMesh getVegTrimesh(boolean internal, NodePlaceholder place, RenderedCube c, J3DCore core,TextureStateVegetationModel tm, int k, int j, float heightDiff, float variationCutter)
	{
		TextureState[] ts = core.modelLoader.loadTextureStates(tm.textureNames);
		Node[] quads = quadCache.get(tm.getKey()+internal);
		if (quads==null) 
		{
			Jcrpg.LOGGER.warning("New veg quad :"+tm.getKey()+internal);
			quads = new Node[ts.length];
			for (int i=0; i<ts.length; i++){
				Node n = new Node();
				//if (steepDirection==SurfaceHeightAndType.NOT_STEEP) 
				{
	
					//Box quad = new Box("grassQuad",new Vector3f(),tm.quadSizeX,tm.quadSizeY,tm.quadSizeY);
					Quad quad = new Quad("grassQuad",tm.quadSizeX,tm.quadSizeY);
					//quad.setLightCombineMode(LightState.INHERIT);
					quad.setModelBound(new BoundingBox());
					//quad.setDefaultColor(ColorRGBA.green);
					quad.updateModelBound();
					
					Texture t1 = ts[i].getTexture();
					t1.setApply(Texture.AM_MODULATE);
					t1.setCombineFuncRGB(Texture.ACF_MODULATE);
					t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
					t1.setCombineOp0RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
					t1.setCombineSrc1RGB(Texture.ACS_TEXTURE);
					t1.setCombineOp1RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
					t1.setCombineScaleRGB(1.0f);
					quad.setRenderState(ts[i]);
					quad.setRenderState(as);
					if (!internal) {
						quad.setLightCombineMode(LightState.OFF);
						quad.setSolidColor(new ColorRGBA(1,1,1,1));
						J3DCore.hmSolidColorSpatials.put(quad,quad);
					}
					MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
					.createMaterialState();
					ms.setColorMaterial(MaterialState.CM_AMBIENT_AND_DIFFUSE);
					quad.setRenderState(ms);
					
					n.attachChild(quad);
	
					if (J3DCore.DOUBLE_GRASS) {
						SharedMesh sQ = new SharedMesh("sharedQuad",quad);
						n.attachChild(sQ);
					}
				} 
				quads[i] = n;
			}
			quadCache.put(tm.getKey()+internal, quads);
		}
		float quadSeparation = tm.quadSeparation/(J3DCore.DOUBLE_GRASS?2:1);
		float x = k * quadSeparation + (HashUtil.mixPercentage((int)k, c.cube.x+c.cube.y+c.cube.z+tm.id.length(), (int)j)/variationCutter) - (100/variationCutter/2f);
		float z = j * quadSeparation + (HashUtil.mixPercentage((int)k+1, c.cube.x+c.cube.y+c.cube.z+tm.id.length(), (int)j)/variationCutter) - (100/variationCutter/2f);
		x = Math.min(x, J3DCore.CUBE_EDGE_SIZE - quadSeparation/4f);
		z = Math.min(z, J3DCore.CUBE_EDGE_SIZE - quadSeparation/4f);
		x = Math.max(x,  + quadSeparation/4f);
		z = Math.max(z,  + quadSeparation/4f);

		// find height
		float height = 0.f;//tb.getHeight(x, z);
		height+=heightDiff;
		
		// adding CUBE_EDGE_SIZE halfs, and half of the quad to height, to display properly
		Vector3f translation = new Vector3f(x - J3DCore.CUBE_EDGE_SIZE/2, height + tm.quadSizeY/2,-z + J3DCore.CUBE_EDGE_SIZE/2);
		translation.addLocal(place.getLocalTranslation());

		// find scale
		float scaleValue = 1.0f+(HashUtil.mixPercentage((int)k, c.cube.x+c.cube.y+c.cube.z, (int)j)/150f) - (100/150f/2f);
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
		Vector3f look = core.getCamera().getDirection().negate();
		Vector3f left1 = core.getCamera().getLeft().negate();
		Quaternion orient = new Quaternion();
		orient.fromAxes(left1, core.getCamera().getUp(), look);
		//Quaternion rotation = new Quaternion();
		//rotation.fromAxes(normalX, normalY, normalZ);
		TriMesh tri = (TriMesh)quads[0].getChild(0);
		tri.setLocalRotation(orient);
		tri.setLocalTranslation(translation);
		tri.setLocalScale(scale);
		//if (steepDirection==SurfaceHeightAndType.NOT_STEEP)
		{
			//rotation.multLocal(new Quaternion(new float[]{0, HashUtil.mixPercentage((int)k, c.cube.x+c.cube.y+c.cube.z, (int)j)*3.6f ,0}));
		}
		return tri;
		 
	}
	
	public static Node createVegetation(RenderedCube c, J3DCore core, Camera cam, TextureState[] ts, TextureStateVegetationModel tm) {
		
		int steepDirection = c.cube.steepDirection;

		if (steepDirection!=SurfaceHeightAndType.NOT_STEEP)
		{
			//return vegetation;
		}

			// Load placeholder models for vegetation
		Node[] quads = quadCache.get(tm.getKey());
		if (quads==null) 
		{
			quads = new Node[ts.length];
			for (int i=0; i<ts.length; i++){
				Node n = new Node();
				//if (steepDirection==SurfaceHeightAndType.NOT_STEEP) 
				{
	
					//Box quad = new Box("grassQuad",new Vector3f(),tm.quadSizeX,tm.quadSizeY,tm.quadSizeY);
					Quad quad = new Quad("grassQuad",tm.quadSizeX,tm.quadSizeY);
					//quad.setLightCombineMode(LightState.INHERIT);
					quad.setModelBound(new BoundingBox());
					//quad.setDefaultColor(ColorRGBA.green);
					quad.updateModelBound();
					
					Texture t1 = ts[i].getTexture();
					t1.setApply(Texture.AM_MODULATE);
					t1.setCombineFuncRGB(Texture.ACF_MODULATE);
					t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
					t1.setCombineOp0RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
					t1.setCombineSrc1RGB(Texture.ACS_TEXTURE);
					t1.setCombineOp1RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
					t1.setCombineScaleRGB(1.0f);
					quad.setRenderState(ts[i]);
					quad.setRenderState(as);
					quad.setLightCombineMode(LightState.OFF);
					quad.setSolidColor(new ColorRGBA(1,1,1,1));
					MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
					.createMaterialState();
					ms.setColorMaterial(MaterialState.CM_AMBIENT_AND_DIFFUSE);
					quad.setRenderState(ms);

					//quad.setRenderState(vp);// TODO  grassMove.vp programming! :-)
					//quad.setRenderState(fp);
					
					n.attachChild(quad);
					J3DCore.hmSolidColorSpatials.put(quad,quad);
	
					if (J3DCore.DOUBLE_GRASS) {
						SharedMesh sQ = new SharedMesh("sharedQuad",quad);
						n.attachChild(sQ);
					}
				} 
				//else
				{
					// till we have a better vegetation rotation, no grass vegetation on steep... :(
					//return vegetation;
	
				}
				quads[i] = n;
			}
			quadCache.put(tm.getKey(), quads);
		}
		TrimeshGeometryBatch vegetation = new TrimeshGeometryBatch(tm.getKey(),core,(TriMesh)quads[0].getChild(0),false,null);
		

		int quadQuantity = tm.quadQuantity*(J3DCore.DOUBLE_GRASS?2:1);
		float quadSeparation = tm.quadSeparation/(J3DCore.DOUBLE_GRASS?2:1);
		
		// Place the darn models
		for (int i = 0; i < quadQuantity; i++) {
			for (int j = 0; j < quadQuantity; j++) {
				float x = i * quadSeparation + (HashUtil.mixPercentage((int)i, c.cube.x+c.cube.y+c.cube.z, (int)j)/150f) - (100/150f/2f);
				float z = j * quadSeparation + (HashUtil.mixPercentage((int)i+1, c.cube.x+c.cube.y+c.cube.z, (int)j)/150f) - (100/150f/2f);
				x = Math.min(x, J3DCore.CUBE_EDGE_SIZE - quadSeparation/4f);
				z = Math.min(z, J3DCore.CUBE_EDGE_SIZE - quadSeparation/4f);
				x = Math.max(x,  + quadSeparation/4f);
				z = Math.max(z,  + quadSeparation/4f);

				// find height
				float height = -0.f;//tb.getHeight(x, z);
				if (Float.isNaN(height)) {
					height = -0.f;
				}
				
				
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
				TriMesh tri = (TriMesh)quads[0].getChild(0);
				tri.setLocalRotation(rotation);
				tri.setLocalTranslation(translation);
				tri.setLocalScale(scale);
				//if (steepDirection==SurfaceHeightAndType.NOT_STEEP)
				{
					rotation.multLocal(new Quaternion(new float[]{0, HashUtil.mixPercentage((int)i, c.cube.x+c.cube.y+c.cube.z, (int)j)*3.6f ,0}));
				}
				/*if (steepDirection!=SurfaceHeightAndType.NOT_STEEP)
				{
					rotation.multLocal(J3DCore.steepRotations.get(new Integer(J3DCore.NORTH)));
					rotation.multLocal(new Quaternion(new float[]{0, HashUtil.mixPercentage((int)i, c.cube.x+c.cube.y+c.cube.z, (int)j)*3.6f ,0}));
				}*/

				// add from diff views same quad, to be nicely displayed
				vegetation.addItem(new NodePlaceholder(),tri);/*VegetationObject(quads[HashUtil.mix(c.cube.x+i,c.cube.y,c.cube.z+j)%quads.length], translation, scale,
						rotation);*/
	//			vegetation.addVegetationObject(quads[(i+j)%quads.length], translation, scale,
		//				rotation);
			}
		}

		//vegetation.setModelBound(new BoundingBox());
		//vegetation.updateModelBound();
		
		//vegetation.setup();

		return vegetation.parent;
	}

}
