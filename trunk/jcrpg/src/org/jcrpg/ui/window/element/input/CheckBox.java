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

package org.jcrpg.ui.window.element.input;

import java.io.File;
import java.util.logging.Level;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.InputWindow;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

/**
 * CheckBox Input
 *
 * @author goq669
 */
public class CheckBox extends InputBase {

    public static final String defaultImage = "./data/ui/checkBoxBase.png";
    public static final String selectedImage = "./data/ui/checkBoxSelected.png";
    private String bgImage = defaultImage; 
    private boolean checked = false;

    Node activeNode = null;
    Node deactiveNode = null;

    public CheckBox(String id, InputWindow w, Node parentNode, boolean checked) {
        super(id, w, parentNode);
        setChecked(checked);
    }

    public CheckBox(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
            float sizeY, boolean checked) {
        super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
        setChecked(checked);
        deactivate();
        w.base.addEventHandler("lookLeft", w);
        w.base.addEventHandler("lookRight", w);
        w.base.addEventHandler("enter", w);
        w.base.addEventHandler("space", w);
        parentNode.updateRenderState();
    }
	

    @Override
    public void init(float centerX, float centerY, float sizeX, float sizeY) {
        super.init(centerX, centerY, sizeX, sizeY);
        if (baseNode!=null) {
            deactivate(); 
            w.base.addEventHandler("lookLeft", w);
            w.base.addEventHandler("lookRight", w);
            w.base.addEventHandler("enter", w);
            w.base.addEventHandler("space", w);
            parentNode.updateRenderState();
        }
    }

    @Override
    public void activate() {
        baseNode.detachAllChildren();
        activeNode = new Node();
        try {
            Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
            w1.setSolidColor(ColorRGBA.white);
            activeNode.attachChild(w1);
        } catch (Exception ex) {
            if (J3DCore.LOGGING) { Jcrpg.LOGGER.log(Level.SEVERE, ex.getMessage(), ex); }
            ex.printStackTrace();
        }
        baseNode.attachChild(activeNode);
        baseNode.updateRenderState();
        super.activate();
    }

    @Override
    public void deactivate() {
        baseNode.detachAllChildren();
        deactiveNode = new Node();
        try {
            Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
            w1.setSolidColor(ColorRGBA.gray);
            deactiveNode.attachChild(w1);
        } catch (Exception ex) {
            if (J3DCore.LOGGING) { Jcrpg.LOGGER.log(Level.SEVERE, ex.getMessage(), ex); }
            ex.printStackTrace();
        }
        baseNode.attachChild(deactiveNode);
        baseNode.updateRenderState();
        super.deactivate();
    }

    @Override
    public boolean handleKey(String key) {
        if (key.equals("enter") || key.equals("space") || 
            key.equals("lookLeft") || key.equals("lookRight") ) 
        {
            setChecked(!isChecked());
            w.core.audioServer.play(SOUND_INPUTSELECTED);
            w.inputUsed(this, key);
            this.setUpdated(true);
            activate();
            return true;
        }
        if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("--- "+id+" "+key);
        return false;
    }

    @Override
    public void reset() {
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        bgImage = (checked ? selectedImage : defaultImage); 
    }

}
