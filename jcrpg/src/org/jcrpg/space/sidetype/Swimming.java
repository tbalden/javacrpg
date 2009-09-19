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
package org.jcrpg.space.sidetype;

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.skill.SkillBase;

public class Swimming extends SideSubType {

public Swimming(String id, byte[] color) {
		super(id, color);
		// TODO Auto-generated constructor stub
	}

	public Swimming(String id) {
		super(id);
	}
	
	static ArrayList<Class<? extends SkillBase>> needed = new ArrayList<Class<? extends SkillBase>>();
	static { needed.add(org.jcrpg.world.ai.abs.skill.physical.Swimming.class);};
	
	@Override
	public ArrayList<Class<? extends SkillBase>> getSkillNeeded() {
		return needed;
	}

}
