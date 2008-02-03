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

import java.util.Collection;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.text.TextBox;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.ui.window.element.ChoiceDescription;

public class PlayerChoiceWindow extends Window implements KeyListener {

	Collection<ChoiceDescription> choices = null;
	
	TextBox box;
	
	
	public PlayerChoiceWindow(UIBase base, TextEntry heading, Collection<ChoiceDescription> choices, String name, float middleX, float middleY, float sizeX, float sizeY) {
		super(base);
		this.choices = choices;
		box = new TextBox(base.hud,name,middleX,middleY,sizeX,sizeY);
		box.addEntry(heading);
		for (ChoiceDescription choice:choices)
		{
			box.addEntry(choice.text);
		}
		
		base.addEventHandler("Y", this);
		
		//box.hide();
	}

	@Override
	public void hide() {
		box.hide();
	}

	@Override
	public void show() {
		box.show();
	}

	public boolean handleKey(String key) {
		if (visible) {
		System.out.println("KEY "+key);
		J3DCore.getInstance().switchEncounterMode(false);
		return true;
		} else return false;
	}

}
