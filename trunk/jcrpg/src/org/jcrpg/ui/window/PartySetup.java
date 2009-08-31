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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.CharacterCreationRules;
import org.jcrpg.game.GameLogicConstants;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.window.element.CharListData;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.PictureSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.TextInputField;
import org.jcrpg.ui.window.element.input.ValueTuner;
import org.jcrpg.util.Language;
import org.jcrpg.util.saveload.SaveLoadNewGame;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.audio.desc.VoiceList;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.Party;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.ai.profession.Profession;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

public class PartySetup extends PagedInputWindow {

	
	FontTT text;
	
	Node pageMemberSelection = new Node("pageMemberSelection");
	Node pageCreationFirst = new Node("pageCreationFirst");
	Node pageCreationSecond = new Node("pageCreationSecond");

	
	ArrayList<PersistentMemberInstance> charactersOfParty = new ArrayList<PersistentMemberInstance>();
	
	// party select
	//ArrayList<PartyMember> members = new ArrayList<PartyMember>();
	ListSelect addCharSelect = null;
	TextButton addChar;
	TextButton viewChar;
	TextButton newChar;
	TextButton delChar;
	TextButton rmChar;
	TextButton startGame;
	
	// creation 1
	ListSelect raceSelect = null;
	ListSelect genderSelect = null;
	TextLabel attrPointsLeftLabel = null;
	HashMap<String, ValueTuner> attributeTuners = new HashMap<String, ValueTuner>();
	ListSelect professionSelect = null;
	PictureSelect pictureSelect = null;
	TextButton nextPage;

	// creation 2
	TextLabel charInfo;
	HashMap<String, ListSelect> skillSelects = new HashMap<String, ListSelect>();
	TextLabel skillText;
	ValueTuner skillValueTuner;
	TextLabel skillPointsLeftLabel = null;
	TextButton readyChar;
	TextButton resetSkills;
	TextInputField surName;
	TextInputField foreName;
	ListSelect voiceSelect = null;
	// which skillgroup was used to enter modification ValueTuner
	ListSelect skillGroupLeftLast = null;
	Class<? extends SkillBase> skillTuned = null; 

	SkillContainer backupSkillContainer = null;
	
	/**
	 * How many attribute points are left.
	 */
	int attrPointsLeft = 0;
	
	/**
	 * How many attribute points are left.
	 */
	int skillPointsLeft = 0;
	int backupSkillPointsLeft = 0;
	
	// character creation result classes
	public MemberPerson personWithGenderAndRace = null;
	public Profession profession = null;
	public org.jcrpg.world.ai.abs.attribute.Attributes attributeValues = null;
	public org.jcrpg.world.ai.abs.attribute.Attributes lowestAttrValues = new FantasyAttributes(false);
	public org.jcrpg.world.ai.abs.attribute.Resistances resistanceValues = null;
	
	public TextButton closeWindow1, closeWindow2, closeWindow3;
	
	public PartySetup(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
		try {
	    	
			// page selection -----------------------------------------------
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.dds", 0.8f*core.getDisplay().getWidth(), 1.61f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.13f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	pageMemberSelection.attachChild(hudQuad);
	    	
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.10f, 0.2f, 0.07f,600f,"Select a character to add:",false); 
	    	addCharSelect = new ListSelect("add_char",this,pageMemberSelection,0.385f,0.15f,0.5f,0.05f,600f,new String[]{"id1","id2"},new String[]{"text to select1","text to select2"},null,null);
	    	addCharSelect.focusUponMouseEnter = true;
	    	addCharSelect.deactivateUponUse = false;
	    	addInput(0,addCharSelect);
	    	
	    	viewChar = new TextButton("view_char",this,pageMemberSelection, 0.23f, 0.42f, 0.21f, 0.07f,430f,Language.v("partySetup.viewChar"));
	    	addInput(0,viewChar);
	    	addChar = new TextButton("add_char",this,pageMemberSelection, 0.50f, 0.42f, 0.21f, 0.07f,430f,Language.v("partySetup.addChar"));
	    	addInput(0,addChar);
	    	newChar = new TextButton("new_char",this,pageMemberSelection, 0.23f, 0.5f, 0.21f, 0.07f,430f,Language.v("partySetup.newChar"));
	    	addInput(0,newChar);
	    	rmChar = new TextButton("rm_char", this,pageMemberSelection, 0.50f, 0.5f, 0.21f, 0.07f,430f,Language.v("partySetup.rmChar"));
	    	addInput(0,rmChar);
	    	startGame = new TextButton("start",this,pageMemberSelection, 0.77f, 0.5f, 0.21f, 0.07f,430f,Language.v("partySetup.startGame"));
	    	addInput(0,startGame);
	    	delChar = new TextButton("del_char",this,pageMemberSelection, 0.50f, 0.62f, 0.21f, 0.07f,430f,Language.v("partySetup.delChar"));
	    	addInput(0,delChar);
	    	
	    	closeWindow1 = new TextButton("close",this,pageMemberSelection, 0.85f, 0.060f, 0.025f, 0.045f,600f," <-");
	    	addInput(0,closeWindow1);

	    	new TextLabel("label1",this,pageMemberSelection, 0.23f, 0.7f, 0.2f, 0.07f,500f,"Use Up/Down to navigate through the screen.",false); 
	    	new TextLabel("label2",this,pageMemberSelection, 0.23f, 0.75f, 0.2f, 0.07f,500f,"Press Left/Right to scroll in lists, Enter to act.",false);
	    	new TextLabel("label3",this,pageMemberSelection, 0.23f, 0.80f, 0.2f, 0.07f,500f,"Create / Add at least one character to your party.",false);
	    	
	    	// page char creation 1 -------------------------------------------
	    	SharedMesh sQuad = new SharedMesh("--",hudQuad);
	    	sQuad.setLocalTranslation(hudQuad.getLocalTranslation());
	    	pageCreationFirst.attachChild(sQuad);
	    	new TextLabel("",this,pageCreationFirst, 0.22f, 0.75f, 0.2f, 0.07f,600f,"Tune the attributes to attain a profession. Press Backspace to go back.",false);
	    	new TextLabel("",this,pageCreationFirst, 0.22f, 0.80f, 0.2f, 0.07f,600f,"Up/Down to move on inputs. Left/Right to change.",false);

	    	new TextLabel("",this,pageCreationFirst, 0.37f, 0.08f, 0.0f, 0.06f,400f,"Character Creation",false); 

	    	new TextLabel("",this,pageCreationFirst, 0.30f, 0.15f, 0.3f, 0.06f,600f,"Race:",false); 
	    	{
		    	raceSelect = new ListSelect("race",this,pageCreationFirst, 0.30f,0.2f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,raceSelect);

	    	new TextLabel("",this,pageCreationFirst, 0.7f, 0.15f, 0.3f, 0.06f,600f,"Gender:",false); 
	    	{
		    	genderSelect = new ListSelect("gender", this,pageCreationFirst, 0.7f,0.2f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,genderSelect);
	    	
	    	float column2 = 0.5f, column1 = 0.33f;
	    	
	    	new TextLabel("",this,pageCreationFirst, column1, 0.5f, 0.3f, 0.06f,600f,"Profession:",false); 
	    	{
		    	professionSelect = new ListSelect("profession", this,pageCreationFirst, column1,0.55f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,professionSelect);
	    	
	    	
	    	attrPointsLeftLabel = new TextLabel("",this,pageCreationFirst, column2, 0.7f, 0.2f, 0.07f,500f,attrPointsLeft+" points left.",false); 
	    	
	    	int posY = 0;
	    	for (String s: FantasyAttributes.attributeName)
	    	{
	    		String text = Language.v("fantasyattributes."+s);
	    		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("TEXT" +text);
	    		new TextLabel(s+"_label",this,pageCreationFirst,column2,0.3f+0.05f*posY,0.0f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,pageCreationFirst, column2+0.22f,0.3f+0.05f*posY,0.15f,0.04f,600f,10,0,100,1);
	    		attributeTuners.put(s, v);
	    		addInput(1,v);
	    		posY++;
	    	}

	    	pictureSelect = new PictureSelect("picture_select", this, pageCreationFirst, column1,0.37f,0.15f,0.2f,600f);
	    	addInput(1,pictureSelect);


	    	new TextLabel("",this,pageCreationFirst, column1, 0.6f, 0.3f, 0.06f,600f,Language.v("partySetup.voiceType")+":",false); 
	    	{
		    	voiceSelect = new ListSelect("voice", this,pageCreationFirst, column1,0.65f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,voiceSelect);

	    	
	    	nextPage = new TextButton("next",this,pageCreationFirst, 0.77f, 0.8f, 0.2f, 0.07f,400f,"Next Page");
	    	addInput(1,nextPage);
	    	closeWindow2 = new TextButton("close",this,pageCreationFirst, 0.85f, 0.060f, 0.025f, 0.045f,600f," <-");
	    	addInput(0,closeWindow2);
	    	
	    	// page char creation 2 -------------------------------------------
	    	SharedMesh sQuad2 = new SharedMesh("--",hudQuad);
	    	sQuad2.setLocalTranslation(hudQuad.getLocalTranslation());
	    	pageCreationSecond.attachChild(sQuad2);

	    	new TextLabel("",this,pageCreationSecond, 0.37f, 0.08f, 0.0f, 0.06f,500f,"Character Creation",false); 
	    	charInfo = new TextLabel("",this,pageCreationSecond, 0.37f, 0.16f, 0.3f, 0.06f,400f,"",false); 
	    	new TextLabel("",this,pageCreationSecond, 0.23f, 0.73f, 0.2f, 0.07f,650f,"Select a skill from the groups, use the input on the",false);
	    	new TextLabel("",this,pageCreationSecond, 0.23f, 0.77f, 0.2f, 0.07f,650f,"left to increase and set it.(Move Mouse cursor over it so it changes",false);
	    	new TextLabel("",this,pageCreationSecond, 0.23f, 0.81f, 0.2f, 0.07f,650f,"to SET, or on keyboard press ENTER.) Leave no points unused!",false);

	    	posY = 0; 
	    	for (String groupId : SkillGroups.orderedGroups)
	    	{
	    		String groupName = Language.v("skillgroups."+groupId);
	    		new TextLabel(groupId+"_label",this,pageCreationSecond,0.21f,0.2f+0.05f*posY,0.15f,0.04f,600f, groupName, false);
	    		ArrayList<String> skillIds = new ArrayList<String>();
	    		ArrayList<String> skillTexts = new ArrayList<String>();
	    		ArrayList<Object> skillObjects = new ArrayList<Object>();
	    		//int counter = 0;
	    		/*for (Class<? extends SkillBase> skill:SkillGroups.groupedSkills.get(groupId))
	    		{
	    			String id = groupId+"."+counter;
	    			String text = skill.getSimpleName();
	    			text = Language.v("skills."+text);
	    			skillIds.add(id);
	    			skillTexts.add(text);
	    			skillObjects.add(skill);
	    			counter++;
	    		}*/
	    		ListSelect sel = new ListSelect("skillgroup", this,pageCreationSecond, 0.38f,0.2f+0.05f*posY,0.3f,0.04f,600f,skillIds.toArray(new String[0]),skillTexts.toArray(new String[0]),skillObjects.toArray(new Object[0]),null,null);
	    		sel.focusUponMouseEnter = true;
	    		sel.deactivateUponUse = true;
		    	posY++;
	    		skillSelects.put(groupId, sel);
	    		addInput(2,sel);
	    	}
	    	skillPointsLeftLabel = new TextLabel("",this,pageCreationSecond, 0.22f, 0.68f, 0.2f, 0.07f,500f,skillPointsLeft+" points left.",false); 
	    	
	    	skillText = new TextLabel("",this,pageCreationSecond, 0.72f, 0.2f, 0.3f, 0.06f,600f,Language.v("partySetup.selectSkill"),false); 
	    	skillValueTuner = new ValueTuner("skill_tuner",this,pageCreationSecond, 0.68f,0.25f,0.15f,0.04f,600f,0,0,100,1);
	    	addInput(2,skillValueTuner);
	    	skillValueTuner.setEnabled(false);
	    	
	    	new TextLabel("",this,pageCreationSecond, 0.3f, 0.57f, 0.3f, 0.06f,600f,Language.v("partySetup.foreName")+":",false); 
	    	foreName = new TextInputField("foreName",this,pageCreationSecond, 0.3f, 0.62f, 0.3f, 0.06f,600f,"",15,false);
	    	addInput(2,foreName);
	    	new TextLabel("",this,pageCreationSecond, 0.66f, 0.57f, 0.3f, 0.06f,600f,Language.v("partySetup.surName")+":",false); 
	    	surName = new TextInputField("surName",this,pageCreationSecond, 0.66f, 0.62f, 0.3f, 0.06f,600f,"",15,false); 
	    	addInput(2,surName);

	    	resetSkills = new TextButton("reset",this,pageCreationSecond, 0.73f, 0.45f, 0.15f, 0.05f,600f,Language.v("partySetup.reset"));
	    	addInput(2,resetSkills);
	    	
	    	readyChar = new TextButton("ready",this,pageCreationSecond, 0.77f, 0.7f, 0.2f, 0.07f,400f,Language.v("partySetup.ready"));
	    	addInput(2,readyChar);

	    	closeWindow3 = new TextButton("close",this,pageCreationSecond, 0.85f, 0.060f, 0.025f, 0.045f,600f," <-");
	    	addInput(0,closeWindow3);

			base.addEventHandler("back", this);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void resetForms()
	{
		skillText.text = Language.v("partySetup.selectSkill");
		skillText.activate();
		foreName.reset();
		surName.reset();
		foreName.deactivate();
		surName.deactivate();
		
	}

	@Override
	public void hide() {
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		lockLookAndMove(false);
	}

	@Override
	public void show() {
		if (!noNeedForRefreshPage)
		{
			currentPage = 0;
			charactersOfParty.clear();
			core.uiBase.hud.characters.updateForPartyCreation(charactersOfParty);
			core.uiBase.hud.characters.show();
			
			setupPage();
			changePage(0);
			core.getUIRootNode().attachChild(windowNode);
			core.getUIRootNode().updateRenderState();
		} else
		{
			setupPage();
			core.getUIRootNode().attachChild(windowNode);
			core.getUIRootNode().updateRenderState();
			noNeedForRefreshPage = false;
		}
		lockLookAndMove(true);
	}
	
	CharacterCreationRules charCreationRule = null;
	@Override
	public void setupPage()
	{
		if (currentPage==0 && !noNeedForRefreshPage)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageMemberSelection);
			refreshCharacterList();
			addCharSelect.ids = dataList.keySet().toArray(new String[0]);
			String[] names = new String[dataList.values().size()];
			Object[] objects = new Object[dataList.values().size()];
			int i=0;
			for (CharListData d:dataList.values())
			{
				names[i] = d.charName;
				objects[i++] = d;
			}
			addCharSelect.texts = names;
			addCharSelect.objects = objects;
			addCharSelect.selected = 0;
			if (addCharSelect.texts.length>0)
			{
				inputChanged(addCharSelect, "");
			}
			addCharSelect.setUpdated(true);
		}
		if (currentPage==1)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageCreationFirst);
			if (core.gameState==null || core.gameState.charCreationRules == null)
			{
				charCreationRule = new CharacterCreationRules(null,null);
			} else
			{
				charCreationRule = core.gameState.charCreationRules;
			}
	    	{
		    	int id = 0;
		    	String[] ids = new String[charCreationRule.selectableRaces.size()];
		    	String[] names = new String[charCreationRule.selectableRaces.size()];
		    	for (Class<? extends EntityMember> c: charCreationRule.selectableRaces)
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
	    	raceSelect.activate();
	    	/*
	    	{
		    	int id = 0;
		    	String[] ids = new String[charCreationRule.selectableProfessions.size()];
		    	String[] names = new String[charCreationRule.selectableProfessions.size()];
		    	for (Class<? extends Profession> c: charCreationRule.selectableProfessions)
		    	{
		    		String s = c.getSimpleName();
		    		ids[id] = ""+id;
		    		names[id] = s;
		    		id++;
		    	}	    	
		    	professionSelect.ids = ids;
		    	professionSelect.texts = names;
		    	professionSelect.setUpdated(true);
	    	}*/
		}
		if (currentPage==2)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageCreationSecond);
			
		}
		noNeedForRefreshPage = false;
		super.setupPage();
		
	}
	
	static TreeMap<String, CharListData> dataList = null;
	
	HashMap<String, MemberPerson> mpCache = new HashMap<String, MemberPerson>();
	
	public void refreshCharacterList()
	{
		try {
			File f = new File(SaveLoadNewGame.charsDir);
			Jcrpg.LOGGER.finest("# FILE: "+f.getAbsolutePath());
			String[] files = f.list();
			TreeMap<String, CharListData> dataList1 = new TreeMap<String, CharListData>();
			if (files!=null)
			for (String file:files)
			{
				//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("# FILE: "+file);
				if (new File(f.getAbsolutePath()+"/"+file).isDirectory())
				{
					CharListData data = new CharListData();
					data.charName = file;
					File dirFile = new File(f.getAbsolutePath()+"/"+file);
					String[] subFiles = dirFile.list();
					for (String sFile:subFiles)
					{
						//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("F: "+sFile);
						File sF = new File(SaveLoadNewGame.charsDir+"/"+file+"/"+sFile);
						if (sF.isFile())
						{
							if (sF.getName().endsWith(".zip"))
							{
								data.dir = dirFile;
								data.charData = sF;
								try 
								{
									MemberPerson p = mpCache.get(sF.getAbsolutePath());
									if (p == null) {
										p = SaveLoadNewGame.loadCharacter(sF);
										mpCache.put(sF.getAbsolutePath(), p);
									}
									data.charName = p.getClass().getSimpleName()+" "+p.professions.get(0).getSimpleName()+" - "+p.foreName+" "+p.surName;
									while (true) {
										if (dataList1.get(data.charName)!=null)
										{
											data.charName += "_";
										} else
										{
											break;
										}
									}
									data.person = p;
									data.pic = new File(p.getPicturePath());
								} catch (Exception ex)
								{
									ex.printStackTrace();
									break;
								}
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
			dataList= dataList1;
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		if (key.equals("enter")) {
		} else
		if (key.equals("back"))
		{
			if (core.coreFullyInitialized)
			{
				core.uiBase.hud.characters.update();
				core.uiBase.hud.characters.show();
			} else
			{
				core.uiBase.hud.characters.hide();
			}
			if (currentPage>0) 
			{
				currentPage=0;
				resetForms();
				setupPage();
				changePage(0);
				core.uiBase.hud.characters.show();
			} else {
				toggle();
				core.mainMenu.toggle();
			}
		}
		return true;
	}
	
	public boolean noNeedForRefreshPage = false;

	@SuppressWarnings("unchecked")
	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base == closeWindow1 || base == closeWindow2 || base == closeWindow3)
		{
			handleKey("back");
			return true;
		}
		if (base.equals(resetSkills))
		{
			skillPointsLeft = GameLogicConstants.SKILL_POINTS_TO_USE;
			backupSkillPointsLeft = skillPointsLeft;
			skillPointsLeftLabel.text = skillPointsLeft + " points left.";
			skillPointsLeftLabel.activate();
			personWithGenderAndRace.setMemberSkills(backupSkillContainer.copy());
			setOriginalSkills();
			skillValueTuner.setEnabled(false);
		}
		else
		if (base.equals(professionSelect))
		{
			Profession p = (Profession)professionSelect.getSelectedObject();
			//p.attrMinLevels;
			MemberPerson race = charCreationRule.raceInstances.get(charCreationRule.selectableRaces.get(raceSelect.getSelection()));
			int i = genderSelect.getSelection();
			int genderId = Integer.parseInt(genderSelect.ids[i]);

			// setting all to race minimum
			int baseValue = GameLogicConstants.BASE_ATTRIBUTE_VALUE;
			for (String id: FantasyAttributes.attributeName) {
				if (race.commonAttributeRatios.attributeRatios.get(id)!=null)
				{
					{
						// race minimum set..
						attributeValues.setAttribute(id, (int)(baseValue*race.commonAttributeRatios.attributeRatios.get(id)));
						lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
					}
				} else
				{
					{
						attributeValues.setAttribute(id, baseValue);
						lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
					}
				}
			}

			int remaining = p.getRemainingPointsAfterLevelingToMinimumAttributes(race, genderId, attributeValues, GameLogicConstants.ATTRIBUTE_POINTS_TO_USE);

			if (remaining<0) return false;
			
			// updating label
			attrPointsLeft = remaining;
			attrPointsLeftLabel.text = attrPointsLeft + " points left.";
			attrPointsLeftLabel.activate();
			
			for (String id: FantasyAttributes.attributeName) {
				if (race.commonAttributeRatios.attributeRatios.get(id)!=null)
				{
					int raceMinimum = (int)(baseValue*race.commonAttributeRatios.attributeRatios.get(id));
					if (p.attrMinLevels.minimumLevels.get(id)!=null && p.attrMinLevels.minimumLevels.get(id).intValue()>raceMinimum)
					{
						// profession min is higher than race minimum, use that...
						attributeValues.setAttribute(id, p.attrMinLevels.minimumLevels.get(id).intValue());
						lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
					} else
					{
						// race minimum set..
						attributeValues.setAttribute(id, (int)(baseValue*race.commonAttributeRatios.attributeRatios.get(id)));
						lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
					}
				} else
				{
					int raceMinimum = baseValue;
					if (p.attrMinLevels.minimumLevels.get(id)!=null && p.attrMinLevels.minimumLevels.get(id).intValue()>raceMinimum)
					{
						// profession min is higher than race minimum, use that...
						attributeValues.setAttribute(id, p.attrMinLevels.minimumLevels.get(id).intValue());
						lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
					} else
					{
						attributeValues.setAttribute(id, baseValue);
						lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
					}
				}
				Jcrpg.LOGGER.finer("ID = "+id+" = "+attributeValues.attributes.get(id));
				ValueTuner v = attributeTuners.get(id);
				v.value = attributeValues.attributes.get(id);
				v.text = ""+v.value;
				v.minValueVisible = true;
				v.minValue = lowestAttrValues.attributes.get(id);
				v.setUpdated(true);
				v.deactivate();
			}

			
		} else
		if (base.equals(viewChar))
		{
			CharListData d = (CharListData)addCharSelect.getSelectedObject();
			PartyInstance pi = new PartyInstance(new Party(),null,null,-1,"_",6,1,1,1);
			pi.orderedParty.add(new PersistentMemberInstance(pi.theFragment, pi,d.person,null,Ecology.getNextEntityId(),0,0,0));
			core.charSheetWindow.setPageData(pi);
			core.charSheetWindow.fallbackWindow = this;
			core.charSheetWindow.noToggleWindowByKeySettingAfterFallbackWindowUse = false;
			noNeedForRefreshPage = true;
			toggle();
			core.getKeyboardHandler().noToggleWindowByKey=false;
			core.charSheetWindow.toggle();
			return true;
			
		}
		else
		if (base.equals(delChar))
		{
			CharListData d = (CharListData)addCharSelect.getSelectedObject();
			try {
				d.charData.delete();
				d.dir.delete();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			setupPage();
		} else
		if (base.equals(rmChar))
		{
			// ############### REMOVING Selected Char
			if (charactersOfParty.size()==0) return true;
			int s = addCharSelect.getSelection();
			Iterator<CharListData> it = dataList.values().iterator();
			CharListData d = null;
			for (int i=0; i<=s; i++) {
				d = it.next();
			}
			int count = 0;
			for (EntityMemberInstance i:charactersOfParty)
			{
				if (i.description.equals(d.person)) 
					{
						charactersOfParty.remove(count);
						core.uiBase.hud.characters.updateForPartyCreation(charactersOfParty);
						core.uiBase.hud.characters.show();
						break;
					}
				count++;
			}
		} else
		if (base.equals(addCharSelect) || base.equals(addChar))
		{
			// ############# ADDING Char
			if (charactersOfParty.size()==6) return true;
			if (dataList.size()==0) return true;
			int s = addCharSelect.getSelection();
			Iterator<CharListData> it = dataList.values().iterator();
			CharListData d = null;
			for (int i=0; i<=s; i++) {
				d = it.next();
			}
			for (EntityMemberInstance i:charactersOfParty)
			{
				if (i.description.equals(d.person)) return true; // no duplication
			}
			charactersOfParty.add(new PersistentMemberInstance(null, null,d.person,null,Ecology.getNextEntityId(),0,0,0));
			core.uiBase.hud.characters.updateForPartyCreation(charactersOfParty);
			core.uiBase.hud.characters.showNoPointUpdate();
			
		} else
		if (base.equals(skillValueTuner))
		{
			if (message.equals("enter")) {
				// ############## SKILL VALUE SET, feed it back into listSelect
				Class<? extends SkillBase> skill = (Class<? extends SkillBase>)skillValueTuner.tunedObject;
				personWithGenderAndRace.getMemberSkills().setSkillValue(skill, skillValueTuner.value);
				skillGroupLeftLast.setUpdated(true);
				int id = 0;
				for (Object o:skillGroupLeftLast.objects)
				{
					if (o.equals(skillValueTuner.tunedObject))
					{
						// this is the id that's modified:
						int modifier = profession.skillLearnModifier.getMultiplier(skill);
						skillGroupLeftLast.texts[id] = Language.v("skills."+((Class)o).getSimpleName())+" ("+modifier+"x): "+skillValueTuner.value;
					}
					id++;
				}
				setSelected(skillGroupLeftLast);
				backupSkillPointsLeft = skillPointsLeft;
			} else 
			{
				// ################ Tuning the skill level value, modifying pointsLeft textlabel
				if (message.equals("lookLeft"))
				{
					skillPointsLeft++;
				} else
				if (message.equals("lookRight"))
				{
					skillPointsLeft--;
				}
				if (skillPointsLeft<0)
				{
					skillPointsLeft = 0;
					return false;
				} else
				{
					skillPointsLeftLabel.text = skillPointsLeft + " points left.";
					skillPointsLeftLabel.activate();
					return true;
				}
			}
		} else
		if (skillSelects.values().contains(base))
		{
			//#################### MODIFYING A SKILL with skillValueTuner... 
			ListSelect select = (ListSelect)base;
			if (select.ids.length==0) return true;
			skillGroupLeftLast = select;
			//String id = select.ids[select.getSelection()];
			Class<?extends SkillBase> skill = (Class<? extends SkillBase>)select.getSelectedObject();
			skillTuned = skill;
			//String group = id.substring(0,id.indexOf('.'));
			//int count = Integer.parseInt(id.substring(id.indexOf('.')+1));
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("GROUP = "+group+ " - "+count);
			skillValueTuner.setEnabled(true);
			skillValueTuner.value = personWithGenderAndRace.getMemberSkills().getSkillLevel(skill, null);
			skillValueTuner.minValue = skillValueTuner.value;
			skillValueTuner.setUpdated(true);
			skillValueTuner.tunedObject = skill;
			int modifier = 1;
			try {
				modifier = profession.skillLearnModifier.getMultiplier(skill);
			} catch (Exception ex)
			{	
			}
			skillValueTuner.setStep(modifier);
			skillText.text = Language.v("skills."+skill.getSimpleName())+" ("+modifier+"x):";
			skillText.activate();
			setSelected(skillValueTuner);
		} else		
		if (base instanceof ValueTuner) {
			int count = 0;
			for (ValueTuner v:attributeTuners.values())
			{
				if (base.equals(v))
				{
					int val = lowestAttrValues.getAttribute(v.id); // cannot go under original attributes for race
					if (v.value<val) return false; // value tuner will receive False and won't accept the modification...
					if (message.equals("lookLeft"))
					{
						attrPointsLeft++;
					} else
					if (message.equals("lookRight"))
					{
						attrPointsLeft--;
					}
					if (attrPointsLeft<0)
					{
						attrPointsLeft = 0;
						return false; // value tuner will receive False and won't accept the modification...
					} else
					{
						attrPointsLeftLabel.text = attrPointsLeft + " points left.";
						attrPointsLeftLabel.activate();
						//inputEntered(professionSelect, "fake");
						return true;
					}
						
				}
				count++;
			}
		}
		else
		if (base.equals(newChar))
		{
			// ######### STARTING A NEW CHARACTER
			base.deactivate();
			resetForms();
			attrPointsLeft = GameLogicConstants.ATTRIBUTE_POINTS_TO_USE;
			skillPointsLeft = GameLogicConstants.SKILL_POINTS_TO_USE;
			backupSkillPointsLeft = skillPointsLeft;
			attrPointsLeftLabel.text = attrPointsLeft + " points left.";
			attrPointsLeftLabel.activate();
			skillPointsLeftLabel.text = skillPointsLeft + " points left.";
			skillPointsLeftLabel.activate();
			skillValueTuner.setEnabled(false);
			skillValueTuner.value = 0;
			currentPage=1;
			setupPage();
		}
		else
		if (base.equals(nextPage))
		{
			if (attrPointsLeft>0) return false;
			// ######### MOVING TO SKILL PAGE, race/profession/attributes are done
			personWithGenderAndRace = charCreationRule.raceInstances.get(charCreationRule.selectableRaces.get(raceSelect.getSelection())).copy(null);
			if (genderSelect.ids.length<1) return true;
			{
				int i = genderSelect.getSelection();
				int id = Integer.parseInt(genderSelect.ids[i]);
				personWithGenderAndRace.genderType = id;
			}
			personWithGenderAndRace.setAudioDesc((AudioDescription)voiceSelect.getSelectedObject());
			
			if (professionSelect.texts.length==0) return true;
			profession = charCreationRule.profInstances.get(charCreationRule.selectableProfessions.get(Integer.parseInt(professionSelect.ids[professionSelect.getSelection()])));
			if (true==false && (profession==null || attrPointsLeft>0)) return true;
			if (profession==null) return true;
			personWithGenderAndRace.addProfessionInitially(profession);

			attributeValues = new FantasyAttributes(false);
			
			for (String id:attributeTuners.keySet())
			{
				ValueTuner v = attributeTuners.get(id);
				int value = v.getSelection();
				attributeValues.setAttribute(id, value);
				Jcrpg.LOGGER.finer("CHARACTER ATTRIBUTES _ "+id + " = "+value);
			}
			Jcrpg.LOGGER.finer("CHARACTER PERSON & PROFESSION : "+personWithGenderAndRace+" "+profession);
			charInfo.text = Language.v("races."+personWithGenderAndRace.getClass().getSimpleName()) + " " + Language.v("professions."+profession.getClass().getSimpleName());
			charInfo.activate();
			
			backupSkillContainer = personWithGenderAndRace.getMemberSkills().copy();

			setOriginalSkills();

			base.deactivate();
			currentPage=2;
			setupPage();
		}
		if (base.equals(readyChar))
		{
			inputLeft(skillValueTuner, "fake"); // this makes sure points are the backed up one after tweaking but no saving!
			
			// ################## CHARACTER COMPLETE, saving it
			if (foreName.text.length()==0) return true; // a name must be entered
			if (skillPointsLeft>0) return true; // all skill points must be used
			//personWithGenderAndRace.professions.add(profession.getClass());
			personWithGenderAndRace.setAttributes(attributeValues);
			personWithGenderAndRace.setResistances(resistanceValues);
			personWithGenderAndRace.setForeName(foreName.text);
			personWithGenderAndRace.setSurName(surName.text);
			personWithGenderAndRace.setPictureId(pictureSelect.getPictureId());
			int i = genderSelect.getSelection();
			int id = Integer.parseInt(genderSelect.ids[i]);
			personWithGenderAndRace.genderType = id;
			SaveLoadNewGame.saveCharacter(personWithGenderAndRace);
			base.deactivate();
			currentPage=0;
			setupPage();
		}
		else
		if (base.equals(startGame))
		{
			if (charactersOfParty.size()==0) return true;
			// ################ Let's start the game...
			base.deactivate();
			toggle();
			core.clearCore();
			
			if (charCreationRule == null)
			{
				charCreationRule = new CharacterCreationRules(null,null);
			}
			core.uiBase.hud.characters.hide();
			SaveLoadNewGame.newGame(core,charactersOfParty,charCreationRule);
			core.init3DGame();
			core.getClassicInputHandler().enableMouse(true);
			core.uiBase.hud.characters.update();
			core.uiBase.hud.characters.show();
			core.getUIRootNode().updateRenderState();
			core.gameState.engine.setPause(false);
			core.audioServer.stopAndResumeOthers("main");
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean inputLeft(InputBase base, String message) {
		if (base.equals(raceSelect))
		{
			Jcrpg.LOGGER.finer("RACE SELECT LEFT");
			MemberPerson race = charCreationRule.raceInstances.get(charCreationRule.selectableRaces.get(raceSelect.getSelection()));
			if (race.possibleGenders==EntityDescription.GENDER_BOTH)
			{
				genderSelect.ids = new String[]{""+EntityDescription.GENDER_MALE, ""+EntityDescription.GENDER_FEMALE};
				genderSelect.texts = new String[]{Language.v("gender."+EntityDescription.GENDER_MALE), Language.v("gender."+EntityDescription.GENDER_FEMALE)};
			}
			if (race.possibleGenders==EntityDescription.GENDER_NEUTRAL)
			{
				genderSelect.ids = new String[]{""+EntityDescription.GENDER_NEUTRAL};
				genderSelect.texts = new String[]{Language.v("gender."+EntityDescription.GENDER_NEUTRAL)};
				
			}
			if (race.possibleGenders==EntityDescription.GENDER_FEMALE)
			{
				genderSelect.ids = new String[]{""+EntityDescription.GENDER_FEMALE};
				genderSelect.texts = new String[]{Language.v("gender."+EntityDescription.GENDER_FEMALE)};
				
			}
			if (race.possibleGenders==EntityDescription.GENDER_MALE)
			{
				genderSelect.ids = new String[]{""+EntityDescription.GENDER_MALE};
				genderSelect.texts = new String[]{Language.v("gender."+EntityDescription.GENDER_MALE)};
			}
			genderSelect.setUpdated(true);
			genderSelect.activate();
			

			// attribute ratio
			int baseValue = GameLogicConstants.BASE_ATTRIBUTE_VALUE;
			attrPointsLeft = GameLogicConstants.ATTRIBUTE_POINTS_TO_USE;
			if (attributeValues==null) attributeValues = new FantasyAttributes(false);
			for (String id: FantasyAttributes.attributeName) {
				if (race.commonAttributeRatios.attributeRatios.get(id)!=null)
				{
					attributeValues.setAttribute(id, (int)(baseValue*race.commonAttributeRatios.attributeRatios.get(id)));
					lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
				} else
				{
					attributeValues.setAttribute(id, baseValue);
					lowestAttrValues.setAttribute(id, attributeValues.getAttribute(id));
				}
				Jcrpg.LOGGER.finer("ID = "+id+" = "+attributeValues.attributes.get(id));
				ValueTuner v = attributeTuners.get(id);
				v.value = attributeValues.attributes.get(id);
				v.text = ""+v.value;
				v.deactivate();
			}
			
			baseValue = GameLogicConstants.BASE_RESISTANCE_VALUE;
			if (resistanceValues==null) resistanceValues = new FantasyResistances(false);
			for (String id: FantasyResistances.resistanceName) {
				if (race.commonResistenceRatios.resistanceRatios.get(id)!=null)
				{
					resistanceValues.setResistance(id, (int)(baseValue*race.commonResistenceRatios.resistanceRatios.get(id)));
				} else
				{
					resistanceValues.setResistance(id, baseValue);
				}
			}
			inputLeft(genderSelect, "fake");
			inputEntered(professionSelect, "fake");

		} else
		if (base.equals(genderSelect))
		{
			MemberPerson race = charCreationRule.raceInstances.get(charCreationRule.selectableRaces.get(raceSelect.getSelection()));
			int i = genderSelect.getSelection();
			int id = Integer.parseInt(genderSelect.ids[i]);
			String genderPath = "";
			if (id==EntityDescription.GENDER_MALE) genderPath="male/";
			if (id==EntityDescription.GENDER_FEMALE) genderPath="female/";
			String path = "./data/portraits/"+race.pictureRoot+"/"+genderPath;
			pictureSelect.picturesPath = path;
			pictureSelect.setUpdated(true);
			pictureSelect.activate();
			
			VoiceList list = VoiceList.male;
			if (id==EntityDescription.GENDER_FEMALE) 
			{
				list = VoiceList.female;
			}
			Object[] audioDescList = new Object[list.list.size()];
			String[] ids = new String[list.list.size()];
			String[] texts = new String[list.list.size()];
			int count=0;
			for (AudioDescription desc:list.list)
			{
				audioDescList[count] = desc;
				ids[count] = ""+count;
				texts[count] = desc.getFormattedName();
				count++;
			}
			voiceSelect.objects = audioDescList;
			voiceSelect.texts = texts;
			voiceSelect.ids = ids;
			voiceSelect.setSelected(0);
			voiceSelect.setUpdated(true);
			voiceSelect.activate();
			voiceSelect.deactivate();
			
		} else
		if (base.equals(skillValueTuner))
		{
			skillPointsLeft = backupSkillPointsLeft;
			skillPointsLeftLabel.text = skillPointsLeft + " points left.";
			skillPointsLeftLabel.activate();
			
			Class<? extends SkillBase> skill = (Class<? extends SkillBase>)skillValueTuner.tunedObject;
			int level = personWithGenderAndRace.getMemberSkills().getSkillLevel(skill, null);
			
			skillValueTuner.value = level;
			skillValueTuner.setUpdated(true);
			skillValueTuner.deactivate();
		}
		return true;
	}
	
	public void playVoiceType(AudioDescription desc)
	{
		String snd = desc.getSound(AudioDescription.T_ATTACK);
		if (snd==null) snd = desc.getSound(AudioDescription.T_JOY);
		if (snd!=null)
			J3DCore.getInstance().audioServer.playLoading(snd,"ai");
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		if (base.equals(voiceSelect))
		{
			AudioDescription desc  = (AudioDescription)voiceSelect.getSelectedObject();
			playVoiceType(desc);
		} else
		if (base.equals(professionSelect))
		{
	    	if (message.equals("fake") && attributeValues!=null) {
				for (String id:attributeTuners.keySet())
				{
					ValueTuner v = attributeTuners.get(id);
					int value = v.getSelection();
					attributeValues.setAttribute(id, value);
					Jcrpg.LOGGER.finer("CHARACTER ATTRIBUTES _ "+id + " = "+value);
				}
				ArrayList<String> ids = new ArrayList<String>();
				ArrayList<String> texts = new ArrayList<String>();
				MemberPerson race = charCreationRule.raceInstances.get(charCreationRule.selectableRaces.get(raceSelect.getSelection()));
				int i = genderSelect.getSelection();
				int genderId = Integer.parseInt(genderSelect.ids[i]);
				int id = 0;
				ArrayList<Profession> professions = new ArrayList<Profession>();
				for (Class<? extends Profession> pClass: charCreationRule.selectableProfessions)
				{
					Profession p = charCreationRule.profInstances.get(pClass);
					if (p.isQualifiedEnoughWithLevelingPoints(race,genderId,attributeValues,GameLogicConstants.ATTRIBUTE_POINTS_TO_USE))
					{
			    		String s = Language.v("professions."+p.getClass().getSimpleName());
			    		ids.add(""+id);
			    		texts.add(s);
			    		professions.add(p);
					}
					id++;
				}
				String[] oldTexts = professionSelect.texts;
		    	professionSelect.ids = ids.toArray(new String[0]);
		    	professionSelect.texts = texts.toArray(new String[0]);
		    	professionSelect.objects = professions.toArray(new Object[0]);
		    	professionSelect.setUpdated(true);
	    		int count = 0;
	    		boolean needActivate = false;
	    		if (oldTexts.length!=professionSelect.texts.length) needActivate = true;
	    		for (String old:oldTexts)
	    		{
	    			try {
		    			if (!old.equals(professionSelect.texts[count++]))
		    			{
		    				needActivate = true; break;
		    			}
	    			}
	    			catch (Exception ex)
	    			{
	    				needActivate = true; break;
	    			}
	    		}
	    		if (needActivate) {
	    			core.audioServer.play(InputBase.SOUND_INPUTSELECTED);
	    			professionSelect.activate();
	    			professionSelect.deactivate();
	    		}
	    		inputUsed(base, message);
	    	}
		}
		return true;
	}

	Node imageNode = new Node("imageNode");
	HashMap<String, Quad> imgQuads = new HashMap<String, Quad>();
	@Override
	public boolean inputChanged(InputBase base, String message) {
		if (base.equals(voiceSelect))
		{
			AudioDescription desc  = (AudioDescription)voiceSelect.getSelectedObject();
			playVoiceType(desc);
		} else
		if (base.equals(addCharSelect))
		{
			int s = addCharSelect.getSelection();
			Iterator<CharListData> it = dataList.values().iterator();
			CharListData d = null;
			for (int i=0; i<=s; i++) {
				if (it.hasNext())
				{
					d = it.next();
				}
			}
			if (d==null) return false;
			Jcrpg.LOGGER.finer(d.pic.getName());
			try {
				Quad q = imgQuads.get(d.pic.getAbsolutePath());
				if (q==null) 
				{
					q = loadImageToQuad(d.pic,  0.12f*core.getDisplay().getWidth(), (0.16f)*core.getDisplay().getHeight(),0.75f*core.getDisplay().getWidth(), (1f-0.2f)*core.getDisplay().getHeight()  );
					imgQuads.put(d.pic.getAbsolutePath(), q);
				}
				imageNode.detachAllChildren();
				imageNode.attachChild(q);
				imageNode.attachChild(imageNode);
				pageMemberSelection.attachChild(imageNode);
				pageMemberSelection.updateRenderState();
			} catch (Exception ex)
			{
				
			}
				
		}
		return true;
	}

	private void setOriginalSkills()
	{
    	for (String groupId : SkillGroups.orderedGroups)
    	{
    		ArrayList<String> skillIds = new ArrayList<String>();
    		ArrayList<String> skillTexts = new ArrayList<String>();
    		ArrayList<Object> skillObjects = new ArrayList<Object>();
    		int counter = 0;
    		for (Class<? extends SkillBase> skill:SkillGroups.groupedSkills.get(groupId))
    		{
    			if (personWithGenderAndRace.getMemberSkills().skills.containsKey(skill)) {
    				int level = personWithGenderAndRace.getMemberSkills().skills.get(skill).level;
	    			String id = groupId+"."+counter;
	    			String text = skill.getSimpleName();
	    			int modifier = 1;
	    			try {
	    				modifier = profession.skillLearnModifier.getMultiplier(skill);
	    			} catch (Exception ex)
	    			{	
	    			}
	    			text = Language.v("skills."+text)+" ("+modifier+"x): "+level;
	    			skillIds.add(id);
	    			skillTexts.add(text);
	    			skillObjects.add(skill);
    			}
    			counter++;
    		}
    		ListSelect sel = skillSelects.get(groupId);
    		sel.ids = skillIds.toArray(new String[0]);
    		sel.texts = skillTexts.toArray(new String[0]);
    		sel.objects = skillObjects.toArray(new Object[0]);
    		sel.setUpdated(true);
    		sel.deactivate();
    	}
	}
	
}
