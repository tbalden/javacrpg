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

import org.jcrpg.game.element.PlacementMatrix;
import org.jcrpg.space.Cube;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.StickingOut;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnitData;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;

/**
 * Class for per turn encounter management.
 * @author pali
 *
 */
public class GameLogic {
	
	
	
	public transient HashSet<EncounterInfo> previousInfos = new HashSet<EncounterInfo>();
	public transient HashSet<EncounterInfo> infos = new HashSet<EncounterInfo>();
	
	
	public Collection<VisibleLifeForm> previousForms = new ArrayList<VisibleLifeForm>();
	public Collection<VisibleLifeForm> forms = new ArrayList<VisibleLifeForm>();
	
	public World world;
	public Ecology ecology;
	public PartyInstance player;
	public Engine engine;
	public EncounterLogic encounterLogic;
	public transient J3DCore core;
	
	public GameLogic(J3DCore core, Engine engine, World world, Ecology ecology, PartyInstance player)
	{
		this.core = core;
		this.world = world;
		this.ecology = ecology;
		this.player = player;
		this.engine = engine;
		this.encounterLogic = new EncounterLogic(this);
	}
	
	
	public void newEncounterPhase(EncounterInfo possibleEncounter, int startingPhase, boolean playerInitiated)
	{
		possibleEncounter.playerIfPresent = player.theFragment;
		System.out.println("-- newTurn "+startingPhase);
		if (!J3DCore.DEMO_ENCOUTNER_MODE) {

			if (startingPhase==Ecology.PHASE_INTERCEPTION)
			{
				core.preEncounterWindow.setPageData(core.gameState.player, possibleEncounter);
				core.preEncounterWindow.toggle();
			}
			
			if (startingPhase==Ecology.PHASE_ENCOUNTER)
			{
				possibleEncounter.setEncounterPhaseStatus();
				encounterLogic.fillInitEncounterPhaseLineup(possibleEncounter);
				core.encounterWindow.setPageData(core.gameState.player, possibleEncounter,playerInitiated);
				if (encounter(possibleEncounter)) {
					core.encounterWindow.toggle();
				}
			}
			
			if (startingPhase==Ecology.PHASE_TURNACT_SOCIAL_RIVALRY)
			{
				encounterLogic.currentTurnActTurn = 0;
				possibleEncounter.setTurnActPhaseCombatStatus();
				encounterLogic.fillInitTurnActPhaseLineup(possibleEncounter);
				core.uiBase.hud.mainBox.addEntry("Neutrals are leaving the Encounter...");
				encounterLogic.postLeaversMessage(possibleEncounter.filterNeutralsForSubjectBeforeTurnAct(true,player));
				// removing Encounter Phase 3d visualforms
				endPlayerEncounters();
				// starting new visualization
				if (encounter(possibleEncounter)) {
					core.turnActWindow.setPageData(EncounterLogic.ENCOUTNER_PHASE_RESULT_SOCIAL_RIVALRY, core.gameState.player, possibleEncounter, playerInitiated);
					core.turnActWindow.toggle();
				}
				
			}
			if (startingPhase==Ecology.PHASE_TURNACT_COMBAT)
			{
				encounterLogic.currentTurnActTurn = 0;
				possibleEncounter.setTurnActPhaseCombatStatus();
				encounterLogic.fillInitTurnActPhaseLineup(possibleEncounter);
				core.uiBase.hud.mainBox.addEntry("Neutrals are leaving the Encounter...");
				encounterLogic.postLeaversMessage(possibleEncounter.filterNeutralsForSubjectBeforeTurnAct(true,player));
				// removing Encounter Phase 3d visualforms // TODO clear out VisibleLifeForms from EncounterInfo etc.
				endPlayerEncounters();
				// starting new visualization
				if (encounter(possibleEncounter)) {
					core.turnActWindow.setPageData(EncounterLogic.ENCOUTNER_PHASE_RESULT_COMBAT, core.gameState.player, possibleEncounter, playerInitiated);
					core.turnActWindow.toggle();
				}
			}
		} else 
		{
			core.switchEncounterMode(true);
			encounter(possibleEncounter);
		}
	}
	
	
	public boolean inEncounter = false;
	
	public boolean encounter(EncounterInfo possibleEncounter) 
	{
		possibleEncounter.initPlacementMatrixForPhase();
		inEncounter = true;
		if (previousInfos == null)
		{
			previousInfos = new HashSet<EncounterInfo>();
			infos = new HashSet<EncounterInfo>();
		} else 
		{
			previousInfos.clear();
			previousInfos.addAll(infos);
			infos.clear();
		}
		infos.add(possibleEncounter);
		previousForms.addAll(forms);
		forms.clear();
		VisibleLifeForm playerFakeForm = new VisibleLifeForm("player",null,null,null);
		playerFakeForm.worldX = player.theFragment.getEncounterBoundary().posX;
		playerFakeForm.worldY = player.theFragment.getEncounterBoundary().posY;
		playerFakeForm.worldZ = player.theFragment.getEncounterBoundary().posZ;
		HashSet<String> playedAudios = new HashSet<String>();
		int sizeOfAll = 0;
		EncounterInfo info = possibleEncounter;
		if (info.active) {
			
			PlacementMatrix matrix = info.getCurrentPhaseMatrix();
			
			while (matrix.matrixAhead.hasNext())
			{
				EncounterUnitData data = matrix.matrixAhead.next();
				if (data.getUnit()==player.theFragment) continue;
				VisibleLifeForm form = data.setupVisibleLifeForm();
				if (form==null)
				{
					ecology.callbackMessage("You seem to trespass a Domain : "+data.getUnit().getName());
				} else
				{
					ecology.callbackMessage("Facing an *ENCOUNTER* : "+data.getUnit().getName());
					ArrayList<EntityMemberInstance> members = data.getUnit().getGroup(data.groupId);
					data.appendNewMembers(members);
					if (data.isGroupId) info.setGroupMemberInstances(data.groupId, members);
					form.targetForm = playerFakeForm;
					forms.add(form);
					{
						EntityMemberInstance member = members.get(0);
						if (member.description.audioDescription!=null && member.description.audioDescription.ENCOUNTER!=null && member.description.audioDescription.ENCOUNTER.length>0) {
							if (!playedAudios.contains(member.description.audioDescription.ENCOUNTER[0])) {
								core.audioServer.playLoading(member.description.audioDescription.ENCOUNTER[0], "ai");
								playedAudios.add(member.description.audioDescription.ENCOUNTER[0]);
							}
						}
					}
					sizeOfAll++;
				}
				System.out.println("#_#_#_#_#_#_: "+data.parent.getName());
			}
			
		}
		if (sizeOfAll>0) {
			placeVisibleForms(forms);
			J3DCore.getInstance().mEngine.render(forms);
			return true;
		} else
		{
			inEncounter = false;
			core.gameState.engine.turnFinishedForPlayer();
			return false;
		}
	}
	
	public void endPlayerEncounters()
	{
		inEncounter = false;
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
				if (i>10) {
					form.worldX = core.gameState.viewPositionX+(i/3+2)*trans[0]+(((i%3)-1)*trans[2]);
					form.worldY = core.gameState.viewPositionY;
					form.worldZ = core.gameState.viewPositionZ+(i/3+2)*trans[2]+(((i%3)-1)*trans[0]);
					form.notRendered = true;
					found=false; break;
				}
				form.worldX = core.gameState.viewPositionX+(i/3+1)*trans[0]+(((i%3)-1)*trans[2]);
				//form.worldY = core.gameState.viewPositionY;
				form.worldZ = core.gameState.viewPositionZ+(i/3+1)*trans[2]+(((i%3)-1)*trans[0]);
				ArrayList<SurfaceHeightAndType[] > data = core.gameState.world.getSurfaceData(form.worldX, form.worldZ);
				if (data!=null)
				{
					for (SurfaceHeightAndType[] d:data)
					{
						// TODO multiple layers, choose the closest Y one
						if (!d[0].canContain) continue;
						if (Math.abs(d[0].surfaceY-core.gameState.viewPositionY)<3) {
							form.worldY = d[0].surfaceY;
						}
							else continue;
					}
				}
				c = world.getCube(-1, form.worldX, form.worldY, form.worldZ, false);
				if (c==null || !c.canContain) 
				{
					i++;
					continue;
				}
				if (core.hasSideOfInstance(c.getSide(J3DCore.BOTTOM), stickingOut))
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
