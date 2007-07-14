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
import java.util.WeakHashMap;

import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
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
    WeakHashMap<String,Node> sharedNodeCache = new WeakHashMap<String, Node>();
    
    int counter=0;
    
    AlphaState as = null;

    
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
    		    	if (mipmap) {
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
     
    public Node loadNode(SimpleModel o)
    {
    	// the big shared node cache -> mem size lowerer and performance boost
    	if (sharedNodeCache.get(o.modelName+o.textureName)!=null)
    	{
    		Node n = sharedNodeCache.get(o.modelName+o.textureName+o.mipMap);
    		Node r =  new SharedNode("node"+counter++,n);
    		r.setModelBound(new BoundingBox());
            r.updateModelBound();
            return r;
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
						as.setBlendEnabled(true);
						as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
						as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
						as.setReference(0.0f);
						as.setTestEnabled(true);
						as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
					}
					// TODO alphastate type of model
					spatial.setRenderState(as);
					
					sharedNodeCache.put(o.modelName+o.textureName+o.mipMap, node);
					node.setModelBound(new BoundingBox());
					node.updateModelBound();
					return node;
				} catch(Exception err)  {
				    System.out.println("Error loading model:"+err);
				    err.printStackTrace();
				    return null;
				}
   		
    		
    	} else {
    	
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
						texture.setRotation(J3DCore.qTexture);
						textureCache.put(o.textureName, texture);
					}
	
					TextureState ts = core.getDisplay().getRenderer().createTextureState();
					ts.setTexture(texture, 0);
					//System.out.println("Texture!");
					
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
					as.setBlendEnabled(true);
					as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
					as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
					as.setReference(0.0f);
					as.setTestEnabled(true);
					as.setTestFunction(AlphaState.TF_GREATER);//GREATER is good only
				}
				// TODO alphastate type of model
				node.setRenderState(as);

				sharedNodeCache.put(o.modelName+o.textureName+o.mipMap, node);
				node.setModelBound(new BoundingBox());//new Vector3f(0f,0f,0f),2f,2f,2f));
				node.updateModelBound();
				return node;
			} catch(Exception err)  {
			    System.out.println("Error loading model:"+err);
			    err.printStackTrace();
			    return null;
			}
    	}
    	
    }
	
}
