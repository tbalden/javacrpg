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

package org.jcrpg.threed.input;

import org.jcrpg.threed.J3DCore;

import com.jme.input.InputHandler;
import com.jme.input.MouseLookHandler;
import com.jme.renderer.Camera;

public class ClassicInputHandler  extends InputHandler {

    private MouseLookHandler mouseLookHandler;
    //private ClassicMouseLookHandler mouseLookHandler;
    private ClassicKeyboardLookHandler keyboardLookHandler;

    /**
     * @return handler for keyboard controls
     */
    public ClassicKeyboardLookHandler getKeyboardLookHandler() {
        return keyboardLookHandler;
    }

    /**
     * @return handler for mouse controls
     */
    public MouseLookHandler getMouseLookHandler() {
        return mouseLookHandler;
    }

    
    public ClassicInputHandler(J3DCore core, Camera cam)
    {
    	keyboardLookHandler = new ClassicKeyboardLookHandler(core,cam);
        addToAttachedHandlers( keyboardLookHandler );
    	mouseLookHandler = new MouseLookHandler(cam,1.0f);//ClassicMouseLookHandler(cam);
    	//mouseLookHandler.setLockAxis(new Vector3f(1f,1f,0));
    	enableMouse(false);
    	//mouseLookHandler = new ClassicMouseLookHandler(cam);
        addToAttachedHandlers( mouseLookHandler );
        org.lwjgl.input.Mouse.setGrabbed(false);
    }
    
    public void enableMouse(boolean state)
    {
    	mouseLookHandler.setEnabled(J3DCore.MOUSELOOK && state);
    }
    
    public void applyMouseSettings()
    {
    	enableMouse(J3DCore.MOUSELOOK);
    }
   
}

