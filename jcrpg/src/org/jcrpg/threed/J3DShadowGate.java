/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.threed;

import com.jme.renderer.pass.DefaultShadowGate;
import com.jme.scene.batch.TriangleBatch;

public class J3DShadowGate extends DefaultShadowGate {

	public J3DCore core;
	
	@Override
	public boolean shouldDrawShadows(TriangleBatch batch) {
		int l = batch.getParentGeom().getName().length();
		if (batch.getParentGeom().getName().charAt(l-1)=='3') return false;
		float dS =batch.getParentGeom().getWorldTranslation().distanceSquared(core.getCamera().getLocation());
		if (dS>J3DCore.RENDER_SHADOW_DISTANCE_SQR) return false;
		return true;
	}

	@Override
	public boolean shouldUpdateShadows(TriangleBatch batch) {
		//if (System.currentTimeMillis()%30>27) return true;
		// TODO Auto-generated method stub
		return true;
	}

}
