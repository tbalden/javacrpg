package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.controls.controller.CameraController;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class CKeyStrafeRightAction extends KeyInputAction {
    //temp holder for the multiplication of the direction and time
    private static final Vector3f tempVa = new Vector3f();
    //the camera to manipulate
    private Camera camera;
    
    ClassicKeyboardLookHandler handler;
    /**
     * Constructor instantiates a new <code>KeyForwardAction</code> object.
     * 
     * @param camera
     *            the camera that will be affected by this action.
     * @param speed
     *            the speed at which the camera can move.
     */
    public CKeyStrafeRightAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
        this.handler = handler;
    }

    /**
     * <code>performAction</code> moves the camera along it's positive
     * direction vector at a speed of movement speed * time. Where time is the
     * time between frames and 1 corresponds to 1 second.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
    	handler.lockHandling();
        
        Vector3f from = handler.core.getCurrentLocation();
        handler.core.moveRight(handler.core.viewDirection);
        Vector3f toReach = handler.core.getCurrentLocation();
        
        float steps = J3DCore.MOVE_STEPS;
    	for (float i=0; i<=steps; i++)
        {
    		float x, y, z;
    		x = (1f/steps)* (i) * (toReach.x);
    		y = (1f/steps)* (i) * (toReach.y);
    		z = (1f/steps)* (i) * (toReach.z);
    		
    		x += (1f/steps) * (steps-i) * from.x;
    		y += (1f/steps) * (steps-i) * from.y;
    		z += (1f/steps) * (steps-i) * from.z;
    		
    		camera.setLocation(new Vector3f(x,y,z));
    		
            camera.update();
            handler.core.updateCam();
     
        }
    	handler.core.setCalculatedCameraLocation();
        camera.update();
        handler.core.render();
        handler.unlockHandling(true);
    }

}
