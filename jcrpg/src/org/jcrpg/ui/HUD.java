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

import org.jcrpg.threed.J3DCore;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class HUD {

	public Node hudNode;
	
	public UIBase base;
	public J3DCore core;
	
	public HUDParams params;
	
	public HUD(HUDParams params, UIBase base, J3DCore core) throws Exception
	{
		this.params = params;
		this.base = base;
		this.core = core;
		initNodes();
		
		
	}
	
	public void initNodes() throws Exception
	{
        hudNode = new Node("hudNode");
        Quad hudQuad = new Quad("hud", core.getDisplay().getWidth(), (core.getDisplay().getHeight()));
        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  

        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/2,0));
 
		Image heightImage = TextureManager.loadImage(new File(params.image).toURI().toURL(),true);
		
        TextureState state = core.getDisplay().getRenderer().createTextureState();
        Texture texture = new Texture();
        texture.setImage(heightImage);
        state.setTexture(texture);
        hudQuad.setRenderState(state);

        AlphaState hudAS = core.getDisplay().getRenderer().createAlphaState();
        hudAS.setBlendEnabled(true);
  
        hudAS.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        hudAS.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        hudAS.setTestEnabled(false);
        hudAS.setEnabled(true);
        hudQuad.setRenderState(hudAS);
        hudNode.attachChild(hudQuad);
                
        
        Quad mapQuad = new Quad("hud", core.getDisplay().getWidth()/13, (core.getDisplay().getHeight()/9));
        mapQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  

        mapQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/26,core.getDisplay().getHeight()/18,0));

        mapQuad.setLightCombineMode(LightState.OFF);
        TextureState state2 = core.getDisplay().getRenderer().createTextureState();
        state2.setTexture(core.world.worldMap.getMapTexture());
        mapQuad.setRenderState(state2);
        mapQuad.updateRenderState();
        mapQuad.setRenderState(hudAS);
        hudNode.attachChild(mapQuad);
        
        
		
	}
	
}
