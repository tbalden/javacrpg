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
package org.jcrpg.threed.jme.program.impl;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.program.EffectNode;

import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.ZBufferState;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticlePoints;

public class FireDots extends EffectNode {

	private ParticlePoints pPoints;
	private Box debugBox;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FireDots()
	{
	       pPoints = ParticleFactory.buildPointParticles("particles", 300);
	        pPoints.setPointSize(5);
	        pPoints.setAntialiased(true);
	        pPoints.setEmissionDirection(new Vector3f(0, 1, 0));
	        pPoints.setOriginOffset(new Vector3f(0, 0, 0));
	        pPoints.setInitialVelocity(.006f);
	        pPoints.setStartSize(2.5f);
	        pPoints.setEndSize(.5f);
	        pPoints.setMinimumLifeTime(1200f);
	        pPoints.setMaximumLifeTime(1400f);
	        pPoints.setStartColor(new ColorRGBA(1, 0, 0, 1));
	        pPoints.setEndColor(new ColorRGBA(0, 1, 0, 0));
	        pPoints.setMaximumAngle(360f * FastMath.DEG_TO_RAD);
	        pPoints.getParticleController().setControlFlow(false);
	        pPoints.warmUp(120);

	        AlphaState as1 = J3DCore.getInstance().getDisplay().getRenderer().createAlphaState();
	        as1.setBlendEnabled(true);
	        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	        as1.setDstFunction(AlphaState.DB_ONE);
	        as1.setEnabled(true);
	        this.setRenderState(as1);

	        ZBufferState zstate = J3DCore.getInstance().getDisplay().getRenderer().createZBufferState();
	        zstate.setEnabled(false);
	        pPoints.setRenderState(zstate);

	        pPoints.setModelBound(new BoundingSphere());
	        pPoints.updateModelBound();
	        
	        //debugBox = new Box("box",new Vector3f(1f,1f,1f),new Vector3f(1f,1f,1f));
	        //debugBox.setModelBound(new BoundingBox());
	        //debugBox.updateModelBound();
	        
	        //this.attachChild(debugBox);

	        this.attachChild(pPoints);
	}



	@Override
	public void setPosition(Vector3f newPos) {
		currentPos = newPos;
        pPoints.setOriginOffset(currentPos);
        //debugBox.setLocalTranslation(newPos);
	}

}
