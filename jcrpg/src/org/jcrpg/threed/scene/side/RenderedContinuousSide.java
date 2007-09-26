/*
 *  This file is part of JavaCRPG.
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
package org.jcrpg.threed.scene.side;

import org.jcrpg.threed.scene.model.SimpleModel;

public class RenderedContinuousSide extends RenderedSide{

	public SimpleModel[] continuous;
	public SimpleModel[] oneSideContinuousNormal;
	public SimpleModel[] oneSideContinuousOpposite;
	public SimpleModel[] nonContinuous;
	
	public RenderedContinuousSide(SimpleModel[] objects, SimpleModel[] continuous, SimpleModel[] oneSideContinuousNormal,SimpleModel[] oneSideContinuousOpposite,SimpleModel[] nonContinuous)
	{
		super(objects);
		this.continuous = continuous;
		this.nonContinuous = nonContinuous;
		this.oneSideContinuousNormal = oneSideContinuousNormal;
		this.oneSideContinuousOpposite = oneSideContinuousOpposite;
		type = RS_CONTINUOUS;
	}
	
}
