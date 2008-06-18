/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jcrpg.apps;

import java.io.File;

import org.jcrpg.threed.jme.moving.AnimatedModelNode;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;

import com.jme.app.SimpleGame;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;

/**
 * <code>TestLensFlare</code> Test of the lens flare effect in jME. Notice
 * that currently it doesn't do occlusion culling.
 * 
 * @author Joshua Slack
 * @version $Id: TestLensFlare.java,v 1.15 2006/11/16 19:59:29 nca Exp $
 */
public class AnimatedModelViewer extends SimpleGame {

    private LightNode lightNode;
    LensFlare flare;

    static String mesh = null;
    static String anim = null;
    
    
    public static void main(String[] args) {
    	if (args.length<2) return;
    	mesh = args[0];
    	anim = args[1];
        AnimatedModelViewer app = new AnimatedModelViewer();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {

    	display.getRenderer().setBackgroundColor(ColorRGBA.lightGray);
        display.setTitle("Lens Flare!");
        cam.setLocation(new Vector3f(0.0f, 0.0f, 10.0f));
        cam.update();
        lightState.detachAll();
    	SimpleResourceLocator loc1 = new SimpleResourceLocator( new File("./data/models/fauna/gorilla").toURI());
       ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, loc1);
       MovingModelAnimDescription des = new MovingModelAnimDescription();
       des.IDLE = anim;
      AnimatedModelNode n = new AnimatedModelNode(mesh,des,1f);
        
        rootNode.attachChild(n);

        PointLight dr = new PointLight();
        dr.setEnabled(true);
        dr.setDiffuse(ColorRGBA.white);
        dr.setAmbient(ColorRGBA.gray);
        dr.setLocation(new Vector3f(0f, 0f, 0f));
        lightState.setTwoSidedLighting(true);

        lightNode = new LightNode("light", lightState);
        lightNode.setLight(dr);

        Vector3f min2 = new Vector3f(-0.5f, -0.5f, -0.5f);
        Vector3f max2 = new Vector3f(0.5f, 0.5f, 0.5f);
        //Box lightBox = new Box("box", min2, max2);
        //lightBox.setModelBound(new BoundingBox());
        //lightBox.updateModelBound();
        //lightNode.attachChild(lightBox);
        lightNode.setTarget(rootNode);
        lightNode.setLocalTranslation(new Vector3f(-14f, 14f, -14f));

        // clear the lights from this lightbox so the lightbox itself doesn't
        // get affected by light:
        
        //lightBox.setLightCombineMode(LightState.OFF);

        // Setup the lensflare textures.
        TextureState[] tex = new TextureState[4];
        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture("./data/flare/flare1.png",
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888,
                1.0f, true));
        tex[0].setEnabled(true);

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture("./data/flare/flare2.png",
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR));
        tex[1].setEnabled(true);

        tex[2] = display.getRenderer().createTextureState();
        tex[2].setTexture(TextureManager.loadTexture(("./data/flare/flare3.png"),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR));
        tex[2].setEnabled(true);

        tex[3] = display.getRenderer().createTextureState();
        tex[3].setTexture(TextureManager.loadTexture(("./data/flare/flare4.png"),
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR));
        tex[3].setEnabled(true);

        flare = LensFlareFactory.createBasicLensFlare("flare", tex);
        flare.setRootNode(rootNode);
        lightNode.attachChild(flare);
        rootNode.attachChild(lightNode);

        // notice that it comes at the end
        //lightNode.attachChild(flare);

    }

}