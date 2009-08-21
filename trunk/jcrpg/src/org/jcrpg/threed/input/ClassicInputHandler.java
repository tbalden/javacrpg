/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.threed.input;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.mouse.UiMouseHandler;
import org.jcrpg.world.ai.EntityMemberInstance;

import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.MouseLookHandler;
import com.jme.renderer.Camera;
import com.jme.scene.Node;

public class ClassicInputHandler extends InputHandler {

	private MouseLookHandler mouseLookHandler;
	private UiMouseHandler menuMouseHandler;
	// private ClassicMouseLookHandler mouseLookHandler;
	private ClassicKeyboardLookHandler keyboardLookHandler;

	/**
	 * @return handler for keyboard controls
	 */
	public ClassicKeyboardLookHandler getKeyboardLookHandler() {
		return keyboardLookHandler;
	}

	/**
	 * @return handler for mouse controls
	 */
	public MouseLookHandler getMouseLookHandler() {
		return mouseLookHandler;
	}

	public UiMouseHandler getMenuMouseHandler() {
		return menuMouseHandler;
	}

	public ClassicInputHandler(J3DCore core, Camera cam) {
		keyboardLookHandler = new ClassicKeyboardLookHandler(core, cam);
		addToAttachedHandlers(keyboardLookHandler);
		mouseLookHandler = new MouseLookHandler(cam, 1.0f);// ClassicMouseLookHandler(cam);
		menuMouseHandler = new UiMouseHandler();
		// mouseLookHandler.setLockAxis(new Vector3f(1f,1f,0));
		enableMouse(true);
		// mouseLookHandler = new ClassicMouseLookHandler(cam);
		addToAttachedHandlers(mouseLookHandler);
		addToAttachedHandlers(menuMouseHandler);
	}

	public void enableMouse(boolean state) {
		mouseLookHandler.setEnabled(J3DCore.SETTINGS.MOUSELOOK && state);
		menuMouseHandler.setEnabled(J3DCore.SETTINGS.UIMOUSE && state);
	}

	public void applyMouseSettings() {
		enableMouse(J3DCore.SETTINGS.MOUSELOOK);
		enableMouse(J3DCore.SETTINGS.UIMOUSE);
	}

	private boolean TEMP_MOUSELOOK_STATE_STORE = true;
	
	public void setRootNode(Node rootNode) {
		if (rootNode != null)
		{
			setMouseLook(false);
		}
		else
		{
			setMouseLook(TEMP_MOUSELOOK_STATE_STORE&&J3DCore.SETTINGS.MOUSELOOK);
		}
		menuMouseHandler.setRootNode(rootNode);
	}
	public void setSecondaryFocusNode(Node node) {
		menuMouseHandler.setSecondaryFocusNode(node);
	}
	
	public void switchMouseLook()
	{
			setMouseLook(!((ClassicInputHandler)J3DCore.getInstance().getInputHandler()).getMouseLookHandler().isEnabled());
			TEMP_MOUSELOOK_STATE_STORE = mouseLookHandler.isEnabled(); // storing the current state - window toggles should
			// not override user choice (see setRootNode).
	}
	
	public void setMouseLook(boolean enabled)
	{
		((ClassicInputHandler)J3DCore.getInstance().getInputHandler()).getMouseLookHandler().setEnabled(enabled);
		MouseInput.get().setCursorVisible(!mouseLookHandler.isEnabled());
		if (!enabled)UiMouseHandler.normalCursor();
	}
	
	public static final int PRIMARY_INPUT_TYPE = 0, SECONDARY_INPUT_TYPE = 1;
	
	/**
	 * Call this back if portrait is 'used' (clicked etc.)
	 * @param count Number of member in party
	 * @param member Member object
	 * @param secondaryWay Was it a secondary input
	 */
	public void characterSelected(int count, EntityMemberInstance member, int inputType)
	{
		if (J3DCore.getInstance().gameState.player!=null)
		{
			if (J3DCore.getInstance().uiBase.activeWindows.size()==0)
			{
				if (inputType == PRIMARY_INPUT_TYPE)
				{
					
					J3DCore.getInstance().inventoryWindow.toggle();
					J3DCore.getInstance().inventoryWindow.directUpdateToMember(member);
					
				} else
				{
					J3DCore.getInstance().charSheetWindow.toggle();
					J3DCore.getInstance().charSheetWindow.directUpdateToMember(member);
				}
			} else
			{
				for (Window w:J3DCore.getInstance().uiBase.activeWindows)
				{
					w.characterSelected(count, member, inputType);
				}
			}
		}
	}

}
