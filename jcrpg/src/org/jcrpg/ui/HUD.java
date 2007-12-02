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
import org.jcrpg.ui.meter.DirectionTimeMeter;

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
	
	public AlphaState hudAS;
	public DirectionTimeMeter meter;
	
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
 
		Image hudImage = TextureManager.loadImage(new File(params.image).toURI().toURL(),true);
		
        TextureState state = core.getDisplay().getRenderer().createTextureState();
        Texture texture = new Texture();
        texture.setImage(hudImage);
        state.setTexture(texture);
        hudQuad.setRenderState(state);

        hudAS = core.getDisplay().getRenderer().createAlphaState();
        hudAS.setBlendEnabled(true);
  
        hudAS.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        hudAS.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        hudAS.setTestEnabled(false);
        hudAS.setEnabled(true);
        hudQuad.setRenderState(hudAS);
        hudNode.attachChild(hudQuad);
                
        
        Quad mapQuad = new Quad("hud", core.getDisplay().getWidth()/12, (core.getDisplay().getHeight()/9));
        mapQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  

        mapQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth() - (core.getDisplay().getWidth()/24),(core.getDisplay().getHeight()/18),0));

        mapQuad.setLightCombineMode(LightState.OFF);
        TextureState state2 = core.getDisplay().getRenderer().createTextureState();
        state2.setTexture(core.world.worldMap.getMapTexture());
        mapQuad.setRenderState(state2);
        mapQuad.updateRenderState();
        mapQuad.setRenderState(hudAS);
        hudNode.attachChild(mapQuad);
        
        meter = new DirectionTimeMeter(this);
        meter.quad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        meter.quad.setLocalTranslation(new Vector3f((core.getDisplay().getWidth()/26),(core.getDisplay().getHeight()/18),0));
        meter.quad.setLightCombineMode(LightState.OFF);
        meter.quad.updateRenderState();
        hudNode.attachChild(meter.quad);
        
        meter.quad_sign_dir.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        meter.quad_sign_dir.setLocalTranslation(new Vector3f((core.getDisplay().getWidth()/26),(core.getDisplay().getHeight()/18),0));
        meter.quad_sign_dir.setLightCombineMode(LightState.OFF);
        meter.quad_sign_dir.updateRenderState();
        hudNode.attachChild(meter.quad_sign_dir);
        
        meter.quad_sign_sun.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        meter.quad_sign_sun.setLocalTranslation(new Vector3f((core.getDisplay().getWidth()/26),(core.getDisplay().getHeight()/18),0));
        meter.quad_sign_sun.setLightCombineMode(LightState.OFF);
        meter.quad_sign_sun.updateRenderState();
        hudNode.attachChild(meter.quad_sign_sun);        
		
	}
	
}
