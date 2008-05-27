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

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.UIImageCache;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListMultiSelect;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.object.EntityObjInventory;
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
	public ListMultiSelect potions;
	public ListMultiSelect keys;
	public ListMultiSelect books;
	public ListMultiSelect scrolls;
	public ListMultiSelect other;
	
	public InventoryWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.8f*core.getDisplay().getWidth(), 1.7f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.10f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.42f, 0.058f, 0.3f, 0.06f,400f,"Inventory",false);

    		characterSelect = new ListSelect("member", this,page0, 0.50f,0.11f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,characterSelect);

	    	new TextLabel("",this,page0, 0.30f, 0.15f, 0.3f, 0.06f,600f,"Weapons",false);
    		weapons = new ListMultiSelect("weapons", this,page0, 0.30f,0.18f,0.20f, 0.20f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0], new SharedMesh[0], null,null);
	    	addInput(0,weapons);

	    	new TextLabel("",this,page0, 0.70f, 0.15f, 0.3f, 0.06f,600f,"Armors",false);
    		armors = new ListMultiSelect("armors", this,page0, 0.70f,0.58f,0.20f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,armors);

	    	new TextLabel("",this,page0, 0.30f, 0.25f, 0.3f, 0.06f,600f,"Potions, kits",false);
    		potions = new ListMultiSelect("potions", this,page0, 0.30f,0.10f,0.30f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,potions);

	    	new TextLabel("",this,page0, 0.70f, 0.25f, 0.3f, 0.06f,600f,"Keys",false);
    		keys = new ListMultiSelect("keys", this,page0, 0.70f,0.55f,0.30f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,keys);

	    	new TextLabel("",this,page0, 0.30f, 0.35f, 0.3f, 0.06f,600f,"Books",false);
    		books = new ListMultiSelect("books", this,page0, 0.30f,0.10f,0.40f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,books);

	    	new TextLabel("",this,page0, 0.70f, 0.35f, 0.3f, 0.06f,600f,"Scrolls",false);
    		scrolls = new ListMultiSelect("scrolls", this,page0, 0.70f,0.55f,0.40f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,scrolls);

	    	new TextLabel("",this,page0, 0.30f, 0.45f, 0.3f, 0.06f,600f,"Other",false);
    		other = new ListMultiSelect("others", this,page0, 0.30f,0.10f,0.50f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,other);

	    	addPage(0, page0);
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
			if (i.memberState.healthPoint>=0)
			{
				// living, TODO tune to better state values
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
		updateToInventory(instance.inventory);
	}
	
	
	public ArrayList<ObjInstance> tmpWeaponsList = new ArrayList<ObjInstance>();
	
	public void updateToInventory(EntityObjInventory inventory)
	{
		
		int weaponCount = 0; tmpWeaponsList.clear();
		// TODO other counts
		
		for (ObjInstance o:inventory.inventory)
		{
			if (o.description instanceof Weapon)
			{
				weaponCount++;
				tmpWeaponsList.add(o);
				System.out.println(o.description);
			}
		}

		{
			String[] ids = new String[weaponCount];
			Object[] objects = new Object[weaponCount];
			String[] texts = new String[weaponCount];
			SharedMesh[] icons = new SharedMesh[weaponCount];
			int counter = 0;
			for (ObjInstance weapon:tmpWeaponsList) {
				ids[counter] = ""+counter;
				objects[counter] = weapon;
				texts[counter] = weapon.description.getClass().getSimpleName();
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

	
	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==characterSelect)
		{
			characterSelect.deactivate();
			updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
			return true;
		}
		return super.inputUsed(base, message);
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
