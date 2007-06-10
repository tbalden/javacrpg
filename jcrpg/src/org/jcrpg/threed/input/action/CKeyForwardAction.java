package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.controls.controller.CameraController;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class CKeyForwardAction extends CKeyAction {

	public CKeyForwardAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
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
    	if (handler.lock){
        	System.out.println("locked...");
    		return;
    	}
    	handler.lockHandling();
        
        Vector3f from = handler.core.getCurrentLocation();
        handler.core.moveForward(handler.core.viewDirection);
        handler.core.render();
        Vector3f toReach = handler.core.getCurrentLocation();        
        float steps = J3DCore.MOVE_STEPS;
        movePosition(steps, from, toReach);
        handler.core.setCalculatedCameraLocation();
        camera.update();
        handler.unlockHandling(true);
    }

}
