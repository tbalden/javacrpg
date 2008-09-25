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
package org.jcrpg.world.place.economic.infrastructure;

import java.util.ArrayList;

import org.jcrpg.world.place.economic.AbstractInfrastructure;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.InfrastructureElementParameters;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Residence;

public class BigBlockInfrastructure extends AbstractInfrastructure {

	public BigBlockInfrastructure(Population population) {
		super(population);
		buildProgram();
	}

	@Override
	public ArrayList<InfrastructureElementParameters> buildProgramLevel(
			int sizeLevel,
			ArrayList<InfrastructureElementParameters> fixProperties,
			ArrayList<Class<? extends Residence>> residenceTypes,
			ArrayList<Class<? extends EconomicGround>> groundTypes) {
		ArrayList<InfrastructureElementParameters> params = new ArrayList<InfrastructureElementParameters>();
		
		InfrastructureElementParameters param = new InfrastructureElementParameters();
		int size = population.blockSize;
		
		param.type = residenceTypes.get(0);
		param.sizeX = size-3;
		param.sizeZ = size-3;
		param.sizeY = 3;
		param.relOrigoX = 3;
		param.relOrigoY = 3;
		param.relOrigoZ = 3;
		
		params.add(param);
		
		//System.out.println("BigBlockInfrastructure : "+param.sizeX);
		
		return params;
	}

	@Override
	public void prepareBuildProgram(
			ArrayList<InfrastructureElementParameters> fixProperties,
			ArrayList<Class<? extends Residence>> residenceTypes,
			ArrayList<Class<? extends EconomicGround>> groundTypes) {

		// no need to do this
	}

}
