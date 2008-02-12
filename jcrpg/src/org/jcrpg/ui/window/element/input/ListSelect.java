/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.ui.window.element.input;

import org.jcrpg.ui.Window;

public class ListSelect extends InputBase {

	public String[] ids;
	public String[] texts;
	
	public int selected = 0;
	public int fromCount = 0;
	public int maxCount = 0;
	public int maxVisible = 0;
	public boolean reloadNeeded = false;
	
	public ListSelect(Window w, float centerX, float centerY, float sizeX, float sizeY, String[] ids, String[] texts) {
		super(w, centerX, centerY, sizeX, sizeY);
		this.ids = ids;
		this.texts = texts;
	}
	
	public boolean select(boolean next)
	{
		if (!next) {
			selected--;
		} else
		{
			selected++;
		}
		if (selected+fromCount>=maxVisible)
		{
			selected--;
		}
		if (selected>=maxVisible)
		{
			if (selected+fromCount<maxCount)
			{
				fromCount = selected;
				selected = 0;

				reloadNeeded = true;
			} 
		}
		if (selected<0) {
			if (fromCount-maxVisible>=0)
			{
				fromCount -= maxVisible;
				selected = maxCount-1;
				reloadNeeded = true;
			} else {
				selected = 0;
			}
		}
		return reloadNeeded;
	}
	
	public boolean handleKey(String key) {
		if (!enabled) return false;
		if (key.equals("lookUp"))
		{
			if (select(false))
			{
				
			}
		} else
		if (key.equals("lookDown"))
		{
			if (select(true))
			{
				
			}
		}
		return true;
	}
}
