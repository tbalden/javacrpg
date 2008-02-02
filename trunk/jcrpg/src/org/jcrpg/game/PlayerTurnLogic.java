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

import java.util.Collection;
import java.util.HashSet;

import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PreEncounterInfo;
import org.jcrpg.world.place.World;

/**
 * Class for per turn encounter management.
 * @author pali
 *
 */
public class PlayerTurnLogic {
	
	public HashSet<PreEncounterInfo> previousInfos = new HashSet<PreEncounterInfo>();
	public HashSet<PreEncounterInfo> infos = new HashSet<PreEncounterInfo>();
	
	
	public World world;
	public Ecology ecology;
	public EntityInstance player;
	public Engine engine;
	
	public PlayerTurnLogic(Engine engine, World world, Ecology ecology, EntityInstance player)
	{
		this.world = world;
		this.ecology = ecology;
		this.player = player;
		this.engine = engine;
	}
	
	public void newTurn(Collection<PreEncounterInfo> possibleEncounters)
	{
		previousInfos.clear();
		previousInfos.addAll(infos);
		infos.addAll(possibleEncounters);
		for (PreEncounterInfo info:infos)
		{
			if (info.subject==null) continue;
			for (EntityInstance i:info.encountered.keySet()) {
				if (i==player) continue;
				int[] groupIds = info.encounteredGroupIds.get(i);
				//System.out.println("PlayerTurnLogic : "+i.description.getClass().getSimpleName());
				ecology.callbackMessage("You would encounter : "+i.description.getClass().getSimpleName()+ " "+i.id +" g:"+(groupIds!=null?groupIds.length:null));
				System.out.println("GROUP ID = "+(groupIds!=null?groupIds.length:null)+" "+groupIds);
				if (groupIds !=null)
				for (int in:groupIds)
				{
					int size = i.groupSizes[in];
					Collection<EntityMemberInstance> members = i.description.groupingRule.getGroup(size);
					ecology.callbackMessage(""+size+" of "+members.iterator().next().description.visibleTypeId);
				}
				
			}
		}
	}

}
