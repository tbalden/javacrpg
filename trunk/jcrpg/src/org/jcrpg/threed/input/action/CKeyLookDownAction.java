package org.jcrpg.threed.input.action;


import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.controls.controller.CameraController;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyRotateLeftAction</code> performs the action of rotating a camera a
 * certain angle. This angle is determined by the speed at which the camera can
 * turn and the time between frames.
 * 
 * @author Mark Powell
 * @version $Id: KeyRotateLeftAction.java,v 1.16 2006/09/29 22:30:17 nca Exp $
 */
public class CKeyLookDownAction extends CKeyAction {

    public CKeyLookDownAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
    	super(handler,camera);
        this.speed = speed;
    }


    /**
     * <code>performAction</code> rotates the camera a certain angle.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public synchronized void performAction(InputActionEvent evt) {
    	if (handler.lock){
        	System.out.println("locked...");
    		return;
    	}
    	
    	handler.lockHandling();

        handler.lookUpDownPercent-=4;
        if (handler.lookUpDownPercent<-50) handler.lookUpDownPercent = -50;
        
        setLookVertical();

        camera.update();
        handler.core.updateDisplay(J3DCore.turningDirectionsUnit[handler.core.viewDirection]);
    	handler.unlockHandling(false);
    }
}