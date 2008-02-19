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
import java.util.HashMap;

import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.humanoid.modifier.race.Dwarf;
import org.jcrpg.world.ai.humanoid.modifier.race.Elf;
import org.jcrpg.world.ai.humanoid.modifier.race.Human;
import org.jcrpg.world.ai.profession.Profession;
import org.jcrpg.world.ai.profession.adventurer.Fighter;
import org.jcrpg.world.ai.profession.adventurer.Mage;
import org.jcrpg.world.ai.profession.adventurer.Thief;

public class CharacterCreationRules {

	public ArrayList<Class<? extends MemberPerson>> selectableRaces = new ArrayList<Class<? extends MemberPerson>>();
	public ArrayList<Class<? extends Profession>> selectableProfessions = new ArrayList<Class<? extends Profession>>();
	
	public HashMap<Class<? extends MemberPerson>, MemberPerson> raceInstances = new HashMap<Class<? extends MemberPerson>, MemberPerson>();
	public HashMap<Class<? extends Profession>, Profession> profInstances = new HashMap<Class<? extends Profession>, Profession>();
	
	public CharacterCreationRules(ArrayList<Class<? extends MemberPerson>> selectableRaces, ArrayList<Class<? extends Profession>> selectableProfessions)
	{
		if (selectableRaces!=null) {
			this.selectableRaces = selectableRaces;
		} else
		{
			this.selectableRaces.add(Human.class);
			this.selectableRaces.add(Elf.class);
			this.selectableRaces.add(Dwarf.class);
			raceInstances.put(Human.class, new Human(Human.UNDEFINED_VISIBLE_TYPEID,null));
			raceInstances.put(Elf.class, new Elf(Elf.UNDEFINED_VISIBLE_TYPEID,null));
			raceInstances.put(Dwarf.class, new Dwarf(Dwarf.UNDEFINED_VISIBLE_TYPEID,null));
		}
		if (selectableProfessions!=null) {
			this.selectableProfessions = selectableProfessions;
		} else
		{
			this.selectableProfessions.add(Fighter.class);
			this.selectableProfessions.add(Thief.class);
			this.selectableProfessions.add(Mage.class);
			profInstances.put(Fighter.class, new Fighter());
			profInstances.put(Thief.class, new Thief());
			profInstances.put(Mage.class, new Mage());
		}
	}
	
	
}
