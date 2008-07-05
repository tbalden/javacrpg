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
package org.jcrpg.world.object;

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;

/**
 * Describes a bonus object.
 * @author illes
 *
 */
public interface BonusObject {
	/**
	 * Attribute bonus/malus.
	 * @return
	 */
	public Attributes getAttributeValues();
	/**
	 * Resistance bonus/malus.
	 * @return
	 */
	public Resistances getResistanceValues();
	
	/**
	 * Tells that the bonus is only for a given body part - for an armor when it's attacked.
	 * @return
	 */
	public boolean isBodyPartBonusOnly();
	
	/**
	 * Return what skill form does it do upon using it in turn act.
	 * @return the skill act form list.
	 */
	public ArrayList<BonusSkillActFormDesc> getSkillActFormBonusEffectTypes();
	
	public boolean isDestructive();
	
	
	/**
	 * Tells if it can be un-equipped or not (cursed).
	 * @return
	 */
	public boolean isCursed();

}
