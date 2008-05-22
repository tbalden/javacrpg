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

package org.jcrpg.ui.window.interaction;

import java.util.Collection;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.world.ai.PreEncounterInfo;
import org.jcrpg.world.ai.player.PartyInstance;

/**
 * Player-AI or forced AI-player encounter decision maker window. Before  a turn based combat or social rivalry begins
 * player can interact with the met group if it isn't too much hostile towards the player. :-)
 * @author pali
 *
 */
public class EncounterWindow extends PagedInputWindow {

	public EncounterWindow(UIBase base) {
		super(base);
		// TODO Auto-generated constructor stub
	}
	
	public PartyInstance party;
	public Collection<PreEncounterInfo> encountered;
	
	public void setPageData(PartyInstance party, Collection<PreEncounterInfo> encountered)
	{
		this.party = party;
		this.encountered = encountered;
	}

	@Override
	public boolean inputChanged(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputLeft(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

}
