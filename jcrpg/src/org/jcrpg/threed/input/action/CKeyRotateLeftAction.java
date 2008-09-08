package org.jcrpg.threed.input.action;


import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public class CKeyRotateLeftAction extends CKeyAction {
   

    public CKeyRotateLeftAction(ClassicKeyboardLookHandler handler, Camera camera, float speed) {
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
    }

    /**
     * <code>performAction</code> rotates the camera a certain angle.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public synchronized void performAction(InputActionEvent evt) {
    	if (!performActionCheck(evt)){
        	if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("locked...");
    		return;
    	}
    	
    	handler.lockHandling();
        if (handler.lookLeftRightPercent>0) {
        	// if looked away to right, bigger view needed
        	if (handler.sEngine.optimizeAngle) handler.sEngine.renderToViewPort(J3DCore.ROTATE_VIEW_ANGLE+0.6f);
        } else
        {
        	if (handler.sEngine.optimizeAngle) handler.sEngine.renderToViewPort(J3DCore.ROTATE_VIEW_ANGLE);
        }
    	Vector3f from = J3DCore.turningDirectionsUnit[handler.core.gameState.getCurrentRenderPositions().viewDirection];
    	Vector3f fromPos = J3DCore.getInstance().getCurrentLocation();
        handler.core.turnLeft();
        //if (J3DCore.OPTIMIZED_RENDERING) handler.sEngine.render(handler.core.gameState.getCurrentRenderPositions().viewPositionX,handler.core.gameState.getCurrentRenderPositions().viewPositionY,handler.core.gameState.getCurrentRenderPositions().viewPositionZ,false);
    	Vector3f toReach = J3DCore.turningDirectionsUnit[handler.core.gameState.getCurrentRenderPositions().viewDirection];
    	Vector3f toPos = J3DCore.getInstance().getCurrentLocation();
        float steps = J3DCore.MOVE_STEPS;
        turnDirectionAndMove(steps,from,toReach,fromPos,toPos,false);
        handler.lookUpDownPercent = 0;
        handler.lookLeftRightPercent = 0;
        setLookVertical(); // this should be always called to override bad camera view caused by performance related rotation skips
        camera.update();
        handler.core.updateDisplay(from);
    	handler.unlockHandling(true);
    }
}