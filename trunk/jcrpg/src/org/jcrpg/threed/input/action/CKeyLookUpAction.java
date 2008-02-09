package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.renderer.Camera;

public class CKeyLookUpAction extends CKeyAction {

	public CKeyLookUpAction(ClassicKeyboardLookHandler handler, Camera camera,
			float speed) {
		super(handler, camera);
		this.speed = speed;
	}

	static int renderToViewPort = 0;

	/**
	 * <code>performAction</code> rotates the camera a certain angle.
	 * 
	 * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
	 */
	public synchronized void performAction(InputActionEvent evt) {
		if (handler.lock) {
			//System.out.println("locked...");
			return;
		}

		handler.lockHandling();


		if (handler.lookLeftRightPercent!=0)
		{
			handler.core.sEngine.renderToViewPort(J3DCore.ROTATE_VIEW_ANGLE);
		}
		handler.lookUpDownPercent += 8;
		if (handler.lookUpDownPercent > 100)
			handler.lookUpDownPercent = 100;

		setLookVerHor();

		camera.update();
		if (renderToViewPort > 4) {
			if (J3DCore.OPTIMIZE_ANGLES) handler.core.sEngine.renderToViewPort();
			renderToViewPort = 0;
		} else {
			renderToViewPort++;
		}
		handler.core
				.updateDisplay(J3DCore.turningDirectionsUnit[handler.core.gameState.viewDirection]);
		handler.unlockHandling(false);
	}
}