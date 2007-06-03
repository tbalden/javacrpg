package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.controls.controller.CameraController;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class CKeyForwardAction extends KeyInputAction {
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
    public CKeyForwardAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
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
        Vector3f loc = camera.getLocation();
        float oldZ = loc.getZ();
        if ( !camera.isParallelProjection() ) {
    		loc.addLocal(J3DCore.directions[handler.core.viewDirection]);//camera.getDirection().mult(10.0f * evt.getTime(), tempVa));//speed
/*    		while (true)
        	{
        		loc.addLocal(camera.getDirection().mult(speed * evt.getTime(), tempVa));
        		camera.update();
        		if (oldZ-0.1f >= loc.getZ())
        		{
        			loc.setZ(oldZ-0.1f);
        			camera.update();
        			break;
        		}
        	}*/
        } else {
            // move up instead of forward if in parallel mode
            loc.addLocal(camera.getUp().mult(speed * evt.getTime(), tempVa));
        }
        camera.update();
        handler.unlockHandling(true);
    }

}
