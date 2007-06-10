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
public class CKeyRotateLeftAction extends KeyInputAction {
    //the camera to manipulate
    private Camera camera;
    //the axis to lock
    private Vector3f lockAxis;
    //temporary matrix for rotation
    private static final Matrix3f incr = new Matrix3f();
    
    ClassicKeyboardLookHandler handler;

    /**
     * Constructor instantiates a new <code>KeyRotateLeftAction</code> object.
     * 
     * @param camera
     *            the camera to rotate.
     * @param speed
     *            the speed at which to rotate.
     */
    public CKeyRotateLeftAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
        this.handler = handler;
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
    	System.out.println("performAction...");
    	if (handler.lock){
        	System.out.println("locked...");
    		return;
    	}
    	
    	handler.lockHandling();
    	Vector3f from = J3DCore.tDirections[handler.core.viewDirection];
        handler.core.turnLeft();
    	Vector3f toReach = J3DCore.tDirections[handler.core.viewDirection];
        float steps = J3DCore.MOVE_STEPS;
    	for (float i=0; i<=steps; i++)
        {
    		float x, y, z;
    		x = (1/steps)* i * toReach.x;
    		y = (1/steps)* i * toReach.y;
    		z = (1/steps)* i * toReach.z;
    		
    		x += (1/steps) * (steps-i) * from.x;
    		y += (1/steps) * (steps-i) * from.y;
    		z += (1/steps) * (steps-i) * from.z;
    		
        	System.out.println("ANGLING...");
    		camera.setDirection(new Vector3f(x,y,z));
    		
            camera.update();
            handler.core.updateCam();
     
        }
        System.out.println("SET FINAL DIR "+handler.core.viewDirection);
        camera.setDirection(J3DCore.tDirections[handler.core.viewDirection]);
        camera.update();
        handler.core.updateCam();
    	handler.unlockHandling(true);
    }
}