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
package org.jcrpg.world.ai.body;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BodyBase {
	
	public static HashMap<Class <? extends  BodyBase>, BodyBase> bodyBaseInstances = new HashMap<Class <? extends BodyBase>, BodyBase>();

	static
	{
		bodyBaseInstances.put(SinglePartBody.class, new SinglePartBody());
		bodyBaseInstances.put(MammalBody.class, new MammalBody());
		bodyBaseInstances.put(HumanoidBody.class, new HumanoidBody());
	}
	
	public ArrayList<BodyPart> bodyParts = new ArrayList<BodyPart>();
	public String bodyImage = "default";
	
	public String getBodyImage()
	{
		return bodyImage;
	}

}
