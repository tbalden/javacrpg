/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.space.sidetype.trigger;

import org.jcrpg.space.sidetype.SideSubType;

public abstract class TriggerBaseSideSubtype extends SideSubType {

	
	public static final String TRIGGER_EFFECT_OPENING = "OPENING";
	public static final String TRIGGER_EFFECT_CLOSING = "CLOSING";
	public static final String TRIGGER_EFFECT_OPEN = "OPEN";
	public static final String TRIGGER_EFFECT_CLOSED = "CLOSED";
	// TODO more
	
	public TriggerBaseSideSubtype(String id,
			boolean overrideGeneratedTileMiddleHeight) {
		super(id, overrideGeneratedTileMiddleHeight);
		// TODO Auto-generated constructor stub
	}

	public TriggerBaseSideSubtype(String id, byte[] color) {
		super(id, color);
		// TODO Auto-generated constructor stub
	}

	public TriggerBaseSideSubtype(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	public String[] getEffectOnEnter()
	{
		return null;
	}

	public String[] getEffectOnLeave()
	{
		return null;
	}

	public String[] getEffectOnSuccess()
	{
		return null;
	}

	public String[] getEffectOnFailure()
	{
		return null;
	}

}
