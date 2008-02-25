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
import java.util.Iterator;
import java.util.TreeMap;

import org.jcrpg.game.CharacterCreationRules;
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
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyMember;
import org.jcrpg.world.ai.profession.Profession;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

public class PartySetup extends PagedInputWindow {

	
	FontTT text;
	
	Node pageMemberSelection = new Node();
	Node pageCreationFirst = new Node();
	Node pageCreationSecond = new Node();

	
	ArrayList<EntityMemberInstance> charactersOfParty = new ArrayList<EntityMemberInstance>();
	
	// party select
	ArrayList<PartyMember> members = new ArrayList<PartyMember>();
	ListSelect addCharSelect = null;
	TextButton newChar;
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
	TextInputField surName;
	TextInputField foreName;
	// which skillgroup was used to enter modification ValueTuner
	ListSelect skillGroupLeftLast = null;
	Class<? extends SkillBase> skillTuned = null; 
	
	/**
	 * how many attribute points can be used by default.
	 */
	public static final int ATTRIBUTE_POINTS_TO_USE = 10;
	/**
	 * How many attribute points are left.
	 */
	int attrPointsLeft = 0;
	
	/**
	 * how many attribute points can be used by default.
	 */
	public static final int SKILL_POINTS_TO_USE = 5;
	/**
	 * How many attribute points are left.
	 */
	int skillPointsLeft = 0;
	
	// character creation result classes
	public MemberPerson personWithGenderAndRace = null;
	public Profession profession = null;
	public org.jcrpg.world.ai.abs.attribute.Attributes attributeValues = null;
	public org.jcrpg.world.ai.abs.attribute.Attributes lowestAttrValues = new FantasyAttributes();
	
	public PartySetup(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
		try {
	    	
			// page selection -----------------------------------------------
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.png", 0.8f*core.getDisplay().getWidth(), 1.65f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
	    	
	    	pageMemberSelection.attachChild(hudQuad);
	    	
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.10f, 0.35f, 0.07f,600f,"Select a character to add:",false); 
	    	addCharSelect = new ListSelect("add_char",this,pageMemberSelection,0.385f,0.15f,0.5f,0.05f,600f,new String[]{"id1","id2"},new String[]{"text to select1","text to select2"},null,null);
	    	addInput(0,addCharSelect);
	    	
	    	newChar = new TextButton("new_char",this,pageMemberSelection, 0.23f, 0.5f, 0.2f, 0.07f,400f,"New Character");
	    	addInput(0,newChar);
	    	rmChar = new TextButton("rm_char", this,pageMemberSelection, 0.50f, 0.5f, 0.2f, 0.07f,400f,"Remove Char.");
	    	addInput(0,rmChar);
	    	startGame = new TextButton("start",this,pageMemberSelection, 0.77f, 0.5f, 0.2f, 0.07f,400f,"Start Game");
	    	addInput(0,startGame);
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.7f, 0.2f, 0.07f,500f,"Use Up/Down to navigate through the screen.",false); 
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.75f, 0.2f, 0.07f,500f,"Press Left/Right to scroll in lists, Enter to act.",false);
	    	
	    	// page char creation 1 -------------------------------------------
	    	SharedMesh sQuad = new SharedMesh("--",hudQuad);
	    	pageCreationFirst.attachChild(sQuad);
	    	new TextLabel("",this,pageCreationFirst, 0.23f, 0.75f, 0.2f, 0.07f,600f,"Tune the attributes to attain a profession.",false);

	    	new TextLabel("",this,pageCreationFirst, 0.37f, 0.08f, 0.3f, 0.06f,400f,"Character Creation",false); 

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
	    	
	    	
	    	attrPointsLeftLabel = new TextLabel("",this,pageCreationFirst, 0.23f, 0.7f, 0.2f, 0.07f,500f,attrPointsLeft+" points left.",false); 
	    	
	    	int posY = 0;
	    	for (String s: FantasyAttributes.attributeName)
	    	{
	    		String text = Language.v("fantasyattributes."+s);
	    		System.out.println("TEXT" +text);
	    		new TextLabel(s+"_label",this,pageCreationFirst,0.23f,0.3f+0.05f*posY,0.15f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,pageCreationFirst, 0.45f,0.3f+0.05f*posY,0.15f,0.04f,600f,10,0,100,1);
	    		attributeTuners.put(s, v);
	    		addInput(1,v);
	    		posY++;
	    	}

	    	pictureSelect = new PictureSelect("picture_select", this, pageCreationFirst, 0.7f,0.4f,0.15f,0.2f,600f);
	    	addInput(1,pictureSelect);

	    	new TextLabel("",this,pageCreationFirst, 0.7f, 0.6f, 0.3f, 0.06f,600f,"Profession:",false); 
	    	{
		    	professionSelect = new ListSelect("profession", this,pageCreationFirst, 0.7f,0.65f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,professionSelect);
	    	
	    	
	    	nextPage = new TextButton("next",this,pageCreationFirst, 0.77f, 0.8f, 0.2f, 0.07f,400f,"Next Page");
	    	addInput(1,nextPage);
	    	
	    	// page char creation 2 -------------------------------------------
	    	SharedMesh sQuad2 = new SharedMesh("--",hudQuad);
	    	pageCreationSecond.attachChild(sQuad2);

	    	new TextLabel("",this,pageCreationSecond, 0.37f, 0.08f, 0.3f, 0.06f,500f,"Character Creation",false); 
	    	charInfo = new TextLabel("",this,pageCreationSecond, 0.37f, 0.16f, 0.3f, 0.06f,400f,"",false); 
	    	new TextLabel("",this,pageCreationSecond, 0.14f, 0.73f, 0.2f, 0.07f,700f,"Select a skill group, navigate skill (left/right), press Enter to tune.",false);
	    	new TextLabel("",this,pageCreationSecond, 0.14f, 0.77f, 0.2f, 0.07f,700f,"While tuning skill press Enter to set selected value for skill.",false);
	    	new TextLabel("",this,pageCreationSecond, 0.14f, 0.81f, 0.2f, 0.07f,700f,"Leave no points unused to be able finish.",false);

	    	posY = 0; 
	    	for (String groupId : SkillGroups.orderedGroups)
	    	{
	    		String groupName = Language.v("skillgroups."+groupId);
	    		new TextLabel(groupId+"_label",this,pageCreationSecond,0.13f,0.2f+0.05f*posY,0.15f,0.04f,600f, groupName, false);
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
	    		ListSelect sel = new ListSelect("skillgroup", this,pageCreationSecond, 0.38f,0.2f+0.05f*posY,0.3f,0.04f,600f,skillIds.toArray(new String[0]),skillTexts.toArray(new String[0]),null,null);
	    		sel.objects = skillObjects.toArray(new Object[0]); // helping the selection
	    		posY++;
	    		skillSelects.put(groupId, sel);
	    		addInput(2,sel);
	    	}
	    	skillPointsLeftLabel = new TextLabel("",this,pageCreationSecond, 0.23f, 0.7f, 0.2f, 0.07f,500f,skillPointsLeft+" points left.",false); 
	    	
	    	skillText = new TextLabel("",this,pageCreationSecond, 0.6f, 0.2f, 0.3f, 0.06f,600f,Language.v("partySetup.selectSkill"),false); 
	    	skillValueTuner = new ValueTuner("skill_tuner",this,pageCreationSecond, 0.68f,0.25f,0.15f,0.04f,600f,0,0,100,1);
	    	addInput(2,skillValueTuner);
	    	skillValueTuner.setEnabled(false);
	    	
	    	new TextLabel("",this,pageCreationSecond, 0.3f, 0.57f, 0.3f, 0.06f,600f,Language.v("partySetup.foreName")+":",false); 
	    	foreName = new TextInputField("foreName",this,pageCreationSecond, 0.3f, 0.62f, 0.3f, 0.06f,600f,"",15);
	    	addInput(2,foreName);
	    	new TextLabel("",this,pageCreationSecond, 0.66f, 0.57f, 0.3f, 0.06f,600f,Language.v("partySetup.surName")+":",false); 
	    	surName = new TextInputField("surName",this,pageCreationSecond, 0.66f, 0.62f, 0.3f, 0.06f,600f,"",15); 
	    	addInput(2,surName);

	    	readyChar = new TextButton("ready",this,pageCreationSecond, 0.77f, 0.7f, 0.2f, 0.07f,400f,Language.v("partySetup.ready"));
	    	addInput(2,readyChar);
	    	
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
		core.getRootNode().detachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(false);
	}

	@Override
	public void show() {
		currentPage = 0;
		charactersOfParty.clear();
		core.uiBase.hud.characters.updateForPartyCreation(charactersOfParty);
		core.uiBase.hud.characters.show();
		
		setupPage();
		changePage(0);
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(true);
	}
	
	CharacterCreationRules charCreationRule = null;
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
			File f = new File(SaveLoadNewGame.charsDir);
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
						File sF = new File(SaveLoadNewGame.charsDir+"/"+file+"/"+sFile);
						if (sF.isFile())
						{
							if (sF.getName().endsWith(".zip"))
							{
								data.charData = sF;
								try 
								{
									MemberPerson p = SaveLoadNewGame.loadCharacter(sF);
									data.charName = p.getClass().getSimpleName()+" "+p.professions.get(0).getClass().getSimpleName()+" - "+p.foreName+" "+p.surName;
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
			} else {
				toggle();
				core.mainMenu.toggle();
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean inputUsed(InputBase base, String message) {
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
		if (base.equals(addCharSelect))
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
			charactersOfParty.add(new EntityMemberInstance(d.person));
			core.uiBase.hud.characters.updateForPartyCreation(charactersOfParty);
			core.uiBase.hud.characters.show();
			
		} else
		if (base.equals(skillValueTuner))
		{
			if (message.equals("enter")) {
				// ############## SKILL VALUE SET, feed it back into listSelect
				personWithGenderAndRace.commonSkills.setSkillValue((Class<? extends SkillBase>)skillValueTuner.tunedObject, skillValueTuner.value);
				skillGroupLeftLast.setUpdated(true);
				int id = 0;
				for (Object o:skillGroupLeftLast.objects)
				{
					if (o.equals(skillValueTuner.tunedObject))
					{
						// this is the id that's modified:
						skillGroupLeftLast.texts[id] = Language.v("skills."+((Class)o).getSimpleName())+ ": "+skillValueTuner.value;
					}
					id++;
				}
				setSelected(skillGroupLeftLast);
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
			String id = select.ids[select.getSelection()];
			Class<?extends SkillBase> skill = (Class<? extends SkillBase>)select.objects[select.getSelection()];
			skillTuned = skill;
			String group = id.substring(0,id.indexOf('.'));
			int count = Integer.parseInt(id.substring(id.indexOf('.')+1));
			System.out.println("GROUP = "+group+ " - "+count);
			skillValueTuner.setEnabled(true);
			skillValueTuner.value = personWithGenderAndRace.commonSkills.getSkillLevel(skill, null);
			skillValueTuner.setUpdated(true);
			skillValueTuner.tunedObject = skill;
			int modifier = 1;
			try {
				modifier = profession.skillLearnModifier.multipliers.get(skill);
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
					if (v.value<val) return false;
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
						return false;
					} else
					{
						attrPointsLeftLabel.text = attrPointsLeft + " points left.";
						attrPointsLeftLabel.activate();
						inputEntered(professionSelect, "fake");
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
			attrPointsLeft = ATTRIBUTE_POINTS_TO_USE;
			skillPointsLeft = SKILL_POINTS_TO_USE;
			attrPointsLeftLabel.text = attrPointsLeft + " points left.";
			attrPointsLeftLabel.activate();
			skillPointsLeftLabel.text = skillPointsLeft + " points left.";
			skillPointsLeftLabel.activate();
			currentPage=1;
			setupPage();
		}
		else
		if (base.equals(nextPage))
		{
			// ######### MOVING TO SKILL PAGE, race/profession/attributes are done
			personWithGenderAndRace = charCreationRule.raceInstances.get(charCreationRule.selectableRaces.get(raceSelect.getSelection())).copy(null);
			if (professionSelect.texts.length==0) return true;
			profession = charCreationRule.profInstances.get(charCreationRule.selectableProfessions.get(Integer.parseInt(professionSelect.ids[professionSelect.getSelection()])));
			if (true==false && (profession==null || attrPointsLeft>0)) return true;
			if (profession==null) return true;
			personWithGenderAndRace.addProfessionInitially(profession);

			attributeValues = new FantasyAttributes();
			for (String id:attributeTuners.keySet())
			{
				ValueTuner v = attributeTuners.get(id);
				int value = v.getSelection();
				attributeValues.setAttribute(id, value);
				System.out.println("CHARACTER ATTRIBUTES _ "+id + " = "+value);
			}
			System.out.println("CHARACTER PERSON & PROFESSION : "+personWithGenderAndRace+" "+profession);
			charInfo.text = Language.v("races."+personWithGenderAndRace.getClass().getSimpleName()) + " " + Language.v("professions."+profession.getClass().getSimpleName());
			charInfo.activate();
			
	    	for (String groupId : SkillGroups.orderedGroups)
	    	{
	    		ArrayList<String> skillIds = new ArrayList<String>();
	    		ArrayList<String> skillTexts = new ArrayList<String>();
	    		ArrayList<Object> skillObjects = new ArrayList<Object>();
	    		int counter = 0;
	    		for (Class<? extends SkillBase> skill:SkillGroups.groupedSkills.get(groupId))
	    		{
	    			if (personWithGenderAndRace.commonSkills.skills.containsKey(skill)) {
	    				int level = personWithGenderAndRace.commonSkills.skills.get(skill).level;
		    			String id = groupId+"."+counter;
		    			String text = skill.getSimpleName();
		    			int modifier = 1;
		    			try {
		    				modifier = profession.skillLearnModifier.multipliers.get(skill);
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
			
			base.deactivate();
			currentPage=2;
			setupPage();
		}
		if (base.equals(readyChar))
		{
			// ################## CHARACTER COMPLETE, saving it
			if (foreName.text.length()==0) return true; // a name must be entered
			if (skillPointsLeft>0) return true; // all skill points must be used
			personWithGenderAndRace.professions.add(profession);
			personWithGenderAndRace.setAttributes(attributeValues);
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
			
			for (int i=0; i<6; i++)
			{
				members.add(new PartyMember("_"+i,new AudioDescription()));
			}			
			if (charCreationRule == null)
			{
				charCreationRule = new CharacterCreationRules(null,null);
			}
			core.uiBase.hud.characters.hide();
			SaveLoadNewGame.newGame(core,charactersOfParty,charCreationRule);
			core.init3DGame();
			core.uiBase.hud.characters.update();
			core.uiBase.hud.characters.show();
			core.getRootNode().updateRenderState();
			core.gameState.engine.setPause(false);
			core.audioServer.stopAndResumeOthers("main");
		}
		return true;
	}

	@Override
	public boolean inputLeft(InputBase base, String message) {
		if (base.equals(raceSelect))
		{
			System.out.println("RACE SELECT LEFT");
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
			

			// attribute ratio
			int baseValue = 10;
			attrPointsLeft = ATTRIBUTE_POINTS_TO_USE;
			if (attributeValues==null) attributeValues = new FantasyAttributes();
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
				System.out.println("ID = "+id+" = "+attributeValues.attributes.get(id));
				ValueTuner v = attributeTuners.get(id);
				v.value = attributeValues.attributes.get(id);
				v.text = ""+v.value;
				v.deactivate();
			}
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
			pictureSelect.deactivate();
		} else
		if (base.equals(skillValueTuner))
		{
			personWithGenderAndRace.commonSkills.setSkillValue((Class<? extends SkillBase>)skillValueTuner.tunedObject, skillValueTuner.value);
			skillGroupLeftLast.setUpdated(true);
			int id = 0;
			for (Object o:skillGroupLeftLast.objects)
			{
				if (o.equals(skillValueTuner.tunedObject))
				{
					// this is the id that's modified:
					skillGroupLeftLast.texts[id] = Language.v("skills."+((Class)o).getSimpleName())+ ": "+skillValueTuner.value;
				}
				id++;
			}
		}
		return true;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		if (base.equals(professionSelect))
		{
			for (String id:attributeTuners.keySet())
			{
				ValueTuner v = attributeTuners.get(id);
				int value = v.getSelection();
				attributeValues.setAttribute(id, value);
				System.out.println("CHARACTER ATTRIBUTES _ "+id + " = "+value);
			}
			ArrayList<String> ids = new ArrayList<String>();
			ArrayList<String> texts = new ArrayList<String>();
			MemberPerson race = charCreationRule.raceInstances.get(charCreationRule.selectableRaces.get(raceSelect.getSelection()));
			int i = genderSelect.getSelection();
			int genderId = Integer.parseInt(genderSelect.ids[i]);
			int id = 0;
			for (Class<? extends Profession> pClass: charCreationRule.selectableProfessions)
			{
				Profession p = charCreationRule.profInstances.get(pClass);
				if (p.isQualifiedEnough(race,genderId,attributeValues))
				{
		    		String s = Language.v("professions."+p.getClass().getSimpleName());
		    		ids.add(""+id);
		    		texts.add(s);		    		
				}
				id++;
			}
			String[] oldTexts = professionSelect.texts;
	    	professionSelect.ids = ids.toArray(new String[0]);
	    	professionSelect.texts = texts.toArray(new String[0]);
	    	professionSelect.setUpdated(true);
	    	if (message.equals("fake")) {
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
	    	}
		}
		return true;
	}

	Node imageNode = new Node();
	HashMap<String, Quad> imgQuads = new HashMap<String, Quad>();
	@Override
	public boolean inputChanged(InputBase base, String message) {
		if (base.equals(addCharSelect))
		{
			int s = addCharSelect.getSelection();
			Iterator<CharListData> it = dataList.values().iterator();
			CharListData d = null;
			for (int i=0; i<=s; i++) {
				d = it.next();
			}
			System.out.println(d.pic.getName());
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

}
