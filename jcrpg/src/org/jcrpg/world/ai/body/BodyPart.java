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

import org.jcrpg.util.Language;

public abstract class BodyPart {
	
	
	public int maxNumberOfObjToEquip = 1;
	
	
	
	public String getName()
	{
		return Language.v("bodypart."+this.getClass().getName());
	}
	public static String getName(Class<? extends BodyPart> clazz)
	{
		return Language.v("bodypart."+clazz.getName());
	}
	/**
	 * The value of placement inside the body image in 1/100.
	 * @return
	 */
	public abstract float[] getPlacingRatioXY();
	
	
	/**
	 * 1-100 represents how critical is a hit on that body part. Override this in extensions' constructor.
	 */
	public int criticality = 10;
	
	/**
	 * returns 1-100 represents how critical is a hit on that body part.
	 * @return
	 */
	public int getCriticalityOfInjury()
	{
		return criticality;
	}
	
	
	/**
	 * the body part size on a scale of 1-100 1 is fingers, 50 is a human body. Override this
	 * in extension constructor.
	 */
	public int bodyPartSize = 10;
	
	/**
	 * Returns the body part size on a scale of 1-100 1 is fingers, 50 is a human body.
	 * @return
	 */
	public int getBodyPartSize()
	{
		return bodyPartSize;
	}
	
	public int getMaxNumberOfObjToEquip()
	{
		return maxNumberOfObjToEquip;
	}

}
