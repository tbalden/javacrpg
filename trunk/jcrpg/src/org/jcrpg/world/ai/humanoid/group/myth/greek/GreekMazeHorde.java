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

package org.jcrpg.world.ai.humanoid.group.myth.greek;

import java.util.HashMap;

import org.jcrpg.ui.map.BlockPattern;
import org.jcrpg.ui.map.IconReader;
import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.humanoid.group.myth.greek.member.Evilrip;
import org.jcrpg.world.ai.humanoid.group.myth.greek.member.EyeBat;
import org.jcrpg.world.ai.humanoid.group.myth.greek.member.HellPig;
import org.jcrpg.world.ai.humanoid.group.myth.greek.member.Scorpoholder;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.ground.RawStreetGround;
import org.jcrpg.world.place.economic.population.DungeonDistrict;
import org.jcrpg.world.place.economic.population.SimpleDistrict;
import org.jcrpg.world.place.economic.residence.dungeon.SimpleDungeonPart;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;

import com.jme.renderer.ColorRGBA;

public class GreekMazeHorde extends HumanoidEntityDescription {


	@Override
	public String getEntityIconPic() {
		return "greekmazehorde";
	}

	public GreekMazeHorde()
	{
		//iconPic = "boarman";
		
		economyTemplate.addPopulationType(Plain.class, DungeonDistrict.class);
		economyTemplate.addResidenceType(Plain.class, SimpleDungeonPart.class);
		economyTemplate.addPopulationType(Forest.class, DungeonDistrict.class);
		economyTemplate.addResidenceType(Forest.class, SimpleDungeonPart.class);
		economyTemplate.addEcoGroundType(Plain.class, RawStreetGround.class);
		economyTemplate.addEcoGroundType(Forest.class, RawStreetGround.class);
	
		climates.add(Desert.class);
		climates.add(Tropical.class);
		climates.add(Continental.class);
		geographies.add(Forest.class);
		geographies.add(Plain.class);
		geographies.add(Mountain.class);
		//geographies.add(Cave.class);
		
		behaviors.add(Peaceful.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		
		setAverageGroupSizeAndDeviation(3, 2);
		
		addGroupingRuleMember(EyeBat.EYEBAT);
		addGroupingRuleMember(HellPig.HELLPIG);
		addGroupingRuleMember(Scorpoholder.SCORPOHOLDER);
		addGroupingRuleMember(Evilrip.EVILRIP);
	}
	
	public static boolean[][] CITY = new boolean[][] 
			  	                                    {
			  		{ true, true, true, true, false,true, true, false, false },
			  		{ false, true, true, true, true,true, true, false, false },
			  		{ false, true, false, true, false,true, false, false, false },
			  		{ false, true, false, true, false,true, false, false, false },
			  		{ false, true, false, true, false,true, false, false, false },
			  		{ false, true, false, true, false,true, false, false, false },
			  		{ true, true, true, false, true,true, true, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;
	
	
	static ColorRGBA TR = WorldMap.TR_COL;
	static ColorRGBA RD = ColorRGBA.red;
	static ColorRGBA OR = ColorRGBA.orange;
	static ColorRGBA BL = ColorRGBA.black;
	static ColorRGBA GY = ColorRGBA.gray;
	public static ColorRGBA[][] CITY_COLORED = IconReader.readMapIconFile("./data/ui/mapicons/greek.ico");
		/*
		new ColorRGBA[][]
	                                                           {
		{ TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR },
		{ TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR },
		{ TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR },
		{ TR, TR, OR, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, TR, TR, TR },
		{ TR, TR, OR, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, TR, TR, TR },
		{ TR, TR, TR, OR, OR, RD, OR, OR, OR, RD, OR, OR, OR, RD, TR, TR, TR, TR },
		{ TR, TR, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, TR, TR },
		{ TR, TR, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, TR, TR },
		{ TR, TR, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, TR, TR },
		{ TR, TR, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, OR, RD, TR, TR, TR, TR },
		{ TR, TR, OR, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, TR, TR, TR },
		{ TR, TR, OR, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, RD, TR, TR, TR },
		{ TR, TR, TR, OR, OR, OR, OR, OR, OR, OR, OR, OR, OR, OR, TR, TR, TR, TR },
		{ TR, TR, TR, BL, BL, BL, GY, GY, GY, GY, OR, OR, OR, OR, TR, TR, TR, TR },
		{ TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR },
		{ TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR },
		{ TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR },
		{ TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR, TR }
		
	                                                           };
*/
	static HashMap<Class<? extends Population>, BlockPattern> patternMap = new HashMap<Class<? extends Population>, BlockPattern>();
	static 
	{
		BlockPattern patternSimpleDistrict = new BlockPattern();
		patternSimpleDistrict.PATTERN = CITY;
		patternSimpleDistrict.COLORED_PATTERN = CITY_COLORED;
		patternMap.put(DungeonDistrict.class, patternSimpleDistrict);
	}
	
	@Override
	public HashMap<Class<? extends Population>, BlockPattern> getPopulationPatternMap() {
		return patternMap;
	}
	

	
}
