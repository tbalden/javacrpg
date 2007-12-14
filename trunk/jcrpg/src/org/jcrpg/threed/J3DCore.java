/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.threed;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.input.ClassicInputHandler;
import org.jcrpg.threed.jme.GeometryBatchHelper;
import org.jcrpg.threed.jme.TrimeshGeometryBatch;
import org.jcrpg.threed.jme.effects.WaterRenderPass;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.LODModel;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.jcrpg.threed.scene.side.RenderedContinuousSide;
import org.jcrpg.threed.scene.side.RenderedHashAlteredSide;
import org.jcrpg.threed.scene.side.RenderedHashRotatedSide;
import org.jcrpg.threed.scene.side.RenderedSide;
import org.jcrpg.threed.scene.side.RenderedTopSide;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.Map;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.ground.Sand;
import org.jcrpg.world.ai.flora.ground.Snow;
import org.jcrpg.world.ai.flora.middle.deciduous.GreenBush;
import org.jcrpg.world.ai.flora.middle.succulent.GreenFern;
import org.jcrpg.world.ai.flora.middle.succulent.JungleBush;
import org.jcrpg.world.ai.flora.tree.cactus.BigCactus;
import org.jcrpg.world.ai.flora.tree.deciduous.Acacia;
import org.jcrpg.world.ai.flora.tree.deciduous.CherryTree;
import org.jcrpg.world.ai.flora.tree.deciduous.OakTree;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
import org.jcrpg.world.ai.flora.tree.palm.JunglePalmTrees;
import org.jcrpg.world.ai.flora.tree.pine.GreatPineTree;
import org.jcrpg.world.ai.flora.tree.pine.GreenPineTree;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.MountainNew;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.orbiter.Orbiter;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.place.water.Lake;
import org.jcrpg.world.place.water.River;
import org.jcrpg.world.time.Time;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.light.DirectionalLight;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.BillboardNode;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;
import com.jmex.effects.glsl.BloomRenderPass;

public class J3DCore extends com.jme.app.BaseSimpleGame implements Runnable {

    HashMap<String,Integer> hmAreaSubType3dType = new HashMap<String,Integer>();

    HashMap<Integer,RenderedSide> hm3dTypeRenderedSide = new HashMap<Integer,RenderedSide>();
    
    /**
     * This is the maximum view distance with far enabled * 2,
     * so that you cannot see the world's cubes twice from one viewpoint. :)
     */
    public static final int MINIMUM_WORLD_REALSIZE = 100;
    
	/**
	 * rendered cubes in each direction (N,S,E,W,T,B).
	 */
    public static int RENDER_DISTANCE_ORIG = 10;
    public static int RENDER_DISTANCE = 10;
    public static int VIEW_DISTANCE = 10;
    public static int VIEW_DISTANCE_SQR = 100;
    public static int VIEW_DISTANCE_FRAG_SQR = 20;
    public static int RENDER_GRASS_DISTANCE = 10;
    public static int RENDER_SHADOW_DISTANCE = 10;
    public static int RENDER_SHADOW_DISTANCE_SQR = 100;
    public static int ANTIALIAS_SAMPLES = 0;

	public static final float CUBE_EDGE_SIZE = 1.9999f; 
	
	public static final int MOVE_STEPS = 16;
	public static long TIME_TO_ENSURE = 16; 

    public static Integer EMPTY_SIDE = new Integer(0);
    
    public static boolean OPTIMIZED_RENDERING = false;
    
    public static boolean MIPMAP_TREES = false;
    
    public static boolean MIPMAP_GLOBAL = true;

    public static int TEXTURE_QUALITY = 0;

    public static boolean BLOOM_EFFECT = false;
    public static boolean SHADOWS = true;

    public static boolean ANIMATED_GRASS = true;
    public static boolean DOUBLE_GRASS = true;

    public static boolean ANIMATED_TREES = true;
    public static boolean DETAILED_TREES = true;
    public static boolean LOD_VEGETATION = false;
    public static boolean BUMPED_GROUND = false;
    public static boolean WATER_SHADER = false;
    public static boolean WATER_DETAILED = false;
    
    public static int FARVIEW_GAP = 1;
    public static boolean FARVIEW_ENABLED = false;

    static Properties p = new Properties();
    static {
    	try {
    		File f = new File("./config.properties");
	    	FileInputStream fis = new FileInputStream(f);
	    	p.load(fis);
	    	
	    	String farViewEnabled = p.getProperty("FARVIEW_ENABLED");
	    	if (farViewEnabled!=null)
	    	{
	    		farViewEnabled = farViewEnabled.trim();
	    		try {
	    			FARVIEW_ENABLED = Boolean.parseBoolean(farViewEnabled);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("FARVIEW_ENABLED", "false");
	    		}
	    	}
	    	
	    	String renderDistance = p.getProperty("RENDER_DISTANCE");
	    	if (renderDistance!=null)
	    	{
	    		renderDistance = renderDistance.trim();
	    		try {
	    			RENDER_DISTANCE_ORIG = Integer.parseInt(renderDistance);
	    			RENDER_DISTANCE = (int)(RENDER_DISTANCE_ORIG/CUBE_EDGE_SIZE);
	    			//if (RENDER_DISTANCE>15) RENDER_DISTANCE = 15;
	    			if (RENDER_DISTANCE<5) RENDER_DISTANCE = 5;
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("RENDER_DISTANCE", "10");
	    		}
	    	}
	    	String viewDistance = p.getProperty("VIEW_DISTANCE");
	    	if (viewDistance!=null)
	    	{
	    		viewDistance = viewDistance.trim();
	    		try {
	    			VIEW_DISTANCE = Integer.parseInt(viewDistance);
	    			//if (RENDER_DISTANCE>15) RENDER_DISTANCE = 15;
	    			if (VIEW_DISTANCE<5) VIEW_DISTANCE = 5;
	    			VIEW_DISTANCE_SQR = VIEW_DISTANCE*VIEW_DISTANCE;
	    			VIEW_DISTANCE_FRAG_SQR = VIEW_DISTANCE_SQR/4;
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("VIEW_DISTANCE", "10");
	    		}
	    	}
	    	String renderGrassDistance = p.getProperty("RENDER_GRASS_DISTANCE");
	    	if (renderGrassDistance!=null)
	    	{
	    		renderGrassDistance = renderGrassDistance.trim();
	    		try {
	    			RENDER_GRASS_DISTANCE = Integer.parseInt(renderGrassDistance);
	    			if (RENDER_GRASS_DISTANCE>15*CUBE_EDGE_SIZE) RENDER_GRASS_DISTANCE = (int)(15*CUBE_EDGE_SIZE);
	    			if (RENDER_GRASS_DISTANCE>RENDER_DISTANCE*CUBE_EDGE_SIZE) RENDER_GRASS_DISTANCE = (int)(RENDER_DISTANCE*CUBE_EDGE_SIZE);
	    			if (RENDER_GRASS_DISTANCE<0) RENDER_GRASS_DISTANCE = 0;
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("RENDER_GRASS_DISTANCE", "10");
	    		}
	    	}
	    	String renderShadowDistance = p.getProperty("RENDER_SHADOW_DISTANCE");
	    	if (renderShadowDistance!=null)
	    	{
	    		renderShadowDistance = renderShadowDistance.trim();
	    		try {
	    			RENDER_SHADOW_DISTANCE = Integer.parseInt(renderShadowDistance);
	    			if (RENDER_SHADOW_DISTANCE>15*CUBE_EDGE_SIZE) RENDER_SHADOW_DISTANCE = (int)(15*CUBE_EDGE_SIZE);
	    			if (RENDER_SHADOW_DISTANCE>RENDER_DISTANCE*CUBE_EDGE_SIZE) RENDER_SHADOW_DISTANCE = (int)(RENDER_DISTANCE*CUBE_EDGE_SIZE);
	    			if (RENDER_SHADOW_DISTANCE<5) RENDER_SHADOW_DISTANCE = 5;
	    			RENDER_SHADOW_DISTANCE_SQR=RENDER_SHADOW_DISTANCE*RENDER_SHADOW_DISTANCE;
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("RENDER_SHADOW_DISTANCE", "10");
	    		}
	    	}
	    	String mipmapGlobal = p.getProperty("MIPMAP_GLOBAL");
	    	if (mipmapGlobal!=null)
	    	{
	    		mipmapGlobal = mipmapGlobal.trim();
	    		try {
	    			MIPMAP_GLOBAL = Boolean.parseBoolean(mipmapGlobal);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("MIPMAP_GLOBAL", "true");
	    		}
	    	}
	    	String mipmapTrees = p.getProperty("MIPMAP_TREES");
	    	if (mipmapTrees!=null)
	    	{
	    		mipmapTrees = mipmapTrees.trim();
	    		try {
	    			MIPMAP_TREES = Boolean.parseBoolean(mipmapTrees);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("MIPMAP_TREES", "false");
	    		}
	    	}
	    	String textureQuality = p.getProperty("TEXTURE_QUALITY");
	    	if (textureQuality!=null)
	    	{
	    		textureQuality = textureQuality.trim();
	    		try {
	    			TEXTURE_QUALITY = Integer.parseInt(textureQuality);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("TEXTURE_QUALITY", "false");
	    		}
	    	}
	    	String bloomEffect = p.getProperty("BLOOM_EFFECT");
	    	if (bloomEffect!=null)
	    	{
	    		bloomEffect = bloomEffect.trim();
	    		try {
	    			BLOOM_EFFECT = Boolean.parseBoolean(bloomEffect);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("BLOOM_EFFECT", "false");
	    		}
	    	}

	    	String cpuAnimatedGrass = p.getProperty("ANIMATED_GRASS");
	    	if (cpuAnimatedGrass!=null)
	    	{
	    		cpuAnimatedGrass = cpuAnimatedGrass.trim();
	    		try {
	    			ANIMATED_GRASS = Boolean.parseBoolean(cpuAnimatedGrass);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("ANIMATED_GRASS", "false");
	    		}
	    	}
	    	String doubleGrass = p.getProperty("DOUBLE_GRASS");
	    	if (doubleGrass!=null)
	    	{
	    		doubleGrass = doubleGrass.trim();
	    		try {
	    			DOUBLE_GRASS = Boolean.parseBoolean(doubleGrass);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("DOUBLE_GRASS", "false");
	    		}
	    	}

	    	String shadows = p.getProperty("SHADOWS");
	    	if (shadows!=null)
	    	{
	    		shadows = shadows.trim();
	    		try {
	    			SHADOWS = Boolean.parseBoolean(shadows);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("SHADOWS", "false");
	    		}
	    	}

	    	String cpuAnimatedTrees = p.getProperty("ANIMATED_TREES");
	    	if (cpuAnimatedTrees!=null)
	    	{
	    		cpuAnimatedGrass = cpuAnimatedGrass.trim();
	    		try {
	    			ANIMATED_TREES = Boolean.parseBoolean(cpuAnimatedTrees);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("ANIMATED_TREES", "false");
	    		}
	    	}
	    	String detailedTrees = p.getProperty("DETAILED_TREES");
	    	if (detailedTrees!=null)
	    	{
	    		detailedTrees = detailedTrees.trim();
	    		try {
	    			DETAILED_TREES = Boolean.parseBoolean(detailedTrees);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("DETAILED_TREES", "false");
	    		}
	    	}
	    	String antialiasSamples = p.getProperty("ANTIALIAS_SAMPLES");
	    	if (antialiasSamples!=null)
	    	{
	    		antialiasSamples = antialiasSamples.trim();
	    		try {
	    			ANTIALIAS_SAMPLES = Integer.parseInt(antialiasSamples);
	    			if (ANTIALIAS_SAMPLES>8) ANTIALIAS_SAMPLES = 8;
	    			if (ANTIALIAS_SAMPLES<0) ANTIALIAS_SAMPLES = 0;
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("ANTIALIAS_SAMPLES", "0");
	    		}
	    	}
	    	String bumpedGround = p.getProperty("BUMPED_GROUND");
	    	if (bloomEffect!=null)
	    	{
	    		bumpedGround = bumpedGround.trim();
	    		try {
	    			BUMPED_GROUND = Boolean.parseBoolean(bumpedGround);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("BUMPED_GROUND", "false");
	    		}
	    	}
	    	String waterShader = p.getProperty("WATER_SHADER");
	    	if (waterShader!=null)
	    	{
	    		waterShader = waterShader.trim();
	    		try {
	    			WATER_SHADER = Boolean.parseBoolean(waterShader);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("WATER_SHADER", "false");
	    		}
	    	}
	    	String waterDetailed = p.getProperty("WATER_DETAILED");
	    	if (waterDetailed!=null)
	    	{
	    		waterDetailed = waterDetailed.trim();
	    		try {
	    			WATER_DETAILED = Boolean.parseBoolean(waterDetailed);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("WATER_DETAILED", "false");
	    		}
	    	}
	    	
	    	
    	} catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }


    
	public int viewDirection = NORTH;
	public int viewPositionX = 0;
	public int viewPositionY = 0;
	public int viewPositionZ = 0;
	public int relativeX = 0, relativeY = 0, relativeZ = 0;
	public boolean onSteep = false;
	public boolean insideArea = false;
	
	public ModelLoader modelLoader = new ModelLoader(this);
	public GeometryBatchHelper batchHelper = new GeometryBatchHelper(this);
	public ModelPool modelPool = new ModelPool(this);
	
	public Engine engine = null;
	
	public RenderedArea renderedArea = new RenderedArea();

	/**
	 * Put quads with solid color depending on light power of orbiters, updateTimeRelated will update their shade.
	 */
	public static HashMap<Spatial,Spatial> hmSolidColorSpatials = new HashMap<Spatial,Spatial>();
	
	
	public void setEngine(Engine engine)
	{
		this.engine = engine;
	}
	
	public World world = null;
	
	public void setWorld(World area)
	{
		world = area;
	}
	
	public void setViewPosition(int x,int y,int z)	
	{
		System.out.println("!!!!!!!!!! VIEW POS: "+y);
		viewPositionX = x;
		viewPositionY = y;
		viewPositionZ = z;
	}

    
	/**
	 * cube side rotation quaternion
	 */
	static Quaternion qN, qS, qW, qE, qT, qB, qTexture;

	/**
	 * Horizontal Rotations 
	 */
	static Quaternion horizontalN, horizontalS, horizontalW, horizontalE;
	static Quaternion horizontalNReal, horizontalSReal, horizontalWReal, horizontalEReal;

	/**
	 * Steep Rotations 
	 */
	static Quaternion steepN, steepS, steepW, steepE;
	static Quaternion steepN_noRot, steepS_noRot, steepW_noRot, steepE_noRot;
	
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, TOP = 4, BOTTOM = 5;

	public static Vector3f dNorth = new Vector3f(0, 0, -1 * CUBE_EDGE_SIZE),
			dSouth = new Vector3f(0, 0, 1 * CUBE_EDGE_SIZE),
			dEast = new Vector3f(1 * CUBE_EDGE_SIZE, 0, 0),
			dWest = new Vector3f(-1 * CUBE_EDGE_SIZE, 0, 0);
	public static Vector3f[] directions = new Vector3f[] {dNorth, dEast, dSouth, dWest};
	
	public static Vector3f tdNorth = new Vector3f(0, 0, -1),
		tdSouth = new Vector3f(0, 0, 1),
		tdEast = new Vector3f(1, 0, 0),
		tdWest = new Vector3f(-1, 0, 0),
		tdTop = new Vector3f(0, 1, 0),
		tdBottom = new Vector3f(0, -1, 0);
	public static Vector3f[] turningDirectionsUnit = new Vector3f[] {tdNorth, tdEast, tdSouth, tdWest,tdTop,tdBottom};
	
	static 
	{
		// creating rotation quaternions for all sides of a cube...
		qT = new Quaternion();
		qT.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
		qB = new Quaternion();
		qB.fromAngleAxis(FastMath.PI * 3 / 2, new Vector3f(1,0,0));
		qS = new Quaternion();
		qS.fromAngleAxis(FastMath.PI * 2, new Vector3f(0,1,0));
		qN = new Quaternion();
		qN.fromAngleAxis(FastMath.PI, new Vector3f(0,1,0));
		qE = new Quaternion();
		qE.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));
		qW = new Quaternion();
		qW.fromAngleAxis(FastMath.PI * 3 / 2, new Vector3f(0,1,0));
		qTexture = new Quaternion();
		qTexture.fromAngleAxis(FastMath.PI/2, new Vector3f(0,0,1));
	}
	
	public static HashMap<Integer,Vector3f> viewDirectionTranslation = new HashMap<Integer, Vector3f>();
	static {
		viewDirectionTranslation.put(new Integer(NORTH),new Vector3f(0,0,1));
		viewDirectionTranslation.put(new Integer(SOUTH),new Vector3f(0,0,-1));
		viewDirectionTranslation.put(new Integer(WEST),new Vector3f(1,0,0));
		viewDirectionTranslation.put(new Integer(EAST),new Vector3f(-1,0,0));
		
	}
	
	public static HashMap<Integer,Object[]> directionAnglesAndTranslations = new HashMap<Integer,Object[]>();
	static 
	{
		directionAnglesAndTranslations.put(new Integer(NORTH), new Object[]{qN,new int[]{0,0,-1}});
		directionAnglesAndTranslations.put(new Integer(SOUTH), new Object[]{qS,new int[]{0,0,1}});
		directionAnglesAndTranslations.put(new Integer(WEST), new Object[]{qW,new int[]{-1,0,0}});
		directionAnglesAndTranslations.put(new Integer(EAST), new Object[]{qE,new int[]{1,0,0}});
		directionAnglesAndTranslations.put(new Integer(TOP), new Object[]{qT,new int[]{0,1,0}});
		directionAnglesAndTranslations.put(new Integer(BOTTOM), new Object[]{qB,new int[]{0,-1,0}});
	}
	
	public static HashMap<Integer,Integer> oppositeDirections = new HashMap<Integer, Integer>();
	static
	{
		oppositeDirections.put(new Integer(NORTH), new Integer(SOUTH));
		oppositeDirections.put(new Integer(SOUTH), new Integer(NORTH));
		oppositeDirections.put(new Integer(WEST), new Integer(EAST));
		oppositeDirections.put(new Integer(EAST), new Integer(WEST));
		oppositeDirections.put(new Integer(TOP), new Integer(BOTTOM));
		oppositeDirections.put(new Integer(BOTTOM), new Integer(TOP));
	}
	public static HashMap<Integer,Integer> nextDirections = new HashMap<Integer, Integer>();
	static
	{
		nextDirections.put(new Integer(NORTH), new Integer(WEST));
		nextDirections.put(new Integer(SOUTH), new Integer(EAST));
		nextDirections.put(new Integer(EAST), new Integer(SOUTH));
		nextDirections.put(new Integer(WEST), new Integer(NORTH));
		nextDirections.put(new Integer(TOP), new Integer(BOTTOM));
		nextDirections.put(new Integer(BOTTOM), new Integer(TOP));
	}
	public static HashMap<Integer,Quaternion> horizontalRotations = new HashMap<Integer, Quaternion>();
	static
	{
		// horizontal rotations
		horizontalN = new Quaternion();
		horizontalN.fromAngles(new float[]{0,0,FastMath.PI * 2});
		horizontalS = new Quaternion();
		horizontalS.fromAngles(new float[]{0,0,FastMath.PI});
		horizontalW = new Quaternion();
		horizontalW.fromAngles(new float[]{0,0,FastMath.PI/2});
		horizontalE = new Quaternion();
		horizontalE.fromAngles(new float[]{0,0,FastMath.PI*3/2});

		horizontalRotations.put(new Integer(NORTH), horizontalN);
		horizontalRotations.put(new Integer(SOUTH), horizontalS);
		horizontalRotations.put(new Integer(WEST), horizontalW);
		horizontalRotations.put(new Integer(EAST), horizontalE);
	}

	public static HashMap<Integer,Quaternion> horizontalRotationsReal = new HashMap<Integer, Quaternion>();
	static
	{
		// horizontal rotations
		horizontalNReal = new Quaternion();
		horizontalNReal.fromAngles(new float[]{0,FastMath.PI * 2,0});
		horizontalSReal = new Quaternion();
		horizontalSReal.fromAngles(new float[]{0,FastMath.PI,0});
		horizontalWReal = new Quaternion();
		horizontalWReal.fromAngles(new float[]{0,FastMath.PI/2,0});
		horizontalEReal = new Quaternion();
		horizontalEReal.fromAngles(new float[]{0,FastMath.PI*3/2,0});

		horizontalRotationsReal.put(new Integer(NORTH), horizontalNReal);
		horizontalRotationsReal.put(new Integer(SOUTH), horizontalSReal);
		horizontalRotationsReal.put(new Integer(WEST), horizontalWReal);
		horizontalRotationsReal.put(new Integer(EAST), horizontalEReal);
	}
	
	public static HashMap<Integer,Quaternion> steepRotations = new HashMap<Integer, Quaternion>();
	static
	{
		// steep rotations
		steepE = new Quaternion();
		steepE.fromAngles(new float[]{0,FastMath.PI/4,0});
		steepW = new Quaternion();
		steepW.fromAngles(new float[]{0,-FastMath.PI/4,0});
		steepS = new Quaternion();
		steepS.fromAngles(new float[]{FastMath.PI/4,0,0});
		steepN = new Quaternion();
		steepN.fromAngles(new float[]{-FastMath.PI/4,0,0});

		steepRotations.put(new Integer(NORTH), steepN);
		steepRotations.put(new Integer(SOUTH), steepS);
		steepRotations.put(new Integer(WEST), steepW);
		steepRotations.put(new Integer(EAST), steepE);
	}
	
	public static HashMap<Integer,Quaternion> steepRotations_special = new HashMap<Integer, Quaternion>();
	static
	{
		// steep rotations with special in-one-step rotation
		steepE_noRot = new Quaternion();
		steepE_noRot.fromAngles(new float[]{FastMath.PI/2,0,3*FastMath.PI/4});
		steepW_noRot = new Quaternion();
		steepW_noRot.fromAngles(new float[]{-FastMath.PI/2,0,FastMath.PI/4});
		steepS_noRot = new Quaternion();
		steepS_noRot.fromAngles(new float[]{0,FastMath.PI/4,FastMath.PI/2});
		steepN_noRot = new Quaternion();
		steepN_noRot.fromAngles(new float[]{0,-3*FastMath.PI/4,-FastMath.PI/2});

		steepRotations_special.put(new Integer(NORTH), steepN_noRot);
		steepRotations_special.put(new Integer(SOUTH), steepS_noRot);
		steepRotations_special.put(new Integer(WEST), steepW_noRot);
		steepRotations_special.put(new Integer(EAST), steepE_noRot);
	}

	public static HashMap<Integer,int[]> moveTranslations = new HashMap<Integer,int[]>();
	static 
	{
		moveTranslations.put(new Integer(NORTH), new int[]{0,0,1});
		moveTranslations.put(new Integer(SOUTH), new int[]{0,0,-1});
		moveTranslations.put(new Integer(WEST), new int[]{-1,0,0});
		moveTranslations.put(new Integer(EAST), new int[]{1,0,0});
		moveTranslations.put(new Integer(TOP), new int[]{0,1,0});
		moveTranslations.put(new Integer(BOTTOM), new int[]{0,-1,0});
	}
	
	
	public static float[][] TREE_LOD_DIST_HIGH = new float[][]{{0f,8f},{8f,16f},{16f,24f},{24f,50f}};
	public static float[][] TREE_LOD_DIST_LOW = new float[][]{{0f,0f},{0f,10f},{10f,20f},{20f,40f}};
	public float[][] treeLodDist = TREE_LOD_DIST_LOW;
	
	public J3DCore()
	{
		self = this;
		if (J3DCore.SHADOWS) stencilBits = 8;
		alphaBits = 0;
		depthBits = 4;
		samples = ANTIALIAS_SAMPLES;
		
		// area subtype to 3d type mapping
		hmAreaSubType3dType.put(Side.DEFAULT_SUBTYPE.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(World.SUBTYPE_OCEAN.id, new Integer(10));
		hmAreaSubType3dType.put(World.SUBTYPE_GROUND.id, new Integer(21));
		hmAreaSubType3dType.put(Plain.SUBTYPE_GROUND.id, EMPTY_SIDE); // no 3d object, flora ground will be rendered
		hmAreaSubType3dType.put(Forest.SUBTYPE_FOREST.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(Lake.SUBTYPE_WATER.id, new Integer(10));
		hmAreaSubType3dType.put(Lake.SUBTYPE_ROCKSIDE.id, new Integer(39));
		hmAreaSubType3dType.put(Lake.SUBTYPE_ROCKBOTTOM.id, new Integer(38));
		hmAreaSubType3dType.put(Lake.SUBTYPE_WATER_EMPTY.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(River.SUBTYPE_WATER.id, new Integer(10));
		hmAreaSubType3dType.put(River.SUBTYPE_WATERFALL.id, new Integer(36));
		hmAreaSubType3dType.put(River.SUBTYPE_INTERSECT.id, new Integer(27));
		hmAreaSubType3dType.put(River.SUBTYPE_ROCKSIDE.id, new Integer(39));
		hmAreaSubType3dType.put(River.SUBTYPE_ROCKBOTTOM.id, new Integer(38));
		hmAreaSubType3dType.put(River.SUBTYPE_ROCKBOTTOM_STEEP.id, new Integer(38));
		hmAreaSubType3dType.put(River.SUBTYPE_WATER_EMPTY.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(House.SUBTYPE_INTERNAL_CEILING.id, new Integer(7));
		hmAreaSubType3dType.put(House.SUBTYPE_INTERNAL_GROUND.id, new Integer(29));
		hmAreaSubType3dType.put(House.SUBTYPE_BOOKCASE.id, new Integer(28));
		hmAreaSubType3dType.put(House.SUBTYPE_EXTERNAL_GROUND.id, new Integer(3));
		hmAreaSubType3dType.put(House.SUBTYPE_EXTERNAL_DOOR.id, new Integer(5));
		hmAreaSubType3dType.put(House.SUBTYPE_WINDOW.id, new Integer(6));
		hmAreaSubType3dType.put(House.SUBTYPE_WALL.id, new Integer(1));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_STEEP.id, 40); // TODO create element for this !!! // no 3d object, flora ground will be rendered rotated!
		hmAreaSubType3dType.put(Mountain.SUBTYPE_INTERSECT_EMPTY.id, EMPTY_SIDE); // No 3d object, it is just climbing side
		hmAreaSubType3dType.put(Mountain.SUBTYPE_ROCK_BLOCK.id, EMPTY_SIDE);//new Integer(13));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_ROCK_BLOCK_VISIBLE.id, new Integer(13));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_ROCK_SIDE.id, new Integer(35));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_GROUND.id, EMPTY_SIDE); // no 3d object, flora ground will be rendered
		hmAreaSubType3dType.put(Mountain.SUBTYPE_INTERSECT.id, new Integer(27));
		hmAreaSubType3dType.put(MountainNew.SUBTYPE_CORNER.id, new Integer(40));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_INTERSECT_BLOCK.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(OakTree.SUBTYPE_TREE.id, new Integer(9));
		hmAreaSubType3dType.put(CherryTree.SUBTYPE_TREE.id, new Integer(12));
		hmAreaSubType3dType.put(GreenPineTree.SUBTYPE_TREE.id, new Integer(18));
		hmAreaSubType3dType.put(GreatPineTree.SUBTYPE_TREE.id, new Integer(25));
		hmAreaSubType3dType.put(GreenBush.SUBTYPE_BUSH.id, new Integer(19));
		hmAreaSubType3dType.put(CoconutTree.SUBTYPE_TREE.id, new Integer(15));
		hmAreaSubType3dType.put(Acacia.SUBTYPE_TREE.id, new Integer(20));
		hmAreaSubType3dType.put(Grass.SUBTYPE_GRASS.id, new Integer(2));
		hmAreaSubType3dType.put(Sand.SUBTYPE_SAND.id, new Integer(16));
		hmAreaSubType3dType.put(Snow.SUBTYPE_SNOW.id, new Integer(17));
		hmAreaSubType3dType.put(JungleGround.SUBTYPE_GROUND.id, new Integer(22));
		hmAreaSubType3dType.put(BigCactus.SUBTYPE_CACTUS.id, new Integer(23));
		hmAreaSubType3dType.put(JunglePalmTrees.SUBTYPE_TREE.id,  new Integer(24));//new Integer(24)); TODO quad model
		hmAreaSubType3dType.put(GreenFern.SUBTYPE_BUSH.id, EMPTY_SIDE);//new Integer(26)); TODO, quad model?
		hmAreaSubType3dType.put(JungleBush.SUBTYPE_BUSH.id, new Integer(30));
		hmAreaSubType3dType.put(Cave.SUBTYPE_GROUND.id, new Integer(31));
		hmAreaSubType3dType.put(Cave.SUBTYPE_BLOCK_GROUND.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(Cave.SUBTYPE_WALL.id, new Integer(32));
		hmAreaSubType3dType.put(Cave.SUBTYPE_WALL_REVERSE.id, new Integer(35));
		hmAreaSubType3dType.put(Cave.SUBTYPE_ENTRANCE.id, new Integer(33));
		hmAreaSubType3dType.put(Cave.SUBTYPE_ROCK.id, new Integer(34));
		hmAreaSubType3dType.put(Cave.SUBTYPE_BLOCK.id, EMPTY_SIDE);
		

		PartlyBillboardModel cherry = new PartlyBillboardModel("pbm_cherry_0","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"cher_1.png"},0,MIPMAP_TREES);
		cherry.shadowCaster = true;
		PartlyBillboardModel cherry_low = new PartlyBillboardModel("pbm_cherry_1","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{},new String[]{"cher_1.png"},1,MIPMAP_TREES);
		cherry_low.shadowCaster = true;
		//cherry_low.windAnimation = false;
		PartlyBillboardModel cherry_lowest = new PartlyBillboardModel("pbm_cherry_2","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"cher_1.png"},2,MIPMAP_TREES);
		cherry_lowest.windAnimation = false;
		PartlyBillboardModel cherry_lowest_2 = new PartlyBillboardModel("pbm_cherry_3","models/tree/cherry_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"cher_1.png"},3,MIPMAP_TREES);
		cherry_lowest_2.windAnimation = false;

		PartlyBillboardModel acacia = new PartlyBillboardModel("pbm_acacia_0","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},0,MIPMAP_TREES);
		acacia.shadowCaster = true;
		PartlyBillboardModel acacia_low = new PartlyBillboardModel("pbm_acacia_1","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{},new String[]{"acac_1.png"},1,MIPMAP_TREES);
		acacia_low.shadowCaster = true;
		//acacia_low.windAnimation = false;
		PartlyBillboardModel acacia_lowest = new PartlyBillboardModel("pbm_acacia_2","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},2,MIPMAP_TREES);
		acacia_lowest.windAnimation = false;
		PartlyBillboardModel acacia_lowest_2 = new PartlyBillboardModel("pbm_acacia_3","models/tree/acacia_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},3,MIPMAP_TREES);
		acacia_lowest_2.windAnimation = false;

		PartlyBillboardModel bush = new PartlyBillboardModel("pbm_bush_0","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},0,MIPMAP_TREES);
		bush.quadXSizeMultiplier = 3.8f;
		bush.quadYSizeMultiplier = 3.2f;
		bush.shadowCaster = true;
		PartlyBillboardModel bush_low = new PartlyBillboardModel("pbm_bush_1","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1.png"},1,MIPMAP_TREES);
		bush_low.quadXSizeMultiplier = 3.3f;
		bush_low.quadYSizeMultiplier = 3.0f;
		bush_low.shadowCaster = true;
		PartlyBillboardModel bush_lowest = new PartlyBillboardModel("pbm_bush_2","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1_low.png"},2,MIPMAP_TREES);
		PartlyBillboardModel bush_lowest_2 = new PartlyBillboardModel("pbm_bush_3","models/bush/bush1.obj",new String[]{"3"},new String[]{"2"},new String[]{"acac_1_low.png"},3,MIPMAP_TREES);
		bush_lowest.quadXSizeMultiplier = 3.8f;
		bush_lowest.quadYSizeMultiplier = 3.5f;
		bush_lowest.windAnimation = false;
		bush_lowest_2.quadXSizeMultiplier = 3.8f;
		bush_lowest_2.quadYSizeMultiplier = 3.5f;
		bush_lowest_2.windAnimation = false;
		
/*		PartlyBillboardModel fern = new PartlyBillboardModel("models/bush/fern.3ds",new String[]{"3"},new String[]{"2"},new String[]{"fern.png"},0,MIPMAP_TREES);
		fern.shadowCaster = true;
		PartlyBillboardModel fern_low = new PartlyBillboardModel("models/bush/fern.3ds",new String[]{"3"},new String[]{"2"},new String[]{"fern.png"},1,MIPMAP_TREES);
		fern_low.shadowCaster = true;
		PartlyBillboardModel fern_lowest = new PartlyBillboardModel("models/bush/fern.3ds",new String[]{"3"},new String[]{"2"},new String[]{"fern.png"},2,MIPMAP_TREES);
*/
		PartlyBillboardModel pine_high = new PartlyBillboardModel("pbm_pine_0","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},0,MIPMAP_TREES);
		pine_high.quadXSizeMultiplier = 3.0f;
		pine_high.quadYSizeMultiplier = 3.5f;
		pine_high.shadowCaster = true;
		PartlyBillboardModel pine_low = new PartlyBillboardModel("pbm_pine_1","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},1,MIPMAP_TREES);
		pine_low.quadXSizeMultiplier = 2f;
		pine_low.quadYSizeMultiplier = 3f;
		pine_low.shadowCaster = true;
		PartlyBillboardModel pine_lowest = new PartlyBillboardModel("pbm_pine_2","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},2,MIPMAP_TREES);
		pine_lowest.quadXSizeMultiplier = 1.6f;
		pine_lowest.quadYSizeMultiplier = 3f;
		pine_lowest.windAnimation = false;
		PartlyBillboardModel pine_lowest_2 = new PartlyBillboardModel("pbm_pine_3","models/tree/pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_1.png"},3,MIPMAP_TREES);
		pine_lowest_2.quadXSizeMultiplier = 1.6f;
		pine_lowest_2.quadYSizeMultiplier = 3f;
		pine_lowest_2.windAnimation = false;

		PartlyBillboardModel great_pine_high = new PartlyBillboardModel("pbm_great_pine_0","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},0,MIPMAP_TREES);
		great_pine_high.quadXSizeMultiplier = 2f;
		great_pine_high.quadYSizeMultiplier = 2f;
		great_pine_high.shadowCaster = true;
		PartlyBillboardModel great_pine_low = new PartlyBillboardModel("pbm_great_pine_1","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},1,MIPMAP_TREES);
		great_pine_low.quadXSizeMultiplier = 2f;
		great_pine_low.quadYSizeMultiplier = 2.5f;
		great_pine_low.shadowCaster = true;
		PartlyBillboardModel great_pine_lowest = new PartlyBillboardModel("pbm_great_pine_2","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},2,MIPMAP_TREES);
		great_pine_lowest.quadXSizeMultiplier = 1.3f;
		great_pine_lowest.quadYSizeMultiplier = 2.0f;
		great_pine_lowest.windAnimation = false;
		PartlyBillboardModel great_pine_lowest_2 = new PartlyBillboardModel("pbm_great_pine_3","models/tree/great_pine_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"pine_2.png"},3,MIPMAP_TREES);
		great_pine_lowest_2.quadXSizeMultiplier = 1.3f;
		great_pine_lowest_2.quadYSizeMultiplier = 2.0f;
		great_pine_lowest_2.windAnimation = false;

		PartlyBillboardModel palm_high = new PartlyBillboardModel("pbm_palm_0","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{},new String[]{"jung_succ_1.png"},0,MIPMAP_TREES);
		palm_high.quadXSizeMultiplier = 1.5f;
		palm_high.quadYSizeMultiplier = 2f;
		palm_high.shadowCaster = true;
		PartlyBillboardModel palm_low = new PartlyBillboardModel("pbm_palm_1","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"jung_succ_1.png"},1,MIPMAP_TREES);
		//palm_low.quadXSizeMultiplier = 2f;
		//palm_low.quadYSizeMultiplier = 2.5f;
		palm_low.shadowCaster = true;
		PartlyBillboardModel palm_lowest = new PartlyBillboardModel("pbm_palm_2","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"jung_succ_1.png"},2,MIPMAP_TREES);
		//palm_lowest.quadXSizeMultiplier = 1.6f;
		palm_lowest.quadXSizeMultiplier = 0.5f;
		palm_lowest.quadYSizeMultiplier = 0.7f;
		palm_lowest.windAnimation = false;
		PartlyBillboardModel palm_lowest_2 = new PartlyBillboardModel("pbm_palm_3","models/tree/great_succ_bb1.obj",new String[]{"3"},new String[]{"2"},new String[]{"jung_succ_1.png"},3,MIPMAP_TREES);
		palm_lowest_2.quadXSizeMultiplier = 0.5f;
		palm_lowest_2.quadYSizeMultiplier = 0.5f;
		//palm_lowest_2.quadXSizeMultiplier = 1.6f;
		//palm_lowest_2.quadYSizeMultiplier = 2.5f;
		palm_lowest_2.windAnimation = false;

		
		PartlyBillboardModel coconut_high = new PartlyBillboardModel("pbm_coconut_0","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},0,MIPMAP_TREES);
		coconut_high.quadXSizeMultiplier = 4f;
		coconut_high.quadYSizeMultiplier = 4f;
		coconut_high.shadowCaster = true;
		coconut_high.cullNone = true;
		PartlyBillboardModel coconut_low = new PartlyBillboardModel("pbm_coconut_1","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},1,MIPMAP_TREES);
		coconut_low.quadXSizeMultiplier = 3f;
		coconut_low.quadYSizeMultiplier = 6f;
		coconut_low.shadowCaster = true;
		coconut_low.cullNone = true;
		PartlyBillboardModel coconut_lowest = new PartlyBillboardModel("pbm_coconut_2","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},2,MIPMAP_TREES);
		coconut_lowest.quadXSizeMultiplier = 1.8f;
		coconut_lowest.quadYSizeMultiplier = 1.7f;
		coconut_lowest.windAnimation = false;
		coconut_lowest.cullNone = true;
		PartlyBillboardModel coconut_lowest_2 = new PartlyBillboardModel("pbm_coconut_3","models/tree/coconut_bb1.obj",new String[]{"3"},new String[]{},new String[]{"palm2.png"},3,MIPMAP_TREES);
		coconut_lowest_2.quadXSizeMultiplier = 1.8f;
		coconut_lowest_2.quadYSizeMultiplier = 1.7f;
		coconut_lowest_2.windAnimation = false;
		coconut_lowest_2.cullNone = true;

		PartlyBillboardModel jungle_bush = new PartlyBillboardModel("pbm_jungle_bush_0","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},0,MIPMAP_TREES);
		jungle_bush.quadXSizeMultiplier = 2f;
		jungle_bush.quadYSizeMultiplier = 2f;
		jungle_bush.shadowCaster = true;
		PartlyBillboardModel jungle_bush_low = new PartlyBillboardModel("pbm_jungle_bush_1","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},1,MIPMAP_TREES);
		jungle_bush_low.quadXSizeMultiplier = 1.6f;
		jungle_bush_low.quadYSizeMultiplier = 1.6f;
		jungle_bush_low.shadowCaster = true;
		PartlyBillboardModel jungle_bush_lowest = new PartlyBillboardModel("pbm_jungle_bush_2","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},2,MIPMAP_TREES);
		PartlyBillboardModel jungle_bush_lowest_2 = new PartlyBillboardModel("pbm_jungle_bush_3","models/bush/bush1.3ds",new String[]{"3"},new String[]{"2"},new String[]{"palm2a.png"},3,MIPMAP_TREES);
		jungle_bush_lowest.quadXSizeMultiplier = 3.5f;
		jungle_bush_lowest.quadYSizeMultiplier = 3.5f;
		jungle_bush_lowest.windAnimation = false;
		jungle_bush_lowest_2.quadXSizeMultiplier = 3.5f;
		jungle_bush_lowest_2.quadYSizeMultiplier = 3.5f;
		jungle_bush_lowest_2.windAnimation = false;
		
		SimpleModel pine = new SimpleModel("models/tree/pine.3ds",null,MIPMAP_TREES);
		pine.shadowCaster = true; pine.useClodMesh = true;
		SimpleModel great_pine = new SimpleModel("models/tree/great_pine.3ds",null,MIPMAP_TREES);
		great_pine.shadowCaster = true; great_pine.useClodMesh = true;
		SimpleModel palm = new SimpleModel("models/tree/coconut.3ds",null,MIPMAP_TREES);
		palm.shadowCaster = true; palm.useClodMesh = true; palm.cullNone = true;
		SimpleModel jungletrees_mult = new SimpleModel("models/tree/palm.3ds",null,MIPMAP_TREES);
		jungletrees_mult.shadowCaster = true; jungletrees_mult.useClodMesh = true; jungletrees_mult.cullNone = true;
		SimpleModel cactus = new SimpleModel("sides/cactus.obj",null,MIPMAP_TREES);
		cactus.shadowCaster = true; cactus.useClodMesh = false; cactus.cullNone = true;
		SimpleModel bush1 = new SimpleModel("models/bush/bush1.3ds",null,MIPMAP_TREES);
		bush1.shadowCaster = true; bush1.useClodMesh = true;
		SimpleModel fern1 = new SimpleModel("models/bush/fern.3ds",null,MIPMAP_TREES);
		fern1.shadowCaster = true; fern1.useClodMesh = true;

		treeLodDist = TREE_LOD_DIST_LOW;
		if (DETAILED_TREES)
		{
			treeLodDist = TREE_LOD_DIST_HIGH;
		}

		LODModel lod_cherry = new LODModel("cherry",new SimpleModel[]{cherry,cherry_low,cherry_lowest,cherry_lowest_2},treeLodDist);
		lod_cherry.shadowCaster = true;
		LODModel lod_acacia = new LODModel("acacia",new SimpleModel[]{acacia,acacia_low,acacia_lowest,acacia_lowest_2},treeLodDist);
		lod_acacia.shadowCaster = true;
		//LODModel lod_pine = new LODModel(new SimpleModel[]{pine},new float[][]{{0f,15f}});
		LODModel lod_pine = new LODModel("pine",new SimpleModel[]{pine_high,pine_high,pine_lowest,pine_lowest_2},treeLodDist);
		lod_pine.shadowCaster = true;
		//LODModel lod_great_pine = new LODModel(new SimpleModel[]{great_pine},new float[][]{{0f,15f}});
		LODModel lod_great_pine = new LODModel("great_pine",new SimpleModel[]{great_pine_high,great_pine_high,great_pine_lowest,great_pine_lowest_2},treeLodDist);
		lod_great_pine.shadowCaster = true;
		//LODModel lod_palm = new LODModel(new SimpleModel[]{palm},new float[][]{{0f,15f}});
		LODModel lod_palm = new LODModel("palm",new SimpleModel[]{coconut_high,coconut_high,coconut_lowest,coconut_lowest_2},treeLodDist);
		lod_palm.shadowCaster = true;
		LODModel lod_jungletrees_mult = new LODModel("jungletrees_mult",new SimpleModel[]{palm_high,palm_high,palm_lowest_2,palm_lowest_2},treeLodDist);
		lod_jungletrees_mult.shadowCaster = true;
		LODModel lod_cactus = new LODModel("cactus",new SimpleModel[]{cactus},new float[][]{{0f,15f}});
		lod_cactus.shadowCaster = true;
		LODModel lod_bush1 = new LODModel("bush1",new SimpleModel[]{bush,bush_low,bush_lowest,bush_lowest_2},treeLodDist);
		lod_bush1.shadowCaster = true;
		LODModel lod_jungle_bush1 = new LODModel("jungle_bush1",new SimpleModel[]{jungle_bush,jungle_bush_low,jungle_bush_lowest,jungle_bush_lowest_2},treeLodDist);
		lod_jungle_bush1.shadowCaster = true;
		LODModel lod_fern = new LODModel("fern",new SimpleModel[]{fern1},new float[][]{{0f,15f}});
		lod_fern.shadowCaster = true;

		TextureStateVegetationModel tsm_cont_grass = new TextureStateVegetationModel(new String[]{"grass_aard.png"},0.55f,0.4f,3,0.7f);
		TextureStateVegetationModel tsm_cont_grass_flower = new TextureStateVegetationModel(new String[]{"grass1_flower_2.png"},0.7f,0.4f,2,1.0f);
		//,"grass1_flower.png","grass1_flower_2.png"
		LODModel lod_cont_grass_1 = new LODModel("cont_grass_1",new Model[]{tsm_cont_grass},new float[][]{{0f,RENDER_GRASS_DISTANCE}});
		lod_cont_grass_1.rotateOnSteep = true;
		
		TextureStateVegetationModel tsm_jung_grass = new TextureStateVegetationModel(new String[]{"jungle_foliage1.png"},0.5f,0.45f,3,0.7f);
		TextureStateVegetationModel tsm_jung_grass_flower = new TextureStateVegetationModel(new String[]{"jungle_foliage1_flower.png"},0.5f,0.45f,2,1.0f);
		LODModel lod_jung_grass_1 = new LODModel("jung_grass_1",new Model[]{tsm_jung_grass},new float[][]{{0f,RENDER_GRASS_DISTANCE}});
		lod_jung_grass_1.rotateOnSteep = true;

		// 3d type to file mapping		
		SimpleModel wall_thick = new SimpleModel("sides/wall_thick.3ds", null);
		wall_thick.shadowCaster = true;
		wall_thick.cullNone = true;
		wall_thick.farViewEnabled = true;
		SimpleModel wall_window = new SimpleModel("sides/wall_window.3ds", null);
		wall_window.shadowCaster = true;
		wall_window.cullNone = true;
		wall_window.farViewEnabled = true;
		SimpleModel wall_door = new SimpleModel("sides/door.3ds", null);
		wall_door.shadowCaster = true;
		wall_door.cullNone = true;
		wall_door.farViewEnabled = true;
		SimpleModel wall_door_wall = new SimpleModel("sides/wall_door.3ds", null);
		wall_door_wall.shadowCaster = true;
		wall_door_wall.cullNone = true;
		wall_door_wall.farViewEnabled = true;
		SimpleModel roof_side = new SimpleModel("sides/roof_side.3ds", null);
		roof_side.shadowCaster = true;
		roof_side.cullNone = true;
		roof_side.farViewEnabled = true;
		SimpleModel roof_corner = new SimpleModel("sides/roof_corner.3ds", null);
		roof_corner.shadowCaster = true;
		roof_corner.farViewEnabled = true;
		SimpleModel roof_opp = new SimpleModel("sides/roof_corner_opp.3ds", null);
		roof_opp.shadowCaster = true;
		roof_opp.farViewEnabled = true;
		SimpleModel roof_non = new SimpleModel("sides/roof_corner_non.3ds", null);
		roof_non.shadowCaster = true;
		roof_non.farViewEnabled = true;
		SimpleModel ceiling = new SimpleModel("sides/ceiling_pattern1.3ds",null);
		ceiling.shadowCaster = true;
		SimpleModel roof_top = new SimpleModel("sides/roof_top.3ds",null);
		roof_top.shadowCaster = true;
		roof_top.farViewEnabled = true;
		
		SimpleModel cave_rock = new SimpleModel("models/ground/cave_rock.obj", null);
		SimpleModel wall_cave = new SimpleModel("models/ground/wall_cave.obj", null);
		SimpleModel wall_cave_rev = new SimpleModel("models/ground/wall_cave_rev.obj", null);
		SimpleModel ground_cave = new SimpleModel("models/ground/ground_cave.obj", null);
		SimpleModel entrance_cave = new SimpleModel("models/ground/cave_entrance.obj", null);
		//SimpleModel entrance_cave = new SimpleModel("models/ground/ground_cave.obj", null);
		
		hm3dTypeRenderedSide.put(new Integer(1), new RenderedContinuousSide(
				new SimpleModel[]{wall_thick},
				new SimpleModel[]{roof_side},
				new SimpleModel[]{roof_corner},
				new SimpleModel[]{roof_opp},
				new SimpleModel[]{roof_non}
				));
		hm3dTypeRenderedSide.put(new Integer(5), new RenderedContinuousSide(
				new SimpleModel[]{wall_door,wall_door_wall},
				new SimpleModel[]{roof_side},
				new SimpleModel[]{roof_corner},
				new SimpleModel[]{roof_opp},
				new SimpleModel[]{roof_non}
				));
		hm3dTypeRenderedSide.put(new Integer(6), new RenderedContinuousSide(
				new SimpleModel[]{wall_window,new SimpleModel("sides/window1.3ds", null)},
				new SimpleModel[]{roof_side},
				new SimpleModel[]{roof_corner},
				new SimpleModel[]{roof_opp},
				new SimpleModel[]{roof_non}
				));


		hm3dTypeRenderedSide.put(new Integer(7), new RenderedTopSide(
				//new SimpleModel[]{},
				new SimpleModel[]{ceiling},
				new SimpleModel[]{roof_top}
				));
		
		int yCommon = 1;
		
		QuadModel qm_grass = new QuadModel("grass2.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE); qm_grass.rotateOnSteep = true; qm_grass.farViewEnabled = true;
		SimpleModel sm_grass = new SimpleModel("models/ground/ground_1.obj","grass2.jpg"); sm_grass.rotateOnSteep = true; sm_grass.yGeomBatchSize = yCommon; sm_grass.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass.farViewEnabled = true;
		SimpleModel sm_grass_2 = new SimpleModel("models/ground/ground_2.obj","grass2.jpg"); sm_grass_2.rotateOnSteep = true; sm_grass_2.yGeomBatchSize = yCommon; sm_grass_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_2.farViewEnabled = true;
		SimpleModel sm_grass_3 = new SimpleModel("models/ground/ground_3.obj","grass2.jpg"); sm_grass_3.rotateOnSteep = true; sm_grass_3.yGeomBatchSize = yCommon; sm_grass_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_3.farViewEnabled = true;
		SimpleModel sm_grass_steep = new SimpleModel("models/ground/ground_steep_1.obj","grass2.jpg"); sm_grass_steep.rotateOnSteep = true; sm_grass_steep.yGeomBatchSize = yCommon; sm_grass_steep.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_steep.noSpecialSteepRotation = false; sm_grass_steep.farViewEnabled = true;
		SimpleModel sm_grass_steep_2 = new SimpleModel("models/ground/ground_steep_2.obj","grass2.jpg"); sm_grass_steep_2.rotateOnSteep = true; sm_grass_steep_2.yGeomBatchSize = yCommon; sm_grass_steep_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_steep_2.noSpecialSteepRotation = false; sm_grass_steep_2.farViewEnabled = true;
		SimpleModel sm_grass_steep_3 = new SimpleModel("models/ground/ground_steep_3.obj","grass2.jpg"); sm_grass_steep_3.rotateOnSteep = true; sm_grass_steep_3.yGeomBatchSize = yCommon; sm_grass_steep_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_grass_steep_3.noSpecialSteepRotation = false; sm_grass_steep_3.farViewEnabled = true;

		QuadModel qm_rock_no_rot = new QuadModel("cave_wall.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE);
		QuadModel qm_cave_wall = new QuadModel("cave_wall.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE);
		
		QuadModel qm_cave_ground = new QuadModel("cave_ground.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE); qm_cave_ground.rotateOnSteep = true; qm_cave_ground.farViewEnabled = true;
		SimpleModel sm_cave_ground = new SimpleModel("models/ground/ground_1.obj","cave_ground.jpg"); sm_cave_ground.yGeomBatchSize = yCommon; sm_cave_ground.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_cave_ground.farViewEnabled = true;
		SimpleModel sm_cave_ground_2 = new SimpleModel("models/ground/ground_2.obj","cave_ground.jpg"); sm_cave_ground_2.yGeomBatchSize = yCommon; sm_cave_ground_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_cave_ground_2.farViewEnabled = true;
		SimpleModel sm_cave_ground_3 = new SimpleModel("models/ground/ground_3.obj","cave_ground.jpg"); sm_cave_ground_3.yGeomBatchSize = yCommon; sm_cave_ground_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_cave_ground_3.farViewEnabled = true;
		
		SimpleModel sm_river_bottom = new SimpleModel("models/ground/ground_2.obj","cave_ground.jpg"); sm_river_bottom.yGeomBatchSize = yCommon; sm_river_bottom.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_river_bottom.rotateOnSteep = true; sm_river_bottom.farViewEnabled = true;
		SimpleModel sm_river_side_norot = new SimpleModel("models/ground/ground_1.obj","cave_ground.jpg"); sm_river_side_norot.yGeomBatchSize = yCommon; sm_river_side_norot.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_river_side_norot.rotateOnSteep = false; sm_river_side_norot.farViewEnabled = true;
		SimpleModel sm_water_rock_side = new SimpleModel("models/ground/water_rock_side.obj","cave_ground.jpg"); sm_river_bottom.yGeomBatchSize = yCommon; sm_river_bottom.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_river_bottom.rotateOnSteep = true; sm_river_bottom.farViewEnabled = true;

		LODModel lod_cave_wall = new LODModel("cave_wall",new Model[]{wall_cave,qm_cave_wall},treeLodDist);
		lod_jungle_bush1.shadowCaster = false;
		
		SimpleModel sm_road_stone = new SimpleModel("models/ground/road_stone_1.3ds",null); sm_road_stone.rotateOnSteep = true; sm_road_stone.farViewEnabled = true;
		//QuadModel qm_road_stone = new QuadModel("stone.jpg","stone_bump.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE,true); qm_grass.rotateOnSteep = true;
		QuadModel qm_road_stone = new QuadModel("stone.jpg","NormalMap.jpg",CUBE_EDGE_SIZE,CUBE_EDGE_SIZE,false); qm_road_stone.rotateOnSteep = true; qm_road_stone.farViewEnabled = true;

		SimpleModel sm_house_wood = new SimpleModel("models/ground/house_wood.3ds",null); sm_house_wood.rotateOnSteep = true; sm_house_wood.farViewEnabled = true;
		QuadModel qm_house_wood = new QuadModel("grndwnot.jpg"); qm_house_wood.rotateOnSteep = true; qm_house_wood.rotateOnSteep = true; qm_house_wood.farViewEnabled = true;

		QuadModel qm_desert = new QuadModel("sand2.jpg"); qm_desert.rotateOnSteep = true; qm_desert.farViewEnabled = true;
		SimpleModel sm_desert = new SimpleModel("models/ground/ground_1.obj","sand2.jpg"); sm_desert.rotateOnSteep = true; sm_desert.yGeomBatchSize = yCommon; sm_desert.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert.farViewEnabled = true;
		SimpleModel sm_desert_2 = new SimpleModel("models/ground/ground_2.obj","sand2.jpg"); sm_desert_2.rotateOnSteep = true; sm_desert_2.yGeomBatchSize = yCommon; sm_desert_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_2.farViewEnabled = true;
		SimpleModel sm_desert_3 = new SimpleModel("models/ground/ground_3.obj","sand2.jpg"); sm_desert_3.rotateOnSteep = true; sm_desert_3.yGeomBatchSize = yCommon; sm_desert_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_3.farViewEnabled = true;
		SimpleModel sm_desert_steep = new SimpleModel("models/ground/ground_steep_1.obj","sand2.jpg"); sm_desert_steep.rotateOnSteep = true; sm_desert_steep.yGeomBatchSize = yCommon; sm_desert_steep.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_steep.noSpecialSteepRotation = false; sm_desert_steep.farViewEnabled = true;
		SimpleModel sm_desert_steep_2 = new SimpleModel("models/ground/ground_steep_2.obj","sand2.jpg"); sm_desert_steep_2.rotateOnSteep = true; sm_desert_steep_2.yGeomBatchSize = yCommon; sm_desert_steep_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_steep_2.noSpecialSteepRotation = false; sm_desert_steep_2.farViewEnabled = true;
		SimpleModel sm_desert_steep_3 = new SimpleModel("models/ground/ground_steep_3.obj","sand2.jpg"); sm_desert_steep_3.rotateOnSteep = true; sm_desert_steep_3.yGeomBatchSize = yCommon; sm_desert_steep_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_desert_steep_3.noSpecialSteepRotation = false; sm_desert_steep_3.farViewEnabled = true;
		
		QuadModel qm_arctic = new QuadModel("snow1.jpg"); qm_arctic.rotateOnSteep = true; qm_arctic.farViewEnabled = true;
		
		QuadModel qm_jungle = new QuadModel("jungle.jpg"); qm_jungle.rotateOnSteep = true; qm_jungle.farViewEnabled = true;
		SimpleModel sm_jungle = new SimpleModel("models/ground/ground_1.obj","jungle.jpg"); sm_jungle.rotateOnSteep = true; sm_jungle.yGeomBatchSize = yCommon; sm_jungle.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle.farViewEnabled = true;
		SimpleModel sm_jungle_2 = new SimpleModel("models/ground/ground_2.obj","jungle.jpg"); sm_jungle_2.rotateOnSteep = true; sm_jungle_2.yGeomBatchSize = yCommon; sm_jungle_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_2.farViewEnabled = true;
		SimpleModel sm_jungle_3 = new SimpleModel("models/ground/ground_3.obj","jungle.jpg"); sm_jungle_3.rotateOnSteep = true; sm_jungle_3.yGeomBatchSize = yCommon; sm_jungle_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_3.farViewEnabled = true;
		SimpleModel sm_jungle_steep = new SimpleModel("models/ground/ground_steep_1.obj","jungle.jpg"); sm_jungle_steep.rotateOnSteep = true; sm_jungle_steep.yGeomBatchSize = yCommon; sm_jungle_steep.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_steep.noSpecialSteepRotation = false; sm_jungle_steep.farViewEnabled = true;
		SimpleModel sm_jungle_steep_2 = new SimpleModel("models/ground/ground_steep_2.obj","jungle.jpg"); sm_jungle_steep_2.rotateOnSteep = true; sm_jungle_steep_2.yGeomBatchSize = yCommon; sm_jungle_steep_2.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_steep_2.noSpecialSteepRotation = false; sm_jungle_steep_2.farViewEnabled = true;
		SimpleModel sm_jungle_steep_3 = new SimpleModel("models/ground/ground_steep_3.obj","jungle.jpg"); sm_jungle_steep_3.rotateOnSteep = true; sm_jungle_steep_3.yGeomBatchSize = yCommon; sm_jungle_steep_3.xGeomBatchSize = GeometryBatchHelper.QUAD_MODEL_BATCHED_SPACE_SIZE; sm_jungle_steep_3.noSpecialSteepRotation = false; sm_jungle_steep_3.farViewEnabled = true;

		SimpleModel sm_female = new SimpleModel("models/fauna/fem.obj",null); sm_female.rotateOnSteep = true;
		sm_female.cullNone = true; sm_female.batchEnabled = false;
		SimpleModel sm_male = new SimpleModel("models/fauna/male.obj",null); sm_male.rotateOnSteep = true;
		sm_male.cullNone = true; sm_male.batchEnabled = false;
		SimpleModel sm_wolf = new SimpleModel("models/fauna/wolf.obj",null); sm_wolf.rotateOnSteep = true;
		sm_wolf.cullNone = true; sm_wolf.batchEnabled = false;
		SimpleModel sm_warthog = new SimpleModel("models/fauna/warthog_model.obj",null); sm_warthog.rotateOnSteep = true;
		sm_warthog.cullNone = true; sm_warthog.batchEnabled = false;
		SimpleModel sm_fox = new SimpleModel("models/fauna/redfox.obj",null); sm_fox.rotateOnSteep = true;
		sm_fox.cullNone = true; sm_fox.batchEnabled = false;
		SimpleModel sm_gorilla = new SimpleModel("models/fauna/gorilla_texture.obj",null); sm_gorilla.rotateOnSteep = true;
		sm_gorilla.cullNone = true; sm_gorilla.batchEnabled = false; sm_gorilla.shadowCaster = true;
		
		if (RENDER_GRASS_DISTANCE>0) 
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedHashAlteredSide(new Model[]{tsm_cont_grass,tsm_cont_grass_flower}, new Model[][]{{sm_grass,sm_grass_2,sm_grass_3,sm_grass_3,sm_grass_3}}, new Model[][]{{sm_grass_steep,sm_grass_steep_2,sm_grass_steep_3,sm_grass_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide(new Model[]{qm_grass,tsm_cont_grass,tsm_cont_grass_flower}));//lod_cont_grass_1}));
		} else
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedHashAlteredSide(new Model[]{}, new Model[][]{{sm_grass,sm_grass_2,sm_grass_3,sm_grass_3,sm_grass_3}}, new Model[][]{{sm_grass_steep,sm_grass_steep_2,sm_grass_steep_3,sm_grass_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide(new Model[]{qm_grass}));
		}
		
		hm3dTypeRenderedSide.put(new Integer(3), new RenderedSide(new Model[]{qm_road_stone}));
		//hm3dTypeRenderedSide.put(new Integer(3), new RenderedHashAlteredSide(new Model[]{qm_road_stone}, new Model[][]{{sm_wolf,sm_warthog,sm_gorilla,sm_fox}}));
		hm3dTypeRenderedSide.put(new Integer(29), new RenderedSide(new Model[]{qm_house_wood}));
		hm3dTypeRenderedSide.put(new Integer(4), new RenderedSide("sides/ceiling_pattern1.3ds",null));
		//hm3dTypeRenderedSide.put(new Integer(16), new RenderedSide(new Model[]{sm_desert}));
		if (BUMPED_GROUND) 
		{
			hm3dTypeRenderedSide.put(new Integer(16), new RenderedHashAlteredSide(new Model[]{},new Model[][]{{sm_desert,sm_desert_2,sm_desert_3,sm_desert_3,sm_desert_3}},new Model[][]{{sm_desert_steep,sm_desert_steep_2,sm_desert_steep_3,sm_desert_steep_3}}));
		} else
		{
			hm3dTypeRenderedSide.put(new Integer(16), new RenderedSide(new Model[]{qm_desert}));
		}
		hm3dTypeRenderedSide.put(new Integer(17), new RenderedSide(new Model[]{qm_arctic}));
		hm3dTypeRenderedSide.put(new Integer(21), new RenderedSide(new Model[]{sm_grass}));
		hm3dTypeRenderedSide.put(new Integer(35), new RenderedSide(new Model[]{qm_rock_no_rot}));
		
		if (RENDER_GRASS_DISTANCE>0) 
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedHashAlteredSide(new Model[]{tsm_jung_grass, tsm_jung_grass_flower}, new Model[][]{{sm_jungle,sm_jungle_2,sm_jungle_3,sm_jungle_3,sm_jungle_3}},new Model[][]{{sm_jungle_steep,sm_jungle_steep_2,sm_jungle_steep_3,sm_jungle_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedSide(new Model[]{qm_jungle,tsm_jung_grass, tsm_jung_grass_flower}));
		}
		else 
		{
			if (BUMPED_GROUND)
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedHashAlteredSide(new Model[]{}, new Model[][]{{sm_jungle,sm_jungle_2,sm_jungle_3,sm_jungle_3,sm_jungle_3}},new Model[][]{{sm_jungle_steep,sm_jungle_steep_2,sm_jungle_steep_3,sm_jungle_steep_3}}));//lod_cont_grass_1}));
			else
				hm3dTypeRenderedSide.put(new Integer(22), new RenderedSide(new Model[]{qm_jungle}));
		}
		
		hm3dTypeRenderedSide.put(new Integer(8), new RenderedSide("sides/fence.3ds",null));
		
		boolean LOD_VEG = LOD_VEGETATION;
		
		// lod vegetations
		hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new Model[]{lod_cherry})); // oak TODO!
		hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new Model[]{lod_cherry}));
		hm3dTypeRenderedSide.put(new Integer(15), new RenderedHashRotatedSide(new Model[]{lod_palm}));
		hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new Model[]{lod_pine}));
		hm3dTypeRenderedSide.put(new Integer(19), new RenderedHashRotatedSide(new Model[]{lod_bush1})); 
		hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new Model[]{lod_acacia}));
		hm3dTypeRenderedSide.put(new Integer(23), new RenderedHashRotatedSide(new Model[]{lod_cactus}));
		hm3dTypeRenderedSide.put(new Integer(24), new RenderedHashRotatedSide(new Model[]{lod_jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(25), new RenderedHashRotatedSide(new Model[]{lod_great_pine}));
		hm3dTypeRenderedSide.put(new Integer(26), new RenderedHashRotatedSide(new Model[]{lod_fern}));
		hm3dTypeRenderedSide.put(new Integer(30), new RenderedHashRotatedSide(new Model[]{lod_jungle_bush1}));


		if (!LOD_VEG)
		{
			// no lod version
			hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new Model[]{cherry})); // oak TODO!
			hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new Model[]{cherry}));
			hm3dTypeRenderedSide.put(new Integer(15), new RenderedHashRotatedSide(new Model[]{coconut_high}));
			hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new Model[]{pine_high}));
			hm3dTypeRenderedSide.put(new Integer(19), new RenderedHashRotatedSide(new Model[]{bush})); 
			hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new Model[]{acacia}));
			hm3dTypeRenderedSide.put(new Integer(23), new RenderedHashRotatedSide(new Model[]{cactus}));
			hm3dTypeRenderedSide.put(new Integer(24), new RenderedHashRotatedSide(new Model[]{palm_high}));
			hm3dTypeRenderedSide.put(new Integer(25), new RenderedHashRotatedSide(new Model[]{great_pine_high}));
			hm3dTypeRenderedSide.put(new Integer(26), new RenderedHashRotatedSide(new Model[]{fern1}));
			hm3dTypeRenderedSide.put(new Integer(30), new RenderedHashRotatedSide(new Model[]{jungle_bush}));
		}
		
		/*hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new Model[]{jungletrees_mult})); // oak TODO!
		hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(15), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(19), new RenderedHashRotatedSide(new Model[]{bush1})); 
		hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(23), new RenderedHashRotatedSide(new Model[]{cactus}));
		hm3dTypeRenderedSide.put(new Integer(24), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(25), new RenderedHashRotatedSide(new Model[]{jungletrees_mult}));
		hm3dTypeRenderedSide.put(new Integer(26), new RenderedHashRotatedSide(new Model[]{fern1}));
		hm3dTypeRenderedSide.put(new Integer(30), new RenderedHashRotatedSide(new Model[]{bush1}));*/
		
		QuadModel qm_water = new QuadModel("water1.jpg"); qm_water.rotateOnSteep = true; qm_water.waterQuad = true; qm_water.farViewEnabled = true;
		QuadModel qm_waterfall = new QuadModel("water_fall1.jpg"); qm_waterfall.rotateOnSteep = true; qm_waterfall.waterQuad = false; qm_waterfall.farViewEnabled = true;
		hm3dTypeRenderedSide.put(new Integer(10), new RenderedSide(new Model[]{qm_water}));
		//hm3dTypeRenderedSide.put(new Integer(11), new RenderedSide("models/ground/hill_side.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(13), new RenderedSide("sides/hill.3ds",null));
		SimpleModel sm_intersect = new SimpleModel("models/ground/hillintersect.obj",null); sm_intersect.farViewEnabled = true;
		hm3dTypeRenderedSide.put(new Integer(27), new RenderedSide(new Model[]{sm_intersect}));
		SimpleModel sm_bookcase = new SimpleModel("models/inside/furniture/bookcase.3ds",null);
		sm_bookcase.batchEnabled = false;
		hm3dTypeRenderedSide.put(new Integer(28), new RenderedSide(new Model[]{sm_bookcase}));
		SimpleModel sm_rockcorner = new SimpleModel("models/ground/rockcorner.obj",null); sm_rockcorner.farViewEnabled = true;
		hm3dTypeRenderedSide.put(new Integer(40), new RenderedSide(new Model[]{sm_rockcorner}));
		
		hm3dTypeRenderedSide.put(new Integer(31), new RenderedHashAlteredSide(new Model[]{},new Model[][]{{sm_cave_ground,sm_cave_ground_2,sm_cave_ground_3,sm_cave_ground_3,sm_cave_ground_3}}));//ground_cave}));
		//hm3dTypeRenderedSide.put(new Integer(32), new RenderedSide(new Model[]{qm_cave_wall}));
		hm3dTypeRenderedSide.put(new Integer(32), new RenderedSide(new Model[]{wall_cave}));//lod_cave_wall}));
		hm3dTypeRenderedSide.put(new Integer(33), new RenderedSide(new Model[]{entrance_cave}));
		hm3dTypeRenderedSide.put(new Integer(34), new RenderedHashRotatedSide(new Model[]{cave_rock},true));
		hm3dTypeRenderedSide.put(new Integer(35), new RenderedSide(new Model[]{wall_cave_rev}));//lod_cave_wall}));*/
		hm3dTypeRenderedSide.put(new Integer(36), new RenderedSide(new Model[]{qm_waterfall}));
		hm3dTypeRenderedSide.put(new Integer(37), new RenderedSide(new Model[]{sm_river_side_norot}));
		hm3dTypeRenderedSide.put(new Integer(38), new RenderedSide(new Model[]{sm_river_bottom}));
		hm3dTypeRenderedSide.put(new Integer(39), new RenderedSide(new Model[]{sm_water_rock_side}));

		// NEXT ID = 41
		
	}

	public void initCore()
	{
       this.setDialogBehaviour(J3DCore.ALWAYS_SHOW_PROPS_DIALOG);//FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        this.start();
	}
	
	protected void initSystem() throws JmeException
	{
		super.initSystem();
		input = new ClassicInputHandler(this,cam);
	}
	
	public DisplaySystem getDisplay()
	{
		return display;
	}
    
	public Camera getCamera()
	{
		return cam;
	}
	
	HashMap<Integer, RenderedCube> hmCurrentCubes = new HashMap<Integer, RenderedCube>();
	ArrayList<RenderedCube> alCurrentCubes = new ArrayList<RenderedCube>();
	
	
	/**
	 * Creates the spatials (spheres) for a world orbiter
	 * @param o
	 * @return
	 */
	public Spatial createSpatialForOrbiter(Orbiter o)
	{
		if (o.type==SimpleSun.SIMPLE_SUN_ORBITER) {
			// lens flare code...
	        LightNode lightNode;
	        LensFlare flare;

	        PointLight dr = new PointLight();
	        dr.setEnabled(true);
	        dr.setDiffuse(ColorRGBA.black);
	        dr.setAmbient(ColorRGBA.black);
	        dr.setEnabled(false);
	        dr.setLocation(new Vector3f(0f, 0f, 0f));
	        dr.setShadowCaster(false);
	        extLightState.setTwoSidedLighting(false);
	        
	        lightNode = new LightNode("light", skydomeLightState);
	        lightNode.setLight(dr);

	        lightNode.setTarget(skyParentNode);
	        lightNode.setLocalTranslation(new Vector3f(-4f, -4f, -4f));

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
	        //flare.setIntensity(J3DCore.BLOOM_EFFECT?0.0001f:1.0f);
	        flare.setRootNode(rootNode);
	        groundParentNode.attachChild(lightNode);

	        // notice that it comes at the end
	        lightNode.attachChild(flare);

	        TriMesh sun = new Sphere(o.id,20,20,5f);
	        Node sunNode = new Node();
	        sunNode.attachChild(sun);
			skyParentNode.attachChild(sunNode);
			
			Texture texture = TextureManager.loadTexture("./data/textures/low/"+"sun.png",Texture.MM_LINEAR,
                    Texture.FM_LINEAR);

			texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
			texture.setApply(Texture.AM_REPLACE);
			texture.setRotation(J3DCore.qTexture);

			TextureState ts = getDisplay().getRenderer().createTextureState();
			ts.setTexture(texture, 0);
			
            ts.setEnabled(true);
			sun.setRenderState(ts);
			sun.setRenderState(getDisplay().getRenderer().createFogState());
			
			
			lightNode.attachChild(sun);
			sunNode.attachChild(lightNode);
	        return lightNode;
			
	        
		} else
		if (o.type==SimpleMoon.SIMPLE_MOON_ORBITER) {
			TriMesh moon = new Sphere(o.id,20,20,5f);
			
			Texture texture = TextureManager.loadTexture("./data/orbiters/moon2.jpg",Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
			
			if (texture!=null) {

				texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
				texture.setApply(Texture.AM_REPLACE);
				texture.setRotation(qTexture);
				TextureState state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				state.setTexture(texture,0);
				
				state.setEnabled(true);
	            
				moon.setRenderState(state);
			}
			moon.updateRenderState();

			skyParentNode.attachChild(moon);
			moon.setLightCombineMode(LightState.OFF);
			moon.setRenderState(getDisplay().getRenderer().createFogState());
			return moon;
		} 
		return null;
			
	}
	
	LightState extLightState, skydomeLightState = null, internalLightState;
	
	/**
	 * Creates the lights for a world orbiter
	 * @param o
	 * @return
	 */
	public LightNode[] createLightsForOrbiter(Orbiter o)
	{
		if (o.type==SimpleSun.SIMPLE_SUN_ORBITER) {
			LightNode dirLightNode = new LightNode("Sun light "+o.id, extLightState);		
			DirectionalLight dirLight = new DirectionalLight();
			dirLight.setDiffuse(new ColorRGBA(1,1,1,1));
			dirLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,0.6f));
			dirLight.setDirection(new Vector3f(0,0,1));
			dirLight.setEnabled(true);
			dirLightNode.setLight(dirLight);
			dirLightNode.setTarget(extRootNode);
			dirLight.setShadowCaster(true);
			extLightState.attach(dirLight);

			LightNode pointLightNode = new LightNode("Sun spotlight "+o.id, skydomeLightState);		
			PointLight pointLight = new PointLight();
			pointLight.setDiffuse(new ColorRGBA(1,1,1,0));
			pointLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,0));
			pointLight.setEnabled(true);
			pointLight.setShadowCaster(false);
			pointLightNode.setLight(pointLight);
	        
			return new LightNode[]{dirLightNode,pointLightNode};
		} else
		if (o.type==SimpleMoon.SIMPLE_MOON_ORBITER) {
			LightNode dirLightNode = new LightNode("Moon light "+o.id, extLightState);		
			DirectionalLight dirLight = new DirectionalLight();
			dirLight.setDiffuse(new ColorRGBA(1,1,1,1));
			dirLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,1));
			dirLight.setDirection(new Vector3f(0,0,1));
			dirLight.setShadowCaster(false);//moon shouldnt cast shadow (?)
			dirLight.setEnabled(true);
			dirLightNode.setLight(dirLight);
			dirLightNode.setTarget(extRootNode);
			extLightState.attach(dirLight);

			LightNode pointLightNode = new LightNode("Moon spotlight "+o.id, skydomeLightState);		
			SpotLight pointLight = new SpotLight();
			pointLight.setDiffuse(new ColorRGBA(1,1,1,1));
			pointLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,1));
			pointLight.setDirection(new Vector3f(0,0,1));
			pointLight.setEnabled(true);
			pointLight.setAngle(180);
			pointLight.setShadowCaster(false);
			pointLightNode.setLight(pointLight);
			
			return new LightNode[]{dirLightNode,pointLightNode};
		}
		return null;
			
	}
	
	@Override
	protected void initGame() {
        pManager = new BasicPassManager();
		super.initGame();
	}

	public HashMap<String, Spatial> orbiters3D = new HashMap<String, Spatial>();
	public HashMap<String, LightNode[]> orbitersLight3D = new HashMap<String, LightNode[]>();
	
	public Node groundParentNode = new Node(); 
	/** skyparent for skysphere/sun/moon -> simple water reflection needs this node */
	public Node skyParentNode = new Node(); 
	/** external all root */
	public Node extRootNode;
	/** internal all root */
	public Node intRootNode; 
	/** skyroot */
	//Node skyRootNode = new Node(); 
	Sphere skySphere = null;
	
	/**
	 * Updates all time related things in the 3d world
	 */
	public void updateTimeRelated()
	{
		updateTimeRelated(true);
	}	
	public void updateTimeRelated(boolean modifyLights)
	{
		if (true==false) {
			//cRootNode.clearRenderState(RenderState.RS_LIGHT);
			//cRootNode.setLightCombineMode(LightState.OFF);
			//skydomeLightState.detachAll();
			//cLightState.detachAll();
			return;
		}
		
		
		//map.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		
		Time localTime = engine.getWorldMeanTime().getLocalTime(world, viewPositionX, viewPositionY, viewPositionZ);
		CubeClimateConditions conditions = world.climate.getCubeClimate(localTime, viewPositionX, viewPositionY, viewPositionZ, false);
		uiBase.hud.meter.updateQuad(viewDirection, localTime);
		world.worldMap.update(viewPositionX/world.magnification, viewPositionY/world.magnification, viewPositionZ/world.magnification);
		uiBase.hud.update();

		/*
		 * Orbiters
		 */
		boolean updateRenderState = false;
		float[] vTotal = new float[3];
		// iterating through world's sky orbiters
		for (Orbiter orb : world.getOrbiterHandler().orbiters.values()) {
			if (orbiters3D.get(orb.id)==null)
			{
				Spatial s = createSpatialForOrbiter(orb);
				s.removeFromParent(); // workaround some internal mess
				orbiters3D.put(orb.id, s);
			}
			if (orbitersLight3D.get(orb.id)==null)
			{
				LightNode[] l = createLightsForOrbiter(orb);
				if (l!=null)
					orbitersLight3D.put(orb.id, l);
			}
			Spatial s = orbiters3D.get(orb.id); // get 3d Spatial for the orbiter
			LightNode l[] = orbitersLight3D.get(orb.id);
			float[] orbiterCoords = orb.getCurrentCoordinates(localTime, conditions); // get coordinates of the orbiter
			if (orbiterCoords!=null)
			{
				if (s.getParent()==null)
				{
					// newly appearing, attach to root
					skyParentNode.attachChild(s);
					updateRenderState = true;
				}
				s.setLocalTranslation(new Vector3f(orbiterCoords[0],orbiterCoords[1],orbiterCoords[2]).add(cam.getLocation()));
				//s.updateRenderState();
			}
			else {
				// if there is no coordinates, detach the orbiter
				s.removeFromParent();
			}
			if (l!=null)
			{
				
				float[] lightDirectionCoords = orb.getLightDirection(localTime, conditions);
				if (lightDirectionCoords!=null)
				{
					// 0. is directional light for the planet surface
					l[0].getLight().setEnabled(true);
					((DirectionalLight)l[0].getLight()).setDirection(new Vector3f(lightDirectionCoords[0],lightDirectionCoords[1],lightDirectionCoords[2]).normalizeLocal());
					l[0].setTarget(extRootNode);
					extLightState.attach(l[0].getLight());
					float[] v = orb.getLightPower(localTime, conditions);
					vTotal[0]+=v[0];
					vTotal[1]+=v[1];
					vTotal[2]+=v[2];
					ColorRGBA c = new ColorRGBA(v[0],v[1],v[2],0.6f);
					
					l[0].getLight().setDiffuse(c);//new ColorRGBA(1,1,1,1));
					l[0].getLight().setAmbient(c);
					l[0].getLight().setSpecular(c);
					l[0].getLight().setShadowCaster(true);
					extRootNode.setRenderState(extLightState);

					// 1. is point light for the skysphere
					l[1].getLight().setEnabled(true);
					l[1].setTarget(skySphere);
					skydomeLightState.attach(l[1].getLight());
					c = new ColorRGBA(v[0],v[1],v[2],0.6f);
					l[1].getLight().setDiffuse(c);
					l[1].getLight().setAmbient(c);
					l[1].getLight().setSpecular(c);					
					if (updateRenderState) {
						// this is a workaround, lightstate seems to move to the parent, don't know why. \
						// Clearing it helps:
						skyParentNode.setRenderState(skydomeLightState);
						groundParentNode.clearRenderState(RenderState.RS_LIGHT);
						groundParentNode.updateRenderState();
					}
					l[1].setLocalTranslation(new Vector3f(orbiterCoords[0],orbiterCoords[1],orbiterCoords[2]));
				
				} else 
				
				{
					// switching of the two lights
					l[0].removeFromParent();
					l[0].getLight().setEnabled(false);
					extLightState.detach(l[0].getLight());
					l[1].removeFromParent();
					l[1].getLight().setEnabled(false);
					skydomeLightState.detach(l[1].getLight());
				}
			}
		}

		// this part sets the naive veg quads to a mixed color of the light's value with the quad texture!
		int counter = 0;
		for (Spatial q:hmSolidColorSpatials.values())
		{
			counter++;
			//q.setSolidColor(new ColorRGBA(vTotal+0.2f,vTotal+0.2f,vTotal+0.2f,1));
			if ( (q.getType() & Node.TRIMESH)>0) {
				((TriMesh)q).setSolidColor(new ColorRGBA(vTotal[0]/1.3f,vTotal[1]/1.3f,vTotal[2]/1.3f,1f));
			} else {
				((TriMesh)((Node)q).getChild(0)).setSolidColor(new ColorRGBA(vTotal[0]/1.3f,vTotal[1]/1.3f,vTotal[2]/1.3f,1f));
			}
		}
		// set fog state color to the light power !
		fs_external.setColor(new ColorRGBA(vTotal[0]/2f,vTotal[1]/1.5f,vTotal[2]/1.1f,1f));
		fs_external_special.setColor(new ColorRGBA(vTotal[0]/2f,vTotal[1]/1.5f,vTotal[2]/1.1f,1f));

		// SKYSPHERE
		// moving skysphere with camera
		Vector3f sV3f = new Vector3f(cam.getLocation());
		sV3f.y-=10;
		skySphere.setLocalTranslation(sV3f);
		// Animating skySphere rotated...
		Quaternion qSky = new Quaternion();
		qSky.fromAngleAxis(FastMath.PI*localTime.getCurrentDayPercent()/100, new Vector3f(0,0,-1));
		skySphere.setLocalRotation(qSky);
		

		//if (skyParentNode.getParent()==null)
		{
			groundParentNode.attachChild(skyParentNode);
		}
		if (skySphere.getParent()==null) 
		{
			skyParentNode.attachChild(skySphere);
		}
		if (insideArea)
		{
			skyParentNode.setCullMode(Node.CULL_ALWAYS);
			skySphere.setCullMode(Node.CULL_ALWAYS);
		} else
		{
			skyParentNode.setCullMode(Node.CULL_NEVER);
			skySphere.setCullMode(Node.CULL_NEVER);
		}
		skySphere.updateRenderState(); // do not update root or groundParentNode, no need for that here

		if (updateRenderState) {
			groundParentNode.updateRenderState(); // this is a must, moon will see through the house if not!
		}
		
	}

	public void renderParallel()
	{
		new Thread(this).start();
	}
	
	
	public HashSet<NodePlaceholder> possibleOccluders = new HashSet<NodePlaceholder>();
	
	/**
	 * Removes node and all subnodes from shadowrenderpass. Use it when removing node from scenario!
	 * @param s Node.
	 */
	public void removeOccludersRecoursive(Node s)
	{
		if (s==null) return;
		sPass.removeOccluder(s);
		if (s.getChildren()!=null)
		for (Spatial c:s.getChildren())
		{
			if ((c.getType()&Node.NODE)>0)
			{
				removeOccludersRecoursive((Node)c);
			}
		}
	}

	/**
	 * Removes node and all subnodes from solid color quads. Use it when removing node from scenario!
	 * @param s Node.
	 */
	public void removeSolidColorQuadsRecoursive(Node s)
	{
		/*if (s instanceof BillboardPartVegetation)
		{
			hmSolidColorSpatials.remove(((BillboardPartVegetation)s).targetQuad);
			((BillboardPartVegetation)s).targetQuad = null;
		}*/
		{
			hmSolidColorSpatials.remove(s);
			((Node)s).removeUserData("rotateOnSteep");
		}
		if (s.getChildren()!=null)
		for (Spatial c:s.getChildren())
		{
			if (c instanceof BillboardPartVegetation)
			{
				hmSolidColorSpatials.remove(((BillboardPartVegetation)c).targetQuad);
				((BillboardPartVegetation)c).targetQuad = null;
				
			}
			if ((c.getType()&Node.NODE)>0)
			{
				hmSolidColorSpatials.remove(c);
				removeSolidColorQuadsRecoursive((Node)c);
				((Node)c).removeUserData("rotateOnSteep");
			}
			if ((c.getType()&Spatial.TRIMESH)>0)
			{
				hmSolidColorSpatials.remove(c);
				//c.removeFromParent();
			}
			/*for (int i=0; i<RenderState.RS_MAX_STATE; i++)
			{
				//c.getRenderState(i);
				
				c.clearRenderState(i);
			}*/
		}
	}

	public int lastRenderX,lastRenderY,lastRenderZ;

	int garbCollCounter = 0;

	Text loadText;
	Quad loadFloppy;
	int cPercent = 0;
	public void loadingText(int percent, boolean state)
	{
		if (true) return;
        if (state)
        	hud1Node.attachChild(bbFloppy);
        else
        	hud1Node.detachChild(bbFloppy);
        bbFloppy.setLocalTranslation(getCurrentLocation().add(2,2,2));
        hud1Node.updateGeometricState(0.0f, true);
        hud1Node.updateRenderState();
	}
	
	
	/**
	 * Renders the scenario, adds new jme Nodes, removes outmoved nodes and keeps old nodes on scenario.
	 */
	public HashSet<RenderedCube> render()
	{
		HashSet<RenderedCube> detacheable = new HashSet<RenderedCube>();
		
		modelLoader.setLockForSharedNodes(false);
    	//loadingText(0,true);
		
		uiBase.hud.sr.setVisibility(true, "LOAD");
    	updateDisplay(null);

		/*lastRenderX = viewPositionX;
		lastRenderY = viewPositionY;
		lastRenderZ = viewPositionZ;*/
		lastRenderX = relativeX;
		lastRenderY = relativeY;
		lastRenderZ = relativeZ;

		// start to collect the nodes/binaries which this render will use now
		modelLoader.startRender();
		long timeS = System.currentTimeMillis();
		
		System.out.println("**** RENDER ****");
		
		int already = 0;
		int newly = 0;
		int removed = 0;

		Time localTime = engine.getWorldMeanTime().getLocalTime(world, viewPositionX, viewPositionY, viewPositionZ);
		CubeClimateConditions conditions = world.climate.getCubeClimate(localTime, viewPositionX, viewPositionY, viewPositionZ, false);
		
		
		if (conditions!=null) System.out.println("- "+conditions.getBelt()+" \n - "+ conditions.getSeason()+" \n"+ conditions.getDayTime());

		
		/*
		 * Render cubes
		 */
		
    	// get a specific part of the area to render
		System.out.println("1-RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));
    	RenderedCube[][] newAndOldCubes = renderedArea.getRenderedSpace(world, viewPositionX, viewPositionY, viewPositionZ,viewDirection);
    	RenderedCube[] cubes = newAndOldCubes[0];
    	RenderedCube[] removableCubes = newAndOldCubes[1];
    	System.out.println("!!!! REMOVABLE CUBES = "+removableCubes.length);
    	for (RenderedCube c:removableCubes)
    	{
    		if (c==null) continue;
    		Integer cubeKey = Boundaries.getKey(c.cube.x,c.cube.y,c.cube.z);
    		c = hmCurrentCubes.get(cubeKey);
    		detacheable.add(c);
    		liveNodes-= c.hsRenderedNodes.size();
    	}
    	
		System.out.println("1-RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));

    	System.out.println("getRenderedSpace size="+cubes.length);
		
		HashMap<Integer, RenderedCube> hmNewCubes = new HashMap<Integer, RenderedCube>();

		System.out.println("hmCurrentCubes: "+hmCurrentCubes.keySet().size());
		
	    for (int i=0; i<cubes.length; i++)
		{
			//System.out.println("CUBE "+i);
			RenderedCube c = cubes[i];
			Integer cubeKey = Boundaries.getKey(c.cube.x,c.cube.y,c.cube.z);
			if (hmCurrentCubes.containsKey(cubeKey))
			{
				already++;
				// yes, we have it rendered...
				// remove to let the unrendered ones in the hashmap for after removal from space of cRootNode
				RenderedCube cOrig = hmCurrentCubes.remove(cubeKey);
				
				// add to the new cubes, it is rendered already
				hmNewCubes.put(cubeKey,cOrig); // keep cOrig with jme nodes!!
				continue;				
			}
			newly++;
			// render the cube newly
			Side[][] sides = c.cube.sides;
			for (int j=0; j<sides.length; j++)
			{
				if (sides[j]!=null) {
					for (int k=0; k<sides[j].length; k++) {
						renderSide(c,c.renderedX, c.renderedY, c.renderedZ, j, sides[j][k],false); // fake = false !
					}
				}
			}
			// store it to new cubes hashmap
			hmNewCubes.put(cubeKey,c);
		}
		System.out.println("hmCurrentCubes: "+hmCurrentCubes.keySet().size());
	    for (RenderedCube cToDetach:hmCurrentCubes.values())
	    {
			removed++;
    		outOfViewPort.remove(cToDetach);
    		inViewPort.remove(cToDetach);
	    	cToDetach.hsRenderedNodes.clear(); // clear references to nodePlaceholders
	    }
	    hmCurrentCubes.clear();
	    hmCurrentCubes = hmNewCubes; // the newly rendered/remaining cubes are now the current cubes
		
		fpsNode.detachChild(loadText);

		System.out.println("RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));

		modelLoader.setLockForSharedNodes(true);
		
		// stop to collect and clean the nodes/binaries which this render will not use now
		modelLoader.stopRenderAndClear();

		//loadingText(0,false);
    	//updateDisplay(null);

		//TextureManager.clearCache();
		//System.gc();
		System.out.println(" ######################## LIVE NODES = "+liveNodes + " --- LIVE HM QUADS "+hmSolidColorSpatials.size());
		uiBase.hud.sr.setVisibility(false, "LOAD");
		return detacheable;
	}

	/**
	 * Renders a set of node into 3d space, rotating, positioning them.
	 * @param n Nodes
	 * @param cube the r.cube parent of the nodes, needed for putting the rendered node as child into it.
	 * @param x X cubesized distance from current relativeX
	 * @param y Y cubesized distance from current relativeY
	 * @param z Z cubesized distance from current relativeZ
	 * @param direction Direction
	 * @param horizontalRotation Horizontal rotation
	 * @param scale Scale
	 */
	private void renderNodes(NodePlaceholder[] n, RenderedCube cube, int x, int y, int z, int direction, int horizontalRotation, float scale)
	{
		//int s = (x << 16) + (y << 8) + z;
		//String coordKey = ""+s;
		
		if (n==null) return;
		Object[] f = (Object[])directionAnglesAndTranslations.get(new Integer(direction));
		float cX = ((x+relativeX)*CUBE_EDGE_SIZE+1*((int[])f[1])[0]);//+0.5f;
		float cY = ((y+relativeY)*CUBE_EDGE_SIZE+1*((int[])f[1])[1]);//+0.5f;
		float cZ = ((z-relativeZ)*CUBE_EDGE_SIZE+1*((int[])f[1])[2]);//+25.5f;
		
		Quaternion hQ = null;
		Quaternion hQReal = null;
		if (horizontalRotation!=-1) {
			hQ = horizontalRotations.get(new Integer(horizontalRotation));
			hQReal = horizontalRotationsReal.get(new Integer(horizontalRotation));
		}
		
		//Node sideNode = new Node();
	
		for (int i=0; i<n.length; i++) {
			n[i].setLocalTranslation(new Vector3f(cX,cY,cZ));
			Quaternion q = (Quaternion)f[0];
			Quaternion qC = null;
			if (n[i].model.noSpecialSteepRotation) {
				qC = new Quaternion(q); // base rotation
			} else
			{
				qC = new Quaternion();
			}
			if (hQ!=null)
			{
				n[i].horizontalRotation = hQReal;
				// horizontal rotation
				qC.multLocal(hQ);
			} 
			
			// steep rotation part...
			if (n[i].getUserData("rotateOnSteep")!=null) {
				// model loader did set a rotateOnSteep object, which means, that node can be rotated on a steep,
				// so let's do it if we are on a steep...
				if (cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP)
				{
					// yes, this is a steep:
					
					// mult with steep rotation quaternion for the steep direction...
					if (n[i].model.noSpecialSteepRotation) 
					{	try {
							qC.multLocal(steepRotations.get(cube.cube.steepDirection));
						}catch (Exception ex)
						{
							System.out.println(cube.cube + " --- "+cube.cube.steepDirection);
						}
					} else 
					{
						qC = steepRotations_special.get(cube.cube.steepDirection);
					}
					// the necessary local translation : half cube up
					Vector3f newTrans = n[i].getLocalTranslation().add(new Vector3f(0f,CUBE_EDGE_SIZE/2,0f));
					n[i].setLocalTranslation(newTrans);

					// square root 2 is the scaling for that side, so we will set it depending on N-S or E-W steep direction
					if (cube.cube.steepDirection==NORTH||cube.cube.steepDirection==SOUTH)
					{
						// NORTH-SOUTH steep...
						if (n[i].model.noSpecialSteepRotation) 
						{
							n[i].setLocalScale(new Vector3f(1f,1.41421356f,1f));
						} else
						{
							n[i].setLocalScale(new Vector3f(1.41421356f,1,1f));							
						}
					}
					else
					{
						// EAST-WEST steep...
						n[i].setLocalScale(new Vector3f(1.41421356f,1,1f));
					}
				} else
				{
					n[i].setLocalScale(1);
				}
			} else
			{				
				n[i].setLocalScale(scale);
			}
			
			n[i].setLocalRotation(qC);

			cube.hsRenderedNodes.add((NodePlaceholder)n[i]);
			liveNodes++;
			
		}
	}
	
	HashSet<RenderedCube> inViewPort = new HashSet<RenderedCube>();
	HashSet<RenderedCube> inFarViewPort = new HashSet<RenderedCube>();
	HashSet<RenderedCube> outOfViewPort = new HashSet<RenderedCube>();
	
	int cullVariationCounter = 0;
	
	public static boolean OPTIMIZE_ANGLES = true;
	public static float ROTATE_VIEW_ANGLE = OPTIMIZE_ANGLES?2.5f:3.14f;

	public static boolean GEOMETRY_BATCH = true;
	public static boolean GRASS_BIG_BATCH = true;

	public void renderToViewPort()
	{
		renderToViewPort(OPTIMIZE_ANGLES?1.1f:3.14f);
	}
	public void renderToViewPort(int segmentCount, int segments)
	{
		renderToViewPort(OPTIMIZE_ANGLES?1.1f:3.14f, true, segmentCount, segments);
	}
	public void renderToViewPort(float refAngle)
	{
		renderToViewPort(refAngle, false, 0,0);
	}
	public void renderToViewPort(float refAngle, boolean segmented, int segmentCount, int segments)
	{
		engine.setPause(true);
		
		
		Vector3f lastLoc = new Vector3f(lastRenderX*CUBE_EDGE_SIZE,lastRenderY*CUBE_EDGE_SIZE,lastRenderZ*CUBE_EDGE_SIZE);
		Vector3f currLoc = new Vector3f(relativeX*CUBE_EDGE_SIZE,relativeY*CUBE_EDGE_SIZE,relativeZ*CUBE_EDGE_SIZE);
		int mulWalkDist = 1;
		//if (J3DCore.FARVIEW_ENABLED) mulWalkDist = 2; // if farview , more ofter render is added by this multiplier
		if (lastLoc.distance(currLoc)*mulWalkDist > (RENDER_DISTANCE*CUBE_EDGE_SIZE)-VIEW_DISTANCE)
		{
			// doing the render, getting the unneeded renderedCubes too.
			HashSet<RenderedCube> detacheable = render();
			// removing the unneeded.
			for (RenderedCube c:detacheable) { 
	    		if (c!=null) {
    	    		inViewPort.remove(c);
    	    		inFarViewPort.remove(c);
    	    		outOfViewPort.remove(c);
	    	    	for (Iterator<NodePlaceholder> itNode = c.hsRenderedNodes.iterator(); itNode.hasNext();)
	    	    	{
	    	    		NodePlaceholder n = itNode.next();
	    	    		
	    				if (GEOMETRY_BATCH && n.model.batchEnabled && 
	    						(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
   	    						//(n.model.type == Model.SIMPLEMODEL
	    								|| GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
	    					 )
	    				{
	    					if (n!=null && n.batchInstance!=null)
	    						batchHelper.removeItem(c.cube.internalCube, n.model, n, n.farView);
	    				} else 
	    				{ 
							PooledNode pooledRealNode = n.realNode;
							
							n.realNode = null;
							if (pooledRealNode!=null) {
								Node realNode = (Node)pooledRealNode;
								if (SHADOWS) removeOccludersRecoursive(realNode);
								realNode.removeFromParent();
								modelPool.releaseNode(pooledRealNode);
							}
	    				}
	    				n.farView = false;
	    	    	}
	    		}
			}

		}
		
		long sysTime = System.currentTimeMillis();
		
		int visibleNodeCounter = 0;
		int nonVisibleNodeCounter = 0;
		int addedNodeCounter = 0;
		int removedNodeCounter = 0;
		
		
		if (segmented && segmentCount==0 || !segmented)
		{
			alCurrentCubes.clear();
			alCurrentCubes.addAll(hmCurrentCubes.values());
		}
		int fromCubeCount = 0; int toCubeCount = alCurrentCubes.size();
		if (segmented)
		{
			int sSize = alCurrentCubes.size()/segments;
			fromCubeCount = sSize*segmentCount;
			toCubeCount = sSize*(segmentCount+1);
			if (toCubeCount>alCurrentCubes.size())
			{
				toCubeCount = alCurrentCubes.size();
			}
		}
		
		for (int cc = fromCubeCount; cc<toCubeCount; cc++)
		{
			RenderedCube c = alCurrentCubes.get(cc);
			// TODO farview selection, only every 10th x/z based on coordinates -> do scale up in X/Z direction only
			if (c.hsRenderedNodes.size()>0)
			{
				boolean found = false;
				boolean foundFar = false;
				// OPTIMIZATION: if inside and not insidecube is checked, or outside and not outsidecube -> view distance should be fragmented:
				boolean fragmentViewDist = false;
				if (c.cube!=null) {
					fragmentViewDist = c.cube.internalCube&&(!insideArea) || (!c.cube.internalCube)&&insideArea;
				}

				int checkDistCube = (fragmentViewDist?VIEW_DISTANCE/4 : VIEW_DISTANCE/2);
				boolean checked = false;
				int distX = Math.abs(viewPositionX-c.cube.x);
				int distY = Math.abs(viewPositionY-c.cube.y);
				int distZ = Math.abs(viewPositionZ-c.cube.z);
				
				// handling the globe world border cube distances...
				if (distX>world.realSizeX/2)
				{
					if (viewPositionX<world.realSizeX/2) {
						distX = Math.abs(viewPositionX - (c.cube.x - world.realSizeX) );
					} else
					{
						distX = Math.abs(viewPositionX - (c.cube.x + world.realSizeX) );
					}
				}
				if (distZ>world.realSizeZ/2)
				{
					if (viewPositionZ<world.realSizeZ/2) {
						distZ = Math.abs(viewPositionZ - (c.cube.z - world.realSizeZ) );
					} else
					{
						distZ = Math.abs(viewPositionZ - (c.cube.z + world.realSizeZ) );	
					}
				}
				
				
				// checking the view distance of the cube from viewpoint
				if (distX<=checkDistCube && distY<=checkDistCube && distZ<=checkDistCube)
				{
					// inside view dist...
					checked = true;
				} else
				{
					//System.out.println("DIST X,Z: "+distX+" "+distZ);
				}
				//checked = true;
				
				// this tells if a not in farview cube can be a farview cube
				// regardless its position, to cover the gap between farview part and normal view part:
				boolean farviewGapFiller = false; 
				
				if (checked && J3DCore.FARVIEW_ENABLED)
				{
					int viewDistFarViewModuloX = viewPositionX%FARVIEW_GAP;
					int viewDistFarViewModuloZ = viewPositionZ%FARVIEW_GAP;
					
					if (Math.abs(checkDistCube-distX)<=viewDistFarViewModuloX)
					{
						farviewGapFiller = true;
					}
					if (Math.abs(checkDistCube-distZ)<=viewDistFarViewModuloZ)
					{
						farviewGapFiller = true;
					}
					if (c.cube.x%FARVIEW_GAP==0 && c.cube.z%FARVIEW_GAP==0)
					{
						// this can be a gapfiller magnified farview cube.
					} else
					{
						//this cannot be
						farviewGapFiller = false;
					}
				} 
				
				for (NodePlaceholder n : c.hsRenderedNodes)
				{
					if (checked && !farviewGapFiller)
					{
						float dist = n.getLocalTranslation().distanceSquared(cam.getLocation());

						if (dist<CUBE_EDGE_SIZE*CUBE_EDGE_SIZE*6) {
							found = true;
							break;
						}
						Vector3f relative = n.getLocalTranslation().subtract(cam.getLocation()).normalize();
						float angle = cam.getDirection().normalize().angleBetween(relative);
						//System.out.println("RELATIVE = "+relative+ " - ANGLE = "+angle);
						if (angle<refAngle) {
							found = true;
						}
						break;
					} else
					{
						// check if farview enabled
						if (!J3DCore.FARVIEW_ENABLED || fragmentViewDist) break;
						
						// enabled, we can check for the cube coordinates in between the gaps...
						if (c.cube.x%FARVIEW_GAP==0 && c.cube.z%FARVIEW_GAP==0)// || c.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP)
						{
							// looking for farview enabled model on the cube...
							if (n.model.farViewEnabled)
							{								
								//if (c.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP) {
									//foundFar = false;
									//found = true;
									//break;
								//}
								// found one... checking for angle:								
								Vector3f relative = n.getLocalTranslation().subtract(cam.getLocation()).normalize();
								float angle = cam.getDirection().normalize().angleBetween(relative);
								//System.out.println("RELATIVE = "+relative+ " - ANGLE = "+angle);
								if (angle<refAngle) {
									// angle is good, we can enable foundFar for this cube
									foundFar = true;
								}
								break;
							} else
							{
								// continue to check all the other nodes of the cube in this farview place.
								continue;
							}
						}
						break;
					}
				}
				
				
				// farview
				if (foundFar)
				{
					visibleNodeCounter++;
					if (!inFarViewPort.contains(c)) 
					{
						addedNodeCounter++;
						inFarViewPort.add(c);
						
						// checking if its in normal view port, if so removing it
						if (inViewPort.contains(c))
						{
							removedNodeCounter++;			
							for (NodePlaceholder n : c.hsRenderedNodes)
							{								
								if (GEOMETRY_BATCH && n.model.batchEnabled && 
										(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
												|| GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
									 )
								{
									if (n!=null)
										batchHelper.removeItem(c.cube.internalCube, n.model, n, n.farView);
								} else 
								{
									PooledNode pooledRealNode = n.realNode;
									
									n.realNode = null;
									if (pooledRealNode!=null) {
										Node realNode = (Node)pooledRealNode;
										if (SHADOWS) removeOccludersRecoursive(realNode);
										realNode.removeFromParent();
										modelPool.releaseNode(pooledRealNode);
									}
								}
								n.farView = false;
							}
						}
						inViewPort.remove(c);
						outOfViewPort.remove(c);
						
						// add all far view enabled model nodes to the scenario
						for (NodePlaceholder n : c.hsRenderedNodes)
						{
							if (!n.model.farViewEnabled) continue;
							n.farView = true;
							if (GEOMETRY_BATCH && n.model.batchEnabled && 
									(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
									//(n.model.type == Model.SIMPLEMODEL
											|| GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
								) 
							{
								
								if (n.batchInstance==null)
									batchHelper.addItem(c.cube.internalCube, n.model, n, true);
							} else 
							{
								Node realPooledNode = (Node)modelPool.getModel(c, n.model, n);
								if (realPooledNode==null) continue;
								n.realNode = (PooledNode)realPooledNode;
							
								// unlock
								boolean sharedNode = false;
								if (realPooledNode instanceof SharedNode)
								{	
									realPooledNode.unlockMeshes();
									sharedNode = true;
								}
								{
									realPooledNode.unlockShadows();
									realPooledNode.unlockTransforms();
									realPooledNode.unlockBounds();
									realPooledNode.unlockBranch();
								}
							
								// set data from placeholder
								realPooledNode.setLocalTranslation(n.getLocalTranslation());
								// detailed loop through children, looking for TrimeshGeometryBatch preventing setting localRotation
								// on it, because its rotation is handled by the TrimeshGeometryBatch's billboarding.
								for (Spatial s:realPooledNode.getChildren()) {
									if ( (s.getType()&Node.NODE)>0 )
									{
										for (Spatial s2:((Node)s).getChildren())
										{	
											if ( (s2.getType()&Node.NODE)>0 )
											{
												for (Spatial s3:((Node)s2).getChildren())
												{
													if (s3 instanceof TrimeshGeometryBatch) {
														// setting separate horizontalRotation for trimeshGeomBatch
														((TrimeshGeometryBatch)s3).horizontalRotation = n.horizontalRotation;
													}												
												}												
											}
											s2.setLocalScale(n.getLocalScale());
											if (s2 instanceof TrimeshGeometryBatch) {
												// setting separate horizontalRotation for trimeshGeomBatch
												((TrimeshGeometryBatch)s2).horizontalRotation = n.horizontalRotation;
											} else {
												s2.setLocalRotation(n.getLocalRotation());
											}
										}
									} else {
										s.setLocalRotation(n.getLocalRotation());
										Vector3f scale = new Vector3f(n.getLocalScale());
										scale.x*=FARVIEW_GAP;
										scale.z*=FARVIEW_GAP;
										s.setLocalScale(scale);
									}
								}
							
								if (c.cube.internalCube) {
									intRootNode.attachChild((Node)realPooledNode);
								} else 
								{
									extRootNode.attachChild((Node)realPooledNode);
								}
								if (sharedNode)
								{	
									realPooledNode.lockMeshes();
								}
								{
									realPooledNode.lockShadows();
									realPooledNode.lockBranch();
									realPooledNode.lockBounds();
									realPooledNode.lockTransforms();																	
								}
							}
						}
					} 
				} else
				if (found)
				{
					visibleNodeCounter++;
					if (!inViewPort.contains(c)) 
					{
						addedNodeCounter++;
						inViewPort.add(c);
						if (inFarViewPort.contains(c))
						{
							removedNodeCounter++;							
							for (NodePlaceholder n : c.hsRenderedNodes)
							{
								if (!n.model.farViewEnabled) continue;
								if (GEOMETRY_BATCH && n.model.batchEnabled && 
										(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
												|| GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
									 )
								{
									if (n!=null)
										batchHelper.removeItem(c.cube.internalCube, n.model, n, true);
								} else 
								{
									PooledNode pooledRealNode = n.realNode;
									
									n.realNode = null;
									if (pooledRealNode!=null) {
										Node realNode = (Node)pooledRealNode;
										if (SHADOWS) removeOccludersRecoursive(realNode);
										realNode.removeFromParent();
										modelPool.releaseNode(pooledRealNode);
									}
								}
								n.farView = false;
							}
						}

						inFarViewPort.remove(c);
						outOfViewPort.remove(c);
						for (NodePlaceholder n : c.hsRenderedNodes)
						{
							n.farView = false;
							if (GEOMETRY_BATCH && n.model.batchEnabled && 
									(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
									//(n.model.type == Model.SIMPLEMODEL
											|| GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
								) 
							{
								
								if (n.batchInstance==null)
									batchHelper.addItem(c.cube.internalCube, n.model, n, false);
							} else 
							{
								Node realPooledNode = (Node)modelPool.getModel(c, n.model, n);
								if (realPooledNode==null) continue;
								n.realNode = (PooledNode)realPooledNode;
							
								// unlock
								boolean sharedNode = false;
								if (realPooledNode instanceof SharedNode)
								{	
									realPooledNode.unlockMeshes();
									sharedNode = true;
								}
								{
									realPooledNode.unlockShadows();
									realPooledNode.unlockTransforms();
									realPooledNode.unlockBounds();
									realPooledNode.unlockBranch();
								}
							
								// set data from placeholder
								realPooledNode.setLocalTranslation(n.getLocalTranslation());
								// detailed loop through children, looking for TrimeshGeometryBatch preventing setting localRotation
								// on it, because its rotation is handled by the TrimeshGeometryBatch's billboarding.
								for (Spatial s:realPooledNode.getChildren()) {
									if ( (s.getType()&Node.NODE)>0 )
									{
										for (Spatial s2:((Node)s).getChildren())
										{	
											if ( (s2.getType()&Node.NODE)>0 )
											{
												for (Spatial s3:((Node)s2).getChildren())
												{
													if (s3 instanceof TrimeshGeometryBatch) {
														// setting separate horizontalRotation for trimeshGeomBatch
														((TrimeshGeometryBatch)s3).horizontalRotation = n.horizontalRotation;
													}												
												}												
											}
											s2.setLocalScale(n.getLocalScale());
											if (s2 instanceof TrimeshGeometryBatch) {
												// setting separate horizontalRotation for trimeshGeomBatch
												((TrimeshGeometryBatch)s2).horizontalRotation = n.horizontalRotation;
											} else {
												s2.setLocalRotation(n.getLocalRotation());
											}
										}
									} else {
										s.setLocalRotation(n.getLocalRotation());
										s.setLocalScale(n.getLocalScale());
									}
								}
							
								if (c.cube.internalCube) {
									intRootNode.attachChild((Node)realPooledNode);
								} else 
								{
									extRootNode.attachChild((Node)realPooledNode);
								}
								if (sharedNode)
								{	
									realPooledNode.lockMeshes();
									
								}
								{
									if (n.model.type==Model.PARTLYBILLBOARDMODEL)
									{
										for (Spatial s:realPooledNode.getChildren())
										{
											//s.lockBounds();
										}
									}
									realPooledNode.lockShadows();
									realPooledNode.lockTransforms();								
									realPooledNode.lockBranch();
									realPooledNode.lockBounds();
								}
							}
						}
					} 
				}
				else
				{
					 nonVisibleNodeCounter++;
					 if (!outOfViewPort.contains(c)) 
					 {
						removedNodeCounter++;
						outOfViewPort.add(c);
						inViewPort.remove(c);
						inFarViewPort.remove(c);
						for (NodePlaceholder n : c.hsRenderedNodes)
						{
							if (GEOMETRY_BATCH && n.model.batchEnabled && 
									(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
											|| GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
								 )
							{
								if (n!=null)
									batchHelper.removeItem(c.cube.internalCube, n.model, n, n.farView);
							} else 
							{
								PooledNode pooledRealNode = n.realNode;
								
								n.realNode = null;
								if (pooledRealNode!=null) {
									Node realNode = (Node)pooledRealNode;
									if (SHADOWS) removeOccludersRecoursive(realNode);
									realNode.removeFromParent();
									modelPool.releaseNode(pooledRealNode);
								}
							}
							n.farView = false;
						}
						
					 }
				}
			}
		}
		if (segmentCount==segments-1 || !segmented) {
			
			if (GEOMETRY_BATCH) batchHelper.updateAll();
			
			System.out.println("J3DCore.renderToViewPort: visilbe nodes = "+visibleNodeCounter + " nonV = "+nonVisibleNodeCounter+ " ADD: "+addedNodeCounter+ " RM: "+removedNodeCounter);
		    // handling possible occluders
		    if (SHADOWS) {
		    	System.out.println("OCCS: "+sPass.occludersSize());
				for (NodePlaceholder psn : possibleOccluders) {
					if (psn.realNode != null) {
						Node n = (Node) psn.realNode;
						float dist = n.getWorldTranslation().distanceSquared(
								cam.getLocation());
						if (dist < J3DCore.RENDER_SHADOW_DISTANCE_SQR) {
							if (!sPass.containsOccluder(n))
							{
								System.out.println("ADDING OCCLUDER: "+n.getName());
								sPass.addOccluder(n);
								
							}
						} else {
							removeOccludersRecoursive(n);
						}
					}
				}
		    }
		    
		    System.out.println("rtoviewport time: "+(System.currentTimeMillis()-sysTime));
		    sysTime = System.currentTimeMillis();
		    
		    
			updateTimeRelated();
	
			cullVariationCounter++;
			groundParentNode.setCullMode(Node.CULL_NEVER);
			updateDisplayNoBackBuffer();
			groundParentNode.setCullMode(Node.CULL_INHERIT);
			if (cullVariationCounter%1==0) 
			{
				groundParentNode.updateRenderState();
			} else
			{
				//updateDisplayNoBackBuffer();
			}
	
			System.out.println("CAMERA: "+cam.getLocation()+ " NODES EXT: "+(extRootNode.getChildren()==null?"-":extRootNode.getChildren().size()));
		    System.out.println("crootnode cull update time: "+(System.currentTimeMillis()-sysTime));
		    System.out.println("hmSolidColorSpatials:"+hmSolidColorSpatials.size());
	
		    if (cullVariationCounter%30==0) {
				modelPool.cleanPools();
				System.gc();
			}
	
			// every 20 steps do a garbage collection
			garbCollCounter++;
			if (garbCollCounter==20) {
				//
				garbCollCounter = 0;
			}
		}
		
		engine.setPause(false);
	}
	
	private void renderNodes(NodePlaceholder[] n, RenderedCube cube, int x, int y, int z, int direction)
	{
		renderNodes(n, cube, x, y, z, direction, -1, 1f);
	}
	
	public int liveNodes = 0;
	
	/**
	 * Renders one side into 3d space percepting what kind of RenderedSide it is.
	 * @param cube
	 * @param x
	 * @param y
	 * @param z
	 * @param direction
	 * @param side
	 * @param fakeLoadForCacheMaint No true rendering if this is true, only fake loading the objects through model loader.
	 */
	public void renderSide(RenderedCube cube,int x, int y, int z, int direction, Side side, boolean fakeLoadForCacheMaint)
	{
		if (side==null||side.subtype==null) return;
		Integer n3dType = hmAreaSubType3dType.get(side.subtype.id);
		if (n3dType==null) return;
		if (n3dType.equals(EMPTY_SIDE)) return;
		RenderedSide renderedSide = hm3dTypeRenderedSide.get(n3dType);
		
		
		NodePlaceholder[] n = modelPool.loadPlaceHolderObjects(cube,renderedSide.objects,fakeLoadForCacheMaint);
		if (!fakeLoadForCacheMaint) {
			if (renderedSide.type == RenderedSide.RS_HASHROTATED)
			{
				int rD = ((RenderedHashRotatedSide)renderedSide).rotation(cube.cube.x, cube.cube.y, cube.cube.z);
				float scale = ((RenderedHashRotatedSide)renderedSide).scale(cube.cube.x, cube.cube.y, cube.cube.z);
				renderNodes(n, cube, x, y, z, direction, rD,scale);
			} 
			else
			if (renderedSide.type == RenderedSide.RS_HASHALTERED)
			{
				renderNodes(n, cube, x, y, z, direction);
				Model[] m = ((RenderedHashAlteredSide)renderedSide).getRenderedModels(cube.cube.x, cube.cube.y, cube.cube.z, cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP);
				NodePlaceholder[] n2 = modelPool.loadPlaceHolderObjects(cube,m,fakeLoadForCacheMaint);
				if (n2.length>0)
					renderNodes(n2, cube, x, y, z, direction);
			} 
			else
			{
				renderNodes(n, cube, x, y, z, direction);
			}
		}

		Cube checkCube = null;
		if (direction==TOP && renderedSide.type == RenderedSide.RS_TOPSIDE) // Top Side
		{
			if (cube.cube.getNeighbour(TOP)!=null)
			{
				// if there is a side of the same kind above the current side, 
				//  we don't need continuous side rendering
				if (cube.cube.getNeighbour(TOP).hasSideOfType(direction,side.type))
				{
					return;					
				}
				
			} else {
				//System.out.println("# TOP IS NULL!");
			}
			boolean render = true;
			// Check if there is no same cube side type near in any direction, so we can safely put the Top objects on, no bending roofs are near...
			for (int i=NORTH; i<=WEST; i++)
			{
				Cube n1 = cube.cube.getNeighbour(i);
				if (n1!=null)
				{
					if (n1.hasSideOfType(i,side.type) || n1.hasSideOfType(oppositeDirections.get(new Integer(i)).intValue(),side.type))
					{
						render = false; break;
					}
				} 
			}
			if (render)
			{
				n= modelPool.loadPlaceHolderObjects(cube,((RenderedTopSide)renderedSide).nonEdgeObjects,fakeLoadForCacheMaint);
				if (!fakeLoadForCacheMaint)
				{
					renderNodes(n, cube, x, y, z, direction);
				}
			}
		}
		if (direction!=TOP && direction!=BOTTOM && renderedSide.type == RenderedSide.RS_CONTINUOUS) // Continuous side
		{
			int dir = nextDirections.get(new Integer(direction)).intValue();
			if (cube.cube.getNeighbour(TOP)!=null)
			{
				// if there is a side of the same kind above the current side, 
				//  we don't need continuous side rendering
				if (cube.cube.getNeighbour(TOP).hasSideOfType(direction,side.type))
				{
					return;					
				}
				
			}
			if (cube.cube.getNeighbour(dir)!=null)
			if (cube.cube.getNeighbour(dir).hasSideOfType(direction,side.type))
			{
				checkCube = cube.cube.getNeighbour(oppositeDirections.get(new Integer(dir)).intValue());
				if (checkCube !=null) {
					if (checkCube.hasSideOfType(direction,side.type))
					{
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).continuous, fakeLoadForCacheMaint );
						if (!fakeLoadForCacheMaint)
						{
							renderNodes(n, cube, x, y, z, direction);
						}
					} else
					{
						// normal direction is continuous
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousNormal, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint)
						{
							renderNodes(n, cube, x, y, z, direction);
						}
					}
				} else
				{
					// normal direction is continuous
					n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousNormal, fakeLoadForCacheMaint );
					if (!fakeLoadForCacheMaint)
					{
						renderNodes(n, cube, x, y, z, direction);
					}
				}
				
			} else 
			{
				checkCube = cube.cube.getNeighbour(oppositeDirections.get(new Integer(dir)).intValue());
				if (checkCube!=null)
				{
					if (checkCube.hasSideOfType(direction, side.type))
					{
						// opposite to normal direction is continuous 
						// normal direction is continuous
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
					
					}else
					{
						// no continuous side found
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).nonContinuous, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
					}
				} else {
					// opposite to normal direction is continuous 
					// normal direction is continuous
					n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
					if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
				}
			} else {
				// opposite to normal direction is continuous 
				// normal direction is continuous
				n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
				if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
			}
			
		}
		
		
	}
	
	/**
	 * Sets the camera to its proper current position
	 */
	public void setCalculatedCameraLocation()
	{
		cam.setLocation(getCurrentLocation());
		if (J3DCore.WATER_SHADER)
		{
			waterEffectRenderPass.setWaterHeight(cam.getLocation().y);
		}
	}
	
	public Vector3f getCurrentLocation()
	{
		Vector3f v = new Vector3f(relativeX*CUBE_EDGE_SIZE,relativeY*CUBE_EDGE_SIZE+0.11f+(onSteep?1.5f:0f),-1*relativeZ*CUBE_EDGE_SIZE);
		return v;
	}
	
	
	/**
	 * Tells if any of a set of sides is of a set of sideSubTypes. 
	 * @param sides
	 * @param classNames
	 * @return
	 */
	public boolean hasSideOfInstance(Side[] sides, HashSet<Class> classNames)
	{
		if (sides!=null)
		for (int i=0; i<sides.length; i++)
		{
			if (sides[i]!=null)
			{
				//System.out.println("SIDE SUBTYPE: "+sides[i].subtype.getClass().getCanonicalName());
				
				if (classNames.contains(sides[i].subtype.getClass()))
				{
					return true;
				}
			}
		}
		return false;
		
	}

	/**
	 * Tells if the cube has any side of a set of sideSubTypes.
	 * @param c
	 * @param classNames
	 * @return
	 */
	public int hasSideOfInstanceInAnyDir(Cube c, HashSet<Class> classNames)
	{
		for (int j=0; j<c.sides.length; j++)
		{
			Side[] sides = c.sides[j];
			if (sides!=null)
			for (int i=0; i<sides.length; i++)
			{
				if (sides[i]!=null)
				{
					//System.out.println("SIDE SUBTYPE: "+sides[i].subtype.getClass().getCanonicalName());
					if (classNames.contains(sides[i].subtype.getClass()))
					{
						return j;
					}
				}
			}
		}
		return -1;
		
	}
	
	
	public static J3DCore self;
	
	public static J3DCore getInstance()
	{
		return self;
	}
	
	/**
	 * The base movement method.
	 * @param direction The direction to move.
	 */
	public int[] calcMovement(int[] orig, int direction, boolean limitsCut)
	{
		int[] r = new int[3];
		int[] vector = moveTranslations.get(new Integer(direction));
		r[0] = orig[0]+vector[0];
		r[1] = orig[1]+vector[1];
		r[2] = orig[2]+vector[2];
		if (limitsCut) {
			r[0] = world.shrinkToWorld(r[0]);
			r[1] = world.shrinkToWorld(r[1]);
			r[2] = world.shrinkToWorld(r[2]);
		}
		
		return r;
	}
	
	public void setViewPosition(int[] coords)
	{
		System.out.println(" NEW VIEW POSITION = "+coords[0]+" - "+coords[1]+" - "+coords[2]);
		viewPositionX = coords[0];
		viewPositionY = coords[1];
		viewPositionZ = coords[2];
	}
	public void setRelativePosition(int[] coords)
	{
		relativeX = coords[0];
		relativeY = coords[1];
		relativeZ = coords[2];
	}
	
	// putting common sidetypes together
	/**
	 * You cannot walk (horizontal) through these.
	 */
	public static HashSet<Class> notWalkable = new HashSet<Class>();
	/**
	 * You cannot pass through this (any direction).
	 */
	public static HashSet<Class> notPassable = new HashSet<Class>();
	/**
	 * You get onto steep on these.
	 */
	public static HashSet<Class> climbers = new HashSet<Class>();
	static
	{
		notWalkable.add(NotPassable.class);
		notWalkable.add(Swimming.class);
		notPassable.add(NotPassable.class);
		notPassable.add(GroundSubType.class);
		notPassable.add(Swimming.class);
		climbers.add(Climbing.class);
	}

	public static boolean FREE_MOVEMENT = true; // debug true, otherwise false!
	
	/**
	 * Tries to move in directions, and sets coords if successfull
	 * @param from From coordinates (world coords)
	 * @param fromRel From coordinates relative (3d space coords)
	 * @param directions A set of directions to move into
	 */
	public boolean move(int[] from, int[] fromRel, int[] directions)
	{
		int[] newCoords = from;
		int[] newRelCoords = fromRel;
		for (int i=0; i<directions.length; i++) {
			System.out.println("Moving dir: "+directions[i]);
			newCoords = calcMovement(newCoords, directions[i],true); 
			newRelCoords = calcMovement(newRelCoords, directions[i],false);
		}
		if (FREE_MOVEMENT)
		{ // test free movement
			setViewPosition(newCoords);
			setRelativePosition(newRelCoords);
			Cube c = world.getCube(newCoords[0], newCoords[1], newCoords[2]);
			if (c!=null) {
				if (c.internalCube)
				{
					System.out.println("Moved: INTERNAL");
					insideArea = true;
					groundParentNode.detachAllChildren(); // workaround for culling
					groundParentNode.attachChild(intRootNode);
					groundParentNode.attachChild(extRootNode);
				} else
				{
					System.out.println("Moved: EXTERNAL");
					insideArea = false;
					groundParentNode.detachAllChildren(); // workaround for culling
					groundParentNode.attachChild(extRootNode);
					groundParentNode.attachChild(intRootNode);
				}
			}
			return true;
			
		}

		Cube c = world.getCube(from[0], from[1], from[2]);
		
		if (c!=null) {
			System.out.println("Current Cube = "+c.toString());
			// get current steep dir for knowing if checking below or above Cube for moving on steep 
			int currentCubeSteepDirection = hasSideOfInstanceInAnyDir(c, climbers);
			System.out.println("STEEP DIRECTION"+currentCubeSteepDirection+" - "+directions[0]);
			if (currentCubeSteepDirection==oppositeDirections.get(new Integer(directions[0])).intValue())
			{
				newCoords = calcMovement(newCoords, TOP, true); 
				newRelCoords = calcMovement(newRelCoords, TOP, false);
			}
			Side[] sides = c.getSide(directions[0]);
			if (sides!=null)
			{
				System.out.println("SAME CUBE CHECK: NOTPASSABLE");
				if (hasSideOfInstance(sides, notPassable) && !onSteep) return false;
				System.out.println("SAME CUBE CHECK: NOTPASSABLE - passed");
			}
			Cube nextCube = world.getCube(newCoords[0], newCoords[1], newCoords[2]);
			if (nextCube==null) System.out.println("NEXT CUBE = NULL");
			if (nextCube!=null && hasSideOfInstance(nextCube.getSide(BOTTOM), notPassable))
			{
				// we have next cube in walk dir, and it has bottom too
				System.out.println("Next Cube = "+nextCube.toString());
				sides = nextCube.getSide(oppositeDirections.get(new Integer(directions[0])).intValue());
				if (sides!=null)
				{
					if (hasSideOfInstance(sides, notPassable)) return false;
				}

				sides = nextCube!=null?nextCube.getSide(BOTTOM):null;
				if (sides!=null)
				{
					if (hasSideOfInstance(sides, notWalkable)) return false;
				}

				// checking steep setting
				int nextCubeSteepDirection = hasSideOfInstanceInAnyDir(nextCube, climbers);
				if (nextCubeSteepDirection!=-1) {
					onSteep = true;
				} else
				{
					onSteep = false;
				}
			} else 
			{
				// no next cube in same direction, trying lower part steep, until falling down deadly if nothing found... :)
				int yMinus = 1; // value of delta downway
				while (true)
				{
					// cube below
					nextCube = world.getCube(newCoords[0], newCoords[1]-(yMinus++), newCoords[2]);
					System.out.println("FALLING: "+nextCube);
					if (yMinus>10) break; /// i am faaaalling.. :)
					if (nextCube==null) continue;

					sides = nextCube!=null?nextCube.getSide(directions[0]):null;
					if (sides!=null)
					{
						// Try to get climber side
						if (hasSideOfInstance(sides, climbers))
						{
							sides = nextCube!=null?nextCube.getSide(BOTTOM):null;
							if (hasSideOfInstance(sides, notWalkable)) return false;
							newCoords[1] = newCoords[1]-(yMinus-1);
							newRelCoords[1] = newRelCoords[1]-(yMinus-1);
							onSteep = true; // found steep
							break;
						} else
						{
							sides = nextCube!=null?nextCube.getSide(TOP):null;
							if (sides!=null) // checking if cube's top is not passable
								if (hasSideOfInstance(sides, notPassable))
									return false; // not passable, do not make this step
							// no luck with climbers , let's see notPassable bottom...
							sides = nextCube!=null?nextCube.getSide(BOTTOM):null;
							if (sides!=null)
							if (hasSideOfInstance(sides, notPassable))
							{	
								// yeah, a place to stand...
								newCoords[1] = newCoords[1]-(yMinus-1);
								newRelCoords[1] = newRelCoords[1]-(yMinus-1);
								onSteep = false; // yeah, found
								break;
							}
						}
					} else
					{
						// no luck with climbers on walk direction, let's see notPassable bottom...
						sides = nextCube!=null?nextCube.getSide(BOTTOM):null;
						if (sides!=null)
						if (hasSideOfInstance(sides, notPassable))
						{							
							newCoords[1] = newCoords[1]-(yMinus-1);
							newRelCoords[1] = newRelCoords[1]-(yMinus-1);
							if (hasSideOfInstanceInAnyDir(nextCube, climbers)!=-1) // check for climbers
							{
								onSteep = true;
							} else
							{
								onSteep = false; // yeah, found
							}
							break;
						}
					}
				}
				
				
				//return;
			}
			
		} else 
		{
			onSteep = false;
			//return;
		}
		setViewPosition(newCoords);
		setRelativePosition(newRelCoords);
		c = world.getCube(newCoords[0], newCoords[1], newCoords[2]);
		if (c!=null) {
			if (c.internalCube)
			{
				System.out.println("Moved: INTERNAL");
				insideArea = true;
				groundParentNode.detachAllChildren(); // workaround for culling
				groundParentNode.attachChild(intRootNode);
				groundParentNode.attachChild(extRootNode);
			} else
			{
				System.out.println("Moved: EXTERNAL");
				insideArea = false;
				groundParentNode.detachAllChildren(); // workaround for culling
				groundParentNode.attachChild(extRootNode);
				groundParentNode.attachChild(intRootNode);
			}
		}
		return true;
	}
	
	boolean debugLeak = false;
	public boolean moveForward(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		if (debugLeak) {
			viewPositionX+=40;
			relativeX+=40;
			return true;
		} else
		return move(coords,relCoords,new int[]{direction});
	}

	/**
	 * Move view Left (strafe)
	 * @param direction
	 */
	public boolean moveLeft(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		if (direction == NORTH) {
			return move(coords,relCoords,new int[]{WEST});
		} else if (direction == SOUTH) {
			return move(coords,relCoords,new int[]{EAST});
		} else if (direction == EAST) {
			return move(coords,relCoords,new int[]{NORTH});
		} else if (direction == WEST) {
			return move(coords,relCoords,new int[]{SOUTH});
		}
		return false;
	}
	/**
	 * Move view Right (strafe)
	 * @param direction
	 */
	public boolean moveRight(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		if (direction == NORTH) {
			return move(coords,relCoords,new int[]{EAST});
		} else if (direction == SOUTH) {
			return move(coords,relCoords,new int[]{WEST});
		} else if (direction == EAST) {
			return move(coords,relCoords,new int[]{SOUTH});
		} else if (direction == WEST) {
			return move(coords,relCoords,new int[]{NORTH});
		}
		return false;
	}

	public boolean moveBackward(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		return move(coords,relCoords,new int[]{oppositeDirections.get(new Integer(direction)).intValue()});
	}

	
	/**
	 * Move view Up (strafe)
	 * @param direction
	 */
	public boolean moveUp() {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		return move(coords,relCoords,new int[]{TOP});
	}
	public boolean moveDown() {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		return move(coords,relCoords,new int[]{BOTTOM});
	}
	
	
	public void turnRight()
	{
		viewDirection++;
		if (viewDirection==directions.length) viewDirection = 0;
        //cam.setDirection(J3DCore.directions[viewDirection]);
	}
	public void turnLeft()
	{
		viewDirection--;
		if (viewDirection==-1) viewDirection = directions.length-1;
        //cam.setDirection(J3DCore.directions[viewDirection]);
	}
	
	
	boolean noInput = false;

	public void updateDisplay(Vector3f from)
	{

		noInput = true;
        // update game state, do not use interpolation parameter
        update(-1.0f);

        // render, do not use interpolation parameter
        render(-1.0f);

        // swap buffers
//        pManager.remove(sPass);
        display.getRenderer().displayBackBuffer();
  //      pManager.add(sPass);
		noInput = false;
		
	}
	public void updateDisplayNoBackBuffer()
	{

		noInput = true;
        // update game state, do not use interpolation parameter
        //update(-1.0f);

        // render, do not use interpolation parameter
        render(-1.0f);

        // swap buffers
		noInput = false;
		
	}
	
    @Override
	protected void updateInput() {
		//fpsNode.detachAllChildren();
    	if (!noInput)
    		super.updateInput();
	}

	@Override
	protected void cleanup() {
		//engine.exit();
		super.cleanup();
        if (bloomRenderPass != null)
            bloomRenderPass.cleanup();
	}

	@Override
	public void finish() {
		//engine.exit();
		super.finish();
	}

	@Override
	protected void quit() {
		engine.exit();
		super.quit();
	}
 
	public FogState fs_external;
	public FogState fs_external_special;
	public FogState fs_internal;
    public ShadowedRenderPass sPass = null;
	private BloomRenderPass bloomRenderPass;
	public static WaterRenderPass waterEffectRenderPass;
	
	public CullState cs_back = null;
	public CullState cs_none = null;
	
	public VertexProgramState vp = null;
	public FragmentProgramState fp = null;
	
	Node hud1Node;

	BoundingSphere bigSphere = new BoundingSphere();

	public UIBase uiBase;
	
	/**
     * This is used to display print text.
     */
    protected StringBuffer hud1Buffer = new StringBuffer( 30 );
    BillboardNode bbFloppy;
	@Override
	protected void simpleInitGame() {
		//cam.resize(100, 100);
		//cam.setViewPort(30, 90, 30, 90);
		bigSphere.setCenter(new Vector3f(0,0,0));
		//bigSphere.s
		bigSphere.setRadius(10000f);
		// external cubes' rootnode
		extRootNode = new Node();
		//extRootNode.setModelBound(bigSphere);
		//extRootNode.attachChild(new Node());
		// internal cubes' rootnode
		intRootNode = new Node();
		//intRootNode.setModelBound(bigSphere);
		//intRootNode.attachChild(new Node());
		groundParentNode.setModelBound(null);
		intRootNode.setModelBound(null);
		extRootNode.setModelBound(null);

        //cRootNode = new ScenarioNode(J3DCore.VIEW_DISTANCE,cam);
		//Setup renderpasses
		
		bloomRenderPass = new BloomRenderPass(cam, 4);
		
		ShadeState ss = display.getDisplaySystem().getRenderer().createShadeState();
		ss.setShade(ShadeState.SM_FLAT);
		ss.setEnabled(false);
		//rootNode.setRenderState(ss);
		//rootNode.clearRenderState(RenderState.RS_SHADE);
		
		cs_back = display.getRenderer().createCullState();
		cs_back.setCullMode(CullState.CS_BACK);
		cs_back.setEnabled(true);
		cs_none = display.getRenderer().createCullState();
		cs_none.setCullMode(CullState.CS_NONE);

		rootNode.setRenderState(cs_none);
		/*rootNode.clearRenderState(RenderState.RS_DITHER);
		rootNode.clearRenderState(RenderState.RS_FRAGMENT_PROGRAM);
		rootNode.clearRenderState(RenderState.RS_MATERIAL);
		rootNode.clearRenderState(RenderState.RS_TEXTURE);
		rootNode.clearRenderState(RenderState.RS_SHADE);
		rootNode.clearRenderState(RenderState.RS_STENCIL);*/
		


		DisplaySystem.getDisplaySystem().getRenderer().getQueue().setTwoPassTransparency(false);
		rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

		lightState.detachAll();
		extLightState = getDisplay().getRenderer().createLightState();
		internalLightState = getDisplay().getRenderer().createLightState();
		skydomeLightState = getDisplay().getRenderer().createLightState();
		
		
		display.getRenderer().setBackgroundColor(ColorRGBA.black);
		

		cam.setFrustumPerspective(45.0f,(float) display.getWidth() / (float) display.getHeight(), 0.002f, 350);
		groundParentNode.attachChild(intRootNode);
		groundParentNode.attachChild(extRootNode);
		groundParentNode.attachChild(skyParentNode);
		rootNode.attachChild(groundParentNode);

        AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as.setEnabled(true);
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		if (!BLOOM_EFFECT) {
			if (TEXTURE_QUALITY==2)
				as.setReference(0.9f);
			else
				as.setReference(0.9f);
		}
			else as.setReference(0.9f);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
		
		fs_external = display.getRenderer().createFogState();
		fs_external_special = display.getRenderer().createFogState();
        fs_external.setDensity(0.5f);
        fs_external.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        if (J3DCore.FARVIEW_ENABLED) 
        {
            fs_external.setEnd((RENDER_DISTANCE_ORIG/1.15f));
            fs_external.setStart(1.5f*RENDER_DISTANCE_ORIG/3);
            fs_external_special.setEnd((VIEW_DISTANCE/1.15f));
            fs_external_special.setStart(2*VIEW_DISTANCE/3);
            fs_external_special.setDensityFunction(FogState.DF_LINEAR);
            fs_external_special.setApplyFunction(FogState.AF_PER_VERTEX);
            fs_external_special.setNeedsRefresh(true);
            fs_external_special.setEnabled(true);        
        } else
        {
            fs_external.setEnd((VIEW_DISTANCE/1.15f));
        	fs_external.setStart(2*VIEW_DISTANCE/3);
        }
        fs_external.setDensityFunction(FogState.DF_LINEAR);
        fs_external.setApplyFunction(FogState.AF_PER_VERTEX);
        fs_external.setNeedsRefresh(true);
        fs_external.setEnabled(true);
        extRootNode.setRenderState(fs_external);
        extRootNode.setRenderState(as);

		fs_internal = display.getRenderer().createFogState();
		fs_internal.setDensity(0.5f);
		fs_internal.setEnabled(true);
		fs_internal.setColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
		fs_internal.setEnd((VIEW_DISTANCE/1.15f));
		fs_internal.setStart(3);
		fs_internal.setDensityFunction(FogState.DF_LINEAR);
		fs_internal.setApplyFunction(FogState.AF_PER_VERTEX);
        intRootNode.setRenderState(fs_internal);
        intRootNode.setRenderState(as);
 		
        // default light states
		extRootNode.setRenderState(extLightState);
		intRootNode.clearRenderState(RenderState.RS_LIGHT);
		intRootNode.setRenderState(internalLightState);
		
		if (true==true && dr == null) {

			dr = new PointLight();
			dr.setEnabled(true);
			float lp = 0.8f;
			dr.setDiffuse(new ColorRGBA(lp, lp, lp, 0.5f));
			dr.setAmbient(new ColorRGBA(1f, 1f, 1f, 0.5f));
			dr.setSpecular(new ColorRGBA(1, 1, 1, 0.5f));
			dr.setQuadratic(1f);
			dr.setLinear(1f);
			//dr.setAngle(45);
			dr.setShadowCaster(false);
			internalLightState.attach(dr);
		}
        
		RenderPass rootPass = new RenderPass();
		//rootPass.add(rootNode);
		pManager.add(rootPass);

		if (SHADOWS) {
			sPass = new ShadowedRenderPass();
			System.out.println("SHADOWS!");
			//sPass.setShadowColor(new ColorRGBA(0,0,0,1f));
			sPass.setEnabled(true);
	    	sPass.add(extRootNode);
	    	sPass.add(intRootNode);
	    	sPass.setRenderShadows(true);
	    	sPass.setLightingMethod(ShadowedRenderPass.MODULATIVE);
	    	J3DShadowGate dsg = new J3DShadowGate();
	    	dsg.core = this;
	    	sPass.setShadowGate(dsg);
	    	//sPass.setZOffset(0f);
	    	pManager.add(sPass);
		}
		
		if (BLOOM_EFFECT) {
	      if(!bloomRenderPass.isSupported()) {
	    	   System.out.println("!!!!!! BLOOM NOT SUPPORTED !!!!!!!! ");
	           Text t = new Text("Text", "Bloom not supported (FBO needed).");
	           t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
	           t.setLightCombineMode(LightState.OFF);
	           t.setLocalTranslation(new Vector3f(0,display.getHeight()-20,0));
	           fpsNode.attachChild(t);
	           BLOOM_EFFECT = false;
	       } else {
	    	   System.out.println("!!!!!!!!!!!!!! BLOOM!");
	           bloomRenderPass.add(rootNode);
	           bloomRenderPass.setUseCurrentScene(true);
	           bloomRenderPass.setBlurIntensityMultiplier(1f);
	           pManager.add(bloomRenderPass);
	       }
		}
		waterEffectRenderPass = new WaterRenderPass( cam, 4, false, true);
		//set equations to use z axis as up
		waterEffectRenderPass.setWaterPlane( new Plane( new Vector3f( 0.0f, 1.0f, 0.0f ), 0.0f ) );
		waterEffectRenderPass.setTangent( new Vector3f( 1.0f, 0.0f, 0.0f ) );
		waterEffectRenderPass.setBinormal( new Vector3f( 0.0f, 1.0f, 0.0f ));
		//waterEffectRenderPass.setWaterMaxAmplitude(2f);
		pManager.add( waterEffectRenderPass );
		
		/*
		 * Skysphere
		 */
		skySphere = new Sphere("SKY_SPHERE",20,20,300f);
		waterEffectRenderPass.setReflectedScene(WATER_DETAILED?groundParentNode:skyParentNode);
		skyParentNode.attachChild(skySphere);
		skySphere.setModelBound(null); // this must be set to null for lens flare
		skySphere.setRenderState(cs_none);
		skySphere.setCullMode(Node.CULL_NEVER);
		
		groundParentNode.clearRenderState(RenderState.RS_LIGHT);
		rootNode.clearRenderState(RenderState.RS_LIGHT);
		skySphere.setRenderState(skydomeLightState);
		
		//intRootNode.attachChild(skySphereInvisibleGround);
		Texture texture = TextureManager.loadTexture("./data/sky/day/top.jpg",Texture.MM_LINEAR,
                Texture.FM_LINEAR);

		
		if (texture!=null) {

			texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
			texture.setApply(Texture.AM_MODULATE);
			texture.setRotation(qTexture);
			TextureState state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
			state.setTexture(texture,0);
			state.setEnabled(true);
			skySphere.setRenderState(state);
		}
		skySphere.updateRenderState();
		
		//
		
		//setupSkyDome();
		
		setCalculatedCameraLocation();
        
		cam.update();

		fpsNode.getChild(0).setLocalTranslation(new Vector3f(0,display.getHeight()-20,0));
        //t.setLocalTranslation();

		// initial hud part for world map
		
		try 
		{
			uiBase = new UIBase(this);
			uiBase.addWindow("worldMap", new Map(uiBase,world.worldMap));
			rootNode.attachChild(uiBase.hud.hudNode);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(-1);
			
		}
        
        updateDisplay(null);

		/*Spatial map = world.worldMap.getMapQuad();
		if (map.getParent()==null)
		{
			extRootNode.attachChild(map);
			//map.setLocalTranslation((relativeX)*CUBE_EDGE_SIZE, relativeY*CUBE_EDGE_SIZE, (-1.5f+(-1*relativeZ))*CUBE_EDGE_SIZE);
			//map.updateRenderState();
			extRootNode.updateRenderState();
		}*/
		
		render();
		renderToViewPort();
		renderToViewPort(); // for correct culling, call it twice ;-)
		engine.setPause(false);
	}
	
	LightNode drn;
	PointLight dr;

	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		
		if (dr!=null) {
			dr.setLocation(cam.getLocation().add(new Vector3f(0f, 0.1f, 0f)));
			//dr.setDirection(cam.getDirection());
		}

		if (engine.timeChanged) 
		{
			engine.setTimeChanged(false);
			updateTimeRelated();
			
		}
		if ( !pause ) {
			/** Call simpleUpdate in any derived classes of SimpleGame. */

			/** Update controllers/render states/transforms/bounds for rootNode. */
			rootNode.updateGeometricState(tpf, true);
			fpsNode.updateGeometricState(tpf, true);
			
			//if (BLOOM_EFFECT|| SHADOWS || WATER_SHADER) 
			pManager.updatePasses(tpf);
		}
	}

	@Override
	protected void simpleRender() {
		TrimeshGeometryBatch.passedTimeCalculated = false;
        /** Have the PassManager render. */
        try {
        	//if (BLOOM_EFFECT||SHADOWS||WATER_SHADER) 
        		pManager.renderPasses(display.getRenderer());
        } catch (NullPointerException npe)
        {
        }
 		super.simpleRender();
	}
	
    protected BasicPassManager pManager;

    boolean rendering = false;
    Object mutex = new Object();
	public void run() {
		
		if (rendering) return;
		synchronized (mutex) {
			rendering = true;		
			renderToViewPort();
			rendering = false;
		}
	}

	  
    /**
     * Called every frame to update scene information.
     * 
     * @param interpolation
     *            unused in this implementation
     * @see BaseSimpleGame#update(float interpolation)
     */
    protected final void update(float interpolation) {
        super.update(interpolation);

        if ( !pause ) {
            /** Call simpleUpdate in any derived classes of SimpleGame. */
            simpleUpdate();

            /** Update controllers/render states/transforms/bounds for rootNode. */
            rootNode.updateGeometricState(tpf, true);
        }
    }

    /**
     * This is called every frame in BaseGame.start(), after ()
     * 
     * @param interpolation
     *            unused in this implementation
     * @see AbstractGame#render(float interpolation)
     */
    protected final void render(float interpolation) {
        super.render(interpolation);
        
        Renderer r = display.getRenderer();

        /** Draw the rootNode and all its children. */
        r.draw(rootNode);
        
        /** Call simpleRender() in any derived classes. */
        simpleRender();
        
        /** Draw the fps node to show the fancy information at the bottom. */
        r.draw(fpsNode);
        
        doDebug(r);
    }

    
    public Node getRootNode()
    {
    	return rootNode;
    }
    public InputHandler getInputHandler()
    {
    	return input;
    }
}