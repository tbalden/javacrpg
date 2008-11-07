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

package org.jcrpg.world.ai;




/**
 * Class for entity inter-relations.
 * @author illes
 *
 */
public class EntityMemberRelations 
{
	public EntityScaledRelationType relations = new EntityScaledRelationType();
	
	public int getRelationLevel(EntityMemberInstance i)
	{
		return relations.getRelationQuality(i.numericId);
	}
	public void increaseRelationLevel(EntityMemberInstance i, byte num)
	{
		relations.increase(i.numericId, num);
	}
	public void decreaseRelationLevel(EntityMemberInstance i, byte num)
	{
		relations.decrease(i.numericId, num);
	}
	public void turnNeutralization()
	{
		relations.doNeutralization();
	}
}
