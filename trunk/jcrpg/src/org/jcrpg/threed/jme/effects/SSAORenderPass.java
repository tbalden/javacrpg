package org.jcrpg.threed.jme.effects;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.image.Texture.RenderToTextureType;
import com.jme.image.Texture.WrapMode;
import com.jme.math.Vector2f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;

/**
 * This RenderPass must be added after the normal renderpass of the scene for the effect to be applied correctly.
 * @author Haladria
 *
 */
public class SSAORenderPass extends Pass
{
//	private final TextureRenderer tRendererHalf; // texture renderer with half the screen size
	private final TextureRenderer tRenderer;
	
	private final ArrayList<Texture> textures; 
	private final ArrayList<Texture> texturesDepth; 
	
	private final Texture2D normalTexture;
	private final Texture2D depthTexture;
	private final Texture2D SSAOTexture;
	private final Texture2D SSAOBlurXTexture;
	private final Texture2D SSAOBlurYTexture;
	private final Texture noiseTexture;
	
	private final TextureState fsQuadTextureState; 
	
	private final  GLSLShaderObjectsState preRenderShader;
	private final  GLSLShaderObjectsState preRenderDepthShader;
	private final  GLSLShaderObjectsState SSAOShader;
	private final  GLSLShaderObjectsState blurXShader;
	private final  GLSLShaderObjectsState blurYShader;
	private final  GLSLShaderObjectsState texturingShader;
	
	private final Quad fullScreenQuad;
	
	private final int selectedTextureWidth;
	
	private final Camera cam; 
	
	private final BlendState blendState; 
	
	float blurFallOff = 0.003f, 
				blurSharpness = 10110.07f,
				blurRadius = 10.2f;
	
	float ssaoTotalStrength = 1.00f;
	float ssaoRayStrength = 0.07f;
	float ssaoOffset = 18.0f;
	float ssaoRadius = 0.007f;
	float ssaoFallOff =  0.00002f;
	
	private boolean shouldUpdateUniforms = false; 
	
	public SSAORenderPass(Camera cam, int renderScale)
	{
		this.cam = cam; 
		
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		selectedTextureWidth = display.getWidth()/renderScale;
		
		normalTexture = new Texture2D();
		normalTexture.setRenderToTextureType(RenderToTextureType.RGBA32F);
		normalTexture.setWrap(WrapMode.BorderClamp);
		normalTexture.setHasBorder(true);
		normalTexture.setBorderColor(new ColorRGBA(1.0f,1.0f,1.0f,1.0f));
		normalTexture.setMinificationFilter(Texture.MinificationFilter.NearestNeighborNoMipMaps);
		normalTexture.setMagnificationFilter(Texture.MagnificationFilter.NearestNeighbor);
		
		depthTexture = new Texture2D();
		depthTexture.setRenderToTextureType(RenderToTextureType.RGBA32F);
		depthTexture.setWrap(WrapMode.BorderClamp);
		depthTexture.setHasBorder(true);
		depthTexture.setBorderColor(new ColorRGBA(1.0f,1.0f,1.0f,1.0f));
		depthTexture.setMinificationFilter(Texture.MinificationFilter.NearestNeighborNoMipMaps);
		depthTexture.setMagnificationFilter(Texture.MagnificationFilter.NearestNeighbor);
		
		SSAOBlurXTexture = new Texture2D();
		SSAOBlurXTexture.setRenderToTextureType(RenderToTextureType.Luminance8);
		SSAOBlurXTexture.setWrap(WrapMode.BorderClamp);
		//SSAOBlurXTexture.setHasBorder(true); // not sure about this right now
		SSAOBlurXTexture.setBorderColor(ColorRGBA.white);
		SSAOBlurXTexture.setMinificationFilter(Texture.MinificationFilter.BilinearNearestMipMap);
		SSAOBlurXTexture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		
		SSAOBlurYTexture = new Texture2D();
		SSAOBlurYTexture.setRenderToTextureType(RenderToTextureType.Luminance8);
		SSAOBlurYTexture.setWrap(WrapMode.BorderClamp);
		//SSAOBlurYTexture.setHasBorder(true); // not sure about this right now
		SSAOBlurYTexture.setBorderColor(ColorRGBA.white);
		SSAOBlurYTexture.setMinificationFilter(Texture.MinificationFilter.BilinearNearestMipMap);
		SSAOBlurYTexture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		
		SSAOTexture = new Texture2D();
		SSAOTexture.setRenderToTextureType(RenderToTextureType.Luminance8);
		SSAOTexture.setWrap(WrapMode.BorderClamp);
		//SSAOTexture.setHasBorder(true); // not sure about this right now
		SSAOTexture.setBorderColor(ColorRGBA.white);
		SSAOTexture.setMinificationFilter(Texture.MinificationFilter.BilinearNearestMipMap);
		SSAOTexture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
		
		URL u = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "noise.dds");
    	
		// load the noise texture for the SSAO shader
		noiseTexture = TextureManager.loadTexture(
		        u,
		        Texture.MinificationFilter.NearestNeighborNoMipMaps,
		        Texture.MagnificationFilter.NearestNeighbor);
		noiseTexture.setWrap(WrapMode.Repeat);
		
		// load the shaders
		preRenderShader = createPreRenderShader(display);
		preRenderDepthShader = createPreRenderDepthShader(display);
	    blurXShader = createBlurXShader(display);
	    blurYShader = createBlurYShader(display);
	    SSAOShader = createSSAOShader(display);
	    texturingShader = createTexturingShader(display);
	    
		// Set up the texture renderer
		tRenderer = display.createTextureRenderer(selectedTextureWidth, selectedTextureWidth, TextureRenderer.Target.Texture2D);
		tRenderer.setBackgroundColor(new ColorRGBA(.0f, .0f, .0f, 0f));
		tRenderer.setMultipleTargets(false);
		tRenderer.setupTexture(normalTexture);
		tRenderer.setupTexture(depthTexture);
		tRenderer.setupTexture(SSAOTexture);
		tRenderer.setupTexture(SSAOBlurXTexture);
		tRenderer.setupTexture(SSAOBlurYTexture);
		tRenderer.setCamera(cam);
		
		// create the fullscreen quad
		fullScreenQuad = new Quad("FullScreenQuad", display.getWidth(), display.getHeight());
		fullScreenQuad.getLocalTranslation().set(display.getWidth() / 2, display.getHeight() / 2, 0);
		fullScreenQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		fullScreenQuad.setCullHint(Spatial.CullHint.Never);
		//fullScreenQuad.setTextureCombineMode(Spatial.TextureCombineMode.Replace);
		fullScreenQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
		
		// create the SSAO texture states
		fsQuadTextureState = display.getRenderer().createTextureState();
		fsQuadTextureState.setEnabled(true);

		fsQuadTextureState.setTexture(noiseTexture, 0);
		fsQuadTextureState.setTexture(normalTexture, 1);
		fsQuadTextureState.setTexture(depthTexture, 5);
		fsQuadTextureState.setTexture(SSAOTexture, 2);
		fsQuadTextureState.setTexture(SSAOBlurXTexture, 3);
	    fsQuadTextureState.setTexture(SSAOBlurYTexture, 4);
	    
	    if(fsQuadTextureState == null)
	    	System.out.println("Could not create TextureState in SSAORenderPass!");
	    
	    fullScreenQuad.setRenderState(fsQuadTextureState);

	    blendState = display.getRenderer().createBlendState();
	    blendState.setBlendEnabled(true);
	    blendState.setSourceFunction(BlendState.SourceFunction.Zero);
	    blendState.setDestinationFunction(BlendState.DestinationFunction.SourceColor);
	    blendState.setEnabled(true);
	    
	    ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled( true );
        buf.setFunction( ZBufferState.TestFunction.LessThanOrEqualTo );
        fullScreenQuad.setRenderState( buf );
		
        fullScreenQuad.updateGeometricState(0,true);
        fullScreenQuad.updateRenderState();
        
        textures = new ArrayList<Texture>(1);
        textures.add(normalTexture);
        texturesDepth = new ArrayList<Texture>(1);
        textures.add(depthTexture);
	}
	
	boolean doThing = true;
	
	@Override
	protected void doRender(Renderer r)
	{
		// first render the normal and depths
		
		if(shouldUpdateUniforms)
		{
			blurXShader.setUniform("g_BlurFalloff", blurFallOff);
			blurXShader.setUniform("g_Sharpness", blurSharpness);
			blurXShader.setUniform("g_BlurRadius", blurRadius);
			
			blurYShader.setUniform("g_BlurFalloff", blurFallOff);
			blurYShader.setUniform("g_Sharpness", blurSharpness);
			blurYShader.setUniform("g_BlurRadius", blurRadius);
			
			SSAOShader.setUniform("totStrength", ssaoTotalStrength);
			SSAOShader.setUniform("strength", ssaoRayStrength);
			SSAOShader.setUniform("offset", ssaoOffset);
			SSAOShader.setUniform("rad", ssaoRadius);
	        SSAOShader.setUniform("falloff", ssaoFallOff);
	        
	        shouldUpdateUniforms = false; 
		}
		
		// enforce the shader that will render the depth and the normals
		context.enforceState(preRenderShader);
		
		// render to texture
		tRenderer.render(spatials, textures,true);

		// restore context
		context.clearEnforcedState(RenderState.StateType.GLSLShaderObjects);
		
		
		// enforce the shader that will render the depth and the normals
		context.enforceState(preRenderDepthShader);
		
		// render to texture
		tRenderer.render(spatials, texturesDepth,true);

		// restore context
		context.clearEnforcedState(RenderState.StateType.GLSLShaderObjects);
		

		if (doThing){
		// second, render SSAO to SSAO texture
	    fullScreenQuad.setRenderState(SSAOShader);
	    fullScreenQuad.updateRenderState();
		
//	    tRendererHalf.render(fullScreenQuad, SSAOTexture,true);
	    tRenderer.render(fullScreenQuad, SSAOTexture,false);
		}
		// third, render a X-blur to SSAOBlurXTexture

	    fullScreenQuad.setRenderState(blurXShader);
	    fullScreenQuad.updateRenderState();
	    
	    tRenderer.render(fullScreenQuad, SSAOBlurXTexture,false);
	    
		// fourth, render a Y-blur to the screen (for now)
	    fullScreenQuad.setRenderState(blurYShader);
	    fullScreenQuad.updateRenderState();
	    
	    tRenderer.render(fullScreenQuad, SSAOBlurYTexture,false);
	    
	    
	    fullScreenQuad.setRenderState(texturingShader);
	    
	    
	    // apply the blend state so the result will be blended with the current scene
        fullScreenQuad.setRenderState(blendState); 
	    fullScreenQuad.updateRenderState();
	    
	    // we must manually draw this one
	    r.draw(fullScreenQuad);
	    r.renderQueue();
	    
	    // remove the blend state
	   fullScreenQuad.clearRenderState(RenderState.StateType.Blend);
	}

	
	public static String shaderDirectory2 = "org/jcrpg/threed/jme/effects/shader/";

	public static String shaderDirectory = "org/jcrpg/threed/jme/effects/shader/SSAO/";
	public float nearBlurDepth = 10f;
    public float focalPlaneDepth = 25f;
    public float farBlurDepth = 50f;
    /** blurriness cutoff constant for objects behind the focal plane */
    public float blurrinessCutoff = 1f;

	private GLSLShaderObjectsState createPreRenderShader(DisplaySystem display) 
    {
        GLSLShaderObjectsState so = display.getRenderer().createGLSLShaderObjectsState();

        try 
        {
            so .load(
    				SSAORenderPass.class
                            .getClassLoader()
                            .getResource(
                            		shaderDirectory+"PreRender_vp.glsl"),
                    SSAORenderPass.class
                            .getClassLoader()
                            .getResource(
                            		shaderDirectory+"PreRender_fp.glsl"));
            so.apply();
        /*   so .load(
            				SSAORenderPass.class
                                    .getClassLoader()
                                    .getResource(
                                    		shaderDirectory2+"dof_1_depth.vert"),//"PreRender_vp.glsl"),
                            SSAORenderPass.class
                                    .getClassLoader()
                                    .getResource(
                                    		shaderDirectory2+"dof_1_depth.frag"));//"PreRender_fp.glsl"));
            so.apply();*/
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) 
        {
        	System.out.println(Level.WARNING + "Error loading shader" + e);
        }

        so.setUniform("zFar", 40);//cam.getFrustumFar());
        
        so.setUniform("dofParams", nearBlurDepth, focalPlaneDepth, farBlurDepth, blurrinessCutoff);
        so.setUniform("mainTexture", 0);       
        
        so.setEnabled(true);

        return so;
    }
	private GLSLShaderObjectsState createPreRenderDepthShader(DisplaySystem display) 
    {
        GLSLShaderObjectsState so = display.getRenderer().createGLSLShaderObjectsState();

        try 
        {
            so .load(
    				SSAORenderPass.class
                            .getClassLoader()
                            .getResource(
                            		shaderDirectory+"PreRenderDepth_vp.glsl"),
                    SSAORenderPass.class
                            .getClassLoader()
                            .getResource(
                            		shaderDirectory+"PreRenderDepth_fp.glsl"));
            so.apply();
        /*   so .load(
            				SSAORenderPass.class
                                    .getClassLoader()
                                    .getResource(
                                    		shaderDirectory2+"dof_1_depth.vert"),//"PreRender_vp.glsl"),
                            SSAORenderPass.class
                                    .getClassLoader()
                                    .getResource(
                                    		shaderDirectory2+"dof_1_depth.frag"));//"PreRender_fp.glsl"));
            so.apply();*/
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) 
        {
        	System.out.println(Level.WARNING + "Error loading shader" + e);
        }

        so.setUniform("zFar", cam.getFrustumFar());
        
        so.setUniform("dofParams", nearBlurDepth, focalPlaneDepth, farBlurDepth, blurrinessCutoff);
        so.setUniform("mainTexture", 0);       
        
        so.setEnabled(true);

        return so;
    }
	
	private GLSLShaderObjectsState createTexturingShader(DisplaySystem display) 
    {
        GLSLShaderObjectsState so = display.getRenderer().createGLSLShaderObjectsState();

        try 
        {
            so.load(SSAORenderPass.class.getClassLoader()
					.getResource(shaderDirectory+"Texturing_vp.glsl"),
					SSAORenderPass.class.getClassLoader()
							.getResource(shaderDirectory+"Texturing_fp.glsl"));
			so.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) 
        {
        	System.out.println(Level.WARNING + "Error loading shader" + e);
        }
        
        so.setUniform("texture", doThing?4:1);//1); //4
        
        so.setEnabled(true);

        return so;
    }
	
    private GLSLShaderObjectsState createSSAOShader(DisplaySystem display) 
    {
        GLSLShaderObjectsState so = display.getRenderer().createGLSLShaderObjectsState();

        try 
        {
            so.load(SSAORenderPass.class.getClassLoader()
					.getResource(shaderDirectory+"SSAO_vp.glsl"),
					SSAORenderPass.class.getClassLoader()
							.getResource(shaderDirectory+"SSAO_fp.glsl"));
			so.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) 
        {
        	System.out.println(Level.WARNING + "Error loading shader" + e);
        }

        so.setUniform("rnm", 0);
        so.setUniform("normalMap", 1);
        so.setUniform("depthMap", 5);
               
        so.setUniform("totStrength", ssaoTotalStrength);
        so.setUniform("strength", ssaoRayStrength);
        so.setUniform("offset", ssaoOffset);
        so.setUniform("rad", ssaoRadius); // 0.007
        so.setUniform("falloff", ssaoFallOff); // 0.00002f
        
        so.setEnabled(true);

        return so;
    }
    
    private GLSLShaderObjectsState createBlurXShader(DisplaySystem display) 
    {
        GLSLShaderObjectsState so = display.getRenderer().createGLSLShaderObjectsState();

        try 
        {
            so.load(SSAORenderPass.class.getClassLoader()
					.getResource(shaderDirectory+"BlurX_vp.glsl"),
					SSAORenderPass.class.getClassLoader()
							.getResource(shaderDirectory+"BlurX_fp.glsl"));
			so.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) 
        {
        	System.out.println(Level.WARNING + "Error loading shader" + e);
        }

        so.setUniform("g_BlurFalloff", blurFallOff);
        so.setUniform("g_Sharpness", blurSharpness);
        so.setUniform("g_BlurRadius", blurRadius);
        so.setUniform("g_InvResolutionFull", new Vector2f(1.0f/(float)selectedTextureWidth,1.0f/(float)selectedTextureWidth));

        so.setUniform("AOMap", 2);
        so.setUniform("normalMap", 1);
        
        so.setEnabled(true);

        return so;
    }
    
    private GLSLShaderObjectsState createBlurYShader(DisplaySystem display) 
    {
        GLSLShaderObjectsState so = display.getRenderer().createGLSLShaderObjectsState();

        try 
        {
            so.load(SSAORenderPass.class.getClassLoader()
					.getResource(shaderDirectory+"BlurY_vp.glsl"),
					SSAORenderPass.class.getClassLoader()
							.getResource(shaderDirectory+"BlurY_fp.glsl"));
			so.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) 
        {
        	System.out.println(Level.WARNING + "Error loading shader" + e);
        }

        so.setUniform("g_BlurFalloff", blurFallOff);
        so.setUniform("g_Sharpness", blurSharpness);
        so.setUniform("g_BlurRadius", blurRadius);
        so.setUniform("g_InvResolutionFull", new Vector2f(1.0f/((float)selectedTextureWidth),1.0f/((float)selectedTextureWidth)));
        so.setUniform("normalMap", 1);
        so.setUniform("AOBlurXMap", 3);
        
        so.setEnabled(true);

        return so;
    }
    
    public void cleanup()
    {
    	tRenderer.cleanup();
//    	tRendererHalf.cleanup();
    }
    
    /**
     * @return
     */
    public boolean isSupported()
    {
    	return GLSLShaderObjectsState.isSupported() && tRenderer.isSupported();
    }

	/**
	 * @return the blurFallOff
	 */
	public float getBlurFallOff()
	{
		return blurFallOff;
	}

	/**
	 * @param blurFallOff the blurFallOff to set
	 */
	public void setBlurFallOff(float blurFallOff)
	{
		this.blurFallOff = blurFallOff;
		shouldUpdateUniforms = true; 
	}

	/**
	 * @return the blurSharpness
	 */
	public float getBlurSharpness()
	{
		return blurSharpness;
	}

	/**
	 * @param blurSharpness the blurSharpness to set
	 */
	public void setBlurSharpness(float blurSharpness)
	{
		this.blurSharpness = blurSharpness;
		shouldUpdateUniforms = true; 
	}

	/**
	 * @return the blurRadius
	 */
	public float getBlurRadius()
	{
		return blurRadius;
	}

	/**
	 * @param blurRadius the blurRadius to set
	 */
	public void setBlurRadius(float blurRadius)
	{
		this.blurRadius = blurRadius;
		shouldUpdateUniforms = true; 
	}

	/**
	 * @return the ssaoTotalStrength
	 */
	public float getSsaoTotalStrength()
	{
		return ssaoTotalStrength;
	}

	/**
	 * @param ssaoTotalStrength the ssaoTotalStrength to set
	 */
	public void setSsaoTotalStrength(float ssaoTotalStrength)
	{
		this.ssaoTotalStrength = ssaoTotalStrength;
		shouldUpdateUniforms = true; 
	}

	/**
	 * @return the ssaoRayStrength
	 */
	public float getSsaoRayStrength()
	{
		return ssaoRayStrength;
	}

	/**
	 * @param ssaoRayStrength the ssaoRayStrength to set
	 */
	public void setSsaoRayStrength(float ssaoRayStrength)
	{
		this.ssaoRayStrength = ssaoRayStrength;
		shouldUpdateUniforms = true; 
	}

	/**
	 * @return the ssaoOffset
	 */
	public float getSsaoOffset()
	{
		return ssaoOffset;
	}

	/**
	 * @param ssaoOffset the ssaoOffset to set
	 */
	public void setSsaoOffset(float ssaoOffset)
	{
		this.ssaoOffset = ssaoOffset;
		shouldUpdateUniforms = true; 
	}

	/**
	 * @return the ssaoRadius
	 */
	public float getSsaoRadius()
	{
		return ssaoRadius;
	}

	/**
	 * @param ssaoRadius the ssaoRadius to set
	 */
	public void setSsaoRadius(float ssaoRadius)
	{
		this.ssaoRadius = ssaoRadius;
		shouldUpdateUniforms = true; 
	}

	/**
	 * @return the ssaoFallOff
	 */
	public float getSsaoFallOff()
	{
		return ssaoFallOff;
	}

	/**
	 * @param ssaoFallOff the ssaoFallOff to set
	 */
	public void setSsaoFallOff(float ssaoFallOff)
	{
		this.ssaoFallOff = ssaoFallOff;
		shouldUpdateUniforms = true; 
	}
	
	
}
