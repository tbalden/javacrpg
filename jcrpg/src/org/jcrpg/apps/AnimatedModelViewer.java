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
import java.nio.FloatBuffer;

import org.jcrpg.threed.jme.effects.DepthOfFieldRenderPass;
import org.jcrpg.threed.jme.moving.AnimatedModelNode;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;

import com.jme.app.SimplePassGame;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.PassNode;
import com.jme.scene.PassNodeState;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;
import com.jmex.terrain.TerrainBlock;

/**
 * <code>TestLensFlare</code> Test of the lens flare effect in jME. Notice that
 * currently it doesn't do occlusion culling.
 * 
 * @author Joshua Slack
 * @version $Id: TestLensFlare.java,v 1.15 2006/11/16 19:59:29 nca Exp $
 */
public class AnimatedModelViewer extends SimplePassGame {

	long time = System.currentTimeMillis();
	int animCount = 0;

	@Override
	protected void simpleUpdate() {
		pManager.updatePasses(tpf);
		super.simpleUpdate();
		if (true)
			return;

		if (time - System.currentTimeMillis() < -4000) {
			time = System.currentTimeMillis();
			AnimatedModelNode n = this.n;
			{
				if (animCount == 0)
					n
							.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER);
				else if (animCount == 1)
					n
							.playAnimation(MovingModelAnimDescription.ANIM_DEFEND_UPPER);
				if (animCount == 2)
					n.playAnimation(MovingModelAnimDescription.ANIM_PAIN);
				if (animCount == 3)
					n.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE);
				// n.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER,
				// MovingModelAnimDescription.ANIM_IDLE_COMBAT);
				animCount++;
				animCount = animCount % 4;
			}
			n = this.n2;
			{
				time = System.currentTimeMillis();
				if (animCount == 0)
					n
							.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER);
				else if (animCount == 1)
					n
							.playAnimation(MovingModelAnimDescription.ANIM_DEFEND_UPPER);
				if (animCount == 2)
					n.playAnimation(MovingModelAnimDescription.ANIM_PAIN);
				if (animCount == 3)
					n.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE);
				// n.playAnimation(MovingModelAnimDescription.ANIM_ATTACK_LOWER,
				// MovingModelAnimDescription.ANIM_IDLE_COMBAT);
				animCount++;
				animCount = animCount % 4;
			}
		}

	}

	private LightNode lightNode;
	LensFlare flare;

	static String mesh = null;
	static String anim = null;

	public static void main(String[] args) {
		if (args.length < 2)
			return;
		mesh = args[0];
		anim = args[1];
		AnimatedModelViewer app = new AnimatedModelViewer();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	AnimatedModelNode n = null;
	AnimatedModelNode n2 = null;

	protected void simpleInitGame() {

		display.getRenderer().setBackgroundColor(ColorRGBA.lightGray);
		display.setTitle("Lens Flare!");
		cam.setLocation(new Vector3f(0.0f, 0.0f, 10.0f));
		cam.update();
		lightState.detachAll();
		SimpleResourceLocator loc1 = new SimpleResourceLocator(new File(
				"./data/models/fauna/gorilla").toURI());
		SimpleResourceLocator loc2 = new SimpleResourceLocator(new File(
				"./data/textures/common").toURI());
		SimpleResourceLocator loc3 = new SimpleResourceLocator(new File(
				"./data/textures/low").toURI());
		ResourceLocatorTool.addResourceLocator(
				ResourceLocatorTool.TYPE_TEXTURE, loc1);
		ResourceLocatorTool.addResourceLocator(
				ResourceLocatorTool.TYPE_TEXTURE, loc2);
		ResourceLocatorTool.addResourceLocator(
				ResourceLocatorTool.TYPE_TEXTURE, loc3);
		MovingModelAnimDescription des = new MovingModelAnimDescription();
		des.IDLE = anim;

		;
		/*
		 * n = new
		 * AnimatedModelNode(BoarmanTribe.boarmanMale.modelName,BoarmanTribe
		 * .boarmanMale.animation,1f,new float[] {0,0,0},1f);
		 * n.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE_COMBAT);
		 * rootNode.attachChild(n); n2 = new
		 * AnimatedModelNode(BoarmanTribe.boarmanMale
		 * .modelName,BoarmanTribe.boarmanMale.animation,1f,new float[]
		 * {0,0,0},1f);
		 * n2.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE_COMBAT);
		 * n2.setLocalTranslation(new Vector3f(3f,0,0));
		 */
		/*
		 * for (int i=0; i<36; i++) { n = new
		 * AnimatedModelNode(BoarmanTribe.boarmanMaleMage
		 * .modelName,BoarmanTribe.boarmanMaleMage.animation,1f,new float[]
		 * {0,0,0},1f); //SharedNode ns = new SharedNode("_",n);
		 * //ns.setLocalTranslation(new Vector3f(i/4,2f+(i%4)2f,0)); //n = new
		 * AnimatedModelNode
		 * (BoarmanTribe.boarmanMaleMage.modelName,BoarmanTribe.
		 * boarmanMaleMage.animation,1f,true);;
		 * n.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE_COMBAT);
		 * n.unlockTransforms(); n.setLocalTranslation(new
		 * Vector3f(i/4,2f+(i%4)2f,0)); n.lockTransforms(); //n.lockBranch();
		 * rootNode.attachChild(n); } rootNode.attachChild(n);
		 * //rootNode.attachChild(n2);
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
		// Box lightBox = new Box("box", min2, max2);
		// lightBox.setModelBound(new BoundingBox());
		// lightBox.updateModelBound();
		// lightNode.attachChild(lightBox);
		lightNode.setTarget(rootNode);
		lightNode.setLocalTranslation(new Vector3f(+14f, 14f, +14f));

		// clear the lights from this lightbox so the lightbox itself doesn't
		// get affected by light:

		// lightBox.setLightCombineMode(LightState.OFF);

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
		tex[2].setTexture(TextureManager.loadTexture(
				("./data/flare/flare3.png"), Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR));
		tex[2].setEnabled(true);

		tex[3] = display.getRenderer().createTextureState();
		tex[3].setTexture(TextureManager.loadTexture(
				("./data/flare/flare4.png"), Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR));
		tex[3].setEnabled(true);

		flare = LensFlareFactory.createBasicLensFlare("flare", tex);
		flare.setRootNode(rootNode);
		lightNode.attachChild(flare);

		Quad q = new Quad("a", 12, 12);
		Quad q2 = new Quad("a", 12, 12);

		AlphaState as = display.getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);
		as.setEnabled(true);
		// alpha used for blending the lightmap
		AlphaState as2 = display.getRenderer().createAlphaState();
		as2.setBlendEnabled(true);
		as2.setSrcFunction(AlphaState.SB_DST_COLOR);
		as2.setDstFunction(AlphaState.DB_SRC_COLOR);
		as2.setTestEnabled(true);
		as2.setTestFunction(AlphaState.TF_GREATER);
		as2.setEnabled(true);

		// create some interesting texturestates for splatting
		TextureState ts1 = createSplatTextureState("jungle_atlas.png", null);

		/*
		 * TextureState ts2 = createSplatTextureState( "darkrock.jpg",
		 * "darkrockalpha.png");
		 * 
		 * TextureState ts3 = createSplatTextureState( "deadgrass.jpg",
		 * "deadalpha.png");
		 * 
		 * TextureState ts4 = createSplatTextureState( "nicegrass.jpg",
		 * "grassalpha.png");
		 */
		TextureState ts5 = createSplatTextureState("sand2.jpg",
				"blendAlphaOpp1.png");
		// TextureState ts6 =
		// createLightmapTextureState("./data/test/lightmap.jpg");

		PassNode splattingPassNode = new PassNode("SplatPassNode");
		Vector3f terrainScale = new Vector3f(155, 0.003f, 156);
		// heightMap.setHeightScale(0.001f);
		TerrainBlock page = new TerrainBlock("Terrain", 2, terrainScale,
				new int[2 * 2], new Vector3f(), false);
		page.getLocalTranslation().set(0, -9.5f, 0);
		page.setRenderState(ts1);

		FloatBuffer b = page.getTextureBuffers(0)[0];
		float position = 2;
		int atlas_size = 3;
		/*for (int i = 0; i < b.capacity(); i++) {
			if (i%2==1) continue;
			float f = b.get(i);
			System.out.println(f);
			b.put(i, (f / atlas_size)+ position/atlas_size);
		}*/

		// q.setRenderState(as);
		// page.setRenderState(as);
		// page.setDetailTexture(1, 1);

		// splattingPassNode.attachChild(page);

		PassNodeState passNodeState = new PassNodeState();

		passNodeState.setPassState(ts1);
		splattingPassNode.addPass(passNodeState);

		/*
		 * 
		 * passNodeState = new PassNodeState(); passNodeState.setPassState(ts2);
		 * passNodeState.setPassState(as);
		 * splattingPassNode.addPass(passNodeState);
		 * 
		 * passNodeState = new PassNodeState(); passNodeState.setPassState(ts3);
		 * passNodeState.setPassState(as);
		 * splattingPassNode.addPass(passNodeState);
		 * 
		 * passNodeState = new PassNodeState(); passNodeState.setPassState(ts4);
		 * passNodeState.setPassState(as);
		 * splattingPassNode.addPass(passNodeState);
		 */
		if (true == true) {
			passNodeState = new PassNodeState();
			passNodeState.setPassState(ts5);
			passNodeState.setPassState(as);
			splattingPassNode.addPass(passNodeState);
		}

		// splattingPassNode.set
		// passNodeState = new PassNodeState();
		// passNodeState.setPassState(ts6);
		// passNodeState.setPassState(as2);
		// splattingPassNode.addPass(passNodeState);
		// //////////////////// PASS STUFF END

		// lock some things to increase the performance
		// splattingPassNode.lockBounds();
		// splattingPassNode.lockTransforms();
		// splattingPassNode.lockShadows();

		// splatTerrain = splattingPassNode;

		page.updateRenderState();
		splattingPassNode.setLocalTranslation(new Vector3f(3f, 0, 0));
		// rootNode.attachChild(page);
		// rootNode.attachChild(splattingPassNode);

		// page.setLocalTranslation(new Vector3f(3f,3,0));
		// page.setRenderState(ts1);
		q.setRenderState(ts1);
		page.updateRenderState();
		q.updateRenderState();
		q2.getLocalTranslation().addLocal(new Vector3f(10,5,2));
		rootNode.attachChild(page);
		rootNode.attachChild(q);
		rootNode.attachChild(q2);

		rootNode.updateRenderState();
		rootNode.attachChild(lightNode);
		RenderPass rootPass = new RenderPass();
		rootPass.add(rootNode);
		pManager.add(rootPass);
		
		DepthOfFieldRenderPass dof = new DepthOfFieldRenderPass(cam,4);

		dof.setBlurSize(0.013f);
		dof.setNearBlurDepth(30f);
		dof.setFocalPlaneDepth(50f);
		dof.setFarBlurDepth(120f);
		dof.setRootSpatial(rootNode);
	
		pManager.add(dof);
		rootNode.attachChild(fpsNode);

		// notice that it comes at the end
		// lightNode.attachChild(flare);

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
		// Texture t0 = TextureCreator.newAlphaMaskTexture(data);
		Texture t0 = TextureManager.loadTexture(texture,
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		// t0.setWrap(Texture.WM_WRAP_S_WRAP_T);
		// t0.setApply(Texture.AM_MODULATE);
		t0.setScale(new Vector3f(10.1f,10f, 10.1f));
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
		// Texture t1 = TextureCreator.newAlphaMaskTexture(data);

		Texture t1 = TextureManager.loadTexture(alpha,
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		// t1.setScale(new Vector3f(1110.01f,1110.01f, 1110.01f));
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