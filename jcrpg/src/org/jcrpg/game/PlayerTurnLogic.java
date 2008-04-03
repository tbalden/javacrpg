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

package org.jcrpg.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.jcrpg.space.Cube;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.StickingOut;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PreEncounterInfo;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;

/**
 * Class for per turn encounter management.
 * @author pali
 *
 */
public class PlayerTurnLogic {
	
	public HashSet<PreEncounterInfo> previousInfos = new HashSet<PreEncounterInfo>();
	public HashSet<PreEncounterInfo> infos = new HashSet<PreEncounterInfo>();
	
	
	public Collection<VisibleLifeForm> previousForms = new ArrayList<VisibleLifeForm>();
	public Collection<VisibleLifeForm> forms = new ArrayList<VisibleLifeForm>();
	
	public World world;
	public Ecology ecology;
	public EntityInstance player;
	public Engine engine;
	public transient J3DCore core;
	
	public PlayerTurnLogic(J3DCore core, Engine engine, World world, Ecology ecology, EntityInstance player)
	{
		this.core = core;
		this.world = world;
		this.ecology = ecology;
		this.player = player;
		this.engine = engine;
	}
	
	
	public void newTurn(Collection<PreEncounterInfo> possibleEncounters, int startingPhase, boolean playerInitiated)
	{
		if (!J3DCore.DEMO_ENCOUTNER_MODE) {

			if (startingPhase==Ecology.PHASE_INTERCEPTION)
			{
				core.switchEncounterMode(true);
				core.preEncounterWindow.setPageData(core.gameState.player, possibleEncounters);
				core.preEncounterWindow.toggle();
			}
			
			if (startingPhase==Ecology.PHASE_ENCOUNTER)
			{
				J3DCore.getInstance().switchEncounterMode(false);
				//encounter(possibleEncounters);
			}
		} else 
		{
			encounter(possibleEncounters);
		}
	}
	
	
	public void encounter(Collection<PreEncounterInfo> possibleEncounters) 
	{
		
		previousInfos.clear();
		previousInfos.addAll(infos);
		infos.addAll(possibleEncounters);
		previousForms.addAll(forms);
		forms.clear();
		VisibleLifeForm playerFakeForm = new VisibleLifeForm("player",null,null,null);
		playerFakeForm.worldX = player.roamingBoundary.posX;
		playerFakeForm.worldY = player.roamingBoundary.posY;
		playerFakeForm.worldZ = player.roamingBoundary.posZ;
		HashSet<String> playedAudios = new HashSet<String>();
		int sizeOfAll = 0;
		for (PreEncounterInfo info:infos)
		{
			if (info.subject==null) continue;
			for (EntityInstance entityInstance:info.encountered.keySet()) {
				if (entityInstance==player) continue;
				int[] groupIds = info.encounteredGroupIds.get(entityInstance);
				if (groupIds.length>0)
					ecology.callbackMessage("Facing an *ENCOUNTER* : "+entityInstance.description.getClass().getSimpleName()+ " "+entityInstance.id +" g:"+(groupIds!=null?groupIds.length:null));
				else
					ecology.callbackMessage("You seem to trespass a Domain : "+entityInstance.description.getClass().getSimpleName());
				System.out.println("GROUP ID = "+(groupIds!=null?groupIds.length:null)+" "+groupIds);
				boolean played = false;
				if (groupIds !=null)
				for (int in:groupIds)
				{
					int size = entityInstance.groupSizes[in];
					Collection<EntityMemberInstance> members = entityInstance.description.groupingRule.getGroup(entityInstance,in,size);
					String types = "";
					HashSet<String> typesSet = new HashSet<String>();
					for (EntityMemberInstance mInst:members)
					{
						typesSet.add(mInst.description.visibleTypeId);
					}
					for (String type:typesSet)
					{
						types+=","+type;
					}
					ecology.callbackMessage(""+size+" "+types);
					for (EntityMemberInstance member:members)
					{
						if (!played) 
						{
							if (member.description.audioDescription!=null && member.description.audioDescription.ENCOUNTER!=null && member.description.audioDescription.ENCOUNTER.length>0) {
								if (!playedAudios.contains(member.description.audioDescription.ENCOUNTER[0])) {
									core.audioServer.playLoading(member.description.audioDescription.ENCOUNTER[0], "ai");
									playedAudios.add(member.description.audioDescription.ENCOUNTER[0]);
									played = true;
								}
							}
						}
						VisibleLifeForm form = entityInstance.getOne(member.description,member);
						form.targetForm = playerFakeForm;
						forms.add(form);
						sizeOfAll++;
					}
				}
			}
		}
		if (sizeOfAll>0) {
			placeVisibleForms(forms);
			J3DCore.getInstance().mEngine.render(forms);
			core.switchEncounterMode(true);
		} else
		{
			core.gameState.engine.turnFinishedForPlayer();
		}
	}
	
	public void endPlayerEncounters()
	{
		J3DCore.getInstance().mEngine.clearPreviousUnits();
		
	}
	
	public void placeVisibleForms(Collection<VisibleLifeForm> forms)
	{
		int dir = core.gameState.viewDirection;
		int[] trans = J3DCore.moveTranslations.get(dir);
		HashSet<Integer> usedPositions = new HashSet<Integer>();
		for (VisibleLifeForm form:forms)
		{
			boolean found = true;
			int i=0;
			Cube c = null;
			while (true) { 
				if (i>15) {
					form.worldX = core.gameState.viewPositionX+(i/3+2)*trans[0]+(((i%3)-1)*trans[2]);
					form.worldY = core.gameState.viewPositionY;
					form.worldZ = core.gameState.viewPositionZ+(i/3+2)*trans[2]+(((i%3)-1)*trans[0]);
					form.notRendered = true;
					found=false; break;
				}
				form.worldX = core.gameState.viewPositionX+(i/3+2)*trans[0]+(((i%3)-1)*trans[2]);
				form.worldY = core.gameState.viewPositionY;
				form.worldZ = core.gameState.viewPositionZ+(i/3+2)*trans[2]+(((i%3)-1)*trans[0]);
				c = world.getCube(-1, form.worldX, form.worldY, form.worldZ, false);
				if (!c.canContain) 
				{
					i++;
					continue;
				}
				if (c==null || core.hasSideOfInstance(c.getSide(J3DCore.BOTTOM), stickingOut))
				{
					i++; continue;
				}
				boolean land = form.entity.description.isLandDweller();
				boolean water = form.entity.description.isWaterDweller();
				boolean air = form.entity.description.isAirDweller();
				boolean indoor = form.entity.description.isIndoorDweller();
				boolean outdoor= form.entity.description.isOutdoorDweller();
				if (air && !usedPositions.contains(i)) break;
				if (c.internalCube && !indoor)
				{
					i++;
					continue;
				}
				if (!c.internalCube && !outdoor)
				{
					i++;
					continue;
				}
				if (water && c.waterCube && !usedPositions.contains(i))
				{
					//System.out.println("FOUND WATER"+c);
					break;
				}
				if (land && !c.waterCube && !usedPositions.contains(i)) 
				{
					//System.out.println("FOUND LAND "+c);
					break;
				}
				i++;
			}
			if (found)
			{
				if (c.steepDirection!=SurfaceHeightAndType.NOT_STEEP || core.hasSideOfInstanceInAnyDir(c, steepSides)!=null)
				{
					form.onSteep = true;
				}
				usedPositions.add(i);
			}
		}
	}
	
	static HashSet<Class<? extends SideSubType>> steepSides = new HashSet<Class<? extends SideSubType>>();
	static {
		steepSides.add(Climbing.class);
	}
	static HashSet<Class<? extends SideSubType>> stickingOut = new HashSet<Class<? extends SideSubType>>();
	static {
		stickingOut.add(StickingOut.class);
	}
	
	

}
