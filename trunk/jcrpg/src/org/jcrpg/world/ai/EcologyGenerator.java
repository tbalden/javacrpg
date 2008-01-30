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

package org.jcrpg.world.ai;

import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.fauna.mammals.gorilla.GorillaHorde;
import org.jcrpg.world.place.World;

public class EcologyGenerator {
	public Ecology generateEcology(World world) throws Exception
	{
		Ecology ecology = new Ecology();
		int n = 100;
		for (int i=0; i<n; i++)
			ecology.addEntity(new EntityInstance(new GorillaHorde(),world,ecology,"1-"+i,10,1+(world.realSizeX/n)*i,0,(world.realSizeZ/n)*i));
		return ecology;
	}

}
