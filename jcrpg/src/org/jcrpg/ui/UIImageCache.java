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
package org.jcrpg.ui;

import java.io.File;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.ui.ZoomingQuad;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class UIImageCache {

	public static HashMap<String, TextureState> imageCache = new HashMap<String, TextureState>();
	
	public static Quad getImage(String filePath, boolean alpha, float sizeMul)
	{
		TextureState q = imageCache.get(filePath);
		if (q==null)
		{
			try {
				File file = new File(filePath);
	
				Image hudImage = TextureManager.loadImage(file.toURI()
						.toURL(), true);
	
				TextureState state = J3DCore.getInstance().getDisplay().getRenderer()
						.createTextureState();
				Texture texture = new Texture();
				texture.setImage(hudImage);
	
				state.setTexture(texture,0);
				
	
				q = state;
				imageCache.put(filePath, q);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				q = null;//new Quad();
			}
			
		}
		Quad quad = new Quad(filePath, 1f * sizeMul, 1f * sizeMul);
		if (alpha) quad.setRenderState(J3DCore.getInstance().uiBase.hud.hudAS);
		quad.setRenderState(q);
		quad.updateRenderState();
		//quad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		quad.setLocalTranslation(new Vector3f(0, 0, 0));
		System.out.println("LOADED "+filePath);
		return quad;
	}

	public static Quad getImage(String filePath, boolean alpha, float sizeX, float sizeY)
	{
		TextureState q = imageCache.get(filePath);
		if (q==null)
		{
			try {
				File file = new File(filePath);
	
				Image hudImage = TextureManager.loadImage(file.toURI()
						.toURL(), true);
	
				TextureState state = J3DCore.getInstance().getDisplay().getRenderer()
						.createTextureState();
				Texture texture = new Texture();
				texture.setImage(hudImage);
	
				state.setTexture(texture,0);
				
	
				q = state;
				imageCache.put(filePath, q);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				q = null;//new Quad();
			}
			
		}
		Quad quad = new Quad(filePath, 1f * sizeX, 1f * sizeY);
		if (alpha) quad.setRenderState(J3DCore.getInstance().uiBase.hud.hudAS);
		quad.setRenderState(q);
		quad.updateRenderState();
		//quad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		quad.setLocalTranslation(new Vector3f(0, 0, 0));
		//System.out.println("LOADED "+filePath);
		return quad;
	}

	public static ZoomingQuad getImageZoomingQuad(String filePath, boolean alpha, float sizeX, float sizeY)
	{
		TextureState q = imageCache.get(filePath);
		if (q==null)
		{
			try {
				File file = new File(filePath);
	
				Image hudImage = TextureManager.loadImage(file.toURI()
						.toURL(), true);
	
				TextureState state = J3DCore.getInstance().getDisplay().getRenderer()
						.createTextureState();
				Texture texture = new Texture();
				texture.setImage(hudImage);
	
				state.setTexture(texture,0);
				
	
				q = state;
				imageCache.put(filePath, q);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				q = null;//new Quad();
			}
			
		}
		ZoomingQuad quad = new ZoomingQuad(filePath, 1f * sizeX, 1f * sizeY);
		if (alpha) quad.setRenderState(J3DCore.getInstance().uiBase.hud.hudAS);
		quad.setRenderState(q);
		quad.updateRenderState();
		//quad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		quad.setLocalTranslation(new Vector3f(0, 0, 0));
		//System.out.println("LOADED "+filePath);
		return quad;
	}

}
