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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.input.ClassicInputHandler;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedContinuousSide;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.RenderedHashRotatedSide;
import org.jcrpg.threed.scene.RenderedSide;
import org.jcrpg.threed.scene.RenderedTopSide;
import org.jcrpg.threed.scene.SimpleModel;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.flora.ground.Grass;
import org.jcrpg.world.ai.flora.ground.JungleGround;
import org.jcrpg.world.ai.flora.ground.Sand;
import org.jcrpg.world.ai.flora.ground.Snow;
import org.jcrpg.world.ai.flora.middle.deciduous.GreenBush;
import org.jcrpg.world.ai.flora.tree.cactus.BigCactus;
import org.jcrpg.world.ai.flora.tree.deciduous.Acacia;
import org.jcrpg.world.ai.flora.tree.deciduous.CherryTree;
import org.jcrpg.world.ai.flora.tree.deciduous.OakTree;
import org.jcrpg.world.ai.flora.tree.palm.CoconutTree;
import org.jcrpg.world.ai.flora.tree.palm.JunglePalmTrees;
import org.jcrpg.world.ai.flora.tree.pine.GreenPineTree;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.climate.DayTime;
import org.jcrpg.world.climate.impl.generic.Day;
import org.jcrpg.world.climate.impl.generic.Night;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.River;
import org.jcrpg.world.time.Time;

import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.LightNode;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.Skybox;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.XMLparser.Converters.MaxToJme;

public class J3DCore extends com.jme.app.SimpleGame{

    HashMap<String,Integer> hmAreaSubType3dType = new HashMap<String,Integer>();

    HashMap<Integer,RenderedSide> hm3dTypeRenderedSide = new HashMap<Integer,RenderedSide>();
    
	/**
	 * rendered cubes in each direction (N,S,E,W,T,B).
	 */
    public static int RENDER_DISTANCE = 10;

	public static final float CUBE_EDGE_SIZE = 1.9999f; 
	
	public static final int MOVE_STEPS = 20;

    public static Integer EMPTY_SIDE = new Integer(0);
    
    public static boolean OPTIMIZED_RENDERING = true;

    
	public int viewDirection = NORTH;
	public int viewPositionX = 0;
	public int viewPositionY = 0;
	public int viewPositionZ = 0;
	public int relativeX = 0, relativeY = 0, relativeZ = 0;
	public boolean onSteep = false;
	
	
	public Engine engine = null;
	
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
		
		// horizontal rotations
		horizontalN = new Quaternion();
		horizontalN.fromAngles(new float[]{0,0,FastMath.PI * 2});
		horizontalS = new Quaternion();
		horizontalS.fromAngles(new float[]{0,0,FastMath.PI});
		horizontalW = new Quaternion();
		horizontalW.fromAngles(new float[]{0,0,FastMath.PI/2});
		horizontalE = new Quaternion();
		horizontalE.fromAngles(new float[]{0,0,FastMath.PI*3/2});

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
		horizontalRotations.put(new Integer(NORTH), horizontalN);
		horizontalRotations.put(new Integer(SOUTH), horizontalS);
		horizontalRotations.put(new Integer(WEST), horizontalW);
		horizontalRotations.put(new Integer(EAST), horizontalE);
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
		hmAreaSubType3dType.put(Mountain.SUBTYPE_STEEP.id, new Integer(11));
		hmAreaSubType3dType.put(Mountain.SUBTYPE_ROCK.id, EMPTY_SIDE); // 13
		hmAreaSubType3dType.put(Mountain.SUBTYPE_GROUND.id, EMPTY_SIDE);
		hmAreaSubType3dType.put(OakTree.SUBTYPE_TREE.id, new Integer(9));
		hmAreaSubType3dType.put(CherryTree.SUBTYPE_TREE.id, new Integer(12));
		hmAreaSubType3dType.put(GreenPineTree.SUBTYPE_TREE.id, new Integer(18));
		hmAreaSubType3dType.put(GreenBush.SUBTYPE_BUSH.id, new Integer(19));
		hmAreaSubType3dType.put(CoconutTree.SUBTYPE_TREE.id, new Integer(15));
		hmAreaSubType3dType.put(Acacia.SUBTYPE_TREE.id, new Integer(20));
		hmAreaSubType3dType.put(Grass.SUBTYPE_GRASS.id, new Integer(2));
		hmAreaSubType3dType.put(Sand.SUBTYPE_SAND.id, new Integer(16));
		hmAreaSubType3dType.put(Snow.SUBTYPE_SNOW.id, new Integer(17));
		hmAreaSubType3dType.put(JungleGround.SUBTYPE_GROUND.id, new Integer(22));
		hmAreaSubType3dType.put(BigCactus.SUBTYPE_CACTUS.id, new Integer(23));
		hmAreaSubType3dType.put(JunglePalmTrees.SUBTYPE_TREE.id, new Integer(24));
		
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

		hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide("sides/ground_cont_grass.3ds",null));
		//hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide("sides/tgrass1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(3), new RenderedSide("sides/ground_road_stone_1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(4), new RenderedSide("sides/ceiling_pattern1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(16), new RenderedSide("sides/ground_desert_1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(17), new RenderedSide("sides/ground_arctic_1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(21), new RenderedSide("sides/plane.3ds","textures/hillside.png"));
		hm3dTypeRenderedSide.put(new Integer(22), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/ground_jung_grass.3ds",null),new SimpleModel("sides/jungle_middle_small.3ds",null)}));
		
		hm3dTypeRenderedSide.put(new Integer(8), new RenderedSide("sides/fence.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree4.3ds",null)}));
		hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree1.3ds",null)}));
		hm3dTypeRenderedSide.put(new Integer(15), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree_palm.3ds",null)}));
		hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree_pine.3ds",null)}));
		hm3dTypeRenderedSide.put(new Integer(19), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/bush.3ds",null)}));
		hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree_big2.3ds",null)}));
		hm3dTypeRenderedSide.put(new Integer(23), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/cactus.3ds",null)}));
		hm3dTypeRenderedSide.put(new Integer(24), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/jungle_trees_multiple.3ds",null)}));

		//hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree_ng2.3ds",null)}));
		//hm3dTypeRenderedSide.put(new Integer(12), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree_ng2.3ds",null)}));
		//hm3dTypeRenderedSide.put(new Integer(20), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree_ng2.3ds",null)}));
		//hm3dTypeRenderedSide.put(new Integer(18), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree_ng2.3ds",null)}));

		
		hm3dTypeRenderedSide.put(new Integer(10), new RenderedSide("sides/ground_water1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(11), new RenderedSide("sides/hill_side.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(13), new RenderedSide("sides/hill.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(14), new RenderedSide("sides/plane.3ds","sides/wall_mossy.jpg"));
				
				//new String[]{"sides/door.3ds","sides/wall_door.3ds","sides/roof_side.3ds"},new String[]{null,null,null}));//"sides/wall_stone.jpg"));
		
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
	
	private static HashMap<String, Skybox> skyboxCache = new HashMap<String, Skybox>();
    
    public Skybox createSkybox(String prefix) {
    	if (skyboxCache.containsKey(prefix)) return skyboxCache.get(prefix);

		Skybox skybox = new Skybox("skybox", 500, 500, 500);

		String path = "./data/sky/"+prefix+"/";
		
		Texture north = TextureManager.loadTexture(path+"sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture south = TextureManager.loadTexture(path+"sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture east = TextureManager.loadTexture(path+"sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture west = TextureManager.loadTexture(path+"sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture up = TextureManager.loadTexture(path+"sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture down = TextureManager.loadTexture(path+"sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		//LightNode light = new LightNode();
		//light.setLocalTranslation(new Vector3f(0,0,0));
		//light.setTarget(skybox);
		
		
		skybox.attachChild(new LightNode());
		
		skybox.setTexture(Skybox.NORTH, north);
		skybox.setTexture(Skybox.WEST, west);
		skybox.setTexture(Skybox.SOUTH, south);
		skybox.setTexture(Skybox.EAST, east);
		skybox.setTexture(Skybox.UP, up);
		skybox.setTexture(Skybox.DOWN, down);
		skyboxCache.put(prefix, skybox);
		return skybox;

	}

    
    HashMap<String,Texture> textureCache = new HashMap<String,Texture>();
    HashMap<String,byte[]> binaryCache = new HashMap<String,byte[]>();
    HashMap<String,Node> sharedNodeCache = new HashMap<String, Node>();

    private Node loadNode(SimpleModel o)
    {
    	// the big shared node cache -> mem size lowerer and performance boost
    	if (sharedNodeCache.get(o.modelName+o.textureName)!=null)
    	{
    		Node n = sharedNodeCache.get(o.modelName+o.textureName);
    		return new SharedNode("node",n);
    	}
    	
		MaxToJme maxtojme = new MaxToJme();
		try {
			// setting texture directory for 3ds models...
			maxtojme.setProperty(MaxToJme.TEXURL_PROPERTY, new File("./data/textures/").toURI().toURL());
		}
		 catch (IOException ioex)
		 {
			 
		 }
		Node node = null; // Where to dump mesh.
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(); 
		
		try {
			byte[] bytes = null;
			bytes = binaryCache.get(o.modelName);
			if (bytes==null)
			{
				FileInputStream is = new FileInputStream(new File("./data/"+o.modelName));
				// Converts the file into a jme usable file
				maxtojme.convert(is, bytearrayoutputstream);
		 
				// 	Used to convert the jme usable file to a TriMesh
				bytes = (bytearrayoutputstream.toByteArray());
				binaryCache.put(o.modelName,bytes);
			    is.close();
			}
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			BinaryImporter binaryImporter = new BinaryImporter(); 
		    //importer returns a Loadable, cast to Node
		    node = (Node)binaryImporter.load(in);

			if (o.textureName!=null)
			{
				Texture texture = (Texture)textureCache.get(o.textureName);
				
				if (texture==null) {
					texture = TextureManager.loadTexture("./data/"+o.textureName,Texture.MM_LINEAR,
		                    Texture.FM_LINEAR);
	
					texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
					texture.setApply(Texture.AM_REPLACE);
					texture.setRotation(qTexture);
					textureCache.put(o.textureName, texture);
				}

				TextureState ts = display.getRenderer().createTextureState();
				ts.setTexture(texture, 0);
				//System.out.println("Texture!");
				
                ts.setEnabled(true);
                
				node.setRenderState(ts);
				
			}
			sharedNodeCache.put(o.modelName+o.textureName, node);
			return node;
		} catch(Exception err)  {
		    System.out.println("Error loading model:"+err);
		    err.printStackTrace();
		    return null;
		}
    	
    }
    
	protected Node[] loadObjects(SimpleModel[] objects)
    {
		
		Node[] r = null;
		r = new Node[objects.length];
		if (objects!=null)
		for (int i=0; i<objects.length; i++) {
			if (objects[i]==null) continue;
			Node node = loadNode(objects[i]); // Where to dump mesh.
			r[i] = node;
		}
		return r;
    }
	
	
	@Override
	protected void simpleInitGame() {
		//cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
		cam.setFrustumPerspective(45.0f,(float) display.getWidth() / (float) display.getHeight(), 1, 1000);
		setCalculatedCameraLocation();
		render();
		engine.setPause(false);
	}
	
	HashMap<String, RenderedCube> hmCurrentCubes = new HashMap<String, RenderedCube>();
	
	Skybox currentSkyBox = null;
	Node sun = null;
	DirectionalLight sl = null;
	public void render()
	{
		long timeS = System.currentTimeMillis();
		
		System.out.println("**** RENDER ****");
		
		int already = 0;
		int newly = 0;
		int removed = 0;

		Time localTime = engine.getWorldMeanTime().getLocalTime(world, viewPositionX, viewPositionY, viewPositionZ);
		CubeClimateConditions conditions = world.climate.getCubeClimate(localTime, viewPositionX, viewPositionY, viewPositionZ);
		int dayNightPer = conditions.getSeason().dayOrNightPeriodPercentage(localTime);
		
		int dayOrNightPeriodPercentage = dayNightPer; 
		
		// dayNightPer now contain how deep in the period of day / night time is 100 is the middle of the day, 
		// -100 is the middle of the night, 0 is between them.
		if ( dayNightPer>=0) {
			dayNightPer = ( 100 - (Math.abs( dayNightPer - 50 ) * 2) ) /2; 
		}
		if ( dayNightPer<0) {
			dayNightPer = -1 * ( 100 - Math.abs(( Math.abs( dayNightPer ) - 50 ) * 2) ); 
		}

		
		float v = ((dayNightPer+100)/200f)+0.1f;
		// TODO light set to v value, which represent a good basis for sun light strength
		System.out.println("GLOBAL AMBIENT --- "+v+ " -- "+dayNightPer);
		lightState.setTwoSidedLighting(true);
		lightState.setGlobalAmbient(new ColorRGBA(v,v,v,1));
		Light l = lightState.get(0);
		l.setEnabled(false);

		if (sun==null)
		{ 
			sun = new Node();
			rootNode.attachChild(sun);
			LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
			LightNode ln = new LightNode("Sun light", ls);		
			sl = new DirectionalLight();
			sl.setDiffuse(new ColorRGBA(1,1,1,1));
			sl.setAmbient(new ColorRGBA(0.4f, 0.4f, 0.4f,1));
			sl.setDirection(new Vector3f(0,0,1));
			sl.setEnabled(true);
			ln.setLight(sl);
			ln.setTarget(rootNode);
		}
		
		//sun.setLocalTranslation(cam.getLocation());
		sl.setDirection(new Vector3f((Math.abs(dayOrNightPeriodPercentage)-50)/20,-1,0.2f));
		sl.setDiffuse(new ColorRGBA(v, v, v, 1));
		sl.setAmbient(new ColorRGBA(v, v, v, 1));
		sl.setSpecular(new ColorRGBA(v, v, v, 1));
		
		DayTime dT = conditions.getDayTime();
		System.out.println("- "+conditions.getBelt()+" \n - "+ conditions.getSeason()+" \n"+ conditions.getDayTime());
		Skybox newBox = null;
		if (dT instanceof Day)
		{
			newBox = createSkybox("day");
		} else if (dT instanceof Night)
		{
			newBox = createSkybox("night");
		}

		if (newBox!=currentSkyBox)
		{
			rootNode.detachChild(currentSkyBox);
			currentSkyBox = newBox;
			rootNode.attachChild(currentSkyBox);
		}

		
		System.out.println("MOVED TO LOCAL TIME: "+localTime);

		// moving skybox with view movement vector too.
		currentSkyBox.setLocalTranslation(cam.getLocation());//new Vector3f(relativeX*CUBE_EDGE_SIZE,relativeY*CUBE_EDGE_SIZE,-1*relativeZ*CUBE_EDGE_SIZE));
	    currentSkyBox.updateRenderState();
		
		
    	// get a specific part of the area to render
    	RenderedCube[] cubes = RenderedArea.getRenderedSpace(world, viewPositionX, viewPositionY, viewPositionZ,viewDirection);
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
				// remove to let the unrendered ones in the hashmap for after removal from space of rootNode
				RenderedCube cOrig = hmCurrentCubes.remove(""+c.cube.x+" "+c.cube.y+" "+c.cube.z);
				// add to the new cubes, it is rendered already
				hmNewCubes.put(""+c.cube.x+" "+c.cube.y+" "+c.cube.z,cOrig); // keep cOrig with jme nodes!!
				continue;				
			}
			newly++;
			// render the cube newly
			//System.out.println("CUBE Coords: "+ ""+c.cube.x+" "+c.cube.y+" "+c.cube.z);
			Side[][] sides = c.cube.sides;
			for (int j=0; j<sides.length; j++)
			{
				if (sides[j]!=null)
				for (int k=0; k<sides[j].length; k++)
					renderSide(c,c.renderedX, c.renderedY, c.renderedZ, j, sides[j][k]);
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
	    		//rootNode.detachChild(itNode.next());
	    		
	    	}
	    }
	    hmCurrentCubes = hmNewCubes; // the newly rendered/remaining cubes are now the current cubes
		rootNode.updateRenderState();
		System.out.println("RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));
	}
	

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
				qC = qC.multLocal(hQ);
			} 
			n[i].setLocalRotation(qC);
			
			n[i].updateRenderState();

			cube.hsRenderedNodes.add(n[i]);
			rootNode.attachChild(n[i]);
		}
	}
	private void renderNodes(Node[] n, RenderedCube cube, int x, int y, int z, int direction)
	{
		renderNodes(n, cube, x, y, z, direction, -1, 1f);
	}
	
	public void renderSide(RenderedCube cube,int x, int y, int z, int direction, Side side)
	{
		Integer n3dType = hmAreaSubType3dType.get(side.subtype.id);
		if (n3dType==null) return;
		if (n3dType.equals(EMPTY_SIDE)) return;
		RenderedSide renderedSide = hm3dTypeRenderedSide.get(n3dType);
		
		
		Node[] n = loadObjects(renderedSide.objects);
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
				n= loadObjects(((RenderedTopSide)renderedSide).nonEdgeObjects);
				renderNodes(n, cube, x, y, z, direction);
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
						n = loadObjects( ((RenderedContinuousSide)renderedSide).continuous );
						renderNodes(n, cube, x, y, z, direction);
					} else
					{
						// normal direction is continuous
						n = loadObjects(((RenderedContinuousSide)renderedSide).oneSideContinuousNormal);
						renderNodes(n, cube, x, y, z, direction);
					}
				} else
				{
					// normal direction is continuous
					n = loadObjects( ((RenderedContinuousSide)renderedSide).oneSideContinuousNormal );
					renderNodes(n, cube, x, y, z, direction);
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
						n = loadObjects(((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite);
						renderNodes(n, cube, x, y, z, direction);
					
					}else
					{
						// no continuous side found
						n = loadObjects(((RenderedContinuousSide)renderedSide).nonContinuous);
						renderNodes(n, cube, x, y, z, direction);
					}
				} else {
					// opposite to normal direction is continuous 
					// normal direction is continuous
					n = loadObjects(((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite);
					renderNodes(n, cube, x, y, z, direction);
				}
			} else {
				// opposite to normal direction is continuous 
				// normal direction is continuous
				n = loadObjects(((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite);
				renderNodes(n, cube, x, y, z, direction);
			}
			
		}
		
		
	}
	
	public void setCalculatedCameraLocation()
	{
		cam.setLocation(getCurrentLocation());
		cam.setDirection(turningDirectionsUnit[viewDirection]);
	}
	
	public Vector3f getCurrentLocation()
	{
		return new Vector3f(relativeX*CUBE_EDGE_SIZE,relativeY*CUBE_EDGE_SIZE+0.11f+(onSteep?1.5f:0f),-1*relativeZ*CUBE_EDGE_SIZE);
	}
	
	
	public boolean hasSideOfInstance(Side[] sides, HashSet<Class> classNames)
	{
		for (int i=0; i<sides.length; i++)
		{
			if (sides[i]!=null)
			{
				System.out.println("SIDE SUBTYPE: "+sides[i].subtype.getClass().getCanonicalName());
				
				if (classNames.contains(sides[i].subtype.getClass()))
				{
					return true;
				}
			}
		}
		return false;
		
	}

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
					System.out.println("SIDE SUBTYPE: "+sides[i].subtype.getClass().getCanonicalName());
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
	
	public static HashSet<Class> notWalkable = new HashSet<Class>();
	public static HashSet<Class> notPassable = new HashSet<Class>();
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
			Cube c2 = world.getCube(newCoords[0], newCoords[1], newCoords[2]);
			if (c2==null) System.out.println("NEXT CUBE = NULL");
			if (c2!=null)
			{
				System.out.println("Next Cube = "+c2.toString());
				sides = c2.getSide(oppositeDirections.get(new Integer(directions[0])).intValue());
				//sides = c2.getSide(oppositeDirections.get(new Integer(directions[0])).intValue());
				if (sides!=null)
				{
					if (hasSideOfInstance(sides, notPassable)) return;
				}

				sides = c2!=null?c2.getSide(BOTTOM):null;
				if (sides!=null)
				{
					if (hasSideOfInstance(sides, notWalkable)) return;
				}

				// checking steep setting
				int nextCubeSteepDirection = hasSideOfInstanceInAnyDir(c2, climbers);
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
					c2 = world.getCube(newCoords[0], newCoords[1]-(yMinus++), newCoords[2]);
					if (yMinus>10) break; /// i am faaaalling.. :)
					if (c2==null) continue;

					sides = c2!=null?c2.getSide(directions[0]):null;
					if (sides!=null)
					{
						// Try to get climber side
						if (hasSideOfInstance(sides, climbers))
						{
							newCoords[1] = newCoords[1]-(yMinus-1);
							newRelCoords[1] = newRelCoords[1]-(yMinus-1);
							onSteep = true; // found steep
							break;
						}
					} else
					{
						// no luck, let's see notPassable bottom...
						sides = c2!=null?c2.getSide(BOTTOM):null;
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
	public void updateCam()
	{
		rootNode.updateRenderState();

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

}
