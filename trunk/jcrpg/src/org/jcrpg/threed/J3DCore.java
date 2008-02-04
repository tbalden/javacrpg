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
import java.util.Properties;
import java.util.Set;

import org.jcrpg.game.PlayerTurnLogic;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.StickingOut;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.input.ClassicInputHandler;
import org.jcrpg.threed.jme.GeometryBatchHelper;
import org.jcrpg.threed.jme.TrimeshGeometryBatch;
import org.jcrpg.threed.jme.effects.WaterRenderPass;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.moving.J3DMovingEngine;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.config.SideTypeModels;
import org.jcrpg.threed.scene.side.RenderedSide;
import org.jcrpg.threed.standing.J3DStandingEngine;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.ui.window.Map;
import org.jcrpg.ui.window.PlayerChoiceWindow;
import org.jcrpg.ui.window.element.ChoiceDescription;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.orbiter.Orbiter;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
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
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
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

    public HashMap<String,Integer> hmCubeSideSubTypeToRenderedSideId = new HashMap<String,Integer>();

    public HashMap<Integer,RenderedSide> hm3dTypeRenderedSide = new HashMap<Integer,RenderedSide>();
    
    /**
     * This is the maximum view distance with far enabled * 2,
     * so that you cannot see the world's cubes twice from one viewpoint. :)
     */
    public static final int MINIMUM_WORLD_REALSIZE = 100;
    
	/**
	 * rendered cubes in each direction (N,S,E,W,T,B).
	 */
    public static int RENDER_DISTANCE_ORIG = 10;
    public static int RENDER_DISTANCE_FARVIEW = 40;
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
    
    public static int FARVIEW_GAP = 4;
    public static boolean FARVIEW_ENABLED = false;

    static Properties p = new Properties();
    static {
		try {
			File f = new File("./config.properties");
			FileInputStream fis = new FileInputStream(f);
			p.load(fis);

			FARVIEW_ENABLED = loadValue("FARVIEW_ENABLED", false);

			RENDER_DISTANCE_FARVIEW = loadValue("RENDER_DISTANCE_FARVIEW",
					(int) (60 * CUBE_EDGE_SIZE), (int) (20 * CUBE_EDGE_SIZE),
					Integer.MAX_VALUE);
			RENDER_DISTANCE_FARVIEW /= CUBE_EDGE_SIZE;

			RENDER_DISTANCE = loadValue("RENDER_DISTANCE",
					(int) (10 * CUBE_EDGE_SIZE), (int) (10 * CUBE_EDGE_SIZE),
					Integer.MAX_VALUE);
			RENDER_DISTANCE /= CUBE_EDGE_SIZE;

			VIEW_DISTANCE = loadValue("VIEW_DISTANCE", 10, 5, Integer.MAX_VALUE);
			VIEW_DISTANCE_SQR = VIEW_DISTANCE * VIEW_DISTANCE;
			VIEW_DISTANCE_FRAG_SQR = VIEW_DISTANCE_SQR / 4;

			RENDER_GRASS_DISTANCE = loadValue("RENDER_GRASS_DISTANCE", 10, 0,
					(int) (15 * CUBE_EDGE_SIZE));

			RENDER_SHADOW_DISTANCE = loadValue("RENDER_SHADOW_DISTANCE", 10, 0,
					(int) (15 * CUBE_EDGE_SIZE));
			if (RENDER_SHADOW_DISTANCE > RENDER_DISTANCE * CUBE_EDGE_SIZE)
				RENDER_SHADOW_DISTANCE = (int) (RENDER_DISTANCE * CUBE_EDGE_SIZE);
			RENDER_SHADOW_DISTANCE_SQR = RENDER_SHADOW_DISTANCE
					* RENDER_SHADOW_DISTANCE;

			MIPMAP_GLOBAL = loadValue("MIPMAP_GLOBAL", true);
			MIPMAP_TREES = loadValue("MIPMAP_TREES", false);

			TEXTURE_QUALITY = loadValue("TEXTURE_QUALITY", 0, 0,
					Integer.MAX_VALUE);
			BLOOM_EFFECT = loadValue("BLOOM_EFFECT", false);
			ANIMATED_GRASS = loadValue("ANIMATED_GRASS", false);
			DOUBLE_GRASS = loadValue("DOUBLE_GRASS", false);
			SHADOWS = loadValue("SHADOWS", false);
			ANIMATED_TREES = loadValue("ANIMATED_TREES", false);
			DETAILED_TREES = loadValue("DETAILED_TREES", false);
			ANTIALIAS_SAMPLES = loadValue("ANTIALIAS_SAMPLES", 0, 0, 8);
			BUMPED_GROUND = loadValue("BUMPED_GROUND", false);
			WATER_SHADER = loadValue("WATER_SHADER", false);
			WATER_DETAILED = loadValue("WATER_DETAILED", false);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static private boolean loadValue(String name, boolean defaultValue) {
		boolean result = parse(p.getProperty(name), defaultValue);
		p.setProperty(name, result + "");
		return result;
	}

	static private <T extends Comparable<T>> T loadValue(String name,
			T defaultValue, T min, T max) {
		T result = parse(p.getProperty(name), defaultValue);
		if (result.compareTo(min) < 0)
			result = min;
		if (result.compareTo(max) > 0)
			result = max;
		p.setProperty(name, result + "");
		return result;
	}

	/*
	 * this is a generic and useful function that is better to put in a place
	 * where all can see it, typically used in graph interface and so on.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parse(String info, T defaultValue) {
		if (defaultValue.getClass() == String.class)
			return (T) (Object) info;
		info = info.trim();
		if (defaultValue.getClass() == Character.class)
			if (info.length() == 1)
				return (T) (Object) info.charAt(0);
			else
				return defaultValue;
		if (defaultValue.getClass() == Boolean.class) {
			Boolean b = Boolean.parseBoolean(info);
			if (b || info.equalsIgnoreCase("false"))
				return (T) (Object) b;
			return defaultValue;
		}
		try {
			if (defaultValue.getClass() == Byte.class)
				return (T) (Object) Byte.parseByte(info);
			if (defaultValue.getClass() == Short.class)
				return (T) (Object) Short.parseShort(info);
			if (defaultValue.getClass() == Integer.class)
				return (T) (Object) Integer.parseInt(info);
			if (defaultValue.getClass() == Long.class)
				return (T) (Object) Long.parseLong(info);
			if (defaultValue.getClass() == Float.class)
				return (T) (Object) Float.parseFloat(info);
			if (defaultValue.getClass() == Double.class)
				return (T) (Object) Double.parseDouble(info);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
		assert false : "FAST_PARSE::Type not reconized";
		throw new AssertionError("FAST_PARSE::Type not reconized");
	}

    
	public int viewDirection = NORTH;
	public int viewPositionX = 0;
	public int viewPositionY = 0;
	public int viewPositionZ = 0;
	public int relativeX = 0, relativeY = 0, relativeZ = 0;
	public int origoX = 0;
	public int origoY = 0;
	public int origoZ = 0;
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
	public Ecology ecology = null;
	
	public void setWorld(World area)
	{
		world = area;
	}

	public void setEcology(Ecology ecology)
	{
		this.ecology = ecology;
	}
	
	public EntityInstance player = null;
	public PlayerTurnLogic playerTurnLogic = null;
	public void setPlayer(EntityInstance player, PlayerTurnLogic playerTurnLogic)
	{
		this.player = player;
		this.playerTurnLogic = playerTurnLogic;
	}
	
	public void setViewPosition(int x,int y,int z)	
	{
		System.out.println("!!!!!!!!!! VIEW POS: "+y);
		viewPositionX = x;
		viewPositionY = y;
		viewPositionZ = z;
		player.setPosition(new int[]{x,y,z});
	}

	/**
	 * For storing the origo cube coordinate in the world when starting a game session - for rendering use. 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setOrigoRenderPosition(int x,int y,int z)
	{
		origoX = x;
		origoY = y;
		origoZ = z;
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
	// farview steeps
	public static HashMap<Integer,Quaternion> steepRotations_FARVIEW = new HashMap<Integer, Quaternion>();
	static
	{
		// steep rotations
		Quaternion steepE = new Quaternion();
		steepE.fromAngles(new float[]{0,(FastMath.PI/4)/FARVIEW_GAP,0});
		Quaternion steepW = new Quaternion();
		steepW.fromAngles(new float[]{0,(-FastMath.PI/4)/FARVIEW_GAP,0});
		Quaternion steepS = new Quaternion();
		steepS.fromAngles(new float[]{(FastMath.PI/4)/FARVIEW_GAP,0,0});
		Quaternion steepN = new Quaternion();
		steepN.fromAngles(new float[]{(-FastMath.PI/4)/FARVIEW_GAP,0,0});

		steepRotations_FARVIEW.put(new Integer(NORTH), steepN);
		steepRotations_FARVIEW.put(new Integer(SOUTH), steepS);
		steepRotations_FARVIEW.put(new Integer(WEST), steepW);
		steepRotations_FARVIEW.put(new Integer(EAST), steepE);
	}
	
	public static HashMap<Integer,Quaternion> steepRotations_special_FARVIEW = new HashMap<Integer, Quaternion>();
	static
	{
		// steep rotations with special in-one-step rotation
		Quaternion steepE_noRot = new Quaternion();
		steepE_noRot.fromAngles(new float[]{FastMath.PI/2,0,(3*FastMath.PI/4)/FARVIEW_GAP});
		Quaternion steepW_noRot = new Quaternion();
		steepW_noRot.fromAngles(new float[]{-FastMath.PI/2,0,(FastMath.PI/4)/FARVIEW_GAP});
		Quaternion steepS_noRot = new Quaternion();
		steepS_noRot.fromAngles(new float[]{0,(FastMath.PI/4)/FARVIEW_GAP,FastMath.PI/2});
		Quaternion steepN_noRot = new Quaternion();
		steepN_noRot.fromAngles(new float[]{0,(-3*FastMath.PI/4)/FARVIEW_GAP,-FastMath.PI/2});

		steepRotations_special_FARVIEW.put(new Integer(NORTH), steepN_noRot);
		steepRotations_special_FARVIEW.put(new Integer(SOUTH), steepS_noRot);
		steepRotations_special_FARVIEW.put(new Integer(WEST), steepW_noRot);
		steepRotations_special_FARVIEW.put(new Integer(EAST), steepE_noRot);
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
	
	public J3DCore()
	{
		self = this;
		if (J3DCore.SHADOWS) stencilBits = 8;
		alphaBits = 0;
		depthBits = 4;
		samples = ANTIALIAS_SAMPLES;

		new SideTypeModels().fillMap(hmCubeSideSubTypeToRenderedSideId, hm3dTypeRenderedSide, MIPMAP_TREES, DETAILED_TREES, BUMPED_GROUND, RENDER_GRASS_DISTANCE, LOD_VEGETATION);
		
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

	public int garbCollCounter = 0;

	public static boolean OPTIMIZE_ANGLES = true;
	public static float ROTATE_VIEW_ANGLE = OPTIMIZE_ANGLES?2.5f:3.14f;

	public static boolean GEOMETRY_BATCH = true;
	public static boolean GRASS_BIG_BATCH = true;
	
	
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
	public boolean hasSideOfInstance(Side[] sides, Set<Class<? extends SideSubType>> classNames)
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
	public Integer[] hasSideOfInstanceInAnyDir(Cube c, Set<Class<? extends SideSubType>> classNames)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
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
						list.add(j);
					}
				}
			}
		}
		if (list.size()==0) return null;
		return (Integer[])list.toArray(new Integer[0]);
		
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
		player.setPosition(coords);
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
	public static Set<Class<? extends SideSubType>> notWalkable = new HashSet<Class<? extends SideSubType>>();
	/**
	 * You cannot pass through this (any direction).
	 */
	public static Set<Class<? extends SideSubType>> notPassable = new HashSet<Class<? extends SideSubType>>();
	/**
	 * You cannot fall onto this (from top).
	 */
	public static Set<Class<? extends SideSubType>> notFallable = new HashSet<Class<? extends SideSubType>>();
	/**
	 * You get onto steep on these.
	 */
	public static Set<Class<? extends SideSubType>> climbers = new HashSet<Class<? extends SideSubType>>();
	static
	{
		notWalkable.add(NotPassable.class);
		notWalkable.add(Swimming.class);
		notWalkable.add(StickingOut.class);
		notPassable.add(NotPassable.class);
		notPassable.add(GroundSubType.class);
		notPassable.add(Swimming.class);
		notPassable.add(StickingOut.class);
		climbers.add(Climbing.class);
		notFallable.add(StickingOut.class);
	}

	public static boolean FREE_MOVEMENT = false; // debug true, otherwise false!
	
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
			Cube c = world.getCube(newCoords[0], newCoords[1], newCoords[2], false);
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

		Cube c = world.getCube(from[0], from[1], from[2], false);
		
		if (c!=null) {
			System.out.println("Current Cube = "+c.toString());
			// get current steep dir for knowing if checking below or above Cube for moving on steep 
			Integer[] currentCubeSteepDirections = hasSideOfInstanceInAnyDir(c, climbers);
			System.out.println("STEEP DIRECTION"+(currentCubeSteepDirections!=null?currentCubeSteepDirections.length:"null")+" - "+directions[0]+"  "+c.steepDirection);
			if (currentCubeSteepDirections!=null)
			for (int steepDir: currentCubeSteepDirections) {
				if ( steepDir==oppositeDirections.get(new Integer(directions[0])).intValue())
				{
					newCoords = calcMovement(newCoords, TOP, true); 
					newRelCoords = calcMovement(newRelCoords, TOP, false);
					break;
				}
			}
			Side[] sides = c.getSide(directions[0]);
			if (sides!=null)
			{
				System.out.println("SAME CUBE CHECK: NOTPASSABLE");
				if (hasSideOfInstance(sides, notPassable) && (!onSteep || directions[0]==BOTTOM || directions[0]==TOP)) return false;
				System.out.println("SAME CUBE CHECK: NOTPASSABLE - passed");
			}
			Cube nextCube = world.getCube(newCoords[0], newCoords[1], newCoords[2], false);
			if (nextCube==null) System.out.println("NEXT CUBE = NULL");
				else 
			{
					System.out.println("Next Cube = "+nextCube.toString());
					sides = nextCube.getSide(oppositeDirections.get(new Integer(directions[0])).intValue());
					if (sides!=null)
					{
						if (hasSideOfInstance(sides, notPassable)) return false;
					}
			}
			
			if (nextCube!=null && hasSideOfInstance(nextCube.getSide(BOTTOM), notPassable))
			{
				// we have next cube in walk dir, and it has bottom too
				
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
				Integer[] nextCubeSteepDirections = hasSideOfInstanceInAnyDir(nextCube, climbers);
				if (nextCubeSteepDirections!=null) {
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
					nextCube = world.getCube(newCoords[0], newCoords[1]-(yMinus++), newCoords[2], false);
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
								if (hasSideOfInstance(sides, notFallable)) return false;
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
							if (hasSideOfInstance(sides, notFallable)) return false;
							newCoords[1] = newCoords[1]-(yMinus-1);
							newRelCoords[1] = newRelCoords[1]-(yMinus-1);
							Integer[] nextCubeSteepDirections = hasSideOfInstanceInAnyDir(nextCube, climbers);
							if (nextCubeSteepDirections!=null) 
							{// check for climbers
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
		c = world.getCube(newCoords[0], newCoords[1], newCoords[2], false);
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
	

	BoundingSphere bigSphere = new BoundingSphere();

	/**
	 * User Interface base object.
	 */
	public UIBase uiBase;
	
	/**
	 * This renders a world initially, call it after loading a game into a clean core.
	 */
	public void init3DGame()
	{
		
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
		
		
		setCalculatedCameraLocation();
        
		cam.update();

		fpsNode.getChild(0).setLocalTranslation(new Vector3f(0,display.getHeight()-20,0));
        //t.setLocalTranslation();

        
        updateDisplay(null);
		
        if (sEngine == null) 
        {
        	sEngine = new J3DStandingEngine(this);
        }
		sEngine.render();
		sEngine.renderToViewPort();
		sEngine.renderToViewPort(); // for correct culling, call it twice ;-)
		
		if (mEngine==null)
		{
			mEngine = new J3DMovingEngine(this);
		}
		
		engine.setPause(false);
		
	}
	
	/**
	 * This is responsible to reset the core for a load/new game from main menu.
	 */
	public void clearCore()
	{
		modelLoader.cleanAll();
		modelPool.cleanAll();
		hmSolidColorSpatials.clear();
		sEngine.clearAll();
		mEngine.clearAll();
		extRootNode.detachAllChildren();
		intRootNode.detachAllChildren();
		groundParentNode.detachAllChildren();
		rootNode.detachAllChildren();
		batchHelper.clearAll();
	}
	
	@Override
	protected void simpleInitGame() {
		rootNode.setCullMode(Node.CULL_NEVER);
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
		
		ShadeState ss = DisplaySystem.getDisplaySystem().getRenderer().createShadeState();
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
            fs_external.setEnd(((RENDER_DISTANCE_FARVIEW*2)/1.15f));
            fs_external.setStart(1.5f*(RENDER_DISTANCE_FARVIEW*2)/3);
            fs_external.setDensity(0.3f);
            fs_external_special.setDensity(0.3f);
            fs_external_special.setEnd((VIEW_DISTANCE*1.65f));
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
        
		try 
		{
			uiBase = new UIBase(this);
			uiBase.addWindow("worldMap", new Map(uiBase,world.worldMap));
			ChoiceDescription yes = new ChoiceDescription("Y","yes","Yes");
			ChoiceDescription no = new ChoiceDescription("N","yes","No");
			ArrayList<ChoiceDescription> quitAnswers = new ArrayList<ChoiceDescription>();
			quitAnswers.add(yes);quitAnswers.add(no);
			uiBase.addWindow("quitQuestion", new PlayerChoiceWindow(uiBase,new TextEntry("Quit?", ColorRGBA.red),quitAnswers,"Quit",0.088f,0.088f,0.3f,0.1f));
			rootNode.attachChild(uiBase.hud.hudNode); // shadows not working because of this node -> the hudNode shall occupy only the lower part, Done, image cut.
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(-1);
			
		}

		RenderPass rootPass = new RenderPass();
		pManager.add(rootPass);

		if (SHADOWS) {
			sPass = new ShadowedRenderPass();
			System.out.println("SHADOWS!");
			//sPass.setShadowColor(new ColorRGBA(0,0,0,1f));
			sPass.setEnabled(true);
			//sPass.add(extRootNode);
			sPass.add(extRootNode);
	    	sPass.add(intRootNode);
	    	sPass.setRenderShadows(true);
	    	sPass.setLightingMethod(ShadowedRenderPass.MODULATIVE);
	    	//sPass.rTexture = false;
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
		
		// TODO do not call init3DGame - instead view Main Menu window - create it. It will use SaveLoadNew class for game creation, and it will
		// call init3DGame. 
		init3DGame();
	}
	
	public J3DMovingEngine mEngine = null;
	public J3DStandingEngine sEngine = null;
	
	
	LightNode drn;
	PointLight dr;
	
	/**
	 * If doing an engine-paused encounter mode this is with value true, switch it with core->switchEncounterMode(value) only!
	 */
	public boolean encounterMode = false;
	
	PlayerChoiceWindow encounterWindow = null;
	
	public void switchEncounterMode(boolean value)
	{
		encounterMode = value;
		if (encounterMode)
		{
			if (encounterWindow==null) { 
				ChoiceDescription yes = new ChoiceDescription("Y","yes","Yes");
				ArrayList<ChoiceDescription> encAnswers = new ArrayList<ChoiceDescription>();
				encAnswers.add(yes);
				encounterWindow = new PlayerChoiceWindow(uiBase,new TextEntry("Encounter acknowledged?", ColorRGBA.red),encAnswers,"Encounter",0.088f,0.088f,0.3f,0.1f);
				uiBase.addWindow("Encounter", encounterWindow);
			}
			uiBase.hud.mainBox.hide();
			updateDisplay(null);
			encounterWindow.toggle();
		}
		else
		{
			encounterWindow.toggle();
			uiBase.hud.mainBox.show();
			uiBase.hud.mainBox.addEntry(new TextEntry("Encounters finished", ColorRGBA.yellow));
			playerTurnLogic.endPlayerEncounters();
			engine.turnFinishedForPlayer();
		}
	}
	

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
		if (engine.turnComes())
		{
			pause = true;
			ecology.doTurn();
			engine.turnFinishedForAI();
			pause = false;
		}
		if ( !pause ) {
			/** Call simpleUpdate in any derived classes of SimpleGame. */

			/** Update controllers/render states/transforms/bounds for rootNode. */
			if (mEngine!=null) mEngine.updateScene(tpf);
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
        	npe.printStackTrace();
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
			sEngine.renderToViewPort();
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
