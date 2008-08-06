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

package org.jcrpg.apps.test;

import java.io.File;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.ModelLoader.BillboardNodePooled;
import org.jcrpg.threed.jme.TextureCreator;
import org.jcrpg.threed.jme.moving.AnimatedModelNode;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.scene.config.SideTypeModels;
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.BillboardNode;
import com.jme.scene.ImposterNode;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.effects.LensFlare;
import com.jmex.model.util.ModelLoader;

/**
 * <code>TestLensFlare</code> Test of the lens flare effect in jME. Notice
 * that currently it doesn't do occlusion culling.
 * 
 * @author Joshua Slack
 * @version $Id: TestLensFlare.java,v 1.15 2006/11/16 19:59:29 nca Exp $
 */
public class AnimatedModelViewer extends SimplePassGame{

	
	long time = System.currentTimeMillis();
	int animCount = 0;
    @Override
	protected void simpleUpdate() {
    	
		super.simpleUpdate();
		if (true) return;
		
		if (time-System.currentTimeMillis()<-4000)
		{
			time = System.currentTimeMillis();
			AnimatedModelNode n = this.n;
			{
				if (animCount==0)
					n.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER);
				else 
				if (animCount==1)
						n.playAnimation(MovingModelAnimDescription.ANIM_DEFEND_UPPER);
				if (animCount==2)
					n.playAnimation(MovingModelAnimDescription.ANIM_PAIN);
				if (animCount==3)
					n.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE);
				//n.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER, MovingModelAnimDescription.ANIM_IDLE_COMBAT);
				animCount++;
				animCount=animCount%4;
			}
			n = this.n2;
			{
				time = System.currentTimeMillis();
				if (animCount==0)
					n.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER);
				else 
				if (animCount==1)
						n.playAnimation(MovingModelAnimDescription.ANIM_DEFEND_UPPER);
				if (animCount==2)
					n.playAnimation(MovingModelAnimDescription.ANIM_PAIN);
				if (animCount==3)
					n.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE);
				//n.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER, MovingModelAnimDescription.ANIM_IDLE_COMBAT);
				animCount++;
				animCount=animCount%4;
			}
			}
			
	}

	private LightNode lightNode;
    LensFlare flare;

    static String mesh = null;
    static String anim = null;
    
    
    public static void main(String[] args) {
    	//if (args.length<2) return;
    	//mesh = args[0];
    	//anim = args[1];
        AnimatedModelViewer app = new AnimatedModelViewer();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
    AnimatedModelNode n =null;
    AnimatedModelNode n2 =null;
    protected void simpleInitGame() {

    	display.getRenderer().setBackgroundColor(ColorRGBA.lightGray);
        display.setTitle("Lens Flare!");
        cam.setLocation(new Vector3f(0.0f, 0.0f, 10.0f));
        cam.update();
        lightState.detachAll();
    	SimpleResourceLocator loc1 = new SimpleResourceLocator( new File("./data/models/fauna/gorilla").toURI());
    	SimpleResourceLocator loc2 = new SimpleResourceLocator( new File("./data/textures/common").toURI());
    	SimpleResourceLocator loc3 = new SimpleResourceLocator( new File("./data/textures/low").toURI());
       ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, loc1);
       ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, loc2);
       ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, loc3);
       MovingModelAnimDescription des = new MovingModelAnimDescription();
       des.IDLE = anim;
       
       ;
 /*     n = new AnimatedModelNode(BoarmanTribe.boarmanMale.modelName,BoarmanTribe.boarmanMale.animation,1f,new float[] {0,0,0},1f);
      n.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE_COMBAT);

      n2 = new AnimatedModelNode(BoarmanTribe.boarmanMale.modelName,BoarmanTribe.boarmanMale.animation,1f,new float[] {0,0,0},1f);
      n2.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE_COMBAT);
      n2.setLocalTranslation(new Vector3f(3f,0,0));

        rootNode.attachChild(n);
        rootNode.attachChild(n2);
*/
        PointLight dr = new PointLight();
        dr.setEnabled(true);
        dr.setDiffuse(ColorRGBA.white);
        dr.setAmbient(ColorRGBA.gray);
        dr.setLocation(new Vector3f(0f, 0f, 0f));
        lightState.setTwoSidedLighting(false);

        lightNode = new LightNode("light", lightState);
        lightNode.setLight(dr);

        Vector3f min2 = new Vector3f(-0.5f, -0.5f, -0.5f);
        Vector3f max2 = new Vector3f(0.5f, 0.5f, 0.5f);
        //Box lightBox = new Box("box", min2, max2);
        //lightBox.setModelBound(new BoundingBox());
        //lightBox.updateModelBound();
        //lightNode.attachChild(lightBox);
        lightNode.setTarget(rootNode);
        lightNode.setLocalTranslation(new Vector3f(+14f, 14f, +14f));

        // clear the lights from this lightbox so the lightbox itself doesn't
        // get affected by light:
        
        //lightBox.setLightCombineMode(LightState.OFF);

        // Setup the lensflare textures.
        
        
        J3DCore core = new J3DCore();
        Jcrpg.LOGGER = java.util.logging.Logger.getLogger("");
        org.jcrpg.threed.ModelLoader loader = new org.jcrpg.threed.ModelLoader(core);
        core.modelLoader = loader;
        core.setCamera(cam);
        core.fs_external = DisplaySystem.getDisplaySystem().getRenderer().createFogState();
		
        PartlyBillboardModel cherry = new PartlyBillboardModel("pbm_cherry_0","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"cher_1.png"},0,false);
		cherry.genericScale=1.5f;
		cherry.shadowCaster = true;
        
		Node node = null;
		node = loader.loadNode(cherry,false);
		BillboardPartVegetation bbOrig = new BillboardPartVegetation(core,cam,SideTypeModels.TREE_LOD_DIST[3][1],cherry,false, false);
		//sharedBBNodeCache.put(key, bbOrig);
		bbOrig.attachChild(node);
		bbOrig.setModelBound(new BoundingBox());
		bbOrig.updateModelBound();
		bbOrig.updateRenderState();
		bbOrig.updateGeometricState(0, true);
		BoundingBox bound = (BoundingBox) bbOrig.getWorldBound();
	    float size = bound.xExtent;
	    if (bound.yExtent > size) size = bound.yExtent;
	    if (bound.zExtent > size) size = bound.zExtent;
	    final float sizeFaktor = 1.6f;
	    // We should make the size of the quad a little larger that the real scene
	    size *= 2 * sizeFaktor; 
		
		ImposterNode iNode = new ImposterNode("1",15, 256, 256);
		iNode.attachChild(bbOrig);
		
		iNode.setLocalTranslation(bound.getCenter().clone());
	      // We must update the world data explicitly to update the quads world
	      // bounds. The texture rendering camera will aim at it's position
	      iNode.updateWorldData(0);		
		final float TEXTURE_CAM_DISTANCE = 100; // just a decision
	    iNode.setCameraDistance(TEXTURE_CAM_DISTANCE);
	    Camera textureCam = iNode.getTextureRenderer().getCamera();
	    // Setup the cam frustrum that it matches the size of the object
	    float viewAngle = FastMath.atan(size / TEXTURE_CAM_DISTANCE)
	        * FastMath.RAD_TO_DEG;
	    textureCam.setFrustumPerspective(viewAngle, 1, 1, TEXTURE_CAM_DISTANCE * 2);		
	    //iNode.updateCamera(bound.getCenter().add(0,0,-1)); // just provide a direction
	      iNode.updateScene(0);
	      iNode.renderTexture();
	      BillboardNode bNode = new BillboardNode("billboard");
	      bNode.setAlignment(BillboardNode.CAMERA_ALIGNED); // just any alignment
	      Quad q = iNode.getStandIn();
	      bNode.attachChild(q);
	      bNode.setModelBound(new BoundingBox());
	      bNode.updateModelBound();
	      bNode.updateRenderState();
	      //bNode.setLocalRotation(J3DCore.qE);
		
		rootNode.attachChild(bNode);
		
        rootNode.updateRenderState();
        rootNode.attachChild(lightNode);
        RenderPass rootPass = new RenderPass();
        rootPass.add(rootNode);
        pManager.add(rootPass);


        // notice that it comes at the end
        //lightNode.attachChild(flare);

    }
    private TextureState createLightmapTextureState(String texture) {
        TextureState ts = display.getRenderer().createTextureState();

        Texture t0 = TextureManager.loadTexture(texture,
                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
        t0.setWrap(Texture.WM_WRAP_S_WRAP_T);
        ts.setTexture(t0, 0);

        return ts;
    }

  
    private TextureState createSplatTextureState(String texture, String alpha) {
        TextureState ts = display.getRenderer().createTextureState();
        boolean[][] data = new boolean[50][50];
        Texture t0 = TextureCreator.newAlphaMaskTexture(data);
        //Texture t0 = TextureManager.loadTexture(texture,
          //   Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
        //t0.setWrap(Texture.WM_WRAP_S_WRAP_T);
        //t0.setApply(Texture.AM_MODULATE);
        //t0.setScale(new Vector3f(1.f,1.f, 1.0f));
        ts.setTexture(t0, 0);
        ts.apply();
        if (alpha != null) {
            addAlphaSplat(ts, alpha);
        }

        return ts;
    }

    private void addAlphaSplat(TextureState ts, String alpha) {
    	boolean[][] data = new boolean[256][256];
    	data[3][3] = true;
        Texture t1 = TextureCreator.newAlphaMaskTexture(data);
        	
        	
        	//TextureManager.loadTexture(alpha, Texture.MM_LINEAR_LINEAR,
              //  Texture.FM_LINEAR);
        //t1.setScale(new Vector3f(1110.01f,1110.01f, 1110.01f));
        t1.setWrap(Texture.WM_WRAP_S_WRAP_T);
        t1.setApply(Texture.AM_COMBINE);
        t1.setCombineFuncRGB(Texture.ACF_REPLACE);
        t1.setCombineSrc0RGB(Texture.ACS_PREVIOUS);
        t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
        t1.setCombineFuncAlpha(Texture.ACF_REPLACE);
        ts.setTexture(t1, ts.getNumberOfSetTextures());
        ts.apply();
    }


}