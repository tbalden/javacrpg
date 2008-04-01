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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.TextButton;

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
	
	/**
	 * Adds an input to the window.
	 * @param input
	 */
	public void addInput(InputBase input)
	{
		inputs.add(input);
	}
	/**
	 * Get an input at given place.
	 * @param count
	 * @return
	 */
	public InputBase getInput(int count)
	{
		return inputs.get(count);
	}
	
	/**
	 * Sets the active input.
	 * @param input
	 */
	public void setSelected(InputBase input)
	{
		int i = getInputPlace(input);
		if (i>=0 && i!=selectedInput) {
			selectedInput = i;
			activateSelectedInput();
		}
	}
	
	public void setSelected(int selectedInput)
	{
		if (inputs.size()>selectedInput && this.selectedInput!=selectedInput)
		{
			this.selectedInput = selectedInput;
			activateSelectedInput();
		}
	}
	
	/**
	 * Returns place in array of the input.
	 * @param input
	 * @return
	 */
	public int getInputPlace(InputBase input)
	{
		int i=0;
		//Jcrpg.LOGGER.info(input);
		for (InputBase cinput:inputs)
		{
			//Jcrpg.LOGGER.info(cinput);
			if (input.equals(cinput))
			{
				Jcrpg.LOGGER.info("INPUT FOUND "+i);
				return i;
			}
			i++;
		}
		//Jcrpg.LOGGER.info("INPUT NOT FOUND "+i);
		return -1;
	}
	
	public void activateSelectedInput()
	{
		if (inputs!=null && previouslyActive>=0 && inputs.size()>previouslyActive && selectedInput!=previouslyActive)
		{
			inputs.get(previouslyActive).deactivate();
		}
		if (inputs!=null && inputs.size()>selectedInput && selectedInput!=previouslyActive) {
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
		core.getRootNode().detachChild(windowNode);
		core.getRootNode().updateRenderState();
	}

	@Override
	public void show() {
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
	}

	public boolean handleKey(String key) {
		if (key.equals("lookDown"))
		{
			int orig = selectedInput;
			while (true)
			{
				selectedInput++;
				if (selectedInput>=inputs.size())
				{
					selectedInput = orig;
					return true;
				}
				if (inputs.get(selectedInput).isEnabled())
				{
					break;
				}
			}
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
			int orig = selectedInput;
			while (true)
			{
				selectedInput--;
				if (selectedInput<0)
				{
					selectedInput = orig;
					return true;
				}
				if (inputs.get(selectedInput).isEnabled())
				{
					break;
				}
			}
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
		if (inputs!=null && inputs.size()>selectedInput) 
		{
			if (inputs.get(selectedInput).handleKey(key)) {
				return true;
			}
		}
		// button shortcut handling ...
		for (InputBase i:inputs)
		{
			if (i instanceof TextButton)
			{
				if (((TextButton)i).shortCut!=null)
				{
					if (i.handleKey(key)) return true;
				}
			}
		}
		return false;
	}

}
