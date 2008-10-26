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
import org.jcrpg.ui.window.element.input.CheckBox;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.ValueTuner;
import org.jcrpg.ui.window.layout.SimpleLayout;
import org.jcrpg.util.Language;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * UI class for changing configuration settings in game.
 *
 * @author goq669
 */
public class OptionsMenu extends PagedInputWindow {

    Node pageFirst = new Node();
    Node pageSecond = new Node();

    CheckBox toggleMLook;
    CheckBox toggleContinuousLoad;
    ValueTuner tunerViewDistance;
    ValueTuner tunerRenderDistance;
    ValueTuner tunerRenderGrassDistance;
    ValueTuner tunerTextureDetail;
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

            // --- First Page ---
            SharedMesh sQuad = new SharedMesh("--",hudQuad);
            pageFirst.attachChild(sQuad);

            SimpleLayout inputsLayout = new SimpleLayout(0.2f, 0.2f, 0.3f, 0.07f ,2);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.mouselook"), false));
            toggleMLook = new CheckBox("", this, pageFirst, J3DCore.MOUSELOOK);
            inputsLayout.addToColumn(1, toggleMLook, 0.1f, 0.5f);
            addInput(0, toggleMLook);
            
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.continuous.load"), false));
            toggleContinuousLoad = new CheckBox("", this, pageFirst, J3DCore.CONTINUOUS_LOAD);
            inputsLayout.addToColumn(1, toggleContinuousLoad, 0.1f, 0.5f);
            addInput(0, toggleContinuousLoad);

            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.view.distance"), false));
            tunerViewDistance = new ValueTuner("",this,pageFirst, 600f, J3DCore.VIEW_DISTANCE, 10, 60, 5);
            inputsLayout.addToColumn(1, tunerViewDistance, 0.35f, 0.5f);
            addInput(0, tunerViewDistance);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.render.distance"), false));
            tunerRenderDistance = new ValueTuner("",this,pageFirst, 600f, J3DCore.RENDER_DISTANCE, 16, 66, 5);
            inputsLayout.addToColumn(1, tunerRenderDistance, 0.35f, 0.5f);
            addInput(0, tunerRenderDistance);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.render.grass.distance"), false));
            tunerRenderGrassDistance = new ValueTuner("",this,pageFirst, 600f, J3DCore.RENDER_GRASS_DISTANCE, 0, 20, 1);
            inputsLayout.addToColumn(1, tunerRenderGrassDistance, 0.35f, 0.5f);
            addInput(0, tunerRenderGrassDistance);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.texture.detail"), false));
            tunerTextureDetail = new ValueTuner("",this,pageFirst, 600f, J3DCore.TEXTURE_QUALITY, 0, 2, 1);
            inputsLayout.addToColumn(1, tunerTextureDetail, 0.35f, 0.5f);
            addInput(0, tunerTextureDetail);
            

            // buttons
            save = new TextButton("save",this, pageFirst, 0.3f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.save"),"S");
            addInput(0, save);
            cancel = new TextButton("cancel",this, pageFirst, 0.72f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.cancel"),"C");
            addInput(0, cancel);

            addPage(0, pageFirst);
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
        if (base == save) {
            // TODO: save to config file
            toggle();
            core.mainMenu.toggle();
            return true;
        } else if (base == cancel) {
            toggle();
            core.mainMenu.toggle();
            return true; 
        } else if (base.equals(tunerViewDistance)) {
            if (message.equals("enter")) {
                System.out.println("--- tunerViewDistance: "+message);
            } else if (message.equals("lookLeft") || message.equals("lookRight")) {
                tunerViewDistance.setUpdated(true);
                System.out.println("--- tunerViewDistance: "+message+", tunerViewDistance.value"+tunerViewDistance.getSelection());
            } 
        }

        return false;
    }

}
