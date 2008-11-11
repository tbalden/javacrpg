/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcrpg.threed.J3DCore;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.image.Texture.WrapMode;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * <code>WaterRenderPass</code>
 * Water effect pass.
 *
 * @author Rikard Herlitz (MrCoder)
 * @version $Id: WaterRenderPass.java,v 1.18 2007/09/11 15:48:54 nca Exp $
 */
public class WaterRenderPass extends Pass {
    private static final Logger logger = Logger.getLogger(WaterRenderPass.class
            .getName());
    
    private static final long serialVersionUID = 1L;

    private Camera cam;
	private float tpf;
    private float reflectionThrottle = 1/50f, refractionThrottle = 1/50f;
    private float reflectionTime = 0, refractionTime = 0;
	private boolean useFadeToFogColor = false;

	private TextureRenderer tRenderer;
	private Texture2D textureReflect;
	private Texture2D textureRefract;
	private Texture2D textureDepth;

	private ArrayList<Spatial> renderList;
    private ArrayList<Texture> texArray = new ArrayList<Texture>();
	private Node skyBox;

	private GLSLShaderObjectsState waterShader;
	private CullState cullBackFace;
	private TextureState textureState;
	private BlendState as1;
	private ClipState clipState;
    private FogState noFog;

	private Plane waterPlane;
	private Vector3f tangent;
    private Vector3f binormal;
    private Vector3f calcVect = new Vector3f();
	private float clipBias;
	private ColorRGBA waterColorStart;
	private ColorRGBA waterColorEnd;
	private float heightFalloffStart;
	private float heightFalloffSpeed;
	private float waterMaxAmplitude;
	private float speedReflection;
	private float speedRefraction;

	private boolean aboveWater;
	private float normalTranslation = 0.0f;
	private float refractionTranslation = 0.0f;
	private boolean supported = true;
	private boolean useProjectedShader = false;
	private boolean useRefraction = false;
	private boolean useReflection = true;
	private int renderScale;

	public static String simpleShaderStr = "com/jmex/effects/water/data/flatwatershader";
	public static String simpleShaderRefractionStr = "com/jmex/effects/water/data/flatwatershader_refraction";
	public static String projectedShaderStr = "com/jmex/effects/water/data/projectedwatershader";
	public static String projectedShaderRefractionStr = "com/jmex/effects/water/data/projectedwatershader_refraction";
	private String currentShaderStr;

    public static String normalMapTexture = "com/jmex/effects/water/data/normalmap3.dds";
    public static String dudvMapTexture = "com/jmex/effects/water/data/dudvmap.png";
    public static String foamMapTexture = "com/jmex/effects/water/data/oceanfoam.png";
    public static String fallbackMapTexture = "com/jmex/effects/water/data/water2.png";

    /**
     * Resets water parameters to default values
     *
     */
    public void resetParameters() {
		waterPlane = new Plane( new Vector3f( 0.0f, 1.0f, 0.0f ), 0.0f );
		tangent = new Vector3f( 1.0f, 0.0f, 0.0f );
		binormal = new Vector3f( 0.0f, 0.0f, 1.0f );

		waterMaxAmplitude = 1.0f;
		clipBias = 0.0f;
		waterColorStart = new ColorRGBA( 0.0f, 0.0f, 0.1f, 1.0f );
		waterColorEnd = new ColorRGBA( 0.0f, 0.3f, 0.1f, 1.0f );
		heightFalloffStart = 300.0f;
		heightFalloffSpeed = 500.0f;
		speedReflection = 0.1f;
		speedRefraction = -0.05f;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		if( isSupported() )
			tRenderer.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}

	/**
	 * Creates a new WaterRenderPass
	 *
	 * @param cam				main rendercam to use for reflection settings etc
	 * @param renderScale		how many times smaller the reflection/refraction textures should be compared to the main display
	 * @param useProjectedShader true - use the projected setup for variable height water meshes, false - use the flast shader setup
	 * @param useRefraction	  enable/disable rendering of refraction textures
	 */
	public WaterRenderPass( Camera cam, int renderScale, boolean useProjectedShader, boolean useRefraction ) {
		this.cam = cam;
		this.useProjectedShader = useProjectedShader;
        this.useRefraction = useRefraction;
		this.renderScale = renderScale;
		resetParameters();
		initialize();
	}

	private void initialize() {
		if( useRefraction && useProjectedShader && TextureState.getNumberOfFragmentUnits() < 6 ||
			useRefraction && TextureState.getNumberOfFragmentUnits() < 5 ) {
			useRefraction = false;
			logger.info("Not enough textureunits, falling back to non refraction water");
		}

		DisplaySystem display = DisplaySystem.getDisplaySystem();

		if( J3DCore.SETTINGS.WATER_SHADER ) 
		{
			waterShader = display.getRenderer().createGLSLShaderObjectsState();
		}

		if( !J3DCore.SETTINGS.WATER_SHADER || !waterShader.isSupported() ) {
			supported = false;
		}
		else {
		}

		cullBackFace = display.getRenderer().createCullState();
		cullBackFace.setEnabled( true );
		cullBackFace.setCullFace( CullState.Face.None );
		clipState = display.getRenderer().createClipState();
		if( isSupported() ) {
			tRenderer = display.createTextureRenderer(
					    display.getWidth() / renderScale,
                        display.getHeight() / renderScale,
                        TextureRenderer.Target.Texture2D);

			if( tRenderer.isSupported() ) {
                tRenderer.setMultipleTargets(true);
				tRenderer.setBackgroundColor( new ColorRGBA( 0.0f, 0.0f, 0.0f, 1.0f ) );
				tRenderer.getCamera().setFrustum( cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom() );

				textureReflect = new Texture2D();
				textureReflect.setWrap( WrapMode.Clamp );
				textureReflect.setMagnificationFilter( Texture.MagnificationFilter.Bilinear );
				textureReflect.setScale( new Vector3f( -1.0f, 1.0f, 1.0f ) );
				textureReflect.setTranslation( new Vector3f( 1.0f, 0.0f, 0.0f ) );
				tRenderer.setupTexture( textureReflect );

				textureRefract = new Texture2D();
				textureRefract.setWrap( WrapMode.Clamp );
				textureReflect.setMagnificationFilter( Texture.MagnificationFilter.Bilinear );
				tRenderer.setupTexture( textureRefract );

				textureDepth = new Texture2D();
				textureDepth.setWrap( WrapMode.Clamp );
				textureReflect.setMagnificationFilter( Texture.MagnificationFilter.NearestNeighbor );
				textureDepth.setRenderToTextureType( Texture.RenderToTextureType.Depth );
				tRenderer.setupTexture( textureDepth );

				textureState = display.getRenderer().createTextureState();
				textureState.setEnabled( true );

				Texture t1 = TextureManager.loadTexture(
						WaterRenderPass.class.getClassLoader().getResource( normalMapTexture ),
						Texture.MinificationFilter.BilinearNoMipMaps,
						Texture.MagnificationFilter.Bilinear
				);
				textureState.setTexture( t1, 0 );
				t1.setWrap( Texture.WrapMode.Repeat );

				textureState.setTexture( textureReflect, 1 );

				t1 = TextureManager.loadTexture(
						WaterRenderPass.class.getClassLoader().getResource( dudvMapTexture ),
						Texture.MinificationFilter.BilinearNoMipMaps,
						Texture.MagnificationFilter.Bilinear, com.jme.image.Image.Format.GuessNoCompression, 1.0f, false
				);
				textureState.setTexture( t1, 2 );
				t1.setWrap( Texture.WrapMode.Repeat );

				if( useRefraction ) {
					textureState.setTexture( textureRefract, 3 );
					textureState.setTexture( textureDepth, 4 );
				}

				if( useProjectedShader ) {
					t1 = TextureManager.loadTexture(
							WaterRenderPass.class.getClassLoader().getResource( foamMapTexture ),
							Texture.MinificationFilter.BilinearNoMipMaps,
							Texture.MagnificationFilter.Bilinear );
					if( useRefraction ) {
						textureState.setTexture( t1, 5 );
					}
					else {
						textureState.setTexture( t1, 3 );
					}
					t1.setWrap( Texture.WrapMode.Repeat );
				}

				clipState.setEnabled( true );
				clipState.setEnableClipPlane( ClipState.CLIP_PLANE0, true );

				reloadShader();
			}
			else {
				supported = false;
			}
		}

		if( !isSupported() ) {
			textureState = display.getRenderer().createTextureState();
			textureState.setEnabled( true );

			Texture t1 = TextureManager.loadTexture(
					WaterRenderPass.class.getClassLoader().getResource( fallbackMapTexture ),
					Texture.MinificationFilter.BilinearNoMipMaps,
					Texture.MagnificationFilter.Bilinear );
			textureState.setTexture( t1, 0 );
			t1.setWrap( Texture.WrapMode.Repeat );

			as1 = display.getRenderer().createBlendState();
			as1.setBlendEnabled( true );
			as1.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
			as1.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
			as1.setEnabled( true );
		} else {
            noFog = display.getRenderer().createFogState();
            noFog.setEnabled(false);      
        }
	}

	@Override
	protected void doUpdate( float tpf ) {
		super.doUpdate( tpf );
		this.tpf = tpf;
	}
	
	public void setUseShader(boolean enabled)
	{
		supported = enabled;
	}


	public void doRender( Renderer r ) {
		normalTranslation += speedReflection * tpf;
		refractionTranslation += speedRefraction * tpf;

		float camWaterDist = waterPlane.pseudoDistance( cam.getLocation() );
		aboveWater = camWaterDist >= 0;

		if( isSupported() ) {
			waterShader.setUniform( "tangent", tangent.x, tangent.y, tangent.z );
			waterShader.setUniform( "binormal", binormal.x, binormal.y, binormal.z );
			waterShader.setUniform( "useFadeToFogColor", useFadeToFogColor );
			waterShader.setUniform( "waterColor", waterColorStart.r, waterColorStart.g, waterColorStart.b, waterColorStart.a );
			waterShader.setUniform( "waterColorEnd", waterColorEnd.r, waterColorEnd.g, waterColorEnd.b, waterColorEnd.a );
			waterShader.setUniform( "normalTranslation", normalTranslation );
			waterShader.setUniform( "refractionTranslation", refractionTranslation );
			waterShader.setUniform( "abovewater", aboveWater );
			if( useProjectedShader ) {
				waterShader.setUniform( "cameraPos", cam.getLocation().x, cam.getLocation().y, cam.getLocation().z );
				waterShader.setUniform( "waterHeight", waterPlane.getConstant() );
				waterShader.setUniform( "amplitude", waterMaxAmplitude );
				waterShader.setUniform( "heightFalloffStart", heightFalloffStart );
				waterShader.setUniform( "heightFalloffSpeed", heightFalloffSpeed );
			}

			float heightTotal = clipBias + waterMaxAmplitude - waterPlane.getConstant();
			Vector3f normal = waterPlane.getNormal();
			clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, normal.x, normal.y, normal.z, heightTotal );
			clipState.setEnabled( true );

			if( useReflection ) {
				renderReflection();
			}

			clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, -normal.x, -normal.y, -normal.z, -heightTotal );

			if( useRefraction && aboveWater ) {
				renderRefraction();
			}

			clipState.setEnabled( false );
		}
		else {
			textureState.getTexture().setTranslation( new Vector3f( 0, normalTranslation, 0 ) );
		}
	}

	public void reloadShader() {
		if( useProjectedShader ) {
			if( useRefraction ) {
				currentShaderStr = projectedShaderRefractionStr;
			}
			else {
				currentShaderStr = projectedShaderStr;
			}
		}
		else {
			if( useRefraction ) {
				currentShaderStr = simpleShaderRefractionStr;
			}
			else {
				currentShaderStr = simpleShaderStr;
			}
		}
		GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
		try {
			testShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
							 WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );
			testShader.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
		} catch( JmeException e ) {
            logger.log(Level.WARNING, "Error loading shader", e);
			return;
		}

		waterShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
						  WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );

        waterShader.setUniform( "normalMap", 0 );
        waterShader.setUniform( "reflection", 1 );
        waterShader.setUniform( "dudvMap", 2 );
        if( useRefraction ) {
            waterShader.setUniform( "refraction", 3 );
            waterShader.setUniform( "depthMap", 4 );
        }
        if( useProjectedShader ) {
            if( useRefraction ) {
                waterShader.setUniform( "foamMap", 5 );
            }
            else {
                waterShader.setUniform( "foamMap", 3 );
            }
        }

        logger.info("Shader reloaded...");
	}

    /**
     * Sets a spatial up for being rendered with the watereffect
     * @param spatial Spatial to use as base for the watereffect
     */
	public void setWaterEffectOnSpatial( Spatial spatial ) {
		spatial.setRenderState( cullBackFace );
		if( isSupported() ) {
			spatial.setRenderQueueMode( Renderer.QUEUE_SKIP );
			spatial.setRenderState( waterShader );
			spatial.setRenderState(textureState);
		}
		else {
			spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
			//spatial.setLightCombineMode( LightState.OFF );
			spatial.setRenderState(textureState);
			spatial.setRenderState( as1 );
		}
		spatial.updateRenderState();
	}

	//temporary vectors for mem opt.
	private Vector3f tmpLocation = new Vector3f();
	private Vector3f camReflectPos = new Vector3f();
	private Vector3f camReflectDir = new Vector3f();
	private Vector3f camReflectUp = new Vector3f();
	private Vector3f camReflectLeft = new Vector3f();
	private Vector3f camLocation = new Vector3f();

	private void renderReflection() {
	    reflectionTime += tpf;
        if (reflectionTime < reflectionThrottle) return;
        reflectionTime = 0;

		if( aboveWater ) {
			camLocation.set( cam.getLocation() );

			float planeDistance = waterPlane.pseudoDistance( camLocation );
            calcVect.set(waterPlane.getNormal()).multLocal( planeDistance * 2.0f );
			camReflectPos.set( camLocation.subtractLocal( calcVect ) );

			camLocation.set( cam.getLocation() ).addLocal( cam.getDirection() );
			planeDistance = waterPlane.pseudoDistance( camLocation );
            calcVect.set(waterPlane.getNormal()).multLocal( planeDistance * 2.0f );
			camReflectDir.set( camLocation.subtractLocal( calcVect ) ).subtractLocal( camReflectPos ).normalizeLocal();

			camLocation.set( cam.getLocation() ).addLocal( cam.getUp() );
			planeDistance = waterPlane.pseudoDistance( camLocation );
            calcVect.set(waterPlane.getNormal()).multLocal( planeDistance * 2.0f );
			camReflectUp.set( camLocation.subtractLocal( calcVect ) ).subtractLocal( camReflectPos ).normalizeLocal();

			camReflectLeft.set( camReflectDir ).crossLocal( camReflectUp ).normalizeLocal();

			tRenderer.getCamera().getLocation().set( camReflectPos );
			tRenderer.getCamera().getDirection().set( camReflectDir );
			tRenderer.getCamera().getUp().set( camReflectUp );
			tRenderer.getCamera().getLeft().set( camReflectLeft );
		}
		else {
			tRenderer.getCamera().getLocation().set( cam.getLocation() );
			tRenderer.getCamera().getDirection().set( cam.getDirection() );
			tRenderer.getCamera().getUp().set( cam.getUp() );
			tRenderer.getCamera().getLeft().set( cam.getLeft() );
		}

		if ( skyBox != null ) {
			tmpLocation.set( skyBox.getLocalTranslation() );
			skyBox.getLocalTranslation().set( tRenderer.getCamera().getLocation() );
			skyBox.updateWorldData( 0.0f );
		}

        texArray.clear();
        texArray.add(textureReflect);
        
        if (isUseFadeToFogColor()) {
            context.enforceState(noFog);
            tRenderer.render( renderList, texArray );
            context.clearEnforcedState(RenderState.RS_FOG);
        } else {
            tRenderer.render( renderList, texArray );
        }

		if ( skyBox != null ) {
			skyBox.getLocalTranslation().set( tmpLocation );
			skyBox.updateWorldData( 0.0f );
		}
	}

	private void renderRefraction() {
        refractionTime += tpf;
        if (refractionTime < refractionThrottle) return;
        refractionTime = 0;

        tRenderer.getCamera().getLocation().set( cam.getLocation() );
		tRenderer.getCamera().getDirection().set( cam.getDirection() );
		tRenderer.getCamera().getUp().set( cam.getUp() );
		tRenderer.getCamera().getLeft().set( cam.getLeft() );

		CullHint cullMode = CullHint.Inherit;
		if ( skyBox != null ) {
			cullMode = skyBox.getCullHint();
			skyBox.setCullHint( CullHint.Always );
		}

        texArray.clear();
        texArray.add(textureRefract);
        texArray.add(textureDepth);
        
        if (isUseFadeToFogColor()) {
            context.enforceState(noFog);
            tRenderer.render( renderList, texArray );
            context.clearEnforcedState(RenderState.RS_FOG);
        } else {
            tRenderer.render( renderList, texArray );
        }

		if ( skyBox != null ) {
			skyBox.setCullHint( cullMode );
		}
	}

	public void removeReflectedScene( Spatial renderNode ) {
		if(renderList != null) {
			logger.info("Removed reflected scene: " + renderList.remove(renderNode));
		}
	}
	
	public void clearReflectedScene() {
		if(renderList != null) {
			renderList.clear();
		}
	}
	
    /**
     * Sets spatial to be used as reflection in the water(clears previously set)
     * @param renderNode Spatial to use as reflection in the water
     */
	public void setReflectedScene( Spatial renderNode ) {
		if(renderList == null) {
			renderList = new ArrayList<Spatial>();
		}
		renderList.clear();
		renderList.add(renderNode);
		renderNode.setRenderState( clipState );
		renderNode.updateRenderState();
	}
    
	/**
     * Adds a spatial to the list of spatials used as reflection in the water
     * @param renderNode Spatial to add to the list of objects used as reflection in the water
	 */
	public void addReflectedScene( Spatial renderNode ) {
		if(renderList == null) {
			renderList = new ArrayList<Spatial>();
		}
		if(!renderList.contains(renderNode)) {
			renderList.add(renderNode);
			renderNode.setRenderState( clipState );
			renderNode.updateRenderState();
		}
	}

    /**
     * Sets up a node to be transformed and clipped for skybox usage
     * @param skyBox Handle to a node to use as skybox
     */
	public void setSkybox( Node skyBox ) {
		ClipState skyboxClipState = DisplaySystem.getDisplaySystem().getRenderer().createClipState();
		skyboxClipState.setEnabled( false );
		skyBox.setRenderState( skyboxClipState );
		skyBox.updateRenderState();

		this.skyBox = skyBox;
	}

	public Camera getCam() {
		return cam;
	}

	public void setCam( Camera cam ) {
		this.cam = cam;
	}

	public ColorRGBA getWaterColorStart() {
		return waterColorStart;
	}

    /** 
     * Color to use when the incident angle to the surface is low 
     */ 
	public void setWaterColorStart( ColorRGBA waterColorStart ) {
		this.waterColorStart = waterColorStart;
	}

	public ColorRGBA getWaterColorEnd() {
		return waterColorEnd;
	}

    /**
     * Color to use when the incident angle to the surface is high
     */
	public void setWaterColorEnd( ColorRGBA waterColorEnd ) {
		this.waterColorEnd = waterColorEnd;
	}

	public float getHeightFalloffStart() {
		return heightFalloffStart;
	}

    /**
     * Set at what distance the waveheights should start to fade out(for projected water only)
     * @param heightFalloffStart
     */
	public void setHeightFalloffStart( float heightFalloffStart ) {
		this.heightFalloffStart = heightFalloffStart;
	}

	public float getHeightFalloffSpeed() {
		return heightFalloffSpeed;
	}

    /**
     * Set the fadeout length of the waveheights, when over falloff start(for projected water only)
     * @param heightFalloffStart
     */
	public void setHeightFalloffSpeed( float heightFalloffSpeed ) {
		this.heightFalloffSpeed = heightFalloffSpeed;
	}

	public float getWaterHeight() {
		return waterPlane.getConstant();
	}

    /**
     * Set base height of the waterplane(Used for reflecting the camera for rendering reflection)
     * @param waterHeight Waterplane height
     */
	public void setWaterHeight( float waterHeight ) {
		this.waterPlane.setConstant( waterHeight );
	}

	public Vector3f getNormal() {
		return waterPlane.getNormal();
	}

    /**
     * Set the normal of the waterplane(Used for reflecting the camera for rendering reflection)
     * @param normal Waterplane normal
     */
	public void setNormal( Vector3f normal ) {
		waterPlane.setNormal( normal );
	}

	public float getSpeedReflection() {
		return speedReflection;
	}

    /**
     * Set the movement speed of the reflectiontexture
     * @param speedReflection Speed of reflectiontexture
     */
	public void setSpeedReflection( float speedReflection ) {
		this.speedReflection = speedReflection;
	}

	public float getSpeedRefraction() {
		return speedRefraction;
	}

    /**
     * Set the movement speed of the refractiontexture
     * @param speedRefraction Speed of refractiontexture
     */
	public void setSpeedRefraction( float speedRefraction ) {
		this.speedRefraction = speedRefraction;
	}

	public float getWaterMaxAmplitude() {
		return waterMaxAmplitude;
	}

    /**
     * Maximum amplitude of the water, used for clipping correctly(projected water only)
     * @param waterMaxAmplitude Maximum amplitude
     */
	public void setWaterMaxAmplitude( float waterMaxAmplitude ) {
		this.waterMaxAmplitude = waterMaxAmplitude;
	}

	public float getClipBias() {
		return clipBias;
	}

	public void setClipBias( float clipBias ) {
		this.clipBias = clipBias;
	}

	public Plane getWaterPlane() {
		return waterPlane;
	}

	public void setWaterPlane( Plane waterPlane ) {
		this.waterPlane = waterPlane;
	}

	public Vector3f getTangent() {
		return tangent;
	}

	public void setTangent( Vector3f tangent ) {
		this.tangent = tangent;
	}

	public Vector3f getBinormal() {
		return binormal;
	}

	public void setBinormal( Vector3f binormal ) {
		this.binormal = binormal;
	}

	public Texture getTextureReflect() {
		return textureReflect;
	}

	public Texture getTextureRefract() {
		return textureRefract;
	}

	public Texture getTextureDepth() {
		return textureDepth;
	}

    /**
     * If true, fade to fogcolor. If false, fade to 100% reflective surface
     * @param value
     */
    public void useFadeToFogColor(boolean value) {
        useFadeToFogColor = value;
    }

    public boolean isUseFadeToFogColor() {
        return useFadeToFogColor;
    }

	public boolean isUseReflection() {
		return useReflection;
	}

    /**
     * Turn reflection on and off
     * @param useReflection
     */
	public void setUseReflection(boolean useReflection) {
        if (useReflection == this.useReflection) return;
		this.useReflection = useReflection;
		reloadShader();
	}

	public boolean isUseRefraction() {
		return useRefraction;
	}

    /**
     * Turn refraction on and off
     * @param useRefraction
     */
	public void setUseRefraction(boolean useRefraction) {
        if (useRefraction == this.useRefraction) return;
		this.useRefraction = useRefraction;
		reloadShader();
	}

	public int getRenderScale() {
		return renderScale;
	}

	public void setRenderScale(int renderScale) {
		this.renderScale = renderScale;
	}

    public boolean isUseProjectedShader() {
        return useProjectedShader;
    }

    public void setUseProjectedShader(boolean useProjectedShader) {
        if (useProjectedShader == this.useProjectedShader) return;
        this.useProjectedShader = useProjectedShader;
        reloadShader();
    }

    public float getReflectionThrottle() {
        return reflectionThrottle;
    }

    public void setReflectionThrottle(float reflectionThrottle) {
        this.reflectionThrottle = reflectionThrottle;
    }

    public float getRefractionThrottle() {
        return refractionThrottle;
    }

    public void setRefractionThrottle(float refractionThrottle) {
        this.refractionThrottle = refractionThrottle;
    }

    public TextureState getTextureState() {
        return textureState;
    }

    public void setTextureState(TextureState textureState) {
        this.textureState = textureState;
    }
}