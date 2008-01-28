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

import org.jcrpg.ui.HUD;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.text.TextBox;
import org.jcrpg.ui.window.element.ChoiceDescription;

public class PlayerChoiceWindow extends Window {

	Collection<ChoiceDescription> choices = null;
	
	TextBox box;
	
	public PlayerChoiceWindow(UIBase base, Collection<ChoiceDescription> choices, HUD hud, String name, float middleX, float middleY, float sizeX, float sizeY) {
		super(base);
		this.choices = choices;
		box = new TextBox(hud,name,middleX,middleY,sizeX,sizeY);
		box.hide();
	}

	@Override
	public void hide() {
	}

	@Override
	public void show() {
		box.show();
	}

}
