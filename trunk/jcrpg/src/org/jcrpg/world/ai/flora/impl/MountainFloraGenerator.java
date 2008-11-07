/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.world.ai.flora.impl;

import org.jcrpg.world.ai.flora.FloraListElement;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.middle.deciduous.GreenBush;
import org.jcrpg.world.ai.flora.middle.succulent.GreenFern;
import org.jcrpg.world.ai.flora.middle.succulent.JungleBush;
import org.jcrpg.world.ai.flora.tree.deciduous.CherryTree;
import org.jcrpg.world.ai.flora.tree.deciduous.OakTree;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
import org.jcrpg.world.ai.flora.tree.palm.JungleLowTree;
import org.jcrpg.world.ai.flora.tree.palm.JunglePalmTrees;
import org.jcrpg.world.ai.flora.tree.pine.GreatPineTree;
import org.jcrpg.world.ai.flora.tree.pine.GreenPineTree;
import org.jcrpg.world.climate.ClimateLevel;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;

public class MountainFloraGenerator extends BaseFloraGenerator{

	
	public MountainFloraGenerator()
	{
		addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new Grass()),new FloraListElement(new OakTree(),0), new FloraListElement(new CherryTree(),1),new FloraListElement(new GreenPineTree(),30),new FloraListElement(new GreatPineTree(),60),new FloraListElement(new GreenBush(),50)});
		addFlora(Tropical.TROPICAL_ID,ClimateLevel.CLIMATELEVEL_ID,OUTDOOR,new FloraListElement[]{new FloraListElement(new JungleGround()),new FloraListElement(new CoconutTree(),25),new FloraListElement(new JungleLowTree(),35),new FloraListElement(new JunglePalmTrees(),35),new FloraListElement(new JungleBush(),30),new FloraListElement(new GreenFern(),80)});
	}
	
}
