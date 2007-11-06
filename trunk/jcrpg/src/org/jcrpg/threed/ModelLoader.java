/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import java.util.Iterator;

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
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.ObjToJme;

public class ModelLoader {

	J3DCore core = null;
	
	String TEXDIR = "low/"; 
	
	public ModelLoader(J3DCore core)
	{
		this.core = core;
		
	    try {
    		TEXDIR = J3DCore.TEXTURE_QUALITY==0?"low/":(J3DCore.TEXTURE_QUALITY==1?"mid/":"high/");
	    	SimpleResourceLocator loc1 = new SimpleResourceLocator( new File("./data/textures/"+TEXDIR).toURI());
	    	SimpleResourceLocator loc2 = new SimpleResourceLocator( new File("./data/orbiters/").toURI());
	    	SimpleResourceLocator loc3 = new SimpleResourceLocator( new File("./data/flare/").toURI());
	        ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, loc1);
	        ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, loc2);
	        ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, loc3);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
    
	HashMap<String,Texture> textureCache = new HashMap<String,Texture>();
    HashMap<String,byte[]> binaryCache = new HashMap<String,byte[]>();
    // this better be not weak hashmap
    HashMap<String,Node> sharedNodeCache = new HashMap<String, Node>();
    HashMap<String,TextureState> textureStateCache = new HashMap<String,TextureState>();
    HashMap<String,BillboardPartVegetation> sharedBBNodeCache = new HashMap<String, BillboardPartVegetation>();
    
    int counter=0;
    
    //AlphaState as;
    AlphaState as_off;

    HashMap<String, Node> vegetationTargetCache = new HashMap<String, Node>();

    
	/**
	 * Loads a pooled node for a model (rotated or not if needed or not special rotation for billboarding).
	 * @param rc
	 * @param object
	 * @param horRotated
	 * @return
	 */
    protected PooledNode loadObject(RenderedCube rc, Model object, boolean horRotated)
    {
		return loadObjects(rc, new Model[]{object}, horRotated, false)[0];
    }    
	/**
	 * Loading a set of models into JME nodes.
	 * @param objects
	 * @param fakeLoadForCacheMaint Do not really load or create JME node, only call ModelLoader for cache maintenance.
	 * @return
	 */
	protected PooledNode[] loadObjects(RenderedCube rc, Model[] objects, boolean horRotated, boolean fakeLoadForCacheMaint)
    {
		
		//GeometryBatchCreator cr = new GeometryBatchCreator();
		//cr.addInstance(new GeometryBatchInstance());
		
		
		PooledNode[] r = null;
		if (!fakeLoadForCacheMaint) r = new PooledNode[objects.length];
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
					//node = VegetationSetupOld.createVegetation(rc, core, core.getCamera(), ts, tm);
					//vegetationTargetCache.put(((TextureStateVegetationModel)m).getKey(), node);
				} else 
				{
					node = new SharedNode("sveg"+((TextureStateVegetationModel)m).getKey(),node);
				}
				
			} else
			// Quad models
			if (objects[i] instanceof QuadModel) {
				PooledSharedNode node = loadQuadModel((QuadModel)objects[i],fakeLoadForCacheMaint);				
				if (fakeLoadForCacheMaint) continue;
				r[i] = node;

			} else
			if (objects[i] instanceof PartlyBillboardModel) 
			{
				SimpleModel o = (SimpleModel)objects[i];
				String key = o.modelName+o.textureName+o.mipMap;
				BillboardPartVegetation bbOrig = sharedBBNodeCache.get(key);
				Node node = null;
				if (bbOrig==null) {
					node = loadNode((SimpleModel)objects[i],fakeLoadForCacheMaint);
					//node = loadNodeOriginal((SimpleModel)objects[i],fakeLoadForCacheMaint,true);
					if (fakeLoadForCacheMaint) continue;
					bbOrig = new BillboardPartVegetation(core,core.getCamera(),core.treeLodDist[3][1],(PartlyBillboardModel)objects[i],horRotated);
					//sharedBBNodeCache.put(key, bbOrig);
					bbOrig.attachChild(node);
					if (J3DCore.FARVIEW_ENABLED)
						bbOrig.setRenderState(core.fs_external_special);
				}
				if (fakeLoadForCacheMaint) continue;
				// adding to drawer
				//PooledSharedNode sn = new PooledSharedNode("!-",bbOrig);
		    	//Node node = sn;
				bbOrig.setName(((SimpleModel)objects[i]).modelName+i);
				//r[i] = sn;//bbOrig;// new PooledSharedNode("1",node);// bbOrig;
				r[i] = bbOrig;
			} else
			if (objects[i] instanceof SimpleModel) 
			{
				PooledSharedNode node = loadNode((SimpleModel)objects[i],fakeLoadForCacheMaint);
				if (fakeLoadForCacheMaint) continue;
				
		    	//PooledSharedNode psnode = new PooledSharedNode("s"+node.getName(),node);
				r[i] = node;
				node.setName(((SimpleModel)objects[i]).modelName+i);
			} else
			// ** LODModel **
			if (objects[i] instanceof LODModel)
			{
				LODModel lm = (LODModel)objects[i];
				
				int c=0; // counter
				DistanceSwitchModel dsm = new DistanceSwitchModel(lm.models.length);
				PooledDiscreteLodNode lodNode = new PooledDiscreteLodNode("dln",dsm);
				for (Model m : lm.models) {
					Node node = null;
					if (m instanceof TextureStateVegetationModel)
					{
						
						if (fakeLoadForCacheMaint) continue;
						node = vegetationTargetCache.get(((TextureStateVegetationModel)m).getKey());
						
						if (node==null) {
							TextureStateVegetationModel tm = (TextureStateVegetationModel)m;
							TextureState[] ts = loadTextureStates(tm.textureNames);
							//node = VegetationSetupOld.createVegetation(rc, core, core.getCamera(), ts, tm);
							node = VegetationSetup.createVegetation(rc, core, core.getCamera(), ts, tm);
							//vegetationTargetCache.put(((TextureStateVegetationModel)m).getKey(), node); // TODO need cache?
						} else 
						{
							Node target = node;
							node = new SharedNode("sveg"+((TextureStateVegetationModel)m).getKey(),target);
						}
						
					} else 
					if (m instanceof PartlyBillboardModel) 
					{
						node = loadNode((SimpleModel)m,fakeLoadForCacheMaint);
						if (fakeLoadForCacheMaint) continue;
						// adding to drawer
						BillboardPartVegetation bbNode = new BillboardPartVegetation(core,core.getCamera(),core.treeLodDist[3][1],(PartlyBillboardModel)m,horRotated);
						bbNode.attachChild(node);
				    	node = bbNode;
				    	node.setName(((SimpleModel)m).modelName+i);
						//r[i] = bbNode;
						
					} else
					if (m instanceof QuadModel) {
						node = loadQuadModel((QuadModel)m,fakeLoadForCacheMaint);				
						if (fakeLoadForCacheMaint) continue;
						//r[i] = node;

					} else
					{	
						node = loadNode((SimpleModel)m,fakeLoadForCacheMaint);
						if (fakeLoadForCacheMaint) continue;
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
					if (fakeLoadForCacheMaint) continue;
					lodNode.attachChildAt(node,c);
					dsm.setModelDistance(c, lm.distances[c][0], lm.distances[c][1]);
					c++;
				}
				
				if (fakeLoadForCacheMaint) continue;
				r[i] = lodNode;
			}
			
		}
		if (!fakeLoadForCacheMaint)
			return r;
		else 
			return null;
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
            //g.clearRenderState(RenderState.RS_TEXTURE);
            TextureState ts1 = (TextureState)g.getRenderState(RenderState.RS_TEXTURE);
            //g.clearRenderState(RenderState.RS_MATERIAL);
            //g.clearRenderState(RenderState.RS_DITHER);
            //g.clearRenderState(RenderState.RS_SHADE);
            //g.clearRenderState(RenderState.RS_STENCIL);
            //g.clearRenderState(RenderState.RS_ATTRIBUTE);
            //g.clearRenderState(RenderState.RS_CLIP);
            //g.clearRenderState(RenderState.RS_ZBUFFER);
            
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
    
    private boolean lockedShared = false;
    public void setLockForSharedNodes(boolean lockState)
    {
    	for (Node n: sharedNodeCache.values())
    	{
    		if (lockState && !lockedShared)
    		{
    			n.lockMeshes();
    			n.lockBounds();
    			n.lockBranch();
    			lockedShared = true;
    		} else
    		{
    			//lockedShared = false;
    			//n.unlockMeshes();
    			//n.unlockBounds();
    			//n.unlockBranch();
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
	    	System.out.println("ModelLoader.loadTextureStates - New Texture "+textureNames[i]);
	    	
	    	ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    	if (normalNames!=null && normalNames[i]!=null)
	    	{
	            try {
		            Texture tex = null;
	            	if (transformNormal) {
	            		tex = new Texture();
	            		Image heightImage = TextureManager.loadImage(new File("./data/textures/"+TEXDIR+normalNames[i]).toURI().toURL(),true);
	            		Image bumpImage = new SobelImageFilter().apply(heightImage);
			            tex.setImage(bumpImage);
	            	} else
	            	{
			            tex = TextureManager.loadTexture("./data/textures/"+TEXDIR+normalNames[i],Texture.MM_LINEAR,
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
	    	
			Texture qtexture = TextureManager.loadTexture("./data/textures/"+TEXDIR+textureNames[i],Texture.MM_LINEAR,
		            Texture.FM_LINEAR);
			//qtexture.setWrap(Texture.WM_WRAP_S_WRAP_T); // do not use this here, or add switch for it, grass is weird if set!
			qtexture.setApply(Texture.AM_MODULATE); // use modulate here!
			if (J3DCore.MIPMAP_GLOBAL)
			{	
				qtexture.setFilter(Texture.FM_LINEAR);
				qtexture.setMipmapState(Texture.MM_LINEAR_LINEAR);
			}
			
			
			if (normalNames!=null && normalNames[i]!=null)
			{
				// TODO texture colors don't get in when using dot3 normal map! what settings here?
				qtexture.setCombineFuncRGB(Texture.ACF_MODULATE);
				qtexture.setCombineSrc0RGB(Texture.ACS_PREVIOUS);
				qtexture.setCombineSrc1RGB(Texture.ACS_TEXTURE); 
				qtexture.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
				qtexture.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
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
		
   
    public PooledSharedNode loadQuadModel(QuadModel m, boolean fake)
    {
    	String key = m.textureName+m.dot3TextureName+m.waterQuad;
		// adding keys to render temp key sets. These wont be removed from the cache after the rendering.
    	tempNodeKeys.add(key);
		tempBinaryKeys.add(key);
		if (fake) return null;
		
		Node node = sharedNodeCache.get(key);
		if (node!=null) {
			PooledSharedNode r =  new PooledSharedNode("node"+counter++,node);
			return r;
		}
    	
		//Box quad = new Box("quadModel"+m.textureName,new Vector3f(0,0,0),m.sizeX/2f,m.sizeY/2f,0.02f);
		
		Quad quad = new Quad("quadModel"+m.textureName,m.sizeX,m.sizeY);
		quad.setModelBound(new BoundingBox());
		quad.updateModelBound();

		 
		{
			TextureState[] ts = loadTextureStates(new String[]{m.textureName}, new String[]{m.dot3TextureName},m.transformToNormal);
			if (m.dot3TextureName!=null) {
			}
			MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
			.createMaterialState();
			ms.setColorMaterial(MaterialState.CM_AMBIENT_AND_DIFFUSE);
			//ms.setAmbient(new ColorRGBA(0.0f,0.0f,0.0f,0.5f));
			quad.setRenderState(ms);
			quad.setLightCombineMode(LightState.COMBINE_FIRST);
			
			quad.setRenderState(ts[0]);
			quad.setSolidColor(new ColorRGBA(1,1,1,1));
			quad.setRenderState(core.cs_none);
			if (as_off==null) 
			{
				as_off = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
				as_off.setEnabled(false);
			}
			quad.setRenderState(as_off);
		}
		if (m.waterQuad)
		{
			J3DCore.waterEffectRenderPass.setWaterEffectOnSpatial(quad);
		}
		
		
		Node nnode = new Node();
		nnode.attachChild(quad);
		
		sharedNodeCache.put(key, nnode);
		PooledSharedNode r = new PooledSharedNode("node"+counter++,nnode);
		return r;
     	
    }

    public Node loadQuadModelNode(QuadModel m, boolean fake)
    {
		// adding keys to render temp key sets. These wont be removed from the cache after the rendering.
    	tempNodeKeys.add(m.textureName+m.dot3TextureName+m.waterQuad);
		tempBinaryKeys.add(m.textureName+m.dot3TextureName+m.waterQuad);
		if (fake) return null;
		
		Node node = sharedNodeCache.get(m.textureName+m.dot3TextureName+m.waterQuad);
		if (node!=null) {
			return node;
		}
    	
		//Box quad = new Box("quadModel"+m.textureName,new Vector3f(0,0,0),m.sizeX/2f,m.sizeY/2f,0.02f);
		
		Quad quad = new Quad("quadModel"+m.textureName+m.waterQuad,m.sizeX,m.sizeY);
		quad.setModelBound(new BoundingBox());
		quad.updateModelBound();
		
		{
		
			TextureState[] ts = loadTextureStates(new String[]{m.textureName}, new String[]{m.dot3TextureName},m.transformToNormal);
			if (m.dot3TextureName!=null) {
			}
			MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
			.createMaterialState();
			ms.setColorMaterial(MaterialState.CM_AMBIENT_AND_DIFFUSE);
			//ms.setAmbient(new ColorRGBA(0.0f,0.0f,0.0f,0.5f));
			quad.setRenderState(ms);
			quad.setLightCombineMode(LightState.COMBINE_FIRST);
			
			quad.setRenderState(ts[0]);
			quad.setSolidColor(new ColorRGBA(1,1,1,1));
			quad.setRenderState(core.cs_none);
			if (as_off==null) 
			{
				as_off = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
				as_off.setEnabled(false);
			}
			quad.setRenderState(as_off);
		}
		Node nnode = new Node();
		nnode.attachChild(quad);
		sharedNodeCache.put(m.textureName+m.dot3TextureName+m.waterQuad, nnode);
		return nnode;
     	
    }

    
    private Node getClodNodeFromParent(Node meshParent) {
		// Create a node to hold my cLOD mesh objects
		Node clodNode = new Node("Clod node");
		// For each mesh in maggie
		for (int i = 0; i < meshParent.getQuantity(); i++) {
			// Create an AreaClodMesh for that mesh. Let it compute records
			// automatically
			if (meshParent.getChild(i) instanceof TriMesh) {
				try {
					AreaClodMesh acm = new AreaClodMesh("part" + i,
							(TriMesh) meshParent.getChild(i), null);
					acm.setModelBound(new BoundingBox());
					acm.updateModelBound();
					// Allow 1/2 of a triangle in every pixel on the screen in the
					// bounds.
					acm.setTrisPerPixel(.5f);
					// Force a move of 2 units before updating the mesh geometry
					acm.setDistanceTolerance(2);
					// Give the clodMesh node the material state that the original
					// had.
					acm.setRenderState(meshParent.getChild(i).getRenderState(
							RenderState.RS_MATERIAL));
					acm.setRenderState(meshParent.getChild(i).getRenderState(
							RenderState.RS_TEXTURE));
					// Attach clod node.
					clodNode.attachChild(acm);
				} catch (Exception ex)
				{
					
				}
			}
		}
		return clodNode;
	}     

    public PooledSharedNode loadNode(SimpleModel o, boolean fakeLoadForCacheMaint)
    {
    	Node n = loadNodeOriginal(o, fakeLoadForCacheMaint);
		PooledSharedNode r =  new PooledSharedNode("node"+counter++,n);
        return r;
    }

    public Node loadNodeOriginal(SimpleModel o, boolean fakeLoadForCacheMaint)
    {
    	return loadNodeOriginal(o, fakeLoadForCacheMaint,false);
    }    
    /**
     * Load one simplemodel to node
     * @param o SimpleModel descriptor
     * @param fakeLoadForCacheMaint If this is true, only cache maintenance is needed, the model is already rendered and live
     * @return
     */
    public Node loadNodeOriginal(SimpleModel o, boolean fakeLoadForCacheMaint, boolean reload)
    {
		String key = o.modelName+o.textureName+o.mipMap;
		
    	//System.out.println("CACHE SIZE: NODE "+sharedNodeCache.size()+" - BIN "+binaryCache.size()+" - TEXST "+ textureStateCache.size()+" - TEX "+textureCache.size());
		// adding keys to render temp key sets. These wont be removed from the cache after the rendering.
		
		tempNodeKeys.add(key);
		tempBinaryKeys.add(o.modelName);
		
		// don't have to really load it, return with null
		if (fakeLoadForCacheMaint) return null;
		

		// debugging:
		//sharedNodeCache.clear();
		
		// the big shared node cache -> mem size lowerer and performance boost
    	if (sharedNodeCache.get(key)!=null)
    	{
    		Node n = sharedNodeCache.get(key);
    		if (n!=null&&!reload) {
    			return n;
    		}
    	}
    	System.out.println("ModelLoader.loadNode - New model: "+o.modelName);
    	
    	if (o.modelName.endsWith(".obj"))
    	{
    		String path = o.modelName.substring(0,o.modelName.lastIndexOf('/'));
    		ObjToJme objtojme = new ObjToJme();
			try {
				objtojme.setProperty("mtllib",new File("./data/"+path).toURI().toURL());
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
					spatial.setModelBound(new BoundingBox());
					spatial.updateModelBound();
					node.attachChild(spatial);
					
					if (o.textureName!=null)
					{
						Texture texture = (Texture)textureCache.get(o.textureName);
						
						if (texture==null) {
							texture = TextureManager.loadTexture("./data/textures/"+TEXDIR+o.textureName,Texture.MM_LINEAR,
				                    Texture.FM_LINEAR);
			
							texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
							texture.setApply(Texture.AM_MODULATE);
							texture.setRotation(J3DCore.qTexture);
							textureCache.put(o.textureName, texture);
						}
						/*MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
						.createMaterialState();
						ms.setColorMaterial(MaterialState.CM_AMBIENT_AND_DIFFUSE);
						//ms.setAmbient(new ColorRGBA(0.0f,0.0f,0.0f,0.5f));
						spatial.setRenderState(ms);
						spatial.setLightCombineMode(LightState.COMBINE_FIRST);*/
						
		
						TextureState ts = core.getDisplay().getRenderer().createTextureState();
						ts.setTexture(texture, 0);
						
		                ts.setEnabled(true);
		                spatial.setRenderState(ts);
						
					} else 
					{
						System.out.println(o.modelName);
						setTextures(node,o.mipMap);
					}
					
				    if (o.cullNone)
				    {
				    	node.setRenderState(core.cs_none);
				    	
				    }
					
					//spatial.setRenderState(as);
					
					sharedNodeCache.put(key, node);
					node.setModelBound(new BoundingBox());
					node.updateModelBound();		
		            //r.setRenderState(core.vp);
		            //r.setRenderState(core.fp);
		            return node;
				} catch(Exception err)  {
				    System.out.println("Error loading model:"+err);
				    err.printStackTrace();
				    return null;
				}
   		
    		
    	} else {

			MaxToJme maxtojme = new MaxToJme();
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
			    if (o.useClodMesh) {
			    	// use clod mesh for the node, part it into clod meshes...
			    	Node node2 = getClodNodeFromParent(node);
				    for (Spatial child:node.getChildren())
				    {
				    	System.out.println("child type = "+child.getType());
				    	if (child instanceof Node)
				    	{
				    		node2.attachChild(getClodNodeFromParent((Node)child));
				    	}
				    }
				    node = node2;
			    }
			    if (o.cullNone)
			    {
			    	node.setRenderState(core.cs_none);
			    	
			    }
			    
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
					System.out.println(o.modelName);
					setTextures(node,o.mipMap);
				}
				
				//node.setRenderState(as);

				sharedNodeCache.put(key, node);
				node.setModelBound(new BoundingBox());
				node.updateModelBound();		
	            return node;
			} catch(Exception err)  {
			    System.out.println("Error loading model:"+err);
			    err.printStackTrace();
			    return null;
			}
    	}
    	
    }
    
	public static void cleanTexture(Spatial s) {
		TextureState ts = (TextureState) s.getRenderState(RenderState.RS_TEXTURE);
		if (ts != null)
			ts.deleteAll(true);
		if (s instanceof Node) {
			ArrayList<Spatial> children = ((Node) s).getChildren();
			if (children != null) {
				Iterator i = children.iterator();
				while (i.hasNext()) {
					cleanTexture((Spatial) i.next());
				}
			}
		}
		ts = null;
	}

}
