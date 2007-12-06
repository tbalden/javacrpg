/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.ui.window;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.map.WorldMap;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;

public class Map extends Window {

	WorldMap wmap;
	
	public Map(UIBase base, WorldMap wmap) {
		super(base);
		this.wmap = wmap;
        // main hud image area

        AlphaState hudAS = core.getDisplay().getRenderer().createAlphaState();
        hudAS.setBlendEnabled(true);
  
        hudAS.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        hudAS.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        hudAS.setTestEnabled(false);
        hudAS.setEnabled(true);
        TextureState[] textureStates = wmap.getMapTextures();
        {
        	Quad hudQuad = new Quad("hud", (core.getDisplay().getWidth()/10)*3, ((core.getDisplay().getHeight()/10)*4));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/2,0));
			
	        hudQuad.setRenderState(textureStates[0]);
	        windowNode.attachChild(hudQuad);
        }
        
        {
        	Quad hudQuad = new Quad("hud", (core.getDisplay().getWidth()/10)*6, ((core.getDisplay().getHeight()/10)*8));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/2,0));
			
	        hudQuad.setRenderState(textureStates[1]);
	        windowNode.attachChild(hudQuad);
	        hudQuad.setRenderState(hudAS);
        }
        
        
	}

	@Override
	public void hide() {
		core.getRootNode().detachChild(windowNode);
		core.getRootNode().updateRenderState();
	}

	@Override
	public void show() {
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
	}
	
	
	
	

}
