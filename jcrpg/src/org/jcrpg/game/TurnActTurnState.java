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

package org.jcrpg.game;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.game.EncounterLogic.PlannedTurnActEvent;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.game.logic.PlayerActChoiceInfo;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EntityMemberInstance;

public class TurnActTurnState {
	public PlayerActChoiceInfo choiceInfo = null;
	public EncounterInfo encounter;
	public int nextEventCount = -1;
	//public int maxEventCount = 0;
	
	public Object currentActor = null;
	public Object currentTarget = null;
	
	public HashMap<EntityMemberInstance, TurnActMemberChoice> memberChoices = new HashMap<EntityMemberInstance, TurnActMemberChoice>();
	
	public ArrayList<PlannedTurnActEvent> plan = new ArrayList<PlannedTurnActEvent>();
	public boolean playing = false;
	public long playStart = 0;
	public long maxTime = 0;
	
	public TurnActTurnState(EncounterInfo encounter, PlayerActChoiceInfo info)
	{
		this.choiceInfo = info;
		this.encounter = encounter;
	}
	
	public PlannedTurnActEvent getCurrentEvent()
	{
		return plan.get(nextEventCount);
	}
	
	public void highlightActor(boolean on)
	{
		if (currentActor==null) return;
		if (currentActor instanceof EntityMemberInstance)
		{
			EntityMemberInstance i = (EntityMemberInstance)currentActor;
			if (i.parentFragment == J3DCore.getInstance().gameState.player.theFragment)
			{
				J3DCore.getInstance().uiBase.hud.characters.highlightCharacter(i, on);
			}
		}
	}
	public void highlightTarget(boolean on)
	{
		if (currentTarget==null) return;
		if (currentTarget instanceof EntityMemberInstance)
		{
			EntityMemberInstance i = (EntityMemberInstance)currentTarget;
			if (i.parentFragment == J3DCore.getInstance().gameState.player.theFragment)
			{
				J3DCore.getInstance().uiBase.hud.characters.targetCharacter(i, on);
			}
		}
	}
	
}