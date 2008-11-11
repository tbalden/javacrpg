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

    static final String BG_IMAGE = "./data/ui/baseWindowFrame.dds";

    Node pageFirst = new Node();
    Node pageSecond = new Node();

    CheckBox toggleMLook;
    CheckBox toggleContinuousLoad;
    ValueTuner tunerViewDistance;
    ValueTuner tunerRenderDistance;
    ValueTuner tunerRenderGrassDistance;
    ValueTuner tunerTextureDetail;
    ValueTuner tunerEffectsVolume;
    ValueTuner tunerMusicVolume;

    CheckBox toggleNormalMapShader;
    ListSelect selectWaterDeatil;
    static final String[] waterDeatilIds = {Language.v("optionsmenu.on"),
                                            Language.v("optionsmenu.off"),
                                            Language.v("optionsmenu.detailed")};
    CheckBox toggleBloom;
    CheckBox toggleDepthOfField;
    ValueTuner tunerShadowDistance;
    CheckBox toggleSlowAnimation;

    TextButton save, cancel, nextPage, prevPage;

    public OptionsMenu(UIBase base) {
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
            pageFirst.attachChild(hudQuad);

            // header
            float sizeX = 1.28f* 1.2f * core.getDisplay().getWidth() / 5f;
            float sizeY = 0.82f* (core.getDisplay().getHeight() / 11);
            float posY = core.getDisplay().getHeight()*0.92f;
            float posX = core.getDisplay().getWidth() / 2;
            Quad header = loadImageToQuad("./data/ui/mainmenu/"+MainMenu.OPTIONS, sizeX, sizeY, posX, posY);
            header.setRenderState(base.hud.hudAS);
            pageFirst.attachChild(header);

            SimpleLayout inputsLayout = new SimpleLayout(0.2f, 0.16f, 0.3f, 0.07f ,2);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.mouselook"), false));
            toggleMLook = new CheckBox("", this, pageFirst, J3DCore.MOUSELOOK);
            inputsLayout.addToColumn(1, toggleMLook, 0.1f, 0.5f);
            addInput(0, toggleMLook);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.continuous.load"), false));
            toggleContinuousLoad = new CheckBox("", this, pageFirst, J3DCore.CONTINUOUS_LOAD);
            inputsLayout.addToColumn(1, toggleContinuousLoad, 0.1f, 0.5f);
            addInput(0, toggleContinuousLoad);

            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.view.distance"), false));
            tunerViewDistance = new ValueTuner("",this,pageFirst, 600f, J3DCore.VIEW_DISTANCE, 10, 60, 2);
            inputsLayout.addToColumn(1, tunerViewDistance, 0.35f, 0.5f);
            addInput(0, tunerViewDistance);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.render.distance"), false));
            int tunerValue = (J3DCore.RENDER_DISTANCE>J3DCore.VIEW_DISTANCE+6 ? J3DCore.RENDER_DISTANCE : J3DCore.VIEW_DISTANCE+6);
            tunerRenderDistance = new ValueTuner("",this,pageFirst, 600f, tunerValue, 16, 66, 2);
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

            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.effects.volume"), false));
            tunerEffectsVolume = new ValueTuner("",this,pageFirst, 600f, J3DCore.EFFECT_VOLUME_PERCENT, 0, 100, 5);
            inputsLayout.addToColumn(1, tunerEffectsVolume, 0.35f, 0.5f);
            addInput(0, tunerEffectsVolume);
            inputsLayout.addToColumn(0, new TextLabel("",this,pageFirst, 600f, Language.v("optionsmenu.music.volume"), false));
            tunerMusicVolume = new ValueTuner("",this,pageFirst, 600f, J3DCore.MUSIC_VOLUME_PERCENT, 0, 100, 5);
            inputsLayout.addToColumn(1, tunerMusicVolume, 0.35f, 0.5f);
            addInput(0, tunerMusicVolume);

            // buttons
            nextPage = new TextButton("nextPage",this, pageFirst, 0.26f, 0.75f, 0.18f, 0.06f, 500f,Language.v("optionsmenu.next.page"),"N");
            addInput(0, nextPage);
            save = new TextButton("save",this, pageFirst, 0.5f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.save"),"S");
            addInput(0, save);
            cancel = new TextButton("cancel",this, pageFirst, 0.75f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.cancel"),"C");
            addInput(0, cancel);

            // --- Second Page ---

            // background
            Quad hudQuad2 = loadImageToQuad(BG_IMAGE, 
                                            0.8f*core.getDisplay().getWidth(), 
                                            1.61f*(core.getDisplay().getHeight() / 2), 
                                            core.getDisplay().getWidth() / 2, 
                                            1.13f*core.getDisplay().getHeight() / 2);
            hudQuad2.setRenderState(base.hud.hudAS);
            pageSecond.attachChild(hudQuad2);

            // header
            Quad header2 = loadImageToQuad("./data/ui/mainmenu/"+MainMenu.OPTIONS, sizeX, sizeY, posX, posY);
            header2.setRenderState(base.hud.hudAS);
            pageSecond.attachChild(header2);

            SimpleLayout inputsLayout2 = new SimpleLayout(0.2f, 0.16f, 0.3f, 0.07f ,2);
            inputsLayout2.addToColumn(0, new TextLabel("",this,pageSecond, 600f, Language.v("optionsmenu.normalmap.shader"), false));
            toggleNormalMapShader = new CheckBox("", this, pageSecond, J3DCore.NORMALMAP_ENABLED);
            inputsLayout2.addToColumn(1, toggleNormalMapShader, 0.1f, 0.5f);
            addInput(1, toggleNormalMapShader);
            inputsLayout2.addToColumn(0, new TextLabel("",this,pageSecond, 600f, Language.v("optionsmenu.water.detail"), false));
            selectWaterDeatil = new ListSelect("water", this, pageSecond, 600f, waterDeatilIds, waterDeatilIds, null, null);
            inputsLayout2.addToColumn(1, selectWaterDeatil, 0.35f, 0.5f);
            addInput(1, selectWaterDeatil);
            inputsLayout2.addToColumn(0, new TextLabel("",this,pageSecond, 600f, Language.v("optionsmenu.bloom"), false));
            toggleBloom = new CheckBox("", this, pageSecond, J3DCore.BLOOM_EFFECT);
            inputsLayout2.addToColumn(1, toggleBloom, 0.1f, 0.5f);
            addInput(1, toggleBloom);
            inputsLayout2.addToColumn(0, new TextLabel("",this,pageSecond, 600f, Language.v("optionsmenu.dof.effect"), false));
            toggleDepthOfField = new CheckBox("", this, pageSecond, J3DCore.DOF_EFFECT);
            inputsLayout2.addToColumn(1, toggleDepthOfField, 0.1f, 0.5f);
            addInput(1, toggleDepthOfField);
            inputsLayout2.addToColumn(0, new TextLabel("",this,pageSecond, 600f, Language.v("optionsmenu.shadow.distance"), false));
            tunerShadowDistance = new ValueTuner("",this,pageSecond, 600f, J3DCore.RENDER_SHADOW_DISTANCE, 0, 20, 1);
            inputsLayout2.addToColumn(1, tunerShadowDistance, 0.35f, 0.5f);
            addInput(1, tunerShadowDistance);
            inputsLayout2.addToColumn(0, new TextLabel("",this,pageSecond, 600f, Language.v("optionsmenu.slow.animation"), false));
            toggleSlowAnimation = new CheckBox("", this, pageSecond, J3DCore.SLOW_ANIMATION);
            inputsLayout2.addToColumn(1, toggleSlowAnimation, 0.1f, 0.5f);
            addInput(1, toggleSlowAnimation);

            // buttons
            prevPage = new TextButton("prevPage",this, pageSecond, 0.26f, 0.75f, 0.18f, 0.06f, 500f,Language.v("optionsmenu.prev.page"),"P");
            addInput(1, prevPage);

            // adding pages
            addPage(0, pageFirst);
            addPage(1, pageSecond);
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
            if (currentPage>0) {
                currentPage=0;
                setupPage();
                changePage(0);
            } else {
                toggle();
                core.mainMenu.toggle();
            }
        }
        return true;
    }

    @Override
    public boolean inputUsed(InputBase base, String message) {
        // Save
        if (base == save) {
            // Setting new Values to J3DCore
            J3DCore.MOUSELOOK = toggleMLook.isChecked();
            J3DCore.CONTINUOUS_LOAD = toggleContinuousLoad.isChecked();

            J3DCore.VIEW_DISTANCE = tunerViewDistance.getSelection();
            J3DCore.RENDER_DISTANCE = tunerRenderDistance.getSelection();
            J3DCore.RENDER_GRASS_DISTANCE = tunerRenderGrassDistance.getSelection();
            J3DCore.TEXTURE_QUALITY = tunerTextureDetail.getSelection();
            J3DCore.EFFECT_VOLUME_PERCENT = tunerEffectsVolume.getSelection();
            J3DCore.MUSIC_VOLUME_PERCENT = tunerMusicVolume.getSelection();

            J3DCore.NORMALMAP_ENABLED = toggleNormalMapShader.isChecked();
            // TODO: selectWaterDeatil value exam
            //J3DCore.WATER_SHADER;
            //J3DCore.WATER_DETAILED;
            J3DCore.BLOOM_EFFECT = toggleBloom.isChecked();
            J3DCore.DOF_EFFECT = toggleDepthOfField.isChecked();
            J3DCore.RENDER_SHADOW_DISTANCE = tunerShadowDistance.getSelection();
            J3DCore.SLOW_ANIMATION = toggleSlowAnimation.isChecked();

            // TODO: save to config file

            // back to main menu
            toggle();
            core.mainMenu.toggle();
            core.applyOptions();
            return true;
        // Cancel
        } else if (base == cancel) {
            // Resetting Ui elements
            toggleMLook.setChecked(J3DCore.MOUSELOOK);
            toggleContinuousLoad.setChecked(J3DCore.CONTINUOUS_LOAD);

            tunerViewDistance.setValue(J3DCore.VIEW_DISTANCE);
            tunerRenderDistance.setValue(J3DCore.RENDER_DISTANCE);
            tunerRenderGrassDistance.setValue(J3DCore.RENDER_GRASS_DISTANCE);
            tunerTextureDetail.setValue(J3DCore.TEXTURE_QUALITY);
            tunerEffectsVolume.setValue(J3DCore.EFFECT_VOLUME_PERCENT);
            tunerMusicVolume.setValue(J3DCore.MUSIC_VOLUME_PERCENT);

            toggleNormalMapShader.setChecked(J3DCore.NORMALMAP_ENABLED);
            toggleNormalMapShader.setChecked(J3DCore.NORMALMAP_ENABLED);
            // TODO: selectWaterDeatil value exam
            toggleBloom.setChecked(J3DCore.BLOOM_EFFECT);
            toggleDepthOfField.setChecked(J3DCore.DOF_EFFECT);
            tunerShadowDistance.setValue(J3DCore.RENDER_SHADOW_DISTANCE);
            toggleSlowAnimation.setChecked(J3DCore.SLOW_ANIMATION);

            // back to main menu
            toggle();
            core.mainMenu.toggle();
            return true; 
        // Next Page
        } else if (base == nextPage) {
            currentPage=1;
            nextPage.deactivate();
            setupPage();
            return true; 
        // Previous Page
        } else if (base == prevPage) {
            currentPage=0;
            prevPage.deactivate();
            setupPage();
            return true; 
        } else if (base == tunerViewDistance) {
            System.out.println("tunerViewDistance: "+tunerViewDistance.getSelection()+", tunerRenderDistance: "+tunerRenderDistance.getSelection());
            if (tunerViewDistance.getSelection()+6>tunerRenderDistance.getSelection()) {
                tunerRenderDistance.setValue(tunerViewDistance.getSelection()+6);
                tunerRenderDistance.setUpdated(true);
                tunerRenderDistance.deactivate();
            }
        } else if (base == tunerRenderDistance) {
            System.out.println("tunerViewDistance: "+tunerViewDistance.getSelection()+", tunerRenderDistance: "+tunerRenderDistance.getSelection());
            if (tunerViewDistance.getSelection()+6>tunerRenderDistance.getSelection()) {
                tunerRenderDistance.setValue(tunerViewDistance.getSelection()+6);
                tunerRenderDistance.setUpdated(true);
                tunerRenderDistance.activate();
            }
        }

        return true;
    }

}
