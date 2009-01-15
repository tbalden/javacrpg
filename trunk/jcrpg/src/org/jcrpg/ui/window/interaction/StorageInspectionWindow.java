/*
*   This file is part of jClassicRPG.
*	Copyright (C) 2008 Illes Pal Zoltan
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/                                                                       

package org.jcrpg.ui.window.interaction;

import java.util.ArrayList;
import java.util.logging.Level;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.trigger.StorageObjectHandler;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.layout.SimpleLayout;
import org.jcrpg.util.Language;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class StorageInspectionWindow extends PagedInputWindow {
	
	
	
	/*
	 * STORAGE INSPECTION
	 * 
	 * Current State: Unknown/Identified/Open
	 * 
	 * Skill Level in Party: X   Chance of Success: X %
	 * Magic Level in Party: X   Chance of Success: X %
	 * 
	 * Identified Trap Level/Type: -/- or LEVEL/TYPE
	 * 
	 * Force Chance of Success: X %
	 * 
	 * Identify               Unlock  
	 * [Inspect][Sense]       [Disarm][Spell]
	 * 
	 * [Open] [Leave] [Force]
	 * 
	 */
	
	TextLabel currentState, skillLevel, chanceOfSkillSuccess, spellLevel, chanceOfSpellSuccess, trapLevelAndType,
	chanceOfForceSuccess;
	
	static final String BG_IMAGE = "./data/ui/nonPatternFrame1_trans.png";

    Node page0 = new Node();
    TextButton inspect, sense, disarm, spell, open, leave, force;
    

	@Override
	public synchronized void toggle() {
		if (visible)
		{
			super.toggle();
			return;
		}
		if (!storageNearby) return; // no storage nearby, shouldn't show up.
		super.toggle();
	}

	
	public StorageInspectionWindow(UIBase base) {
		super(base);
        try {
	        // --- First Page ---
	
	        // background
	        Quad hudQuad = loadImageToQuad(BG_IMAGE, 
	                                        0.8f*core.getDisplay().getWidth(), 
	                                        1.61f*(core.getDisplay().getHeight() / 2), 
	                                        core.getDisplay().getWidth() / 2, 
	                                        1.13f*core.getDisplay().getHeight() / 2);
	        hudQuad.setRenderState(base.hud.hudAS);
	        page0.attachChild(hudQuad);
	        
	    	new TextLabel("",this,page0, 0.40f, 0.046f, 0.3f, 0.06f,400f,"Storage Inspection",false);
	    	
	    	// layouts
	    	
            SimpleLayout page0Layout = new SimpleLayout(0.2f, 0.16f, 0.2f, 0.07f ,4);

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 500f, Language.v("storageInspectionWindow.currentState"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 500f, "", false));
            currentState = new TextLabel("",this,page0, 500f, "Unknown Closed", false);
            page0Layout.addToColumn(2, currentState, 0.1f, 0.5f);
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 500f, "", false));

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("storageInspectionWindow.skillLevel"), false));
            skillLevel = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(1, skillLevel, 0.1f, 0.5f);            
            page0Layout.addToColumn(2, new TextLabel("",this,page0, 600f, Language.v("storageInspectionWindow.chanceOfSkillSuccess"), false));
            chanceOfSkillSuccess = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(3, chanceOfSkillSuccess, 0.1f, 0.5f);

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("storageInspectionWindow.spellLevel"), false));
            spellLevel = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(1, spellLevel, 0.1f, 0.5f);            
            page0Layout.addToColumn(2, new TextLabel("",this,page0, 600f, Language.v("storageInspectionWindow.chanceOfSpellSuccess"), false));
            chanceOfSpellSuccess = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(3, chanceOfSpellSuccess, 0.1f, 0.5f);

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("storageInspectionWindow.trapLevelAndType"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 600f, "", false));
            trapLevelAndType = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(2, trapLevelAndType, 0.1f, 0.5f);
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 600f, "", false));
	    	
            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("storageInspectionWindow.chanceOfForceSuccess"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 600f, "", false));
            chanceOfForceSuccess = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(2, chanceOfForceSuccess, 0.1f, 0.5f);
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 600f, "", false));

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 500f, Language.v("storageInspectionWindow.identify"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 500f, "", false));
            page0Layout.addToColumn(2, new TextLabel("",this,page0, 500f, Language.v("storageInspectionWindow.disarming"), false));
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 500f, "", false));
            
            // TODO the remaining buttons into the layout!

            inspect = new TextButton("inspect",this, page0, 0.25f, 0.57f, 0.14f, 0.06f, 500f,Language.v("storageInspectionWindow.inspect"),"I");
            addInput(0, inspect);
            sense = new TextButton("sense",this, page0, 0.405f, 0.57f, 0.14f, 0.06f, 500f,Language.v("storageInspectionWindow.sense"),"E");
            addInput(0, sense);

            disarm = new TextButton("disarm",this, page0, 0.65f, 0.57f, 0.14f, 0.06f, 500f,Language.v("storageInspectionWindow.disarm"),"D");
            addInput(0, disarm);
            spell = new TextButton("spell",this, page0, 0.80f, 0.57f, 0.14f, 0.06f, 500f,Language.v("storageInspectionWindow.spell"),"S");
            addInput(0, spell);
            inspect = new TextButton("force",this, page0, 0.65f, 0.65f, 0.14f, 0.06f, 500f,Language.v("storageInspectionWindow.force"),"C");
            addInput(0, inspect);
	    	
            // buttons
            open = new TextButton("open",this, page0, 0.5f, 0.75f, 0.18f, 0.06f, 500f,Language.v("storageInspectionWindow.open"),"P");
            addInput(0, open);
            leave = new TextButton("leave",this, page0, 0.75f, 0.75f, 0.18f, 0.06f, 500f,Language.v("storageInspectionWindow.leave"),"L");
            addInput(0, leave);
	    	
	    	addPage(0, page0);
            windowNode.updateRenderState();
	        
	        
        } catch (Exception ex) {
            if (J3DCore.SETTINGS.LOGGING) { Jcrpg.LOGGER.log(Level.SEVERE, "StorageManipulationWindow creation error: "+ex.getMessage(), ex); }
            ex.printStackTrace();
        }
	}
	
	ArrayList<Side> triggerSides;
	StorageObjectHandler handdler;
	Cube enteredCube; RenderedCube renderedEnteredCube; Cube leftCube; RenderedCube renderedLeftCube;
	
	/**
	 * If this is true, storage is in cube, so player can toggle this window.
	 */
	boolean storageNearby = false;
	
	public void setInspectableStorageObjectData(ArrayList<Side> triggerSides, StorageObjectHandler handler,Cube enteredCube, RenderedCube renderedEnteredCube, Cube leftCube, RenderedCube renderedLeftCube)
	{
		this.triggerSides = triggerSides;
		this.handdler = handler;
		this.enteredCube = enteredCube;
		this.renderedEnteredCube = renderedEnteredCube;
		this.leftCube = leftCube;
		this.renderedLeftCube = renderedLeftCube;
	}
	
	public void setStorageNearby(boolean value)
	{
		storageNearby = value;
	}

	
    @Override
    public boolean inputUsed(InputBase base, String message) {
        // Save
        if (base == open) {
        	handdler.openTriggerSides(enteredCube, renderedEnteredCube, leftCube, renderedLeftCube);
            return true;
        // Cancel
        } else if (base == leave) {
            // back to main menu
        	handdler.closeTriggerSides(enteredCube, renderedEnteredCube, leftCube, renderedLeftCube);
            toggle();
            return true; 
        // Next Page
        }
        return true;
    }

}
