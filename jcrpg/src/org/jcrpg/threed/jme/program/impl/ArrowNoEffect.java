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
package org.jcrpg.threed.jme.program.impl;

import org.jcrpg.threed.jme.program.EffectNode;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class ArrowNoEffect extends EffectNode {

	//private Box debugBox;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArrowNoEffect() {
		speed = 8;
	}

	@Override
	public void setPosition(Vector3f newPos, Quaternion newAngle) {
		currentPos = newPos;
		super.setPosition(newPos,newAngle);
	}

}
