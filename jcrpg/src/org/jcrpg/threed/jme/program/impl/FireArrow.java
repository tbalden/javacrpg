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

import java.io.File;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.jme.program.EffectNode;

import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

public class FireArrow extends EffectNode {

	  private ParticleMesh pMesh;
	private Box debugBox;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FireArrow()
	{
		    AlphaState as1 = J3DCore.getInstance().getDisplay().getRenderer().createAlphaState();
		    as1.setBlendEnabled(true);
		    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		    as1.setDstFunction(AlphaState.DB_ONE);
		    as1.setTestEnabled(true);
		    as1.setTestFunction(AlphaState.TF_GREATER);
		    as1.setEnabled(true);
	
		    TextureState ts = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		    try {
		    ts.setTexture(
		        TextureManager.loadTexture(new File("data/flaresmall.jpg").toURL(),
		        Texture.MM_LINEAR_LINEAR,
		        Texture.FM_LINEAR));
		    } catch (Exception ex){}
		    ts.setEnabled(true);
	
		    pMesh = ParticleFactory.buildParticles("particles", 300);
		    pMesh.setEmissionDirection(new Vector3f(0,1,0));
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
    
	        ZBufferState zstate = J3DCore.getInstance().getDisplay().getRenderer().createZBufferState();
	        zstate.setEnabled(false);
	        pMesh.setRenderState(zstate);

		    pMesh.setModelBound(new BoundingSphere());
		    pMesh.updateModelBound();
	        
	        //debugBox = new Box("box",new Vector3f(1f,1f,1f),new Vector3f(1f,1f,1f));
	        //debugBox.setModelBound(new BoundingBox());
	        //debugBox.updateModelBound();
	        
	        //this.attachChild(debugBox);

	        this.attachChild(pMesh);
	}



	@Override
	public void setPosition(Vector3f newPos) {
		currentPos = newPos;
		pMesh.setOriginOffset(currentPos);
        //debugBox.setLocalTranslation(newPos);
	}

}
