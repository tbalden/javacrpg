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
import java.util.HashMap;
import java.util.TreeMap;

import org.jcrpg.game.CharacterCreationRules;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.window.element.CharListData;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.ValueTuner;
import org.jcrpg.util.Language;
import org.jcrpg.util.saveload.SaveLoadNewGame;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyMember;
import org.jcrpg.world.ai.profession.Profession;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

public class PartySetup extends PagedInputWindow {

	public static final String charsDir = "./chars";
	
	FontTT text;
	
	Node pageMemberSelection = new Node();
	Node pageCreationFirst = new Node();
	Node pageCreationSecond = new Node();

	
	// party select
	ArrayList<PartyMember> members = new ArrayList<PartyMember>();
	ListSelect addCharSelect = null;
	TextButton newChar;
	TextButton rmChar;
	TextButton startGame;
	
	// creation 1
	ListSelect raceSelect = null;
	ListSelect professionSelect = null;
	HashMap<String, ValueTuner> attributes = new HashMap<String, ValueTuner>();
	TextButton nextPage;

	// creation 2
	TextButton readyChar;
	
	public PartySetup(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
		try {
	    	
			// page selection -----------------------------------------------
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.png", 0.8f*core.getDisplay().getWidth(), 1.65f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
	    	
	    	pageMemberSelection.attachChild(hudQuad);
	    	
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.25f, 0.2f, 0.07f,400f,"Select a character to add:",false); 
	    	addCharSelect = new ListSelect("add_char",this,pageMemberSelection,0.37f,0.3f,0.3f,0.06f,600f,new String[]{"id1","id2"},new String[]{"text to select1","text to select2"},null,null);
	    	addInput(0,addCharSelect);
	    	
	    	newChar = new TextButton("new_char",this,pageMemberSelection, 0.23f, 0.5f, 0.2f, 0.07f,400f,"New Character");
	    	addInput(0,newChar);
	    	rmChar = new TextButton("rm_char", this,pageMemberSelection, 0.50f, 0.5f, 0.2f, 0.07f,400f,"Remove Char.");
	    	addInput(0,rmChar);
	    	startGame = new TextButton("start",this,pageMemberSelection, 0.77f, 0.5f, 0.2f, 0.07f,400f,"Start Game");
	    	addInput(0,startGame);
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.7f, 0.2f, 0.07f,400f,"Use Up/Down to navigate through the screen.",false); 
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.75f, 0.2f, 0.07f,400f,"Press Enter to act.",false);
	    	
	    	// page char creation 1 -------------------------------------------
	    	SharedMesh sQuad = new SharedMesh("--",hudQuad);
	    	pageCreationFirst.attachChild(sQuad);

	    	{
		    	raceSelect = new ListSelect("race",this,pageCreationFirst, 0.37f,0.3f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,raceSelect);
	    	
	    	{
		    	professionSelect = new ListSelect("profession", this,pageCreationFirst, 0.63f,0.3f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,professionSelect);
	    	
	    	int posY = 0;
	    	for (String s: FantasyAttributes.attributeName)
	    	{
	    		String text = Language.v("fantasyattributes."+s);
	    		System.out.println("TEXT" +text);
	    		new TextLabel(s+"_label",this,pageCreationFirst,0.23f,0.5f+0.05f*posY,0.15f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,pageCreationFirst, 0.45f,0.5f+0.05f*posY,0.15f,0.04f,600f,10,0,100);
	    		addInput(1,v);
	    		posY++;
	    	}
	    	
	    	nextPage = new TextButton("next",this,pageCreationFirst, 0.77f, 0.5f, 0.2f, 0.07f,400f,"Next Page");
	    	addInput(1,nextPage);
	    	
	    	// page char creation 2 -------------------------------------------
	    	SharedMesh sQuad2 = new SharedMesh("--",hudQuad);
	    	pageCreationSecond.attachChild(sQuad2);
	    	readyChar = new TextButton("ready",this,pageCreationSecond, 0.77f, 0.5f, 0.2f, 0.07f,400f,"Ready");
	    	addInput(2,readyChar);
	    	
	    	
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
		currentPage = 0;
		setupPage();
		changePage(0);
		//activateSelectedInput();			
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(true);
	}
	
	CharacterCreationRules cCR = null;
	@Override
	public void setupPage()
	{
		if (currentPage==0)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageMemberSelection);
			refreshCharacterList();
			addCharSelect.ids = dataList.keySet().toArray(new String[0]);
			String[] names = new String[dataList.values().size()];
			int i=0;
			for (CharListData d:dataList.values())
			{
				names[i++] = d.charName;
			}
			addCharSelect.texts = names;
			
			// TODO just testing...
			addCharSelect.ids = new String[]{"1", "2"};
			addCharSelect.texts = new String[]{"Urmuc - Dwarf, Fighter", "Athos - Human, Mage"};			
			
			addCharSelect.setUpdated(true);
		}
		if (currentPage==1)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageCreationFirst);
			if (core.gameState==null || core.gameState.charCreationRules == null)
			{
				cCR = new CharacterCreationRules(null,null);
			} else
			{
				cCR = core.gameState.charCreationRules;
			}
	    	{
		    	int id = 0;
		    	String[] ids = new String[cCR.selectableRaces.size()];
		    	String[] names = new String[cCR.selectableRaces.size()];
		    	for (Class<? extends MemberPerson> c: cCR.selectableRaces)
		    	{
		    		String s = c.getSimpleName();
		    		ids[id] = ""+id;
		    		names[id] = s;
		    		id++;
		    	}	    	
		    	raceSelect.ids = ids;
		    	raceSelect.texts = names;
		    	raceSelect.setUpdated(true);
	    	}
	    	
	    	{
		    	int id = 0;
		    	String[] ids = new String[cCR.selectableProfessions.size()];
		    	String[] names = new String[cCR.selectableProfessions.size()];
		    	for (Class<? extends Profession> c: cCR.selectableProfessions)
		    	{
		    		String s = c.getSimpleName();
		    		ids[id] = ""+id;
		    		names[id] = s;
		    		id++;
		    	}	    	
		    	professionSelect.ids = ids;
		    	professionSelect.texts = names;
		    	professionSelect.setUpdated(true);
	    	}
		}
		if (currentPage==2)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageCreationSecond);
			
		}
		super.setupPage();
		
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
	public boolean inputUsed(InputBase base, String message) {
		if (base.equals(newChar))
		{
			base.deactivate();
			currentPage=1;
			setupPage();
		}
		else
		if (base.equals(nextPage))
		{
			base.deactivate();
			currentPage=2;
			setupPage();
		}
		if (base.equals(readyChar))
		{
			base.deactivate();
			currentPage=0;
			setupPage();
		}
		else
		if (base.equals(startGame))
		{
			base.deactivate();
			toggle();
			this.base.hud.characters.show();
			core.clearCore();
			
			for (int i=0; i<6; i++)
			{
				members.add(new PartyMember("_"+i,new AudioDescription()));
			}			
			if (cCR == null)
			{
				cCR = new CharacterCreationRules(null,null);
			}
			SaveLoadNewGame.newGame(core,members,cCR);
			core.init3DGame();
			core.getRootNode().updateRenderState();
			core.gameState.engine.setPause(false);
			core.audioServer.stopAndResumeOthers("main");
		}
		return true;
	}

}
