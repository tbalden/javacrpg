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

import org.jcrpg.game.element.EncounterPhaseLineup;
import org.jcrpg.game.element.TurnActUnitTopology;
import org.jcrpg.ui.window.interaction.TurnActWindow.TurnActPlayerChoiceInfo;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnit;
import org.jcrpg.world.ai.EncounterUnitData;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.skill.SkillInstance;

public class EncounterLogic {
	
	public static final int ENCOUTNER_PHASE_CONTINUE = 0;
	public static final int ENCOUTNER_PHASE_RESULT_COMBAT = 1;
	public static final int ENCOUTNER_PHASE_RESULT_SOCIAL_RIVALRY = 2;
	
	GameLogic gameLogic;
	
	public EncounterLogic(GameLogic gameLogic)
	{
		this.gameLogic = gameLogic;
	}
	
	/**
	 * Checks if the encounter can be left by a given fragment.
	 * @param fragment
	 * @param possibleEncounters
	 * @return true if can.
	 */
	public boolean checkLeaveEncounterPhase(EntityFragment fragment, EncounterInfo encounters)
	{
		// TODO do check if instance can leave the encounter (forced to stay, or too much tension)
		return true;
	}
	
	/**
	 * Checks if a member can leave the encounter.
	 * @param instance
	 * @param possibleEncounters
	 * @return true if can.
	 */
	public boolean checkLeaveEncounterPhase(EntityMemberInstance instance, ArrayList<EncounterInfo> encounters)
	{
		// TODO do check if instance can leave the encounter (forced to stay, or too much tension)
		return true;
	}
	
	// encounter state related
	
	public class EncounterRoundState {
		public EntityMemberInstance initiatorSkillUser;
		public SkillInstance initiatorSkill;
		public EncounterInfo encounter;
		public int nextEventCount = 0;
		//public int maxEventCount = 0;
		public ArrayList<PlannedEncounterEvent> plan = new ArrayList<PlannedEncounterEvent>();
		public boolean playing = false;
		public long playStart = 0;
		public long maxTime = 0;
	}
	public class PlannedEncounterEvent
	{
		public static final int TYPE_PAUSE = 1;
		public int type = 0;
		
	}
	EncounterRoundState encounterRoundState = null;
	
	public void doEncounterRound(EntityMemberInstance initiatorSkillUser, SkillInstance initiatorSkill, EncounterInfo encounter)
	{
		encounterRoundState = new EncounterRoundState();
		encounterRoundState.initiatorSkillUser = initiatorSkillUser;
		encounterRoundState.initiatorSkill = initiatorSkill;
		encounterRoundState.encounter = encounter;
		
		
		// TODO do a preliminary skill usage plan into state with eventCount
		
		// TODO skill use, and check tension levels if combat or social rivalry happens.

		// demo pause
		PlannedEncounterEvent p = new PlannedEncounterEvent();
		p.type = PlannedEncounterEvent.TYPE_PAUSE;
		encounterRoundState.plan.add(p);

		playEncStep();
	}
	public void playEncStep()
	{
		if (encounterRoundState.nextEventCount>=encounterRoundState.plan.size())
		{
			// ending screenplay..check result:			
			int result = ENCOUTNER_PHASE_RESULT_COMBAT;
			if (result==EncounterLogic.ENCOUTNER_PHASE_RESULT_COMBAT)
			{				
				gameLogic.core.gameState.gameLogic.newEncounterPhase(encounterRoundState.encounter, Ecology.PHASE_TURNACT_COMBAT, true);
			} else
			if (result==EncounterLogic.ENCOUTNER_PHASE_RESULT_SOCIAL_RIVALRY)
			{
				gameLogic.core.gameState.gameLogic.newEncounterPhase(encounterRoundState.encounter, Ecology.PHASE_TURNACT_SOCIAL_RIVALRY, true);
			} else
			if (result==EncounterLogic.ENCOUTNER_PHASE_CONTINUE)
			{
				gameLogic.core.uiBase.hud.mainBox.addEntry("Next encounter round...");
				gameLogic.core.encounterWindow.toggle();
				encounterRoundState = null;
			}			
		} else
		{
			if (encounterRoundState.plan.get(encounterRoundState.nextEventCount).type == PlannedEncounterEvent.TYPE_PAUSE) 
			{
				encounterRoundState.playing = true;
				encounterRoundState.playStart = System.currentTimeMillis();
				encounterRoundState.maxTime = 1000;
				encounterRoundState.nextEventCount++;
			} else {
				// unknown step type...
				encounterRoundState.nextEventCount++;
				playEncStep();
			}
		}
	}
	
	public void checkEncounterCallbackNeed()
	{
		if (encounterRoundState!=null && encounterRoundState.playing)
		{
			if (encounterRoundState.maxTime>0 && encounterRoundState.maxTime<System.currentTimeMillis()-encounterRoundState.playStart)
			{
				encounterRoundState.playing = false;
				playEncStep();
			}
		} 
	}
	

	public class TurnActTurnState {
		public EncounterInfo encounter;
		public int nextEventCount = 0;
		//public int maxEventCount = 0;
		public ArrayList<PlannedTurnActEvent> plan = new ArrayList<PlannedTurnActEvent>();
		public boolean playing = false;
		public long playStart = 0;
		public long maxTime = 0;
	}
	public class PlannedTurnActEvent
	{
		public static final int TYPE_PAUSE = 1;
		public int type = 0;
		
	}
	TurnActTurnState turnActTurnState = null;
	
	public void doTurnActTurn(TurnActPlayerChoiceInfo info, EncounterInfo encountered)
	{
		turnActTurnState = new TurnActTurnState();

		// TODO do a preliminary skill usage plan into state with eventCount, speed counts for initiative
		
		// TODO skill use

		// demo pause
		PlannedTurnActEvent p = new PlannedTurnActEvent();
		p.type = PlannedEncounterEvent.TYPE_PAUSE;
		turnActTurnState.plan.add(p);

		playTurnActStep();
	}

	public void checkTurnActCallbackNeed()
	{
		if (turnActTurnState!=null && turnActTurnState.playing)
		{
			if (turnActTurnState.maxTime>0 && turnActTurnState.maxTime<System.currentTimeMillis()-turnActTurnState.playStart)
			{
				turnActTurnState.playing = false;
				playTurnActStep();
			}
		} 
	}

	public void playTurnActStep()
	{
		if (turnActTurnState.nextEventCount>=turnActTurnState.plan.size())
		{
			turnActTurnState = null;
			gameLogic.core.uiBase.hud.mainBox.addEntry("Next turn comes...");
			gameLogic.core.turnActWindow.toggle();			
		} else
		{
			if (turnActTurnState.plan.get(turnActTurnState.nextEventCount).type == PlannedTurnActEvent.TYPE_PAUSE) 
			{
				turnActTurnState.playing = true;
				turnActTurnState.playStart = System.currentTimeMillis();
				turnActTurnState.maxTime = 1000;
				turnActTurnState.nextEventCount++;
			} else
			{
				// unknown step type...
				turnActTurnState.nextEventCount++;
				playTurnActStep();
			}
		}
	}
	
	public void fillInitEncounterPhaseLineup(EncounterInfo info)
	{
		EncounterPhaseLineup lineup = new EncounterPhaseLineup(info);
		info.setEncounterPhaseLineup(lineup);
		System.out.println("{ fillInitEncounterPhaseLineup }");
		for (EncounterUnitData d:info.getEncounterUnitDataList(gameLogic.player.theFragment))
		{
			info.getEncounterPhaseLineup().addUnit(d,d.getEncPhasePriority(info));	
		}
	}

	public void fillInitTurnActPhaseLineup(EncounterInfo info)
	{
		TurnActUnitTopology topology = new TurnActUnitTopology(info);
		info.setTopology(topology);
		System.out.println("{ fillInitTurnActPhaseLineup }");
		for (EncounterUnitData d:info.getEncounterUnitDataList(gameLogic.player.theFragment))
		{
			info.getTopology().addUnitPushing(d,0);	
		}
		
	}

	public void postLeaversMessage(ArrayList<EncounterUnit> units)
	{
		if (units!=null)
		for (EncounterUnit unit:units)
		{
			gameLogic.core.uiBase.hud.mainBox.addEntry(unit.getName()+" leave(s).");
		}
	}


}
