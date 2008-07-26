/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.world.ai.flora.impl;

import org.jcrpg.world.ai.flora.FloraListElement;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.ground.Snow;
import org.jcrpg.world.ai.flora.middle.succulent.JungleBush;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
import org.jcrpg.world.climate.ClimateLevel;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;

public class EconomicFloraGenerator extends BaseFloraGenerator{

	
	public EconomicFloraGenerator()
	{
		addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Grass())});
		addFlora(Tropical.TROPICAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new JungleGround()),new FloraListElement(new CoconutTree(),20),new FloraListElement(new JungleBush(),60)});
		addFlora(Arctic.ARCTIC_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Snow())});
		
		addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Grass())});
		addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,INSIDE,new FloraListElement[]{new FloraListElement(new Grass())});

		addFlora(Tropical.TROPICAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Grass())});
		addFlora(Tropical.TROPICAL_ID,ClimateLevel.CLIMATELEVEL_ID,INSIDE,new FloraListElement[]{new FloraListElement(new Grass())});
		
		addFlora(Arctic.ARCTIC_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Grass())});
		addFlora(Desert.DESERT_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Grass())});
	}
	
}
