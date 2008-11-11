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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.UIImageCache;
import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.InventoryBody;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListMultiSelect;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.TextInputField;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.body.BodyPart;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.object.Ammunition;
import org.jcrpg.world.object.Armor;
import org.jcrpg.world.object.BonusObject;
import org.jcrpg.world.object.BonusSkillActFormDesc;
import org.jcrpg.world.object.EntityObjInventory;
import org.jcrpg.world.object.Equippable;
import org.jcrpg.world.object.InventoryListElement;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.PotionAndKit;
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

	Node page1_details = new Node();

	// page 0
	
	public ListSelect characterSelect;
	
	public ListMultiSelect weapons;
	public ListMultiSelect armors;
	public ListMultiSelect ammunitions;
	public ListMultiSelect potions;
	public ListMultiSelect keys;
	public ListMultiSelect books;
	public ListMultiSelect scrolls;
	public ListMultiSelect other;
	
	public ListMultiSelect equipped;
	
	public ArrayList<ListMultiSelect> selectors = new ArrayList<ListMultiSelect>();
	
	public ListSelect toCharacterSelect;
	public TextButton attach;
	public TextButton use;
	public TextButton equip;
	public TextInputField quantity;
	public TextButton give;
	public TextButton drop;
	
	public InventoryBody body;
	
	public EntityMemberInstance currentMember = null;
	
	// page 1
	
	public TextLabel detailObjectName;
	public TextLabel attachmentTypeName;
	public TextLabel rangeTypeName;
	public TextLabel weaponOrArmorDataText;
	public TextLabel bonusDataText;
	public TextLabel bonusDataTextActForm;
	public ListSelect bonusActFormList;
	public TextLabel equippableDataText;
	public TextLabel requiredSkillDataText;
	
	public TextButton back;
	
	public InventoryWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.75f*core.getDisplay().getWidth(), 1.7f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.10f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);

	    	sQuad = new SharedMesh("",hudQuad);
	    	page1_details.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.42f, 0.058f, 0.3f, 0.06f,400f,"Inventory",false);

    		characterSelect = new ListSelect("member", this,page0, 0.50f,0.11f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,characterSelect);

	    	new TextLabel("",this,page0, 0.30f, 0.15f, 0.3f, 0.06f,600f,"Weapons",false);
    		weapons = new ListMultiSelect("weapons", this,page0, 0.30f,0.18f,0.20f, 0.20f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0], new Quad[0], null,null);
	    	addInput(0,weapons);

	    	new TextLabel("",this,page0, 0.70f, 0.15f, 0.3f, 0.06f,600f,"Armors",false);
    		armors = new ListMultiSelect("armors", this,page0, 0.70f,0.58f,0.60f, 0.20f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,armors);

	    	new TextLabel("",this,page0, 0.30f, 0.25f, 0.3f, 0.06f,600f,"Ammunitions",false);
    		ammunitions = new ListMultiSelect("ammunitions", this,page0, 0.30f,0.18f,0.20f,0.30f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,ammunitions);

	    	new TextLabel("",this,page0, 0.70f, 0.25f, 0.3f, 0.06f,600f,"Potions, kits",false);
	    	potions = new ListMultiSelect("keys", this,page0, 0.70f,0.58f,0.60f, 0.30f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,potions);

	    	new TextLabel("",this,page0, 0.30f, 0.35f, 0.3f, 0.06f,600f,"Books",false);
    		books = new ListMultiSelect("books", this,page0, 0.30f,0.18f,0.20f ,0.40f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,books);

	    	new TextLabel("",this,page0, 0.70f, 0.35f, 0.3f, 0.06f,600f,"Scrolls",false);
    		scrolls = new ListMultiSelect("scrolls", this,page0, 0.70f,0.58f,0.60f, 0.40f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,scrolls);

	    	new TextLabel("",this,page0, 0.30f, 0.45f, 0.3f, 0.06f,600f,"Keys",false);
    		keys = new ListMultiSelect("keys", this,page0, 0.30f,0.18f,0.20f ,0.50f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,keys);

	    	new TextLabel("",this,page0, 0.70f, 0.45f, 0.3f, 0.06f,600f,"Other",false);
    		other = new ListMultiSelect("others", this,page0, 0.70f,0.58f,0.60f, 0.50f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,other);

	    	new TextLabel("",this,page0, 0.30f, 0.55f, 0.3f, 0.06f,600f,"Equipped",false);
    		equipped = new ListMultiSelect("equipped", this,page0, 0.30f,0.18f,0.20f,0.60f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],new Quad[0],null,null);
	    	addInput(0,equipped);

	    	body = new InventoryBody("body",this,page0,0.71f,0.69f, 0.3f, 0.06f, 700f);
	    	
	    	new TextLabel("",this,page0, 0.15f, 0.65f, 0.2f, 0.06f,600f,Language.v("inventory.quantity")+":",false); 
	    	quantity = new TextInputField("quantity",this,page0, 0.25f, 0.70f, 0.2f, 0.06f,600f,"",15,true);
	    	addInput(0,quantity);

	    	new TextLabel("",this,page0, 0.40f, 0.65f, 0.2f, 0.06f,600f,Language.v("inventory.toCharacter")+":",false); 
    		toCharacterSelect = new ListSelect("tomember", this,page0, 0.50f,0.70f,0.2f,0.06f,800f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,toCharacterSelect);

	    	use = new TextButton("use",this,page0, 0.18f, 0.78f, 0.07f, 0.07f,600f,Language.v("inventory.use"),"U");
	    	addInput(0,use);

	    	equip = new TextButton("equip",this,page0, 0.25f, 0.78f, 0.09f, 0.07f,600f,Language.v("inventory.equip"),"E");
	    	addInput(0,equip);
	    	
	    	attach = new TextButton("attach",this,page0, 0.35f, 0.78f, 0.09f, 0.07f,600f,Language.v("inventory.attach"),"A");
	    	addInput(0,attach);

	    	give = new TextButton("give",this,page0, 0.45f, 0.78f, 0.09f, 0.07f,600f,Language.v("inventory.give"),"G");
	    	addInput(0,give);

	    	drop = new TextButton("drop",this,page0, 0.55f, 0.78f, 0.09f, 0.07f,600f,Language.v("inventory.drop"));
	    	addInput(0,drop);
	    	
	    	addPage(0, page0);
	    	
	    	// DETAILS PAGE
	    	
	    	new TextLabel("",this,page1_details, 0.42f, 0.058f, 0.3f, 0.06f,400f,"Inventory",false);
	    	
	    	detailObjectName = new TextLabel("",this,page1_details, 0.30f, 0.15f, 0.3f, 0.06f,500f,"",false);
	    	attachmentTypeName = new TextLabel("",this,page1_details, 0.20f, 0.40f, 0.3f, 0.06f,700f,"",false);
	    	rangeTypeName = new TextLabel("",this,page1_details, 0.60f, 0.40f, 0.3f, 0.06f,700f,"",false);
	    	
	    	weaponOrArmorDataText = new TextLabel("",this,page1_details, 0.20f, 0.45f, 0.3f, 0.06f,700f,"",false);
	    	bonusDataText = new TextLabel("",this,page1_details, 0.20f, 0.50f, 0.3f, 0.06f,700f,"",false);
	    	
	    	bonusDataTextActForm = new TextLabel("",this,page1_details, 0.20f, 0.55f, 0.3f, 0.06f,700f,"Bonus Forms:",false);
	    	bonusActFormList = new ListSelect("list",this,page1_details, 0.57f, 0.55f, 0.50f, 0.06f,700f,new String[0], new String[0], new Object[0], null,null);
	    	addInput(1, bonusActFormList);

	    	equippableDataText = new TextLabel("",this,page1_details, 0.20f, 0.60f, 0.3f, 0.06f,700f,"",false);
	    	requiredSkillDataText = new TextLabel("",this,page1_details, 0.20f, 0.65f, 0.3f, 0.06f,700f,"",false);
	    	
	    	back = new TextButton("back",this,page1_details, 0.53f, 0.78f, 0.09f, 0.07f,600f,Language.v("inventory.back"));
	    	addInput(1,back);
	    	
	    	addPage(1, page1_details);
	    	
	    	selectors.add(weapons);
	    	selectors.add(armors);
	    	selectors.add(ammunitions);
	    	selectors.add(potions);
	    	selectors.add(books);
	    	selectors.add(scrolls);
	    	selectors.add(keys);
	    	selectors.add(other);
	    	
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
	
	public int lastUpdatedLivingPartySize = 0;
	private ArrayList<EntityMemberInstance> tmpFilteredMembers = new ArrayList<EntityMemberInstance>();
	private void updateToParty()
	{
		int livingMembersCounter = 0;
		boolean foundCurrent = false;
		tmpFilteredMembers.clear();
		for (EntityMemberInstance i : party.orderedParty)
		{
			if (!i.memberState.isDead())
			{
				livingMembersCounter++;
				if (i == currentMember)
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
		}
		else
		{
			characterSelect.setSelected(currentMember);
		}
		updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
	}
	
	private void updateToMemberInstance(EntityMemberInstance instance)
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
	
	
	
	private void fillSelect(ListMultiSelect selectList, ArrayList<InventoryListElement> list)
	{
		{
			String[] ids = new String[list.size()];
			Object[] objects = new Object[list.size()];
			String[] texts = new String[list.size()];
			Quad[] icons = new Quad[list.size()];
			int counter = 0;
			for (InventoryListElement objEl:list) {
				ids[counter] = ""+counter;
				objects[counter] = objEl;
				texts[counter] = objEl.getName();
				try {
					icons[counter] = UIImageCache.getImage(objEl.description.getIconFilePath(), true,15f);
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
				counter++;
			}
			selectList.reset();
			selectList.ids = ids;
			selectList.objects = objects;
			selectList.texts = texts;
			selectList.icons = icons;
			selectList.setUpdated(true);
			selectList.deactivate();
		}
		
	}
	
	private HashMap<Obj, InventoryListElement> hmGroupableDescToElement = new HashMap<Obj, InventoryListElement>();
	private ArrayList<InventoryListElement> listNotGroupable = new ArrayList<InventoryListElement>();
	
	
	public ArrayList<InventoryListElement> tmpWeaponsList = new ArrayList<InventoryListElement>();
	public ArrayList<InventoryListElement> tmpAmmunitionList = new ArrayList<InventoryListElement>();
	public ArrayList<InventoryListElement> tmpArmorList = new ArrayList<InventoryListElement>();
	public ArrayList<InventoryListElement> tmpPotionList = new ArrayList<InventoryListElement>();
	public ArrayList<InventoryListElement> tmpOtherList = new ArrayList<InventoryListElement>();
	
	public ArrayList<InventoryListElement> tmpEquippedList = new ArrayList<InventoryListElement>();
	
	
	private void updateToInventory(EntityObjInventory inventory)
	{
		body.updateToEntityMemberInstance(currentMember);
		hmGroupableDescToElement.clear();
		listNotGroupable.clear();
		
		tmpWeaponsList.clear();
		tmpAmmunitionList.clear();
		tmpArmorList.clear();
		tmpPotionList.clear();
		tmpOtherList.clear();
		
		tmpEquippedList.clear();

		for (ObjInstance o:inventory.getEquipped())
		{
			InventoryListElement list = new InventoryListElement(currentMember.inventory,o.description);
			list.objects.add(o);
			tmpEquippedList.add(list);
		}
		fillSelect(equipped, tmpEquippedList);
		
		for (ObjInstance o:inventory.getInventory())
		{
			if (o.description.isGroupable())
			{
		
				InventoryListElement list = hmGroupableDescToElement.get(o.description);
				if (list==null)
				{
					list = new InventoryListElement(currentMember.inventory,o.description);
					hmGroupableDescToElement.put(o.description, list);
				}
				list.objects.add(o);
			} else
			{
				InventoryListElement list = new InventoryListElement(currentMember.inventory,o.description);
				list.objects.add(o);
				listNotGroupable.add(list);
			}
		}
		
		listNotGroupable.addAll(hmGroupableDescToElement.values());
		
		for (InventoryListElement o:listNotGroupable)
		{
			if (o.description instanceof Weapon)
			{
				tmpWeaponsList.add(o);
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("InvWindow WEA: "+o.description);
			} else
			if (o.description instanceof Ammunition)
			{
				tmpAmmunitionList.add(o);
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("InvWindow AMM: "+o.description);
			} else
			if (o.description instanceof Armor)
			{
				tmpArmorList.add(o);
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("InvWindow ARM: "+o.description);
			} else
			if (o.description instanceof PotionAndKit)
			{
				tmpPotionList.add(o);
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("InvWindow POT: "+o.description);
			} else
			{
				tmpOtherList.add(o);
			}
		}
		
		fillSelect(weapons, tmpWeaponsList);
		fillSelect(ammunitions, tmpAmmunitionList);
		fillSelect(armors, tmpArmorList);
		fillSelect(potions, tmpPotionList);
		fillSelect(other, tmpOtherList);
	}
	

	
	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==equip && canDoActions)
		{
			// equipping
			
			ArrayList<InventoryListElement> lists = getAllSelection();
			// equipping selected items
			for (InventoryListElement l:lists)
			{
				if (l.description instanceof Equippable)
				{
					boolean success = currentMember.equip(l.objects.get(0));
					if (success)
					{
						core.uiBase.hud.mainBox.addEntry("Equipped "+l.getSingleName()+".");
					}
				}
			}
			// unequipping selected items
			ArrayList<Object> equippedList = equipped.getMultiSelection();
			for (Object l:equippedList)
			{
				for (ObjInstance i:((InventoryListElement)l).objects)
				{
					if (currentMember.unequip(i))
					{
						core.uiBase.hud.mainBox.addEntry("Unequipped "+i.description.getName()+".");
					} else
					{
						core.uiBase.hud.mainBox.addEntry("Can't unequip "+i.description.getName()+".");
					}
				}
			}
			updateToInventory(currentMember.inventory);
			return true;
		} else
		if (base==characterSelect)
		{
			// updating to selected character
			characterSelect.deactivate();
			updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
			return true;
		}
		else
		if (base==attach && canDoActions)
		{
			// attaching ammunition to weapons, detach if no ammunition is selected.
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
									if (aOI.description.isIdentifiableAs(wOI.description.getAttachmentDependencyType()))
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
		if (base==drop && canDoActions)
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
					currentMember.inventory.remove(oI);
					updateNeeded = true;
				}
			}
			if (updateNeeded) updateToInventory(currentMember.inventory);
		} else
		if (base==give && canDoActions)
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
					currentMember.inventory.remove(oI);
					toChar.inventory.add(oI);
					updateNeeded = true;
				}
			}
			if (updateNeeded) updateToInventory(currentMember.inventory);
		} else
		if (selectors.contains(base) && "space".equals(message))
		{
			Object o = ((ListMultiSelect)base).getSelectedObject();
			if (o!=null)
			{
				lastUsedBeforeDetail = base;
				currentPage=1;
				updateDetailPageToInvListElement((InventoryListElement)o);
				setupPage();
			}
		} else
		if (base == back)
		{
			goBackToFirstPage();
		}
		return super.inputUsed(base, message);
	}
	private void goBackToFirstPage()
	{
		currentPage=0;
		setupPage();
		setSelected(lastUsedBeforeDetail);
	}
	
	InputBase lastUsedBeforeDetail = null;
	
	Quad currentDetailQuad = null;
	
	/**
	 * Updates detail page to a selected inventory element.
	 * @param element
	 */
	private void updateDetailPageToInvListElement(InventoryListElement element)
	{
		if (currentDetailQuad!=null) currentDetailQuad.removeFromParent();
		detailObjectName.text = element.getSingleName();
		detailObjectName.activate();
		
		String attTypeName = "Attaching: -";
		if (element.description.getAttachableToType()!=null)
			attTypeName+="Attachable To: "+element.description.getAttachableToType().getSimpleName();
		if (element.description.getAttachmentDependencyType()!=null)
			attTypeName+="Attachment: "+element.description.getAttachmentDependencyType().getSimpleName();
		attachmentTypeName.text = attTypeName;
		attachmentTypeName.activate();
		
		int range = element.description.getUseRangeInLineup();
		String rangeTypeText = "Range: "+(range-1)+". line"; 
		if (range==Obj.NO_RANGE)
		{
			rangeTypeText = "Range: Unlimited";
		}
		rangeTypeName.text = rangeTypeText;
		rangeTypeName.activate();
		
		String armorOrWeaponText = "Not armor/weapon.";
		if (element.description instanceof Armor)
		{
			Armor a = (Armor)element.description;
			armorOrWeaponText = "Armor Defense/Hit Decrease: "+a.getDefenseValue()+"/"+a.getHitPointImpactDecrease();
		} else
		if (element.description instanceof Weapon)
		{
			Weapon w = (Weapon)element.description;
			armorOrWeaponText = "Weapon Atk/Def/Spd: "+(int)(w.getAttackMultiplicator()*100)+"% / "+""+(int)(w.getDefenseMultiplicator()*100)+"% / "+w.getSpeed();
		}
		weaponOrArmorDataText.text = armorOrWeaponText;
		weaponOrArmorDataText.activate();
		
		String bonusText = "No Bonus.";
		if (element.description instanceof BonusObject)
		{
			BonusObject b = (BonusObject)element.description;
			Attributes a = b.getAttributeValues();
			Resistances r = b.getResistanceValues();
			bonusText = "Artifact Bonus: ";
			if (a!=null) {
				for (String attrName :a.attributes.keySet())
				{
					if (a.attributes.get(attrName)!=null && a.attributes.get(attrName)>0)
					bonusText+=a.getShortestName(attrName)+" "+a.getAttribute(attrName)+" ";
				}
			}
			if (r!=null) {
				for (String resName :r.resistances.keySet())
				{
					if (r.resistances.get(resName)!=null && r.resistances.get(resName)>0)
					bonusText+=r.getShortestName(resName)+" "+r.getResistance(resName)+" ";
				}
			}
			
			ArrayList<BonusSkillActFormDesc> bonusForms = b.getSkillActFormBonusEffectTypes();

			String[] ids = new String[bonusForms==null?0:bonusForms.size()];
			String[] texts = new String[bonusForms==null?0:bonusForms.size()];
			
			int count = 0;
			ArrayList<BonusSkillActFormDesc> usableForms = element.objects.get(0).currentlyUsableBonusSkillActForms();
			if (bonusForms!=null)
			{
				for (BonusSkillActFormDesc form:bonusForms)
				{
					String text = ""+form.form.getName()+" Replenish: "+form.replenishFrequency+" Level: "+form.skillLevel+" Use Times: "+form.maxUsePerReplenish;
					if (usableForms.contains(form)) text+=" (R)";
					texts[count] = text;
					ids[count] = ""+count;
					count++;
				}
			}
			bonusActFormList.reset();
			bonusActFormList.ids = ids;
			bonusActFormList.texts = texts;
			bonusActFormList.setUpdated(true);
			bonusActFormList.deactivate();
			
		}
		bonusDataText.text = bonusText;
		bonusDataText.activate();
		
		String equippableText = "Not equippable.";
		if (element.description instanceof Equippable)
		{
			Equippable eq = (Equippable)element.description;
			equippableText ="Equippable: "+
				BodyPart.getName(eq.getEquippableBodyPart());
		}
		equippableDataText.text = equippableText;
		equippableDataText.activate();
		
		String skillRequirement = "No skill required.";
		if (element.description.requirementSkillAndLevel!=null)
		{
			skillRequirement="Skill req.:" + element.description.requirementSkillAndLevel.getSkill().getName()+" ("+element.description.requirementSkillAndLevel.level+")";
		}
		requiredSkillDataText.text = skillRequirement;
		requiredSkillDataText.activate();
		
		//String bonusSkillActFormText
			
		//attachmentTypeName = element.description.getAttacheableToType()==null?
		
		try {
			currentDetailQuad = loadImageToQuad(element.description.getIconFilePath(), 0.17f*core.getDisplay().getWidth(), 0.20f*(core.getDisplay().getHeight()),
				core.getDisplay().getWidth() / 2, 1.45f*core.getDisplay().getHeight() / 2);
			page1_details.attachChild(currentDetailQuad);
		} catch (Exception ex)
		{
			
		}
    			

	}
	
	@Override
	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		if (key.equals("back"))
		{
			if (currentPage==1)
			{
				goBackToFirstPage();
			}
		}
		return true;
	}
	
	private ArrayList<InventoryListElement> getAllSelection()
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
		if (fallbackWindow!=null) {
			fallbackWindow.toggle();
			// setting back these values.
			core.getKeyboardHandler().noToggleWindowByKey=true;
			fallbackWindow = null;
			canDoActions = true;
		}
	}
	@Override
	public void show() {
		super.show();
		updateToParty();
	}
	/**
	 * fallback window (for example in turn act, it should be turn act window)
	 */
	public InputWindow fallbackWindow = null;
	/**
	 * Determines if inventory actions can be done - in turn act window it should be set to false;
	 */
	public boolean canDoActions = true;
	
}
