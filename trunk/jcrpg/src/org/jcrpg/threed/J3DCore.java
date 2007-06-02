package org.jcrpg.threed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.threed.input.ClassicInputHandler;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.RenderedSide;

import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.BumpMapColorController;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.MaxToJme;

public class J3DCore extends com.jme.app.SimpleGame{

    HashMap hmAreaType3dType = new HashMap();

    HashMap hm3dTypeFile = new HashMap();
    
    public static Integer EMPTY_SIDE = new Integer(0);
	
	public J3DCore()
	{
		// area type to 3d type mapping
		hmAreaType3dType.put(new Integer(0), EMPTY_SIDE);
		hmAreaType3dType.put(new Integer(1), new Integer(1));
		hmAreaType3dType.put(new Integer(2), new Integer(2));
		
		// 3d type to file mapping		
		hm3dTypeFile.put(new Integer(1), new RenderedSide("sides/grass.3ds","sides/wall_mossy.jpg"));
		hm3dTypeFile.put(new Integer(2), new RenderedSide("sides/grass.3ds","sides/grass_leaf.jpg"));
		
	}

	public void initCore()
	{
       this.setDialogBehaviour(J3DCore.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        this.start();
	}
	
	protected void initSystem() throws JmeException
	{
		super.initSystem();
		input = new ClassicInputHandler(cam);

	}
	

    @Override
	protected void updateInput() {
		// TODO Auto-generated method stub
	
		fpsNode.detachAllChildren();   	
    	super.updateInput();
	}
    
    
    
    public Skybox  createSkybox(){

        
        Skybox skybox = new Skybox("skybox", 100, 100, 100);
        
        Texture north = TextureManager.loadTexture(
                    "./data/sky/north.jpg",
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
        Texture south = TextureManager.loadTexture(
                    "./data/sky/south.jpg",
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
        Texture east = TextureManager.loadTexture(
                    "./data/sky/east.jpg",
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
        Texture west = TextureManager.loadTexture(
                    "./data/sky/west.jpg",
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
        Texture up = TextureManager.loadTexture(
                    "./data/sky/top.jpg",
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
        Texture down = TextureManager.loadTexture(
                    "./data/sky/bottom.jpg",
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR);
        
        skybox.setTexture(Skybox.NORTH, north);
        skybox.setTexture(Skybox.WEST, west);
        skybox.setTexture(Skybox.SOUTH, south);
        skybox.setTexture(Skybox.EAST, east);
        skybox.setTexture(Skybox.UP, up);
        skybox.setTexture(Skybox.DOWN, down);
        return skybox;
        
   }


	protected Node loadNode(int areaType)
    {
		Integer n3dType = (Integer)hmAreaType3dType.get(new Integer(areaType));
		if (n3dType.equals(EMPTY_SIDE)) return null;
		
		RenderedSide file = (RenderedSide)hm3dTypeFile.get(n3dType);
		
		MaxToJme maxtojme = new MaxToJme(); 
		Node node = null; //Where to dump mesh.
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(); //For loading the raw file
				
		try {
			FileInputStream is = new FileInputStream(new File("./data/"+file.modelName));
			

			// Converts the file into a jme usable file
			maxtojme.convert(is, bytearrayoutputstream);
		 
		    // Used to convert the jme usable file to a TriMesh
		    BinaryImporter binaryImporter = new BinaryImporter(); 
		    ByteArrayInputStream in=new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
		 
		    //importer returns a Loadable, cast to Node
		    node = (Node)binaryImporter.load(in); 
		    is.close();

			if (file.textureName!=null)
			{
				System.out.println("./data/"+file.textureName);
				Texture texture = TextureManager.loadTexture("./data/"+file.textureName,Texture.MM_LINEAR,
	                    Texture.FM_LINEAR);
//				Image img = new Image();
//				texture.setImage(img);

				texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
				texture.setApply(Texture.AM_ADD);
				//texture.setCombineFuncRGB(Texture.ACF_ADD);
				//texture.setCombineSrc0RGB(Texture.ACS_TEXTURE);
				//texture.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);

				TextureState ts = display.getRenderer().createTextureState();
				ts.setTexture(texture, 0);
				System.out.println("Texture!");
				
                ts.setEnabled(true);
                
				node.setRenderState(ts);
				
			}

		} catch(Exception err)  {
		    System.out.println("Error loading md3 model:"+err);
		}
		return node;
    }
	
	
	/**
	 * cube side rotation quaternion
	 */
	static Quaternion qN, qS, qW, qE, qT, qB;	
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
		
	}
	
	public static HashMap directionAnglesAndTranslations = new HashMap();
	static 
	{
		directionAnglesAndTranslations.put(new Integer(Cube.NORTH), new Object[]{qN,new float[]{0,0,1}});
		directionAnglesAndTranslations.put(new Integer(Cube.SOUTH), new Object[]{qS,new float[]{0,0,-1}});
		directionAnglesAndTranslations.put(new Integer(Cube.WEST), new Object[]{qW,new float[]{-1,0,0}});
		directionAnglesAndTranslations.put(new Integer(Cube.EAST), new Object[]{qE,new float[]{1,0,0}});
		directionAnglesAndTranslations.put(new Integer(Cube.TOP), new Object[]{qT,new float[]{0,1,0}});
		directionAnglesAndTranslations.put(new Integer(Cube.BOTTOM), new Object[]{qB,new float[]{0,-1,0}});
	}
    
	public RenderedCube[] cubes = null;
	
	@Override
	protected void simpleInitGame() {
		render(cubes);
	}
	
	public void render(RenderedCube[] cubes)
	{
		Skybox skybox = createSkybox();
	    rootNode.attachChild(skybox);
	    
	    for (int i=0; i<cubes.length; i++)
		{
			System.out.println("CUBE "+i);
			RenderedCube c = cubes[i];
			Side[] sides = c.cube.sides;
			for (int j=0; j<sides.length; j++)
			{
				renderSide(c.renderedX, c.renderedY, c.renderedZ, j, sides[j]);
			}
		}		
	}
	
	public static final float CUBE_EDGE_SIZE = 2.004f; 
	
	public void renderSide(int x, int y, int z, int direction, Side side)
	{
		System.out.println("RENDER SIDE: "+x+" "+y+" "+z+" "+direction+" - "+side.type);
		Node n = loadNode(side.type);
		if (n==null) return;
		Object[] f = (Object[])directionAnglesAndTranslations.get(new Integer(direction));
		float cX = (x*CUBE_EDGE_SIZE+1*((float[])f[1])[0]);
		float cY = (y*CUBE_EDGE_SIZE+1*((float[])f[1])[1]);
		float cZ = (z*CUBE_EDGE_SIZE+1*((float[])f[1])[2])+25;
	
		n.setLocalTranslation(new Vector3f(cX,cY,cZ));
		Quaternion q = (Quaternion)f[0];
		n.setLocalRotation(q);
		
		//node.updateRenderState();
		n.updateRenderState();
		//rootNode.attachChild(node);
		rootNode.attachChild(n);
		
		rootNode.updateRenderState();
		
	}


}
