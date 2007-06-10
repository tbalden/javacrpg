package org.jcrpg.threed.input.action;


import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.controls.controller.CameraController;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class CKeyLookUpAction extends CKeyAction {
    //the axis to lock
    private Vector3f lockAxis;

    public CKeyLookUpAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
    	super(handler,camera);
        this.speed = speed;
    }

    /**
     * 
     * <code>setLockAxis</code> allows a certain axis to be locked, meaning
     * the camera will always be within the plane of the locked axis. For
     * example, if the camera is a first person camera, the user might lock the
     * camera's up vector. This will keep the camera vertical of the ground.
     * 
     * @param lockAxis
     *            the axis to lock - should be unit length (normalized).
     */
    public void setLockAxis(Vector3f lockAxis) {
        this.lockAxis = lockAxis;
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
    	Vector3f from = J3DCore.turningDirectionsUnit[handler.core.viewDirection];
        //handler.core.turnLeft();
    	Vector3f toReach = J3DCore.turningDirectionsUnit[J3DCore.TOP];
        float steps = J3DCore.MOVE_STEPS*2;
        moveDirection(steps, from, toReach, true);
        try {
        	Thread.sleep(1000);
        }catch (Exception ex)
        {
        	
        }
        moveDirection(steps, toReach, from, true);
        camera.setDirection(J3DCore.turningDirectionsUnit[handler.core.viewDirection]);
        camera.update();
        handler.core.updateCam();
    	handler.unlockHandling(true);
    }
}