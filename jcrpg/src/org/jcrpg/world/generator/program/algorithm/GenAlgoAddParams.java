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

package org.jcrpg.world.generator.program.algorithm;

public class GenAlgoAddParams {
	public String[] baseGeos;
	public int likeness;
	public int[] worldHeightsToAddTo;
	public GenAlgoAddParams(String[] baseGeos,  int likeness, int[] worldHeightsToAddTo) {
		super();
		this.baseGeos = baseGeos;
		this.likeness = likeness;
		this.worldHeightsToAddTo = worldHeightsToAddTo;
	}
}

