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
import org.jcrpg.threed.input.action.CKeyBackwardAction;
import org.jcrpg.threed.input.action.CKeyDownAction;
import org.jcrpg.threed.input.action.CKeyForwardAction;
import org.jcrpg.threed.input.action.CKeyLookDownAction;
import org.jcrpg.threed.input.action.CKeyLookLeftAction;
import org.jcrpg.threed.input.action.CKeyLookRightAction;
import org.jcrpg.threed.input.action.CKeyLookUpAction;
import org.jcrpg.threed.input.action.CKeyRotateLeftAction;
import org.jcrpg.threed.input.action.CKeyRotateRightAction;
import org.jcrpg.threed.input.action.CKeyStrafeLeftAction;
import org.jcrpg.threed.input.action.CKeyStrafeRightAction;
import org.jcrpg.threed.input.action.CKeyUpAction;
import org.jcrpg.threed.input.menu.CKeyMenu;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class ClassicKeyboardLookHandler  extends InputHandler {
    private CKeyRotateRightAction right;
    private CKeyRotateLeftAction left;
    
    public J3DCore core;
    
    public int lookUpDownPercent = 0;
    public int lookLeftRightPercent = 0;

    public ClassicKeyboardLookHandler( J3DCore core, Camera cam ) {
    	this.core = core;
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        
        if (lock) return;

        keyboard.set( "forward", KeyInput.KEY_W );
        keyboard.set( "backward", KeyInput.KEY_S );
        keyboard.set( "strafeLeft", KeyInput.KEY_Q );
        keyboard.set( "strafeRight", KeyInput.KEY_E );
        keyboard.set( "lookUp", KeyInput.KEY_UP );
        keyboard.set( "lookDown", KeyInput.KEY_DOWN );
        keyboard.set( "lookLeft", KeyInput.KEY_LEFT );
        keyboard.set( "lookRight", KeyInput.KEY_RIGHT );
        keyboard.set( "climbUp", KeyInput.KEY_R );
        keyboard.set( "climbDown", KeyInput.KEY_F );
        keyboard.set( "turnRight", KeyInput.KEY_D );
        keyboard.set( "turnLeft", KeyInput.KEY_A );
        
        keyboard.set( "worldMap", KeyInput.KEY_F1 );
        keyboard.set( "logUp", KeyInput.KEY_PGUP );
        keyboard.set( "logDown", KeyInput.KEY_PGDN );
        keyboard.set( "Y", KeyInput.KEY_Y );
       
        float moveSpeed = 0.001f;
        float rotateSpeed = 1.0f;

        addAction( new CKeyForwardAction( this, cam, moveSpeed ), "forward", true );
        addAction( new CKeyBackwardAction( this, cam, moveSpeed ), "backward", true );
        addAction( new CKeyStrafeLeftAction( this, cam, moveSpeed ), "strafeLeft", true );
        addAction( new CKeyStrafeRightAction( this, cam, moveSpeed ), "strafeRight", true );
        addAction( new CKeyLookUpAction( this, cam, rotateSpeed ), "lookUp", true );
        addAction( new CKeyLookDownAction( this, cam, rotateSpeed ), "lookDown", true );
        addAction( new CKeyLookLeftAction( this, cam, rotateSpeed ), "lookLeft", true );
        addAction( new CKeyLookRightAction( this, cam, rotateSpeed ), "lookRight", true );
        addAction( new CKeyUpAction( this, cam, rotateSpeed ), "climbUp", true );
        addAction( new CKeyDownAction( this, cam, rotateSpeed ), "climbDown", true );
        addAction( new CKeyMenu(this), "worldMap", false);
        addAction( new CKeyMenu(this), "logUp", false);
        addAction( new CKeyMenu(this), "logDown", false);
        addAction( new CKeyMenu(this), "Y", false);
        right = new CKeyRotateRightAction( this, cam, rotateSpeed );
        right.setLockAxis(new Vector3f(cam.getUp()));
        addAction(right, "turnRight", true );
        left = new CKeyRotateLeftAction( this, cam, rotateSpeed );
        left.setLockAxis(new Vector3f(cam.getUp()));
        addAction( left, "turnLeft", true );
    }
    
    public void setLockAxis(Vector3f lock) {
        right.setLockAxis(new Vector3f(lock));
        left.setLockAxis(new Vector3f(lock));
    }
	
    public boolean lock = false;
    /**
     * For concurrency and minor locking later.
     */
    public boolean secLock = false;
    private long timeLockStart = 0;
    
    public synchronized void lockSecondaryHandling()
    {
    	secLock = true;
    }
    public synchronized void unlockSecondaryHandling()
    {
    	secLock = false;
    	lock = false;
    }

    public synchronized void lockHandling()
    {
    	lock = true;
    	timeLockStart = System.currentTimeMillis();
    }
    public synchronized void unlockHandling(boolean wait)
    {
    	if (wait) try 
    	{
    		long waitPlus = 000 - (System.currentTimeMillis() - timeLockStart);
    		if ( (waitPlus) > 0 ) Thread.sleep( waitPlus );
    	} catch (Exception ex)
    	{
    		
    	}
    	if (!secLock) {
    		lock = false;
    	}
    }
    
}
