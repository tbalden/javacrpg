package org.jcrpg.threed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
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
		hm3dTypeFile.put(new Integer(1), "sides/wall_sample.3ds");
		hm3dTypeFile.put(new Integer(2), "sides/grass.3ds");
		
	}

	public void initCore()
	{
        this.setDialogBehaviour(J3DCore.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        this.start();
	}
	
	

    @Override
	protected void updateInput() {
		// TODO Auto-generated method stub
	
    	
    	super.updateInput();
	}
    
    
    



	protected Node loadNode(int areaType)
    {
		Integer n3dType = (Integer)hmAreaType3dType.get(new Integer(areaType));
		if (n3dType.equals(EMPTY_SIDE)) return null;
		
		String file = (String)hm3dTypeFile.get(n3dType);
		
		MaxToJme maxtojme = new MaxToJme(); 
		Node node = null; //Where to dump mesh.
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(); //For loading the raw file
				
		try {
			FileInputStream is = new FileInputStream(new File("./data/"+file));
		 
		    // Converts the file into a jme usable file
			maxtojme.convert(is, bytearrayoutputstream);
		 
		    // Used to convert the jme usable file to a TriMesh
		    BinaryImporter binaryImporter = new BinaryImporter(); 
		    ByteArrayInputStream in=new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
		 
		    //importer returns a Loadable, cast to Node
		    node = (Node)binaryImporter.load(in); 
		    is.close();
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
	
	public static final float CUBE_EDGE_SIZE = 2; 
	
	public void renderSide(int x, int y, int z, int direction, Side side)
	{
		System.out.println("RENDER SIDE: "+x+" "+y+" "+z+" "+direction+" - "+side.type);
		Node n = loadNode(side.type);
		if (n==null) return;
		Object[] f = (Object[])directionAnglesAndTranslations.get(new Integer(direction));
		float cX = (x*CUBE_EDGE_SIZE+1*((float[])f[1])[0]);
		float cY = (y*CUBE_EDGE_SIZE+1*((float[])f[1])[1]);
		float cZ = (z*CUBE_EDGE_SIZE+1*((float[])f[1])[2]);
	
		n.setLocalTranslation(new Vector3f(cX,cY,cZ));
		Quaternion q = (Quaternion)f[0];
		n.setLocalRotation(q);
		
		//node.updateRenderState();
		n.updateRenderState();
		//rootNode.attachChild(node);
		rootNode.attachChild(n);
		
	}


}
