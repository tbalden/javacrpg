/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.threed;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.audio.AudioServer;
import org.jcrpg.game.GameStateContainer;
import org.jcrpg.game.trigger.StorageObjectHandler;
import org.jcrpg.game.trigger.TriggerHandler;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.StickingOut;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.input.ClassicInputHandler;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;
import org.jcrpg.threed.jme.GeometryBatchHelper;
import org.jcrpg.threed.jme.QuaternionBuggy;
import org.jcrpg.threed.jme.TrimeshGeometryBatch;
import org.jcrpg.threed.jme.effects.DepthOfFieldRenderPass;
import org.jcrpg.threed.jme.effects.DirectionalShadowMapPass;
import org.jcrpg.threed.jme.effects.WaterRenderPass;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.moving.J3DEncounterEngine;
import org.jcrpg.threed.moving.J3DMovingEngine;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.config.SideTypeModels;
import org.jcrpg.threed.scene.side.RenderedSide;
import org.jcrpg.threed.standing.J3DStandingEngine;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.ui.window.LoadMenu;
import org.jcrpg.ui.window.MainMenu;
import org.jcrpg.ui.window.Map;
import org.jcrpg.ui.window.OptionsMenu;
import org.jcrpg.ui.window.PartySetup;
import org.jcrpg.ui.window.PlayerChoiceWindow;
import org.jcrpg.ui.window.debug.CacheStateInfo;
import org.jcrpg.ui.window.element.ChoiceDescription;
import org.jcrpg.ui.window.interaction.BehaviorWindow;
import org.jcrpg.ui.window.interaction.EncounterWindow;
import org.jcrpg.ui.window.interaction.NormalActWindow;
import org.jcrpg.ui.window.interaction.PostEncounterWindow;
import org.jcrpg.ui.window.interaction.PreEncounterWindow;
import org.jcrpg.ui.window.interaction.TurnActWindow;
import org.jcrpg.ui.window.player.CharacterLevelingWindow;
import org.jcrpg.ui.window.player.CharacterSheetWindow;
import org.jcrpg.ui.window.player.InventoryWindow;
import org.jcrpg.ui.window.player.PartyOrderWindow;
import org.jcrpg.util.Language;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.orbiter.Orbiter;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.time.Time;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
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
import com.jme.renderer.pass.Pass;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Debug;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.stat.StatCollector;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;
import com.jmex.effects.glsl.BloomRenderPass;

public class J3DCore extends com.jme.app.BaseSimpleGame {

	public HashMap<String, Integer> hmCubeSideSubTypeToRenderedSideId = new HashMap<String, Integer>();

	public HashMap<Integer, RenderedSide> hm3dTypeRenderedSide = new HashMap<Integer, RenderedSide>();

	/**
	 * This is the maximum view distance with far enabled * 2, so that you
	 * cannot see the world's cubes twice from one viewpoint. :)
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

	public static final int MOVE_STEPS = 18;
	public static long TIME_TO_ENSURE = 14;

	public static Integer EMPTY_SIDE = new Integer(0);

	public static boolean OPTIMIZED_RENDERING = false;

	public static boolean MIPMAP_TREES = false;

	public static boolean MIPMAP_GLOBAL = true;

	public static int TEXTURE_QUALITY = 0;

	public static boolean SOUND_ENABLED = true;
	public static int MUSIC_VOLUME_PERCENT = 10;
	public static int EFFECT_VOLUME_PERCENT = 60;

	public static boolean BLOOM_EFFECT = false;
	public static boolean DOF_EFFECT = false;
	public static boolean DOF_DETAILED = false;
	public static boolean SHADOWS = true;

	public static boolean ANIMATED_GRASS = true;
	public static boolean DOUBLE_GRASS = true;

	public static boolean ANIMATED_TREES = true;
	public static boolean DETAILED_TREES = true;
	public static boolean DETAILED_TREE_FOLIAGE = true;
	public static boolean LOD_VEGETATION = false;
	public static boolean WATER_SHADER = false;
	public static boolean WATER_DETAILED = false;

	public static int FARVIEW_GAP = 4;
	public static boolean FARVIEW_ENABLED = false;

	public static boolean CONTINUOUS_LOAD = true;
	
	public static boolean DISABLE_DDS = false;

	public static boolean LOGGING = true;
	public static boolean FPSCOUNTER = true;
	public static boolean TEXTURE_SPLATTING = false;
	public static boolean SECONDARY_TEXTURES = false;
	public static boolean SLOW_ANIMATION = false;
	public static boolean VBO_ENABLED = true;
	
	public static boolean NORMALMAP_ENABLED = true;

	// Developer settings
	public static boolean WITHOUT_COMBATS = false;
	public static boolean QUICK_EXIT = true;

	// Controller settings
	public static boolean MOUSELOOK = false;
	
	static Properties p = new Properties();

	public static void loadConfig(String fileName) {
		try {
			File f = new File(fileName);
			FileInputStream fis = new FileInputStream(f);
			p.load(fis);

			FARVIEW_ENABLED = false;// XXX removing farview option, new geo
									// tiling is not working well with it,
			// especially the water part,
			// loadValue("FARVIEW_ENABLED", false);

			RENDER_DISTANCE_FARVIEW = 80;
			/*
			 * loadValue("RENDER_DISTANCE_FARVIEW", (int) (60 CUBE_EDGE_SIZE),
			 * (int) (20 CUBE_EDGE_SIZE), Integer.MAX_VALUE);
			 */
			RENDER_DISTANCE_FARVIEW /= CUBE_EDGE_SIZE;

			RENDER_DISTANCE = loadValue("RENDER_DISTANCE",
					(int) (10 * CUBE_EDGE_SIZE), (int) (10 * CUBE_EDGE_SIZE),
					Integer.MAX_VALUE);
			RENDER_DISTANCE /= CUBE_EDGE_SIZE;

			//if (CONTINUOUS_LOAD)
				//VIEW_DISTANCE = (int) (RENDER_DISTANCE * CUBE_EDGE_SIZE);
			//else
				VIEW_DISTANCE = loadValue("VIEW_DISTANCE", 10, 5,
						Integer.MAX_VALUE);
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
			
			CONTINUOUS_LOAD = loadValue("CONTINUOUS_LOAD", true);

			TEXTURE_QUALITY = loadValue("TEXTURE_QUALITY", 0, 0,
					Integer.MAX_VALUE);
			BLOOM_EFFECT = loadValue("BLOOM_EFFECT", false);
			DOF_EFFECT = loadValue("DOF_EFFECT", false);
			DOF_DETAILED = loadValue("DOF_DETAILED", false);
			ANIMATED_GRASS = loadValue("ANIMATED_GRASS", false);
			DOUBLE_GRASS = loadValue("DOUBLE_GRASS", false);
			SHADOWS = loadValue("SHADOWS", false);
			ANIMATED_TREES = loadValue("ANIMATED_TREES", false);
			DETAILED_TREES = loadValue("DETAILED_TREES", false);
			DETAILED_TREE_FOLIAGE = loadValue("DETAILED_TREE_FOLIAGE", false);
			ANTIALIAS_SAMPLES = loadValue("ANTIALIAS_SAMPLES", 0, 0, 8);
			WATER_SHADER = loadValue("WATER_SHADER", false);
			WATER_DETAILED = loadValue("WATER_DETAILED", false);

			SOUND_ENABLED = loadValue("SOUND_ENABLED", true);
			MUSIC_VOLUME_PERCENT = loadValue("MUSIC_VOLUME_PERCENT", 10, 0, 100);
			EFFECT_VOLUME_PERCENT = loadValue("EFFECT_VOLUME_PERCENT", 10, 0,
					100);
			LOGGING = loadValue("LOGGING", false);
			if (!LOGGING) {
				if (Jcrpg.LOGGER != null)
					Jcrpg.LOGGER.setLevel(Level.OFF);
			}
			FPSCOUNTER = loadValue("FPSCOUNTER", false);
			TEXTURE_SPLATTING = loadValue("TEXTURE_SPLATTING", false);
			SECONDARY_TEXTURES = loadValue("SECONDARY_TEXTURES", false);
			SLOW_ANIMATION = loadValue("SLOW_ANIMATION", false);
			VBO_ENABLED = loadValue("VBO_ENABLED", true);
			
			NORMALMAP_ENABLED = loadValue("NORMALMAP_ENABLED", true);

			// controller settings
			MOUSELOOK = loadValue("MOUSELOOK", false);
			
			// developer settings
			WITHOUT_COMBATS = loadValue("WITHOUT_COMBATS", false);
			QUICK_EXIT = loadValue("QUICK_EXIT", true);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static private boolean loadValue(String name, boolean defaultValue) {
		boolean result = defaultValue;
		try {
			result = parse(p.getProperty(name), defaultValue);
			p.setProperty(name, result + "");
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
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

	public GameStateContainer gameState = null;

	public ModelLoader modelLoader;
	public GeometryBatchHelper batchHelper = new GeometryBatchHelper(this);
	public GeometryBatchHelper batchHelperEncounterArea = new GeometryBatchHelper(
			this);
	public ModelPool modelPool = new ModelPool(this);

	public RenderedArea renderedArea = new RenderedArea();
	public RenderedArea renderedEncounterArea = new RenderedArea();

	public HashMap<String, Spatial> orbiters3D = new HashMap<String, Spatial>();
	public HashMap<String, LightNode[]> orbitersLight3D = new HashMap<String, LightNode[]>();

	public Node groundParentNode = new Node("groundParent");
	public Node dofParentNode = new Node("dofParent");
	/**
	 * skyparent for skysphere/sun/moon -> simple water reflection needs this
	 * node
	 */
	public Node skyParentNode = new Node("skyParent");
	/** external all root */
	public Node extRootNode;
	public Node extWaterRefNode; // reflected part
	/** internal all root */
	public Node intRootNode;
	public Node intWaterRefNode;

	/** encounter mode all root */
	public Node encounterExtRootNode;
	public Node encounterIntRootNode;

	/** skyroot */
	// Node skyRootNode = new Node();
	Sphere skySphere = null;

	/** ui root */
	public Node uiRootNode;

	/**
	 * Put quads with solid color depending on light power of orbiters,
	 * updateTimeRelated will update their shade.
	 */
	public static HashMap<Spatial, Spatial> hmSolidColorSpatials = new HashMap<Spatial, Spatial>();

	public void setGameState(GameStateContainer gameState) {
		this.gameState = gameState;
	}

	/**
	 * cube side rotation quaternion
	 */
	public static Quaternion qN, qS, qW, qE, qT, qB, qTexture;

	/**
	 * Horizontal Rotations
	 */
	static Quaternion horizontalN, horizontalS, horizontalW, horizontalE;
	static Quaternion horizontalNReal, horizontalSReal, horizontalWReal,
			horizontalEReal;

	/**
	 * Steep Rotations
	 */
	static Quaternion steepN, steepS, steepW, steepE;

	static QuaternionBuggy steepN_noRot, steepS_noRot, steepW_noRot,
			steepE_noRot;

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, TOP = 4,
			BOTTOM = 5;

	public static Vector3f dNorth = new Vector3f(0, 0, -1 * CUBE_EDGE_SIZE),
			dSouth = new Vector3f(0, 0, 1 * CUBE_EDGE_SIZE),
			dEast = new Vector3f(1 * CUBE_EDGE_SIZE, 0, 0),
			dWest = new Vector3f(-1 * CUBE_EDGE_SIZE, 0, 0);
	public static Vector3f[] directions = new Vector3f[] { dNorth, dEast,
			dSouth, dWest };

	public static Vector3f tdNorth = new Vector3f(0, 0, -1),
			tdSouth = new Vector3f(0, 0, 1), tdEast = new Vector3f(1, 0, 0),
			tdWest = new Vector3f(-1, 0, 0), tdTop = new Vector3f(0, 1, 0),
			tdBottom = new Vector3f(0, -1, 0);
	public static Vector3f[] turningDirectionsUnit = new Vector3f[] { tdNorth,
			tdEast, tdSouth, tdWest, tdTop, tdBottom };

	public static final float dirPostDelta = 0.7f;
	public static final Vector3f dpNorth = new Vector3f(0, 0, -dirPostDelta),
			dpSouth = new Vector3f(0, 0, dirPostDelta), dpEast = new Vector3f(
					dirPostDelta, 0, 0), dpWest = new Vector3f(-dirPostDelta,
					0, 0), dpTop = new Vector3f(0, dirPostDelta, 0),
			dpBottom = new Vector3f(0, -dirPostDelta, 0);
	/**
	 * The plus to the center of cube where player stands, used by
	 * getCurrentLocation.
	 */
	public static final Vector3f[] directionPositions = new Vector3f[] {
			dpNorth, dpEast, dpSouth, dpWest, dpTop, dpBottom };

	static {
		// creating rotation quaternions for all sides of a cube...
		qT = new Quaternion();
		qT.fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));
		qB = new Quaternion();
		qB.fromAngleAxis(FastMath.PI * 3 / 2, new Vector3f(1, 0, 0));
		qS = new Quaternion();
		qS.fromAngleAxis(FastMath.PI * 2, new Vector3f(0, 1, 0));
		qN = new Quaternion();
		qN.fromAngleAxis(FastMath.PI, new Vector3f(0, 1, 0));
		qE = new Quaternion();
		qE.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));
		qW = new Quaternion();
		qW.fromAngleAxis(FastMath.PI * 3 / 2, new Vector3f(0, 1, 0));
		qTexture = new Quaternion();
		qTexture.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 0, 1));
	}

	public static final HashMap<Integer, Vector3f> viewDirectionTranslation = new HashMap<Integer, Vector3f>();
	static {
		viewDirectionTranslation.put(new Integer(NORTH), new Vector3f(0, 0, 1));
		viewDirectionTranslation
				.put(new Integer(SOUTH), new Vector3f(0, 0, -1));
		viewDirectionTranslation.put(new Integer(WEST), new Vector3f(1, 0, 0));
		viewDirectionTranslation.put(new Integer(EAST), new Vector3f(-1, 0, 0));

	}

	public static final HashMap<Integer, Object[]> directionAnglesAndTranslations = new HashMap<Integer, Object[]>();
	static {
		directionAnglesAndTranslations.put(new Integer(NORTH), new Object[] {
				qN, new int[] { 0, 0, -1 } });
		directionAnglesAndTranslations.put(new Integer(SOUTH), new Object[] {
				qS, new int[] { 0, 0, 1 } });
		directionAnglesAndTranslations.put(new Integer(WEST), new Object[] {
				qW, new int[] { -1, 0, 0 } });
		directionAnglesAndTranslations.put(new Integer(EAST), new Object[] {
				qE, new int[] { 1, 0, 0 } });
		directionAnglesAndTranslations.put(new Integer(TOP), new Object[] { qT,
				new int[] { 0, 1, 0 } });
		directionAnglesAndTranslations.put(new Integer(BOTTOM), new Object[] {
				qB, new int[] { 0, -1, 0 } });
	}

	public static final HashMap<Integer, Integer> oppositeDirections = new HashMap<Integer, Integer>();
	static {
		oppositeDirections.put(new Integer(NORTH), new Integer(SOUTH));
		oppositeDirections.put(new Integer(SOUTH), new Integer(NORTH));
		oppositeDirections.put(new Integer(WEST), new Integer(EAST));
		oppositeDirections.put(new Integer(EAST), new Integer(WEST));
		oppositeDirections.put(new Integer(TOP), new Integer(BOTTOM));
		oppositeDirections.put(new Integer(BOTTOM), new Integer(TOP));
	}
	public static final HashMap<Integer, Integer> nextDirections = new HashMap<Integer, Integer>();
	static {
		nextDirections.put(new Integer(NORTH), new Integer(WEST));
		nextDirections.put(new Integer(SOUTH), new Integer(EAST));
		nextDirections.put(new Integer(EAST), new Integer(SOUTH));
		nextDirections.put(new Integer(WEST), new Integer(NORTH));
		nextDirections.put(new Integer(TOP), new Integer(BOTTOM));
		nextDirections.put(new Integer(BOTTOM), new Integer(TOP));
	}
	public static final HashMap<Integer, Quaternion> horizontalRotations = new HashMap<Integer, Quaternion>();
	static {
		// horizontal rotations
		horizontalN = new QuaternionBuggy();
		horizontalN.fromAngles(new float[] { 0, 0, FastMath.PI * 2 });
		horizontalS = new QuaternionBuggy();
		horizontalS.fromAngles(new float[] { 0, 0, FastMath.PI });
		horizontalW = new QuaternionBuggy();
		horizontalW.fromAngles(new float[] { 0, 0, FastMath.PI / 2 });
		horizontalE = new QuaternionBuggy();
		horizontalE.fromAngles(new float[] { 0, 0, FastMath.PI * 3 / 2 });

		horizontalRotations.put(new Integer(NORTH), horizontalN);
		horizontalRotations.put(new Integer(SOUTH), horizontalS);
		horizontalRotations.put(new Integer(WEST), horizontalW);
		horizontalRotations.put(new Integer(EAST), horizontalE);
	}

	public static final HashMap<Integer, Quaternion> horizontalRotationsReal = new HashMap<Integer, Quaternion>();
	static {
		// horizontal rotations
		horizontalNReal = new QuaternionBuggy();
		horizontalNReal.fromAngles(new float[] { 0, FastMath.PI * 2, 0 });
		horizontalSReal = new QuaternionBuggy();
		horizontalSReal.fromAngles(new float[] { 0, FastMath.PI, 0 });
		horizontalWReal = new QuaternionBuggy();
		horizontalWReal.fromAngles(new float[] { 0, FastMath.PI / 2, 0 });
		horizontalEReal = new QuaternionBuggy();
		horizontalEReal.fromAngles(new float[] { 0, FastMath.PI * 3 / 2, 0 });

		horizontalRotationsReal.put(new Integer(NORTH), horizontalNReal);
		horizontalRotationsReal.put(new Integer(SOUTH), horizontalSReal);
		horizontalRotationsReal.put(new Integer(WEST), horizontalWReal);
		horizontalRotationsReal.put(new Integer(EAST), horizontalEReal);
	}

	public static final HashMap<Integer, Quaternion> steepRotations = new HashMap<Integer, Quaternion>();
	static {
		// steep rotations
		steepE = new QuaternionBuggy();
		steepE.fromAngles(new float[] { 0, FastMath.PI / 4, 0 });
		steepW = new QuaternionBuggy();
		steepW.fromAngles(new float[] { 0, -FastMath.PI / 4, 0 });
		steepS = new QuaternionBuggy();
		steepS.fromAngles(new float[] { FastMath.PI / 4, 0, 0 });
		steepN = new QuaternionBuggy();
		steepN.fromAngles(new float[] { -FastMath.PI / 4, 0, 0 });

		steepRotations.put(new Integer(NORTH), steepN);
		steepRotations.put(new Integer(SOUTH), steepS);
		steepRotations.put(new Integer(WEST), steepW);
		steepRotations.put(new Integer(EAST), steepE);
	}

	public static final HashMap<Integer, Quaternion> steepRotations_special = new HashMap<Integer, Quaternion>();
	static {
		// steep rotations with special in-one-step rotation
		steepE_noRot = new QuaternionBuggy();
		steepE_noRot.fromAngles(new float[] { FastMath.PI / 2, 0,
				3 * FastMath.PI / 4 });
		steepW_noRot = new QuaternionBuggy();
		steepW_noRot.fromAngles(new float[] { -FastMath.PI / 2, 0,
				FastMath.PI / 4 });
		steepS_noRot = new QuaternionBuggy();
		steepS_noRot.fromAngles(new float[] { 0, FastMath.PI / 4,
				FastMath.PI / 2 });
		steepN_noRot = new QuaternionBuggy();
		steepN_noRot.fromAngles(new float[] { 0, -3 * FastMath.PI / 4,
				-FastMath.PI / 2 });

		steepRotations_special.put(new Integer(NORTH), steepN_noRot);
		steepRotations_special.put(new Integer(SOUTH), steepS_noRot);
		steepRotations_special.put(new Integer(WEST), steepW_noRot);
		steepRotations_special.put(new Integer(EAST), steepE_noRot);
	}
	// farview steeps
	public static final HashMap<Integer, Quaternion> steepRotations_FARVIEW = new HashMap<Integer, Quaternion>();
	static {
		// steep rotations
		Quaternion steepE = new QuaternionBuggy();
		steepE
				.fromAngles(new float[] { 0, (FastMath.PI / 4) / FARVIEW_GAP, 0 });
		Quaternion steepW = new QuaternionBuggy();
		steepW
				.fromAngles(new float[] { 0, (-FastMath.PI / 4) / FARVIEW_GAP,
						0 });
		Quaternion steepS = new QuaternionBuggy();
		steepS
				.fromAngles(new float[] { (FastMath.PI / 4) / FARVIEW_GAP, 0, 0 });
		Quaternion steepN = new QuaternionBuggy();
		steepN
				.fromAngles(new float[] { (-FastMath.PI / 4) / FARVIEW_GAP, 0,
						0 });

		steepRotations_FARVIEW.put(new Integer(NORTH), steepN);
		steepRotations_FARVIEW.put(new Integer(SOUTH), steepS);
		steepRotations_FARVIEW.put(new Integer(WEST), steepW);
		steepRotations_FARVIEW.put(new Integer(EAST), steepE);
	}

	public static final HashMap<Integer, Quaternion> steepRotations_special_FARVIEW = new HashMap<Integer, Quaternion>();
	static {
		// steep rotations with special in-one-step rotation
		Quaternion steepE_noRot = new QuaternionBuggy();
		steepE_noRot.fromAngles(new float[] { FastMath.PI / 2, 0,
				(3 * FastMath.PI / 4) / FARVIEW_GAP });
		Quaternion steepW_noRot = new QuaternionBuggy();
		steepW_noRot.fromAngles(new float[] { -FastMath.PI / 2, 0,
				(FastMath.PI / 4) / FARVIEW_GAP });
		Quaternion steepS_noRot = new QuaternionBuggy();
		steepS_noRot.fromAngles(new float[] { 0,
				(FastMath.PI / 4) / FARVIEW_GAP, FastMath.PI / 2 });
		Quaternion steepN_noRot = new QuaternionBuggy();
		steepN_noRot.fromAngles(new float[] { 0,
				(-3 * FastMath.PI / 4) / FARVIEW_GAP, -FastMath.PI / 2 });

		steepRotations_special_FARVIEW.put(new Integer(NORTH), steepN_noRot);
		steepRotations_special_FARVIEW.put(new Integer(SOUTH), steepS_noRot);
		steepRotations_special_FARVIEW.put(new Integer(WEST), steepW_noRot);
		steepRotations_special_FARVIEW.put(new Integer(EAST), steepE_noRot);
	}

	public static final HashMap<Integer, int[]> moveTranslations = new HashMap<Integer, int[]>();
	static {
		moveTranslations.put(new Integer(NORTH), new int[] { 0, 0, 1 });
		moveTranslations.put(new Integer(SOUTH), new int[] { 0, 0, -1 });
		moveTranslations.put(new Integer(WEST), new int[] { -1, 0, 0 });
		moveTranslations.put(new Integer(EAST), new int[] { 1, 0, 0 });
		moveTranslations.put(new Integer(TOP), new int[] { 0, 1, 0 });
		moveTranslations.put(new Integer(BOTTOM), new int[] { 0, -1, 0 });
	}

	public static final float[][] TREE_LOD_DIST_HIGH = new float[][] {
			{ 0f, 8f }, { 8f, 16f }, { 16f, 24f }, { 24f, 50f } };
	public static final float[][] TREE_LOD_DIST_LOW = new float[][] {
			{ 0f, 0f }, { 0f, 10f }, { 10f, 20f }, { 20f, 40f } };

	public Language language = null;

	public J3DCore() {
		self = this;
		if (J3DCore.SHADOWS)
			stencilBits = 8;
		alphaBits = 0;
		depthBits = 4;
		samples = ANTIALIAS_SAMPLES;

		language = new Language("en");

		new SideTypeModels().fillMap(hmCubeSideSubTypeToRenderedSideId,
				hm3dTypeRenderedSide, MIPMAP_TREES, DETAILED_TREES,
				RENDER_GRASS_DISTANCE, LOD_VEGETATION);

	}

	public void initCore() {
		try {
			this.setConfigShowMode(ConfigShowMode.AlwaysShow,new File("./data/ui/settings.png").toURL());
		} catch (Exception ex)
		{
			this.setConfigShowMode(ConfigShowMode.AlwaysShow);
		}
		this.start();
	}

	private static final Logger logger = Logger.getLogger(J3DCore.class
			.getName());

	protected void initSystem() throws JmeException {
		logger.info(getVersion());
		try {
			/**
			 * Get a DisplaySystem acording to the renderer selected in the
			 * startup box.
			 */
			display = DisplaySystem.getDisplaySystem(settings.getRenderer());

			display.setMinDepthBits(depthBits);
			display.setMinStencilBits(stencilBits);
			display.setMinAlphaBits(alphaBits);
			display.setMinSamples(samples);

			/** Create a window with the startup box's information. */
			display.createWindow(settings.getWidth(), settings.getHeight(),
					settings.getDepth(), settings.getFrequency(), settings.
							isFullscreen());
			logger.info("Running on: " + display.getAdapter()
					+ "\nDriver version: " + display.getDriverVersion() + "\n"
					+ display.getDisplayVendor() + " - "
					+ display.getDisplayRenderer() + " - "
					+ display.getDisplayAPIVersion());

			/**
			 * Create a camera specific to the DisplaySystem that works with the
			 * display's width and height
			 */
			cam = display.getRenderer().createCamera(display.getWidth(),
					display.getHeight());

		} catch (JmeException e) {
			/**
			 * If the displaysystem can't be initialized correctly, exit
			 * instantly.
			 */
			logger.log(Level.SEVERE, "Could not create displaySystem", e);
			System.exit(1);
		}

		/** Set a black background. */
		display.getRenderer().setBackgroundColor(ColorRGBA.black.clone());

		/** Set up how our camera sees. */
		cameraPerspective();
		Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
		/** Move our camera to a correct place and orientation. */
		cam.setFrame(loc, left, up, dir);
		/** Signal that we've changed our camera's location/frustum. */
		cam.update();
		/** Assign the camera to this renderer. */
		display.getRenderer().setCamera(cam);

		/** Create a basic input controller. */
		FirstPersonHandler firstPersonHandler = new FirstPersonHandler(cam, 50,
				1);
		input = firstPersonHandler;

		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();

		/** Sets the title of our display. */
		String className = getClass().getName();
		if (className.lastIndexOf('.') > 0)
			className = className.substring(className.lastIndexOf('.') + 1);
		display.setTitle("Java Classic RPG - prealpha");
		/**
		 * Signal to the renderer that it should keep track of rendering
		 * information.
		 */
		//display.getRenderer().enableStatistics(true); // TODO

		/** Assign key P to action "toggle_pause". */
		if (true == true) {
			KeyBindingManager.getKeyBindingManager().set("toggle_pause",
					KeyInput.KEY_P);
			/** Assign key ADD to action "step". */
			KeyBindingManager.getKeyBindingManager().set("step",
					KeyInput.KEY_ADD);
			/** Assign key T to action "toggle_wire". */
			KeyBindingManager.getKeyBindingManager().set("toggle_wire",
					KeyInput.KEY_T);
			/** Assign key L to action "toggle_lights". */
			KeyBindingManager.getKeyBindingManager().set("toggle_lights",
					KeyInput.KEY_L);
			/** Assign key B to action "toggle_bounds". */
			KeyBindingManager.getKeyBindingManager().set("toggle_bounds",
					KeyInput.KEY_B);
			/** Assign key N to action "toggle_normals". */
			KeyBindingManager.getKeyBindingManager().set("toggle_normals",
					KeyInput.KEY_N);

			/** Assign key C to action "camera_out". */
			KeyBindingManager.getKeyBindingManager().set("camera_out",
					KeyInput.KEY_C);
		}
		KeyBindingManager.getKeyBindingManager().set("screen_shot_jcrpg",
				KeyInput.KEY_F12);
		if (QUICK_EXIT) {
			KeyBindingManager.getKeyBindingManager().set("exit",
					KeyInput.KEY_ESCAPE);
		}
        KeyBindingManager.getKeyBindingManager().set( "toggle_stats",
                KeyInput.KEY_F9 );
		/*
		 * KeyBindingManager.getKeyBindingManager().set( "parallel_projection",
		 * KeyInput.KEY_F2 ); KeyBindingManager.getKeyBindingManager().set(
		 * "toggle_depth", KeyInput.KEY_F3 );
		 * KeyBindingManager.getKeyBindingManager().set("mem_report",
		 * KeyInput.KEY_R);
		 */
		input = new ClassicInputHandler(this, cam);
	}

	public DisplaySystem getDisplay() {
		if (display == null)
			return DisplaySystem.getDisplaySystem();
		return display;
	}

	public Camera getCamera() {
		return cam;
	}

	public HashMap<LensFlare, Node> flares = new HashMap<LensFlare, Node>();

	/**
	 * Creates the spatials (spheres) for a world orbiter
	 * 
	 * @param o
	 * @return
	 */
	public Spatial createSpatialForOrbiter(Orbiter o) {
		if (o.type.equals(SimpleSun.SIMPLE_SUN_ORBITER)) {
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
			//extLightState.setTwoSidedLighting(false);

			lightNode = new LightNode("light");
			lightNode.setLight(dr);
			
			skydomeLightState.attach(dr);

			skyParentNode.setRenderState(skydomeLightState);
			//lightNode.setTarget(skyParentNode);
			lightNode.setLocalTranslation(new Vector3f(-4f, -4f, -4f));

			// Setup the lensflare textures.
			TextureState[] tex = new TextureState[4];
			tex[0] = display.getRenderer().createTextureState();
			tex[0].setTexture(TextureManager.loadTexture(
					"./data/flare/flare1.png", Texture.MinificationFilter.BilinearNoMipMaps,
					Texture.MagnificationFilter.NearestNeighbor, Image.Format.RGBA8, 1.0f, true));
			tex[0].setEnabled(true);

			tex[1] = display.getRenderer().createTextureState();
			tex[1].setTexture(TextureManager.loadTexture(
					"./data/flare/flare2.png", Texture.MinificationFilter.BilinearNoMipMaps,
					Texture.MagnificationFilter.NearestNeighbor));
			tex[1].setEnabled(true);

			tex[2] = display.getRenderer().createTextureState();
			tex[2].setTexture(TextureManager.loadTexture(
					("./data/flare/flare3.png"), Texture.MinificationFilter.BilinearNoMipMaps,
					Texture.MagnificationFilter.NearestNeighbor));
			tex[2].setEnabled(true);

			tex[3] = display.getRenderer().createTextureState();
			tex[3].setTexture(TextureManager.loadTexture(
					("./data/flare/flare4.png"), Texture.MinificationFilter.BilinearNoMipMaps,
					Texture.MagnificationFilter.NearestNeighbor));
			tex[3].setEnabled(true);

			flare = LensFlareFactory.createBasicLensFlare("flare", tex);
			// flare.setIntensity(J3DCore.BLOOM_EFFECT?0.0001f:1.0f);
			flare.setRootNode(groundParentNode);
			skyParentNode.attachChild(lightNode);
			flares.put(flare, flare.getRootNode());

			// notice that it comes at the end
			lightNode.attachChild(flare);

			TriMesh sun = new Sphere(o.id, 20, 20, 3.5f);
			Node sunNode = new Node("SUN");
			sunNode.attachChild(sun);
			skyParentNode.attachChild(sunNode);

			Texture texture = TextureManager.loadTexture("./data/textures/low/"
					+ "sun.png", Texture.MinificationFilter.BilinearNoMipMaps,
					Texture.MagnificationFilter.NearestNeighbor);

			texture.setWrap(Texture.WrapMode.Repeat);//WM_WRAP_S_WRAP_T);
			texture.setApply(Texture.ApplyMode.Replace);//AM_REPLACE);
			texture.setRotation(J3DCore.qTexture);

			TextureState ts = getDisplay().getRenderer().createTextureState();
			ts.setTexture(texture, 0);

			ts.setEnabled(true);
			sun.setRenderState(ts);
			sun.setRenderState(getDisplay().getRenderer().createFogState());

			lightNode.attachChild(sun);
			sunNode.attachChild(lightNode);
			return lightNode;

		} else if (o.type.equals(SimpleMoon.SIMPLE_MOON_ORBITER)) {
			TriMesh moon = new Sphere(o.id, 20, 20, 3.5f);

			Texture texture = TextureManager.loadTexture(
					"./data/orbiters/moon2.jpg", Texture.MinificationFilter.BilinearNoMipMaps,
					Texture.MagnificationFilter.NearestNeighbor);//Texture.MM_LINEAR,
					//Texture.FM_LINEAR);

			if (texture != null) {

				texture.setWrap(Texture.WrapMode.Repeat);//WM_WRAP_S_WRAP_T);
				texture.setApply(Texture.ApplyMode.Replace);//AM_REPLACE);
				texture.setRotation(qTexture);
				TextureState state = DisplaySystem.getDisplaySystem()
						.getRenderer().createTextureState();
				state.setTexture(texture, 0);

				state.setEnabled(true);

				moon.setRenderState(state);
			}
			moon.updateRenderState();

			skyParentNode.attachChild(moon);
			moon.setLightCombineMode(LightCombineMode.Off);
			moon.setRenderState(getDisplay().getRenderer().createFogState());
			return moon;
		}
		return null;

	}

	public LightState extLightState, skydomeLightState = null, internalLightState,
			encounterIntLightState;

	/**
	 * Creates the lights for a world orbiter
	 * 
	 * @param o
	 * @return
	 */
	public LightNode[] createLightsForOrbiter(Orbiter o) {
		if (o.type.equals(SimpleSun.SIMPLE_SUN_ORBITER)) {
			LightNode dirLightNode = new LightNode("Sun light " + o.id
					);
			DirectionalLight dirLight = new DirectionalLight();
			dirLight.setDiffuse(new ColorRGBA(1, 1, 1, 1));
			dirLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f, 0.6f));
			dirLight.setDirection(new Vector3f(0, 0, 1));
			dirLight.setEnabled(true);
			dirLightNode.setLight(dirLight);
			
			dirLight.setShadowCaster(true);
			extLightState.attach(dirLight);
			extRootNode.setRenderState(extLightState);

			LightNode pointLightNode = new LightNode("Sun spotlight " + o.id);
			PointLight pointLight = new PointLight();
			pointLight.setDiffuse(new ColorRGBA(1, 1, 1, 0));
			pointLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f, 0));
			pointLight.setEnabled(true);
			pointLight.setShadowCaster(false);
			pointLight.setAttenuate(false); // fog looks BAD with attenuation,
											// switching off
			pointLight.setLinear(0.0002f);
			pointLightNode.setLight(pointLight);
			skydomeLightState.attach(pointLight);

			return new LightNode[] { dirLightNode, pointLightNode };
		} else if (o.type.equals(SimpleMoon.SIMPLE_MOON_ORBITER)) {
			LightNode dirLightNode = new LightNode("Moon light " + o.id);
			DirectionalLight dirLight = new DirectionalLight();
			dirLight.setDiffuse(new ColorRGBA(1, 1, 1, 1));
			dirLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f, 1));
			dirLight.setDirection(new Vector3f(0, 0, 1));
			dirLight.setShadowCaster(false);// moon shouldnt cast shadow (?)
			dirLight.setEnabled(true);
			dirLightNode.setLight(dirLight);
			extLightState.attach(dirLight);
			extRootNode.setRenderState(extLightState);

			LightNode pointLightNode = new LightNode("Moon spotlight " + o.id);
			SpotLight pointLight = new SpotLight();
			pointLight.setDiffuse(new ColorRGBA(1, 1, 1, 1));
			pointLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f, 1));
			pointLight.setDirection(new Vector3f(0, 0, 1));
			pointLight.setEnabled(true);
			pointLight.setAngle(180);
			pointLight.setShadowCaster(false);
			pointLightNode.setLight(pointLight);
			skydomeLightState.attach(pointLight);

			return new LightNode[] { dirLightNode, pointLightNode };
		}
		return null;

	}

	public Thread engineThread = null;

	@Override
	protected void initGame() {
		display.setVSyncEnabled(true);
		pManager = new BasicPassManager();
		//System.setProperty("jme.stats", "true");
		//showGraphs = true;
		//Debug.updateGraphs = true;
		super.initGame();
		if (Debug.stats)
		{
	        Text f9Hint = new Text("f9", "F9 - toggle stats") {
	            private static final long serialVersionUID = 1L;
	            @Override
	            public void draw(Renderer r) {
	                StatCollector.pause();
	                super.draw(r);
	                StatCollector.resume();
	            }
	        };
	        f9Hint.setCullHint( Spatial.CullHint.Never );
	        f9Hint.setRenderState( Text.getDefaultFontTextureState() );
	        f9Hint.setRenderState( Text.getFontBlend() );
	        f9Hint.setLocalScale(.8f);
	        f9Hint.setTextColor(ColorRGBA.gray);
	        f9Hint.setLocalTranslation(display.getRenderer().getWidth() - f9Hint.getWidth() - 15, display.getRenderer().getHeight() - f9Hint.getHeight() - 10, 0);
	        for (Spatial s:graphNode.getChildren())
	        {
	        	if (s instanceof Text)
	        	{
	        		s.removeFromParent();
	        	}
	        }
	        graphNode.attachChild(f9Hint);
	        graphNode.updateRenderState();
		}

	}

	/**
	 * Updates all time related things in the 3d world
	 */
	public void updateTimeRelated() {
		updateTimeRelated(true);
	}

	public void updateTimeRelated(boolean modifyLights) {

		Time localTime = gameState.engine.getWorldMeanTime().getLocalTime(
				gameState.world, gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ);
		CubeClimateConditions conditions = gameState.world.climate
				.getCubeClimate(localTime,
						gameState.getNormalPositions().viewPositionX, gameState
								.getNormalPositions().viewPositionY, gameState
								.getNormalPositions().viewPositionZ, false);
		uiBase.hud.meter.updateQuad(
				gameState.getNormalPositions().viewDirection, localTime);
		gameState.world.worldMap.update(
				gameState.getNormalPositions().viewPositionX
						/ gameState.world.magnification, gameState
						.getNormalPositions().viewPositionY
						/ gameState.world.magnification, gameState
						.getNormalPositions().viewPositionZ
						/ gameState.world.magnification);
		uiBase.hud.localMap.update(
				gameState.getNormalPositions().viewPositionX, gameState
						.getNormalPositions().viewPositionY, gameState
						.getNormalPositions().viewPositionZ, gameState
						.getNormalPositions().viewDirection);
		uiBase.hud.update();

		/*
		 * Orbiters
		 */
		boolean updateRenderState = false;
		float[] vTotal = new float[3];
		// iterating through gameState.world's sky orbiters
		for (Orbiter orb : gameState.world.getOrbiterHandler().orbiters
				.values()) {
			if (orbiters3D.get(orb.id) == null) {
				Spatial s = createSpatialForOrbiter(orb);
				s.removeFromParent(); // workaround some internal mess
				orbiters3D.put(orb.id, s);
			}
			if (orbitersLight3D.get(orb.id) == null) {
				LightNode[] l = createLightsForOrbiter(orb);
				if (l != null)
					orbitersLight3D.put(orb.id, l);
			}
			Spatial s = orbiters3D.get(orb.id); // get 3d Spatial for the
												// orbiter
			LightNode l[] = orbitersLight3D.get(orb.id);
			float[] orbiterCoords = orb.getCurrentCoordinates(localTime,
					conditions); // get coordinates of the orbiter
			Vector3f orbiterVector = null;
			if (orbiterCoords != null) {
				orbiterVector = new Vector3f(orbiterCoords[0],
						orbiterCoords[1], orbiterCoords[2]).add(cam
						.getLocation());
				if (s.getParent() == null) {
					// newly appearing, attach to root
					skyParentNode.attachChild(s);
					updateRenderState = true;
				}
				s.setLocalTranslation(orbiterVector);
				if (J3DCore.SHADOWS)
				{
					sPass.setEnabled(orb.needsShadowPass);
				}
				// s.updateRenderState();
			} else {
				// if there is no coordinates, detach the orbiter
				s.removeFromParent();
			}
			if (l != null) {

				float[] lightDirectionCoords = orb.getLightDirection(localTime,
						conditions);
				if (lightDirectionCoords != null) {
					// 0. is directional light for the planet surface
					l[0].getLight().setEnabled(true);
					Vector3f dir = new Vector3f(lightDirectionCoords[0],
							lightDirectionCoords[1], lightDirectionCoords[2])
							.normalizeLocal();
					((DirectionalLight) l[0].getLight()).setDirection(dir);

					if (J3DCore.SHADOWS) {
						//Vector3f dir2 = dir.clone();
						//dir2.y *= 2f;
						sPass.setDirection(dir);
						//System.out.println("___ "+dir2);
					}

					//l[0].setTarget(extRootNode);
					extLightState.attach(l[0].getLight());
					float[] v = orb.getLightPower(localTime, conditions);
					vTotal[0] += v[0];
					vTotal[1] += v[1];
					vTotal[2] += v[2];
					ColorRGBA c = new ColorRGBA(v[0], v[1], v[2], 0.3f);
					float cV = (v[0]+v[1]+v[2])/3f;
					ColorRGBA a = new ColorRGBA(cV, cV, cV, 1.0f);

					l[0].getLight().setDiffuse(c);// c);//new
													// ColorRGBA(1,1,1,1));
					l[0].getLight().setAmbient(a.clone().multLocal(0.5f));
					l[0].getLight().setSpecular(c);
					l[0].getLight().setShadowCaster(true);
					extRootNode.setRenderState(extLightState);

					// 1. is point light for the skysphere
					l[1].getLight().setEnabled(true);
					//l[1].setTarget(skySphere);
					skydomeLightState.attach(l[1].getLight());
					c = new ColorRGBA(v[0], v[1], v[2], 0.6f);
					l[1].getLight().setDiffuse(c);
					l[1].getLight().setAmbient(c);
					l[1].getLight().setSpecular(c);
					if (updateRenderState) {
						// this is a workaround, lightstate seems to move to the
						// parent, don't know why. \
						// Clearing it helps:
						skyParentNode.setRenderState(skydomeLightState);
						groundParentNode.clearRenderState(RenderState.RS_LIGHT);
						groundParentNode.updateRenderState();
					}
					skyParentNode.attachChild(l[1]);
					l[1].setLocalTranslation(new Vector3f(orbiterCoords[0],
							orbiterCoords[1], orbiterCoords[2]).add(cam
							.getLocation()));

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

		// this part sets the naive veg quads to a mixed color of the light's
		// value with the quad texture!
		int counter = 0;
		synchronized (hmSolidColorSpatials)
		{
			for (Spatial q : hmSolidColorSpatials.values()) {
				counter++;
				// q.setSolidColor(new
				// ColorRGBA(vTotal+0.2f,vTotal+0.2f,vTotal+0.2f,1));
				if ((q instanceof TriMesh)) {
					((TriMesh) q).setSolidColor(new ColorRGBA(vTotal[0] / 0.3f,
							vTotal[1] / 0.3f, vTotal[2] / 0.3f, 1f));
				} else {
					((TriMesh) ((Node) q).getChild(0)).setSolidColor(new ColorRGBA(
							vTotal[0] / 0.3f, vTotal[1] / 0.3f, vTotal[2] / 0.3f,
							1f));
				}
			}
		}
		/*if (ms!=null)
		{
			ms.setDiffuse(new ColorRGBA(vTotal[0] / 2.3f,
					vTotal[1] / 2.3f, vTotal[2] / 2.3f, 1f));
			ms.setSpecular(new ColorRGBA(vTotal[0] / 2.3f,
					vTotal[1] / 2.3f, vTotal[2] / 2.3f, 1f));
			ms.setAmbient(new ColorRGBA(vTotal[0] / 2.3f,
					vTotal[1] / 2.3f, vTotal[2] / 2.3f, 1f));
		}*/
		
		// set fog state color to the light power !
		fs_external.setColor(new ColorRGBA(vTotal[0] / 2f, vTotal[1] / 1.5f,
				vTotal[2] / 1.1f, 1f));
		fs_external_special.setColor(new ColorRGBA(vTotal[0] / 2f,
				vTotal[1] / 1.5f, vTotal[2] / 1.1f, 1f));

		// SKYSPHERE
		// moving skysphere with camera
		Vector3f sV3f = new Vector3f(cam.getLocation());
		sV3f.y -= 10;
		skySphere.setLocalTranslation(sV3f);
		// Animating skySphere rotated...
		Quaternion qSky = new Quaternion();
		qSky.fromAngleAxis(
				FastMath.PI * 0.5f,//localTime.getCurrentDayPercent() / 100,
				new Vector3f(0, 0, -1));
		skySphere.setLocalRotation(qSky);

		// if (skyParentNode.getParent()==null)
		{			
			groundParentNode.attachChild(skyParentNode);
		}
		if (skySphere.getParent() == null) {
			skyParentNode.attachChild(skySphere);
		}
		if (gameState.getCurrentRenderPositions().internalLight) {
			skyParentNode.setCullHint(CullHint.Always);
			skySphere.setCullHint(CullHint.Always);
		} else {
			skyParentNode.setCullHint(CullHint.Dynamic);
			skySphere.setCullHint(CullHint.Dynamic);
		}
		skySphere.updateRenderState(); // do not update root or
										// groundParentNode, no need for that
										// here

		if (updateRenderState) {
			groundParentNode.updateRenderState(); // this is a must, moon will
													// see through the house if
													// not!
			uiRootNode.updateRenderState();
		}
		quadToFixHUDCulling.setLocalTranslation(cam.getLocation());
	}

	public HashSet<NodePlaceholder> possibleOccluders = new HashSet<NodePlaceholder>();

	/**
	 * Removes node and all subnodes from shadowrenderpass. Use it when removing
	 * node from scenario!
	 * 
	 * @param s
	 *            Node.
	 */
	public void removeOccludersRecoursive(Node s) {
		if (s == null)
			return;
		sPass.removeOccluder(s);
		if (s.getChildren() != null)
			for (Spatial c : s.getChildren()) {
				if ((c instanceof Node)) {
					removeOccludersRecoursive((Node) c);
				}
			}
	}

	/**
	 * Removes node and all subnodes from solid color quads. Use it when
	 * removing node from scenario!
	 * 
	 * @param s
	 *            Node.
	 */
	public void removeSolidColorQuadsRecoursive(Node s) {
		hmSolidColorSpatials.remove(s);
		((Node) s).removeUserData("rotateOnSteep");
		if (s.getChildren() != null)
			for (Spatial c : s.getChildren()) {
				if (c instanceof BillboardPartVegetation) {
					hmSolidColorSpatials
							.remove(((BillboardPartVegetation) c).targetQuad);
					((BillboardPartVegetation) c).targetQuad = null;

				}
				if (c instanceof Node) {
					hmSolidColorSpatials.remove(c);
					removeSolidColorQuadsRecoursive((Node) c);
					((Node) c).removeUserData("rotateOnSteep");
				}
				if (c instanceof TriMesh) {
					hmSolidColorSpatials.remove(c);
					// c.removeFromParent();
				}
				/*
				 * for (int i=0; i<RenderState.RS_MAX_STATE; i++) {
				 * //c.getRenderState(i);
				 * 
				 * c.clearRenderState(i); }
				 */
			}
	}

	public int garbCollCounter = 0;

	public static boolean OPTIMIZE_ANGLES = false;
	public static float ROTATE_VIEW_ANGLE = OPTIMIZE_ANGLES ? 2.5f : 3.15f;

	public static boolean GEOMETRY_BATCH = true;
	public static boolean GRASS_BIG_BATCH = true;

	/**
	 * Sets the camera to its proper current position
	 */
	public void setCalculatedCameraLocation() {
		cam.setLocation(getCurrentLocation());
		if (J3DCore.WATER_SHADER) {
			waterEffectRenderPass.setWaterHeight(cam.getLocation().y);
		}
	}

	public Vector3f getCurrentLocation() {
		float middleHeight = 0;
		boolean cubeMiddleHeight = false;
		Cube c = null;
		try {
			c = gameState.world.getCube(-1, gameState
					.getCurrentRenderPositions().viewPositionX, gameState
					.getCurrentRenderPositions().viewPositionY, gameState
					.getCurrentRenderPositions().viewPositionZ, false);
			middleHeight = c.middleHeight;
			cubeMiddleHeight = c.cornerHeights != null;
			if (c.sides != null)
				for (Side[] sides : c.sides) {
					if (sides != null)
						for (Side s : sides) {
							if (s != null)
								if (s.subtype.overrideGeneratedTileMiddleHeight) {
									cubeMiddleHeight = false;
									break;
								}
						}
				}
		} catch (Exception ex) {
		}
		;
		float bonus = (gameState.getCurrentRenderPositions().onSteep ? 1.5f
				: 0f);
		float middleHeightBonus = CUBE_EDGE_SIZE * (middleHeight + 0.2f);
		if (cubeMiddleHeight || middleHeightBonus > bonus
				&& (c == null || !c.internalCube && !c.internalLight)) {
			bonus = CUBE_EDGE_SIZE * (middleHeight + 0.2f);
		}
		// if (c!=null) System.out.println("C:"+c);
		Vector3f v = new Vector3f(
				gameState.getCurrentRenderPositions().relativeX
						* CUBE_EDGE_SIZE,
				gameState.getCurrentRenderPositions().relativeY
						* CUBE_EDGE_SIZE + 0.11f + bonus, -1
						* gameState.getCurrentRenderPositions().relativeZ
						* CUBE_EDGE_SIZE);
		Vector3f fromPos = J3DCore.directionPositions[gameState
				.getCurrentRenderPositions().viewDirection];
		v.addLocal(fromPos.negate());
		return v;
	}

	/**
	 * Tells if any of a set of sides is of a set of sideSubTypes.
	 * 
	 * @param sides
	 * @param classNames
	 * @return
	 */
	public boolean hasSideOfInstance(Side[] sides,
			Set<Class<? extends SideSubType>> classNames) {
		if (sides != null)
			for (int i = 0; i < sides.length; i++) {
				if (sides[i] != null) {
					// Jcrpg.LOGGER.info("SIDE SUBTYPE: "+sides[i].subtype.
					// getClass().getCanonicalName());

					if (classNames.contains(sides[i].subtype.getClass())) {
						return true;
					}
				}
			}
		return false;

	}

	/**
	 * Tells if the cube has any side of a set of sideSubTypes.
	 * 
	 * @param c
	 * @param classNames
	 * @return
	 */
	public Integer[] hasSideOfInstanceInAnyDir(Cube c,
			Set<Class<? extends SideSubType>> classNames) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int j = 0; j < c.sides.length; j++) {
			Side[] sides = c.sides[j];
			if (sides != null)
				for (int i = 0; i < sides.length; i++) {
					if (sides[i] != null) {
						// Jcrpg.LOGGER.info("SIDE SUBTYPE: "+sides[i].subtype.
						// getClass().getCanonicalName());
						if (classNames.contains(sides[i].subtype.getClass())) {
							list.add(j);
						}
					}
				}
		}
		if (list.size() == 0)
			return null;
		return (Integer[]) list.toArray(new Integer[0]);

	}

	public static J3DCore self;

	public static J3DCore getInstance() {
		return self;
	}

	/**
	 * The base movement method.
	 * 
	 * @param direction
	 *            The direction to move.
	 */
	public int[] calcMovement(int[] orig, int direction, boolean limitsCut) {
		int[] r = new int[3];
		int[] vector = moveTranslations.get(new Integer(direction));
		r[0] = orig[0] + vector[0];
		r[1] = orig[1] + vector[1];
		r[2] = orig[2] + vector[2];
		if (limitsCut) {
			r[0] = gameState.world.shrinkToWorld(r[0]);
			r[1] = gameState.world.shrinkToWorld(r[1]);
			r[2] = gameState.world.shrinkToWorld(r[2]);
		}

		return r;
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
	static {
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

	long lastStepSoundTime = System.currentTimeMillis();

	/**
	 * Tries to move in directions, and sets coords if successfull
	 * 
	 * @param from
	 *            From coordinates (gameState.world coords)
	 * @param fromRel
	 *            From coordinates gameState.relative (3d space coords)
	 * @param directions
	 *            A set of directions to move into
	 */
	public boolean move(int[] from, int[] fromRel, int[] directions) {
		String canMoveString = gameState.player.canMove();

		if (canMoveString != null
				|| gameState.player.theFragment.fragmentState.isCamping
				|| gameState.engine.isPause()) {
			if (System.currentTimeMillis() - lastStepSoundTime > 500) { // don't
																		// write
																		// message
																		// too
																		// often
				if (gameState.player.theFragment.fragmentState.isCamping) {
					uiBase.hud.mainBox
							.addEntry("You can't move while camping!");
				} else {
					if (canMoveString != null) {
						uiBase.hud.mainBox.addEntry(canMoveString);
					}
					// do nothing
				}
				lastStepSoundTime = System.currentTimeMillis();
			}
			return false;
		}

		Cube leftCube = gameState.world
		.getCube(
				-1,
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ,
				false);
		RenderedCube renderedLeftCube = renderedArea.getCubeAtPosition(gameState.world, 
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ);
		
		boolean success = moveBase(from, fromRel, directions);
		
		Cube enteredCube = null; 
		RenderedCube renderedEnteredCube = renderedArea.getCubeAtPosition(gameState.world, 
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ);
		
		if (success) {
			enteredCube = gameState.world
			.getCube(
					-1,
					gameState.getNormalPositions().viewPositionX,
					gameState.getNormalPositions().viewPositionY,
					gameState.getNormalPositions().viewPositionZ,
					false);
		}
		if (System.currentTimeMillis() - lastStepSoundTime > 300) { // don't
																	// play
																	// sound too
																	// often
			if (success) {
				try {
					Side[] s = enteredCube.getSide(BOTTOM);
					boolean played = false;
					for (Side side : s) {
						if (side.subtype.audioStepType != null) {
							audioServer.play(side.subtype.audioStepType);
							played = true;
							break;
						}
					}
					if (!played) {
						audioServer.play(AudioServer.STEP_SOIL);
					}
				} catch (Exception ex) {
					audioServer.play(AudioServer.STEP_SOIL);
				}
			} else {
				audioServer.play(AudioServer.STEP_NO_WAY);
			}
			lastStepSoundTime = System.currentTimeMillis();
		}
		if (success) {
			gameState.player.theFragment.roamTo(
					gameState.getNormalPositions().viewPositionX, gameState
							.getNormalPositions().viewPositionY, gameState
							.getNormalPositions().viewPositionZ);
			gameState.updateEntityIcons();
			gameState.checkAndHandleEnterLeave();
			
			handleStaticTriggerSides(enteredCube, renderedEnteredCube, leftCube, renderedLeftCube);
			
		}
		return success;
	}
	
	static ArrayList<TriggerHandler> triggerHandlers = new ArrayList<TriggerHandler>();
	static
	{
		triggerHandlers.add(new StorageObjectHandler());
	}
	
	public boolean handleStaticTriggerSides(Cube enteredCube, RenderedCube renderedEnteredCube, Cube leftCube, RenderedCube renderedLeftCube)
	{
		for (TriggerHandler handler:triggerHandlers)
		{
			if (handler.handlesType(enteredCube, renderedEnteredCube, leftCube, renderedLeftCube))
			{
				handler.handleStaticTriggerSides(enteredCube, renderedEnteredCube, leftCube, renderedLeftCube);
			}
		}
		return true;
	}

	/**
	 * Tries to move in directions, and sets coords if successfull
	 * 
	 * @param from
	 *            From coordinates (gameState.world coords)
	 * @param fromRel
	 *            From coordinates gameState.relative (3d space coords)
	 * @param directions
	 *            A set of directions to move into
	 */
	public boolean moveBase(int[] from, int[] fromRel, int[] directions) {
		int[] newCoords = from;
		int[] newRelCoords = fromRel;
		for (int i = 0; i < directions.length; i++) {
			if (J3DCore.LOGGING)
				Jcrpg.LOGGER.info("J3DCore.moveBase Moving dir: "
						+ directions[i]);
			newCoords = calcMovement(newCoords, directions[i], true);
			newRelCoords = calcMovement(newRelCoords, directions[i], false);
		}
		if (FREE_MOVEMENT) { // test free movement
			gameState.setViewPosition(newCoords);
			gameState.setRelativePosition(newRelCoords);
			Cube c = gameState.world.getCube(-1, newCoords[0], newCoords[1],
					newCoords[2], false);
			if (c != null) {
				if (c.internalCube) {
					if (J3DCore.LOGGING)
						Jcrpg.LOGGER.info("Moved: INTERNAL");
					gameState.getNormalPositions().insideArea = true;
					dofParentNode.detachAllChildren(); // workaround for
															// culling
					dofParentNode.attachChild(intRootNode);
					dofParentNode.attachChild(extRootNode);
				} else {
					if (J3DCore.LOGGING)
						Jcrpg.LOGGER.info("Moved: EXTERNAL");
					gameState.getNormalPositions().insideArea = false;
					dofParentNode.detachAllChildren(); // workaround for
															// culling
					dofParentNode.attachChild(extRootNode);
					dofParentNode.attachChild(intRootNode);
				}
				gameState.getNormalPositions().internalLight = c.internalLight;

			}
			return true;

		}

		Cube c = gameState.world.getCube(-1, from[0], from[1], from[2], false);

		if (c != null) {
			Jcrpg.LOGGER.info("Current Cube = " + c.toString());
			// get current steep dir for knowing if checking below or above Cube
			// for moving on steep
			Integer[] currentCubeSteepDirections = hasSideOfInstanceInAnyDir(c,
					climbers);
			Jcrpg.LOGGER
					.info("STEEP DIRECTION"
							+ (currentCubeSteepDirections != null ? currentCubeSteepDirections.length
									: "null") + " - " + directions[0] + "  "
							+ c.steepDirection);
			if (currentCubeSteepDirections != null)
				for (int steepDir : currentCubeSteepDirections) {
					if (steepDir == oppositeDirections.get(
							new Integer(directions[0])).intValue()) {
						newCoords = calcMovement(newCoords, TOP, true);
						newRelCoords = calcMovement(newRelCoords, TOP, false);
						break;
					}
				}
			Side[] sides = c.getSide(directions[0]);
			if (sides != null) {
				if (J3DCore.LOGGING)
					Jcrpg.LOGGER.info("SAME CUBE CHECK: NOTPASSABLE");
				if (hasSideOfInstance(sides, notPassable)
						&& (!gameState.getNormalPositions().onSteep
								|| directions[0] == BOTTOM || directions[0] == TOP))
					return false;
				if (J3DCore.LOGGING)
					Jcrpg.LOGGER.info("SAME CUBE CHECK: NOTPASSABLE - passed");
			}
			Cube nextCube = gameState.world.getCube(-1, newCoords[0],
					newCoords[1], newCoords[2], false);
			if (nextCube == null)
				Jcrpg.LOGGER.info("NEXT CUBE = NULL");
			else {
				if (J3DCore.LOGGING)
					Jcrpg.LOGGER.info("Next Cube = " + nextCube.toString());
				sides = nextCube.getSide(oppositeDirections.get(
						new Integer(directions[0])).intValue());
				if (sides != null) {
					if (hasSideOfInstance(sides, notPassable))
						return false;
				}
			}

			if (nextCube != null
					&& hasSideOfInstance(nextCube.getSide(BOTTOM), notPassable)) {
				// we have next cube in walk dir, and it has bottom too

				sides = nextCube.getSide(oppositeDirections.get(
						new Integer(directions[0])).intValue());
				if (sides != null) {
					if (hasSideOfInstance(sides, notPassable))
						return false;
				}

				sides = nextCube != null ? nextCube.getSide(BOTTOM) : null;
				if (sides != null) {
					if (hasSideOfInstance(sides, notWalkable))
						return false;
				}

				// checking steep setting
				Integer[] nextCubeSteepDirections = hasSideOfInstanceInAnyDir(
						nextCube, climbers);
				if (nextCubeSteepDirections != null) {
					gameState.getNormalPositions().onSteep = true;
				} else {
					gameState.getNormalPositions().onSteep = false;
				}
			} else {
				// no next cube in same direction, trying lower part steep,
				// until falling down deadly if nothing found... :)
				int yMinus = 1; // value of delta downway
				while (true) {
					// cube below
					nextCube = gameState.world.getCube(-1, newCoords[0],
							newCoords[1] - (yMinus++), newCoords[2], false);
					if (J3DCore.LOGGING)
						Jcrpg.LOGGER.info("FALLING: " + nextCube);
					if (yMinus > 10)
						break; // / i am faaaalling.. :)
					if (nextCube == null)
						continue;

					sides = nextCube != null ? nextCube.getSide(directions[0])
							: null;
					if (sides != null) {
						// Try to get climber side
						if (hasSideOfInstance(sides, climbers)) {
							sides = nextCube != null ? nextCube.getSide(BOTTOM)
									: null;
							if (hasSideOfInstance(sides, notWalkable))
								return false;
							newCoords[1] = newCoords[1] - (yMinus - 1);
							newRelCoords[1] = newRelCoords[1] - (yMinus - 1);
							gameState.getNormalPositions().onSteep = true; // found
																			// steep
							break;
						} else {
							sides = nextCube != null ? nextCube.getSide(TOP)
									: null;
							if (sides != null) // checking if cube's top is not
												// passable
								if (hasSideOfInstance(sides, notPassable))
									return false; // not passable, do not make
													// this step
							// no luck with climbers , let's see notPassable
							// bottom...
							sides = nextCube != null ? nextCube.getSide(BOTTOM)
									: null;
							if (sides != null)
								if (hasSideOfInstance(sides, notPassable)) {
									if (hasSideOfInstance(sides, notFallable))
										return false;
									// yeah, a place to stand...
									newCoords[1] = newCoords[1] - (yMinus - 1);
									newRelCoords[1] = newRelCoords[1]
											- (yMinus - 1);
									gameState.getNormalPositions().onSteep = false; // yeah
																					// ,
																					// found
									break;
								}
						}
					} else {
						// no luck with climbers on walk direction, let's see
						// notPassable bottom...
						sides = nextCube != null ? nextCube.getSide(BOTTOM)
								: null;
						if (sides != null)
							if (hasSideOfInstance(sides, notPassable)) {
								if (hasSideOfInstance(sides, notFallable))
									return false;
								newCoords[1] = newCoords[1] - (yMinus - 1);
								newRelCoords[1] = newRelCoords[1]
										- (yMinus - 1);
								Integer[] nextCubeSteepDirections = hasSideOfInstanceInAnyDir(
										nextCube, climbers);
								if (nextCubeSteepDirections != null) {// check
																		// for
																		// climbers
									gameState.getNormalPositions().onSteep = true;
								} else {
									gameState.getNormalPositions().onSteep = false; // yeah
																					// ,
																					// found
								}
								break;
							}
					}
				}

				// return;
			}

		} else {
			gameState.getNormalPositions().onSteep = false;
			// return;
		}
		gameState.setViewPosition(newCoords);
		gameState.setRelativePosition(newRelCoords);
		c = gameState.world.getCube(-1, newCoords[0], newCoords[1],
				newCoords[2], false);
		if (c != null) {
			if (c.internalCube) {
				Jcrpg.LOGGER.info("Moved: INTERNAL");
				gameState.getNormalPositions().insideArea = true;
				dofParentNode.detachAllChildren(); // workaround for culling
				dofParentNode.attachChild(intRootNode);
				dofParentNode.attachChild(extRootNode);
			} else {
				Jcrpg.LOGGER.info("Moved: EXTERNAL");
				gameState.getNormalPositions().insideArea = false;
				dofParentNode.detachAllChildren(); // workaround for culling
				dofParentNode.attachChild(extRootNode);
				dofParentNode.attachChild(intRootNode);
			}
			gameState.getNormalPositions().internalLight = c.internalLight;
		}
		return true;
	}

	boolean debugLeak = false;

	public boolean moveForward(int direction) {
		int[] coords = new int[] {
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ };
		int[] relCoords = new int[] { gameState.getNormalPositions().relativeX,
				gameState.getNormalPositions().relativeY,
				gameState.getNormalPositions().relativeZ };
		if (debugLeak) {
			gameState.getNormalPositions().viewPositionX += 40;
			gameState.getNormalPositions().relativeX += 40;
			return true;
		} else
			return move(coords, relCoords, new int[] { direction });
	}

	/**
	 * Move view Left (strafe)
	 * 
	 * @param direction
	 */
	public boolean moveLeft(int direction) {
		int[] coords = new int[] {
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ };
		int[] relCoords = new int[] { gameState.getNormalPositions().relativeX,
				gameState.getNormalPositions().relativeY,
				gameState.getNormalPositions().relativeZ };
		if (direction == NORTH) {
			return move(coords, relCoords, new int[] { WEST });
		} else if (direction == SOUTH) {
			return move(coords, relCoords, new int[] { EAST });
		} else if (direction == EAST) {
			return move(coords, relCoords, new int[] { NORTH });
		} else if (direction == WEST) {
			return move(coords, relCoords, new int[] { SOUTH });
		}
		return false;
	}

	/**
	 * Move view Right (strafe)
	 * 
	 * @param direction
	 */
	public boolean moveRight(int direction) {
		int[] coords = new int[] {
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ };
		int[] relCoords = new int[] { gameState.getNormalPositions().relativeX,
				gameState.getNormalPositions().relativeY,
				gameState.getNormalPositions().relativeZ };
		if (direction == NORTH) {
			return move(coords, relCoords, new int[] { EAST });
		} else if (direction == SOUTH) {
			return move(coords, relCoords, new int[] { WEST });
		} else if (direction == EAST) {
			return move(coords, relCoords, new int[] { SOUTH });
		} else if (direction == WEST) {
			return move(coords, relCoords, new int[] { NORTH });
		}
		return false;
	}

	public boolean moveBackward(int direction) {
		int[] coords = new int[] {
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ };
		int[] relCoords = new int[] { gameState.getNormalPositions().relativeX,
				gameState.getNormalPositions().relativeY,
				gameState.getNormalPositions().relativeZ };
		return move(coords, relCoords, new int[] { oppositeDirections.get(
				new Integer(direction)).intValue() });
	}

	/**
	 * Move view Up (strafe)
	 * 
	 * @param direction
	 */
	public boolean moveUp() {
		int[] coords = new int[] {
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ };
		int[] relCoords = new int[] { gameState.getNormalPositions().relativeX,
				gameState.getNormalPositions().relativeY,
				gameState.getNormalPositions().relativeZ };
		return move(coords, relCoords, new int[] { TOP });
	}

	public boolean moveDown() {
		int[] coords = new int[] {
				gameState.getNormalPositions().viewPositionX,
				gameState.getNormalPositions().viewPositionY,
				gameState.getNormalPositions().viewPositionZ };
		int[] relCoords = new int[] { gameState.getNormalPositions().relativeX,
				gameState.getNormalPositions().relativeY,
				gameState.getNormalPositions().relativeZ };
		return move(coords, relCoords, new int[] { BOTTOM });
	}

	public void turnRight() {
		gameState.getCurrentRenderPositions().viewDirection++;
		if (gameState.getCurrentRenderPositions().viewDirection == directions.length)
			gameState.getCurrentRenderPositions().viewDirection = 0;
	}

	public void turnLeft() {
		gameState.getCurrentRenderPositions().viewDirection--;
		if (gameState.getCurrentRenderPositions().viewDirection == -1)
			gameState.getCurrentRenderPositions().viewDirection = directions.length - 1;
	}

	boolean noInput = false;

	boolean udUpdate = true;

	public void updateDisplayCalmer(Vector3f from) {

		noInput = true;
		// update game state, do not use interpolation parameter
		if (udUpdate) {
			update(-1.0f);
		}
		udUpdate = !udUpdate;

		// render, do not use interpolation parameter
		render(-1.0f);

		// swap buffers
		// pManager.remove(sPass);
		display.getRenderer().displayBackBuffer();
		// pManager.add(sPass);
		noInput = false;
	}

	public boolean noThreadedRenderCheck = false;
	public void updateDisplay(Vector3f from) {

		noThreadedRenderCheck = true;
		noInput = true;
		// update game state, do not use interpolation parameter
		update(-1.0f);

		// render, do not use interpolation parameter
		render(-1.0f);

		// swap buffers
		display.getRenderer().displayBackBuffer();

		noInput = false;
		noThreadedRenderCheck = false;
	}

	public void updateDisplayNoBackBuffer() {

		noInput = true;
		// update game state, do not use interpolation parameter
		update(-1.0f);

		// render, do not use interpolation parameter
		render(-1.0f);

		// swap buffers
		noInput = false;

	}

	@Override
	protected void updateInput() {
		//if (!FPSCOUNTER)
			//fpsNode.detachAllChildren(); // TODO
		if (!noInput)
			super.updateInput();
	}

	@Override
	protected void cleanup() {
		// gameState.engine.exit();
		super.cleanup();
		if (dofRenderPass != null)
			dofRenderPass.cleanup();
	}

	@Override
	public void finish() {
		// gameState.engine.exit();
		super.finish();
	}

	@Override
	protected void quit() {
		gameState.engine.exit();
		super.quit();
	}

	public void doQuit() {
		quit();
	}

	public FogState fs_external;
	public FogState fs_external_special;
	public FogState fs_internal;
	public DirectionalShadowMapPass sPass = null;
	private DepthOfFieldRenderPass dofRenderPass;
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
	public Map worldMap = null;
	public MainMenu mainMenu = null;
	public LoadMenu loadMenu = null;
	public OptionsMenu optionsMenu = null;	
	public PartySetup partySetup = null;
	public CharacterLevelingWindow charLevelingWindow = null;
	public PartyOrderWindow partyOrderWindow = null;
	public NormalActWindow normalActWindow = null;

	public void createWorldMap() {
		try {
			uiBase.removeWindow(worldMap);
			worldMap = new Map(uiBase, gameState.world.worldMap);
			uiBase.addWindow("worldMap", worldMap);
		} catch (Exception ex) {

		}
	}
    public static MaterialState ms;

	/**
	 * This renders a gameState.world initially, call it after loading a game
	 * into a clean core.
	 */
	public void init3DGame() {
		if (coreFullyInitialized) {
			dofParentNode.attachChild(intRootNode);
			dofParentNode.attachChild(extRootNode);
			dofParentNode.attachChild(encounterExtRootNode);
			dofParentNode.attachChild(encounterIntRootNode);
			extRootNode.attachChild(extWaterRefNode);
			intRootNode.attachChild(intWaterRefNode);
			groundParentNode.attachChild(dofParentNode);
			groundParentNode.attachChild(skyParentNode);
		}
		inventoryWindow.setPageData(gameState.player);
		charSheetWindow.setPageData(gameState.player);
		gameState.world.worldMap = new WorldMap(gameState.world);
		uiBase.hud.initGameStateNodes();
		createWorldMap();
		// updating entity icons on HUD
		gameState.updateEntityIcons();

		if (skySphere != null)
			skyParentNode.detachChild(skySphere);
		/*
		 * Skysphere
		 */
		skySphere = new Sphere("SKY_SPHERE", 20, 20, 300f);
		waterEffectRenderPass
				.setReflectedScene(WATER_DETAILED ? groundParentNode
						: skyParentNode);
		skyParentNode.attachChild(skySphere);
		skySphere.setModelBound(null); // this must be set to null for lens
										// flare
		skySphere.setRenderState(cs_none);
		skySphere.setCullHint(CullHint.Never);
		VBOInfo v = new VBOInfo(true);
		skySphere.setVBOInfo(v);

		groundParentNode.clearRenderState(RenderState.RS_LIGHT);
		rootNode.clearRenderState(RenderState.RS_LIGHT);
		skySphere.setRenderState(skydomeLightState);

		// intRootNode.attachChild(skySphereInvisibleGround);
		Texture texture = TextureManager.loadTexture("./data/sky/day/top.jpg",
				Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);

		if (texture != null) {
			skySphere.setTextureMode(Sphere.TEX_PROJECTED);

			//texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
			//texture.setApply(Texture.AM_MODULATE);
			texture.setWrap(Texture.WrapMode.Repeat);//WM_WRAP_S_WRAP_T);
			texture.setApply(Texture.ApplyMode.Modulate);

			//texture.setRotation(qTexture);
			TextureState state = DisplaySystem.getDisplaySystem().getRenderer()
					.createTextureState();
			state.setTexture(texture, 0);
			state.setEnabled(true);
			skySphere.setRenderState(state);
		}
		skySphere.updateRenderState();

		/*if (fpsNode.getChildren() != null && fpsNode.getChildren().size() > 0) {
			fpsNode.getChild(0).setLocalTranslation(
					new Vector3f(0, display.getHeight() - 20, 0));
			fpsNode.getChild(0).setLocalScale(display.getWidth() / 1000f);
		}*/ // TODO new fps?

		updateDisplay(null);

		if (sEngine == null) {
			sEngine = new J3DStandingEngine(this);
		}
		if (eEngine == null) {
			eEngine = new J3DEncounterEngine(this);
		}
		gameState.switchToEncounterScenario(false, null);

		setCalculatedCameraLocation();

		cam.setDirection(J3DCore.directions[gameState.getNormalPositions().viewDirection]);
		cam.update();

		sEngine.render(
				
				gameState.getNormalPositions().relativeX, 
				gameState.getNormalPositions().relativeY, 
				gameState.getNormalPositions().relativeZ, 
				gameState.getNormalPositions().viewPositionX, 
				gameState.getNormalPositions().viewPositionY, 
				gameState.getNormalPositions().viewPositionZ, 
				false,false);
		sEngine.renderToViewPort();
		/*if (!coreFullyInitialized)
			sEngine.renderToViewPort(); // for correct culling, call it twice
										// ;-)
		 */
		
		// call for normal initial camera view
		cam.normalize();
		cam.update();

		if (mEngine == null) {
			mEngine = new J3DMovingEngine(this);
		}
		skyParentNode.updateRenderState();
		updateDisplay(null);
		rootNode.updateGeometricState(0, false);
		gameState.engine.setPause(false);
		intRootNode.attachChild(playerLightNode);
		if (coreFullyInitialized) {
			reinitialized = true;
		}
		coreFullyInitialized = true;
	}

	boolean reinitialized = false;

	/**
	 * This is responsible to reset the core for a load/new game from main menu.
	 */
	public void clearCore() {
		if (coreFullyInitialized) {
			if (encounterMode) {
				switchEncounterMode(false);
			}
			orbiters3D.clear();
			orbitersLight3D.clear();
			gameState.getNormalPositions().insideArea = false;
			gameState.getNormalPositions().internalLight = false;
			gameState.getNormalPositions().onSteep = false;
			skyParentNode.detachAllChildren();
			modelLoader.cleanAll();
			modelPool.cleanAll();
			hmSolidColorSpatials.clear();
			sEngine.clearAll();
			mEngine.clearAll();
			extRootNode.detachAllChildren();
			intRootNode.detachAllChildren();
			extWaterRefNode.detachAllChildren();
			intWaterRefNode.detachAllChildren();
			groundParentNode.detachAllChildren();
			encounterExtRootNode.detachAllChildren();
			encounterIntRootNode.detachAllChildren();
			// rootNode.detachAllChildren();
			batchHelper.clearAll();
			batchHelperEncounterArea.clearAll();
			skySphere.removeFromParent();
			rootNode.updateRenderState();
			gameState.clearAll();
		}
	}

	public void setFlare(boolean state) {
		if (J3DCore.LOGGING)
			Jcrpg.LOGGER.finest("TURNING LENSFLARE " + state);
		for (LensFlare f : flares.keySet()) {
			f.setIntensity(state ? 10f : 0f);
			f.setRootNode(state ? flares.get(f) : null);
			f.updateRenderState();
		}
	}

	/**
	 * When a full game is started/loaded this should be true.
	 */
	public boolean coreFullyInitialized = false;
	public boolean gameLost = false;
	/**
	 * A quad that is only used to add to an ortho ui node. Otherwise it is
	 * culled!
	 */
	Quad quadToFixHUDCulling = null;

	@Override
	protected void simpleInitGame() {
		
		modelLoader = new ModelLoader(this);
		Thread.currentThread().setPriority(2);
		audioServer = new AudioServer();
		audioServer.init();
		ZBufferState zStatePasses = display.getRenderer().createZBufferState();
		zStatePasses.setEnabled(true);
		//zStatePasses.setFunction(ZBufferState.CF_LEQUAL);
		//rootNode.setRenderState(zStatePasses);
		// rootNode.setCullMode(Node.CULL_DYNAMIC);

		// ui root
		uiRootNode = new Node("uiRoot");
		rootNode.attachChild(uiRootNode);
		ZBufferState zStateOff = display.getRenderer().createZBufferState();
		zStateOff.setFunction(ZBufferState.TestFunction.Always);
		zStateOff.setEnabled(true);
		uiRootNode.setCullHint(CullHint.Never);
		uiRootNode.setRenderState(zStateOff);
		uiRootNode.setModelBound(new BoundingBox());
		uiRootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		uiRootNode.setLightCombineMode(LightCombineMode.Off);

		quadToFixHUDCulling = new Quad("", 0, 0);
		quadToFixHUDCulling.setModelBound(new BoundingBox());
		quadToFixHUDCulling.updateModelBound();
		uiRootNode.attachChild(quadToFixHUDCulling);

		// cam.resize(100, 100);
		// cam.setViewPort(30, 90, 30, 90);
		bigSphere.setCenter(new Vector3f(0, 0, 0));
		// bigSphere.s
		bigSphere.setRadius(10000f);
		// uiRootNode.setModelBound(bigSphere);

		// external cubes' rootnode
		extRootNode = FARVIEW_ENABLED ? new Node("ex") : new Node("extRootNode");//new ScenarioNode(cam);
		// extRootNode.setModelBound(bigSphere);
		// extRootNode.attachChild(new Node());
		// internal cubes' rootnode
		intRootNode = FARVIEW_ENABLED ? new Node("in") : new Node("intRootNode");//ScenarioNode(cam);

		encounterExtRootNode = new Node("encExtRootNode");
		encounterIntRootNode = new Node("encIntRootNode");
		// intRootNode.setModelBound(bigSphere);
		// intRootNode.attachChild(new Node());
		/*
		 * groundParentNode.setModelBound(null); rootNode.setModelBound(null);
		 * intRootNode.setModelBound(null); extRootNode.setModelBound(null);
		 */
		intRootNode.setCullHint(CullHint.Dynamic);
		extRootNode.setCullHint(CullHint.Dynamic);
		groundParentNode.setCullHint(CullHint.Dynamic);

		// cRootNode = new ScenarioNode(J3DCore.VIEW_DISTANCE,cam);
		// Setup renderpasses

		dofRenderPass = new DepthOfFieldRenderPass(cam, DOF_DETAILED?1:2, DOF_DETAILED?4:4);
		bloomRenderPass = new BloomRenderPass(cam,4);

		ShadeState ss = DisplaySystem.getDisplaySystem().getRenderer()
				.createShadeState();
		ss.setShadeMode(ShadeState.ShadeMode.Flat);
		ss.setEnabled(false);
		// rootNode.setRenderState(ss);
		// rootNode.clearRenderState(RenderState.RS_SHADE);

		cs_back = display.getRenderer().createCullState();
		cs_back.setCullFace(CullState.Face.Back);
		cs_back.setEnabled(true);
		cs_none = display.getRenderer().createCullState();
		cs_none.setCullFace(CullState.Face.None);

		//rootNode.setRenderState(cs_none);
		/*
		 * rootNode.clearRenderState(RenderState.RS_DITHER);
		 * rootNode.clearRenderState(RenderState.RS_FRAGMENT_PROGRAM);
		 * rootNode.clearRenderState(RenderState.RS_MATERIAL);
		 * rootNode.clearRenderState(RenderState.RS_TEXTURE);
		 * rootNode.clearRenderState(RenderState.RS_SHADE);
		 * rootNode.clearRenderState(RenderState.RS_STENCIL);
		 */
		if (ms==null)
		{
			// Test materialstate (should be set through the import anyway)
	        ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	        ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
	        ms.setAmbient(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
	        ms.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
	        ms.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
	        ms.setShininess(10.0f);
	        
		}
		groundParentNode.setRenderState(ms);

		DisplaySystem.getDisplaySystem().getRenderer().getQueue()
				.setTwoPassTransparency(false);
		rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

		lightState.detachAll();
		extLightState = getDisplay().getRenderer().createLightState();
		internalLightState = getDisplay().getRenderer().createLightState();
		encounterIntLightState = getDisplay().getRenderer().createLightState();
		skydomeLightState = getDisplay().getRenderer().createLightState();

		display.getRenderer().setBackgroundColor(ColorRGBA.black);

		cam.setFrustumPerspective(45.0f, (float) display.getWidth()
				/ (float) display.getHeight(), 0.002f, 350);
		dofParentNode.attachChild(intRootNode);
		dofParentNode.attachChild(extRootNode);
		groundParentNode.attachChild(dofParentNode);
		groundParentNode.attachChild(skyParentNode);
		skyParentNode.setCullHint(CullHint.Always);

		dofParentNode.attachChild(encounterExtRootNode);
		dofParentNode.attachChild(encounterIntRootNode);

		rootNode.attachChild(groundParentNode);

		BlendState as = DisplaySystem.getDisplaySystem().getRenderer()
				.createBlendState();
		as.setEnabled(true);
		as.setBlendEnabled(true);
		as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		if (!BLOOM_EFFECT) {
			if (TEXTURE_QUALITY == 2)
				as.setReference(0.9f);
			else
				as.setReference(0.9f);
		} else
			as.setReference(0.9f);
		as.setTestEnabled(true);
		as.setTestFunction(BlendState.TestFunction.GreaterThan);// GREATER is good only

		fs_external = display.getRenderer().createFogState();
		fs_external_special = display.getRenderer().createFogState();
		fs_external.setDensity(0.5f);
		fs_external.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
		if (J3DCore.FARVIEW_ENABLED) {
			fs_external.setEnd(((RENDER_DISTANCE_FARVIEW * 2) / 1.15f));
			fs_external.setStart(1.5f * (RENDER_DISTANCE_FARVIEW * 2) / 3);
			fs_external.setDensity(0.3f);
			fs_external_special.setDensity(0.3f);
			fs_external_special.setEnd((VIEW_DISTANCE * 1.65f));
			fs_external_special.setStart(2 * VIEW_DISTANCE / 3);
			fs_external_special.setDensityFunction(FogState.DensityFunction.Linear);
			fs_external_special.setQuality(FogState.Quality.PerVertex);
			fs_external_special.setNeedsRefresh(true);
			fs_external_special.setEnabled(true);
		} else {
			fs_external.setEnd((VIEW_DISTANCE ));
			fs_external.setStart(VIEW_DISTANCE*0.92f);
		}
		fs_external.setDensityFunction(FogState.DensityFunction.Linear);
		fs_external.setQuality(FogState.Quality.PerVertex);
		fs_external.setNeedsRefresh(true);
		fs_external.setEnabled(true);
		extRootNode.setRenderState(fs_external);
		extRootNode.setRenderState(as);

		fs_internal = display.getRenderer().createFogState();
		fs_internal.setDensity(0.5f);
		fs_internal.setEnabled(true);
		fs_internal.setColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
		fs_internal.setEnd((VIEW_DISTANCE / 1.15f));
		fs_internal.setStart(3);
		fs_internal.setDensityFunction(FogState.DensityFunction.Linear);
		fs_internal.setQuality(FogState.Quality.PerVertex);
		intRootNode.setRenderState(fs_internal);
		intRootNode.setRenderState(as);

		encounterExtRootNode.setRenderState(fs_external);
		encounterExtRootNode.setRenderState(as);

		encounterIntRootNode.setRenderState(fs_internal);
		encounterIntRootNode.setRenderState(as);

		// default light states
		encounterExtRootNode.setRenderState(extLightState);
		encounterIntRootNode.setRenderState(encounterIntLightState);
		extRootNode.setRenderState(extLightState);
		intRootNode.clearRenderState(RenderState.RS_LIGHT);
		intRootNode.setRenderState(internalLightState);
		
		if (true == true && playerLight == null) {

			playerLight = new PointLight();
			playerLight.setEnabled(true);
			float lp = 0.8f;
			playerLight.setDiffuse(new ColorRGBA(lp, lp, lp, 1f));
			playerLight.setAmbient(new ColorRGBA(0.33f, 0.3f, 0.3f, 0.3f));
			playerLight.setSpecular(new ColorRGBA(0.4f, 0.4f, 0.4f, 1f));
			playerLight.setQuadratic(0.06f);
			playerLight.setLinear(0f);
			playerLight.setAttenuate(true);
			playerLight.setShadowCaster(false);
			playerLightNode = new LightNode("torch");
			playerLightNode.setLight(playerLight);
			intRootNode.attachChild(playerLightNode);
			internalLightState.attach(playerLight);

			PointLight dr2 = new PointLight();
			dr2.setEnabled(true);
			lp = 0.95f;
			dr2.setDiffuse(new ColorRGBA(lp, lp, lp, 0.5f));
			dr2.setAmbient(new ColorRGBA(0.3f, 0.3f,0.3f, 0.5f));
			dr2.setSpecular(new ColorRGBA(1, 1, 1, 0.5f));
			dr2.setQuadratic(1f);
			dr2.setLinear(1f);
			// dr.setAngle(45);
			dr2.setShadowCaster(false);
			encounterIntLightState.attach(dr2);
		}

		try {
			setupUIElements();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);

		}
		extWaterRefNode = new Node("extWatRef");
		extRootNode.attachChild(extWaterRefNode);
		intWaterRefNode = new Node("intWatRef");
		intRootNode.attachChild(intWaterRefNode);

		RenderPass rootPass = new RenderPass();
		//rootPass.add(rootNode);
		pManager.add(rootPass);

		waterEffectRenderPass = new WaterRenderPass(cam, 4, false, true);
		// set equations to use z axis as up
		waterEffectRenderPass.setWaterPlane(new Plane(new Vector3f(0.0f, 1.0f,
				0.0f), 0.0f));
		waterEffectRenderPass.setTangent(new Vector3f(1.0f, 0.0f, 0.0f));
		waterEffectRenderPass.setBinormal(new Vector3f(0.0f, 1.0f, 0.0f));
		// waterEffectRenderPass.setWaterMaxAmplitude(2f);
		pManager.add(waterEffectRenderPass);

		if (SHADOWS) {
			sPass = new DirectionalShadowMapPass(new Vector3f(-1f, -1f, -1f));
			Jcrpg.LOGGER.info("SHADOWS!");
			// sPass.setShadowColor(new ColorRGBA(0,0,0,1f));
			sPass.setEnabled(true);
			// sPass.add(extRootNode);
			// sPass.add(extRootNode);
			// sPass.add(intRootNode);
			// sPass.addOccluder(extRootNode);
			// sPass.addOccluder(intRootNode);
			// sPass.add(encounterExtRootNode);
			// sPass.add(encounterIntRootNode);
			// sPass.setRenderShadows(true);
			// sPass.setLightingMethod(ShadowedRenderPass.MODULATIVE);
			// sPass.rTexture = false;
			J3DShadowGate dsg = new J3DShadowGate();
			dsg.core = this;
			// sPass.setShadowGate(dsg);
			// sPass.setZOffset(0f);
			// sPass.add(rootNode);
			pManager.add(sPass);
			// sPass.addOccluder(groundParentNode);
		}

		bloomRenderPass.setUseCurrentScene(true);
		bloomRenderPass.setBlurIntensityMultiplier(1.0f);

		dofRenderPass.setRootSpatial(dofParentNode);
		dofRenderPass.setBlurSize(0.004f);
		dofRenderPass.setNearBlurDepth(VIEW_DISTANCE/1.3f);
		dofRenderPass.setFocalPlaneDepth(VIEW_DISTANCE/1.6f);
		dofRenderPass.setFarBlurDepth(VIEW_DISTANCE*2f);
		dofRenderPass.setThrottle(0f);

		bloomRenderPass.setEnabled(false);
		pManager.add(bloomRenderPass);
		dofRenderPass.setEnabled(false);
		pManager.add(dofRenderPass);
		
		if (BLOOM_EFFECT) {
			if (!bloomRenderPass.isSupported()) {
				Jcrpg.LOGGER.warning("!!!!!! BLOOM NOT SUPPORTED !!!!!!!! ");
				Text t = new Text("Text", "Bloom not supported (FBO needed).");
				t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				t.setLightCombineMode(LightCombineMode.Off);
				t.setLocalTranslation(new Vector3f(0, display.getHeight() - 20,
						0));
				//fpsNode.attachChild(t);
				BLOOM_EFFECT = false;
			} else {
				Jcrpg.LOGGER.info("!!!!!!!!!!!!!! BLOOM!");
				// bloomRenderPass.add(groundParentNode);
				//bloomRenderPass.setThrottle(0.f);
				bloomRenderPass.setEnabled(true);
			}
		} else
		if (DOF_EFFECT)
		{
			if (!dofRenderPass.isSupported()) {
				Jcrpg.LOGGER.warning("!!!!!! DIF NOT SUPPORTED !!!!!!!! ");
				Text t = new Text("Text", "DoF not supported (FBO needed).");
				t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				t.setLightCombineMode(LightCombineMode.Off);
				t.setLocalTranslation(new Vector3f(0, display.getHeight() - 20,
						0));
				//fpsNode.attachChild(t);
				DOF_EFFECT = false;
			} else {
				Jcrpg.LOGGER.info("!!!!!!!!!!!!!! Depth of field!");
				// bloomRenderPass.add(groundParentNode);
				dofRenderPass.setEnabled(true);
			}
		}

		RenderPass uiPass = new RenderPass();
		uiPass.add(uiRootNode);
		
		pManager.add(uiPass);

		// FPS render pass
        RenderPass statPass = new RenderPass();
        statPass.add(statNode);
        pManager.add(statPass);
		
		
		waterEffectRenderPass.setReflectedScene(groundParentNode);
		cam.normalize();
		cam.update();

		//fpsNode.getChild(0).setLocalTranslation(
			//	new Vector3f(0, display.getHeight() - 20, 0));
		//fpsNode.getChild(0).setLocalScale(display.getWidth() / 1000f);

		/*
		 * Skysphere
		 */
		skySphere = new Sphere("SKY_SPHERE", 20, 20, 300f);
		if (WATER_DETAILED)
		{
			waterEffectRenderPass.setReflectedScene(extWaterRefNode);
			waterEffectRenderPass.addReflectedScene(intWaterRefNode);
			waterEffectRenderPass.addReflectedScene(skyParentNode);
		} else
		{
			waterEffectRenderPass.setReflectedScene(skyParentNode);
		}
		skyParentNode.attachChild(skySphere);
		skySphere.setModelBound(null); // this must be set to null for lens
										// flare
		skySphere.setRenderState(cs_none);
		skySphere.setCullHint(CullHint.Never);

		groundParentNode.clearRenderState(RenderState.RS_LIGHT);
		rootNode.clearRenderState(RenderState.RS_LIGHT);
		skySphere.setRenderState(skydomeLightState);

		// intRootNode.attachChild(skySphereInvisibleGround);
		Texture texture = TextureManager.loadTexture("./data/sky/day/top.jpg",
				Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);

		if (texture != null) {

			texture.setWrap(Texture.WrapMode.Repeat);//WM_WRAP_S_WRAP_T);
			texture.setApply(Texture.ApplyMode.Modulate);
			texture.setRotation(qTexture);
			TextureState state = DisplaySystem.getDisplaySystem().getRenderer()
					.createTextureState();
			state.setTexture(texture, 0);
			state.setEnabled(true);
			skySphere.setRenderState(state);
		}
		skySphere.updateRenderState();

		mainMenu.toggle();
		//SceneMonitor.getMonitor().showViewer(true);
		//SceneMonitor.getMonitor().registerNode(rootNode);
	}

	public void setupUIElements() throws Exception {
		uiBase = new UIBase(this);

		mainMenu = new MainMenu(uiBase);
		loadMenu = new LoadMenu(uiBase);
		optionsMenu = new OptionsMenu(uiBase);
		partySetup = new PartySetup(uiBase);
		charLevelingWindow = new CharacterLevelingWindow(uiBase);
		partyOrderWindow = new PartyOrderWindow(uiBase);
		normalActWindow = new NormalActWindow(uiBase);
		
		behaviorWindow = new BehaviorWindow(uiBase);
		inventoryWindow = new InventoryWindow(uiBase);
		charSheetWindow = new CharacterSheetWindow(uiBase);
		preEncounterWindow = new PreEncounterWindow(uiBase);
		encounterWindow = new EncounterWindow(uiBase);
		turnActWindow = new TurnActWindow(uiBase);
		postEncounterWindow = new PostEncounterWindow(uiBase);

		uiBase.addWindow("behaviorWindow", behaviorWindow);
		uiBase.addWindow("inventoryWindow", inventoryWindow);
		uiBase.addWindow("charSheetWindow", charSheetWindow);
		uiBase.addWindow("mainMenu", mainMenu);
		uiBase.addWindow("cacheStateInfo", new CacheStateInfo(uiBase));
		uiBase.addWindow("partyOrderWindow", partyOrderWindow);
		uiBase.addWindow("normalActWindow", normalActWindow);

		// shadows not working because of this node -> the hudNode shall occupy
		// only the lower part, Done, image cut.
		uiRootNode.attachChild(uiBase.hud.hudNode);

	}

	public AudioServer audioServer = null;
	public J3DEncounterEngine eEngine = null;
	public J3DMovingEngine mEngine = null;
	public J3DStandingEngine sEngine = null;

	LightNode playerLightNode;
	PointLight playerLight;
	protected boolean torchLightEffect = true;

	/**
	 * If doing an gameState.engine-paused encounter mode this is with value
	 * true, switch it with core->switchEncounterMode(value) only!
	 */
	public boolean encounterMode = false;

	public BehaviorWindow behaviorWindow = null;
	public InventoryWindow inventoryWindow = null;
	public CharacterSheetWindow charSheetWindow = null;
	public PreEncounterWindow preEncounterWindow = null;
	public EncounterWindow encounterWindow = null;
	public TurnActWindow turnActWindow = null;
	public PostEncounterWindow postEncounterWindow = null;

	PlayerChoiceWindow pChoiceWindow = null;

	public static boolean DEMO_ENCOUTNER_MODE = false;

	/**
	 * Used for initializing/finishing encounter mode.
	 * 
	 * @param value
	 */
	public void switchEncounterMode(boolean value) {
		encounterMode = value;
		if (encounterMode) {
			// if (DEMO_ENCOUTNER_MODE) {
			if (pChoiceWindow == null) {

				ChoiceDescription yes = new ChoiceDescription("Y", "yes", "Yes");
				ArrayList<ChoiceDescription> encAnswers = new ArrayList<ChoiceDescription>();
				encAnswers.add(yes);
				pChoiceWindow = new PlayerChoiceWindow(uiBase, new TextEntry(
						"Encounter acknowledged?", ColorRGBA.red), encAnswers,
						"Encounter", 0.088f, 0.088f, 0.3f, 0.1f);
				uiBase.addWindow("Encounter", pChoiceWindow);
			}
			uiBase.hud.mainBox.hide();
			updateDisplay(null);
			pChoiceWindow.toggle();

			audioServer.playForced(AudioServer.EVENT_ENC1);
		} else {
			// if (DEMO_ENCOUTNER_MODE) {
			pChoiceWindow.toggle();
			uiBase.hud.mainBox.show();
			// }
			uiBase.hud.mainBox.addEntry(new TextEntry("Encounters finished",
					ColorRGBA.yellow));
			gameState.gameLogic.endPlayerEncounters();
			gameState.engine.turnFinishedForPlayer();
		}
	}

	boolean swapUpdate = false;
	
	float lightDiff = 0; 
	float lightDelta = 0.02f;
	float lightDiffX = (float)Math.random()/3;
	float lightDiffY = (float)Math.random()/3;
	float lightDiffZ = (float)Math.random()/3;

	Vector3f diffVec = new Vector3f();
	
	@Override
	protected void simpleUpdate() {
		

		if (!noThreadedRenderCheck)
		{
			if (sEngine!=null && sEngine.areaResult!=null)
			{
				sEngine.renderToViewPort();
			} 
			if (eEngine!=null && eEngine.areaResult!=null)
			{
				eEngine.renderToViewPort();
			} 

			// 'parallel' render related checks..
			
			// checking for pending full renderToViewPorts
			if (sEngine!=null && !sEngine.threadRendering && !sEngine.updateAfterRenderNeeded && sEngine.newRenderPending)
			{
				sEngine.renderToViewPort();
			}
			if (eEngine!=null && !eEngine.threadRendering && !eEngine.updateAfterRenderNeeded && eEngine.newRenderPending)
			{
				eEngine.renderToViewPort();
			}
			
			// checking for threadRendering step by step
			//long time = 0;
			if (sEngine!=null && sEngine.threadRendering)
			{
				//time = System.currentTimeMillis();	
				if (sEngine.renderToViewPortStepByStep())
				{
					sEngine.threadRendering = false;
					sEngine.updateAfterRenderNeeded = true;
				}
				//System.out.println("* THREAD RENDER T: "+(time-System.currentTimeMillis()));
			}
			if (eEngine!=null && eEngine.threadRendering)
			{
				if (eEngine.renderToViewPortStepByStep())
				{
					eEngine.threadRendering = false;
					eEngine.updateAfterRenderNeeded = true;
				}
			}
	
			// checking for after render update need...
			if (sEngine!=null && sEngine.updateAfterRenderNeeded)
			{
				//time = System.currentTimeMillis();
				sEngine.updateAfterRender();
				//System.out.println("* UPDATE AFTER T: "+(time-System.currentTimeMillis()));
			}
			if (eEngine!=null && eEngine.updateAfterRenderNeeded)
			{
				eEngine.updateAfterRender();
			}
		}
		if (J3DCore.SHADOWS) {
			sPass.setViewTarget(cam.getLocation());
		}

		if (playerLight != null) {
			lightDiff+=tpf;
			diffVec.x = cam.getLocation().x+lightDiffX; 
			diffVec.y = cam.getLocation().y+lightDiffY;
			diffVec.z = cam.getLocation().z+lightDiffZ;
			if (lightDiff>0.08f)
			{
				lightDiff = 0;
				if (torchLightEffect)
				{
					lightDiffX = (float)Math.random()/3;
					lightDiffY = (float)Math.random()/3;
					lightDiffZ = (float)Math.random()/3;
				}
			}
			playerLight.setLocation(diffVec);
			playerLightNode.setLocalTranslation(diffVec);
		}


		if (gameState.gameLogic != null) {
			gameState.gameLogic.encounterLogic.checkEncounterCallbackNeed();
			gameState.gameLogic.encounterLogic.checkTurnActCallbackNeed();
		}

		// time changed, updating lights, orbiters
		if (gameState.engine.hasTimeChanged()) {
			gameState.engine.setTimeChanged(false);
			updateTimeRelated();

		}
		// turn has come.
		if (gameState.engine.turnComes()) {
			pause = true;
			gameState.ecology.doTurn();
			gameState.engine.turnFinishedForAI();
			pause = false;
			tpf = 0;
		} else if (gameState.engine.checkEconomyUpdateNeeded()) {
			pause = true;
			gameState.doEconomyUpdate();
			pause = false;
			tpf = 0;
		} else if (!gameState.engine.isPause()) {
			gameState.checkAndDoLeveling();
		}

		// game-logic independent environmental update (sounds etc.)
		if (gameState.engine.isEnvironmentUpdateNeeded()) {
			gameState.engine.environemntUpdateDone();
			gameState.doEnvironmental();
		}

		// if (!swapUpdate)
		if (!pause) {
			/** Call simpleUpdate in any derived classes of SimpleGame. */

			/** Update controllers/render states/transforms/bounds for rootNode. */
			if (mEngine != null)
				mEngine.updateScene(tpf);
			rootNode.updateGeometricState(tpf, true);
			//fpsNode.updateGeometricState(tpf, true);

			// if (BLOOM_EFFECT|| SHADOWS || WATER_SHADER)
			pManager.updatePasses(tpf);
		}
		// swapUpdate=!swapUpdate;
	}

	public static boolean TRICK_CULL_RENDER = false;

	@Override
	protected void simpleRender() {
		/** Have the PassManager render. */
		try {
			pManager.renderPasses(display.getRenderer());
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}

	protected BasicPassManager pManager;
	
	public static DateFormat timeDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");

	/**
	 * Called every frame to update scene information.
	 * 
	 * @param interpolation
	 *            unused in this implementation
	 * @see BaseSimpleGame#update(float interpolation)
	 */
	protected final void update(float interpolation) {
		if (!pause) {
			super.update(interpolation);
	        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
	                "screen_shot_jcrpg", false ) ) {
	            display.getRenderer().takeScreenShot( "screenshot_"+timeDateFormat.format(new Date()) );
	        }
			/** Call simpleUpdate in any derived classes of SimpleGame. */
			simpleUpdate();

			statNode.updateGeometricState(tpf, true);
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

		TrimeshGeometryBatch.passedTimeCalculated = false;

		/** Draw the rootNode and all its children. */
		r.draw(rootNode);

		/** Call simpleRender() in any derived classes. */
		simpleRender();

		/** Draw the fps node to show the fancy information at the bottom. */
		//r.draw(fpsNode);

		doDebug(r);
	}

	public Node getUIRootNode() {
		return uiRootNode;
	}

	public Node getRootNode1() {
		return rootNode;
	}

	public Node getGroundParentNode() {
		return dofParentNode;
	}

	public InputHandler getInputHandler() {
		return input;
	}

	public ClassicInputHandler getClassicInputHandler() {
		return (ClassicInputHandler) input;
	}

	public ClassicKeyboardLookHandler getKeyboardHandler() {
		return ((ClassicKeyboardLookHandler) getInputHandler()
				.getFromAttachedHandlers(0));
	}

	public void do3DPause(boolean pause) {
		if (!CONTINUOUS_LOAD) {
			this.pause = pause;
		}
	}

	//@Override
	/*public void setDialogBehaviour(int behaviour) {
		URL url = null;
		try {
			url = AbstractGame.class.getResource("./data/ui/settings.png");
		} catch (Exception e) {
			Jcrpg.LOGGER.warning("" + e);
		}
		if (url != null) {
			setDialogBehaviour(behaviour, url);
		} else {
			setDialogBehaviour(behaviour, "./data/ui/settings.png");
		}
	}*/

	public void setCamera(Camera cam) {
		this.cam = cam;
	}
	
	private void switchPass(Pass pass, boolean state)
	{
		if (//pManager.contains(pass) && 
				state==false)
		{
			//pManager.remove(pass);
			pass.setEnabled(false);
		} else
		if (//!pManager.contains(pass) && 
				state==true)
		{
			//pManager.add(pass);
			pass.setEnabled(true);
		}
		
	}
	
	public void applyOptions()
	{
		switchPass(bloomRenderPass, BLOOM_EFFECT);
		switchPass(dofRenderPass, DOF_EFFECT&&!BLOOM_EFFECT);
		
		if (pManager.contains(waterEffectRenderPass))
		{
			waterEffectRenderPass.setUseShader(WATER_SHADER);
		}
		audioServer.applyVolumeSettings();
		getClassicInputHandler().applyMouseSettings();
	}
	

}
