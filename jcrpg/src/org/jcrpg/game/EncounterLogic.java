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

import org.jcrpg.world.ai.EncounterInfo;
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
	public boolean checkLeaveEncounterPhase(EntityFragment fragment, Collection<EncounterInfo> encounters)
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
	
	public EncounterRoundScreenplay doEncounterRound(EntityMemberInstance initiatorSkillUser, SkillInstance initiatorSkill, ArrayList<EncounterInfo> encounters)
	{
		EncounterRoundScreenplay screenplay = new EncounterRoundScreenplay();
		screenplay.encountered = encounters;
		// TODO skill use, and check tension levels if combat or social rivalry happens.

		ScreenplayElement e = new ScreenplayElement(screenplay);
		e.type = ScreenplayElement.TYPE_PAUSE;
		e.maxTime = 1000;
		screenplay.elements.add(e);
		screenplay.result = ENCOUTNER_PHASE_RESULT_COMBAT;
		return screenplay;
	}
	
	public TurnActTurnScreenplay doTurnActTurn(ArrayList<EncounterInfo> encountered)
	{
		TurnActTurnScreenplay screenplay = new TurnActTurnScreenplay();
		screenplay.encountered = encountered;
		// TODO skill use, and check tension levels if combat or social rivalry happens.

		//screenplay.result = ENCOUTNER_PHASE_RESULT_COMBAT;
		ScreenplayElement e = new ScreenplayElement(screenplay);
		e.type = ScreenplayElement.TYPE_PAUSE;
		e.maxTime = 1000;
		screenplay.elements.add(e);
		return screenplay;
	}
	

}
