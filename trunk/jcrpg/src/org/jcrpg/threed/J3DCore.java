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
import org.jcrpg.threed.scene.RenderedSide;
import org.jcrpg.threed.scene.SimpleModel;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.state.TextureState;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.XMLparser.Converters.MaxToJme;

public class J3DCore extends com.jme.app.SimpleGame{

    HashMap<Integer,Integer> hmAreaSubType3dType = new HashMap<Integer,Integer>();

    HashMap<Integer,RenderedSide> hm3dTypeRenderedSide = new HashMap<Integer,RenderedSide>();
    
	public static int RENDER_DISTANCE = 10;

	public static final float CUBE_EDGE_SIZE = 1.9999f; 
	
	public static final int MOVE_STEPS = 200;

    public static Integer EMPTY_SIDE = new Integer(0);

    
	public int viewDirection = NORTH;
	public int viewPositionX = 0;
	public int viewPositionY = 0;
	public int viewPositionZ = 0;
	public int relativeX = 0, relativeY = 0, relativeZ = 0;
	
	public Area gameArea = null;
	
	public void setArea(Area area)
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
	static 
	{
		// creating rotation quaternions for all sides of a cube...
		qT = new Quaternion();
		qT.fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
		qB = new Quaternion();
		qB.fromAngleAxis(FastMath.PI * 3 / 2, new Vector3f(1,0,0));
		qN = new Quaternion();
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
		hmAreaSubType3dType.put(new Integer(0), EMPTY_SIDE);
		hmAreaSubType3dType.put(new Integer(1), new Integer(1));
		hmAreaSubType3dType.put(new Integer(2), new Integer(2));
		hmAreaSubType3dType.put(new Integer(3), new Integer(3));
		hmAreaSubType3dType.put(new Integer(4), new Integer(4));
		hmAreaSubType3dType.put(new Integer(5), new Integer(5));
		hmAreaSubType3dType.put(new Integer(6), new Integer(6));
		
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

		hm3dTypeRenderedSide.put(new Integer(2), new RenderedSide("sides/plane.3ds","sides/grass2.jpg"));
		hm3dTypeRenderedSide.put(new Integer(3), new RenderedSide("sides/plane.3ds","sides/road_stone.jpg"));
		hm3dTypeRenderedSide.put(new Integer(4), new RenderedSide("sides/ceiling_pattern1.3ds",null));
				
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
	

    @Override
	protected void updateInput() {
		// TODO Auto-generated method stub
	
		//fpsNode.detachAllChildren();   	
    	super.updateInput();
	}
    
    
    
    public Skybox createSkybox() {

		Skybox skybox = new Skybox("skybox", 100, 100, 100);

		Texture north = TextureManager.loadTexture("./data/sky/north.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture south = TextureManager.loadTexture("./data/sky/south.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture east = TextureManager.loadTexture("./data/sky/east.jpg",
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		Texture west = TextureManager.loadTexture("./data/sky/west.jpg",
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

    private Node loadNode(SimpleModel o)
    {
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
			FileInputStream is = new FileInputStream(new File("./data/"+o.modelName));
			

			// Converts the file into a jme usable file
			maxtojme.convert(is, bytearrayoutputstream);
		 
		    // Used to convert the jme usable file to a TriMesh
		    BinaryImporter binaryImporter = new BinaryImporter(); 
		    ByteArrayInputStream in=new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
		 
		    //importer returns a Loadable, cast to Node
		    node = (Node)binaryImporter.load(in); 
		    is.close();

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
			return node;
		} catch(Exception err)  {
		    System.out.println("Error loading model:"+err);
		    err.printStackTrace();
		    return null;
		}
    	
    }
    
    int SWITCH_SIDETYPE_NORMAL = 0;
    int SWITCH_SIDETYPE_CONTINUOUS = 1;
    int SWITCH_SIDETYPE_ONESIDECONTINUOUS_NORMAL = 2;
    int SWITCH_SIDETYPE_ONESIDECONTINUOUS_OPPOSITE = 3;
    int SWITCH_SIDETYPE_NONCONTINUOUS = 4;
    
	protected Node[] loadObjects(RenderedSide file, int switchSideType)
    {
		
		Node[] r = null;
		SimpleModel[] objects = null;
		if (switchSideType == SWITCH_SIDETYPE_NORMAL)
		{
			objects = file.objects;
		} else
		if (switchSideType == SWITCH_SIDETYPE_CONTINUOUS)
		{
			if (file instanceof RenderedContinuousSide)
			{
				objects = ((RenderedContinuousSide)file).continuous;
			}
		} else
		if (switchSideType == SWITCH_SIDETYPE_ONESIDECONTINUOUS_NORMAL)
		{
			if (file instanceof RenderedContinuousSide)
			{
				objects = ((RenderedContinuousSide)file).oneSideContinuousNormal;
			}
		} else
		if (switchSideType == SWITCH_SIDETYPE_ONESIDECONTINUOUS_OPPOSITE)
		{
			if (file instanceof RenderedContinuousSide)
			{
				objects = ((RenderedContinuousSide)file).oneSideContinuousOpposite;
			}
		} else
		if (switchSideType == SWITCH_SIDETYPE_NONCONTINUOUS)
		{
			if (file instanceof RenderedContinuousSide)
			{
				objects = ((RenderedContinuousSide)file).nonContinuous;
			}
		}
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
		cam.setLocation(new Vector3f(0,0,0));
		render();
	}
	
	HashMap<String, RenderedCube> hmCurrentCubes = new HashMap<String, RenderedCube>();
	
	Skybox skybox = null;
	
	public void render()
	{
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
    	RenderedCube[] cubes = RenderedArea.getRenderedSpace(gameArea, viewPositionX, viewPositionY, viewPositionZ);
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
			System.out.println("CUBE Coords: "+ ""+c.cube.x+" "+c.cube.y+" "+c.cube.z);
			Side[] sides = c.cube.sides;
			for (int j=0; j<sides.length; j++)
			{
				renderSide(c,c.renderedX, c.renderedY, c.renderedZ, j, sides[j]);
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
		System.out.println("RSTAT = N"+newly+" A"+already+" R"+removed);
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
		//System.out.println("RENDER SIDE: "+x+" "+y+" "+z+" "+direction+" - "+side.type);
		Integer n3dType = (Integer)hmAreaSubType3dType.get(new Integer(side.subtype));
		if (n3dType.equals(EMPTY_SIDE)) return;
		RenderedSide renderedSide = hm3dTypeRenderedSide.get(n3dType);
		
		
		Node[] n = loadObjects(renderedSide, SWITCH_SIDETYPE_NORMAL);
		renderNodes(n, cube, x, y, z, direction);

		Cube checkCube = null;
		if (direction!=TOP && direction!=BOTTOM && renderedSide instanceof RenderedContinuousSide)
		{
			int dir = nextDirections.get(new Integer(direction)).intValue();
			if (cube.cube.getNeighbour(dir)!=null)
			if (cube.cube.getNeighbour(dir).getSide(direction).type == side.type)
			{
				checkCube = cube.cube.getNeighbour(oppositeDirections.get(new Integer(dir)).intValue());
				if (checkCube !=null) {
					if (checkCube.getSide(direction).type == side.type)
					{
						n = loadObjects(renderedSide, SWITCH_SIDETYPE_CONTINUOUS);
						renderNodes(n, cube, x, y, z, direction);
					} else
					{
						// normal direction is continuous
						n = loadObjects(renderedSide, SWITCH_SIDETYPE_ONESIDECONTINUOUS_NORMAL);
						renderNodes(n, cube, x, y, z, direction);
					}
				}
				
			} else 
			{
				checkCube = cube.cube.getNeighbour(oppositeDirections.get(new Integer(dir)).intValue());
				if (checkCube!=null)
				{
					if (checkCube.getSide(direction).type == side.type)
					{
						// opposite to normal direction is continuous 
						// normal direction is continuous
						n = loadObjects(renderedSide, SWITCH_SIDETYPE_ONESIDECONTINUOUS_OPPOSITE);
						renderNodes(n, cube, x, y, z, direction);
					
					}else
					{
						// no continuous side found
						n = loadObjects(renderedSide, SWITCH_SIDETYPE_NONCONTINUOUS);
						renderNodes(n, cube, x, y, z, direction);
					}
				}
			} 
			
		}
		
		
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
		render();
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
		render();
	}

	
	public void turnRight()
	{
		viewDirection++;
		if (viewDirection==directions.length) viewDirection = 0;
	}
	public void turnLeft()
	{
		viewDirection--;
		if (viewDirection==-1) viewDirection = directions.length-1;
	}
	

}
