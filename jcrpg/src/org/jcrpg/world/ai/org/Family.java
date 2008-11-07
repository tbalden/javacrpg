/*
 *  This file is part of JavaCRPG.
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
package org.jcrpg.world.ai.org;

import java.util.HashMap;

import org.jcrpg.world.ai.fauna.Intelligent;
import org.jcrpg.world.place.economic.residence.House;

public class Family extends Group {

	public Intelligent father, mother;
	public HashMap<String, Intelligent> children;
	public HashMap<String, House> homeEstates; 
	
	public Family()
	{
		directMembers.put(father.id, father);
		directMembers.put(mother.id, mother);
		directMembers.putAll(children);
		properties.putAll(homeEstates);
	}
}
