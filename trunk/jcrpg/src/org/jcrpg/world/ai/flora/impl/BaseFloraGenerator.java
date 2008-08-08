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

import org.jcrpg.world.ai.flora.FloraGenerator;
import org.jcrpg.world.ai.flora.FloraListElement;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.ground.Sand;
import org.jcrpg.world.ai.flora.ground.Snow;
import org.jcrpg.world.ai.flora.middle.deciduous.GreenBush;
import org.jcrpg.world.ai.flora.middle.grass.Anethum;
import org.jcrpg.world.ai.flora.middle.mushroom.CaveMushroom;
import org.jcrpg.world.ai.flora.middle.mushroom.RedForestMushroom;
import org.jcrpg.world.ai.flora.middle.succulent.GreenFern;
import org.jcrpg.world.ai.flora.middle.succulent.JungleBush;
import org.jcrpg.world.ai.flora.tree.cactus.BigCactus;
import org.jcrpg.world.ai.flora.tree.deciduous.Acacia;
import org.jcrpg.world.ai.flora.tree.deciduous.CherryTree;
import org.jcrpg.world.ai.flora.tree.deciduous.OakTree;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
import org.jcrpg.world.ai.flora.tree.palm.JungleLowTree;
import org.jcrpg.world.ai.flora.tree.palm.JunglePalmTrees;
import org.jcrpg.world.ai.flora.tree.pine.GreenPineTree;
import org.jcrpg.world.climate.ClimateLevel;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;

public class BaseFloraGenerator extends FloraGenerator{

	
	public BaseFloraGenerator()
	{
		addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Grass()),new FloraListElement(new RedForestMushroom(),20),new FloraListElement(new Anethum(),20),new FloraListElement(new OakTree(),20), new FloraListElement(new CherryTree(),30),new FloraListElement(new GreenPineTree(),40),new FloraListElement(new Acacia(),50),new FloraListElement(new GreenBush(),60)});
		addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,INSIDE,new FloraListElement[]{new FloraListElement(new CaveMushroom(),100)});

		addFlora(Tropical.TROPICAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new JungleGround()),new FloraListElement(new RedForestMushroom(),35),new FloraListElement(new CoconutTree(),40),new FloraListElement(new JungleLowTree(),40),new FloraListElement(new JunglePalmTrees(),60),new FloraListElement(new JungleBush(),90),new FloraListElement(new GreenFern(),90)});
		addFlora(Tropical.TROPICAL_ID,ClimateLevel.CLIMATELEVEL_ID,INSIDE,new FloraListElement[]{new FloraListElement(new CaveMushroom(),100)});
		
		addFlora(Arctic.ARCTIC_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Snow())});
		addFlora(Desert.DESERT_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Sand()),new FloraListElement(new Acacia(),5),new FloraListElement(new BigCactus(),20),new FloraListElement(new GreenBush(),10)});
	}
	
}
