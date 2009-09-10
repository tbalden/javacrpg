/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.ui;

import java.io.File;
import java.util.ArrayList;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.ui.FlyingNode;
import org.jcrpg.threed.jme.ui.NodeFontFreer;
import org.jcrpg.ui.map.LocalMap;
import org.jcrpg.ui.meter.DirectionTimeMeter;
import org.jcrpg.ui.meter.EntityOMeter;
import org.jcrpg.ui.text.TextBox;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.world.ai.EntityMemberInstance;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

public class HUD {

	public Node hudNode;
	
	public UIBase base;
	public J3DCore core;
	
	public HUDParams params;
	
	public BlendState hudAS;
	public DirectionTimeMeter meter;
	public SystemRelated sr;
	public TextBox mainBox;
	public Characters characters;
	public LocalMap localMap;
	public EntityOMeter entityOMeter;
	
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
        mapQuad.setLightCombineMode(LightCombineMode.Off);
        localMap = new LocalMap(core.gameState.world, core.renderedArea);
        localMap.registerQuad(mapQuad);
        mapQuad.setRenderState(hudAS);
        hudNode.attachChild(mapQuad);
        hudNode.updateRenderState();

	}
	
	public void initNodes() throws Exception
	{
        hudNode = new Node("hudNode");

        // main hud image area
        
        Quad hudQuad = new Quad("hud", core.getDisplay().getWidth(), (core.getDisplay().getHeight()/7.0f));
        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  

        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/14f,0));
 
        Image hudImage = TextureManager.loadImage(new File(params.image).toURI().toURL(),true);
		
        TextureState state = core.getDisplay().getRenderer().createTextureState();
        Texture texture = new Texture2D();
        texture.setImage(hudImage);
        state.setTexture(texture);
        hudQuad.setRenderState(state);

        hudAS = core.getDisplay().getRenderer().createBlendState();
        hudAS.setBlendEnabled(true);
  
        hudAS.setSourceFunction( BlendState.SourceFunction.SourceAlpha);
        hudAS.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha);
        hudAS.setTestEnabled(false);
        hudAS.setEnabled(true);
        hudQuad.setRenderState(hudAS);
        hudNode.attachChild(hudQuad);

        // meter area
        
        meter = new DirectionTimeMeter(this);
        hudNode.attachChild(meter.quad_sign_sun);
        hudNode.attachChild(meter.quad_sign_dir);
        //hudNode.attachChild(meter.quad);
        
        // system
        sr = new SystemRelated(this,new String[]{"LOAD","DICE","CAMPFIRE", "ECONOMY"},new String[]{"./data/ui/hourglass_zphr.png","./data/ui/dice_zphr.png","./data/ui/campfire.png","./data/ui/economic.png"});
        
        // main textbox
        
        mainBox = new TextBox(this,"Main",0.080f,0.114f,0.3f,0.13f);
        mainBox.show();
        mainBox.addEntry(new TextEntry("jClassicRPG pre-alpha version, code is LGPL",ColorRGBA.orange));
        //mainBox.addEntry(new TextEntry("Use keyboard in the menus",ColorRGBA.orange));
        base.addEventHandler("logUp", mainBox);
        base.addEventHandler("logDown", mainBox);
        
        characters = new Characters(this);
        
        entityOMeter = new EntityOMeter(this);
        
		
	}
	
	public void update()
	{
		mapQuad.updateRenderState();
		hudNode.updateRenderState();
	}
	
	public void updateCharacterRelated(EntityMemberInstance instance)
	{
		characters.updatePoints(instance);
	}
	FlyingNode lastOSDText = null;
	public void startFloatingOSDText(String text, ColorRGBA color)
	{
		float y = DisplaySystem.getDisplaySystem().getHeight()/1.6f;
		if (lastOSDText!=null) 
		{
			if (!lastOSDText.isFinishedPlaying())
			{
				y = y*0.9f;
			}
		}
		FlyingNode node = new FlyingNode();
		lastOSDText = node;
		node.setLocalTranslation(DisplaySystem.getDisplaySystem().getWidth()/2f,y,0f);
		Node n = FontUtils.textNonBoldVerdana.createOutlinedText(text, 19,color,new ColorRGBA(0.8f,0.8f,0.8f,1f),true);
		NodeFontFreer freer = new NodeFontFreer(FontUtils.textNonBoldVerdana,n);
		ArrayList<Runnable> onFinish = new ArrayList<Runnable>();
		onFinish.add(freer);
		node.onFinish = onFinish;
		n.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		n.setLocalScale(core.getDisplay().getWidth()/600f);		
		node.attachChild(n);
		hudNode.attachChild(node);
		hudNode.updateRenderState();
		node.startFlying(8f,3.0f);
	}
	
}
