/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class CKeyBackwardAction extends CKeyAction {

	public CKeyBackwardAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
		super(handler,camera);
        this.speed = speed;
    }

    /**
     * <code>performAction</code> moves the camera along it's positive
     * direction vector at a speed of movement speed * time. Where time is the
     * time between frames and 1 corresponds to 1 second.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
    	if (!performActionCheck(evt) || handler.secLock){
        	//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("locked...");
    		return;
    	}
    	handler.lockHandling();
        
        Vector3f from = handler.core.getCurrentLocation();
        if (handler.core.moveBackward(handler.core.gameState.getCurrentRenderPositions().viewDirection)) {
	        Vector3f toReach = handler.core.getCurrentLocation();
	        float steps = J3DCore.MOVE_STEPS;
	        movePosition(steps, from, toReach);
	    	handler.core.setCalculatedCameraLocation();
	        camera.update();
           	handler.sEngine.renderToViewPort();
       }
        handler.unlockHandling(true);
        
    }

}
