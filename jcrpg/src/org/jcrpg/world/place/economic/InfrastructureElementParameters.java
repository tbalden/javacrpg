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

package org.jcrpg.world.place.economic;

import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.place.Economic;

public class InfrastructureElementParameters
{
	public static int NOT_PREDEFINED = -1;
	
	public int relOrigoX = NOT_PREDEFINED,relOrigoY = NOT_PREDEFINED,relOrigoZ = NOT_PREDEFINED;
	public int sizeX  = NOT_PREDEFINED, sizeY = NOT_PREDEFINED, sizeZ = NOT_PREDEFINED;
	public Class<? extends Economic> type;
	public PersistentMemberInstance owner = null;
	
	public void overwriteSelfWithPredifined(InfrastructureElementParameters i)
	{
		if (i.relOrigoX!=NOT_PREDEFINED) relOrigoX = i.relOrigoX;
		if (i.relOrigoY!=NOT_PREDEFINED) relOrigoY = i.relOrigoY;
		if (i.relOrigoZ!=NOT_PREDEFINED) relOrigoZ = i.relOrigoZ;
		if (i.sizeX!=NOT_PREDEFINED) sizeX = i.sizeX;
		if (i.sizeY!=NOT_PREDEFINED) sizeY = i.sizeY;
		if (i.sizeZ!=NOT_PREDEFINED) sizeZ = i.sizeZ;
		if (i.type!=null) type = i.type;
		owner = i.owner;
	}
	
}
