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
			System.out.println("locked...");
			return;
		}

		handler.lockHandling();

		handler.lookUpDownPercent += 4;
		if (handler.lookUpDownPercent > 45)
			handler.lookUpDownPercent = 45;

		setLookVertical();

		camera.update();
		if (renderToViewPort > 4) {
			handler.core.renderToViewPort();
			renderToViewPort = 0;
		} else {
			renderToViewPort++;
		}
		handler.core
				.updateDisplay(J3DCore.turningDirectionsUnit[handler.core.viewDirection]);
		handler.unlockHandling(false);
	}
}