/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
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
import model.animation.Animator;
import model.animation.FixedLengthAnimator;
import model.animation.IAnimationListener;
import model.animation.SkeletalAnimationController;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.ModelPool.PoolItemContainer;
import org.jcrpg.threed.scene.model.TriggeredAnimDescription;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.SkinNode;
import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

/**
 * Partially triggered animation model helper node for md5.
 * @author pali
 *
 */
public class TriggeredModelNode extends Node implements PooledNode, IAnimationListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public AnimationController ac;
	public Node modelNode;
	public Bone bone = null;
	public ArrayList<String> animationNames;
	public HashMap<String,String> animationFileNames = new HashMap<String,String>();
	public HashMap<String,Animation> animations = new HashMap<String,Animation>();
	public AnimationAnimator currentAnimator = null;
	public String currentAnimatorName = null;
	public SkinNode skinNode;

	public static HashMap<String,Animation> animationCache = new HashMap<String,Animation>(); 
	public static HashMap<String,Integer> animationCacheUsage = new HashMap<String,Integer>();
	
	/*public AnimatedModelNode(String fileName, MovingModelAnimDescription animation,  float speed, boolean importer)
	{
	
		try 
		{
			
			MD5Importer.getInstance().load(new File(fileName).toURL(), "_",new File(animation.IDLE).toURL(), "__", com.model.md5.controller.JointController.RT_CYCLE);
			com.model.md5.ModelNode node = MD5Importer.getInstance().getModelNode();
			JointAnimation anim = MD5Importer.getInstance().getAnimation();
			//anim.addAnimation(animation)
			//anim.s
			
			this.attachChild(node);			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}*/
	
	SkeletalModelInstance bodyInstance;
	SkeletalAnimationController bodyAnimationController = null;
	
	public void addAnimation(String name, Animation anim)
	{
		animations.put(name,anim);
	}
	
	
	public boolean isFinishedPlaying()
	{
		if (playAnim!=null)
		{
			if (finishedPlaying)
			{
				//Jcrpg.LOGGER.finest("FINISHED PLAYING.");
				playAnim = null;
				finishedPlaying = false;
				return true;
			}
			return false;
		}
		return true;
	}
	
	
	public AnimationAnimator playAnim = null;
	public boolean finishedPlaying = false;
	
	public float playAnimation(String name)
	{
		return playAnimation(name, null);
	}
	
	/**
	 * Sets skip rate of animation if it's a slow animation.
	 * @param name
	 */
	public void setAnimationSpeed(String name, boolean update)
	{
		if (!J3DCore.SLOW_ANIMATION && animationDesc.oneFrameAnim.get(name)!=null && animationDesc.oneFrameAnim.get(name))
		{
			if (update) bodyAnimationController.update(0.5f);
			bodyAnimationController.setSkipRate(0.5f);
		} else
		{
			if (!J3DCore.SLOW_ANIMATION)
			{
				bodyAnimationController.setSkipRate(0f);
			}
		}
		
	}
	
	public float playAnimation(String name, String afterAnim)
	{
		Animation anim = animations.get(name);
		if (anim==null) {
			finishedPlaying = true;
			return 0;
		}
		if (J3DCore.LOGGING) Jcrpg.LOGGER.fine("P_____ PLAY ANIM : "+name+" AFTERANIME = "+afterAnim);
		
		AnimationAnimator newAnimator = bodyAnimationController.addAnimation(anim);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("P_____ CURRENT ANIM : "+currentAnimatorName);
		
		setAnimationSpeed(name,true);

		if (currentAnimator!=null) {
			if (afterAnim!=null && afterAnim!=currentAnimatorName) {
				currentAnimator.fadeOut(0.1f,true);
				while (animations.get(afterAnim)==null)
				{
					if (afterAnim.equals(TriggeredAnimDescription.ANIM_OPENING))
					{
						afterAnim = TriggeredAnimDescription.ANIM_OPEN;
						continue;
					}
					if (afterAnim.equals(TriggeredAnimDescription.ANIM_CLOSING))
					{
						afterAnim = TriggeredAnimDescription.ANIM_CLOSED;
						continue;
					}
					afterAnim = TriggeredAnimDescription.ANIM_DEFAULT;
					break;
				}
				Animation animAfter = animations.get(afterAnim);
				AnimationAnimator newAnimatorAfter = bodyAnimationController.addAnimation(animAfter);
				currentAnimator = newAnimatorAfter;
				currentAnimatorName = afterAnim;
			} 
			currentAnimator.fadeTo(0.2f, 0.1f);
		}
		newAnimator.setCycleType(FixedLengthAnimator.RT_ONCE);
		newAnimator.fadeIn(0.2f);
		newAnimator.setSpeed(0.8f+(float)(Math.random()/10f)-0.1f);
		//Jcrpg.LOGGER.finest("STARTING PLAY... "+ newAnimator);
		
		playAnim = newAnimator;
		return newAnimator.getMax();
	}

	
	public float changeToAnimation(String name)
	{
		//if (currentAnimator!=null)
		{
			while (animations.get(name)==null)
			{
				if (name.equals(TriggeredAnimDescription.ANIM_OPENING))
				{
					name = TriggeredAnimDescription.ANIM_OPEN;
					continue;
				}
				if (name.equals(TriggeredAnimDescription.ANIM_CLOSING))
				{
					name = TriggeredAnimDescription.ANIM_CLOSED;
					continue;
				}
				name = TriggeredAnimDescription.ANIM_DEFAULT;
				break;
			}
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("_____ CHANGE TO ANIM : "+name);
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("_____ CHANGE TO ANIM CURRENT ANIM: "+currentAnimatorName);
			if (name.equals(currentAnimatorName)) return 0;
			Animation anim = animations.get(name);
			if (currentAnimator!=null && anim==currentAnimator.getAnimation()) return 0;
			if (currentAnimator!=null) {
				currentAnimator.fadeOut(0.1f,true);
			}
			if (anim==null || bodyAnimationController==null) 
			{
				System.out.println("ANIM = 0");
				return 0;
			}
			
			setAnimationSpeed(name,true);
			
			AnimationAnimator newAnimator = bodyAnimationController.addAnimation(anim);
			//newAnimator.setCycleType(FixedLengthAnimator.RT_WRAP);
			newAnimator.fadeIn(0.5f);
			//newAnimator.setWeight(1f);
			newAnimator.setSpeed(0.8f+(float)(Math.random()/10f)-0.1f);
			currentAnimator = newAnimator;
			currentAnimatorName = name;
			System.out.println("ANIM = "+name);
			return newAnimator.getMax();
		}
	}
	
	TriggeredAnimDescription animationDesc = null;
	
	public TriggeredModelNode(String fileName, TriggeredAnimDescription animation, float scale, float[] disposition,float speed)
	{
		this.animationDesc = animation;
		boolean animated = animation!=null;
		if (J3DCore.LOGGING) Jcrpg.LOGGER.fine("AnimatedModelNode: LOADING ANIMATED MODEL: "+fileName);
		
		try {
			Model bodyModel = loadModel(fileName);
			bodyInstance = new SkeletalModelInstance(bodyModel);
			//bodyInstance.lockBounds();
			bodyInstance.lockShadows();
			bodyAnimationController = (SkeletalAnimationController) bodyInstance.addAnimationController();
			if (J3DCore.SLOW_ANIMATION)
				bodyAnimationController.setSkipRate(1f);
			bodyAnimationController.addListener(this);
			

			if (animated) {
				HashMap<String, String> animationNames = animation.getAnimationNames();
				for (String aName:animationNames.keySet())
				{
					String aFile = animationNames.get(aName);
					String presentAnimForFile = animationFileNames.get(aFile);
					if (presentAnimForFile!=null)
					{
						Animation a = animations.get(presentAnimForFile);
						addAnimation(aName, a);
					} else
					{
						Animation anim = loadAnimation(aFile);
						addAnimation(aName, anim);
						animationFileNames.put(aFile, aName);
					}
				}
				
			}

	        bodyInstance.setNormalsMode(NormalsMode.Inherit);
	        bodyInstance.getLocalTranslation().set(0, 0, 0);
	        bodyInstance.setLocalScale(scale);
	        Quaternion q = new Quaternion();
			q.fromAngleNormalAxis(new Vector3f(1,0,0).normalize().angleBetween(new Vector3f(0,0,1).normalize()), new Vector3f(1,0,0).normalize());
			//q.inverseLocal();
	        //bodyInstance.setLocalRotation(q);

        	attachChild(bodyInstance);
	        setModelBound(new BoundingBox());
	        
	        updateRenderState();
	        updateModelBound();
        	if (animated)
        	{
        		changeToAnimation(TriggeredAnimDescription.ANIM_DEFAULT);
        	}

	        
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	public TriggeredModelNode(String fileName, TriggeredAnimDescription animation, float scale,float[] disposition) 
	{
		this(fileName,animation,scale, disposition,1f);		
	}
    /*@SuppressWarnings("unused")
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
    }*/
    
    public static HashMap<String, Model> cache = new HashMap<String, Model>();
	
    private Model loadModel(String path) throws IOException {
    	
    	if (cache.get(path)!=null) return cache.get(path);
    	
        InputStream in = new FileInputStream(new File(path));

        if (in == null) {
            throw new FileNotFoundException("Cannot find " + path);
        }

        MD5MeshReader reader = new MD5MeshReader();

        Model m = reader.readModel(in);
        cache.put(path, m);
        return m;
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

	public void notify(Animator animator, int type, Object userObject) {
		//Jcrpg.LOGGER.finest("notify! "+type+" A: "+animator+ " P: "+playAnim);
		if (type==IAnimationListener.ANIMATION_CYCLE_ENDED)
		{
			//Jcrpg.LOGGER.finest("FINISHED!");
			if (animator == playAnim)
			{
				// finished playing of a solo anim, start the old anim
				//Jcrpg.LOGGER.finest("!PLAYING FINISHED!");
				finishedPlaying = true;
				//currentAnimator.setTime(currentAnimator.getMin());
				currentAnimator.fadeIn(1f);
				setAnimationSpeed(currentAnimatorName,false);
				animator.fadeOut(0.6f, true);
			}
		}
	}


	public void update(NodePlaceholder place) {
		// TODO Auto-generated method stub
		
	}


}