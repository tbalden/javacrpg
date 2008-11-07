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
package org.jcrpg.threed.jme.ui;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;

public class ZoomingQuad extends Quad
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean zooming = false;
	
	private float zoomTo = 0.5f;
	
	private Vector3f oldScale = new Vector3f(1f,1f,1f);
	private float currentTimeSpent = 0;
	private float fullTime = 1;
	
	public ZoomingQuad() {
		super();
	}

	public ZoomingQuad(String name, float width, float height) {
		super(name, width, height);
	}

	public ZoomingQuad(String name) {
		super(name);
	}
	

	@Override
	public void updateGeometricState(float time, boolean initiator) {
		if (zooming)
		{
			currentTimeSpent+=time;
			if (fullTime<currentTimeSpent)
			{
				currentTimeSpent = 0;
				zooming = false;
				setLocalScale(oldScale);
			} else
			{
				float ratio = currentTimeSpent/fullTime;
				if (ratio<=0.5f)
				{
				} else
				{
					ratio = 1f-ratio; 
				}
				ratio*=2f;
				setLocalScale(oldScale.mult(1f+(ratio*zoomTo)));
			}
		}
		super.updateGeometricState(time, initiator);
	}
	
	public void startZoomCycle()
	{
		oldScale = getLocalScale();
		zooming = true;
	}
}
