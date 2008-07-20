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
package org.jcrpg.threed.jme.program;

import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;

import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jmex.effects.particles.ParticleMesh;

public abstract class EffectNode extends Node {
	
	public static HashMap< Class<? extends EffectNode>, ParticleMesh> cacheMesh = new HashMap<Class<? extends EffectNode>, ParticleMesh>();

	public static LightNode light = null;
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;
	
	public float speed = 1f;
	
	public Vector3f currentPos = new Vector3f(0,0,0);
	
	public Vector3f startingPos = new Vector3f(0,0,0);
	
	public Node modelNode = null;
	
	public void setPosition(Vector3f newPos,Quaternion angle)
	{
		if (modelNode!=null && angle!=null) modelNode.setLocalRotation(angle);
		if (modelNode!=null)
		{
			this.attachChild(modelNode);
			modelNode.setLocalTranslation(newPos);
		}
	}
	public Quaternion getAngle()
	{
		if (modelNode!=null) return modelNode.getLocalRotation();
		return null;
	}
	
	
	public void addModelObject(SimpleModel model)
	{
		modelNode = J3DCore.getInstance().modelLoader.loadNodeOriginal(model, false);
		
	}
	
	public Node getModelNode()
	{
		return modelNode;
	}
	
	public VisibleLifeForm sourceForm = null;
	public VisibleLifeForm targetForm = null;

	public boolean startedPlaying = false;
	
	public void clearUp()
	{
		if (light!=null) 
		{
			light.removeFromParent();
			light.getLight().setEnabled(false);
		}
	}
	
	public static LightNode getLightNode()
	{
		if (light==null)
		{
			LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
			light = new LightNode("light",ls);
			PointLight pointLight = new PointLight();
			pointLight.setAttenuate(false);
			light.setLight(pointLight);
			pointLight.setEnabled(true);
			light.setTarget(J3DCore.getInstance().getRootNode1());		
		}
		light.removeFromParent();
		light.getLight().setEnabled(true);
		return light;
	}

}
