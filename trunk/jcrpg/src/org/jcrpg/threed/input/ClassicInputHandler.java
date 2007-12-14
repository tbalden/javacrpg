/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.threed.input;

import org.jcrpg.threed.J3DCore;

import com.jme.input.InputHandler;
import com.jme.renderer.Camera;

public class ClassicInputHandler  extends InputHandler {

    //private MouseLookHandler mouseLookHandler;
    private ClassicMouseLookHandler mouseLookHandler;
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
    public ClassicMouseLookHandler getMouseLookHandler() {
        return mouseLookHandler;
    }

    
    public ClassicInputHandler(J3DCore core, Camera cam)
    {
    	keyboardLookHandler = new ClassicKeyboardLookHandler(core,cam);
        addToAttachedHandlers( keyboardLookHandler );
    	//mouseLookHandler = new MouseLookHandler(cam,2.0f);//ClassicMouseLookHandler(cam);
    	mouseLookHandler = new ClassicMouseLookHandler(cam);
        addToAttachedHandlers( mouseLookHandler );
        org.lwjgl.input.Mouse.setGrabbed(false);    	
    }
    
}
