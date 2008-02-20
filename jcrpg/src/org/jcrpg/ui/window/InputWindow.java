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

import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.element.input.InputBase;

public abstract class InputWindow extends Window implements KeyListener{

	public ArrayList<InputBase> inputs = new ArrayList<InputBase>();
	
	int selectedInput = 0;
	int previouslyActive = -1;
	
	public InputWindow(UIBase base) {
		super(base);
		base.addEventHandler("lookUp", this);
		base.addEventHandler("lookDown", this);
		base.addEventHandler("enter", this);
		
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
		if (previouslyActive>=0 && inputs.size()>previouslyActive && selectedInput!=previouslyActive)
		{
			inputs.get(previouslyActive).deactivate();
		}
		if (inputs.size()>selectedInput && selectedInput!=previouslyActive) {
			inputs.get(selectedInput).activate();
			previouslyActive = selectedInput;
		}
	}

	public abstract boolean inputChanged(InputBase base, String message);

	public abstract boolean inputLeft(InputBase base, String message);

	public abstract boolean inputEntered(InputBase base, String message);

	public abstract boolean inputUsed(InputBase base, String message);

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	public boolean handleKey(String key) {
		if (key.equals("lookDown"))
		{
			selectedInput++;
			if (selectedInput>=inputs.size())
			{
				selectedInput--;
			} else
			{
				inputLeft(inputs.get(previouslyActive), key);
				inputEntered(inputs.get(selectedInput), key);
				activateSelectedInput();
			}
			return true;
		} else
		if (key.equals("lookUp"))
		{
			selectedInput--;
			if (selectedInput<0)
			{
				selectedInput = 0;
			} else
			{
				inputLeft(inputs.get(previouslyActive), key);
				inputEntered(inputs.get(selectedInput), key);
				activateSelectedInput();
			}
			return true;
		}
		if (inputs.size()>selectedInput)
			return inputs.get(selectedInput).handleKey(key);
		
		return false;
	}

}
