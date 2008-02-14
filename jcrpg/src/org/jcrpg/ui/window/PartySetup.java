/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.window.element.CharListData;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.util.saveload.SaveLoadNewGame;
import org.jcrpg.world.ai.player.PartyMember;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class PartySetup extends InputWindow {

	public static final String charsDir = "./chars";
	
	FontTT text;
	
	Node pageMemberSelection = new Node();
	Node pageCreationFirst = new Node();
	Node pageCreationSecond = new Node();

	int currentPage = 0;
	
	ArrayList<PartyMember> members = new ArrayList<PartyMember>();
	ListSelect select = null;
	TextButton newChar;
	TextButton rmChar;
	TextButton startGame; 
	public PartySetup(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
		try {
	    	
			// page selection
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.png", 0.8f*core.getDisplay().getWidth(), 1.65f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
	    	
	    	pageMemberSelection.attachChild(hudQuad);
	    	
	    	select = new ListSelect(this,pageMemberSelection,0.2f,0.2f,0.1f,0.1f,new String[]{"id1","id2"},new String[]{"text to select1","text to select2"},null,null);
	    	addInput(select);
	    	
	    	newChar = new TextButton(this,pageMemberSelection, 0.23f, 0.5f, 0.2f, 0.07f,400f,"New Character");
	    	addInput(newChar);
	    	rmChar = new TextButton(this,pageMemberSelection, 0.50f, 0.5f, 0.2f, 0.07f,400f,"Remove Char.");
	    	addInput(rmChar);
	    	startGame = new TextButton(this,pageMemberSelection, 0.77f, 0.5f, 0.2f, 0.07f,400f,"Start Game");
	    	addInput(startGame);
	    	new TextLabel(this,pageMemberSelection, 0.23f, 0.7f, 0.2f, 0.07f,400f,"Use Up/Down to navigate through the screen.",false); 
	    	new TextLabel(this,pageMemberSelection, 0.23f, 0.75f, 0.2f, 0.07f,400f,"Press Enter to act.",false);	    	
			base.addEventHandler("back", this);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void hide() {
		core.getRootNode().detachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(false);
	}

	@Override
	public void show() {
		if (currentPage==0)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageMemberSelection);
			refreshCharacterList();
			select.ids = dataList.keySet().toArray(new String[0]);
			String[] names = new String[dataList.values().size()];
			int i=0;
			for (CharListData d:dataList.values())
			{
				names[i++] = d.charName;
			}
			select.texts = names;
			select.setUpdated(true);
		}
		activateSelectedInput();			
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(true);
	}
	
	static TreeMap<String, CharListData> dataList = null;
	
	public void refreshCharacterList()
	{
		try {
			File f = new File(charsDir);
			System.out.println("# FILE: "+f.getAbsolutePath());
			String[] files = f.list();
			TreeMap<String, CharListData> dataList1 = new TreeMap<String, CharListData>();
			if (files!=null)
			for (String file:files)
			{
				System.out.println("# FILE: "+file);
				if (new File(f.getAbsolutePath()+"/"+file).isDirectory())
				{
					CharListData data = new CharListData();
					data.charName = file;

					String[] subFiles = new File(f.getAbsolutePath()+"/"+file).list();
					for (String sFile:subFiles)
					{
						System.out.println("F: "+sFile);
						File sF = new File(charsDir+"/"+file+"/"+sFile);
						if (sF.isFile())
						{
							if (sF.getName().endsWith(".zip"))
							{
								data.charData = sF;
							}
							if (sF.getName().endsWith("portrait.png"))
							{
								data.pic = sF;
							}
							if (data.charData!=null && data.pic!=null) break;
						}
					}
					if (data.charData!=null && data.pic!=null)
					{
						dataList1.put(data.charName,data);
					}
				}
			}
			dataList= dataList1; /*new TreeMap<String, CharListData>();
			int reorderCount = dataList1.size();
			for (CharListData d:dataList1.values())
			{
				dataList.put(reorderCount+" - "+d.charName, d);
				d.id = reorderCount+" - "+d.charName;				
				reorderCount--;
			}*/
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		if (key.equals("enter")) {
		} else
		if (key.equals("back"))
		{
			if (core.coreFullyInitialized)
			{
				base.hud.characters.show();
			}
			toggle();
			core.mainMenu.toggle();
		}
		return true;
	}

	@Override
	public void inputUsed(InputBase base, String message) {
		if (base.equals(startGame))
		{
			toggle();
			this.base.hud.characters.show();
			core.clearCore();
			
			for (int i=0; i<6; i++)
			{
				members.add(new PartyMember("_"+i));
			}			
			SaveLoadNewGame.newGame(core,members);
			core.init3DGame();
			core.getRootNode().updateRenderState();
			core.gameState.engine.setPause(false);
			
		}
	}

}
