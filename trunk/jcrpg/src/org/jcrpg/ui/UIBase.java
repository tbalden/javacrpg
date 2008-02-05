/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.ui;

import java.util.HashMap;

import org.jcrpg.threed.J3DCore;

public class UIBase {

	public J3DCore core;
	
	public HUD hud;
	public HashMap<String,Window> windows = new HashMap<String, Window>();
	public HashMap<String,KeyListener> eventToElements = new HashMap<String, KeyListener>(); // TODO multiple listeners can handle one key string!!
	
	public UIBase(J3DCore core) throws Exception
	{
		this.core = core;
		hud = new HUD(new HUDParams(),this, core);
	}
	public void addWindow(String trigger, Window window)
	{
		windows.put(trigger, window);
	}
	public boolean handleWindowEvent(String trigger)
	{
		if (windows.get(trigger)==null) return false;
		windows.get(trigger).toggle();
		return true;
	}
	public void handleEvent(String key)
	{
		if (eventToElements.get(key)!=null)
			eventToElements.get(key).handleKey(key);
	}
	public void addEventHandler(String key, KeyListener list)
	{
		eventToElements.put(key, list);
	}
	
}
