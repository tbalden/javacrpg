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
    private Vector3f lockAxis;

    public CKeyLookDownAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
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
    	Vector3f toReach = J3DCore.turningDirectionsUnit[J3DCore.BOTTOM];
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