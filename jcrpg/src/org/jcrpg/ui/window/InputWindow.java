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

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.element.input.InputBase;

public class InputWindow extends Window {

	public ArrayList<InputBase> inputs = new ArrayList<InputBase>();
	
	int selectedInput = 0;
	
	public InputWindow(UIBase base) {
		super(base);
	}
	
	public void addInput(InputBase input)
	{
		inputs.add(input);
	}
	public InputBase getInput(int count)
	{
		return inputs.get(count);
	}
	
	public void activateSelectedInput()
	{
		if (inputs.size()>selectedInput)
			inputs.get(selectedInput).activate();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

}
