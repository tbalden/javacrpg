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
package org.jcrpg.world.ai.body;

import org.jcrpg.world.ai.body.part.mammal.Back;
import org.jcrpg.world.ai.body.part.mammal.Head;
import org.jcrpg.world.ai.body.part.mammal.Leg;
import org.jcrpg.world.ai.body.part.mammal.Neck;
import org.jcrpg.world.ai.body.part.mammal.Torso;
import org.jcrpg.world.ai.body.part.mammal.Waist;
import org.jcrpg.world.ai.body.part.reptile.Tail;

public class LizardBody extends BodyBase {

	public LizardBody()
	{
		bodyParts.add(new Leg());
		bodyParts.add(new Tail());
		bodyParts.add(new Torso());
		bodyParts.add(new Back());
		bodyParts.add(new Neck());
		bodyParts.add(new Head());
		bodyImage = "humanoidbody";
	}
	
}
