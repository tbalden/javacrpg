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
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

public class FireArrow extends EffectNode {

	private ParticleMesh pMesh;
	//private Box debugBox;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FireArrow() {
		AlphaState as1 = J3DCore.getInstance().modelLoader.alphaStateBase;
		ZBufferState zstate = J3DCore.getInstance().modelLoader.zBufferStateOff;
		TextureState ts = J3DCore.getInstance().modelLoader
				.loadTextureStates(new String[] { "flaresmall.jpg" })[0];
		
		pMesh = cacheMesh.get(this.getClass());
		
		if (pMesh==null)
		{

			pMesh = ParticleFactory.buildParticles("particles", 300);
			pMesh.setEmissionDirection(new Vector3f(0, 1, 0));
			pMesh.setInitialVelocity(.006f);
			pMesh.setStartSize(0.25f);
			pMesh.setEndSize(0.15f);
			pMesh.setMinimumLifeTime(1200f);
			pMesh.setMaximumLifeTime(1400f);
			pMesh.setStartColor(new ColorRGBA(1, 0, 0, 1));
			pMesh.setEndColor(new ColorRGBA(0, 1, 0, 0));
			pMesh.setMaximumAngle(360f * FastMath.DEG_TO_RAD);
			pMesh.getParticleController().setControlFlow(false);
			pMesh.warmUp(60);
	
			pMesh.setRenderState(as1);
			pMesh.setRenderState(ts);
			pMesh.setRenderState(zstate);
	
			pMesh.setModelBound(new BoundingSphere());
			pMesh.updateModelBound();
			cacheMesh.put(this.getClass(), pMesh);
		} else
		{
			pMesh.setOriginOffset(new Vector3f(0,0,0));
		}

		this.attachChild(pMesh);
	}

	@Override
	public void setPosition(Vector3f newPos, Quaternion newAngle) {
		currentPos = newPos;
		pMesh.setOriginOffset(currentPos);
		super.setPosition(newPos,newAngle);
	}
	
	@Override
	public void clearUp()
	{
		pMesh.removeFromParent();
	}

}
