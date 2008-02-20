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
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
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

	
	// party select
	ArrayList<PartyMember> members = new ArrayList<PartyMember>();
	ListSelect addCharSelect = null;
	TextButton newChar;
	TextButton rmChar;
	TextButton startGame;
	
	// creation 1
	ListSelect raceSelect = null;
	ListSelect genderSelect = null;
	TextLabel pointsLeft = null;
	HashMap<String, ValueTuner> attributes = new HashMap<String, ValueTuner>();
	ListSelect professionSelect = null;
	PictureSelect pictureSelect = null;
	TextButton nextPage;

	// creation 2
	TextButton readyChar;
	TextInputField sureName;
	TextInputField foreName;
	
	/**
	 * how many attribute points can be used by default.
	 */
	public static final int ATTRIBUTE_POINTS_TO_USE = 20;
	/**
	 * How many attribute points are left.
	 */
	int attrPointsLeft = 0;
	
	
	// character creation result classes
	public MemberPerson personWithGenderAndRace = null;
	public Profession profession = null;
	public org.jcrpg.world.ai.abs.attribute.Attributes attributeValues = null;
	
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
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.7f, 0.2f, 0.07f,400f,"Use Up/Down to navigate through the screen.",false); 
	    	new TextLabel("",this,pageMemberSelection, 0.23f, 0.75f, 0.2f, 0.07f,400f,"Press Enter to act.",false);
	    	
	    	// page char creation 1 -------------------------------------------
	    	SharedMesh sQuad = new SharedMesh("--",hudQuad);
	    	pageCreationFirst.attachChild(sQuad);

	    	new TextLabel("",this,pageCreationFirst, 0.37f, 0.15f, 0.3f, 0.06f,600f,"Race:",false); 
	    	{
		    	raceSelect = new ListSelect("race",this,pageCreationFirst, 0.37f,0.2f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,raceSelect);

	    	new TextLabel("",this,pageCreationFirst, 0.7f, 0.15f, 0.3f, 0.06f,600f,"Gender:",false); 
	    	{
		    	genderSelect = new ListSelect("gender", this,pageCreationFirst, 0.7f,0.2f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,genderSelect);
	    	
	    	
	    	pointsLeft = new TextLabel("",this,pageCreationFirst, 0.23f, 0.8f, 0.2f, 0.07f,400f,attrPointsLeft+" points left.",false); 
	    	
	    	int posY = 0;
	    	for (String s: FantasyAttributes.attributeName)
	    	{
	    		String text = Language.v("fantasyattributes."+s);
	    		System.out.println("TEXT" +text);
	    		new TextLabel(s+"_label",this,pageCreationFirst,0.23f,0.3f+0.05f*posY,0.15f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,pageCreationFirst, 0.45f,0.3f+0.05f*posY,0.15f,0.04f,600f,10,0,100);
	    		attributes.put(s, v);
	    		addInput(1,v);
	    		posY++;
	    	}

	    	pictureSelect = new PictureSelect("picture_select", this, pageCreationFirst, 0.7f,0.4f,0.15f,0.2f,600f);
	    	addInput(1,pictureSelect);

	    	new TextLabel("",this,pageCreationFirst, 0.37f, 0.65f, 0.3f, 0.06f,600f,"Profession:",false); 
	    	{
		    	professionSelect = new ListSelect("profession", this,pageCreationFirst, 0.37f,0.7f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(1,professionSelect);
	    	
	    	
	    	nextPage = new TextButton("next",this,pageCreationFirst, 0.77f, 0.7f, 0.2f, 0.07f,400f,"Next Page");
	    	addInput(1,nextPage);
	    	
	    	// page char creation 2 -------------------------------------------
	    	SharedMesh sQuad2 = new SharedMesh("--",hudQuad);
	    	pageCreationSecond.attachChild(sQuad2);

	    	new TextLabel("",this,pageCreationSecond, 0.3f, 0.57f, 0.3f, 0.06f,600f,"Forename:",false); 
	    	foreName = new TextInputField("foreName",this,pageCreationSecond, 0.3f, 0.62f, 0.3f, 0.06f,600f,"",15);
	    	addInput(2,foreName);
	    	new TextLabel("",this,pageCreationSecond, 0.66f, 0.57f, 0.3f, 0.06f,600f,"Surename:",false); 
	    	sureName = new TextInputField("sureName",this,pageCreationSecond, 0.66f, 0.62f, 0.3f, 0.06f,600f,"",15); 
	    	addInput(2,sureName);

	    	readyChar = new TextButton("ready",this,pageCreationSecond, 0.77f, 0.7f, 0.2f, 0.07f,400f,"Ready");
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
				cCR = new CharacterCreationRules(null,null);
			} else
			{
				cCR = core.gameState.charCreationRules;
			}
	    	{
		    	int id = 0;
		    	String[] ids = new String[cCR.selectableRaces.size()];
		    	String[] names = new String[cCR.selectableRaces.size()];
		    	for (Class<? extends EntityMember> c: cCR.selectableRaces)
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
									data.charName = p.getClass().getSimpleName()+" "+p.professions.get(0).getClass().getSimpleName()+" - "+p.foreName+" "+p.sureName;
									while (true) {
										if (dataList1.get(data.charName)!=null)
										{
											data.charName += "_";
										} else
										{
											break;
										}
									}
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
				base.hud.characters.show();
			}
			toggle();
			core.mainMenu.toggle();
		}
		return true;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base instanceof ValueTuner) {
			int count = 0;
			for (ValueTuner v:attributes.values())
			{
				if (base.equals(v))
				{
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
						pointsLeft.text = attrPointsLeft + " points left.";
						pointsLeft.activate();
						return true;
					}
						
				}
				count++;
			}
		}
		if (base.equals(newChar))
		{
			base.deactivate();
			attrPointsLeft = ATTRIBUTE_POINTS_TO_USE;
			pointsLeft.text = attrPointsLeft + " points left.";
			pointsLeft.activate();
			currentPage=1;
			setupPage();
		}
		else
		if (base.equals(nextPage))
		{
			personWithGenderAndRace = cCR.raceInstances.get(cCR.selectableRaces.get(raceSelect.getSelection())).copy(null);
			profession = cCR.profInstances.get(cCR.selectableProfessions.get(professionSelect.getSelection()));
			if (professionSelect.texts.length==0 || profession==null || attrPointsLeft>0) return true;
			attributeValues = new FantasyAttributes();
			for (String id:attributes.keySet())
			{
				ValueTuner v = attributes.get(id);
				int value = v.getSelection();
				attributeValues.setAttribute(id, value);
				System.out.println("CHARACTER ATTRIBUTES _ "+id + " = "+value);
			}
			System.out.println("CHARACTER PERSON & PROFESSION : "+personWithGenderAndRace+" "+profession);
			base.deactivate();
			currentPage=2;
			setupPage();
		}
		if (base.equals(readyChar))
		{
			if (foreName.text.length()==0) return true; // a name must be entered
			personWithGenderAndRace.professions.add(profession);
			personWithGenderAndRace.setAttributes(attributeValues);
			personWithGenderAndRace.setForeName(foreName.text);
			personWithGenderAndRace.setSureName(sureName.text);
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

	@Override
	public boolean inputLeft(InputBase base, String message) {
		if (base.equals(raceSelect))
		{
			System.out.println("RACE SELECT LEFT");
			MemberPerson race = cCR.raceInstances.get(cCR.selectableRaces.get(raceSelect.getSelection()));
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
				} else
				{
					attributeValues.setAttribute(id, baseValue);
				}
				System.out.println("ID = "+id+" = "+attributeValues.attributes.get(id));
				ValueTuner v = attributes.get(id);
				v.value = attributeValues.attributes.get(id);
				v.text = ""+v.value;
				v.deactivate();
			}
		} else
		if (base.equals(genderSelect))
		{
			MemberPerson race = cCR.raceInstances.get(cCR.selectableRaces.get(raceSelect.getSelection()));
			int i = genderSelect.getSelection();
			int id = Integer.parseInt(genderSelect.ids[i]);
			String genderPath = "";
			if (id==EntityDescription.GENDER_MALE) genderPath="male/";
			if (id==EntityDescription.GENDER_FEMALE) genderPath="female/";
			String path = "./data/portraits/"+race.pictureRoot+"/"+genderPath;
			pictureSelect.picturesPath = path;
			pictureSelect.setUpdated(true);
			pictureSelect.deactivate();
		}
		return true;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		if (base.equals(professionSelect))
		{
			for (String id:attributes.keySet())
			{
				ValueTuner v = attributes.get(id);
				int value = v.getSelection();
				attributeValues.setAttribute(id, value);
				System.out.println("CHARACTER ATTRIBUTES _ "+id + " = "+value);
			}
			ArrayList<String> ids = new ArrayList<String>();
			ArrayList<String> texts = new ArrayList<String>();
			
			int id = 0;
			for (Class<? extends Profession> pClass: cCR.selectableProfessions)
			{
				Profession p = cCR.profInstances.get(pClass);
				if (p.isQualifiedEnough(attributeValues))
				{
		    		String s = pClass.getSimpleName();
		    		ids.add(""+id);
		    		texts.add(s);
		    		id++;
				}
			}
	    	professionSelect.ids = ids.toArray(new String[0]);
	    	professionSelect.texts = texts.toArray(new String[0]);
	    	professionSelect.setUpdated(true);			
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
					q = loadImageToQuad(d.pic,  0.14f*core.getDisplay().getWidth(), (0.16f)*core.getDisplay().getHeight(),0.75f*core.getDisplay().getWidth(), (1f-0.2f)*core.getDisplay().getHeight()  );
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
