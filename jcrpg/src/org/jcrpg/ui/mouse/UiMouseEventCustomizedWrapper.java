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

import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.scene.Spatial;

/**
 * UiMouseEventCustomizedWrapper allows us to reuse the current event object rather than copying it for each inputBase 
 * @author mkienenb
 *
 */
class UiMouseEventCustomizedWrapper implements UiMouseEvent {

	private UiMouseEvent delegate;
	private UiMouseEventType alternateEventType;
	private int alternateX;
	private int alternateY;
	
	UiMouseEventCustomizedWrapper(UiMouseEvent delegate,
			UiMouseEventType alternateEventType, int alternateX, int alternateY)
	{
		this.delegate = delegate;
		this.alternateEventType = alternateEventType;
		this.alternateX = alternateX;
		this.alternateY = alternateY;
	}
	
	public UiMouseEventType getEventType() {
		return alternateEventType;
	}

	public long getTimestamp() {
		return delegate.getTimestamp();
	}

	public int getX() {
		return alternateX;
	}

	public int getY() {
		return alternateY;
	}

	public boolean isButtonPressed(long button) {
		return delegate.isButtonPressed(button);
	}

	public Set<InputBase> getPickedInputBaseSet() {
		return delegate.getPickedInputBaseSet();
	}

	public List<Spatial> getPickedSpatialList() {
		return delegate.getPickedSpatialList();
	}

	public String toString() {
		return "alternateEventType=" + alternateEventType
		+ ", " + delegate.toString();
	}
}
