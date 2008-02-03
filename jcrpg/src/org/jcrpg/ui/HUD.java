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
import org.jcrpg.ui.text.TextBox;
import org.jcrpg.ui.text.TextEntry;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
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
	public SystemRelated sr;
	public TextBox mainBox;
	
	public HUD(HUDParams params, UIBase base, J3DCore core) throws Exception
	{
		this.params = params;
		this.base = base;
		this.core = core;
		initNodes();
		
		
	}
	Quad mapQuad_pos;
	
	public void initNodes() throws Exception
	{
        hudNode = new Node("hudNode");

        // main hud image area
        
        Quad hudQuad = new Quad("hud", core.getDisplay().getWidth(), (core.getDisplay().getHeight()/8));
        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  

        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/16,0));
 
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
                
        // world map area
        
        Quad mapQuad = new Quad("hud", core.getDisplay().getWidth()/12, (core.getDisplay().getHeight()/9));
        mapQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        mapQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth() - (core.getDisplay().getWidth()/24),(core.getDisplay().getHeight()/18),0));
        mapQuad.setLightCombineMode(LightState.OFF);
        TextureState[] textures = core.world.worldMap.getMapTextures();
        mapQuad.setRenderState(textures[0]);
        mapQuad.updateRenderState();
        mapQuad.setRenderState(hudAS);
        hudNode.attachChild(mapQuad);

        mapQuad_pos = new Quad("hud_pos", core.getDisplay().getWidth()/12, (core.getDisplay().getHeight()/9));
        mapQuad_pos.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        mapQuad_pos.setLocalTranslation(new Vector3f(core.getDisplay().getWidth() - (core.getDisplay().getWidth()/24),(core.getDisplay().getHeight()/18),0));
        mapQuad_pos.setLightCombineMode(LightState.OFF);
        mapQuad_pos.setRenderState(textures[1]);
        mapQuad_pos.updateRenderState();
        mapQuad_pos.setRenderState(hudAS);
        hudNode.attachChild(mapQuad_pos);
        
        Quad mapQuad_geo = new Quad("hud_geo", core.getDisplay().getWidth()/12, (core.getDisplay().getHeight()/9));
        mapQuad_geo.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        mapQuad_geo.setLocalTranslation(new Vector3f(core.getDisplay().getWidth() - (core.getDisplay().getWidth()/24),(core.getDisplay().getHeight()/18),0));
        mapQuad_geo.setLightCombineMode(LightState.OFF);
        mapQuad_geo.setRenderState(textures[2]);
        mapQuad_geo.updateRenderState();
        mapQuad_geo.setRenderState(hudAS);
        hudNode.attachChild(mapQuad_geo);

        // meter area
        
        meter = new DirectionTimeMeter(this);
        hudNode.attachChild(meter.quad);
        hudNode.attachChild(meter.quad_sign_dir);
        hudNode.attachChild(meter.quad_sign_sun);
        
        // system
        sr = new SystemRelated(this,new String[]{"LOAD","DICE"},new String[]{"./data/ui/floppy.png","./data/ui/dice.png"});
        
        // main textbox
        
        mainBox = new TextBox(this,"Main",0.088f,0.088f,0.3f,0.1f);
        mainBox.addEntry(new TextEntry("JCRPG pre-alpha version",ColorRGBA.orange));
        base.addEventHandler("logUp", mainBox);
        base.addEventHandler("logDown", mainBox);
		
	}
	
	public void update()
	{
	
	}
	
}
