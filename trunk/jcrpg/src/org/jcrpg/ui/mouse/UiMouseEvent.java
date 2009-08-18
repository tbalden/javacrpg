/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2009
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

package org.jcrpg.ui.mouse;

import java.util.List;
import java.util.Set;

import org.jcrpg.ui.mouse.UiMouseAction.PickedSpatialInfo;
import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.scene.Spatial;

public interface UiMouseEvent {

	public static enum UiMouseEventType {
    	MOUSE_INFO,
    	MOUSE_MOVED,
    	MOUSE_PRESSED,
       	MOUSE_RELEASED,
    	MOUSE_DRAGGED,
       	MOUSE_ENTERED,
       	MOUSE_EXITED
    }

	// bit flags for each button
	public static final long BUTTON_NONE  = 0L;
	public static final long BUTTON_LEFT  = 1L << 0;
	public static final long BUTTON_RIGHT = 1L << 1;
	public static final long BUTTON_ANY = BUTTON_LEFT | BUTTON_RIGHT;

	public int getX();
	public int getY();
    public UiMouseEventType getEventType();
	public long getTimestamp();

	public boolean isButtonPressed(long button) ;

	public List<PickedSpatialInfo> getPickedSpatialList();
	public Set<InputBase> getPickedInputBaseSet();
	
	/**
	 * 
	 * @return The spatial info that represents the given UI element.
	 */
	public PickedSpatialInfo getAreaSpatial();
}
