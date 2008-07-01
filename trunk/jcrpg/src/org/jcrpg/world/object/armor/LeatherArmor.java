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
package org.jcrpg.world.object.armor;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.body.BodyPart;
import org.jcrpg.world.ai.body.part.mammal.Torso;
import org.jcrpg.world.ai.profession.Profession;
import org.jcrpg.world.object.Armor;
import org.jcrpg.world.object.Equippable;
import org.jcrpg.world.object.Obj;

public class LeatherArmor extends Obj implements Armor, Equippable {

	public int getDefenseValue() {
		return 10;
	}

	public int getHitPointImpactDecrease() {
		return 2;
	}

	public HashMap<String, Integer> getAttributeRequirement() {
		return null;
	}

	public Class<? extends BodyPart> getEquippableBodyPart() {
		return Torso.class;
	}

	public int getGenderType() {
		return EntityDescription.GENDER_BOTH;
	}

	public ArrayList<Profession> getProfessionRequirement() {
		return null;
	}

}
