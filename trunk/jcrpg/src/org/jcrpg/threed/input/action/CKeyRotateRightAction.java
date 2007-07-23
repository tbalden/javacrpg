package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class CKeyRotateRightAction extends CKeyAction {
    //the axis to lock
    private Vector3f lockAxis;
    

    public CKeyRotateRightAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
    	super(handler,camera);
        this.speed = speed;
    }

    public void setLockAxis(Vector3f lockAxis) {
        this.lockAxis = lockAxis;
    }

    /**
     * <code>performAction</code> rotates the camera a certain angle.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
    	if (handler.lock){
        	System.out.println("locked...");
    		return;
    	}
    	handler.lockHandling();
    	Vector3f from = J3DCore.turningDirectionsUnit[handler.core.viewDirection];
        handler.core.turnRight();
        if (J3DCore.OPTIMIZED_RENDERING) handler.core.render();
    	Vector3f toReach = J3DCore.turningDirectionsUnit[handler.core.viewDirection];
        float steps = J3DCore.MOVE_STEPS;
        turnDirection(steps, from, toReach);
        handler.lookUpDownPercent = 0;
        setLookVertical(); // this should be always called to override bad camera view caused by performance related rotation skips
        camera.update();
        handler.core.updateDisplay(from);
    	handler.unlockHandling(true);
    }
}