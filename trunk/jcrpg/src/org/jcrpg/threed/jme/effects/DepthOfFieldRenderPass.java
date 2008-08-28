/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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

package org.jcrpg.threed.jme.effects;

import com.jme.image.Texture;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * GLSL Depth of Field effect pass. - Creating a depth texture - Use it on full screen texture downsampled
 * to blur it with stronger opacity on blured part - render result (unblended) on the screen overwriting
 * with the blured parts. 
 * 
 * @author Paul Illes - initial implementation of DepthOfFieldRenderPass for jME 1.0 based on partially MrCorder's 
 *	shaders plus : about original Ogre DoF demo: 
 *  "Depth of Field" demo for Ogre
 *  Copyright (C) 2006  Christian Lindequist Larsen
 *  This code is in the public domain. You may do whatever you want with it.
 *  - Used from that part the depth shader with some modifications. 
 *  
 * @author (MrCoder) - initial implementation of BloomRenderPass (original pass)
 * @author Joshua Slack - Enhancements and reworking to use a single
 *         texrenderer, ability to reuse existing back buffer, faster blur,
 *         throttling speed-up, etc.
 */
public class DepthOfFieldRenderPass extends Pass {
    private static final long serialVersionUID = 1L;

    private float throttle = 1/50f; 
    private float sinceLast = 1; 
    
    private TextureRenderer tRenderer;
	private Texture resultTexture;
	private Texture depthTexture;
    private Texture screenTexture;

    private Quad fullScreenQuad;
	private TriangleBatch fullScreenQuadBatch;

	private GLSLShaderObjectsState finalShader;
	private GLSLShaderObjectsState depthShader;
	private GLSLShaderObjectsState dofShader;

	
	private float blurSize;

	public float nearBlurDepth = 10f;
    public float focalPlaneDepth = 25f;
    public float farBlurDepth = 50f;
    /** blurriness cutoff constant for objects behind the focal plane */
    public float blurrinessCutoff = 1f;

	private boolean supported = true;

	public static String shaderDirectory = "org/jcrpg/threed/jme/effects/shader/";

	/**
	 * Reset bloom parameters to default
	 */
	public void resetParameters() {
		nearBlurDepth = 10f;
		focalPlaneDepth = 25f;
		farBlurDepth = 50f;
		blurrinessCutoff = 50f;
		blurSize = 0.013f;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
        super.cleanUp();
        if (tRenderer != null)
            tRenderer.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}
	int renderScaleP;
	/**
	 * Creates a new bloom renderpass
	 *
	 * @param cam		 Camera used for rendering the bloomsource
	 * @param renderScale Scale of bloom texture
	 */
	public DepthOfFieldRenderPass(Camera cam, int renderScale) {
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		renderScaleP = renderScale;
		resetParameters();

		//Create texture renderers and rendertextures(alternating between two not to overwrite pbuffers)
        tRenderer = display.createTextureRenderer(
                display.getWidth()/ renderScale, 
                display.getHeight()/ renderScale,
                TextureRenderer.RENDER_TEXTURE_2D);
		if (!tRenderer.isSupported()) {
			supported = false;
			return;
		}

        tRenderer.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        tRenderer.setCamera(cam);


        screenTexture = new Texture();
        screenTexture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
        tRenderer.setupTexture(screenTexture);
        
		resultTexture = new Texture();
		resultTexture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
		resultTexture.setFilter(Texture.FM_LINEAR);
        tRenderer.setupTexture(resultTexture);


        depthTexture = new Texture();
		depthTexture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
        tRenderer.setupTexture(depthTexture);


		//Create final shader(basic texturing)
		finalShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!finalShader.isSupported()) {
			supported = false;
			return;
		} else {
			finalShader.load(DepthOfFieldRenderPass.class.getClassLoader().getResource(shaderDirectory + "dof_fullscreen.vert"),
					DepthOfFieldRenderPass.class.getClassLoader().getResource(shaderDirectory + "dof_fullscreen.frag"));
			finalShader.setEnabled(true);
		}

		// DOF
		
		depthShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!depthShader.isSupported()) {
			supported = false;
			return;
		} else {
			depthShader.load(DepthOfFieldRenderPass.class.getClassLoader().getResource(shaderDirectory + "dof_1_depth.vert"),
					DepthOfFieldRenderPass.class.getClassLoader().getResource(shaderDirectory + "dof_1_depth.frag"));
			depthShader.setEnabled(true);
		}

		//Create dof shader
		dofShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!dofShader.isSupported()) {
			supported = false;
			return;
		} else {
			dofShader.load(
					DepthOfFieldRenderPass.class.getClassLoader().getResource(shaderDirectory + "dof_simple.vert"),
					DepthOfFieldRenderPass.class.getClassLoader().getResource(shaderDirectory + "dof_3_dof_2.frag"));
			dofShader.setEnabled(true);
		}
		
		//Create fullscreen quad
		fullScreenQuad = new Quad("FullScreenQuad", display.getWidth()/4, display.getHeight()/4);
        fullScreenQuadBatch = fullScreenQuad.getBatch(0);
		fullScreenQuad.getLocalRotation().set(0, 0, 0, 1);
		fullScreenQuad.getLocalTranslation().set(display.getWidth() / 2, display.getHeight() / 2, 0);
		fullScreenQuad.getLocalScale().set(1, 1, 1);
		fullScreenQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		fullScreenQuad.setCullMode(SceneElement.CULL_NEVER);
		fullScreenQuad.setTextureCombineMode(TextureState.REPLACE);
		fullScreenQuad.setLightCombineMode(LightState.OFF);
        
		TextureState ts = display.getRenderer().createTextureState();		
		ts.setEnabled(true);
        fullScreenQuadBatch.setRenderState(ts);

		AlphaState as = display.getRenderer().createAlphaState();
		// no blending, result texture has to overwrite screen - not blend!
	    as.setTestEnabled(true);
	    as.setTestFunction(AlphaState.TF_GREATER);
	    as.setEnabled(true);
		
        fullScreenQuadBatch.setRenderState(as);

        fullScreenQuad.updateRenderState();
        fullScreenQuad.updateGeometricState(0.0f, true);
		
	}
    

    @Override
    protected void doUpdate(float tpf) {
        super.doUpdate(tpf);
        sinceLast += tpf;
    }
 
	/** A place to internally save previous enforced states setup before rendering this pass */
	private RenderState[] preStates = new RenderState[RenderState.RS_MAX_STATE];

	/**
	 * saves any states enforced by the user for replacement at the end of the
	 * pass.
	 */
	protected void saveEnforcedStates() {
		for (int x = RenderState.RS_MAX_STATE; --x >= 0;) {
			preStates[x] = context.enforcedStateList[x];
		}
	}

	/**
	 * replaces any states enforced by the user at the end of the pass.
	 */
	protected void replaceEnforcedStates() {
		for (int x = RenderState.RS_MAX_STATE; --x >= 0;) {
			context.enforcedStateList[x] = preStates[x];
		}
	}
	
    private Spatial rootSpatial = null;


    public Spatial getRootSpatial() {
		return rootSpatial;
	}
    
    /**
     * Sets the scene's root spacial which is used to render depth texture.
     * @param rootSpatial
     */
    public void setRootSpatial(Spatial rootSpatial) {
		this.rootSpatial = rootSpatial;
	}
    

	public void doRender(Renderer r) {
        if (rootSpatial==null) {
            return;
        }

        AlphaState as = (AlphaState) fullScreenQuadBatch.states[RenderState.RS_ALPHA];
        TextureState ts = (TextureState) fullScreenQuadBatch.states[RenderState.RS_TEXTURE];

        if (throttle<sinceLast)
        {
        	sinceLast = 0;
        	as.setEnabled(false);
	        
	        Spatial s = rootSpatial;
	        
	        // rendering the screen
	        tRenderer.copyToTexture(screenTexture, 
	              DisplaySystem.getDisplaySystem().getWidth(), 
	            DisplaySystem.getDisplaySystem().getHeight() 
	                );
	        
	        // depth
	        context.enforceState(depthShader);
	        depthShader.setUniform("dofParams", nearBlurDepth, focalPlaneDepth, farBlurDepth, blurrinessCutoff);
	        depthShader.setUniform("mainTexture", 0);
	        tRenderer.render( s, depthTexture);  // depth texture
	        replaceEnforcedStates();
	
			// dof
			dofShader.clearUniforms();
			dofShader.setUniform("scene", 0);
			dofShader.setUniform("depth", 1);
			dofShader.setUniform("sampleDist0", getBlurSize());
			fullScreenQuadBatch.states[RenderState.RS_GLSL_SHADER_OBJECTS] = dofShader;
	        ts.setTexture(screenTexture, 0);
	        ts.setTexture(depthTexture,1);
	        fullScreenQuad.setRenderState(ts);
			tRenderer.render( fullScreenQuad , resultTexture);
			
			//ts.setTexture(resultTexture,0);
			ts.setTexture(resultTexture,0);
			ts.setTexture(null, 1);
        }

    	//Final blend
		as.setEnabled(true);
        
        fullScreenQuadBatch.states[RenderState.RS_GLSL_SHADER_OBJECTS] = finalShader;
        r.draw(fullScreenQuadBatch);
        
	}

	/**
     * @return The throttle amount - or in other words, how much time in
     *         seconds must pass before the bloom effect is updated.
     */
    public float getThrottle() {
        return throttle;
    }

    /**
     * @param throttle
     *            The throttle amount - or in other words, how much time in
     *            seconds must pass before the bloom effect is updated.
     */
    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }
    

    public float getBlurSize() {
		return blurSize;
	}

	public void setBlurSize(float blurSize) {
		this.blurSize = blurSize;
	}

    public float getNearBlurDepth() {
		return nearBlurDepth;
	}

	public void setNearBlurDepth(float nearBlurDepth) {
		this.nearBlurDepth = nearBlurDepth;
	}

	public float getFocalPlaneDepth() {
		return focalPlaneDepth;
	}

	public void setFocalPlaneDepth(float focalPlaneDepth) {
		this.focalPlaneDepth = focalPlaneDepth;
	}

	public float getFarBlurDepth() {
		return farBlurDepth;
	}

	public void setFarBlurDepth(float farBlurDepth) {
		this.farBlurDepth = farBlurDepth;
	}

	public float getBlurrinessCutoff() {
		return blurrinessCutoff;
	}

	public void setBlurrinessCutoff(float blurrinessCutoff) {
		this.blurrinessCutoff = blurrinessCutoff;
	}
}
