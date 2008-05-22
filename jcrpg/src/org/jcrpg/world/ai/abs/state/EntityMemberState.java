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
package org.jcrpg.world.ai.abs.state;

public class EntityMemberState {
	
	public static int DEFAULT_HEALTH_POINT = 10;
	public static int DEFAULT_STAMINA_POINT = 10;
	public static int DEFAULT_MORALE_POINT = 10;
	public static int DEFAULT_MANA_POINT = 0;
	public static int DEFAULT_SANITY_POINT = 10;
	
	public int maxHealthPoint = DEFAULT_HEALTH_POINT;
	public int maxStaminaPoint = DEFAULT_STAMINA_POINT;
	public int maxMoralePoint = DEFAULT_MORALE_POINT;
	public int maxSanityPoint = DEFAULT_SANITY_POINT;
	public int maxManaPoint = DEFAULT_MANA_POINT;
	
	public int healthPoint = DEFAULT_HEALTH_POINT;
	public int staminaPoint = DEFAULT_STAMINA_POINT;
	public int moralePoint = DEFAULT_MORALE_POINT;
	public int sanityPoint = DEFAULT_SANITY_POINT;
	public int manaPoint = DEFAULT_MANA_POINT;
	
	public int memberLevel = 1;

	
	// courage
	

}
