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
import org.jcrpg.world.ai.humanoid.modifier.race.Catkind;
import org.jcrpg.world.ai.humanoid.modifier.race.Dwarf;
import org.jcrpg.world.ai.humanoid.modifier.race.Elf;
import org.jcrpg.world.ai.humanoid.modifier.race.Human;
import org.jcrpg.world.ai.profession.Profession;
import org.jcrpg.world.ai.profession.adventurer.Alchemist;
import org.jcrpg.world.ai.profession.adventurer.Bard;
import org.jcrpg.world.ai.profession.adventurer.Crusader;
import org.jcrpg.world.ai.profession.adventurer.Demonist;
import org.jcrpg.world.ai.profession.adventurer.Enlightened;
import org.jcrpg.world.ai.profession.adventurer.Swindler;
import org.jcrpg.world.ai.profession.adventurer.Lobbist;
import org.jcrpg.world.ai.profession.adventurer.Negotiator;
import org.jcrpg.world.ai.profession.adventurer.Priest;
import org.jcrpg.world.ai.profession.adventurer.Psionic;
import org.jcrpg.world.ai.profession.adventurer.Ranger;
import org.jcrpg.world.ai.profession.adventurer.Shadow;
import org.jcrpg.world.ai.profession.adventurer.Jester;
import org.jcrpg.world.ai.profession.adventurer.Warrior;
import org.jcrpg.world.ai.profession.adventurer.Mage;
import org.jcrpg.world.ai.profession.adventurer.Thief;
import org.jcrpg.world.ai.profession.adventurer.Wise;
import org.jcrpg.world.ai.profession.adventurer.Witch;
import org.jcrpg.world.ai.profession.adventurer.WitchMaster;

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
			this.selectableRaces.add(Catkind.class);
			raceInstances.put(Human.class, new Human(Human.UNDEFINED_VISIBLE_TYPEID,null));
			raceInstances.put(Elf.class, new Elf(Elf.UNDEFINED_VISIBLE_TYPEID,null));
			raceInstances.put(Dwarf.class, new Dwarf(Dwarf.UNDEFINED_VISIBLE_TYPEID,null));
			raceInstances.put(Catkind.class, new Catkind(Catkind.UNDEFINED_VISIBLE_TYPEID,null));
		}
		if (selectableProfessions!=null) {
			this.selectableProfessions = selectableProfessions;
		} else
		{
			addProfession(new Warrior());
			addProfession(new Shadow());
			addProfession(new Enlightened());
			addProfession(new Crusader());
			addProfession(new Thief());
			addProfession(new Ranger());
			addProfession(new Mage());
			addProfession(new Priest());
			addProfession(new Witch());
			addProfession(new WitchMaster());
			addProfession(new Bard());
			addProfession(new Psionic());
			addProfession(new Alchemist());
			addProfession(new Demonist());
			addProfession(new Wise());
			addProfession(new Lobbist());
			addProfession(new Negotiator());
			addProfession(new Jester());
			addProfession(new Swindler());
		    
		}
	}
	
	public void addProfession(Profession p)
	{
		if (selectableProfessions.contains(p.getClass())) return;
		selectableProfessions.add(p.getClass());
		profInstances.put(p.getClass(), p);
	}
	
	
}
