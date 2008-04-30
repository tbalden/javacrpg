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

package org.jcrpg.world.place;

public class EconomyHistoryElement {

	public static final short ADDITION = 0;
	public static final short RUIN = 1;
	public static final short REMOVE = 2;
	
	/**
	 * id of the economy (population)
	 */
	public String id;
	public short eventType;
	public EconomyHistoryElement(String economyId, short eventType)
	{
		this.id = economyId;
		this.eventType = eventType;
	}
	
}
