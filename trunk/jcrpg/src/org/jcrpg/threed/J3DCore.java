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
import org.jcrpg.threed.scene.BillboardModel;
import org.jcrpg.threed.scene.ImposterModel;
import org.jcrpg.threed.scene.LODModel;
import org.jcrpg.threed.scene.Model;
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
import org.lwjgl.opengl.GLContext;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.BillboardNode;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.ImposterNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;

public class J3DCore extends com.jme.app.SimpleGame{

    HashMap<String,Integer> hmAreaSubType3dType = new HashMap<String,Integer>();

    HashMap<Integer,RenderedSide> hm3dTypeRenderedSide = new HashMap<Integer,RenderedSide>();
    
	/**
	 * rendered cubes in each direction (N,S,E,W,T,B).
	 */
    public static int RENDER_DISTANCE = 15;

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
	
	public ModelLoader modelLoader = new ModelLoader(this);
	
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

		
		LODModel lod_jungleMiddleSmall = new LODModel(new SimpleModel[]{new SimpleModel("sides/jungle_middle_small.3ds",null)},new float[][]{{0f,6f}});

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
		//hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide("mtl/trans3.3ds",null));
		//hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide(new Model[]{new SimpleModel("sides/ground_cont_grass.3ds",null),jungleMiddleSmall}));
		
		//hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide("sides/tgrass1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(3), new RenderedSide("sides/ground_road_stone_1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(4), new RenderedSide("sides/ceiling_pattern1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(16), new RenderedSide("sides/ground_desert_1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(17), new RenderedSide("sides/ground_arctic_1.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(21), new RenderedSide("sides/plane.3ds","textures/hillside.png"));
		
		hm3dTypeRenderedSide.put(new Integer(22), new RenderedHashRotatedSide(new Model[]{new SimpleModel("sides/ground_jung_grass.3ds",null),
				lod_jungleMiddleSmall
				//new SimpleModel("sides/jungle_middle_small.3ds",null)
		
		}));
		
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
	
	protected DisplaySystem getDisplay()
	{
		return display;
	}
    
	protected Node[] loadObjects(Model[] objects)
    {
		
		Node[] r = null;
		r = new Node[objects.length];
		if (objects!=null)
		for (int i=0; i<objects.length; i++) {
			if (objects[i]==null) continue;
			if (objects[i] instanceof SimpleModel) 
			{
				Node node = modelLoader.loadNode((SimpleModel)objects[i]); // Where to dump mesh.
				r[i] = node;
				node.setName(((SimpleModel)objects[i]).modelName+i);
			} else
			// ** LODModel **
			if (objects[i] instanceof LODModel)
			{
				LODModel lm = (LODModel)objects[i];
				
				int c=0; // counter
				DistanceSwitchModel dsm = new DistanceSwitchModel(lm.models.length);
				DiscreteLodNode lodNode = new DiscreteLodNode("dln",dsm);
				for (Model m : lm.models) {
					Node node = modelLoader.loadNode((SimpleModel)m);
					if (m instanceof BillboardModel)
					{

						BillboardNode iNode = new BillboardNode("a");
						iNode.attachChild(node);
						iNode.setAlignment(BillboardNode.AXIAL_Z); 
					    node = iNode;
					}
					if (m instanceof ImposterModel)
					{
						// TODO imposter node, if FBO present (?) TEST
						/*						
						 * boolean FBOEnabled = GLContext.getCapabilities().GL_EXT_framebuffer_object;*/
						/*TextureRenderer tRenderer = DisplaySystem.getDisplaySystem().createTextureRenderer(
								1, 1, TextureRenderer.RENDER_TEXTURE_RECTANGLE);
						tRenderer.getCamera().setLocation(new Vector3f(0, 0, 75f));
						tRenderer.setBackgroundColor(new ColorRGBA(0, 0, 0, 0f));*/
						boolean FBOEnabled = GLContext.getCapabilities().GL_EXT_framebuffer_object;
						if (FBOEnabled)
						{
							ImposterNode iNode = new ImposterNode("a",10,10,10);
							iNode.attachChild(node);
						    node = iNode;
						}

					}
					lodNode.attachChildAt(node,c);
					dsm.setModelDistance(c, lm.distances[c][0], lm.distances[c][1]);
					c++;
				}
				
				r[i] = lodNode;
			}
		}
		return r;
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
			sun.setSolidColor(ColorRGBA.white);
			sun.setLightCombineMode(TextureState.OFF);
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
			float[] f = orb.getCurrentCoordinates(localTime, conditions); // get coordinates of the orbiter
			if (f!=null)
			{
				if (s.getParent()==null)
				{
					// newly appearing, attach to root
					cRootNode.attachChild(s);
				}
				s.setLocalTranslation(new Vector3f(f[0],f[1],f[2]));
				s.updateRenderState();
			}
			else {
				// if there is no coordinates, detach the orbiter
				cRootNode.detachChild(s);
			}
			if (l!=null)
			{
				System.out.println("ORBITER LIGHT "+orb.id);
				
				float[] f2 = orb.getLightDirection(localTime, conditions);
				if (f2!=null)
				{
					System.out.println("ORBITER LIGHT "+orb.id +" -- ON");
					// 0. is directional light for the planet surface
					l[0].getLight().setEnabled(true);
					((DirectionalLight)l[0].getLight()).setDirection(new Vector3f(f2[0],f2[1],f2[2]));
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
					l[1].setLocalTranslation(new Vector3f(f[0],f[1],f[2]));
					l[1].updateRenderState();
				
				} else {
					System.out.println("ORBITER LIGHT "+orb.id +" -- OFF");
					// switching of the two lights
					l[0].getLight().setEnabled(false);
					l[1].getLight().setEnabled(false);
				}
			}
		}

		// SKYSPHERE
		// moving skysphere with camera
		Vector3f sV3f = new Vector3f(cam.getLocation());
		sV3f.y-=600;
		skySphere.setLocalTranslation(sV3f);
		// Animating skySphere rotated...
		Quaternion qSky = new Quaternion();
		qSky.fromAngleAxis(FastMath.PI*localTime.getCurrentDayPercent()/100, new Vector3f(0,0,-1));
		skySphere.setLocalRotation(qSky);
	    
	    cRootNode.updateRenderState();
		//rootNode.updateRenderState();
		
	}
	
	public void render()
	{
        
        //cRootNode.setLocalTranslation(rootNode.getLocalTranslation());

        long timeS = System.currentTimeMillis();
		
		System.out.println("**** RENDER ****");
		
		int already = 0;
		int newly = 0;
		int removed = 0;

		Time localTime = engine.getWorldMeanTime().getLocalTime(world, viewPositionX, viewPositionY, viewPositionZ);
		CubeClimateConditions conditions = world.climate.getCubeClimate(localTime, viewPositionX, viewPositionY, viewPositionZ);
		
		
		System.out.println("- "+conditions.getBelt()+" \n - "+ conditions.getSeason()+" \n"+ conditions.getDayTime());

		
		/*
		 * Render cubes
		 */
		
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
				// remove to let the unrendered ones in the hashmap for after removal from space of cRootNode
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
	    		//sPass.removeOccluder(n);
	    		//cRootNode.detachChild(itNode.next());
	    		
	    	}
	    }
	    hmCurrentCubes = hmNewCubes; // the newly rendered/remaining cubes are now the current cubes

	    
		updateTimeRelated();

	    cRootNode.updateRenderState();
		rootNode.updateRenderState();
		rootNode.updateModelBound();

		System.out.println("RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));

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
				qC = qC.multLocal(hQ);
			} 
			n[i].setLocalRotation(qC);
			
			n[i].updateRenderState();

			cube.hsRenderedNodes.add(n[i]);
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
	 * Renders one side into 3d space.
	 * @param cube
	 * @param x
	 * @param y
	 * @param z
	 * @param direction
	 * @param side
	 */
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
	
	/**
	 * Sets the camera to its proper current position
	 */
	public void setCalculatedCameraLocation()
	{
		cam.setLocation(getCurrentLocation());
		cam.setDirection(turningDirectionsUnit[viewDirection]);
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
	public void updateDisplay()
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
        //rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        //cam.setFrustumPerspective(45.0f,(float) display.getWidth() / (float) display.getHeight(), 1, 1000);
		rootNode.attachChild(cRootNode);
		rootNode.attachChild(sRootNode);

		/*fs = display.getRenderer().createFogState();
        fs.setDensity(0.5f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        fs.setEnd(1000);
        fs.setStart(1);
        fs.setDensityFunction(FogState.DF_LINEAR);
        fs.setApplyFunction(FogState.AF_PER_VERTEX);
        cRootNode.setRenderState(fs);*/
		
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
		skySphere = new Sphere("SKY_SPHERE",20,20,1000f);
		sRootNode.attachChild(skySphere);
		skySphere.setTextureMode(TextureState.RS_ALPHA);
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
		
		setCalculatedCameraLocation();
        cam.update();
		updateDisplay();
	
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



}
