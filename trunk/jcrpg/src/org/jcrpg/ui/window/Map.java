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
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.ui.map.WorldMap.LabelContainer;
import org.jcrpg.ui.map.WorldMap.LabelDesc;
import org.jcrpg.ui.mouse.UiMouseEvent;
import org.jcrpg.ui.mouse.UiMouseEvent.UiMouseEventType;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.TextButton;

import com.jme.bounding.BoundingBox;
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
import com.jme.util.TextureManager;

public class Map extends InputWindow {

	WorldMap wmap;
	
	TextButton closeWindow;
	
	TextLabel worldTime = null;
	TextLabel labelDesc = null;
	
	public class MapQuad extends InputBase
	{

		public MapQuad(String id, InputWindow w, Node parentNode) {
			super(id, w, parentNode);
		}

		@Override
		public Node getDeactivatedNode() {
			return null;
		}

		@Override
		public void reset() {
		}
		
		public String tooltip = null;
		
		public boolean handleMouse(UiMouseEvent mouseEvent)
		{
			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_EXITED)
			{
				w.inputLeft(this, "");
			}
			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_MOVED)
			{
				float rX = mouseEvent.getPickedSpatialList().get(0).ratioX;
				float rY = mouseEvent.getPickedSpatialList().get(0).ratioY;
				System.out.println("-- "+rX+" / "+rY);
				String[] v = mouseHover(rX, rY);
				if (v==null)
				{
					tooltip = null;
				} else
				{
					tooltip = v[1];
					
				}
			}
			toggleTooltip(getTooltipText());
			return true;
		}

		@Override
		public String getTooltipText() {
			if (tooltip==null)
				return super.getTooltipText();
			return tooltip;
		}
		
		

		
	}
	
	HashMap<String, LabelDesc> labelsToCoordinates = new HashMap<String, LabelDesc>();
	
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
        float widthRatio = 6.0f;
        float heightRatio = 7.4f;
        float heightDiv = 1.8f;
        {
        	Quad hudQuad = new Quad("hud", (int)((core.getDisplay().getWidth()/10)*widthRatio*1.06f), (int)(((core.getDisplay().getHeight()/10)*heightRatio*1.06f)));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/heightDiv,0));
			
	        hudQuad.setRenderState(frameState);
	        hudQuad.setRenderState(hudAS);
	        windowNode.attachChild(hudQuad);
	        
        }
        
        TextureState[] textureStates = wmap.getMapTextures();
        {
        	Quad hudQuad = new Quad("hud", (core.getDisplay().getWidth()/10)*widthRatio, ((core.getDisplay().getHeight()/10)*heightRatio));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/heightDiv,0));
			
	        hudQuad.setRenderState(textureStates[0]);
	        windowNode.attachChild(hudQuad);
	        hudQuad.setRenderState(hudAS);
        }
        
        
        {
        	Quad hudQuad = new Quad("hud_geo", (core.getDisplay().getWidth()/10)*widthRatio, ((core.getDisplay().getHeight()/10)*heightRatio));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/heightDiv,0));
			
	        hudQuad.setRenderState(textureStates[2]);
	        windowNode.attachChild(hudQuad);
	        hudQuad.setRenderState(hudAS);
        }
        {
        	Quad hudQuad = new Quad("hud_pos", (core.getDisplay().getWidth()/10)*widthRatio, ((core.getDisplay().getHeight()/10)*heightRatio));
	        hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
	        hudQuad.setLightCombineMode(LightCombineMode.Off);
	        hudQuad.setLocalTranslation(new Vector3f(core.getDisplay().getWidth()/2,core.getDisplay().getHeight()/heightDiv,0));
	        wmap.registerQuad(hudQuad);
	        windowNode.attachChild(hudQuad);
	        hudQuad.setRenderState(hudAS);
	        
	        MapQuad mapQuad = new MapQuad("-",this,windowNode);
	        hudQuad.setModelBound(new BoundingBox());
	        mapQuad.baseNode.attachChild(hudQuad);
	        mapQuad.baseNode.updateModelBound();
			mapQuad.globalTooltip=("Small icons show populations. Blue lines are rivers. Brown lines roads. Texts are major town names. Darkest shade shows mountain, medium is forest. Base color is determined by the climate zone.");

	        

        }
        
        LabelContainer labelContainer = wmap.getLabels();
        
        
        float xRatio = 0.586f / J3DCore.getInstance().gameState.world.sizeX;
        float yRatio = 0.717f / J3DCore.getInstance().gameState.world.sizeZ;
        float offsetX = 0.21f;
        float offsetY = 0.092f;
		//TextLabel tlc1 = new TextLabel("1",this,windowNode,0 * 0.01f+offsetX, 0 * 0.01f+offsetY, 0.3f, 0.05f, 700, "xxxx",false);
		//TextLabel tlc2 = new TextLabel("2",this,windowNode,J3DCore.getInstance().gameState.world.sizeX * xRatio+offsetX, J3DCore.getInstance().gameState.world.sizeZ * yRatio +offsetY, 0.3f, 0.05f, 700, "xxxx",false);

        HashSet<String> displayedAlready = new HashSet<String>();
        for (LabelDesc townDesc : labelContainer.towns)
        {
        	
        	String key = townDesc.x+"|"+townDesc.z;
        	labelsToCoordinates.put(key, townDesc);
        	if (displayedAlready.contains(townDesc.text))
        	{
        	} else
        	{
        		displayedAlready.add(townDesc.text);
	        	if (townDesc.scale1>1)
	        	{
	           		TextLabel tl = new TextLabel(""+townDesc.text,this,windowNode,townDesc.x * xRatio+offsetX+ xRatio*0.03f, (J3DCore.getInstance().gameState.world.sizeZ-townDesc.z) * yRatio + offsetY + yRatio*0.03f, 0.0f, 0.05f, 600, lS(townDesc.text),false,false, ColorRGBA.black);
	           		TextLabel tl2 = new TextLabel(""+townDesc.text,this,windowNode,townDesc.x * xRatio+offsetX, (J3DCore.getInstance().gameState.world.sizeZ-townDesc.z) * yRatio + offsetY, 0.0f, 0.05f, 600, lS(townDesc.text),false,false, ColorRGBA.white);
	        	}
        	}
        	/*if (townDesc.scale1==1)
        	{
        		TextLabel tl = new TextLabel(""+townDesc.text,this,windowNode,townDesc.x * xRatio+offsetX, (J3DCore.getInstance().gameState.world.sizeZ-townDesc.z) * yRatio + offsetY, 0.3f, 0.05f, 1100, lS(townDesc.text),false,false, ColorRGBA.white);
        	}*/
        }
        
        
        worldTime = new TextLabel("time",this,windowNode,0.5f, 0.051f, 0.25f, 0.041f,600f,"time__________",true);
        //labelDesc = new TextLabel("labelDesc",this,windowNode,0.5f, 0.838f, 0.25f, 0.041f,600f,"",true);
        
    	closeWindow = new TextButton("close",this,windowNode, 0.81f, 0.071f, 0.013f, 0.031f,800f,"x");
    	addInput(closeWindow);
    	
    	windowNode.updateRenderState();
	}

	public String lS(String s)
	{
		if (s.length()>5)
		{
			return s.substring(0,5)+"."; 
		}
		return s;
	}
	
	@Override
	public void hide() {
		toggleTooltip(null);
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
	}

	@Override
	public void show() {
    	updateTime();
		core.getUIRootNode().attachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		toggleTooltip("Small icons show populations. Blue lines are rivers. Brown lines roads. Texts are major town names. Darkest shade shows mountain, medium is forest. Base color is determined by the climate zone.");
	}

	@Override
	public boolean inputChanged(InputBase base, String message) {
		return false;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		return false;
	}

	@Override
	public boolean inputLeft(InputBase base, String message) {
    	updateTime();
		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base == closeWindow)
		{
			toggle();
			return true;
		}
		return true;
	}
	
	public String[] mouseHover(float x, float y)
	{
		int blockX, blockY;
		blockX = (int)(x*wmap.world.sizeX);
		blockY = (int)((1f-y)*wmap.world.sizeZ);
		
    	String key = blockX+"|"+blockY;
    	LabelDesc desc = labelsToCoordinates.get(key);
    	if (desc!=null)
    	{
    		worldTime.text = desc.text;
    		worldTime.setUpdated(true);
    		worldTime.activate();
    		return new String[]{desc.text,desc.text+", size: "+desc.scale1};
    	}

    	updateTime();
    	
    	return null;

	}
	
	private void updateTime()
	{
		worldTime.text = core.gameState.engine.getWorldMeanTime().toReadableString();
		worldTime.setUpdated(true);
		worldTime.activate();

	}

}
