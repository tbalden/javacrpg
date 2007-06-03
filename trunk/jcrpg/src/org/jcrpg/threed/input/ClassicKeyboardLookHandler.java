package org.jcrpg.threed.input;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.action.CKeyBackwardAction;
import org.jcrpg.threed.input.action.CKeyForwardAction;
import org.jcrpg.threed.input.action.CKeyRotateLeftAction;
import org.jcrpg.threed.input.action.CKeyRotateRightAction;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyBackwardAction;
import com.jme.input.action.KeyLookDownAction;
import com.jme.input.action.KeyLookUpAction;
import com.jme.input.action.KeyRotateLeftAction;
import com.jme.input.action.KeyRotateRightAction;
import com.jme.input.action.KeyStrafeLeftAction;
import com.jme.input.action.KeyStrafeRightAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class ClassicKeyboardLookHandler  extends InputHandler {
    private CKeyRotateRightAction right;
    private CKeyRotateLeftAction left;
    
    public J3DCore core;

    public ClassicKeyboardLookHandler( J3DCore core, Camera cam ) {
    	this.core = core;
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        
        if (lock) return;

        keyboard.set( "forward", KeyInput.KEY_W );
        keyboard.set( "backward", KeyInput.KEY_S );
        //keyboard.set( "strafeLeft", KeyInput.KEY_A );
        //keyboard.set( "strafeRight", KeyInput.KEY_D );
        //keyboard.set( "lookUp", KeyInput.KEY_UP );
        //keyboard.set( "lookDown", KeyInput.KEY_DOWN );
        //keyboard.set( "turnRight", KeyInput.KEY_RIGHT );
        //keyboard.set( "turnLeft", KeyInput.KEY_LEFT );
        keyboard.set( "turnRight", KeyInput.KEY_D );
        keyboard.set( "turnLeft", KeyInput.KEY_A );
        
        float moveSpeed = 0.001f;
        float rotateSpeed = 0.05f;

        addAction( new CKeyForwardAction( this, cam, moveSpeed ), "forward", true );
        addAction( new CKeyBackwardAction( this, cam, moveSpeed ), "backward", true );
        //addAction( new KeyStrafeLeftAction( cam, moveSpeed ), "strafeLeft", true );
        //addAction( new KeyStrafeRightAction( cam, moveSpeed ), "strafeRight", true );
        //addAction( new KeyLookUpAction( cam, rotateSpeed ), "lookUp", true );
        //addAction( new KeyLookDownAction( cam, rotateSpeed ), "lookDown", true );
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
	
    boolean lock = false;
    
    public synchronized void lockHandling()
    {
    	lock = true;
    }
    public synchronized void unlockHandling(boolean wait)
    {
    	if (wait) try 
    	{
    		Thread.sleep(500);
    	} catch (Exception ex)
    	{
    		
    	}
    	lock = false;
    }
    
}
