package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.renderer.Camera;

/**
 * <code>KeyRotateLeftAction</code> performs the action of rotating a camera a
 * certain angle. This angle is determined by the speed at which the camera can
 * turn and the time between frames.
 * 
 * @author Mark Powell
 * @version $Id: KeyRotateLeftAction.java,v 1.16 2006/09/29 22:30:17 nca Exp $
 */
public class CKeyLookLeftAction extends CKeyAction {

	public CKeyLookLeftAction(ClassicKeyboardLookHandler handler,
			Camera camera, float speed) {
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

		if (handler.lookUpDownPercent!=0)
		{
			if (J3DCore.OPTIMIZE_ANGLES) handler.core.sEngine.renderToViewPort(J3DCore.ROTATE_VIEW_ANGLE);
		}
		handler.lookLeftRightPercent -= 8;
		if (handler.lookLeftRightPercent < -100)
			handler.lookLeftRightPercent = -100;
		setLookVerHor();

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