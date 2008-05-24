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

import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;

public class ScreenplayElement
{
	public static final int TYPE_PAUSE = 1;
	
	public EntityFragment actorFragment;
	public EntityMemberInstance actor;
	
	public String actText;
	
	public int type = 0;
	
	public long maxTime = 0;
	
	public Object parent = null;
	public ScreenplayElement(Object parent)
	{
		this.parent = parent;
	}
	
	// TODO many other parameters needed!
	
}
