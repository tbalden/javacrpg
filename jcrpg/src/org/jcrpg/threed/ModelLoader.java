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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;

import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.BillboardModel;
import org.jcrpg.threed.scene.model.ImposterModel;
import org.jcrpg.threed.scene.model.LODModel;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.PartlyBillboardModel;
import org.jcrpg.threed.scene.model.QuadModel;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.TextureStateVegetationModel;
import org.lwjgl.opengl.GLContext;

import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BillboardNode;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.Geometry;
import com.jme.scene.ImposterNode;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.XMLparser.Converters.MaxToJme;
import com.jmex.model.XMLparser.Converters.ObjToJme;

public class ModelLoader {

	J3DCore core = null;
	
	public ModelLoader(J3DCore core)
	{
		this.core = core;
	}
	
    
    WeakHashMap<String,Texture> textureCache = new WeakHashMap<String,Texture>();
    WeakHashMap<String,byte[]> binaryCache = new WeakHashMap<String,byte[]>();
    // this better be not weak hashmap
    HashMap<String,Node> sharedNodeCache = new HashMap<String, Node>();
    HashMap<String,TextureState> textureStateCache = new HashMap<String,TextureState>();
    
    int counter=0;
    
    AlphaState as;

    HashMap<String, Node> vegetationTargetCache = new HashMap<String, Node>();
    
	/**
	 * Loading a set of models into JME nodes.
	 * @param objects
	 * @param fakeLoadForCacheMaint Do not really load or create JME node, only call ModelLoader for cache maintenance.
	 * @return
	 */
	protected Node[] loadObjects(RenderedCube rc, Model[] objects, boolean fakeLoadForCacheMaint)
    {
		
		Node[] r = null;
		r = new Node[objects.length];
		if (objects!=null)
		for (int i=0; i<objects.length; i++) {
			if (objects[i]==null) continue;
			// texture state vegetation with flora setup
			if (objects[i] instanceof TextureStateVegetationModel) 
			{
				if (fakeLoadForCacheMaint) continue;
				Model m = objects[i]; 
				Node node = vegetationTargetCache.get(((TextureStateVegetationModel)m).getKey());
				if (node==null) {
					TextureStateVegetationModel tm = (TextureStateVegetationModel)m;
					TextureState[] ts = loadTextureStates(tm.textureNames);
					node = VegetationSetup.createVegetation(rc, core, core.getCamera(), ts, tm);
					//vegetationTargetCache.put(((TextureStateVegetationModel)m).getKey(), node);
				} else 
				{
					node = new SharedNode("sveg"+((TextureStateVegetationModel)m).getKey(),node);
				}
				r[i] = node;
			
			} else
			// Quad models
			if (objects[i] instanceof QuadModel) {
				if (fakeLoadForCacheMaint) continue;
				Node node = loadQuadModel((QuadModel)objects[i]);
				r[i] = node;

			} else
			if (objects[i] instanceof PartlyBillboardModel) 
			{
				Node node = loadNode((SimpleModel)objects[i],fakeLoadForCacheMaint);
				if (fakeLoadForCacheMaint) continue;
				// adding to drawer
				BillboardPartVegetation bbNode = new BillboardPartVegetation(core,core.getCamera(),15f,(PartlyBillboardModel)objects[i]);
				bbNode.attachChild(node);
				if (core.sPass!=null && objects[i].shadowCaster)
				{
					if (node!=null) {
						core.sPass.addOccluder(node);
					}
				}
				node = bbNode;
				r[i] = node;
				node.setName(((SimpleModel)objects[i]).modelName+i);
			} else
			if (objects[i] instanceof SimpleModel) 
			{
				Node node = loadNode((SimpleModel)objects[i],fakeLoadForCacheMaint);
				if (fakeLoadForCacheMaint) continue;
				
				r[i] = node;
				node.setName(((SimpleModel)objects[i]).modelName+i);
				if (core.sPass!=null && objects[i].shadowCaster)
				{
					if (node!=null) {
						core.sPass.addOccluder(node);
					}
				}
			} else
			// ** LODModel **
			if (objects[i] instanceof LODModel)
			{
				LODModel lm = (LODModel)objects[i];
				
				int c=0; // counter
				DistanceSwitchModel dsm = new DistanceSwitchModel(lm.models.length);
				DiscreteLodNode lodNode = new DiscreteLodNode("dln",dsm);
				for (Model m : lm.models) {
					Node node = null;
					if (m instanceof TextureStateVegetationModel)
					{
						
						if (fakeLoadForCacheMaint) continue;
						node = vegetationTargetCache.get(((TextureStateVegetationModel)m).getKey());
						
						if (node==null) {
							TextureStateVegetationModel tm = (TextureStateVegetationModel)m;
							TextureState[] ts = loadTextureStates(tm.textureNames);
							node = VegetationSetup.createVegetation(rc, core, core.getCamera(), ts, tm);
							//vegetationTargetCache.put(((TextureStateVegetationModel)m).getKey(), node); // TODO need cache?
						} else 
						{
							Node target = node;
							node = new SharedNode("sveg"+((TextureStateVegetationModel)m).getKey(),target);
							//if (target.getUserData(key))
							//node.setUserData("", data)
						}
						
					} else 
					if (m instanceof PartlyBillboardModel) 
					{
						node = loadNode((SimpleModel)m,fakeLoadForCacheMaint);
						if (fakeLoadForCacheMaint) continue;
						// adding to drawer
						BillboardPartVegetation bbNode = new BillboardPartVegetation(core,core.getCamera(),30f,(PartlyBillboardModel)m);
						bbNode.attachChild(node);
						if (core.sPass!=null && m.shadowCaster)
						{
							if (node!=null) {
								core.sPass.addOccluder(node);
							}
						}
						node = bbNode;
						r[i] = node;
						node.setName(((SimpleModel)m).modelName+i);
					} else
					{	
						node = loadNode((SimpleModel)m,fakeLoadForCacheMaint);
						if (fakeLoadForCacheMaint) continue;
						
						if (core.sPass!=null && m.shadowCaster)
						{
							if (node!=null) {
								core.sPass.addOccluder(node);
							}
						}
					}
					
					
					if (m instanceof BillboardModel)
					{

						BillboardNode iNode = new BillboardNode("a");
						iNode.attachChild(node);
						iNode.setAlignment(BillboardNode.SCREEN_ALIGNED);
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
				
				if (fakeLoadForCacheMaint) continue;
				
				r[i] = lodNode;
			}
			
			// if model can be rotated on a steep, we set node user data for later use in renderNodes of J3DCore...
			if (objects[i].rotateOnSteep) 
			{
				r[i].setUserData("rotateOnSteep", r[i]);
			}
			//if (objects[i].shadowCaster)
			//{
				//core.sPass.addOccluder(r[i]);
			//}
		}
		return r;
    }
    
    
    /**
     * Sets mipmap rendering state to a scenelement recoursively.
     * @param sp
     */
    private void setTextures(SceneElement sp, boolean mipmap) {
        if (sp instanceof Node) {
            Node n = (Node) sp;
          
            for (Spatial child : n.getChildren()) {
            	setTextures(child,mipmap);
            }
        } else if (sp instanceof Geometry) {
            Geometry g = (Geometry) sp;
            TextureState ts1 = (TextureState)g.getRenderState(RenderState.RS_TEXTURE);
            
            if (ts1!=null) {
                ts1.setCorrection(TextureState.CM_PERSPECTIVE);
                for (int i=0; i<ts1.getNumberOfSetTextures();i++)
    		    {
    		    	Texture t = ts1.getTexture(i);
    		    	if (mipmap && J3DCore.MIPMAP_GLOBAL) {
    		    		t.setFilter(Texture.FM_LINEAR);
    		    		t.setMipmapState(Texture.MM_LINEAR_LINEAR);
    		    	}
    		    }
            }
            for (int x = 0; x < g.getBatchCount(); x++) {
            	
            	setTextures(g.getBatch(x),mipmap);
            }
        }
    }
    
    /**
     * The keys in these sets wont be removed from the cache after one rendering.
     */
    public HashSet<String> tempNodeKeys = new HashSet<String>();
    public HashSet<String> tempBinaryKeys = new HashSet<String>();
    
    /**
     * Call when starting rendering
     */
    public void startRender()
    {
    	tempNodeKeys.clear();
    	tempBinaryKeys.clear();
    }
    /**
     * Call when one render is complete, this will remove the nodes and binaries not needed any more. 
     */
    public void stopRenderAndClear()
    {
    	HashSet<String> removable = new HashSet<String>();
    	for (String key : sharedNodeCache.keySet()) {
			if (!tempNodeKeys.contains(key))
			{
				removable.add(key);
			} else
			{
			}
		}
    	sharedNodeCache.keySet().removeAll(removable);
    	
    	removable.clear();
    	for (String key : binaryCache.keySet()) {
			if (!tempBinaryKeys.contains(key))
			{
				removable.add(key);
			}
		}
		binaryCache.keySet().removeAll(removable);
		
    	tempNodeKeys.clear();
    	tempBinaryKeys.clear();
    }
    
    public void setLockForSharedNodes(boolean lockState)
    {
    	//for (Node n: sharedNodeCache.values())
    	{
    		if (lockState)
    		{
    			//n.lock();
    		} else
    		{
    			//n.unlock();
    		}
    	}
    }
    
    /**
     * Loads texture states.
     * @param textureNames
     * @return
     */
    public TextureState[] loadTextureStates(String[] textureNames)
    {
    	return loadTextureStates(textureNames, null, false);
    }
    /**
     * Loads an array of texturestates (with normal mapping if needed).
     * @param textureNames Texture file names
     * @param normalNames Normal file names
     * @param transformNormal Normal images needs sobel transformation or not (must be true if image is grayscale bumpmap).
     * @return Texturestates
     */
    public TextureState[] loadTextureStates(String[] textureNames,String[] normalNames, boolean transformNormal)
    {
    	ArrayList<TextureState> tss = new ArrayList<TextureState>();
    	for (int i=0; i<textureNames.length; i++) {
    		String key = textureNames[i]+(normalNames!=null?normalNames[i]:"null");
	    	TextureState ts = textureStateCache.get(key);
	    	if (ts!=null) {tss.add(ts); continue;}
	    	
	    	ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    	if (normalNames!=null && normalNames[i]!=null)
	    	{
	            try {
		            Texture tex = null;
	            	if (transformNormal) {
	            		tex = new Texture();
	            		Image heightImage = TextureManager.loadImage(new File("./data/textures/"+(J3DCore.TEXTURE_QUAL_HIGH?"high/":"low/")+normalNames[i]).toURI().toURL(),true);
	            		Image bumpImage = new SobelImageFilter().apply(heightImage);
			            tex.setImage(bumpImage);
	            	} else
	            	{
			            tex = TextureManager.loadTexture("./data/textures/"+(J3DCore.TEXTURE_QUAL_HIGH?"high/":"low/")+normalNames[i],Texture.MM_LINEAR,
					            Texture.FM_LINEAR);
	            	}
					tex.setWrap(Texture.WM_WRAP_S_WRAP_T);
					tex.setApply(Texture.AM_COMBINE);
					tex.setCombineFuncRGB(Texture.ACF_DOT3_RGB);
					tex.setCombineSrc0RGB(Texture.ACS_TEXTURE);
					tex.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
					ts.setTexture(tex, 0);
	            } catch (Exception ex)
	            {
	            	
	            }
	    	}
	    	
			Texture qtexture = TextureManager.loadTexture("./data/textures/"+(J3DCore.TEXTURE_QUAL_HIGH?"high/":"low/")+textureNames[i],Texture.MM_LINEAR,
		            Texture.FM_LINEAR);
			qtexture.setWrap(Texture.WM_WRAP_S_WRAP_T);
			qtexture.setApply(Texture.AM_MODULATE); // use modulate here!
			if (J3DCore.MIPMAP_GLOBAL)
			{	
				qtexture.setFilter(Texture.FM_LINEAR);
				qtexture.setMipmapState(Texture.MM_LINEAR_LINEAR);
			}
			
			qtexture.setCombineOp0RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
			qtexture.setCombineOp1RGB(Texture.ACO_ONE_MINUS_SRC_COLOR);
			
			if (normalNames!=null && normalNames[i]!=null)
			{
				// TODO texture colors don't get in when using dot3 normal map! what settings here?
				qtexture.setCombineFuncRGB(Texture.ACF_MODULATE);
				qtexture.setCombineSrc0RGB(Texture.ACS_PREVIOUS);
				qtexture.setCombineSrc1RGB(Texture.ACS_TEXTURE); 
				ts.setTexture(qtexture,1);
			} else
			{
				ts.setTexture(qtexture,0);
			}
			ts.setEnabled(true);
			textureStateCache.put(key, ts);
			tss.add(ts);
    	}
    	return (TextureState[])tss.toArray(new TextureState[0]);
    	
    }
    
    public Node loadQuadModel(QuadModel m)
    {
    	
		Quad quad = new Quad("quadModel"+m.textureName,m.sizeX,m.sizeY);
		quad.setModelBound(new BoundingBox());
		quad.updateModelBound();
		TextureState[] ts = loadTextureStates(new String[]{m.textureName}, new String[]{m.dot3TextureName},m.transformToNormal);
		if (m.dot3TextureName!=null) {
		}
		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
		.createMaterialState();
		ms.setColorMaterial(MaterialState.CM_AMBIENT_AND_DIFFUSE);
		quad.setRenderState(ms);
		quad.setLightCombineMode(LightState.COMBINE_CLOSEST);
		
		quad.setRenderState(ts[0]);
		//quad.setRenderState(as);
		quad.setSolidColor(new ColorRGBA(1,1,1,1));
		
		
		Node node = new Node();
		node.attachChild(quad);
		return node;
    	
     	
    }
     
    /**
     * Load one simplemodel to node
     * @param o SimpleModel descriptor
     * @param fakeLoadForCacheMaint If this is true, only cache maintenance is needed, the model is already rendered and live
     * @return
     */
    public Node loadNode(SimpleModel o, boolean fakeLoadForCacheMaint)
    {
		// adding keys to render temp key sets. These wont be removed from the cache after the rendering.
    	tempNodeKeys.add(o.modelName+o.textureName+o.mipMap);
		tempBinaryKeys.add(o.modelName);
		
		// don't have to really load it, return with null
		if (fakeLoadForCacheMaint) return null;

		// the big shared node cache -> mem size lowerer and performance boost
    	if (sharedNodeCache.get(o.modelName+o.textureName+o.mipMap)!=null)
    	{
    		Node n = sharedNodeCache.get(o.modelName+o.textureName+o.mipMap);
    		if (n!=null) {
	    		Node r =  new SharedNode("node"+counter++,n);
	    		r.setModelBound(new BoundingBox());
	            r.updateModelBound();
	            return r;
    		}
    	}
    	
    	if (o.modelName.endsWith(".obj"))
    	{
    		ObjToJme objtojme = new ObjToJme();
			try {
				objtojme.setProperty("mtllib",new File("./data/mtl/").toURI().toURL());
				objtojme.setProperty("texdir",new File("./data/mtl/").toURI().toURL());
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
						objtojme.convert(is, bytearrayoutputstream);
				 
						// 	Used to convert the jme usable file to a TriMesh
						bytes = (bytearrayoutputstream.toByteArray());
						binaryCache.put(o.modelName,bytes);
					    is.close();
					}
					ByteArrayInputStream in = new ByteArrayInputStream(bytes);
					BinaryImporter binaryImporter = new BinaryImporter(); 
				    //importer returns a Loadable, cast to Node
					node = new Node();
					Spatial spatial = (Spatial)binaryImporter.load(in);
					node.attachChild(spatial);
					
					if (as==null) 
					{
						as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
						as.setEnabled(true);
						as.setBlendEnabled(false); // TODO 
						as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
						as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
						as.setReference(0.0f);
						as.setTestEnabled(true);
						as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
					}
					
					//spatial.setRenderState(as);
					
					sharedNodeCache.put(o.modelName+o.textureName+o.mipMap, node);
					node.setModelBound(new BoundingBox());
					node.updateModelBound();
					Node r =  new SharedNode("node"+counter++,node);
		    		r.setModelBound(new BoundingBox());
		            r.updateModelBound();
		            return r;
				} catch(Exception err)  {
				    System.out.println("Error loading model:"+err);
				    err.printStackTrace();
				    return null;
				}
   		
    		
    	} else {
    	
			MaxToJme maxtojme = new MaxToJme();
			try {
				// setting texture directory for 3ds models...
				maxtojme.setProperty(MaxToJme.TEXURL_PROPERTY, new File("./data/textures/"+(J3DCore.TEXTURE_QUAL_HIGH?"high/":"low/")).toURI().toURL());
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
						texture.setRotation(J3DCore.qTexture);
						textureCache.put(o.textureName, texture);
					}
	
					TextureState ts = core.getDisplay().getRenderer().createTextureState();
					ts.setTexture(texture, 0);
					
	                ts.setEnabled(true);
					node.setRenderState(ts);
					
				} else 
				{
					setTextures(node,o.mipMap);
				}
				if (as==null) 
				{
					as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
					as.setEnabled(true);
					as.setBlendEnabled(false); // true TODO
					as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
					as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
					as.setReference(0.0f);
					as.setTestEnabled(true);
					as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
				}
				
				//node.setRenderState(as);

				sharedNodeCache.put(o.modelName+o.textureName+o.mipMap, node);
				node.setModelBound(new BoundingBox());//new Vector3f(0f,0f,0f),2f,2f,2f));
				node.updateModelBound();
				Node r =  new SharedNode("node"+counter++,node);
	    		r.setModelBound(new BoundingBox());
	            r.updateModelBound();
	            return r;
			} catch(Exception err)  {
			    System.out.println("Error loading model:"+err);
			    err.printStackTrace();
			    return null;
			}
    	}
    	
    }
	
}
