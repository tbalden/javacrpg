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

package org.jcrpg.ui;

import java.io.File;
import java.util.HashMap;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * HUD display for system related things like load icon.
 * @author pali
 *
 */
public class SystemRelated {

	public HUD hud;
	
	public HashMap<String, Quad> hmQuad = new HashMap<String, Quad>();
	
	public SystemRelated(HUD hud, String[] quadIds, String[] images) throws Exception
	{
		this.hud = hud;
		int y = 0;
		for (String quString:quadIds)
		{
			Quad q = new Quad("Q_"+quString,hud.core.getDisplay().getWidth()/19,hud.core.getDisplay().getHeight()/15);
			Image baseImage = TextureManager.loadImage(new File(images[y]).toURI().toURL(),true);
			Texture base_tex = new Texture();
			base_tex.setImage(baseImage);			
	        TextureState state = hud.core.getDisplay().getRenderer().createTextureState();
			state.setTexture(base_tex, 0);
			q.setRenderState(state);
			q.setCullMode(Quad.CULL_ALWAYS);
			q.setRenderState(hud.hudAS);
	        q.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        q.setLocalTranslation(new Vector3f((hud.core.getDisplay().getWidth()/40)*2,hud.core.getDisplay().getHeight()-(1*(hud.core.getDisplay().getHeight()/36))*2-(y*(hud.core.getDisplay().getHeight()/18)),0));
	        q.setLightCombineMode(LightState.OFF);
	        q.updateRenderState();
	        hmQuad.put(quString, q);
			hud.hudNode.attachChild(q);
			y++;
		}
	}
	
	public void setVisibility(boolean visible, String id)
	{
		try {
			hmQuad.get(id).setCullMode(visible?Quad.CULL_NEVER:Quad.CULL_ALWAYS);
			hmQuad.get(id).updateRenderState();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
}
