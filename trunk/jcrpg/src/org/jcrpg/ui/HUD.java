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
import org.jcrpg.ui.map.LocalMap;
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
	public Characters characters;
	public LocalMap localMap;
	
	public HUD(HUDParams params, UIBase base, J3DCore core) throws Exception
	{
		this.params = params;
		this.base = base;
		this.core = core;
		initNodes();
		
		
	}
	Quad mapQuad;
	
	/**
	 * Used for reinitializing too when gamestate is ready.
	 */
	public void initGameStateNodes()
	{
        // world map area
        
		float dispY = 1.12f;
		float dispX = 1.1f;
		float sizeY = 1.05f;
		float sizeX = 1.05f;
        if (mapQuad!=null) mapQuad.removeFromParent();
        mapQuad = new Quad("hud", (1f/sizeX) * core.getDisplay().getWidth()/12, (1f/sizeY) *(core.getDisplay().getHeight()/9));
        mapQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        mapQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth() - dispX*(core.getDisplay().getWidth()/24),dispY*(core.getDisplay().getHeight()/18),0));
        mapQuad.setLightCombineMode(LightState.OFF);
        localMap = new LocalMap(core.gameState.world, core.renderedArea);
        localMap.registerQuad(mapQuad);
        //TextureState[] textures = localMap.getMapTextures();
        //mapQuad.setRenderState(textures[0]);
        //mapQuad.updateRenderState();
        mapQuad.setRenderState(hudAS);
        hudNode.attachChild(mapQuad);
        hudNode.updateRenderState();

	}
	
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

        // meter area
        
        meter = new DirectionTimeMeter(this);
        hudNode.attachChild(meter.quad_sign_dir);
        hudNode.attachChild(meter.quad_sign_sun);
        hudNode.attachChild(meter.quad);
        
        // system
        sr = new SystemRelated(this,new String[]{"LOAD","DICE"},new String[]{"./data/ui/hourglass.png","./data/ui/dice2.png"});
        
        // main textbox
        
        mainBox = new TextBox(this,"Main",0.088f,0.088f,0.3f,0.1f);
        mainBox.show();
        mainBox.addEntry(new TextEntry("JCRPG pre-alpha version",ColorRGBA.orange));
        base.addEventHandler("logUp", mainBox);
        base.addEventHandler("logDown", mainBox);
        
        characters = new Characters(this);
		
	}
	
	public void update()
	{
		mapQuad.updateRenderState();
		hudNode.updateRenderState();
	}
	
}
