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

import java.util.HashMap;

/**
 * Class containing a possible encounter's data. 
 * @author illes
 */
public class PreEncounterInfo {

	public boolean active = false;
	/**
	 * The initiator group that faces the encounter.
	 */
	public EntityInstance subject;
	/**
	 * Encountered instances and their common radius ratios and middle point data.
	 */
	public HashMap<EntityInstance, int[][]> encountered = new HashMap<EntityInstance, int[][]>();
	/**
	 * encountered instances' subgroupIds facing the possible encounter.
	 */
	public HashMap<EntityInstance, int[]> encounteredGroupIds = new HashMap<EntityInstance, int[]>();
	/**
	 * The subgroups of the initiator group which face the encountered.  
	 */
	public int[] ownGroupIds = null;
	
	
	public PreEncounterInfo(EntityInstance subject) {
		super();
		this.subject = subject;
	}
	
	public PreEncounterInfo copy()
	{
		PreEncounterInfo r = new PreEncounterInfo(subject);
		r.active = active;
		r.encountered.putAll(encountered);
		r.encounteredGroupIds.putAll(encounteredGroupIds);
		r.ownGroupIds = ownGroupIds.clone();
		return r;
	}
	
}
