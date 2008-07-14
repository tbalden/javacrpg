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
package org.jcrpg.world.object.combat.bow.arrow;

import org.jcrpg.threed.jme.program.impl.ArrowNoEffect;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.world.object.Ammunition;
import org.jcrpg.world.object.combat.bow.Bow;

public abstract class Arrow extends Ammunition {

	static SimpleModel effectProgramModel = new SimpleModel("models/item/ammo/Arrow1.3ds",null,false);
	static EffectProgram effectProgram = new EffectProgram(ArrowNoEffect.class,effectProgramModel);

	public Arrow()
	{
		groupable = true;
		maxNumberOfUsage = 1;
	}
	
	@Override
	public EffectProgram getEffectProgram() {
		
		return effectProgram;
	}

	@Override
	public Class getAttachableToType() {
		return Bow.class;
	}

}
