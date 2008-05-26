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

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class UIImageCache {

	public static HashMap<String, Quad> imageCache = new HashMap<String, Quad>();
	
	public static SharedMesh getImage(String filePath)
	{
		Quad q = imageCache.get(filePath);
		if (q==null)
		{
			try {
				File file = new File(filePath);
				Quad hudQuad = new Quad(file.getName(), 1f, 1f);
				hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
	
				hudQuad.setLocalTranslation(new Vector3f(0, 0, 0));
	
				Image hudImage = TextureManager.loadImage(file.toURI()
						.toURL(), true);
	
				TextureState state = J3DCore.getInstance().getDisplay().getRenderer()
						.createTextureState();
				Texture texture = new Texture();
				texture.setImage(hudImage);
	
				state.setTexture(texture,0);
				hudQuad.setRenderState(state);
	
				q = hudQuad;
			} catch (Exception ex)
			{
				q = new Quad();
			}
			
		}
		return new SharedMesh(filePath+"Shared",q);
	}
	
}
