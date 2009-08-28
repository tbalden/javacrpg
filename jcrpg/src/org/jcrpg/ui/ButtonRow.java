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

import java.util.HashSet;

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

	public float rowHeightRatio = 0.064f;
	
	ImageButton menuButton, mapButton, campButton, orderButton, behaviorButton, actButton, searchButton;
	
	public ButtonRow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 1f*core.getDisplay().getWidth(), rowHeightRatio*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, core.getDisplay().getHeight() / 1.014f);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	sQuad.setLocalTranslation(hudQuad.getLocalTranslation());
	    	windowNode.attachChild(sQuad);
	    	
	    	float buttonWidth = 0.06f;
	    	float xOffset = 0.32f;
	    	float rowHeightDiv = 2.25f;
	    	int counter = 0;
	    	menuButton = new ImageButton("1",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Menu", "./data/ui/topbuttons/mainmenu.png");
	    	mapButton = new ImageButton("2",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, "  Map", "./data/ui/topbuttons/map.png" );
	    	campButton = new ImageButton("3",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, "  Camp", "./data/ui/topbuttons/camp.png" );
	    	orderButton = new ImageButton("4",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, " Order", ImageButton.defaultImage );
	    	behaviorButton = new ImageButton("5",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, "   Behav", "./data/ui/topbuttons/behavior.png" );
	    	actButton = new ImageButton("6",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, "  Act", "./data/ui/topbuttons/act.png" );
	    	searchButton = new ImageButton("7",this,windowNode,xOffset+buttonWidth*counter++, rowHeightRatio/4.2f, buttonWidth, rowHeightRatio/rowHeightDiv, 1100f, "  Search", "./data/ui/topbuttons/search.png" );
	    	
	    	
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
	
	public HashSet<Window> listOfHandled = null;

	@Override
	public boolean inputUsed(InputBase base, String message) {
		
		boolean normalMode = core.gameState!=null && core.gameState.gameLogic!=null && !core.gameState.gameLogic.inEncounter;
		if (!normalMode) return true;
		if (Window.windowCounter>1) return true;
		
		if (listOfHandled == null)

		{
			listOfHandled = new HashSet<Window>();
			listOfHandled.add(core.charSheetWindow);
			listOfHandled.add(core.inventoryWindow);
			listOfHandled.add(core.mainMenu);
			listOfHandled.add(core.worldMap);
			listOfHandled.add(core.behaviorWindow);
			listOfHandled.add(core.partyOrderWindow);
			listOfHandled.add(core.lockInspectionWindow);
			listOfHandled.add(core.normalActWindow);
		}

		Window activeWindow = null;
		if (Window.windowCounter==1) {
			activeWindow = core.uiBase.activeWindows.iterator().next();
			if (!listOfHandled.contains(activeWindow)) return true;
		}	
		if (base == menuButton)
		{
			if (activeWindow!=null && activeWindow == core.mainMenu) activeWindow = null;
			if (activeWindow!=null) {
				activeWindow.toggle();
			}
			core.mainMenu.toggle();
		}
		if (base == mapButton)
		{
			if (activeWindow!=null && activeWindow == core.worldMap) activeWindow = null;
			if (activeWindow!=null) {
				activeWindow.toggle();
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
			if (activeWindow!=null && activeWindow == core.behaviorWindow) activeWindow = null;
			if (activeWindow!=null) {
				activeWindow.toggle();
			}
			core.behaviorWindow.toggle();
		}
		if (base == orderButton)
		{
			if (activeWindow!=null && activeWindow == core.partyOrderWindow) activeWindow = null;
			if (activeWindow!=null) {
				activeWindow.toggle();
			}
			core.partyOrderWindow.toggle();
		}
		if (base == actButton)
		{
			if (activeWindow!=null && activeWindow == core.normalActWindow) activeWindow = null;
			if (activeWindow!=null) {
				activeWindow.toggle();
			}
			core.normalActWindow.toggle();
		}
		if (base == searchButton)
		{
			if (activeWindow!=null && activeWindow == core.lockInspectionWindow) activeWindow = null;
			if (activeWindow!=null) {
				activeWindow.toggle();
			}
			core.lockInspectionWindow.toggle();
		}
		return false;
	}

}
