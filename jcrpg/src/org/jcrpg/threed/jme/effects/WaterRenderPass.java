/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.threed.jme.effects;

import java.util.ArrayList;
import java.util.logging.Level;

import org.jcrpg.threed.J3DCore;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;

import com.jme.image.Texture;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * <code>WaterRenderPass</code>
 * Water effect pass.
 *
 * @author Rikard Herlitz (MrCoder)
 * @version $Id: WaterRenderPass.java,v 1.11 2007/04/17 20:41:44 rherlitz Exp $
 */
public class WaterRenderPass extends Pass {
    private static final long serialVersionUID = 1L;

    private Camera cam;
	private float tpf;
    private float reflectionThrottle = 1/50f, refractionThrottle = 1/50f;
    private float reflectionTime = 0, refractionTime = 0;
	private boolean useFadeToFogColor = false;

	private TextureRenderer tRenderer;
	private Texture textureReflect;
	private Texture textureRefract;
	private Texture textureDepth;

	private ArrayList<Spatial> renderList;
    private ArrayList<Texture> texArray = new ArrayList<Texture>();
	private Node skyBox;

	private GLSLShaderObjectsState waterShader;
	private CullState cullBackFace;
	private TextureState textureState;
	private AlphaState as1;
	private ClipState clipState;

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
		}

		DisplaySystem display = DisplaySystem.getDisplaySystem();

		waterShader = display.getRenderer().createGLSLShaderObjectsState();

		if( !J3DCore.WATER_SHADER || !waterShader.isSupported() ) {
			supported = false;
		}
		else {
		}

		cullBackFace = display.getRenderer().createCullState();
		cullBackFace.setEnabled( true );
		cullBackFace.setCullMode( CullState.CS_NONE );
		clipState = display.getRenderer().createClipState();
		if( isSupported() ) {
			tRenderer = display.createTextureRenderer(
					    display.getWidth() / renderScale,
                        display.getHeight() / renderScale,
                        TextureRenderer.RENDER_TEXTURE_2D);
			if( tRenderer.isSupported() ) {
				tRenderer.setBackgroundColor( new ColorRGBA( 0.0f, 0.0f, 0.0f, 1.0f ) );
				tRenderer.getCamera().setFrustum( cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom() );

				textureReflect = new Texture();
				textureReflect.setWrap( Texture.WM_ECLAMP_S_ECLAMP_T );
				textureReflect.setFilter( Texture.FM_LINEAR );
				textureReflect.setScale( new Vector3f( -1.0f, 1.0f, 1.0f ) );
				textureReflect.setTranslation( new Vector3f( 1.0f, 0.0f, 0.0f ) );
				tRenderer.setupTexture( textureReflect );

				textureRefract = new Texture();
				textureRefract.setWrap( Texture.WM_ECLAMP_S_ECLAMP_T );
				textureRefract.setFilter( Texture.FM_LINEAR );
				tRenderer.setupTexture( textureRefract );

				textureDepth = new Texture();
				textureDepth.setWrap( Texture.WM_ECLAMP_S_ECLAMP_T );
				textureDepth.setFilter( Texture.FM_LINEAR );
				textureDepth.setRTTSource( Texture.RTT_SOURCE_DEPTH );
				tRenderer.setupTexture( textureDepth );

				textureState = display.getRenderer().createTextureState();
				textureState.setEnabled( true );

				Texture t1 = TextureManager.loadTexture(
						WaterRenderPass.class.getClassLoader().getResource( normalMapTexture ),
						Texture.MM_LINEAR_LINEAR,
						Texture.FM_LINEAR
				);
				textureState.setTexture( t1, 0 );
				t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

				textureState.setTexture( textureReflect, 1 );

				t1 = TextureManager.loadTexture(
						WaterRenderPass.class.getClassLoader().getResource( dudvMapTexture ),
						Texture.MM_LINEAR_LINEAR,
						Texture.FM_LINEAR, com.jme.image.Image.GUESS_FORMAT_NO_S3TC, 1.0f, false
				);
				textureState.setTexture( t1, 2 );
				t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

				if( useRefraction ) {
					textureState.setTexture( textureRefract, 3 );
					textureState.setTexture( textureDepth, 4 );
				}

				if( useProjectedShader ) {
					t1 = TextureManager.loadTexture(
							WaterRenderPass.class.getClassLoader().getResource( foamMapTexture ),
							Texture.MM_LINEAR_LINEAR,
							Texture.FM_LINEAR );
					if( useRefraction ) {
						textureState.setTexture( t1, 5 );
					}
					else {
						textureState.setTexture( t1, 3 );
					}
					t1.setWrap( Texture.WM_WRAP_S_WRAP_T );
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
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			textureState.setTexture( t1, 0 );
			t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

			as1 = display.getRenderer().createAlphaState();
			as1.setBlendEnabled( true );
			as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
			as1.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
			as1.setEnabled( true );
		}
	}

	@Override
	protected void doUpdate( float tpf ) {
		super.doUpdate( tpf );
		this.tpf = tpf;
	}


	public void doRender( Renderer r ) {
		normalTranslation += speedReflection * tpf;
		refractionTranslation += speedRefraction * tpf;

		float camWaterDist = waterPlane.pseudoDistance( cam.getLocation() );
		aboveWater = camWaterDist >= 0;

		if( isSupported() ) {
			waterShader.clearUniforms();
			waterShader.setUniform( "tangent", tangent.x, tangent.y, tangent.z );
			waterShader.setUniform( "binormal", binormal.x, binormal.y, binormal.z );
			waterShader.setUniform( "useFadeToFogColor", useFadeToFogColor ? 1 : 0);

			waterShader.setUniform( "normalMap", 0 );
			waterShader.setUniform( "reflection", 1 );
			waterShader.setUniform( "dudvMap", 2 );
			if( useRefraction ) {
				waterShader.setUniform( "refraction", 3 );
				waterShader.setUniform( "depthMap", 4 );
			}

			waterShader.setUniform( "waterColor", waterColorStart.r, waterColorStart.g, waterColorStart.b, waterColorStart.a );
			waterShader.setUniform( "waterColorEnd", waterColorEnd.r, waterColorEnd.g, waterColorEnd.b, waterColorEnd.a );
			waterShader.setUniform( "normalTranslation", normalTranslation );
			waterShader.setUniform( "refractionTranslation", refractionTranslation );
			waterShader.setUniform( "abovewater", aboveWater ? 1 : 0 );
			if( useProjectedShader ) {
				waterShader.setUniform( "cameraPos", cam.getLocation().x, cam.getLocation().y, cam.getLocation().z );
				if( useRefraction ) {
					waterShader.setUniform( "foamMap", 5 );
				}
				else {
					waterShader.setUniform( "foamMap", 3 );
				}
				waterShader.setUniform( "waterHeight", waterPlane.getConstant() );
				waterShader.setUniform( "amplitude", waterMaxAmplitude );
				waterShader.setUniform( "heightFalloffStart", heightFalloffStart );
				waterShader.setUniform( "heightFalloffSpeed", heightFalloffSpeed );
			}
			waterShader.apply();

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
			Util.checkGLError();
		} catch( OpenGLException e ) {
			e.printStackTrace();
			return;
		}

		waterShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
						  WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );

	}

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
		tRenderer.render( renderList, texArray );

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

		int cullMode = 0;
		if ( skyBox != null ) {
			cullMode = skyBox.getCullMode();
			skyBox.setCullMode( SceneElement.CULL_ALWAYS );
		}

        texArray.clear();
        texArray.add(textureRefract);
        texArray.add(textureDepth);
		tRenderer.render( renderList, texArray );

		if ( skyBox != null ) {
			skyBox.setCullMode( cullMode );
		}
	}

	public void removeReflectedScene( Spatial renderNode ) {
		if(renderList != null) {
			System.out.println(renderList.remove(renderNode));
		}
	}
	
	public void clearReflectedScene() {
		if(renderList != null) {
			renderList.clear();
		}
	}
	
	public void setReflectedScene( Spatial renderNode ) {
		if(renderList == null) {
			renderList = new ArrayList<Spatial>();
		}
		renderList.clear();
		renderList.add(renderNode);
		renderNode.setRenderState( clipState );
		renderNode.updateRenderState();
	}
	
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

	public void setWaterColorStart( ColorRGBA waterColorStart ) {
		this.waterColorStart = waterColorStart;
	}

	public ColorRGBA getWaterColorEnd() {
		return waterColorEnd;
	}

	public void setWaterColorEnd( ColorRGBA waterColorEnd ) {
		this.waterColorEnd = waterColorEnd;
	}

	public float getHeightFalloffStart() {
		return heightFalloffStart;
	}

	public void setHeightFalloffStart( float heightFalloffStart ) {
		this.heightFalloffStart = heightFalloffStart;
	}

	public float getHeightFalloffSpeed() {
		return heightFalloffSpeed;
	}

	public void setHeightFalloffSpeed( float heightFalloffSpeed ) {
		this.heightFalloffSpeed = heightFalloffSpeed;
	}

	public float getWaterHeight() {
		return waterPlane.getConstant();
	}

	public void setWaterHeight( float waterHeight ) {
		this.waterPlane.setConstant( waterHeight );
	}

	public Vector3f getNormal() {
		return waterPlane.getNormal();
	}

	public void setNormal( Vector3f normal ) {
		waterPlane.setNormal( normal );
	}

	public float getSpeedReflection() {
		return speedReflection;
	}

	public void setSpeedReflection( float speedReflection ) {
		this.speedReflection = speedReflection;
	}

	public float getSpeedRefraction() {
		return speedRefraction;
	}

	public void setSpeedRefraction( float speedRefraction ) {
		this.speedRefraction = speedRefraction;
	}

	public float getWaterMaxAmplitude() {
		return waterMaxAmplitude;
	}

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

    public void useFadeToFogColor(boolean value) {
        useFadeToFogColor = value;
    }

    public boolean isUseFadeToFogColor() {
        return useFadeToFogColor;
    }

	public boolean isUseReflection() {
		return useReflection;
	}

	public void setUseReflection(boolean useReflection) {
        if (useReflection == this.useReflection) return;
		this.useReflection = useReflection;
		reloadShader();
	}

	public boolean isUseRefraction() {
		return useRefraction;
	}

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