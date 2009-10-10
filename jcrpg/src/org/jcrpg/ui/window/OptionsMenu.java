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

import java.io.File;
import java.util.HashMap;
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
import com.jme.scene.shape.Quad;

/**
 * UI class for changing configuration settings in game.
 *
 * @author goq669
 */
public class OptionsMenu extends PagedInputWindow {

    public static final String  CONFIGFILE_CUSTOM = "./config.properties";
    private static final String CONFIGFILE_PRESET_PREFIX  = "./data/configs/config-";
    private static final String CONFIGFILE_PRESET_POSTFIX = ".properties";
    public static final String  CONFIGFILE_PRESET_HIGHEST = CONFIGFILE_PRESET_PREFIX+"highest"+CONFIGFILE_PRESET_POSTFIX;
    public static final String  CONFIGFILE_PRESET_HIGH    = CONFIGFILE_PRESET_PREFIX+"high"+CONFIGFILE_PRESET_POSTFIX;
    public static final String  CONFIGFILE_PRESET_NORMAL  = CONFIGFILE_PRESET_PREFIX+"normal"+CONFIGFILE_PRESET_POSTFIX;
    public static final String  CONFIGFILE_PRESET_LOW     = CONFIGFILE_PRESET_PREFIX+"low"+CONFIGFILE_PRESET_POSTFIX;
    public static final String  CONFIGFILE_PRESET_LOWEST  = CONFIGFILE_PRESET_PREFIX+"lowest"+CONFIGFILE_PRESET_POSTFIX;
    public static final String[] CONFIGFILES = {CONFIGFILE_PRESET_HIGHEST, CONFIGFILE_PRESET_HIGH, 
                                                CONFIGFILE_PRESET_NORMAL, CONFIGFILE_PRESET_LOW, CONFIGFILE_PRESET_LOWEST};
    private HashMap<Integer, J3DCore.CoreSettings> coreSettingsMap;

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
    ListSelect selectPreset;
    static final String[] presetIds = {
    								   Language.v("optionsmenu.preset.custom"),
    								   Language.v("optionsmenu.preset.highest"),
                                       Language.v("optionsmenu.preset.high"),
                                       Language.v("optionsmenu.preset.normal"),
                                       Language.v("optionsmenu.preset.low"),
                                       Language.v("optionsmenu.preset.lowest"),
                                       };
    
    CheckBox toggleBloom;
    CheckBox toggleDepthOfField;
    ValueTuner tunerShadowDistance;
    CheckBox toggleSlowAnimation;

    TextButton save, cancel, save2, cancel2, nextPage, prevPage;

    public OptionsMenu(UIBase base) {
        super(base);
        coreSettingsMap = new HashMap<Integer, J3DCore.CoreSettings>();
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

            SimpleLayout firstLayout = new SimpleLayout(0.30f, 0.16f, 0.25f, 0.07f ,3);
            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.presets"), false));
            selectPreset = new ListSelect("preset", this, pageFirst, 600f, presetIds, presetIds, null, null);
            //selectPreset.focusUponMouseEnter = true;
            //selectPreset.detach();
            addInput(0, selectPreset); 
            firstLayout.addToColumn(1, selectPreset, 0.85f, 0.5f);
            selectPreset.setSelected(0);             
          
            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.mouselook"), false));
            toggleMLook = new CheckBox("", this, pageFirst, J3DCore.SETTINGS.MOUSELOOK);
            firstLayout.addToColumn(1, toggleMLook, 0.1f, 0.5f);
            addInput(0, toggleMLook);

            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.view.distance"), false));
            tunerViewDistance = new ValueTuner("",this, pageFirst, 600f, J3DCore.SETTINGS.VIEW_DISTANCE, 10, 100, 2);
            firstLayout.addToColumn(1, tunerViewDistance, 0.35f, 0.5f);
            addInput(0, tunerViewDistance);
            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.render.distance"), false));
            int tunerValue = (J3DCore.SETTINGS.RENDER_DISTANCE>J3DCore.SETTINGS.VIEW_DISTANCE*1.5f ? J3DCore.SETTINGS.RENDER_DISTANCE : (int)(J3DCore.SETTINGS.VIEW_DISTANCE*1.5f));
            tunerRenderDistance = new ValueTuner("",this, pageFirst, 600f, tunerValue, 16, 150, 2);
            firstLayout.addToColumn(1, tunerRenderDistance, 0.35f, 0.5f);
            addInput(0, tunerRenderDistance);
            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.render.grass.distance"), false));
            tunerRenderGrassDistance = new ValueTuner("",this, pageFirst, 600f, J3DCore.SETTINGS.RENDER_GRASS_DISTANCE, 0, 20, 1);
            firstLayout.addToColumn(1, tunerRenderGrassDistance, 0.35f, 0.5f);
            addInput(0, tunerRenderGrassDistance);
            firstLayout.addToColumn(2, new TextLabel("",this, pageFirst, 600f, "", false), 4); // placeholder for rowspan
            firstLayout.addToColumn(2, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.needs.restart"), false, true));
            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.texture.detail"), false));
            tunerTextureDetail = new ValueTuner("",this, pageFirst, 600f, J3DCore.SETTINGS.TEXTURE_QUALITY, 0, 2, 1);
            firstLayout.addToColumn(1, tunerTextureDetail, 0.35f, 0.5f);
            addInput(0, tunerTextureDetail);
            //firstLayout.addToColumn(2, new TextLabel("",this, pageFirst, 600f, "", false), 5); // placeholder for rowspan
            firstLayout.addToColumn(2, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.needs.restart"), false, true));

            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.effects.volume"), false));
            tunerEffectsVolume = new ValueTuner("",this,pageFirst, 600f, J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT, 0, 100, 5);
            firstLayout.addToColumn(1, tunerEffectsVolume, 0.35f, 0.5f);
            addInput(0, tunerEffectsVolume);
            firstLayout.addToColumn(0, new TextLabel("",this, pageFirst, 600f, Language.v("optionsmenu.music.volume"), false));
            tunerMusicVolume = new ValueTuner("",this,pageFirst, 600f, J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT, 0, 100, 5);
            firstLayout.addToColumn(1, tunerMusicVolume, 0.35f, 0.5f);
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

            SimpleLayout secondLayout = new SimpleLayout(0.30f, 0.16f, 0.25f, 0.07f ,3);
            //SimpleLayout secondLayout = new SimpleLayout(0.4f, 0.16f, 0.3f, 0.07f ,3);
            
            secondLayout.addToColumn(0, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.continuous.load"), false));
            toggleContinuousLoad = new CheckBox("", this, pageSecond, J3DCore.SETTINGS.CONTINUOUS_LOAD);
            secondLayout.addToColumn(1, toggleContinuousLoad, 0.1f, 0.5f);
            addInput(1, toggleContinuousLoad);
            
            //firstLayout.addToColumn(2, new TextLabel("",this, pageFirst, 600f, "", false), 5); // placeholder for rowspan
            secondLayout.addToColumn(0, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.normalmap.shader"), false));
            toggleNormalMapShader = new CheckBox("", this, pageSecond, J3DCore.SETTINGS.NORMALMAP_ENABLED);
            secondLayout.addToColumn(1, toggleNormalMapShader, 0.1f, 0.5f);
            addInput(1, toggleNormalMapShader);
            secondLayout.addToColumn(2, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.needs.restart"), false, true));

            secondLayout.addToColumn(0, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.water.detail"), false));
            selectWaterDeatil = new ListSelect("water", this, pageSecond, 600f, waterDeatilIds, waterDeatilIds, null, null);
            secondLayout.addToColumn(1, selectWaterDeatil, 0.35f, 0.5f);
            addInput(1, selectWaterDeatil);
            secondLayout.addToColumn(0, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.bloom"), false));
            toggleBloom = new CheckBox("", this, pageSecond, J3DCore.SETTINGS.BLOOM_EFFECT);
            secondLayout.addToColumn(1, toggleBloom, 0.1f, 0.5f);
            addInput(1, toggleBloom);
            secondLayout.addToColumn(0, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.dof.effect"), false));
            toggleDepthOfField = new CheckBox("", this, pageSecond, J3DCore.SETTINGS.DOF_EFFECT);
            secondLayout.addToColumn(1, toggleDepthOfField, 0.1f, 0.5f);
            addInput(1, toggleDepthOfField);
            secondLayout.addToColumn(0, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.shadow.distance"), false));
            tunerShadowDistance = new ValueTuner("",this,pageSecond, 600f, J3DCore.SETTINGS.RENDER_SHADOW_DISTANCE, 0, 20, 1);
            secondLayout.addToColumn(1, tunerShadowDistance, 0.35f, 0.5f);
            addInput(1, tunerShadowDistance);
            secondLayout.addToColumn(0, new TextLabel("",this, pageSecond, 600f, Language.v("optionsmenu.slow.animation"), false));
            toggleSlowAnimation = new CheckBox("", this, pageSecond, J3DCore.SETTINGS.SLOW_ANIMATION);
            secondLayout.addToColumn(1, toggleSlowAnimation, 0.1f, 0.5f);
            addInput(1, toggleSlowAnimation);

            // buttons
            prevPage = new TextButton("prevPage",this, pageSecond, 0.26f, 0.75f, 0.18f, 0.06f, 500f,Language.v("optionsmenu.prev.page"),"P");
            addInput(1, prevPage);
            save2 = new TextButton("save",this, pageSecond, 0.5f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.save"),"S");
            addInput(1, save2);
            cancel2 = new TextButton("cancel",this, pageSecond, 0.75f, 0.75f, 0.18f, 0.06f, 500f,Language.v("behaviorWindow.cancel"),"C");
            addInput(1, cancel2);

            // adding pages
            addPage(0, pageFirst);
            addPage(1, pageSecond);
            windowNode.updateRenderState();
            base.addEventHandler("back", this);
        } catch (Exception ex) {
            if (J3DCore.SETTINGS.LOGGING) { Jcrpg.LOGGER.log(Level.SEVERE, "OptionsMenu creation error: "+ex.getMessage(), ex); }
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
    public boolean inputChanged(InputBase base, String message) {
        if (base == tunerViewDistance) {
            // not working. why?
        } else if (base == tunerRenderDistance) {
            // not working. why?
        } else if (base == selectPreset) {
            Integer key = new Integer(selectPreset.getSelection());
            J3DCore.CoreSettings coreSettings = coreSettingsMap.get(key);
            if (coreSettings==null) {
                if (selectPreset.getSelection()==0) {
                    coreSettings = J3DCore.SETTINGS;
                } else {
                    coreSettings = J3DCore.loadConfig(CONFIGFILES[key.intValue()-1]);
                    coreSettingsMap.put(key, coreSettings);
                }
            }
            //System.out.println("====-------- coreSettings.RENDER_DISTANCE: "+coreSettings.RENDER_DISTANCE);
            fillOptions(coreSettings);
        }
        return true;
    }

    @Override
    public boolean inputUsed(InputBase base, String message) {
        // Save
        if (base == save || base == save2) {
            // Setting new Values to J3DCore
            J3DCore.SETTINGS.MOUSELOOK = toggleMLook.isChecked();
            J3DCore.SETTINGS.CONTINUOUS_LOAD = toggleContinuousLoad.isChecked();

            J3DCore.SETTINGS.VIEW_DISTANCE = tunerViewDistance.getSelection();
            J3DCore.SETTINGS.RENDER_DISTANCE = tunerRenderDistance.getSelection();
            J3DCore.SETTINGS.RENDER_DISTANCE_CALC = (int)(tunerRenderDistance.getSelection()/J3DCore.CUBE_EDGE_SIZE);
            J3DCore.SETTINGS.RENDER_GRASS_DISTANCE = tunerRenderGrassDistance.getSelection();
            J3DCore.SETTINGS.TEXTURE_QUALITY = tunerTextureDetail.getSelection();
            J3DCore.SETTINGS.EFFECT_VOLUME_PERCENT = tunerEffectsVolume.getSelection();
            J3DCore.SETTINGS.MUSIC_VOLUME_PERCENT = tunerMusicVolume.getSelection();

            J3DCore.SETTINGS.NORMALMAP_ENABLED = toggleNormalMapShader.isChecked();

            // water detail
            int selIndex = selectWaterDeatil.getSelection();
            J3DCore.SETTINGS.WATER_SHADER = (selIndex==0);
            J3DCore.SETTINGS.WATER_DETAILED = (selIndex==2);

            J3DCore.SETTINGS.BLOOM_EFFECT = toggleBloom.isChecked();
            J3DCore.SETTINGS.DOF_EFFECT = toggleDepthOfField.isChecked();
            J3DCore.SETTINGS.RENDER_SHADOW_DISTANCE = tunerShadowDistance.getSelection();
            J3DCore.SETTINGS.SHADOWS=J3DCore.SETTINGS.RENDER_SHADOW_DISTANCE>0;
            J3DCore.SETTINGS.SLOW_ANIMATION = toggleSlowAnimation.isChecked();

            //System.out.println("SAVING...");
            J3DCore.SETTINGS.saveFile(new File("./config.properties"));
            //System.out.println("TOGGLE");
    		
            // back to main menu
            toggle();
            core.mainMenu.toggle();
    		
            core.applyOptions();
            return true;
        // Cancel
        } else if (base == cancel || base == cancel2) {
            // Resetting Ui elements
            fillOptions(J3DCore.SETTINGS);

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
            if (tunerViewDistance.getSelection()*1.5f>tunerRenderDistance.getSelection()) {
                tunerRenderDistance.setValue((int)(tunerViewDistance.getSelection()*1.5f));
                tunerRenderDistance.setUpdated(true);
                tunerRenderDistance.deactivate();
            }
        } else if (base == tunerRenderDistance) {
            if (tunerViewDistance.getSelection()*1.5f>tunerRenderDistance.getSelection()) {
                tunerRenderDistance.setValue((int)(tunerViewDistance.getSelection()*1.5f));
                tunerRenderDistance.setUpdated(true);
                tunerRenderDistance.activate();
            }
        } 

        return true;
    }

    /**
     * Setting Options values from a CoreSettings instance
     */
    private void fillOptions(J3DCore.CoreSettings coreSettings) {
        toggleMLook.setChecked(coreSettings.MOUSELOOK); toggleMLook.setUpdated(true);toggleMLook.deactivate();
        toggleContinuousLoad.setChecked(coreSettings.CONTINUOUS_LOAD); toggleContinuousLoad.deactivate();
        tunerViewDistance.setValue(coreSettings.VIEW_DISTANCE); tunerViewDistance.setUpdated(true);tunerViewDistance.deactivate();
        System.out.println("-------------------- "+coreSettings.RENDER_DISTANCE);
        tunerRenderDistance.setValue(coreSettings.RENDER_DISTANCE); tunerRenderDistance.setUpdated(true);tunerRenderDistance.deactivate();
        tunerRenderGrassDistance.setValue(coreSettings.RENDER_GRASS_DISTANCE); tunerRenderGrassDistance.setUpdated(true);tunerRenderGrassDistance.deactivate();
        tunerTextureDetail.setValue(coreSettings.TEXTURE_QUALITY); tunerTextureDetail.setUpdated(true);tunerTextureDetail.deactivate();
        tunerEffectsVolume.setValue(coreSettings.EFFECT_VOLUME_PERCENT); tunerEffectsVolume.setUpdated(true);tunerEffectsVolume.deactivate();
        tunerMusicVolume.setValue(coreSettings.MUSIC_VOLUME_PERCENT); tunerMusicVolume.setUpdated(true);tunerMusicVolume.deactivate();
        toggleNormalMapShader.setChecked(coreSettings.NORMALMAP_ENABLED); toggleNormalMapShader.setUpdated(true);toggleNormalMapShader.deactivate();

        // water detail
        int selIndex = (coreSettings.WATER_SHADER ? 0 : 1);
        selIndex = (coreSettings.WATER_DETAILED ? 2 : selIndex);
        selectWaterDeatil.setSelected(selIndex); selectWaterDeatil.deactivate();

        toggleBloom.setChecked(coreSettings.BLOOM_EFFECT); toggleBloom.deactivate();
        toggleDepthOfField.setChecked(coreSettings.DOF_EFFECT); toggleDepthOfField.setUpdated(true);toggleDepthOfField.deactivate();
        tunerShadowDistance.setValue(coreSettings.RENDER_SHADOW_DISTANCE); tunerShadowDistance.setUpdated(true);tunerShadowDistance.deactivate();
        toggleSlowAnimation.setChecked(coreSettings.SLOW_ANIMATION); toggleSlowAnimation.setUpdated(true);toggleSlowAnimation.deactivate();
        //setupPage();
    }

}
