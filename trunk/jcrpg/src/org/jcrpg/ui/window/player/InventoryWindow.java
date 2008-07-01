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
package org.jcrpg.ui.window.player;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.UIImageCache;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListMultiSelect;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.TextInputField;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.object.Ammunition;
import org.jcrpg.world.object.EntityObjInventory;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.Weapon;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Player's party character's inventory window.
 * @author illes
 *
 */
public class InventoryWindow extends PagedInputWindow {

	
	Node page0 = new Node();
	
	public ListSelect characterSelect;
	
	public ListMultiSelect weapons;
	public ListMultiSelect armors;
	public ListMultiSelect ammunitions;
	public ListMultiSelect potions;
	public ListMultiSelect keys;
	public ListMultiSelect books;
	public ListMultiSelect scrolls;
	public ListMultiSelect other;
	
	public ArrayList<ListMultiSelect> selectors = new ArrayList<ListMultiSelect>();
	
	public ListSelect toCharacterSelect;
	public TextButton attach;
	public TextInputField quantity;
	public TextButton give;
	public TextButton drop;
	
	public EntityMemberInstance currentMember = null;
	
	
	
	public InventoryWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.75f*core.getDisplay().getWidth(), 1.7f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.10f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.42f, 0.058f, 0.3f, 0.06f,400f,"Inventory",false);

    		characterSelect = new ListSelect("member", this,page0, 0.50f,0.11f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,characterSelect);

	    	new TextLabel("",this,page0, 0.30f, 0.15f, 0.3f, 0.06f,600f,"Weapons",false);
    		weapons = new ListMultiSelect("weapons", this,page0, 0.30f,0.18f,0.20f, 0.20f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0], new Quad[0], null,null);
	    	addInput(0,weapons);

	    	new TextLabel("",this,page0, 0.70f, 0.15f, 0.3f, 0.06f,600f,"Armors",false);
    		armors = new ListMultiSelect("armors", this,page0, 0.70f,0.58f,0.20f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,armors);

	    	new TextLabel("",this,page0, 0.30f, 0.25f, 0.3f, 0.06f,600f,"Ammunitions",false);
    		ammunitions = new ListMultiSelect("ammunitions", this,page0, 0.30f,0.18f,0.30f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,ammunitions);

	    	new TextLabel("",this,page0, 0.70f, 0.25f, 0.3f, 0.06f,600f,"Potions, kits",false);
	    	potions = new ListMultiSelect("keys", this,page0, 0.70f,0.55f,0.30f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,potions);

	    	new TextLabel("",this,page0, 0.30f, 0.35f, 0.3f, 0.06f,600f,"Books",false);
    		books = new ListMultiSelect("books", this,page0, 0.30f,0.10f,0.40f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,books);

	    	new TextLabel("",this,page0, 0.70f, 0.35f, 0.3f, 0.06f,600f,"Scrolls",false);
    		scrolls = new ListMultiSelect("scrolls", this,page0, 0.70f,0.55f,0.40f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,scrolls);

	    	new TextLabel("",this,page0, 0.30f, 0.45f, 0.3f, 0.06f,600f,"Keys",false);
    		keys = new ListMultiSelect("keys", this,page0, 0.30f,0.18f,0.50f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,keys);

	    	new TextLabel("",this,page0, 0.70f, 0.45f, 0.3f, 0.06f,600f,"Other",false);
    		other = new ListMultiSelect("others", this,page0, 0.70f,0.55f,0.50f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,other);
	    	
	    	new TextLabel("",this,page0, 0.15f, 0.65f, 0.2f, 0.06f,600f,Language.v("inventory.quantity")+":",false); 
	    	quantity = new TextInputField("quantity",this,page0, 0.25f, 0.70f, 0.2f, 0.06f,600f,"",15,true);
	    	addInput(0,quantity);

	    	new TextLabel("",this,page0, 0.40f, 0.65f, 0.2f, 0.06f,600f,Language.v("inventory.toCharacter")+":",false); 
    		toCharacterSelect = new ListSelect("tomember", this,page0, 0.50f,0.70f,0.2f,0.06f,800f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,toCharacterSelect);
	    	
	    	attach = new TextButton("attach",this,page0, 0.23f, 0.78f, 0.13f, 0.07f,600f,Language.v("inventory.attach"));
	    	addInput(0,attach);

	    	give = new TextButton("give",this,page0, 0.38f, 0.78f, 0.13f, 0.07f,600f,Language.v("inventory.give"));
	    	addInput(0,give);

	    	drop = new TextButton("drop",this,page0, 0.53f, 0.78f, 0.13f, 0.07f,600f,Language.v("inventory.drop"));
	    	addInput(0,drop);
	    	
	    	addPage(0, page0);
	    	
	    	selectors.add(weapons);
	    	selectors.add(armors);
	    	selectors.add(ammunitions);
	    	selectors.add(potions);
	    	selectors.add(books);
	    	selectors.add(scrolls);
	    	selectors.add(keys);
	    	selectors.add(other);
	    	
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
	
	public int lastUpdatedLivingPartySize = 0;
	private ArrayList<EntityMemberInstance> tmpFilteredMembers = new ArrayList<EntityMemberInstance>();
	public void updateToParty()
	{
		int livingMembersCounter = 0;
		boolean foundCurrent = false;
		tmpFilteredMembers.clear();
		for (EntityMemberInstance i : party.orderedParty)
		{
			if (!i.memberState.isDead())
			{
				livingMembersCounter++;
				if (i == characterSelect.getSelectedObject())
				{
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
			updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
		}
	}
	
	public void updateToMemberInstance(EntityMemberInstance instance)
	{
		int livingMembersCounter = 0;
		tmpFilteredMembers.clear();
		for (EntityMemberInstance i : party.orderedParty)
		{
			if (!i.memberState.isDead())
			{
				if (i!=instance)
				{
					livingMembersCounter++;
					tmpFilteredMembers.add(i);
				}
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
			toCharacterSelect.reset();
			toCharacterSelect.ids = ids;
			toCharacterSelect.objects = objects;
			toCharacterSelect.texts = texts;
			toCharacterSelect.setUpdated(true);
			toCharacterSelect.deactivate();
		}
		currentMember = instance;
		
		updateToInventory(instance.inventory);
	}
	
	
	public ArrayList<InventoryListElement> tmpWeaponsList = new ArrayList<InventoryListElement>();
	public ArrayList<InventoryListElement> tmpAmmunitionList = new ArrayList<InventoryListElement>();
	
	
	public void fillSelect(ListMultiSelect weapons, ArrayList<InventoryListElement> list)
	{
		{
			String[] ids = new String[list.size()];
			Object[] objects = new Object[list.size()];
			String[] texts = new String[list.size()];
			Quad[] icons = new Quad[list.size()];
			int counter = 0;
			for (InventoryListElement weapon:list) {
				ids[counter] = ""+counter;
				objects[counter] = weapon;
				texts[counter] = weapon.description.getName() + " " + weapon.objects.size();
				try {
					icons[counter] = UIImageCache.getImage("./data/icons/objects/"+weapon.description.icon, true,15f);
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
				counter++;
			}
			weapons.reset();
			weapons.ids = ids;
			weapons.objects = objects;
			weapons.texts = texts;
			weapons.icons = icons;
			weapons.setUpdated(true);
			weapons.deactivate();
		}
		
	}
	
	public class InventoryListElement
	{
		
		Obj description = null;
		
		public InventoryListElement(Obj description) 
		{
			this.description = description;
		}
		
		ArrayList<ObjInstance> objects = new ArrayList<ObjInstance>();
		
	}
	private HashMap<Obj, InventoryListElement> hmDescToElement = new HashMap<Obj, InventoryListElement>();
	
	
	public void updateToInventory(EntityObjInventory inventory)
	{
		hmDescToElement.clear();
		
		tmpWeaponsList.clear();
		tmpAmmunitionList.clear();
		
		for (ObjInstance o:inventory.inventory)
		{
		
			InventoryListElement list = hmDescToElement.get(o.description);
			if (list==null)
			{
				list = new InventoryListElement(o.description);
				hmDescToElement.put(o.description, list);
			}
			list.objects.add(o);
		}
		
		for (InventoryListElement o:hmDescToElement.values())
		{
			if (o.description instanceof Weapon)
			{
				tmpWeaponsList.add(o);
				System.out.println("WEA: "+o.description);
			} else
			if (o.description instanceof Ammunition)
			{
				tmpAmmunitionList.add(o);
				System.out.println("AMM: "+o.description);
			}
		}
		
		fillSelect(weapons, tmpWeaponsList);
		fillSelect(ammunitions, tmpAmmunitionList);

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
		if (base==attach)
		{
			ArrayList<Object> listWeapon = weapons.getMultiSelection();
			ArrayList<Object> listAmmunition = ammunitions.getMultiSelection();
			if (listWeapon.size()>=1)
			{
				if (listAmmunition.size()>0)
				{
					for (Object weaponsO:listWeapon)
					{
						InventoryListElement weapons = (InventoryListElement)weaponsO;
						for (Object o:listAmmunition)
						{
							InventoryListElement ammunitions = (InventoryListElement)o;
							for (ObjInstance wOI:weapons.objects)
							{
								for (ObjInstance aOI:ammunitions.objects)
								{
									if (aOI.description.isAttacheableAs(wOI.description.getAttachmentDependencyType()))
									{
										wOI.addAttachedDependency(aOI.description);
										core.uiBase.hud.mainBox.addEntry("Attach "+aOI.description.getName()+" to "+wOI.description.getName());
									}
								}
							}
						}
					}
				} else
				{
					for (Object weaponsO:listWeapon)
					{
						InventoryListElement weapons = (InventoryListElement)weaponsO;
						for (ObjInstance wOI:weapons.objects)
						{
							wOI.clearDependencies();
							core.uiBase.hud.mainBox.addEntry("Clear attached for "+wOI.description.getName());
						}
					}
				}
			}
			return true;
		} else
		if (base==drop)
		{
			
			ArrayList<InventoryListElement> l = getAllSelection();
			String vS = quantity.getValue();
			int q = -1;
			if (vS!=null && vS.length()>0)
			{
				try {
					q = Integer.parseInt(vS);
				} catch (Exception e){};
			}
			boolean useQuantity = q!=-1&&q!=0;
			boolean updateNeeded = false;
			for (InventoryListElement e:l)
			{
				int count = 0;
				for (ObjInstance oI:e.objects)
				{
					if (useQuantity && count>q) break;
					currentMember.inventory.equipped.remove(oI);
					currentMember.inventory.inventory.remove(oI);
					updateNeeded = true;
				}
			}
			if (updateNeeded) updateToInventory(currentMember.inventory);
		} else
		if (base==give)
		{
			EntityMemberInstance toChar = (EntityMemberInstance)toCharacterSelect.getSelectedObject();
			if (toChar==null || toChar.memberState.isDead() || toChar == currentMember) return true;
			
			
			ArrayList<InventoryListElement> l = getAllSelection();
			String vS = quantity.getValue();
			int q = -1;
			if (vS!=null && vS.length()>0)
			{
				try {
					q = Integer.parseInt(vS);
				} catch (Exception e){};
			}
			boolean useQuantity = q!=-1&&q!=0;
			boolean updateNeeded = false;
			for (InventoryListElement e:l)
			{
				int count = 0;
				for (ObjInstance oI:e.objects)
				{
					if (useQuantity && count>q) break;
					currentMember.inventory.equipped.remove(oI);
					currentMember.inventory.inventory.remove(oI);
					toChar.inventory.inventory.add(oI);
					updateNeeded = true;
				}
			}
			if (updateNeeded) updateToInventory(currentMember.inventory);
		}
		return super.inputUsed(base, message);
	}
	
	public ArrayList<InventoryListElement> getAllSelection()
	{
		ArrayList<InventoryListElement> r = new ArrayList<InventoryListElement>();
		for (ListMultiSelect s:selectors)
		{
			ArrayList<Object> l = s.getMultiSelection();
			for (Object o:l)
			{
				r.add((InventoryListElement)o);
			}
		}
		return r;
	}

	@Override
	public void hide() {
		super.hide();
	}
	@Override
	public void show() {
		super.show();
		updateToParty();
	}
	
}
