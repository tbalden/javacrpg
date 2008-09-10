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

package org.jcrpg.threed;

import com.jme.renderer.pass.DefaultShadowGate;
import com.jme.scene.TriMesh;

public class J3DShadowGate extends DefaultShadowGate {

	public J3DCore core;
	
	@Override
	public boolean shouldDrawShadows(TriMesh batch) {
		//if (true==true)return true;
		//int l = batch.getParentGeom().getName().length();
		//if (batch.getParentGeom().getName().charAt(l-1)=='3') return false;
		//float dS =batch.getParentGeom().getWorldTranslation().distanceSquared(core.getCamera().getLocation());
		//if (dS>J3DCore.RENDER_SHADOW_DISTANCE_SQR) return false;
		return true;
	}

	@Override
	public boolean shouldUpdateShadows(TriMesh batch) {
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(batch.getName());
		//if (System.currentTimeMillis()%30>27) return true;
		// TODO Auto-generated method stub
		return true;
	}

}
