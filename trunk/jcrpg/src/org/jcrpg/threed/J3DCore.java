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

package org.jcrpg.threed;

import java.io.File;
import java.io.FileInputStream;
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
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.LODModel;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.TextureStateModel;
import org.jcrpg.threed.scene.side.RenderedContinuousSide;
import org.jcrpg.threed.scene.side.RenderedHashRotatedSide;
import org.jcrpg.threed.scene.side.RenderedSide;
import org.jcrpg.threed.scene.side.RenderedTopSide;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.ground.Sand;
import org.jcrpg.world.ai.flora.ground.Snow;
import org.jcrpg.world.ai.flora.middle.deciduous.GreenBush;
import org.jcrpg.world.ai.flora.middle.succulent.GreenFern;
import org.jcrpg.world.ai.flora.tree.cactus.BigCactus;
import org.jcrpg.world.ai.flora.tree.deciduous.Acacia;
import org.jcrpg.world.ai.flora.tree.deciduous.CherryTree;
import org.jcrpg.world.ai.flora.tree.deciduous.OakTree;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
import org.jcrpg.world.ai.flora.tree.palm.JunglePalmTrees;
import org.jcrpg.world.ai.flora.tree.pine.GreatPineTree;
import org.jcrpg.world.ai.flora.tree.pine.GreenPineTree;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.River;
import org.jcrpg.world.place.orbiter.Orbiter;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.time.Time;

import com.jme.bounding.BoundingSphere;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;

public class J3DCore extends com.jme.app.SimpleGame implements Runnable {

    HashMap<String,Integer> hmAreaSubType3dType = new HashMap<String,Integer>();

    HashMap<Integer,RenderedSide> hm3dTypeRenderedSide = new HashMap<Integer,RenderedSide>();
    
	/**
	 * rendered cubes in each direction (N,S,E,W,T,B).
	 */
    public static int RENDER_DISTANCE = 10;
    public static int RENDER_GRASS_DISTANCE = 10;

	public static final float CUBE_EDGE_SIZE = 1.9999f; 
	
	public static final int MOVE_STEPS = 12;
	public static long TIME_TO_ENSURE = 11; 

    public static Integer EMPTY_SIDE = new Integer(0);
    
    public static boolean OPTIMIZED_RENDERING = true;
    
    public static boolean MIPMAP_TREES = false;
    
    public static boolean MIPMAP_GLOBAL = true;

    public static boolean TEXTURE_QUAL_HIGH = false;
    
    static Properties p = new Properties();
    static {
    	try {
    		File f = new File("./config.properties");
	    	FileInputStream fis = new FileInputStream(f);
	    	p.load(fis);
	    	
	    	String renderDistance = p.getProperty("RENDER_DISTANCE");
	    	if (renderDistance!=null)
	    	{
	    		try {
	    			RENDER_DISTANCE = Integer.parseInt(renderDistance);
	    			if (RENDER_DISTANCE>15) RENDER_DISTANCE = 15;
	    			if (RENDER_DISTANCE<5) RENDER_DISTANCE = 5;
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("RENDER_DISTANCE", "10");
	    		}
	    	}
	    	String renderGrassDistance = p.getProperty("RENDER_GRASS_DISTANCE");
	    	if (renderDistance!=null)
	    	{
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
	    	String mipmapGlobal = p.getProperty("MIPMAP_GLOBAL");
	    	if (renderDistance!=null)
	    	{
	    		try {
	    			MIPMAP_GLOBAL = Boolean.parseBoolean(mipmapGlobal);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("MIPMAP_GLOBAL", "true");
	    		}
	    	}
	    	String mipmapTrees = p.getProperty("MIPMAP_TREES");
	    	if (renderDistance!=null)
	    	{
	    		try {
	    			MIPMAP_TREES = Boolean.parseBoolean(mipmapTrees);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("MIPMAP_TREES", "false");
	    		}
	    	}
	    	String textureQualityHigh = p.getProperty("TEXTURE_QUAL_HIGH");
	    	if (renderDistance!=null)
	    	{
	    		try {
	    			TEXTURE_QUAL_HIGH = Boolean.parseBoolean(textureQualityHigh);
	    		} catch (Exception pex)
	    		{
	    			p.setProperty("TEXTURE_QUAL_HIGH", "false");
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
	
	public ModelLoader modelLoader = new ModelLoader(this);
	
	public Engine engine = null;
	
	public RenderedArea renderedArea = new RenderedArea();
	
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

	/**
	 * Steep Rotations 
	 */
	static Quaternion steepN, steepS, steepW, steepE;

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
	
	
	public J3DCore()
	{
		// area subtype to 3d type mapping
		hmAreaSubType3dType.put(Side.DEFAULT_SUBTYPE.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(Plain.SUBTYPE_GROUND.id, EMPTY_SIDE);//new Integer(2));
		hmAreaSubType3dType.put(Forest.SUBTYPE_FOREST.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(River.SUBTYPE_WATER.id, new Integer(10));
		hmAreaSubType3dType.put(House.SUBTYPE_INTERNAL_CEILING.id, new Integer(7));
		hmAreaSubType3dType.put(House.SUBTYPE_INTERNAL_GROUND.id, new Integer(3));
		hmAreaSubType3dType.put(House.SUBTYPE_EXTERNAL_GROUND.id, new Integer(3));
		hmAreaSubType3dType.put(House.SUBTYPE_EXTERNAL_DOOR.id, new Integer(5));
		hmAreaSubType3dType.put(House.SUBTYPE_WINDOW.id, new Integer(6));
		hmAreaSubType3dType.put(House.SUBTYPE_WALL.id, new Integer(1));
		hmAreaSubType3dType.put(World.SUBTYPE_OCEAN.id, new Integer(10));
		hmAreaSubType3dType.put(World.SUBTYPE_GROUND.id, new Integer(21));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_STEEP.id, EMPTY_SIDE);//new Integer(11));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_ROCK.id, new Integer(13));//EMPTY_SIDE); // 13
		hmAreaSubType3dType.put(Mountain.SUBTYPE_GROUND.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(Mountain.SUBTYPE_INTERSECT.id, new Integer(27));
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
		hmAreaSubType3dType.put(JunglePalmTrees.SUBTYPE_TREE.id, new Integer(24));
		hmAreaSubType3dType.put(GreenFern.SUBTYPE_BUSH.id, new Integer(26));

		
		LODModel lod_fern = new LODModel(new SimpleModel[]{new SimpleModel("models/bush/fern.3ds",null)},new float[][]{{0f,15f}});
		//LODModel lod_fern = new LODModel(new SimpleModel[]{new SimpleModel("models/fauna/dragon.3ds",null)},new float[][]{{0f,15f}});

		LODModel lod_cherry = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/cherry.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		//LODModel lod_cherry = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/cherry.3ds",null,MIPMAP_TREES),new SimpleModel("models/tree/cherry.3ds",null,true)},new float[][]{{0f,7f},{7.1f,15f}});
		LODModel lod_acacia = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/acacia.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		//LODModel lod_acacia = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/acacia.3ds",null,MIPMAP_TREES),new SimpleModel("models/tree/acacia.3ds",null,true)},new float[][]{{0f,7f},{7f,15f}});
		LODModel lod_pine = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/pine.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		LODModel lod_great_pine = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/great_pine.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		//LODModel lod_pine = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/pine.3ds",null,MIPMAP_TREES),new SimpleModel("models/tree/pine.3ds",null,true)},new float[][]{{0f,7f},{7f,15f}});
		LODModel lod_palm = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/coconut.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		LODModel lod_jungletrees_mult = new LODModel(new SimpleModel[]{new SimpleModel("models/tree/palm.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		LODModel lod_cactus = new LODModel(new SimpleModel[]{new SimpleModel("sides/cactus.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		LODModel lod_bush1 = new LODModel(new SimpleModel[]{new SimpleModel("models/bush/bush1.3ds",null,MIPMAP_TREES)},new float[][]{{0f,15f}});
		
		TextureStateModel tsm_cont_grass = new TextureStateModel(new String[]{"grass1.png","grass1_flower.png","grass1_flower_2.png"},0.9f,0.45f,3,0.7f);
		LODModel lod_cont_grass_1 = new LODModel(new Model[]{tsm_cont_grass},new float[][]{{0f,RENDER_GRASS_DISTANCE}});
		lod_cont_grass_1.rotateOnSteep = true;
		
		TextureStateModel tsm_jung_grass = new TextureStateModel(new String[]{"jungle_foliage1.png","jungle_foliage1_flower.png"},0.7f,0.6f,3,0.7f);
		LODModel lod_jung_grass_1 = new LODModel(new Model[]{tsm_jung_grass},new float[][]{{0f,RENDER_GRASS_DISTANCE}});
		lod_jung_grass_1.rotateOnSteep = true;

		// 3d type to file mapping		
		hm3dTypeRenderedSide.put(new Integer(1), new RenderedContinuousSide(
				new SimpleModel[]{new SimpleModel("sides/wall_thick.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_side.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner_opp.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner_non.3ds", null)}
				));
		hm3dTypeRenderedSide.put(new Integer(5), new RenderedContinuousSide(
				new SimpleModel[]{new SimpleModel("sides/door.3ds", null),new SimpleModel("sides/wall_door.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_side.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner_opp.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner_non.3ds", null)}
				));
		hm3dTypeRenderedSide.put(new Integer(6), new RenderedContinuousSide(
				new SimpleModel[]{new SimpleModel("sides/wall_window.3ds", null),new SimpleModel("sides/window1.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_side.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner_opp.3ds", null)},
				new SimpleModel[]{new SimpleModel("sides/roof_corner_non.3ds", null)}
				));


		hm3dTypeRenderedSide.put(new Integer(7), new RenderedTopSide(
				//new SimpleModel[]{},
				new SimpleModel[]{new SimpleModel("sides/ceiling_pattern1.3ds",null)},
				new SimpleModel[]{new SimpleModel("sides/roof_top.3ds", null)}
				));


		//hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide(new Model[]{new SimpleModel("models/ground/cont_grass.3ds",null)}));//,lod_grass_tsm_1}));
		SimpleModel sm_grass = new SimpleModel("models/ground/cont_grass.3ds",null); sm_grass.rotateOnSteep = true;
		SimpleModel sm_road_stone = new SimpleModel("models/ground/road_stone_1.3ds",null); sm_road_stone.rotateOnSteep = true;
		SimpleModel sm_desert = new SimpleModel("models/ground/desert_1.3ds",null); sm_desert.rotateOnSteep = true;
		SimpleModel sm_arctic = new SimpleModel("models/ground/arctic_1.3ds",null); sm_arctic.rotateOnSteep = true;
		SimpleModel sm_jungle = new SimpleModel("models/ground/jung_grass.3ds",null); sm_jungle.rotateOnSteep = true;
		hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide(new Model[]{sm_grass,lod_cont_grass_1}));
		
		hm3dTypeRenderedSide.put(new Integer(3), new RenderedSide(new Model[]{sm_road_stone}));
		hm3dTypeRenderedSide.put(new Integer(4), new RenderedSide("sides/ceiling_pattern1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(16), new RenderedSide(new Model[]{sm_desert}));
		hm3dTypeRenderedSide.put(new Integer(17), new RenderedSide(new Model[]{sm_arctic}));
		hm3dTypeRenderedSide.put(new Integer(21), new RenderedSide("sides/plane.3ds","textures/low/hillside.png"));
		
		hm3dTypeRenderedSide.put(new Integer(22), new RenderedSide(new Model[]{sm_jungle, lod_jung_grass_1}));
		
		hm3dTypeRenderedSide.put(new Integer(8), new RenderedSide("sides/fence.3ds",null));
		
		
		
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
		
		hm3dTypeRenderedSide.put(new Integer(10), new RenderedSide("models/ground/water1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(11), new RenderedSide("models/ground/hill_side.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(13), new RenderedSide("sides/hill.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(14), new RenderedSide("sides/plane.3ds","textures/low/wall_mossy.jpg"));
		hm3dTypeRenderedSide.put(new Integer(27), new RenderedSide("models/ground/hillintersect.3ds",null));
				
		
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
	
	protected DisplaySystem getDisplay()
	{
		return display;
	}
    
	public Camera getCamera()
	{
		return cam;
	}
	
	HashMap<String, RenderedCube> hmCurrentCubes = new HashMap<String, RenderedCube>();
	
	
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
	        dr.setDiffuse(ColorRGBA.white);
	        dr.setAmbient(ColorRGBA.gray);
	        dr.setLocation(new Vector3f(0f, 0f, 0f));
	        cLightState.setTwoSidedLighting(true);
	        
	        lightNode = new LightNode("light", cLightState);
	        lightNode.setLight(dr);

	        lightNode.setTarget(cRootNode);
	        lightNode.setLocalTranslation(new Vector3f(0f, 14f, 0f));

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
	        flare.setRootNode(cRootNode);
	        cRootNode.attachChild(lightNode);

	        // notice that it comes at the end
	        lightNode.attachChild(flare);

	        TriMesh sun = new Sphere(o.id,40,40,20f);
			cRootNode.attachChild(sun);
			//sun.setSolidColor(ColorRGBA.white);
			//sun.setLightCombineMode(TextureState.OFF);
			
			
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
	        return lightNode;
			
	        
		} else
		if (o.type==SimpleMoon.SIMPLE_MOON_ORBITER) {
			TriMesh moon = new Sphere(o.id,40,40,15f);
			
			Texture texture = TextureManager.loadTexture("./data/orbiters/moon.jpg",Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
			
			if (texture!=null) {

				texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
				texture.setApply(Texture.AM_MODULATE);
				texture.setRotation(qTexture);
				TextureState state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
				state.setTexture(texture,0);
				//System.out.println("Texture!");
				
				state.setEnabled(true);
	            
				moon.setRenderState(state);
			}
			moon.updateRenderState();

			cRootNode.attachChild(moon);
			moon.setLightCombineMode(TextureState.OFF);
			moon.setRenderState(getDisplay().getRenderer().createFogState());
			return moon;
		} 
		return null;
			
	}
	
	LightState cLightState, skydomeLightState = null;
	
	/**
	 * Creates the lights for a world orbiter
	 * @param o
	 * @return
	 */
	public LightNode[] createLightsForOrbiter(Orbiter o)
	{
		if (o.type==SimpleSun.SIMPLE_SUN_ORBITER) {
			LightNode dirLightNode = new LightNode("Sun light "+o.id, cLightState);		
			DirectionalLight dirLight = new DirectionalLight();
			dirLight.setDiffuse(new ColorRGBA(1,1,1,1));
			dirLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,1));
			dirLight.setDirection(new Vector3f(0,0,1));
			dirLight.setEnabled(true);
			dirLightNode.setLight(dirLight);
			dirLightNode.setTarget(cRootNode);
			dirLight.setShadowCaster(true);
			cLightState.attach(dirLight);

			LightNode spotLightNode = new LightNode("Sun spotlight "+o.id, skydomeLightState);		
			PointLight spotLight = new PointLight();
			spotLight.setDiffuse(new ColorRGBA(1,1,1,1));
			spotLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,1));
			//spotLight.setDirection(new Vector3f(0,0,1));
			spotLight.setEnabled(true);
			//spotLight.setAngle(90);
			spotLightNode.setLight(spotLight);
			spotLightNode.setTarget(sRootNode);
			skydomeLightState.attach(spotLight);
	        
			return new LightNode[]{dirLightNode,spotLightNode};
		} else
		if (o.type==SimpleMoon.SIMPLE_MOON_ORBITER) {
			LightNode dirLightNode = new LightNode("Moon light "+o.id, cLightState);		
			DirectionalLight dirLight = new DirectionalLight();
			dirLight.setDiffuse(new ColorRGBA(1,1,1,1));
			dirLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,1));
			dirLight.setDirection(new Vector3f(0,0,1));
			dirLight.setEnabled(true);
			dirLightNode.setLight(dirLight);
			dirLightNode.setTarget(cRootNode);
			cLightState.attach(dirLight);

			LightNode spotLightNode = new LightNode("Moon spotlight "+o.id, skydomeLightState);		
			SpotLight spotLight = new SpotLight();
			spotLight.setDiffuse(new ColorRGBA(1,1,1,1));
			spotLight.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,1));
			spotLight.setDirection(new Vector3f(0,0,1));
			spotLight.setEnabled(true);
			spotLight.setAngle(180);
			spotLightNode.setLight(spotLight);
			spotLightNode.setTarget(sRootNode);
			skydomeLightState.attach(spotLight);
			
			return new LightNode[]{dirLightNode,spotLightNode};
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
	
	Node cRootNode = new Node(); 
	/** skyroot */
	Node sRootNode = new Node(); 
	Sphere skySphere = null;
	
	/**
	 * Updates all time related things in the 3d world
	 */
	public void updateTimeRelated()
	{
		Time localTime = engine.getWorldMeanTime().getLocalTime(world, viewPositionX, viewPositionY, viewPositionZ);
		CubeClimateConditions conditions = world.climate.getCubeClimate(localTime, viewPositionX, viewPositionY, viewPositionZ);
		/*
		 * Orbiters
		 */
		// iterating through world's sky orbiters
		for (Orbiter orb : world.getOrbiterHandler().orbiters.values()) {
			if (orbiters3D.get(orb.id)==null)
			{
				Spatial s = createSpatialForOrbiter(orb);
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
					cRootNode.attachChild(s);
				}
				s.setLocalTranslation(new Vector3f(orbiterCoords[0],orbiterCoords[1],orbiterCoords[2]));
				s.updateRenderState();
			}
			else {
				// if there is no coordinates, detach the orbiter
				cRootNode.detachChild(s);
			}
			if (l!=null)
			{
				System.out.println("ORBITER LIGHT "+orb.id);
				
				float[] lightDirectionCoords = orb.getLightDirection(localTime, conditions);
				if (lightDirectionCoords!=null)
				{
					//System.out.println("ORBITER LIGHT "+orb.id +" -- ON");
					// 0. is directional light for the planet surface
					l[0].getLight().setEnabled(true);
					((DirectionalLight)l[0].getLight()).setDirection(new Vector3f(lightDirectionCoords[0],lightDirectionCoords[1],lightDirectionCoords[2]));
					l[0].setTarget(cRootNode);
					cLightState.attach(l[0].getLight());
					float v = orb.getLightPower(localTime, conditions);
					l[0].getLight().setDiffuse(new ColorRGBA(v, v, v, 1));
					l[0].getLight().setAmbient(new ColorRGBA(v, v, v, 1));
					l[0].getLight().setSpecular(new ColorRGBA(v, v, v, 1));
					l[0].getLight().setShadowCaster(true);
					l[0].updateRenderState();

					// 1. is point light for the skysphere
					l[1].getLight().setEnabled(true);
					l[1].setTarget(sRootNode);
					skydomeLightState.attach(l[1].getLight());
					ColorRGBA c = new ColorRGBA(v,v,v,1);
					l[1].getLight().setDiffuse(c);
					l[1].getLight().setAmbient(c);
					l[1].getLight().setSpecular(c);
					l[1].setLocalTranslation(new Vector3f(orbiterCoords[0],orbiterCoords[1],orbiterCoords[2]));
					l[1].updateRenderState();
				
				} else {
					//System.out.println("ORBITER LIGHT "+orb.id +" -- OFF");
					// switching of the two lights
					l[0].getLight().setEnabled(false);
					l[1].getLight().setEnabled(false);
				}
			}
		}

		// SKYSPHERE
		// moving skysphere with camera
		Vector3f sV3f = new Vector3f(cam.getLocation());
		sV3f.y-=4000;
		skySphere.setLocalTranslation(sV3f);
		// Animating skySphere rotated...
		Quaternion qSky = new Quaternion();
		qSky.fromAngleAxis(FastMath.PI*localTime.getCurrentDayPercent()/100, new Vector3f(0,0,-1));
		skySphere.setLocalRotation(qSky);
		//skySphere.updateRenderState();
	    
	    //cRootNode.updateRenderState();
	    //sRootNode.updateRenderState();
		
	}

	public void renderParallel()
	{
		new Thread(this).start();
	}
	
	/**
	 * Renders the scenario, adds new jme Nodes, removes outmoved nodes and keeps old nodes on scenario.
	 */
	public void render()
	{
		modelLoader.setLockForSharedNodes(false);

		// start to collect the nodes/binaries which this render will use now
		modelLoader.startRender();
        
        //cRootNode.setLocalTranslation(rootNode.getLocalTranslation());

        long timeS = System.currentTimeMillis();
		
		System.out.println("**** RENDER ****");
		
		int already = 0;
		int newly = 0;
		int removed = 0;

		Time localTime = engine.getWorldMeanTime().getLocalTime(world, viewPositionX, viewPositionY, viewPositionZ);
		CubeClimateConditions conditions = world.climate.getCubeClimate(localTime, viewPositionX, viewPositionY, viewPositionZ);
		
		
		if (conditions!=null) System.out.println("- "+conditions.getBelt()+" \n - "+ conditions.getSeason()+" \n"+ conditions.getDayTime());

		
		/*
		 * Render cubes
		 */
		
    	// get a specific part of the area to render
		System.out.println("1-RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));
    	RenderedCube[] cubes = renderedArea.getRenderedSpace(world, viewPositionX, viewPositionY, viewPositionZ,viewDirection);
		System.out.println("1-RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));

    	System.out.println("getRenderedSpace size="+cubes.length);
		
		HashMap<String, RenderedCube> hmNewCubes = new HashMap<String, RenderedCube>();

		System.out.println("hmCurrentCubes: "+hmCurrentCubes.keySet().size());
		
	    for (int i=0; i<cubes.length; i++)
		{
			//System.out.println("CUBE "+i);
			RenderedCube c = cubes[i];
			if (hmCurrentCubes.containsKey(""+c.cube.x+" "+c.cube.y+" "+c.cube.z)) 
			{
				already++;
				// yes, we have it rendered...
				// remove to let the unrendered ones in the hashmap for after removal from space of cRootNode
				RenderedCube cOrig = hmCurrentCubes.remove(""+c.cube.x+" "+c.cube.y+" "+c.cube.z);
				
				// Iterating all sides for fake rendering, cache maintenance in ModelLoader
				Side[][] sides = cOrig.cube.sides;
				for (int j=0; j<sides.length; j++)
				{
					if (sides[j]!=null)
					for (int k=0; k<sides[j].length; k++)
						renderSide(cOrig,c.renderedX, c.renderedY, c.renderedZ, j, sides[j][k],true); // fake = true!
				}

				//for (Iterator<Node> itNode = cOrig.hsRenderedNodes.iterator(); itNode.hasNext();)
		    	{
		    		//Node n = itNode.next();
		    		
		    		//n.updateRenderState();
		    		//n.updateModelBound();
		    		//sPass.removeOccluder(n);
		    		//cRootNode.detachChild(itNode.next());
		    		
		    	}
				// add to the new cubes, it is rendered already
				hmNewCubes.put(""+c.cube.x+" "+c.cube.y+" "+c.cube.z,cOrig); // keep cOrig with jme nodes!!
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
			hmNewCubes.put(""+c.cube.x+" "+c.cube.y+" "+c.cube.z,c);
		}
		System.out.println("hmCurrentCubes: "+hmCurrentCubes.keySet().size());
	    for (Iterator it = hmCurrentCubes.values().iterator();it.hasNext();)
	    {
			removed++;
	    	RenderedCube cToDetach = (RenderedCube)it.next();
	    	for (Iterator<Node> itNode = cToDetach.hsRenderedNodes.iterator(); itNode.hasNext();)
	    	{
	    		Node n = itNode.next();
	    		n.removeFromParent();
	    		if (n instanceof SharedNode) n.detachAllChildren();
	    		//sPass.removeOccluder(n);
	    		//cRootNode.detachChild(itNode.next());
	    		
	    	}
	    }
	    hmCurrentCubes = hmNewCubes; // the newly rendered/remaining cubes are now the current cubes

	    

	    //cRootNode.updateRenderState();
		//rootNode.updateModelBound();
		updateTimeRelated();

		System.out.println("RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));

		rootNode.updateRenderState();

		// stop to collect and clean the nodes/binaries which this render will not use now
		modelLoader.stopRenderAndClear();
		
		modelLoader.setLockForSharedNodes(true);
	
		System.gc();

	}
	

	/**
	 * Renders a set of node into 3d space, rotating, positioning them.
	 * @param n Nodes
	 * @param cube the r.cube parent of the nodes, needed for putting the rendered node as child into it.
	 * @param x X cubesized distance from current relativeX
	 * @param y Y cubesized distance from current relativeX
	 * @param z Z cubesized distance from current relativeX
	 * @param direction Direction
	 * @param horizontalRotation Horizontal rotation
	 * @param scale Scale
	 */
	private void renderNodes(Node[] n, RenderedCube cube, int x, int y, int z, int direction, int horizontalRotation, float scale)
	{
		
		if (n==null) return;
		Object[] f = (Object[])directionAnglesAndTranslations.get(new Integer(direction));
		float cX = ((x+relativeX)*CUBE_EDGE_SIZE+1*((int[])f[1])[0]);//+0.5f;
		float cY = ((y+relativeY)*CUBE_EDGE_SIZE+1*((int[])f[1])[1]);//+0.5f;
		float cZ = ((z-relativeZ)*CUBE_EDGE_SIZE+1*((int[])f[1])[2]);//+25.5f;
		
		Quaternion hQ = null;
		if (horizontalRotation!=-1) hQ = horizontalRotations.get(new Integer(horizontalRotation));
	
		for (int i=0; i<n.length; i++) {
			n[i].setLocalTranslation(new Vector3f(cX,cY,cZ));
			Quaternion q = (Quaternion)f[0];
			Quaternion qC = null;
			qC = new Quaternion(q); // base rotation
			if (hQ!=null)
			{
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
					qC.multLocal(steepRotations.get(cube.cube.steepDirection));
					// the necessary local translation : half cube up
					Vector3f newTrans = n[i].getLocalTranslation().add(new Vector3f(0f,CUBE_EDGE_SIZE/2,0f));
					n[i].setLocalTranslation(newTrans);

					// square root 2 is the scaling for that side, so we will set it depending on N-S or E-W steep direction
					if (cube.cube.steepDirection==NORTH||cube.cube.steepDirection==SOUTH)
					{
						// NORTH-SOUTH steep...
						n[i].setLocalScale(new Vector3f(1f,1.43f,1f));
					}
					else
					{
						// EAST-WEST steep...
						n[i].setLocalScale(new Vector3f(1.43f,1,1f));
					}
				}
			}
			
			n[i].setLocalRotation(qC);
			
			n[i].updateRenderState();

			cube.hsRenderedNodes.add(n[i]);
			
			if (n[i] instanceof SharedNode) {
				n[i].lock();
			}
			cRootNode.attachChild(n[i]);
			//if (n[i].getName().indexOf("tree")!=-1)
			//sPass.addOccluder(n[i]);
		}
	}
	private void renderNodes(Node[] n, RenderedCube cube, int x, int y, int z, int direction)
	{
		renderNodes(n, cube, x, y, z, direction, -1, 1f);
	}
	
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
		Integer n3dType = hmAreaSubType3dType.get(side.subtype.id);
		if (n3dType==null) return;
		if (n3dType.equals(EMPTY_SIDE)) return;
		RenderedSide renderedSide = hm3dTypeRenderedSide.get(n3dType);
		
		
		Node[] n = modelLoader.loadObjects(cube,renderedSide.objects,fakeLoadForCacheMaint);
		if (!fakeLoadForCacheMaint) {
			if (renderedSide instanceof RenderedHashRotatedSide)
			{
				int rD = ((RenderedHashRotatedSide)renderedSide).rotation(cube.cube.x, cube.cube.y, cube.cube.z);
				float scale = ((RenderedHashRotatedSide)renderedSide).scale(cube.cube.x, cube.cube.y, cube.cube.z);
				renderNodes(n, cube, x, y, z, direction, rD,scale);
			} 
			else
			{
				renderNodes(n, cube, x, y, z, direction);
			}
		}

		Cube checkCube = null;
		if (direction==TOP && renderedSide instanceof RenderedTopSide) // Top Side
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
				System.out.println("# TOP IS NULL!");
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
				n= modelLoader.loadObjects(cube,((RenderedTopSide)renderedSide).nonEdgeObjects,fakeLoadForCacheMaint);
				if (!fakeLoadForCacheMaint)
				{
					renderNodes(n, cube, x, y, z, direction);
				}
			}
		}
		if (direction!=TOP && direction!=BOTTOM && renderedSide instanceof RenderedContinuousSide) // Continuous side
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
						n = modelLoader.loadObjects(cube, ((RenderedContinuousSide)renderedSide).continuous, fakeLoadForCacheMaint );
						if (!fakeLoadForCacheMaint)
						{
							renderNodes(n, cube, x, y, z, direction);
						}
					} else
					{
						// normal direction is continuous
						n = modelLoader.loadObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousNormal, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint)
						{
							renderNodes(n, cube, x, y, z, direction);
						}
					}
				} else
				{
					// normal direction is continuous
					n = modelLoader.loadObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousNormal, fakeLoadForCacheMaint );
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
						n = modelLoader.loadObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
					
					}else
					{
						// no continuous side found
						n = modelLoader.loadObjects(cube, ((RenderedContinuousSide)renderedSide).nonContinuous, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
					}
				} else {
					// opposite to normal direction is continuous 
					// normal direction is continuous
					n = modelLoader.loadObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
					if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
				}
			} else {
				// opposite to normal direction is continuous 
				// normal direction is continuous
				n = modelLoader.loadObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
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
	}
	
	public Vector3f getCurrentLocation()
	{
		return new Vector3f(relativeX*CUBE_EDGE_SIZE,relativeY*CUBE_EDGE_SIZE+0.11f+(onSteep?1.5f:0f),-1*relativeZ*CUBE_EDGE_SIZE);
	}
	
	
	/**
	 * Tells if any of a set of sides is of a set of sideSubTypes. 
	 * @param sides
	 * @param classNames
	 * @return
	 */
	public boolean hasSideOfInstance(Side[] sides, HashSet<Class> classNames)
	{
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
	
	/**
	 * The base movement method.
	 * @param direction The direction to move.
	 */
	public int[] calcMovement(int[] orig, int direction)
	{
		int[] r = new int[3];
		int[] vector = moveTranslations.get(new Integer(direction));
		r[0] = orig[0]+vector[0];
		r[1] = orig[1]+vector[1];
		r[2] = orig[2]+vector[2];
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
	
	/**
	 * Tries to move in directions, and sets coords if successfull
	 * @param from From coordinates (world coords)
	 * @param fromRel From coordinates relative (3d space coords)
	 * @param directions A set of directions to move into
	 */
	public void move(int[] from, int[] fromRel, int[] directions)
	{
		int[] newCoords = from;
		int[] newRelCoords = fromRel;
		for (int i=0; i<directions.length; i++) {
			System.out.println("Moving dir: "+directions[i]);
			newCoords = calcMovement(newCoords, directions[i]); 
			newRelCoords = calcMovement(newRelCoords, directions[i]);
		}

		Cube c = world.getCube(from[0], from[1], from[2]);
		
		if (c!=null) {
			System.out.println("Current Cube = "+c.toString());
			// get current steep dir for knowing if checking below or above Cube for moving on steep 
			int currentCubeSteepDirection = hasSideOfInstanceInAnyDir(c, climbers);
			System.out.println("STEEP DIRECTION"+currentCubeSteepDirection+" - "+directions[0]);
			if (currentCubeSteepDirection==oppositeDirections.get(new Integer(directions[0])).intValue())
			{
				newCoords = calcMovement(newCoords, TOP); 
				newRelCoords = calcMovement(newRelCoords, TOP);
			}
			Side[] sides = c.getSide(directions[0]);
			if (sides!=null)
			{
				System.out.println("SAME CUBE CHECK: NOTPASSABLE");
				if (hasSideOfInstance(sides, notPassable)) return;
				System.out.println("SAME CUBE CHECK: NOTPASSABLE - passed");
			}
			Cube nextCube = world.getCube(newCoords[0], newCoords[1], newCoords[2]);
			if (nextCube==null) System.out.println("NEXT CUBE = NULL");
			if (nextCube!=null)
			{
				System.out.println("Next Cube = "+nextCube.toString());
				sides = nextCube.getSide(oppositeDirections.get(new Integer(directions[0])).intValue());
				//sides = c2.getSide(oppositeDirections.get(new Integer(directions[0])).intValue());
				if (sides!=null)
				{
					if (hasSideOfInstance(sides, notPassable)) return;
				}

				sides = nextCube!=null?nextCube.getSide(BOTTOM):null;
				if (sides!=null)
				{
					if (hasSideOfInstance(sides, notWalkable)) return;
				}

				// checking steep setting
				int nextCubeSteepDirection = hasSideOfInstanceInAnyDir(nextCube, climbers);
				if (nextCubeSteepDirection!=-1) {
					onSteep = true;
					//move(newCoords,newRelCoords,new int[]{directions[0],TOP});
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
					if (yMinus>10) break; /// i am faaaalling.. :)
					if (nextCube==null) continue;

					sides = nextCube!=null?nextCube.getSide(directions[0]):null;
					if (sides!=null)
					{
						// Try to get climber side
						if (hasSideOfInstance(sides, climbers))
						{
							sides = nextCube!=null?nextCube.getSide(BOTTOM):null;
							if (hasSideOfInstance(sides, notWalkable)) return;
							newCoords[1] = newCoords[1]-(yMinus-1);
							newRelCoords[1] = newRelCoords[1]-(yMinus-1);
							onSteep = true; // found steep
							break;
						}
					} else
					{
						// no luck, let's see notPassable bottom...
						sides = nextCube!=null?nextCube.getSide(BOTTOM):null;
						if (sides!=null)
						if (hasSideOfInstance(sides, notPassable))
						{							
							newCoords[1] = newCoords[1]-(yMinus-1);
							newRelCoords[1] = newRelCoords[1]-(yMinus-1);
							onSteep = false; // yeah, found
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
	}
	
	public void moveForward(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		move(coords,relCoords,new int[]{direction});
	}

	/**
	 * Move view Left (strafe)
	 * @param direction
	 */
	public void moveLeft(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		if (direction == NORTH) {
			move(coords,relCoords,new int[]{WEST});
		} else if (direction == SOUTH) {
			move(coords,relCoords,new int[]{EAST});
		} else if (direction == EAST) {
			move(coords,relCoords,new int[]{NORTH});
		} else if (direction == WEST) {
			move(coords,relCoords,new int[]{SOUTH});
		}
	}
	/**
	 * Move view Right (strafe)
	 * @param direction
	 */
	public void moveRight(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		if (direction == NORTH) {
			move(coords,relCoords,new int[]{EAST});
		} else if (direction == SOUTH) {
			move(coords,relCoords,new int[]{WEST});
		} else if (direction == EAST) {
			move(coords,relCoords,new int[]{SOUTH});
		} else if (direction == WEST) {
			move(coords,relCoords,new int[]{NORTH});
		}
	}

	public void moveBackward(int direction) {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		move(coords,relCoords,new int[]{oppositeDirections.get(new Integer(direction)).intValue()});
	}

	
	/**
	 * Move view Up (strafe)
	 * @param direction
	 */
	public void moveUp() {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		move(coords,relCoords,new int[]{TOP});
	}
	public void moveDown() {
		int[] coords = new int[]{viewPositionX,viewPositionY,viewPositionZ};
		int[] relCoords = new int[]{relativeX,relativeY,relativeZ};
		move(coords,relCoords,new int[]{BOTTOM});
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
	int cc = 0;
	public void updateDisplay(Vector3f from)
	{
		rootNode.updateModelBound();
		rootNode.updateRenderState();
		sRootNode.updateModelBound();
		cRootNode.updateRenderState();

		noInput = true;
        // update game state, do not use interpolation parameter
        update(-1.0f);

        // render, do not use interpolation parameter
        render(-1.0f);

        // swap buffers
        display.getRenderer().displayBackBuffer();
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
 
	private FogState fs;
    private static ShadowedRenderPass sPass = new ShadowedRenderPass();

    
	@Override
	protected void simpleInitGame() {

		//DisplaySystem.getDisplaySystem().getRenderer().getQueue().setTwoPassTransparency(false);
		//rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        cam.setFrustumPerspective(45.0f,(float) display.getWidth() / (float) display.getHeight(), 1, 6500);
		rootNode.attachChild(cRootNode);
		rootNode.attachChild(sRootNode);
       	

        AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as.setEnabled(true);
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE_MINUS_DST_ALPHA);
		as.setReference(0.0f);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
		
		fs = display.getRenderer().createFogState();
        fs.setDensity(0.5f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        fs.setEnd(RENDER_DISTANCE*2.3f);
        fs.setStart(RENDER_DISTANCE - (RENDER_DISTANCE/3));
        fs.setDensityFunction(FogState.DF_LINEAR);
        fs.setApplyFunction(FogState.AF_PER_VERTEX);
        cRootNode.setRenderState(fs);
		
        /*sPass.add(rootNode);
        sPass.setRenderShadows(true);
        sPass.setLightingMethod(ShadowedRenderPass.ADDITIVE);
        pManager.add(sPass);*/
 		
		lightState.detachAll();
		cLightState = getDisplay().getRenderer().createLightState();
		skydomeLightState = getDisplay().getRenderer().createLightState();

		cRootNode.setRenderState(cLightState);
		sRootNode.setRenderState(skydomeLightState);
		
		/*
		 * Skysphere
		 */
		skySphere = new Sphere("SKY_SPHERE",20,20,5700f);
		sRootNode.attachChild(skySphere);
		skySphere.setModelBound(new BoundingSphere(3000f,new Vector3f(0,0,0)));
		skySphere.setTextureMode(TextureState.RS_ALPHA);
		Texture texture = TextureManager.loadTexture("./data/sky/day/top.jpg",Texture.MM_LINEAR,
                Texture.FM_LINEAR);
		
		sRootNode.setRenderState(as);
		skySphere.setRenderState(as);

		
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
		
		setCalculatedCameraLocation();
        cam.update();
		updateDisplay(null);
	
		render(); // couldn't find out why render have to be called twice...
		render();
		engine.setPause(false);
	}
	
	

	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		//pManager.updatePasses(tpf);
		cLightState.apply();
		skydomeLightState.apply();
		if (engine.timeChanged) 
		{
			engine.setTimeChanged(false);
			updateTimeRelated();
			rootNode.updateRenderState();
		}
	}

	@Override
	protected void simpleRender() {
        //pManager.renderPasses(display.getRenderer());
		super.simpleRender();
		cLightState.apply();
		skydomeLightState.apply();
	}
	
    protected BasicPassManager pManager;

    boolean rendering = false;
    Object mutex = new Object();
	public void run() {
		
		if (rendering) return;
		synchronized (mutex) {
			rendering = true;		
			renderParallel();
			rendering = false;
		}
	}

	  


}
