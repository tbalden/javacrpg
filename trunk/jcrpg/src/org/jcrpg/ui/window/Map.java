/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

package org.jcrpg.ui.window;

import java.io.File;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.map.WorldMap;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class Map extends Window {

	WorldMap wmap;
	
	public Map(UIBase base, WorldMap wmap) throws Exception {
		super(base);
		this.wmap = wmap;
        // main hud image area

		BlendState hudAS = base.hud.hudAS;
        //AlphaState hudAS = core.getDisplay().getRenderer().createAlphaState();
        //hudAS.setBlendEnabled(true);
  
/*        hudAS.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        hudAS.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        hudAS.setTestEnabled(false);
        hudAS.setEnabled(true);*/
        
        TextureState frameState= core.getDisplay().getRenderer().createTextureState();
        Texture frameTex = new Texture2D();
        String fileName = "./data/ui/windowframe.dds";
        if (J3DCore.SETTINGS.DISABLE_DDS)
        {
        	fileName = "./data/ui/windowframe.png";     	
        }
        Image frameImg = TextureManager.loadImage(new File(fileName).toURI().toURL(),true);
        frameTex.setImage(frameImg);
        frameState.setTexture(frameTex);
        float widthRatio = 4.0f;
        float heightRatio = 5.0f;
        {
        	Quad hudQuad = new Quad("hud", (int)((core.getDisplay().getWidth()/10)*widthRatio*1.06f), (int)(((core.getDisplay().getHeight()/10)*heightRatio*1.06f)));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/2,0));
			
	        hudQuad.setRenderState(frameState);
	        hudQuad.setRenderState(hudAS);
	        windowNode.attachChild(hudQuad);
	        
        }
        
        TextureState[] textureStates = wmap.getMapTextures();
        {
        	Quad hudQuad = new Quad("hud", (core.getDisplay().getWidth()/10)*widthRatio, ((core.getDisplay().getHeight()/10)*heightRatio));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/2,0));
			
	        hudQuad.setRenderState(textureStates[0]);
	        windowNode.attachChild(hudQuad);
	        hudQuad.setRenderState(hudAS);
        }
        
        
        {
        	Quad hudQuad = new Quad("hud_geo", (core.getDisplay().getWidth()/10)*widthRatio, ((core.getDisplay().getHeight()/10)*heightRatio));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/2,0));
			
	        hudQuad.setRenderState(textureStates[2]);
	        windowNode.attachChild(hudQuad);
	        hudQuad.setRenderState(hudAS);
        }
        {
        	Quad hudQuad = new Quad("hud_pos", (core.getDisplay().getWidth()/10)*widthRatio, ((core.getDisplay().getHeight()/10)*heightRatio));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLightCombineMode(LightCombineMode.Off);
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/2,0));
	        wmap.registerQuad(hudQuad);
	        windowNode.attachChild(hudQuad);
	        hudQuad.setRenderState(hudAS);
        }
        windowNode.updateRenderState();
	}

	@Override
	public void hide() {
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
	}

	@Override
	public void show() {
		core.getUIRootNode().attachChild(windowNode);
		core.getUIRootNode().updateRenderState();
	}
	
	
	
	

}
