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
import java.util.HashMap;
import java.util.TreeMap;

import org.jcrpg.game.element.EncounterPhaseLineup;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.game.element.TurnActUnitTopology;
import org.jcrpg.game.logic.EvaluatorBase;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
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
		public int nextEventCount = -1;
		//public int maxEventCount = 0;
		public ArrayList<PlannedTurnActEvent> plan = new ArrayList<PlannedTurnActEvent>();
		public boolean playing = false;
		public long playStart = 0;
		public long maxTime = 0;
		
		public PlannedTurnActEvent getCurrentEvent()
		{
			return plan.get(nextEventCount);
		}
		
	}
	public class PlannedTurnActEvent
	{
		public TurnActMemberChoice choice = null;
		public static final int TYPE_PAUSE = 1;
		public static final int TYPE_MEMBER_CHOICE = 2;
		public String initMessage = null;
		public int type = 0;
		
	}
	TurnActTurnState turnActTurnState = null;
	
	public int currentTurnActTurn = 0;
	
	public void doTurnActTurn(TurnActPlayerChoiceInfo info, EncounterInfo encountered)
	{
		turnActTurnState = new TurnActTurnState();
		long seed = ((long)currentTurnActTurn)<<8 + gameLogic.core.gameState.engine.getNumberOfTurn();
		
		ArrayList<EncounterUnitData> dataList = encountered.getEncounterUnitDataList(null);
		TreeMap<Float, EntityMemberInstance> orderedActors = new TreeMap<Float,EntityMemberInstance>();
		HashMap<EntityMemberInstance, TurnActMemberChoice> memberChoices = new HashMap<EntityMemberInstance, TurnActMemberChoice>();
		for (EncounterUnitData data:dataList)
		{
			ArrayList<EntityMemberInstance> instances = data.generatedMembers;
			for (EntityMemberInstance mi: instances)
			{
				mi.encounterData = data;
				TurnActMemberChoice c = mi.makeTurnActChoice(data, encountered);
				memberChoices.put(mi, c);
				if (c==null) c = new TurnActMemberChoice();
				c.member = mi;
				float[] speeds = EvaluatorBase.evaluateActFormTimesWithSpeed((int)seed, mi, c.skill, c.skillActForm, c.usedObject);
				for (float s:speeds) {
					while (orderedActors.get(s)!=null)
					{
						s+=0.0001f;
					}
					orderedActors.put(s, mi);
				}
								
			}
		}
		for (TurnActMemberChoice playerChoice:info.getChoices())
		{
			System.out.println("PLAYER CHOICE TIME... "+playerChoice.member.description.getName());
			memberChoices.put(playerChoice.member, playerChoice);
			float[] speeds = EvaluatorBase.evaluateActFormTimesWithSpeed((int)seed, playerChoice);
			for (float s:speeds) {
				while (orderedActors.get(s)!=null)
				{
					s+=0.0001f;
				}
				orderedActors.put(s, playerChoice.member);
			}
		}
		int step = 0;
		for (Float miSpeed:orderedActors.keySet())
		{
			step++;
			EntityMemberInstance mi = orderedActors.get(miSpeed);
			TurnActMemberChoice c = memberChoices.get(mi);
			String message = "";
			if (c!=null) 
			{
				message = c.getInitMessage();
				memberChoices.put(mi, c);
				PlannedTurnActEvent p = new PlannedTurnActEvent();
				p.type = PlannedTurnActEvent.TYPE_MEMBER_CHOICE;
				p.choice = c;
				p.initMessage = message;
				turnActTurnState.plan.add(p);
			}
			else 
			{
				PlannedTurnActEvent p = new PlannedTurnActEvent();
				p.type = PlannedTurnActEvent.TYPE_PAUSE;
				message = step+". "+mi.description.getName() + " inactive.";
				p.initMessage = message;
				turnActTurnState.plan.add(p);
				
			}
		}

		playTurnActStep();
	}

	public void checkTurnActCallbackNeed()
	{
		synchronized (mutex) { 
			if (turnActTurnState!=null && turnActTurnState.playing)
			{
				if (turnActTurnState.maxTime>0 && turnActTurnState.maxTime<System.currentTimeMillis()-turnActTurnState.playStart)
				{
					if (turnActTurnState.getCurrentEvent().type==PlannedTurnActEvent.TYPE_MEMBER_CHOICE)
					{
						TurnActMemberChoice choice = turnActTurnState.getCurrentEvent().choice;
						if (choice.skillActForm!=null)
						{
							if (gameLogic.core.mEngine.activeUnits.size()>0) 
							{
								return;
							}
							
							String anim = choice.skillActForm.animationType;
							if (!choice.member.encounterData.visibleForm.notRendered) {
								gameLogic.core.mEngine.setAnimationForRenderedUnit(choice.member.encounterData.visibleForm,MovingModelAnimDescription.ANIM_IDLE);
							}
						} else
						{
							System.out.println("NO SKILL ACT, NEXT PLAN STEP...");
						}
	
					}
					turnActTurnState.playing = false;
					playTurnActStep();
				}
			}
		}
	}
	public Object mutex = new Object();

	public void playTurnActStep()
	{
		synchronized (mutex) {
			System.out.println("playTurnActStep ");
			turnActTurnState.nextEventCount++;
			if (turnActTurnState.nextEventCount>=turnActTurnState.plan.size())
			{
				currentTurnActTurn++;
				turnActTurnState = null;
				gameLogic.core.uiBase.hud.mainBox.addEntry("Next turn comes...");
				gameLogic.core.turnActWindow.toggle();			
			} else
			{
				if (turnActTurnState.plan.get(turnActTurnState.nextEventCount).type == PlannedTurnActEvent.TYPE_PAUSE) 
				{
					turnActTurnState.maxTime = 1000;
					turnActTurnState.playing = true;
					turnActTurnState.playStart = System.currentTimeMillis();
				} else
				if (turnActTurnState.plan.get(turnActTurnState.nextEventCount).type == PlannedTurnActEvent.TYPE_MEMBER_CHOICE) 
				{
					PlannedTurnActEvent event = turnActTurnState.plan.get(turnActTurnState.nextEventCount);
					TurnActMemberChoice choice = event.choice;
					gameLogic.core.uiBase.hud.mainBox.addEntry(choice.member.encounterData.getName());
					gameLogic.core.uiBase.hud.mainBox.addEntry(event.initMessage);
					if (choice.skillActForm!=null)
					{
						String anim = choice.skillActForm.animationType;
						if (!choice.member.encounterData.visibleForm.notRendered) {
							
							choice.member.encounterData.visibleForm.unit.startAttack(choice.target.visibleForm, anim);
						}
					}
					
					turnActTurnState.maxTime = 1000;
					turnActTurnState.playing = true;
					turnActTurnState.playStart = System.currentTimeMillis();
				} else
				{
					// unknown step type...
					
					playTurnActStep();
				}
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
		for (EncounterUnitData d:info.getEncounterUnitDataList(null))
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
