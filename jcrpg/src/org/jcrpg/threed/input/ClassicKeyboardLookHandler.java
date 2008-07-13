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
import org.jcrpg.threed.input.menu.CKeyCamp;
import org.jcrpg.threed.input.menu.CKeyMenu;
import org.jcrpg.threed.standing.J3DStandingEngine;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class ClassicKeyboardLookHandler  extends InputHandler {
    private CKeyRotateRightAction right;
    private CKeyRotateLeftAction left;
    
    public J3DCore core;
    public J3DStandingEngine sEngine;
    
    public int lookUpDownPercent = 0;
    public int lookLeftRightPercent = 0;
    
    public boolean eventCatched = false;

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
        
        keyboard.set( "camp", KeyInput.KEY_C );
        keyboard.set( "worldMap", KeyInput.KEY_F1 );
        keyboard.set( "behaviorWindow", KeyInput.KEY_F2 );
        keyboard.set( "charSheetWindow", KeyInput.KEY_F3 );
        keyboard.set( "inventoryWindow", KeyInput.KEY_F4 );
        keyboard.set( "partyOrderWindow", KeyInput.KEY_F5 );
        keyboard.set( "mainMenu", KeyInput.KEY_F10 );
        keyboard.set( "cacheStateInfo", KeyInput.KEY_F11 );
        keyboard.set( "logUp", KeyInput.KEY_PGUP );
        keyboard.set( "logDown", KeyInput.KEY_PGDN );
        keyboard.set( "enter", KeyInput.KEY_RETURN );
        keyboard.set( "back", KeyInput.KEY_BACK);
        keyboard.set( "A", KeyInput.KEY_A );
        if (!J3DCore.FREE_MOVEMENT)
        	keyboard.set( "B", KeyInput.KEY_B );
        keyboard.set( "C", KeyInput.KEY_C );
        keyboard.set( "D", KeyInput.KEY_D );
        keyboard.set( "E", KeyInput.KEY_E );
        keyboard.set( "F", KeyInput.KEY_F );
        keyboard.set( "G", KeyInput.KEY_G );
        keyboard.set( "H", KeyInput.KEY_H );
        keyboard.set( "I", KeyInput.KEY_I );
        keyboard.set( "J", KeyInput.KEY_J );
        keyboard.set( "K", KeyInput.KEY_K );
        if (!J3DCore.FREE_MOVEMENT) // if not debug set this
        	keyboard.set( "L", KeyInput.KEY_L );
        keyboard.set( "M", KeyInput.KEY_M );
        keyboard.set( "N", KeyInput.KEY_N );
        keyboard.set( "O", KeyInput.KEY_O );
        keyboard.set( "P", KeyInput.KEY_P );
        keyboard.set( "Q", KeyInput.KEY_Q );
        keyboard.set( "R", KeyInput.KEY_R );
        keyboard.set( "S", KeyInput.KEY_S );
        if (!J3DCore.FREE_MOVEMENT) // if not debug set this
        	keyboard.set( "T", KeyInput.KEY_T );
        keyboard.set( "U", KeyInput.KEY_U );
        keyboard.set( "V", KeyInput.KEY_V );
        keyboard.set( "W", KeyInput.KEY_W );
        keyboard.set( "X", KeyInput.KEY_X );
        keyboard.set( "Y", KeyInput.KEY_Y );
        keyboard.set( "Z", KeyInput.KEY_Z );
        keyboard.set( "0", KeyInput.KEY_0 );
        keyboard.set( "1", KeyInput.KEY_1 );
        keyboard.set( "2", KeyInput.KEY_2 );
        keyboard.set( "3", KeyInput.KEY_3 );
        keyboard.set( "4", KeyInput.KEY_4 );
        keyboard.set( "5", KeyInput.KEY_5 );
        keyboard.set( "6", KeyInput.KEY_6 );
        keyboard.set( "7", KeyInput.KEY_7 );
        keyboard.set( "8", KeyInput.KEY_8 );
        keyboard.set( "9", KeyInput.KEY_9 );
        keyboard.set( "space", KeyInput.KEY_SPACE );
        keyboard.set( "delete", KeyInput.KEY_DELETE );
        keyboard.set( "shift", KeyInput.KEY_LSHIFT);
       
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

        right = new CKeyRotateRightAction( this, cam, rotateSpeed );
        right.setLockAxis(new Vector3f(cam.getUp()));
        addAction(right, "turnRight", true );
        left = new CKeyRotateLeftAction( this, cam, rotateSpeed );
        left.setLockAxis(new Vector3f(cam.getUp()));
        addAction( left, "turnLeft", true );

        addAction( new CKeyCamp(this), "camp", true);
        
        // add these to menu too
 
        addAction( new CKeyMenu(this), "worldMap", false);
        addAction( new CKeyMenu(this), "behaviorWindow", false);
        addAction( new CKeyMenu(this), "inventoryWindow", false);
        addAction( new CKeyMenu(this), "charSheetWindow", false);
        addAction( new CKeyMenu(this), "partyOrderWindow", false);
        addAction( new CKeyMenu(this), "mainMenu", false);
        addAction( new CKeyMenu(this), "cacheStateInfo", false);
        addAction( new CKeyMenu(this), "logUp", false);
        addAction( new CKeyMenu(this), "logDown", false);
        addAction( new CKeyMenu(this), "A", false);
        if (!J3DCore.FREE_MOVEMENT)
        	addAction( new CKeyMenu(this), "B", false);
        addAction( new CKeyMenu(this), "C", false);
        addAction( new CKeyMenu(this), "D", false);
        addAction( new CKeyMenu(this), "E", false);
        addAction( new CKeyMenu(this), "F", false);
        addAction( new CKeyMenu(this), "G", false);
        addAction( new CKeyMenu(this), "H", false);
        addAction( new CKeyMenu(this), "I", false);
        addAction( new CKeyMenu(this), "J", false);
        addAction( new CKeyMenu(this), "K", false);
        if (!J3DCore.FREE_MOVEMENT)
        	addAction( new CKeyMenu(this), "L", false);
        addAction( new CKeyMenu(this), "M", false);
        if (!J3DCore.FREE_MOVEMENT)
        	addAction( new CKeyMenu(this), "N", false);
        addAction( new CKeyMenu(this), "O", false);
        addAction( new CKeyMenu(this), "P", false);
        addAction( new CKeyMenu(this), "Q", false);
        addAction( new CKeyMenu(this), "R", false);
        addAction( new CKeyMenu(this), "S", false);
        if (!J3DCore.FREE_MOVEMENT)
        	addAction( new CKeyMenu(this), "T", false);
        addAction( new CKeyMenu(this), "U", false);
        addAction( new CKeyMenu(this), "V", false);
        addAction( new CKeyMenu(this), "W", false);
        addAction( new CKeyMenu(this), "X", false);
        addAction( new CKeyMenu(this), "Y", false);
        addAction( new CKeyMenu(this), "Z", false);
        addAction( new CKeyMenu(this), "0", false);
        addAction( new CKeyMenu(this), "1", false);
        addAction( new CKeyMenu(this), "2", false);
        addAction( new CKeyMenu(this), "3", false);
        addAction( new CKeyMenu(this), "4", false);
        addAction( new CKeyMenu(this), "5", false);
        addAction( new CKeyMenu(this), "6", false);
        addAction( new CKeyMenu(this), "7", false);
        addAction( new CKeyMenu(this), "8", false);
        addAction( new CKeyMenu(this), "9", false);
        addAction( new CKeyMenu(this), "enter", false);
        addAction( new CKeyMenu(this), "shift", false);
        addAction( new CKeyMenu(this), "space", false);
        addAction( new CKeyMenu(this), "delete", false);
        addAction( new CKeyMenu(this), "back", false);
        addAction( new CKeyMenu(this), "lookUp", false);
        addAction( new CKeyMenu(this), "lookDown", false);
        addAction( new CKeyMenu(this), "lookLeft", false);
        addAction( new CKeyMenu(this), "lookRight", false);
 
    }
    
    public void setLockAxis(Vector3f lock) {
        right.setLockAxis(new Vector3f(lock));
        left.setLockAxis(new Vector3f(lock));
    }
    
    public boolean noToggleWindowByKey = false;
	
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
    	//if (!secLock) {
    	if (core.uiBase.activeWindows.size()==0) // only if now window is active, should we unlock.
    	{
    		lock = false;
    	}
    	//}
    }
    
    public void setCurrentStandingEngine(J3DStandingEngine e)
    {
    	sEngine = e;
    }
    
}
