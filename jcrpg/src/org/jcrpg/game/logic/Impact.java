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
package org.jcrpg.game.logic;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.ui.text.TextBox;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.object.BonusSkillActFormDesc;


public class Impact
{
	public boolean isConstructive = false;
	public boolean success = false;
	public int experienceGain = 1; // TODO
	public ImpactUnit actCost = new ImpactUnit();
	
	public ArrayList<BonusSkillActFormDesc> additionalEffectsToPlay = null;
	
	public ArrayList<TextEntry> messages = new ArrayList<TextEntry>();
	
	public HashMap<EntityMemberInstance, ImpactUnit> targetImpact = new HashMap<EntityMemberInstance, ImpactUnit>();
	public Impact(){}
	
	public void append(Impact impact, boolean experienceToo, boolean costToo)
	{
		if (experienceToo) this.experienceGain+=impact.experienceGain;
		if (costToo) this.actCost.append(impact.actCost);
		messages.addAll(impact.messages);
		for (EntityMemberInstance i:impact.targetImpact.keySet())
		{
			ImpactUnit u = impact.targetImpact.get(i);
			if (targetImpact.containsKey(i))
			{
				targetImpact.get(i).append(u);
			} else
			{
				targetImpact.put(i, u);
			}
		}
	}
	
	public void notifyUI(TextBox box)
	{
		//for (EntityMemberInstance i:targetImpact.keySet()) {
			//box.addEntry(i.encounterData.getName()+": HP "+targetImpact.get(i).getHealthPoint());
		//}
	}
	
	/**
	 * Used upon application only, postprocessing.
	 */
	public ArrayList<TextEntry> applyMessages = new ArrayList<TextEntry>();
}
