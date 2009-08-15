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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.scene.Spatial;

/**
 * Represents a raw mouse event
 * @author mkienenb
 *
 */
class UiMouseEventImpl implements UiMouseEvent {

	private int x;
	private int y;
	
	private UiMouseEventType eventType;
	private long buttonMask;
	private long timestamp;

	// Unclear whether these are useful yet to input components, but pickedInputBaseSet is used by event loop
	private List<Spatial> pickedSpatialList = Collections.emptyList();
	private Set<InputBase> pickedInputBaseSet = Collections.emptySet();

	UiMouseEventImpl(UiMouseEventType uiMouseEventType, int mouseEventX, int mouseEventY)
	{
		super();
		
		this.eventType = uiMouseEventType;
		this.x = mouseEventX;
		this.y = mouseEventY;

		// Default values
		this.buttonMask = 0;
		this.timestamp = System.currentTimeMillis();
	}
	
    public UiMouseEventType getEventType() {
		return eventType;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public List<Spatial> getPickedSpatialList() {
		return Collections.unmodifiableList(pickedSpatialList);
	}

	public Set<InputBase> getPickedInputBaseSet() {
		return Collections.unmodifiableSet(pickedInputBaseSet);
	}

	public boolean isButtonPressed(long button) 
	{
		if ((button == BUTTON_NONE) && (0 == buttonMask))
		{
			return true;
		}

		return ((this.buttonMask & button) != 0);
	}


	// Methods that should not be available to UiComponents, but only to the event generation code
	
	protected void setButtonPressed(long button) {
		this.buttonMask = this.buttonMask | button;
	}

	protected void setPickedSpatialList(List<Spatial> pickedSpatialList) {
		this.pickedSpatialList = pickedSpatialList;
	}

	protected void setPickedInputBaseSet(Set<InputBase> pickedInputBaseSet) {
		this.pickedInputBaseSet = pickedInputBaseSet;
	}

	protected void setEventType(UiMouseEventType eventType) {
		this.eventType = eventType;
	}

	protected boolean isEquivalentTo(UiMouseEventImpl lastMouseEvent)
	{
		if ( (this.x == lastMouseEvent.x)
		  && (this.y == lastMouseEvent.y)
		  && (this.buttonMask == lastMouseEvent.buttonMask) )
		{
			return true;
		}

		return false;
	}
	
	@Override
	public String toString() {
		return "x=" + x
		+ ", y=" + y
		+ ", buttonMask=" + buttonMask
		+ ", eventType=" + eventType
		+ ", timestamp=" + timestamp;
	}
}
