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
package org.jcrpg.game.logic;

import java.util.Collection;
import java.util.HashMap;

import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.ui.Window;
import org.jcrpg.world.ai.EntityMemberInstance;

public class PlayerActChoiceInfo
{
	/**
	 * Tells if this is not an encounter acting, so no win/lose conditions should be checked, NormalActWindow callback.
	 * Otherwise if this false - combat situation, win/lose checked, TurnActWindow callback.
	 */
	public boolean nonEncounterMode = false;
	
	public Window callbackWindow = null;
	
	/**
	 * Tells if the player is escaping an encounter.
	 */
	public boolean doEscape = false;
	
	public HashMap<EntityMemberInstance, TurnActMemberChoice> memberToChoice = new HashMap<EntityMemberInstance, TurnActMemberChoice>();
	public Collection<TurnActMemberChoice> getChoices()
	{
		return memberToChoice.values();
	}
	public boolean isEscaping()
	{
		return doEscape;
	}
}
