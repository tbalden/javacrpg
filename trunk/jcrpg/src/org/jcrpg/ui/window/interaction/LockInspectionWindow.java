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
import org.jcrpg.game.logic.Impact;
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.game.logic.UnlockEvaluator;
import org.jcrpg.game.logic.UnlockEvaluator.TrapDisarmResult;
import org.jcrpg.game.logic.UnlockEvaluator.UnlockAction;
import org.jcrpg.game.logic.UnlockEvaluator.UnlockEvaluationInfo;
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
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.object.craft.TrapAndLock;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

/**
 * 
 * @author illes
 *
 */
public class LockInspectionWindow extends PagedInputWindow {
	
	
	
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
    

    public String getCurrentStateText()
    {
    	if (lock==null)
    	{
    		return ""+owner.description.getName()+" No lock.";
    	}
    	if (!isIdentified)
    	{
    		return ""+owner.description.getName()+" Unknown.";
    	}
    	if (!isUnlocked)
    	{
    		return ""+owner.description.getName()+" "+lock.getClass().getSimpleName();
    	}
    	{
    		return ""+owner.description.getName()+" Unlocked.";
    	}
    }
    
	@Override
	public synchronized void toggle() {
		if (visible)
		{
			super.toggle();
			return;
		}
		if (!storageNearby) return; // no storage nearby, shouldn't show up.
		
		currentState.text = getCurrentStateText();
		currentState.activate();
		
		super.toggle();
	}

	
	public LockInspectionWindow(UIBase base) {
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
	        
	    	new TextLabel("",this,page0, 0.40f, 0.046f, 0.3f, 0.06f,400f,"Lock Inspection",false);
	    	
	    	// layouts
	    	
            SimpleLayout page0Layout = new SimpleLayout(0.2f, 0.16f, 0.2f, 0.07f ,4);

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 500f, Language.v("lockInspectionWindow.currentState"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 500f, "", false));
            currentState = new TextLabel("",this,page0, 500f, "Unknown Closed", false);
            page0Layout.addToColumn(2, currentState, 0.1f, 0.5f);
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 500f, "", false));

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("lockInspectionWindow.skillLevel"), false));
            skillLevel = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(1, skillLevel, 0.1f, 0.5f);            
            page0Layout.addToColumn(2, new TextLabel("",this,page0, 600f, Language.v("lockInspectionWindow.chanceOfSkillSuccess"), false));
            chanceOfSkillSuccess = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(3, chanceOfSkillSuccess, 0.1f, 0.5f);

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("lockInspectionWindow.spellLevel"), false));
            spellLevel = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(1, spellLevel, 0.1f, 0.5f);            
            page0Layout.addToColumn(2, new TextLabel("",this,page0, 600f, Language.v("lockInspectionWindow.chanceOfSpellSuccess"), false));
            chanceOfSpellSuccess = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(3, chanceOfSpellSuccess, 0.1f, 0.5f);

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("lockInspectionWindow.trapLevelAndType"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 600f, "", false));
            trapLevelAndType = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(2, trapLevelAndType, 0.1f, 0.5f);
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 600f, "", false));
	    	
            page0Layout.addToColumn(0, new TextLabel("",this,page0, 600f, Language.v("lockInspectionWindow.chanceOfForceSuccess"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 600f, "", false));
            chanceOfForceSuccess = new TextLabel("",this,page0, 600f, "-", false);
            page0Layout.addToColumn(2, chanceOfForceSuccess, 0.1f, 0.5f);
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 600f, "", false));

            page0Layout.addToColumn(0, new TextLabel("",this,page0, 500f, Language.v("lockInspectionWindow.identify"), false));
            page0Layout.addToColumn(1, new TextLabel("",this,page0, 500f, "", false));
            page0Layout.addToColumn(2, new TextLabel("",this,page0, 500f, Language.v("lockInspectionWindow.disarming"), false));
            page0Layout.addToColumn(3, new TextLabel("",this,page0, 500f, "", false));
            
            // TODO the remaining buttons into the layout!

            inspect = new TextButton("inspect",this, page0, 0.25f, 0.57f, 0.14f, 0.06f, 500f,Language.v("lockInspectionWindow.inspect"),"I");
            addInput(0, inspect);
            sense = new TextButton("sense",this, page0, 0.405f, 0.57f, 0.14f, 0.06f, 500f,Language.v("lockInspectionWindow.sense"),"E");
            addInput(0, sense);

            disarm = new TextButton("disarm",this, page0, 0.65f, 0.57f, 0.14f, 0.06f, 500f,Language.v("lockInspectionWindow.disarm"),"D");
            addInput(0, disarm);
            spell = new TextButton("spell",this, page0, 0.80f, 0.57f, 0.14f, 0.06f, 500f,Language.v("lockInspectionWindow.spell"),"S");
            addInput(0, spell);
            force = new TextButton("force",this, page0, 0.65f, 0.65f, 0.14f, 0.06f, 500f,Language.v("lockInspectionWindow.force"),"C");
            addInput(0, inspect);
	    	
            // buttons
            open = new TextButton("open",this, page0, 0.5f, 0.75f, 0.18f, 0.06f, 500f,Language.v("lockInspectionWindow.open"),"P");
            addInput(0, open);
            leave = new TextButton("leave",this, page0, 0.75f, 0.75f, 0.18f, 0.06f, 500f,Language.v("lockInspectionWindow.leave"),"L");
            addInput(0, leave);
	    	
	    	addPage(0, page0);
            windowNode.updateRenderState();
	        
	        
        } catch (Exception ex) {
            if (J3DCore.SETTINGS.LOGGING) { Jcrpg.LOGGER.log(Level.SEVERE, "lockInspectionWindow creation error: "+ex.getMessage(), ex); }
            ex.printStackTrace();
        }
	}
	
	ArrayList<Side> triggerSides;
	StorageObjectHandler handdler;
	Cube enteredCube; RenderedCube renderedEnteredCube; Cube leftCube; RenderedCube renderedLeftCube;
	EntityInstance owner; TrapAndLock lock;
	EntityFragment initiator;
	UnlockEvaluationInfo unlockEvalInfo;
	
	/**
	 * If this is true, storage is in cube, so player can toggle this window.
	 */
	boolean storageNearby = false;
	
	public void setInspectableStorageObjectData(EntityFragment initiator, ArrayList<Side> triggerSides, StorageObjectHandler handler,Cube enteredCube, RenderedCube renderedEnteredCube, Cube leftCube, RenderedCube renderedLeftCube, EntityInstance owner, TrapAndLock lock)
	{
		this.initiator = initiator;
		if (lock!=null)
		{
			isUnlocked = false;
			isIdentified = false;
			unlockEvalInfo = UnlockEvaluator.getEvaluationInfo(initiator, lock);
			chanceOfForceSuccess.text = ""+unlockEvalInfo.chanceOfForce+"%";
			chanceOfForceSuccess.deactivate();
			chanceOfSpellSuccess.text = ""+unlockEvalInfo.chanceOfSpell+"%";
			chanceOfSpellSuccess.deactivate();
			chanceOfSkillSuccess.text = ""+unlockEvalInfo.chanceOfSkill+"%";
			chanceOfSkillSuccess.deactivate();

			skillLevel.text = ""+unlockEvalInfo.skillLevel+"";
			skillLevel.deactivate();
			spellLevel.text = ""+unlockEvalInfo.spellLevel+"";
			spellLevel.deactivate();

		} else
		{
			isUnlocked = true;
			unlockEvalInfo = null;
		}
		this.triggerSides = triggerSides;
		this.handdler = handler;
		this.enteredCube = enteredCube;
		this.renderedEnteredCube = renderedEnteredCube;
		this.leftCube = leftCube;
		this.renderedLeftCube = renderedLeftCube;
		this.owner = owner;
		this.lock = lock;
	}
	
	public void setStorageNearby(boolean value)
	{
		storageNearby = value;
	}
	
	public boolean isUnlocked = false;
	public boolean isIdentified = false;

	
    @Override
    public boolean inputUsed(InputBase base, String message) {
    	
    	if (!isUnlocked && !isIdentified)
    	{
    		if (base == inspect)
    		{
        		if (lock==null) isUnlocked = true;
        		boolean success = lock.tryIdentification(unlockEvalInfo,UnlockAction.UNLOCK_ACTION_TYPE_INSPECT);
        		if (success)
        		{
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("LOCK IDENTIFIED!");
        			isIdentified = true;
        		} else
        		{
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("Failure to inspect!");
        		}
    		}
    		if (base == sense)
    		{
        		if (lock==null) isUnlocked = true;
        		boolean success = lock.tryIdentification(unlockEvalInfo,UnlockAction.UNLOCK_ACTION_TYPE_SENSE);
        		if (success)
        		{
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("LOCK IDENTIFIED!");
        			isIdentified = true;
        		} else
        		{
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("Failure to sense!");
        		}
    		}    	
    	}
    	if (!isUnlocked && isIdentified)
    	{
        	Impact failureImpact = null;
  		
    		if (base == disarm)
    		{
        		if (lock==null) isUnlocked = true;
        		TrapDisarmResult result = lock.tryDisarming(unlockEvalInfo,UnlockAction.UNLOCK_ACTION_TYPE_PHYSICAL);
        		if (result.success)
        		{
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("UNLOCKED!");
        			isUnlocked = true;
        		} else
        		{
        			failureImpact = result.impact;
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("FAILED to disarm!");
        		}    			
    		}
    		if (base == spell)
    		{
        		if (lock==null) isUnlocked = true;
        		TrapDisarmResult result = lock.tryDisarming(unlockEvalInfo,UnlockAction.UNLOCK_ACTION_TYPE_MAGICAL);
        		if (result.success)
        		{
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("UNLOCKED!");
        			isUnlocked = true;
        		} else
        		{
           			failureImpact = result.impact;
           		 	J3DCore.getInstance().uiBase.hud.mainBox.addEntry("FAILED to magically unlock!");
        		}    			
    		}
    		if (base == force)
    		{
        		if (lock==null) isUnlocked = true;
        		TrapDisarmResult result = lock.tryDisarming(unlockEvalInfo,UnlockAction.UNLOCK_ACTION_TYPE_FORCE);
        		if (result.success)
        		{
        			failureImpact = result.impact;
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("FORCED!");
        			isUnlocked = true;
        		} else
        		{
        			failureImpact = result.impact;
        			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("FAILED to force!");
        		}    			
    		}
    		if (failureImpact!=null)
    		{
    			// TODO IMPACT, effect etc.
    			for (EntityMemberInstance i:failureImpact.targetImpact.keySet())
    			{
    				ImpactUnit u = failureImpact.targetImpact.get(i);
    				i.applyImpactUnit(u);
    			}
    		}
    	}
    	currentState.text = getCurrentStateText();
    	currentState.activate();
        // Save
        if (base == open && isUnlocked) {
        	handdler.openTriggerSides(enteredCube, renderedEnteredCube, leftCube, renderedLeftCube);
        	
        	toggle();
        	J3DCore.getInstance().storageHandlingWindow.toggle();
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
