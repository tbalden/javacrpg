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

package org.jcrpg.game.logic;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.game.TurnActTurnState;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EncounterUnitData;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.object.InventoryListElement;

public class UseObjEvaluator {

	public static void evaluateUse(EntityMemberInstance source, EntityMemberInstance tTarget, InventoryListElement object)
	{
		int seed = J3DCore.getInstance().gameState.engine.getBaseTimeSeed();
		seed+=source.getNumericId()+tTarget.getNumericId();

		TurnActTurnState state = new TurnActTurnState(null,null);
		TurnActMemberChoice choice = new TurnActMemberChoice();
		choice.member = source;
		choice.usedObject = object;
		EncounterUnitData d = new EncounterUnitData(source.parentFragment,source.parentFragment);
		d.visibleForm = new VisibleLifeForm("",choice.member.description,null,0);
		d.visibleForm.worldX = source.parentFragment.getEncounterBoundary().posX;
		d.visibleForm.worldY = source.parentFragment.getEncounterBoundary().posY;
		d.visibleForm.worldZ = source.parentFragment.getEncounterBoundary().posZ;
		RenderedMovingUnit unit = new RenderedMovingUnit("-",d.visibleForm.worldX,d.visibleForm.worldY,d.visibleForm.worldZ,new org.jcrpg.threed.scene.model.Model[0]);
		unit.form = d.visibleForm;
		d.visibleForm.renderedUnit = unit;
		choice.member.encounterData = d;
		
		choice.doUse = true;
		
		
		// target member
		choice.target = new EncounterUnitData(tTarget.parentFragment,(PersistentMemberInstance)tTarget);
		
		
		ArrayList<EntityMemberInstance> list = new ArrayList<EntityMemberInstance>();
		list.add(tTarget);
		choice.target.appendNewMembers(list);
		choice.targetMember = tTarget;
		
		state.memberChoices = new HashMap<EntityMemberInstance, TurnActMemberChoice>();
		
		J3DCore.getInstance().gameState.gameLogic.encounterLogic.handleChoiceEffects(choice);
		
		Impact imp = EvaluatorBase.evaluateActFormSuccessImpact(seed, choice, state);
		if (imp.messages!=null)
		for (TextEntry e:imp.messages)
		{
			System.out.println("e."+e.text);
		}
		
		// TODO IMPACT, effect etc.
		for (TextEntry m:imp.messages)
		{
			J3DCore.getInstance().uiBase.hud.mainBox.addEntry(m);
		}
		choice.target.applyImpactUnit(imp);
		J3DCore.getInstance().gameState.gameLogic.encounterLogic.handleImpactEffects(imp, choice);

	}
}
