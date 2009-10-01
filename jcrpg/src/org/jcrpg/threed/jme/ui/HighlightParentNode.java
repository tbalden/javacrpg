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

import java.util.ArrayList;

import org.jcrpg.threed.J3DCore;

import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState.StateType;

public class HighlightParentNode extends Node
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float fliedTime= 0f;
	private float maxTime = 1.5f;
	private boolean flying = false;
	private float speed = 0.1f;
	
	public ArrayList<Runnable> onFinish = null;
	
	LightState backupState;
	LightState replacementState;
	PointLight light = new PointLight();
	
	@Override
	public void updateGeometricState(float time, boolean initiator) {
		if (flying && fliedTime<maxTime) {
			light.setSpecular(new ColorRGBA(fliedTime/maxTime,fliedTime/maxTime,fliedTime/maxTime,1f));
			
			//localTranslation.addLocal(0f, speed*time, 0f);
			fliedTime+=1f*time;
			this.getParent().updateRenderState();
		} else
		if (flying && fliedTime>=maxTime)
		{
			this.getParent().setRenderState(backupState);
			this.removeFromParent();
			if (onFinish!=null)
			{
				for (Runnable r:onFinish)
				{
					r.run();
				}
			}
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
		Node n = getParent();
		if (n!=null) backupState = (LightState)n.getRenderState(StateType.Light);
		replacementState = J3DCore.getInstance().getDisplay().getRenderer().createLightState();
		n.setRenderState(replacementState);
		replacementState.setEnabled(true);
		replacementState.attach(light);
		light.setEnabled(true);
		flying = true;
	}
	public void startFlying(float speed, float maxTime)
	{
		Node n = getParent();
		if (n!=null) backupState = (LightState)n.getRenderState(StateType.Light);
		this.speed = speed;
		this.maxTime = maxTime;
		flying = true;
	}
}