/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.ui.NodeFontFreer;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.text.Text;
import org.jcrpg.ui.window.element.SaveSlotData;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.MenuImageButton;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.util.saveload.SaveLoadNewGame;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class LoadMenu extends InputWindow implements KeyListener {
	
	
	int selected = 0;
	
	int fromSlot = 0;
	
	public static int maxSlots = 4;
	
	public ArrayList<MenuImageButton> buttons = new ArrayList<MenuImageButton>();

	FontTT text;
	
	Quad hudQuad;
	
	TextButton closeWindow;
	TextButton prevWindow;
	TextButton nextWindow;
	
	public LoadMenu(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
        try {
        	
        	hudQuad = loadImageToQuad("./data/ui/mainmenu/mainMenu.png", 0.8f*1.2f*core.getDisplay().getWidth() / 2, 1.4f*(core.getDisplay().getHeight() / 2), 
        			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
         	hudQuad.setRenderState(base.hud.hudAS);
        	//Quad logoQuad = loadImageToQuad("./data/ui/mainmenu/menu-logo.png", 2.23f*core.getDisplay().getWidth() / 5f, 2.23f*(core.getDisplay().getHeight() / 11), 
        		//	core.getDisplay().getWidth() / 2, 1.62f*core.getDisplay().getHeight() / 2);
        	//logoQuad.setRenderState(base.hud.hudAS);

         	
			//windowNode.attachChild(logoQuad);
			windowNode.attachChild(hudQuad);
			
			updateFromDirectory();
			
			loadSlots();
			highlightSelected();

	    	closeWindow = new TextButton("close",this,windowNode, 0.63f, 0.310f, 0.03f, 0.045f,600f," <-");
	    	addInput(closeWindow);
	    	prevWindow = new TextButton("prev",this,windowNode, 0.50f, 0.310f, 0.05f, 0.035f,1000f," ...");
	    	addInput(prevWindow);
	    	nextWindow = new TextButton("next",this,windowNode, 0.50f, 0.710f, 0.05f, 0.035f,1000f," ...");
	    	addInput(nextWindow);

			
			
			windowNode.updateRenderState();
			base.addEventHandler("lookUp", this);
			base.addEventHandler("lookDown", this);
			base.addEventHandler("enter", this);
			base.addEventHandler("back", this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	static TreeMap<String, SaveSlotData> dataList = null;
	
	public static boolean updateFromDirectory()
	{
		try {
			File f = new File(SaveLoadNewGame.saveDir);
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("LoadMenu # FILE: "+f.getAbsolutePath());
			String[] files = f.list();
			TreeMap<String, SaveSlotData> dataList1 = new TreeMap<String, SaveSlotData>();
			if (files!=null)
			for (String file:files)
			{
				//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("# FILE: "+file);
				if (new File(f.getAbsolutePath()+"/"+file).isDirectory())
				{
					SaveSlotData data = new SaveSlotData();
					data.slotName = file;

					String[] subFiles = new File(f.getAbsolutePath()+"/"+file).list();
					for (String sFile:subFiles)
					{
						//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("F: "+sFile);
						File sF = new File(SaveLoadNewGame.saveDir+"/"+file+"/"+sFile);
						if (sF.isFile())
						{
							if (sF.getName().endsWith(".zip"))
							{
								data.gameData = sF;
							}
							if (sF.getName().endsWith("screen.png"))
							{
								data.pic = sF;
							}
							if (sF.getName().endsWith("desc.txt"))
							{
								try {
									FileReader fr = new FileReader(sF);
									BufferedReader br = new BufferedReader(fr);
									String name = br.readLine();
									data.descriptionName = name;
									name = br.readLine();
									data.secondLine = name;
									br.close();
								} catch (Exception ex)
								{
									
								}
							}
							if (data.gameData!=null && data.pic!=null && data.descriptionName!=null) break;
						}
					}
					if (data.gameData!=null && data.pic!=null)
					{
						dataList1.put(data.slotName,data);
					}
					System.out.println(file+" "+data.descriptionName);
				}
			}
			dataList= new TreeMap<String, SaveSlotData>();
			int reorderCount = dataList1.size();
			int count = 1;
			for (SaveSlotData d:dataList1.values())
			{
				String key = ""+reorderCount;
				while (key.length()<10)
				{
					key="0"+key;
				}
				dataList.put(key+" - "+d.slotName, d);
				d.id = key+" - "+d.slotName;
				if (d.descriptionName!=null) d.descriptionName = (count)+" - "+d.descriptionName;
				d.slotName = (count++)+" - "+d.slotName;
				reorderCount--;
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		if (dataList == null || dataList.size() == 0)
		{
			return false;
		}
		return true;
	}
	
	ArrayList<Runnable> fontFreers = new ArrayList<Runnable>();
	public void loadSlots() throws Exception
	{
		for (MenuImageButton b:buttons)
		{
			if (b.baseNode!=null)
				windowNode.detachChild(b.baseNode);
			windowNode.detachChild(b.baseNode);
		}
		buttons.clear();

		for (Runnable r:fontFreers)
		{
			r.run();
		}
		fontFreers.clear();
		
		int counter = 0;
		float sizeX = 0.35f* 1.2f * core.getDisplay().getWidth() / 5f;
		float sizeY = 0.87f* (core.getDisplay().getHeight() / 11);
		float startPosY = 1.265f*core.getDisplay().getHeight() / 2;
		float stepPosY = 0.879f* 1.1f*(core.getDisplay().getHeight() / 11);
		float posX = 0.75f*core.getDisplay().getWidth() / 2;
		if (dataList!=null)
		for (SaveSlotData data:dataList.values())
		{
			if (counter>=fromSlot && counter-fromSlot<maxSlots) {
				Quad button = null;
				try 
				{
					button = loadImageToQuad(data.pic,sizeX,sizeY, posX, startPosY - stepPosY*(counter - fromSlot));
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
				Node slottextNode = null;
				if (J3DCore.NATIVE_FONT_RENDER)
				{
					String first = data.descriptionName==null?data.slotName:data.descriptionName;
					String second = data.secondLine;
					if (second==null && first.length()>20)
					{
						second = first.substring(20);
						first = first.substring(0,20);
					}
					Text text = Text.createDefaultTextLabel("",first);
					text.setTextColor(new ColorRGBA(1,1,0.1f,1f));
					text.setLocalScale(0.65f);
					slottextNode = new Node("-");
					if (second!=null)
					{
						Text text2 = Text.createDefaultTextLabel("",second);
						text2.setTextColor(new ColorRGBA(1,1,0.1f,1f));
						text2.setLocalScale(0.60f);
						text2.setLocalTranslation(0, -core.getDisplay().getHeight()/40f, 0);
						slottextNode.attachChild(text2);
					}
					slottextNode.attachChild(text);
				} else
				{
					slottextNode = this.text.createOutlinedText(data.slotName, 9, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0,0,0.1f,1f),false);
					fontFreers.add(new NodeFontFreer(this.text,slottextNode));
				}
				

				slottextNode.setLocalTranslation(posX*1.15f, startPosY - stepPosY*(counter - fromSlot),0);
				slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				slottextNode.setLocalScale(core.getDisplay().getWidth()/650f);
				MenuImageButton b = new MenuImageButton(data.id,this,windowNode,counter-fromSlot);
				b.baseNode.attachChild(button);
				b.baseNode.attachChild(slottextNode);
				b.baseNode.setModelBound(new BoundingBox());
				buttons.add(b);
			}
			counter++;
		}
		windowNode.updateModelBound();
	}
	
	
	public void highlightSelected()
	{
		for (int i=0; i<buttons.size(); i++)
		{
			MenuImageButton b = buttons.get(i);
			if (b.baseNode.getChild(0)!=null)
			{
				if (i==selected)
				{
					((Quad)b.baseNode.getChild(0)).setSolidColor(ColorRGBA.white);
							
				} else
				{
					((Quad)b.baseNode.getChild(0)).setSolidColor(ColorRGBA.gray);
					
				}
			}
		}
		windowNode.updateRenderState();
		
	}

	@Override
	public void hide() {
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		lockLookAndMove(false);
	}

	@Override
	public void show() {
		updateFromDirectory();
		try {
			loadSlots();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		highlightSelected();
		core.getUIRootNode().attachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		lockLookAndMove(true);
	}
	
	public void handleChoice()
	{
		SaveSlotData data = dataList.get(buttons.get(selected).id);
		toggle();		
		core.updateDisplay(null);
		core.clearCore();
		base.hud.characters.hide();
		SaveLoadNewGame.loadGame(core,data.gameData);
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("---------------------- LOADING GAME ----------------------------");
		core.init3DGame();
		core.getClassicInputHandler().enableMouse(true);
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("---------------------- END LOADING GAME ------------------------");
		core.uiBase.hud.characters.update();
		core.uiBase.hud.characters.show();
		core.getUIRootNode().updateRenderState();
		core.gameState.engine.setPause(false);
		core.audioServer.stopAndResumeOthers("main");
	}

	public boolean handleKey(String key) {
		if (!visible) return false;
		if (key.equals("lookUp"))
		{
			selected--;
		} else
		if (key.equals("lookDown"))
		{
			selected++;
		}
		if (buttons.size()!=0)
		{
			if (selected+fromSlot>=dataList.size())
			{
				selected--;
			}
			if (selected>=buttons.size())
			{
				if (selected+fromSlot<dataList.size())
				{
					fromSlot = fromSlot+selected;
					selected = 0;
					try 
					{
						loadSlots();
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				} 
			}
		}
		if (selected<0) {
			if (fromSlot-maxSlots>=0)
			{
				fromSlot -= maxSlots;
				selected = maxSlots-1;
				try 
				{
					loadSlots();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			} else {
			selected = 0;
			}
		}
		highlightSelected();
		
		if (key.equals("enter"))
		{
			handleChoice();
		}
		if (key.equals("back"))
		{
			toggle();
			core.mainMenu.toggle();
		}
		
		return true;
		//return false;
	}


	@Override
	public boolean inputChanged(InputBase base, String message) {
		return false;
	}


	@Override
	public boolean inputEntered(InputBase base, String message) {
		int newSelected = Integer.parseInt(message);
		if (newSelected != selected)
		{
			selected = newSelected;
			highlightSelected();
			return true;
		}
		return false;
	}


	@Override
	public boolean inputLeft(InputBase base, String message) {
		return false;
	}


	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==closeWindow)
		{
			handleKey("back");
		}
		if (base==prevWindow)
		{
			selected = 0;
			handleKey("lookUp");
		}
		if (base==nextWindow)
		{
			selected = buttons.size()-1;
			handleKey("lookDown");
		}
		if (base instanceof MenuImageButton)
		{
			handleKey("enter");
		}
		return false;
	}


}
