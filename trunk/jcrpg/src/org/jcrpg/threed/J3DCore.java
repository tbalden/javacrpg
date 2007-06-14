package org.jcrpg.threed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.jcrpg.space.Area;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.threed.input.ClassicInputHandler;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedContinuousSide;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.RenderedHashRotatedSide;
import org.jcrpg.threed.scene.RenderedSide;
import org.jcrpg.threed.scene.RenderedTopSide;
import org.jcrpg.threed.scene.SimpleModel;
import org.jcrpg.world.Engine;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.River;

import com.jme.image.Texture;
import com.jme.input.action.NodeMouseLook;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.SharedNode;
import com.jme.scene.Skybox;
import com.jme.scene.state.TextureState;
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
    public static int RENDER_DISTANCE = 20;

	public static final float CUBE_EDGE_SIZE = 1.9999f; 
	
	public static final int MOVE_STEPS = 20;

    public static Integer EMPTY_SIDE = new Integer(0);
    
    public static boolean OPTIMIZED_RENDERING = true;

    
	public int viewDirection = NORTH;
	public int viewPositionX = 0;
	public int viewPositionY = 0;
	public int viewPositionZ = 0;
	public int relativeX = 0, relativeY = 0, relativeZ = 0;
	
	
	public Engine engine = null;
	
	public void setEngine(Engine engine)
	{
		this.engine = engine;
	}
	
	public World gameArea = null;
	
	public void setWorld(World area)
	{
		gameArea = area;
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
		qN = new Quaternion();
		qN.fromAngleAxis(FastMath.PI * 2, new Vector3f(0,1,0));
		qS = new Quaternion();
		qS.fromAngleAxis(FastMath.PI, new Vector3f(0,1,0));
		qW = new Quaternion();
		qW.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));
		qE = new Quaternion();
		qE.fromAngleAxis(FastMath.PI * 3 / 2, new Vector3f(0,1,0));
		qTexture = new Quaternion();
		qTexture.fromAngleAxis(FastMath.PI/2, new Vector3f(0,0,1));
		
	}
	
	
	public static HashMap<Integer,Object[]> directionAnglesAndTranslations = new HashMap<Integer,Object[]>();
	static 
	{
		directionAnglesAndTranslations.put(new Integer(NORTH), new Object[]{qN,new int[]{0,0,1}});
		directionAnglesAndTranslations.put(new Integer(SOUTH), new Object[]{qS,new int[]{0,0,-1}});
		directionAnglesAndTranslations.put(new Integer(WEST), new Object[]{qW,new int[]{1,0,0}});
		directionAnglesAndTranslations.put(new Integer(EAST), new Object[]{qE,new int[]{-1,0,0}});
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
    
	public J3DCore()
	{
		// area subtype to 3d type mapping
		hmAreaSubType3dType.put(Side.DEFAULT_SUBTYPE, EMPTY_SIDE);
		hmAreaSubType3dType.put(Plain.SUBTYPE_GRASS, new Integer(2));
		hmAreaSubType3dType.put(Plain.SUBTYPE_TREE, new Integer(9));
		hmAreaSubType3dType.put(Forest.SUBTYPE_GRASS, new Integer(2));
		hmAreaSubType3dType.put(Forest.SUBTYPE_TREE, new Integer(9));
		hmAreaSubType3dType.put(River.SUBTYPE_WATER, new Integer(10));
		/*hmAreaSubType3dType.put(new Integer(2), new Integer(2));
		hmAreaSubType3dType.put(new Integer(3), new Integer(3));
		hmAreaSubType3dType.put(new Integer(4), new Integer(4));
		hmAreaSubType3dType.put(new Integer(5), new Integer(5));
		hmAreaSubType3dType.put(new Integer(6), new Integer(6));
		hmAreaSubType3dType.put(new Integer(7), new Integer(7));
		hmAreaSubType3dType.put(new Integer(8), new Integer(8));
		hmAreaSubType3dType.put(new Integer(9), new Integer(9));*/
		
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

		hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide("sides/plane.3ds","sides/grass2.jpg"));
		//hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide("sides/grass2.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(3), new RenderedSide("sides/plane.3ds","sides/road_stone.jpg"));
		hm3dTypeRenderedSide.put(new Integer(4), new RenderedSide("sides/ceiling_pattern1.3ds",null));
		
		hm3dTypeRenderedSide.put(new Integer(8), new RenderedSide("sides/fence.3ds",null));
		hm3dTypeRenderedSide.put(new Integer(9), new RenderedHashRotatedSide(new SimpleModel[]{new SimpleModel("sides/tree1.3ds",null)}));

		hm3dTypeRenderedSide.put(new Integer(10), new RenderedSide("sides/plane.3ds","sides/water1.jpg"));
				
				//new String[]{"sides/door.3ds","sides/wall_door.3ds","sides/roof_side.3ds"},new String[]{null,null,null}));//"sides/wall_stone.jpg"));
		
	}

	public void initCore()
	{
       this.setDialogBehaviour(J3DCore.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        this.start();
	}
	
	protected void initSystem() throws JmeException
	{
		super.initSystem();
		input = new ClassicInputHandler(this,cam);

	}
	

    
    public Skybox createSkybox() {

		Skybox skybox = new Skybox("skybox", 500, 500, 500);

		Texture north = TextureManager.loadTexture("./data/sky/sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture south = TextureManager.loadTexture("./data/sky/sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture east = TextureManager.loadTexture("./data/sky/sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture west = TextureManager.loadTexture("./data/sky/sky.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture up = TextureManager.loadTexture("./data/sky/top.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture down = TextureManager.loadTexture("./data/sky/bottom.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);

		skybox.setTexture(Skybox.NORTH, north);
		skybox.setTexture(Skybox.WEST, west);
		skybox.setTexture(Skybox.SOUTH, south);
		skybox.setTexture(Skybox.EAST, east);
		skybox.setTexture(Skybox.UP, up);
		skybox.setTexture(Skybox.DOWN, down);
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
	}
	
	HashMap<String, RenderedCube> hmCurrentCubes = new HashMap<String, RenderedCube>();
	
	Skybox skybox = null;
	
	public void render()
	{
		long timeS = System.currentTimeMillis();
		System.out.println("RENDER!");
		int already = 0;
		int newly = 0;
		int removed = 0;
		
		if (skybox==null) {
			skybox = createSkybox();
		    rootNode.attachChild(skybox);
		}

		// moving skybox with view movement vector too.
		skybox.setLocalTranslation(new Vector3f(relativeX*CUBE_EDGE_SIZE,relativeY*CUBE_EDGE_SIZE,-1*relativeZ*CUBE_EDGE_SIZE));
	    skybox.updateRenderState();

    	// get a specific part of the area to render
    	RenderedCube[] cubes = RenderedArea.getRenderedSpace(gameArea, viewPositionX, viewPositionY, viewPositionZ,viewDirection);
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
	

	private void renderNodes(Node[] n, RenderedCube cube, int x, int y, int z, int direction)
	{
		if (n==null) return;
		Object[] f = (Object[])directionAnglesAndTranslations.get(new Integer(direction));
		float cX = ((x+relativeX)*CUBE_EDGE_SIZE+1*((int[])f[1])[0]);//+0.5f;
		float cY = ((y+relativeY)*CUBE_EDGE_SIZE+1*((int[])f[1])[1]);//+0.5f;
		float cZ = ((z-relativeZ)*CUBE_EDGE_SIZE+1*((int[])f[1])[2]);//+25.5f;
	
		for (int i=0; i<n.length; i++) {
			n[i].setLocalTranslation(new Vector3f(cX,cY,cZ));
			Quaternion q = (Quaternion)f[0];
			n[i].setLocalRotation(q);
			
			n[i].updateRenderState();

			cube.hsRenderedNodes.add(n[i]);
			rootNode.attachChild(n[i]);
		}
		
	}
	
	public void renderSide(RenderedCube cube,int x, int y, int z, int direction, Side side)
	{
		Integer n3dType = hmAreaSubType3dType.get(side.subtype);
		if (n3dType.equals(EMPTY_SIDE)) return;
		RenderedSide renderedSide = hm3dTypeRenderedSide.get(n3dType);
		
		
		Node[] n = loadObjects(renderedSide.objects);
		if (renderedSide instanceof RenderedHashRotatedSide)
		{
			int rD = ((RenderedHashRotatedSide)renderedSide).rotation(cube.cube.x, cube.cube.y, cube.cube.z);
			renderNodes(n, cube, x, y, z, rD);
		}
		renderNodes(n, cube, x, y, z, direction);

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
				}
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
		return new Vector3f(relativeX*CUBE_EDGE_SIZE,relativeY*CUBE_EDGE_SIZE+0.11f,-1*relativeZ*CUBE_EDGE_SIZE);
	}
	
	public void moveForward(int direction) {
		if (direction == NORTH) {
			viewPositionZ++;relativeZ++;
		} else if (direction == SOUTH) {
			viewPositionZ--;relativeZ--;
		} else if (direction == EAST) {
			viewPositionX++;relativeX++;
		} else if (direction == WEST) {
			viewPositionX--;relativeX--;
		} else if (direction == TOP) {
			viewPositionY++;relativeY++;
		} else if (direction == BOTTOM) {
			viewPositionY--;relativeY--;
		}
	}

	/**
	 * Move view Left (strafe)
	 * @param direction
	 */
	public void moveLeft(int direction) {
		if (direction == NORTH) {
			viewPositionX--;relativeX--;
		} else if (direction == SOUTH) {
			viewPositionX++;relativeX++;
		} else if (direction == EAST) {
			viewPositionZ++;relativeZ++;
		} else if (direction == WEST) {
			viewPositionZ--;relativeZ--;
		}
	}
	/**
	 * Move view Right (strafe)
	 * @param direction
	 */
	public void moveRight(int direction) {
		if (direction == NORTH) {
			viewPositionX++;relativeX++;
		} else if (direction == SOUTH) {
			viewPositionX--;relativeX--;
		} else if (direction == EAST) {
			viewPositionZ--;relativeZ--;
		} else if (direction == WEST) {
			viewPositionZ++;relativeZ++;
		}
	}

	public void moveBackward(int direction) {
		if (direction == NORTH) {
			viewPositionZ--;relativeZ--;
		} else if (direction == SOUTH) {
			viewPositionZ++;relativeZ++;
		} else if (direction == EAST) {
			viewPositionX--;relativeX--;
		} else if (direction == WEST) {
			viewPositionX++;relativeX++;
		} else if (direction == TOP) {
			viewPositionY--;relativeY--;
		} else if (direction == BOTTOM) {
			viewPositionY++;relativeY++;
		}
	}

	
	/**
	 * Move view Up (strafe)
	 * @param direction
	 */
	public void moveUp() {
		viewPositionY++;relativeY++;		
	}
	public void moveDown() {
		viewPositionY--;relativeY--;		
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
