/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.ui;

import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.element.input.ImageButton;
import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * The row of buttons on the HUD (clickable, or by shortcut)
 * @author illes
 *
 */
public class ButtonRow extends InputWindow {

	public float rowHeightRatio = 0.058f;
	
	ImageButton menuButton, mapButton, campButton, orderButton, behaviorButton, actButton, searchButton;
	
	public ButtonRow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 1f*core.getDisplay().getWidth(), rowHeightRatio*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, core.getDisplay().getHeight() / 1.012f);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	sQuad.setLocalTranslation(hudQuad.getLocalTranslation());
	    	windowNode.attachChild(sQuad);
	    	
	    	float buttonWidth = 0.05f;
	    	float xOffset = 0.35f;
	    	float rowHeightDiv = 2.30f;
	    	int counter = 0;
	    	menuButton = new ImageButton("1",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Menu", ImageButton.defaultImage );
	    	mapButton = new ImageButton("2",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Map", ImageButton.defaultImage );
	    	campButton = new ImageButton("3",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Camp", ImageButton.defaultImage );
	    	orderButton = new ImageButton("4",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Order", ImageButton.defaultImage );
	    	behaviorButton = new ImageButton("5",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Behav", ImageButton.defaultImage );
	    	actButton = new ImageButton("6",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Act", ImageButton.defaultImage );
	    	searchButton = new ImageButton("7",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Search", ImageButton.defaultImage );
	    	
	    	
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public boolean inputChanged(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputLeft(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base == menuButton)
		{
			if (Window.windowCounter>0) {
				if (!core.uiBase.activeWindows.contains(core.mainMenu))
					return true;
			}
			core.mainMenu.toggle();
		}
		if (base == mapButton)
		{
			if (Window.windowCounter>0) {
				if (!core.uiBase.activeWindows.contains(core.worldMap))
					return true;
			}
			core.worldMap.toggle();
		}
		if (base == campButton)
		{
			if (Window.windowCounter>0) {
			} else
			{
				core.gameState.switchCamping();
			}
		}
		if (base == behaviorButton)
		{
			if (Window.windowCounter>0) {
				if (!core.uiBase.activeWindows.contains(core.behaviorWindow))
					return true;
			}
			core.behaviorWindow.toggle();
		}
		if (base == orderButton)
		{
			if (Window.windowCounter>0) {
				if (!core.uiBase.activeWindows.contains(core.partyOrderWindow))
					return true;
			}
			core.partyOrderWindow.toggle();
		}
		if (base == actButton)
		{
			if (Window.windowCounter>0) {
				if (!core.uiBase.activeWindows.contains(core.normalActWindow))
					return true;
			}
			core.normalActWindow.toggle();
		}
		if (base == searchButton)
		{
			if (Window.windowCounter>0) {
				if (!core.uiBase.activeWindows.contains(core.lockInspectionWindow))
					return true;
			}
			core.lockInspectionWindow.toggle();
		}
		return false;
	}

}
