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
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PreEncounterInfo;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
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
	public J3DCore core;
	
	public PlayerTurnLogic(J3DCore core, Engine engine, World world, Ecology ecology, EntityInstance player)
	{
		this.core = core;
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
		previousForms.addAll(forms);
		forms.clear();
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
					Collection<EntityMemberInstance> members = i.description.groupingRule.getGroup(i,in,size);
					ecology.callbackMessage(""+size+" of "+members.iterator().next().description.visibleTypeId);
					for (EntityMemberInstance member:members)
					{
						VisibleLifeForm form = i.getOne(member.description,member);
						forms.add(form);
					}
				}
				
			}
		}
		placeVisibleForms(forms);
		J3DCore.getInstance().mEngine.render(forms);
	}
	
	public void placeVisibleForms(Collection<VisibleLifeForm> forms)
	{
		int dir = core.viewDirection;
		int[] trans = J3DCore.moveTranslations.get(dir);
		HashSet<Integer> usedPositions = new HashSet<Integer>();
		for (VisibleLifeForm form:forms)
		{
			boolean found = true;
			int i=0;
			while (true) { 
				if (i>15) {
					form.worldX = core.viewPositionX+(i/3+1)*trans[0]+(((i%3)-1)*trans[2]);
					form.worldY = core.viewPositionY;
					form.worldZ = core.viewPositionZ+(i/3+1)*trans[2]+(((i%3)-1)*trans[0]);
					found=false; break;
				}
				form.worldX = core.viewPositionX+(i/3+1)*trans[0]+(((i%3)-1)*trans[2]);
				form.worldY = core.viewPositionY;
				form.worldZ = core.viewPositionZ+(i/3+1)*trans[2]+(((i%3)-1)*trans[0]);
				Cube c = world.getCube(form.worldX, form.worldY, form.worldZ, false);
				if (c==null)
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
					System.out.println("FOUND WATER"+c);
					break;
				}
				if (land && !c.waterCube && !usedPositions.contains(i)) 
				{
					System.out.println("FOUND LAND "+c);
					break;
				}
				i++;
			}
			if (found)
			{
				usedPositions.add(i);
			}
		}
	}
	
	

}
