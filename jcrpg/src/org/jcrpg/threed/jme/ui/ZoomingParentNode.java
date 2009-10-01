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

public class ZoomingParentNode extends com.jme.scene.Node
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean zooming = false;
	
	private float zoomTo = 0.7f;
	
	private Vector3f oldScale = new Vector3f(1f,1f,1f);
	private float currentTimeSpent = 0;
	private float fullTime = 1.2f;
	
	public ZoomingParentNode() {
		super();
	}

	public ZoomingParentNode(String name) {
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
				getParent().setLocalScale(oldScale);
				removeFromParent();
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
				getParent().setLocalScale(oldScale.mult(1f+(ratio*zoomTo)));
			}
		}
		//getParent().updateGeometricState(time, initiator);
	}
	
	public void startZoomCycle()
	{
		oldScale = getParent().getLocalScale();
		zooming = true;
	}
}
