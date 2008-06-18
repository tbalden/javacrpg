/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.threed.jme.moving;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import md5reader.MD5AnimReader;
import md5reader.MD5MeshReader;
import model.Model;
import model.SkeletalModelInstance;
import model.animation.Animation;
import model.animation.AnimationAnimator;
import model.animation.SkeletalAnimationController;

import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.ModelPool.PoolItemContainer;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.SkinNode;
import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;

/**
 * Animated model helper node for md5.
 * @author pali
 *
 */
public class AnimatedModelNode extends Node implements PooledNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public AnimationController ac;
	public Node modelNode;
	public Bone bone = null;
	public ArrayList<String> animationNames;
	public HashMap<String,String> animationFileNames = new HashMap<String,String>();
	public HashMap<String,AnimationAnimator> animations = new HashMap<String,AnimationAnimator>();
	public AnimationAnimator defaultAnimator = null; 
	public AnimationAnimator currentAnimator = null;
	public SkinNode skinNode;

	public static HashMap<String,Animation> animationCache = new HashMap<String,Animation>(); 
	public static HashMap<String,Integer> animationCacheUsage = new HashMap<String,Integer>();
	
	/*public AnimatedModelNode(String fileName, String animation, float speed, boolean importer)
	{
	
		try 
		{
			
			MD5Importer.getInstance().load(new File(fileName).toURL(), fileName, new File(animation).toURL(), animation, JointController.RT_CYCLE);
			ModelNode node = MD5Importer.getInstance().getModelNode();
			JointAnimation anim = MD5Importer.getInstance().getAnimation();
			this.attachChild(node);			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}*/
	
	SkeletalModelInstance bodyInstance;
	SkeletalAnimationController bodyAnimationController = null;
	
	public void addAnimationAnimator(String name, Animation anim)
	{
		animations.put(name,bodyAnimationController.addAnimation(anim));
	}
	
	public void addAnimationAnimator(String name, AnimationAnimator anim)
	{
		animations.put(name,anim);
	}
	
	
	public void changeToAnimation(String name)
	{
		if (currentAnimator!=null)
		{
			if (animations.get(name)==null)
				name = MovingModelAnimDescription.ANIM_IDLE;
			AnimationAnimator newAnimator = animations.get(name);
			if (newAnimator==currentAnimator) return;
			currentAnimator.fadeOut(0.5f, true);
			newAnimator.fadeIn(0.5f);
			currentAnimator = newAnimator;
		}
	}
	
	MovingModelAnimDescription animationDesc = null;
	
	public AnimatedModelNode(String fileName, MovingModelAnimDescription animation, float[] disposition,float speed)
	{
		this.animationDesc = animation;
		boolean animated = animation!=null;
		System.out.println("LOADING ANIMATED MODEL: "+fileName);
		try {
			Model bodyModel = loadModel(fileName);
			bodyInstance = new SkeletalModelInstance(bodyModel);
			bodyAnimationController = (SkeletalAnimationController) bodyInstance.addAnimationController();

			if (animated) {
				HashMap<String, String> animationNames = animation.getAnimationNames();
				for (String aName:animationNames.keySet())
				{
					String aFile = animationNames.get(aName);
					String presentAnimForFile = animationFileNames.get(aFile);
					if (presentAnimForFile!=null)
					{
						AnimationAnimator a = animations.get(presentAnimForFile);
						addAnimationAnimator(aName, a);
					} else
					{
						Animation anim = loadAnimation(aFile);
						addAnimationAnimator(aName, anim);
						animationFileNames.put(aFile, aName);
					}
				}
				defaultAnimator = animations.get(MovingModelAnimDescription.ANIM_IDLE);
			}

	        bodyInstance.setNormalsMode(SceneElement.NM_INHERIT);
	        bodyInstance.getLocalTranslation().set(0, 0, 0);
	        bodyInstance.setLocalScale(0.2f);
	        Quaternion q = new Quaternion();
			q.fromAngleNormalAxis(new Vector3f(1,0,0).normalize().angleBetween(new Vector3f(0,0,1).normalize()), new Vector3f(1,0,0).normalize());
			q.inverseLocal();
	        bodyInstance.setLocalRotation(q);

        	if (animated)
        	{
        		defaultAnimator.fadeIn(.5f);
        		defaultAnimator.setSpeed(speed==10f?0.2f+(float)(.15f*Math.random()):speed);
        		currentAnimator = defaultAnimator;
        	}

        	attachChild(bodyInstance);
	        setModelBound(new BoundingBox());
	        updateModelBound();
	        
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	public AnimatedModelNode(String fileName, MovingModelAnimDescription animation, float[] disposition) 
	{
		this(fileName,animation,disposition,1f);		
	}
    @SuppressWarnings("unused")
	private void stripTexturesAndMaterials(SceneElement sp) {
        sp.clearRenderState(RenderState.RS_TEXTURE);
        sp.clearRenderState(RenderState.RS_MATERIAL);
        for (int i=0; i<RenderState.RS_MAX_STATE; i++)
        {
        	sp.clearRenderState(i);
        }
        if (sp instanceof Node) {
            Node n = (Node) sp;
            for (Spatial child : n.getChildren()) {
                stripTexturesAndMaterials(child);
            }
        } else if (sp instanceof Geometry) {
            Geometry g = (Geometry) sp;
            //g.setNormalsMode(SceneElement.NM_GL_NORMALIZE_PROVIDED);
            for (int x = 0; x < g.getBatchCount(); x++) {
                stripTexturesAndMaterials(g.getBatch(x));
            }
        }
    }
	
    private Model loadModel(String path) throws IOException {
        InputStream in = new FileInputStream(new File(path));

        if (in == null) {
            throw new FileNotFoundException("Cannot find " + path);
        }

        MD5MeshReader reader = new MD5MeshReader();

        //reader.setProperty(MD5MeshReader.CLASSLOADER, getClass().getClassLoader());

        return reader.readModel(in);
    }

    
    private void increaseAnimationUsage(String path)
    {
    	if (animationCacheUsage.get(path)==null) {animationCacheUsage.put(path, 1);return;}
    	animationCacheUsage.put(path,animationCacheUsage.get(path)+1);
    	return;
    }
    private Animation loadAnimation(String path) throws IOException {
    	
    	increaseAnimationUsage(path);
    	
    	if (animationCache.get(path)!=null) return animationCache.get(path);
    	
    	InputStream in = new FileInputStream(new File(path));

        if (in == null) {
            throw new FileNotFoundException("Cannot find " + path);
        }

        MD5AnimReader animReader = new MD5AnimReader();

        Animation animation = animReader.readAnimation(in);
        animationCache.put(path, animation);
        return animation;
    }

    PoolItemContainer cont = null;
	public PoolItemContainer getPooledContainer() {
		return cont;
	}

	public void setPooledContainer(PoolItemContainer cont) {
		this.cont = cont;
	}


}
