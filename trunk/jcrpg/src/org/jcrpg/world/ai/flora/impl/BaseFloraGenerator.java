/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.world.ai.flora.impl;

import org.jcrpg.world.ai.flora.FloraGenerator;
import org.jcrpg.world.ai.flora.FloraListElement;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.ground.Sand;
import org.jcrpg.world.ai.flora.ground.Snow;
import org.jcrpg.world.ai.flora.middle.deciduous.GreenBush;
import org.jcrpg.world.ai.flora.middle.succulent.GreenFern;
import org.jcrpg.world.ai.flora.middle.succulent.JungleBush;
import org.jcrpg.world.ai.flora.tree.cactus.BigCactus;
import org.jcrpg.world.ai.flora.tree.deciduous.Acacia;
import org.jcrpg.world.ai.flora.tree.deciduous.CherryTree;
import org.jcrpg.world.ai.flora.tree.deciduous.OakTree;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
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
		addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,new FloraListElement[]{new FloraListElement(new Grass()),new FloraListElement(new OakTree(),20), new FloraListElement(new CherryTree(),50),new FloraListElement(new GreenPineTree(),50),new FloraListElement(new Acacia(),100),new FloraListElement(new GreenBush(),120)});
		//addFlora(Continental.CONTINENTAL_ID,ClimateLevel.CLIMATELEVEL_ID,new FloraListElement[]{new FloraListElement(new Grass()),new FloraListElement(new OakTree(),20), new FloraListElement(new CherryTree(),10),new FloraListElement(new GreenPineTree(),10),new FloraListElement(new Acacia(),50),new FloraListElement(new GreenBush(),220)});
		addFlora(Tropical.TROPICAL_ID,ClimateLevel.CLIMATELEVEL_ID,new FloraListElement[]{new FloraListElement(new JungleGround()),new FloraListElement(new CoconutTree(),170),new FloraListElement(new JunglePalmTrees(),170),new FloraListElement(new JungleBush(),120),new FloraListElement(new GreenFern(),120)});
		addFlora(Arctic.ARCTIC_ID,ClimateLevel.CLIMATELEVEL_ID,new FloraListElement[]{new FloraListElement(new Snow())});
		addFlora(Desert.DESERT_ID,ClimateLevel.CLIMATELEVEL_ID,new FloraListElement[]{new FloraListElement(new Sand()),new FloraListElement(new Acacia(),5),new FloraListElement(new BigCactus(),20),new FloraListElement(new GreenBush(),10)});
	}
	
}
