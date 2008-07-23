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
package org.jcrpg.threed.jme.ui;

import com.jme.scene.Node;

public class FlyingNode extends Node
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float fliedTime= 0f;
	private float maxTime = 1.5f;
	private boolean flying = false;
	private float speed = 0.1f;
	
	@Override
	public void updateGeometricState(float time, boolean initiator) {
		if (flying && fliedTime<maxTime) {
			localTranslation.addLocal(0f, speed*time, 0f);
			fliedTime+=1f*time;
		} else
		if (flying && fliedTime>=maxTime)
		{
			this.removeFromParent();
			return;
		}
		super.updateGeometricState(time, initiator);
	}
	
	public boolean isFinishedPlaying()
	{
		if (fliedTime>=maxTime)
		{
			return true;
		}
		return false;
	}
	public void startFlying()
	{
		flying = true;
	}
	public void startFlying(float speed, float maxTime)
	{
		this.speed = speed;
		this.maxTime = maxTime;
		flying = true;
	}
}