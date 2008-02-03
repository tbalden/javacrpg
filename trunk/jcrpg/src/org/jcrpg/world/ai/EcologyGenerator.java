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
import org.jcrpg.world.ai.fauna.mammals.warthog.Warthogs;
import org.jcrpg.world.ai.fauna.mammals.wolf.WolfPack;
import org.jcrpg.world.place.World;

public class EcologyGenerator {
	public Ecology generateEcology(World world) throws Exception
	{
		Ecology ecology = new Ecology();
		int nX = 20;
		int nY = 20;
		GorillaHorde gorillaDesc = new GorillaHorde();
		WolfPack wolfDesc = new WolfPack();
		Warthogs wartDesc = new Warthogs();
		for (int i=0; i<nX; i++) {
			for (int j=0; j<nY; j++)
			{
				ecology.addEntity(new EntityInstance(gorillaDesc,world,ecology,"GO-"+i+" "+j,50,1+(int)((world.realSizeX*1f/nX)*i),0,(int)((world.realSizeZ*1f/nY)*j)));
				ecology.addEntity(new EntityInstance(wolfDesc,world,ecology,"WO-"+i+" "+j,20,1+(int)((world.realSizeX*1f/nX)*i),0,(int)((world.realSizeZ*1f/nY)*j)));
				ecology.addEntity(new EntityInstance(wartDesc,world,ecology,"WA-"+i+" "+j,50,1+(int)((world.realSizeX*1f/nX)*i),0,(int)((world.realSizeZ*1f/nY)*j)));
			}
		}
		return ecology;
	}

}
