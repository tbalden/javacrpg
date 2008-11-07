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

package org.jcrpg.world.ai.profession.adventurer;

import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.magical.CelestialMagic;
import org.jcrpg.world.ai.abs.skill.martial.Bows;
import org.jcrpg.world.ai.abs.skill.martial.Crossbows;
import org.jcrpg.world.ai.abs.skill.martial.MediumBlades;
import org.jcrpg.world.ai.abs.skill.physical.AnimalHandling;
import org.jcrpg.world.ai.abs.skill.physical.Climbing;
import org.jcrpg.world.ai.abs.skill.physical.Swimming;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.profession.HumanoidProfessional;
import org.jcrpg.world.object.combat.blade.LongSword;
import org.jcrpg.world.object.combat.bow.ShortBow;
import org.jcrpg.world.object.combat.bow.arrow.CrudeArrow;

public class Ranger extends HumanoidProfessional {

	public Ranger()
	{
		super();
		generationNewInstanceObjects.add(LongSword.class);
		generationNewInstanceObjects.add(ShortBow.class);
		generationNewInstanceObjects.add(CrudeArrow.class);
		generationNewInstanceObjects.add(CrudeArrow.class);
		generationNewInstanceObjects.add(CrudeArrow.class);
		generationNewInstanceObjects.add(CrudeArrow.class);
		generationNewInstanceObjects.add(CrudeArrow.class);
		attrMinLevels.minimumLevels.put(FantasyAttributes.STRENGTH, 12);
		attrMinLevels.minimumLevels.put(FantasyAttributes.SPEED, 12);
		attrMinLevels.minimumLevels.put(FantasyAttributes.CONCENTRATION, 12);
		attrMinLevels.minimumLevels.put(FantasyAttributes.PIETY, 10);
		addMinorSkill(CelestialMagic.class);
		addMajorSkill(Crossbows.class);
		addMajorSkill(Bows.class);
		addMajorSkill(MediumBlades.class);
		addMajorSkill(AnimalHandling.class);
		addMinorSkill(Tracking.class);
		addMinorSkill(Swimming.class);
		addMinorSkill(Climbing.class);
	}

}
