package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyRotateRightAction</code> performs the action of rotating a camera
 * a certain angle. This angle is determined by the speed at which the camera
 * can turn and the time between frames.
 * 
 * @author Mark Powell
 * @version $Id: KeyRotateRightAction.java,v 1.16 2006/09/29 22:30:18 nca Exp $
 */
public class CKeyRotateRightAction extends KeyInputAction {
    //temporary matrix to hold rotation
    private static final Matrix3f incr = new Matrix3f();
    //camera to manipulate
    private Camera camera;
    //the axis to lock
    private Vector3f lockAxis;
    
    private ClassicKeyboardLookHandler handler;

    /**
     * Constructor instantiates a new <code>KeyRotateLeftAction</code> object.
     * 
     * @param camera
     *            the camera to rotate.
     * @param speed
     *            the speed at which to rotate.
     */
    public CKeyRotateRightAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
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
    public void performAction(InputActionEvent evt) {
    	handler.lockHandling();
        /*if (lockAxis == null) {
            incr.fromAngleNormalAxis(-speed * evt.getTime(), camera.getUp());
        } else {
            incr.fromAngleNormalAxis(-speed * evt.getTime(), lockAxis);
        }
        incr.mult(camera.getUp(), camera.getUp());
        incr.mult(camera.getLeft(), camera.getLeft());
        incr.mult(camera.getDirection(), camera.getDirection());*/
        //camera.setDirection(direction);
        handler.core.turnRight();
        camera.setDirection(J3DCore.directions[handler.core.viewDirection]);
        //camera.normalize();
        camera.update();
        handler.unlockHandling(true);
    }
}