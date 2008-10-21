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

import java.util.logging.Level;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.ValueTuner;
import org.jcrpg.ui.window.layout.SimpleLayout;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.skill.InterceptionSkill;

import com.jme.scene.shape.Quad;

/**
 * UI class for changing configuration settings in game.
 *
 * @author goq669
 */
public class OptionsMenu extends PagedInputWindow {

    ListSelect toggleMLook;
    ListSelect toggleContinuousLoad;
    ValueTuner tunerViewDistance;
    TextButton save, cancel;

    public static String[] toggleIds = new String[] {"on","off"};
    public static String[] toggleTexts = new String[] {Language.v("configuration.on"),Language.v("configuration.off")};
    public static Object[] toggleObjects = new Object[] {true,false};

    public OptionsMenu(UIBase base) {
        super(base);
        try {
            // background
            Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.dds", 
                                            0.8f*core.getDisplay().getWidth(), 
                                            1.61f*(core.getDisplay().getHeight() / 2), 
                                            core.getDisplay().getWidth() / 2, 
                                            1.13f*core.getDisplay().getHeight() / 2);
            hudQuad.setRenderState(base.hud.hudAS);
            windowNode.attachChild(hudQuad);

            // header
            float sizeX = 1.28f* 1.2f * core.getDisplay().getWidth() / 5f;
            float sizeY = 0.82f* (core.getDisplay().getHeight() / 11);
            float posY = core.getDisplay().getHeight()*0.92f;
            float posX = core.getDisplay().getWidth() / 2;
            Quad header = loadImageToQuad("./data/ui/mainmenu/"+MainMenu.OPTIONS, sizeX, sizeY, posX, posY);
            header.setRenderState(base.hud.hudAS);
            windowNode.attachChild(header);

            // build page
            SimpleLayout layout = new SimpleLayout(0.2f, 0.2f, 0.3f, 0.07f ,2);
            layout.addToColumn(0, new TextLabel("",this,windowNode, 600f, Language.v("optionsmenu.mouselook"), false));
            toggleMLook = new ListSelect("", this, windowNode, 600f,toggleIds,toggleTexts,toggleObjects,null,null);
            layout.addToColumn(1, toggleMLook, 0.35f, 0.5f);
            addInput(0, toggleMLook);
            
            layout.addToColumn(0, new TextLabel("",this,windowNode, 600f, Language.v("optionsmenu.continuous.load"), false));
            toggleContinuousLoad = new ListSelect("", this, windowNode, 600f,toggleIds,toggleTexts,toggleObjects,null,null);
            layout.addToColumn(1, toggleContinuousLoad, 0.35f, 0.5f);
            addInput(0, toggleContinuousLoad);

            layout.addToColumn(0, new TextLabel("",this,windowNode, 600f, Language.v("optionsmenu.view.distance"), false));
            tunerViewDistance = new ValueTuner("",this,windowNode, 600f, 25, 10, 60, 5);
            layout.addToColumn(1, tunerViewDistance, 0.35f, 0.5f);
            addInput(0, tunerViewDistance);
            tunerViewDistance.setEnabled(false);

            // buttons
            save = new TextButton("save",this, windowNode, 0.3f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.save"),"S");
            addInput(0, save);
            cancel = new TextButton("cancel",this, windowNode, 0.72f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.cancel"),"C");
            addInput(0, cancel);

            windowNode.updateRenderState();
            base.addEventHandler("back", this);
        } catch (Exception ex) {
            if (J3DCore.LOGGING) { Jcrpg.LOGGER.log(Level.SEVERE, "OptionsMenu creation error: "+ex.getMessage(), ex); }
            ex.printStackTrace();
        }
    }

    public boolean handleKey(String key) {
        if (super.handleKey(key)) return true;
        if (key.equals("back")) {
            toggle();
            core.mainMenu.toggle();
        }
        return true;
    }

    @Override
    public boolean inputUsed(InputBase base, String message) {
        if (base == save) 
        {
            // TODO: save to config file
            toggle();
            core.mainMenu.toggle();
            return true;
        } else if (base == cancel) 
        {
            toggle();
            core.mainMenu.toggle();
            return true; 
        }
        return false;
    }

}
