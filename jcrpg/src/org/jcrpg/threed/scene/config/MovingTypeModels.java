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

package org.jcrpg.threed.scene.config;

import java.util.HashMap;

import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.fauna.birds.heron.Herons;
import org.jcrpg.world.ai.fauna.insects.spider.GiantCaveSpiders;
import org.jcrpg.world.ai.fauna.mammals.bear.BrownBearFamily;
import org.jcrpg.world.ai.fauna.mammals.bear.PolarBears;
import org.jcrpg.world.ai.fauna.mammals.deer.DeerFamily;
import org.jcrpg.world.ai.fauna.mammals.fox.FoxFamily;
import org.jcrpg.world.ai.fauna.mammals.gorilla.GorillaHorde;
import org.jcrpg.world.ai.fauna.mammals.warthog.Warthogs;
import org.jcrpg.world.ai.fauna.mammals.wolf.WolfPack;
import org.jcrpg.world.ai.humanoid.group.boarman.BoarmanTribe;
import org.jcrpg.world.ai.humanoid.group.human.HumanCommoners;
import org.jcrpg.world.ai.humanoid.group.kobold.KoboldHorde;

/**
 * Mapping for moving life forms to renderend moving units.
 * @author illes
 */
public class MovingTypeModels {
	
	public static final String NON_INSTANCE = "-NONE-";
	
	HashMap<String, Integer> hmMobIdToModelId = new HashMap<String, Integer>();
	HashMap<Integer, RenderedMovingUnit> hmModelIdToRenderedMovingUnit = new HashMap<Integer, RenderedMovingUnit>();

	public MovingTypeModels()
	{
		fillMap();
	}
	
	public void fillMap()
	{
		int counter = 0;
		
		hmMobIdToModelId.put(GorillaHorde.GORILLA_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(GorillaHorde.GORILLA_TYPE_FEMALE.visibleTypeId,counter);
		hmMobIdToModelId.put(GorillaHorde.GORILLA_TYPE_CHILD.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, GorillaHorde.gorilla_unit);
		counter++;
		
		hmMobIdToModelId.put(WolfPack.WOLF_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(WolfPack.WOLF_TYPE_FEMALE.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, WolfPack.wolf_unit);
		counter++;

		hmMobIdToModelId.put(Warthogs.WARTHOG_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(Warthogs.WARTHOG_TYPE_FEMALE.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, Warthogs.warthog_unit);
		counter++;
		
		hmMobIdToModelId.put(FoxFamily.FOX_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(FoxFamily.FOX_TYPE_FEMALE.visibleTypeId,counter);
		hmMobIdToModelId.put(FoxFamily.FOX_TYPE_CHILD.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, FoxFamily.fox_unit);
		counter++;
		
		hmMobIdToModelId.put(BrownBearFamily.BROWNBEAR_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(BrownBearFamily.BROWNBEAR_TYPE_FEMALE.visibleTypeId,counter);
		hmMobIdToModelId.put(BrownBearFamily.BROWNBEAR_TYPE_CHILD.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, BrownBearFamily.brownbear_unit);
		counter++;

		hmMobIdToModelId.put(PolarBears.POLARBEAR_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(PolarBears.POLARBEAR_TYPE_FEMALE.visibleTypeId,counter);
		hmMobIdToModelId.put(PolarBears.POLARBEAR_TYPE_CHILD.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, PolarBears.polarbear_unit);
		counter++;

		hmMobIdToModelId.put(GiantCaveSpiders.GIANTCAVESPIDER_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(GiantCaveSpiders.GIANTCAVESPIDER_TYPE_FEMALE.visibleTypeId,counter);
		hmMobIdToModelId.put(GiantCaveSpiders.GIANTCAVESPIDER_TYPE_CHILD.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, GiantCaveSpiders.giantcavespider_unit);
		counter++;

		hmMobIdToModelId.put(HumanCommoners.HUMAN_MALE_ARTISAN.visibleTypeId,counter);
		hmMobIdToModelId.put(HumanCommoners.HUMAN_MALE_PEASANT.visibleTypeId,counter);
		hmMobIdToModelId.put(HumanCommoners.HUMAN_MALE_SMITH.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, HumanCommoners.humanMale_unit);
		counter++;

		hmMobIdToModelId.put(HumanCommoners.HUMAN_FEMALE_HOUSEWIFE.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, HumanCommoners.humanFemale_unit);
		counter++;

		hmMobIdToModelId.put(BoarmanTribe.BOARMAN_MALE_THUG.visibleTypeId,counter);
		hmMobIdToModelId.put(BoarmanTribe.BOARMAN_MALE_ARCHER.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, BoarmanTribe.boarmanMale_unit);
		counter++;

		hmMobIdToModelId.put(BoarmanTribe.BOARMAN_MALE_MAGE.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, BoarmanTribe.boarmanMaleMage_unit);
		counter++;

		hmMobIdToModelId.put(BoarmanTribe.BOARMAN_FEMALE.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, BoarmanTribe.boarmanFemale_unit);
		counter++;
		
		hmMobIdToModelId.put(KoboldHorde.KOBOLD_MALE_MINER.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, KoboldHorde.koboldMaleMiner_unit);
		counter++;
		
		hmMobIdToModelId.put(DeerFamily.DEER_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(DeerFamily.DEER_TYPE_FEMALE.visibleTypeId,counter);
		hmMobIdToModelId.put(DeerFamily.DEER_TYPE_CHILD.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, DeerFamily.deer_unit);
		counter++;

		hmMobIdToModelId.put(Herons.HERON_TYPE_MALE.visibleTypeId,counter);
		hmMobIdToModelId.put(Herons.HERON_TYPE_FEMALE.visibleTypeId,counter);
		hmModelIdToRenderedMovingUnit.put(counter, Herons.heron_unit);
		counter++;

}
	
	public RenderedMovingUnit getRenderedUnit(String id)
	{
		Integer iid = hmMobIdToModelId.get(id);
		if (iid==null) return null;
		return hmModelIdToRenderedMovingUnit.get(iid);
	}
}
