/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.ModelPool.PoolItemContainer;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;

public class GeoTileLoader {

	ModelLoader l = null;
	public GeoTileLoader(ModelLoader l)
	{
		this.l = l;
	}
	
	public class TerrainBlockExt extends Node implements PooledNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		PoolItemContainer pic = null;
		public PoolItemContainer getPooledContainer() {
			return pic;
		}

		public void setPooledContainer(PoolItemContainer cont) {
			pic = cont;
		}
		
	}
	
	public TerrainBlockExt loadNode(SimpleModel model, RenderedCube rCube)
	{
		TerrainBlockExt ext = new TerrainBlockExt();
		TerrainBlock block = new TerrainBlock();
		float[] cornerHeights = rCube.cube.cornerHeights;
		block.setSize(2);
		int[] map = new int[4];
		for (int i=0; i<4; i++)
		{
			int h = (int)cornerHeights[i]*10;
			map[i] = h;
		}
		block.setHeightMap(map);
		block.updateFromHeightMap();
		ext.attachChild(block);
		ext.updateRenderState();
		return ext;
	}
	
	public TerrainBlock loadNodeOriginal(SimpleModel model, RenderedCube rCube)
	{
		
		float[] cornerHeights = rCube.cube.cornerHeights;
		int[] map = new int[4];
		for (int i=0; i<4; i++)
		{
			int h = (int)(cornerHeights[i%4]*10000f);
			map[i] = h;
		}
		
		float northOverrideDiff = cornerHeights[4];
		float southOverrideDiff = cornerHeights[6];
		float eastOverrideDiff = cornerHeights[5];
		float westOverrideDiff = cornerHeights[7];
		
		
		TerrainBlock block = new TerrainBlock("1",2,new Vector3f(2,0.00020f,2),map,new Vector3f(0f,0,0f),false);
		
		SimpleModel o = model;
		Spatial spatial = block;
		if (o.textureName!=null)
		{
			Texture texture = (Texture)ModelLoader.textureCache.get(o.textureName);
			
			if (texture==null) {
				texture = TextureManager.loadTexture("./data/textures/"+l.TEXDIR+o.textureName,Texture.MM_LINEAR,
	                    Texture.FM_LINEAR);

				texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
				texture.setApply(Texture.AM_MODULATE);
				texture.setRotation(J3DCore.qTexture);
				ModelLoader.textureCache.put(o.textureName, texture);
			}

			TextureState ts = l.core.getDisplay().getRenderer().createTextureState();
			ts.setTexture(texture, 0);
			
            ts.setEnabled(true);
            spatial.setRenderState(ts);
			
		} else 
		{
			Jcrpg.LOGGER.info(o.modelName);
			//setTextures(node,o.mipMap);
		}
		
		return block;
	}
}
