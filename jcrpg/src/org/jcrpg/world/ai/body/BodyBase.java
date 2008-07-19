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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityMemberInstance;

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
	
	
	public BodyPart getBodyPart(int seed, EntityMemberInstance forMember, int targettingCriticalLevel)
	{
		targettingCriticalLevel++; // to make sure non-zero
		int sum = 0;
		for (BodyPart p:bodyParts)
		{
			int size = (int)(p.getBodyPartSize() * Math.exp((p.getCriticalityOfInjury()/100f)*targettingCriticalLevel));
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("BodyBase.getBodyPart: # BODY MODDED SIZE = "+size +" CRIT: "+targettingCriticalLevel);
			sum+=size;
		}
		int random = HashUtil.mix(seed, forMember.getNumericId()+forMember.instance.getNumericId(), 1)%sum;
		
		sum = 0;
		for (BodyPart p:bodyParts)
		{
			int size = (int)(p.getBodyPartSize() * Math.exp((p.getCriticalityOfInjury()/100f)*targettingCriticalLevel));
			sum+=size;
			if (random<sum) return p;
		}
		return null;
	}

}
