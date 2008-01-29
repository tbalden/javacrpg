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

package org.jcrpg.world.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;

public class Ecology {

	HashMap<String, EntityDescription> beings = new HashMap<String, EntityDescription>();
	
	
	public void addEntity(EntityDescription description)
	{
		beings.put(description.id, description);
	}
	
	public Collection<EntityDescription> getEntities(int worldX, int worldY, int worldZ)
	{
		return null;
	}
	
	public Collection<EntityDescription> getNearbyEntities(EntityDescription entity)
	{
		ArrayList<EntityDescription> entities = new ArrayList<EntityDescription>();
		for (EntityDescription targetEntity:beings.values())
		{
			if (targetEntity==entity) continue;
			if (entity.roamingBoundary.isInside(targetEntity.roamingBoundary.posX,targetEntity.roamingBoundary.posY, targetEntity.roamingBoundary.posZ))
			{
				entities.add(targetEntity);
			}
		}
		return entities;
	}
	
	public void doTurn()
	{
		for (EntityDescription entity:beings.values())
		{
			entity.liveOneTurn(getNearbyEntities(entity));
		}
	}
	
	public void callbackMessage(String message)
	{
		// TODO this is just for the testing period
		J3DCore.getInstance().uiBase.hud.mainBox.addEntry(message);
	}
	
}