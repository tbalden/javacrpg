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

package org.jcrpg.ui.window;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.element.input.InputBase;

public abstract class PagedInputWindow extends InputWindow {

	int currentPage = 0;
	
	public HashMap<Integer, ArrayList<InputBase>> inputs = new HashMap<Integer, ArrayList<InputBase>>();
	

	public PagedInputWindow(UIBase base) {
		super(base);
	}
	
	public void changePage(int pageNumber)
	{
		super.inputs = inputs.get(pageNumber);
		super.selectedInput = 0;
		activateSelectedInput();
	}
	

	public void addInput(int pageNumber, InputBase input)
	{
		ArrayList<InputBase> inputs = this.inputs.get(pageNumber);
		if (inputs == null)
		{
			inputs = new ArrayList<InputBase>();
		}
		inputs.add(input);
		this.inputs.put(pageNumber, inputs);
	}


}
