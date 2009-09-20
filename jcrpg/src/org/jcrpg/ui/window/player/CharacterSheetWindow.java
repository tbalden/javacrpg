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
package org.jcrpg.ui.window.player;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.UIImageCache;
import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.ValueTuner;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.state.EntityMemberState;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.ai.profession.Profession;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Player's party character sheet window.
 * @author illes
 *
 */
public class CharacterSheetWindow extends PagedInputWindow {

	Node page0 = new Node();
	
	public ListSelect characterSelect;
	
	// character data
	Quad currentPic = null;
	ListSelect professionSelect = null;

	ValueTuner level = null;
	ValueTuner killNeutDeath = null;
	ValueTuner health = null;
	ValueTuner stamina = null;
	ValueTuner morale = null;
	ValueTuner sanity = null;
	ValueTuner mana = null;
	ValueTuner xp = null;
	
	ListSelect memberStateEffects = null;
	
	HashMap<String, ValueTuner> attributeTuners = new HashMap<String, ValueTuner>();
	HashMap<String, ListSelect> skillSelects = new HashMap<String, ListSelect>();

	HashMap<String, ValueTuner> resistanceTuners = new HashMap<String, ValueTuner>();
	
	TextButton closeWindow;
	
	public CharacterSheetWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.75f*core.getDisplay().getWidth(), 1.7f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.10f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	sQuad.setLocalTranslation(hudQuad.getLocalTranslation());
	    	page0.attachChild(sQuad);

	    	//pictureSelect = new PictureSelect("picture_select", this, page0, 0.78f,0.25f,0.15f,0.2f,600f);

	    	new TextLabel("",this,page0, 0.40f, 0.058f, 0.0f, 0.06f,400f,"Character Sheet",false);
	    	
	    	new TextLabel("",this,page0, 0.54f, 0.80f, 0.0f, 0.06f,600f,"Press F3 or Backspace to leave.",false);
	    	
    		characterSelect = new ListSelect("member", this,page0, 0.50f,0.11f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,characterSelect);
	    	
	    	professionSelect = new ListSelect("profession", this,page0, 0.55f,0.2f,0.3f,0.06f,600f,new String[0],new String[0],null,null);

	    	new TextLabel("level",this,page0,0.45f,0.25f,0.0f,0.04f,600f, "Level", false);
	    	level = new ValueTuner("level",this,page0, 0.47f,0.28f,0.15f,0.04f,600f,10,0,100,1);
	    	new TextLabel("kill",this,page0,0.57f,0.25f,0.0f,0.04f,600f, "Kill/Neut./Death", false);
	    	killNeutDeath = new ValueTuner("level",this,page0, 0.63f,0.28f,0.15f,0.04f,1000f,10,0,100,1);

	    	new TextLabel("health",this,page0,0.45f,0.31f,0.0f,0.04f,600f, "Health", false);
	    	health = new ValueTuner("health",this,page0, 0.47f,0.34f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("stamina",this,page0,0.61f,0.31f,0.0f,0.04f,600f, "Stamina", false);
	    	stamina = new ValueTuner("stamina",this,page0, 0.63f,0.34f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("morale",this,page0,0.45f,0.39f,0.0f,0.04f,600f, "Morale", false);
	    	morale = new ValueTuner("morale",this,page0, 0.47f,0.42f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("sanity",this,page0,0.61f,0.39f,0.0f,0.04f,600f, "Sanity", false);
	    	sanity = new ValueTuner("sanity",this,page0, 0.63f,0.42f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("mana",this,page0,0.45f,0.46f,0.0f,0.04f,600f, "Mana", false);
	    	mana = new ValueTuner("mana",this,page0, 0.47f,0.49f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("xp",this,page0,0.61f,0.46f,0.0f,0.04f,600f, "XP", false);
	    	xp = new ValueTuner("xp",this,page0, 0.63f,0.49f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("membereffects",this,page0,0.40f,0.55f,0.0f,0.04f,600f, "States", false);
	    	memberStateEffects = new ListSelect("stateeffects",this,page0, 0.61f,0.55f,0.3f,0.04f,600f,new String[0],new String[0],null,null);
	    	addInput(0,memberStateEffects);
	    	

	    	int posY = 0;
	    	for (String s: FantasyAttributes.attributeName)
	    	{
	    		String text = Language.v("fantasyattributes.short."+s);
	    		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("TEXT" +text);
	    		new TextLabel(s+"_label",this,page0,0.149f,0.2f+0.05f*posY,0.0f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,page0, 0.317f,0.2f+0.05f*posY,0.15f,0.04f,600f,10,0,100,1);
	    		attributeTuners.put(s, v);
	    		//addInput(1,v);
	    		posY++;
	    	}

	    	posY = 0;
	    	for (String s: FantasyResistances.resistanceName)
	    	{
	    		String text = Language.v("fantasyresistances.shortest."+s);
	    		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("TEXT" +text);
	    		new TextLabel(s+"_label",this,page0,0.549f+0.16f*(posY%2),0.60f+0.05f*(int)(posY/2),0.0f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,page0, 0.66f+0.16f*(posY%2),0.60f+0.05f*(int)(posY/2),0.07f,0.04f,600f,10,0,100,1);
	    		resistanceTuners.put(s, v);
	    		//addInput(1,v);
	    		posY++;
	    	}

	    	//addInput(1,pictureSelect);

	    	posY = 0; 
	    	for (String groupId : SkillGroups.orderedGroups)
	    	{
	    		String groupName = Language.v("skillgroups."+groupId);
	    		new TextLabel(groupId+"_label",this,page0,0.149f,0.6f+0.05f*posY,0.0f,0.04f,600f, groupName, false);
	    		ArrayList<String> skillIds = new ArrayList<String>();
	    		ArrayList<String> skillTexts = new ArrayList<String>();
	    		ArrayList<Object> skillObjects = new ArrayList<Object>();
	    		ListSelect sel = new ListSelect("skillgroup", this,page0, 0.39f,0.6f+0.05f*posY,0.3f,0.04f,600f,skillIds.toArray(new String[0]),skillTexts.toArray(new String[0]),skillObjects.toArray(new Object[0]),null,null);
	    		posY++;
	    		skillSelects.put(groupId, sel);
	    		addInput(0,sel);
	    	}
	    	
	    	addInput(0,professionSelect);

	    	closeWindow = new TextButton("close",this,page0, 0.85f, 0.060f, 0.02f, 0.045f,600f," x");
	    	addInput(0,closeWindow);

	    	addPage(0, page0);
	    	
	    	base.addEventHandler("back", this);
		} catch (Exception ex)
		{	
			ex.printStackTrace();
		}
		
	}

	public PartyInstance party = null;
	
	public void setPageData(PartyInstance party)
	{
		this.party = party;
	}

	/** 
	 * call this with an already setup window only, needs CharacterSelect initialized!
	 * @param i party member to update window for.
	 */
	public void directUpdateToMember(EntityMemberInstance i)
	{
		currentMember = i;
		characterSelect.setSelected(i);
		updateToParty();
	}
	
	public EntityMemberInstance currentMember = null;
	public int lastUpdatedLivingPartySize = 0;
	private ArrayList<EntityMemberInstance> tmpFilteredMembers = new ArrayList<EntityMemberInstance>();
	public void updateToParty()
	{
		int livingMembersCounter = 0;
		boolean foundCurrent = false;
		tmpFilteredMembers.clear();
		for (EntityMemberInstance i : party.orderedParty)
		{
			if (i.memberState.healthPoint>=0)
			{
				// living, TODO tune to better state values
				livingMembersCounter++;
				if (i == characterSelect.getSelectedObject())
				{
					currentMember = i;
					foundCurrent = true;
				}
				tmpFilteredMembers.add(i);
			}
		}
		if (livingMembersCounter!=lastUpdatedLivingPartySize)
		{
			String[] ids = new String[livingMembersCounter];
			Object[] objects = new Object[livingMembersCounter];
			String[] texts = new String[livingMembersCounter];
			int counter = 0;
			for (EntityMemberInstance i:tmpFilteredMembers)
			{
				ids[counter] = ""+counter;
				objects[counter] = i;
				texts[counter] = ((MemberPerson)i.description).getForeName();
				counter++;
			}
			characterSelect.reset();
			characterSelect.ids = ids;
			characterSelect.objects = objects;
			characterSelect.texts = texts;
			characterSelect.setUpdated(true);
			characterSelect.deactivate();

		}
		if (!foundCurrent)
		{
			characterSelect.setSelected(0);
			characterSelect.setUpdated(true);
			characterSelect.deactivate();
		} else
		{
			characterSelect.setSelected(currentMember);
		}
		updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
		
	}

	private void updateToMemberInstance(EntityMemberInstance instance)
	{
		
		//memberStateEffects
		{
			ArrayList<StateEffect> list = instance.memberState.getStateEffects();
			String[] ids = new String[list.size()];
			String[] texts = new String[list.size()];
			int count = 0;
			for (StateEffect state:list)
			{
				ids[count] = ""+count;
				texts[count] = state.getName()+" "+state.getLeftDuration(core.gameState.engine.getWorldMeanTime().getTimeInRound(), core.gameState.engine.getWorldMeanTime());			
				count++;
			}
			memberStateEffects.ids = ids;
			memberStateEffects.texts = texts;
			memberStateEffects.maxCount = ids.length; 
			memberStateEffects.setSelected(0);
			memberStateEffects.setUpdated(true);
			memberStateEffects.deactivate();
		}
		
		
		Attributes attr = (instance).getAttributes();
		Attributes attrVanilla = (instance).getAttributesVanilla();
		for (String id: FantasyAttributes.attributeName) {
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ID = "+id+" = "+attributeValues.attributes.get(id));
			ValueTuner v = attributeTuners.get(id);
			v.value = attr.getAttribute(id);
			int vanilla = attrVanilla.getAttribute(id);
			if (v.value!=vanilla)
			{
				v.text = ""+v.value+" ("+vanilla+")";
			} else
			{
				v.text = ""+v.value;
			}
			v.deactivate();
		}
		
		Resistances res = (instance).getResistances();
		Resistances resVanilla = (instance).getResistancesVanilla();
		for (String id: FantasyResistances.resistanceName) {
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ID = "+id+" = "+attributeValues.attributes.get(id));
			ValueTuner v = resistanceTuners.get(id);
			v.value = res.getResistance(id);
			int vanilla = resVanilla.getResistance(id);
			if (v.value!=vanilla)
			{
				v.text = ""+v.value+" ("+vanilla+")";
			} else
			{
				v.text = ""+v.value;
			}
			v.deactivate();
		}

		
    	for (String groupId : SkillGroups.orderedGroups)
    	{
    		ArrayList<String> skillIds = new ArrayList<String>();
    		ArrayList<String> skillTexts = new ArrayList<String>();
    		ArrayList<String> skillTooltips = new ArrayList<String>();
    		ArrayList<Object> skillObjects = new ArrayList<Object>();
    		int counter = 0;
    		for (Class<? extends SkillBase> skill:SkillGroups.groupedSkills.get(groupId))
    		{
    			if (instance.getSkills().skills.containsKey(skill)) {
    				int level = instance.getSkills().skills.get(skill).level;
	    			String id = groupId+"."+counter;
	    			String text = skill.getSimpleName();
	    			String tooltip = Language.v("skills.tooltip."+skill.getSimpleName());
	    			int modifier = 1;
	    			try {
	    				modifier = core.gameState.charCreationRules.profInstances.get(instance.description.currentProfession).skillLearnModifier.getMultiplier(skill);
	    			} catch (Exception ex)
	    			{}
	    			text = Language.v("skills."+text)+" ("+modifier+"x): "+level;
	    			skillIds.add(id);
	    			skillTexts.add(text);
	    			skillTooltips.add(tooltip);
	    			skillObjects.add(skill);
    			}
    			counter++;
    		}
    		ListSelect sel = skillSelects.get(groupId);
    		sel.ids = skillIds.toArray(new String[0]);
    		sel.texts = skillTexts.toArray(new String[0]);
    		sel.objects = skillObjects.toArray(new Object[0]);
    		sel.tooltips = skillTooltips.toArray(new String[0]);
    		sel.setUpdated(true);
    		sel.deactivate();
    	}
		
		//instance.memberState.
    	
		try {

			if (currentPic!=null) currentPic.removeFromParent();
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("--- PIC: "+((MemberPerson)instance.description).getPicturePath());
			currentPic = UIImageCache.getImage(((MemberPerson)instance.description).getPicturePath(), false, 75f);
			currentPic.setLocalTranslation(1.6f*core.getDisplay().getWidth()/2,1.55f*core.getDisplay().getHeight()/2,0);
			currentPic.setLocalScale(1f* (core.getDisplay().getWidth()/640f));
			page0.attachChild(currentPic);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		{
			int counter = 0;
			int profSelSize = instance.description.professions.size();
			String[] ids = new String[profSelSize];
			String[] texts = new String[profSelSize];
			Object[] objects = new Object[profSelSize];
			for (Class<?extends Profession> p:instance.description.professions)
			{
				ids[counter] = ""+counter;
				texts[counter] = p.getSimpleName();
				objects[counter] = p;
				counter++;
			}
			professionSelect.ids = ids;
			professionSelect.texts = texts;
			professionSelect.objects = objects;
			professionSelect.setUpdated(true);
			professionSelect.deactivate();
		}
    	
    	
    	level.text = ""+instance.memberState.level;
    	level.deactivate();
    	PersistentMemberInstance pMI = ((PersistentMemberInstance)instance);
    	killNeutDeath.text = ""+pMI.killCount+" / "+pMI.neutralizeCount+" / "+pMI.deathCount;
    	killNeutDeath.deactivate();
		
		health.text = ""+instance.memberState.healthPoint+ "/"+instance.memberState.maxHealthPoint;
		health.deactivate();

		stamina.text = ""+instance.memberState.staminaPoint+ "/"+instance.memberState.maxStaminaPoint;
		stamina.deactivate();

		morale.text = ""+instance.memberState.moralePoint+ "/"+instance.memberState.maxMoralePoint;
		morale.deactivate();

		sanity.text = ""+instance.memberState.sanityPoint+ "/"+instance.memberState.maxSanityPoint;
		sanity.deactivate();

		mana.text = ""+instance.memberState.manaPoint+ "/"+instance.memberState.maxManaPoint;
		mana.deactivate();

		xp.text = ""+instance.memberState.experiencePoint+ "/"+ instance.memberState.level * EntityMemberState.LEVELING_XP;
		xp.deactivate();

	}
	
	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==characterSelect)
		{
			characterSelect.deactivate();
			updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
			return true;
		}
		else
		if (base == closeWindow)
		{
			toggle();
			return true;
		}

		return super.inputUsed(base, message);
	}
	
	@Override
	public boolean handleKey(String key)
	{
		if (super.handleKey(key)) return true;
		if (key.equals("back"))
		{
			toggle();
			return true;
		}
		return false;
	}
	
	@Override
	public void hide() {
		super.hide();
		if (fallbackWindow!=null) 
		{
			fallbackWindow.toggle();
			core.getKeyboardHandler().noToggleWindowByKey=noToggleWindowByKeySettingAfterFallbackWindowUse;
			fallbackWindow = null;
		}
	}
	@Override
	public void show() {
		super.show();
		updateToParty();
	}

	public InputWindow fallbackWindow = null;
	public boolean noToggleWindowByKeySettingAfterFallbackWindowUse = false;
	
	@Override
	public void characterSelected(int count, EntityMemberInstance member,
			int inputType) {
			if (fallbackWindow==null) // not in special case of special char needed in the sheet.
			{
				// direct update.
				directUpdateToMember(member);
			}
	}

}
