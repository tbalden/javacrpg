/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world.generator;

import java.util.HashMap;

import org.jcrpg.world.generator.program.algorithm.GenAlgoBase;
import org.jcrpg.world.place.World;

/**
 * base abstract class for geo generation programs.
 * @author illes
 *
 */
public abstract class GenProgram {

	/**
	 * class instantiation factory
	 */
	public ClassFactory factory;
	/**
	 * parent generator.
	 */
	public WorldGenerator generator;
	/**
	 * parent generator's parameters.
	 */
	public WorldParams params;
	
	public HashMap<String,  Class<? extends GenAlgoBase>> genAlgorithms = new HashMap<String, Class<? extends GenAlgoBase>>();
	

	public GenProgram(ClassFactory factory, WorldGenerator generator, WorldParams params)
	{
		this.factory = factory;
		this.factory.program = this;
		this.generator = generator;
		this.params = params;
	}
	
	/**
	 * run the generation program using World to fill up.
	 * @param world
	 * @throws Exception
	 */
	public abstract void runProgram(World world) throws Exception;
	
}
