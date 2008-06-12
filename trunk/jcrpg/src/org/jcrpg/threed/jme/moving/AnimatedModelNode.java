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

import md5reader.MD5AnimReader;
import md5reader.MD5MeshReader;
import model.Model;
import model.SkeletalModelInstance;
import model.animation.Animation;
import model.animation.AnimationAnimator;
import model.animation.SkeletalAnimationController;

import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.ModelPool.PoolItemContainer;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.SkinNode;
import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.model.md5.JointAnimation;
import com.model.md5.ModelNode;
import com.model.md5.controller.JointController;
import com.model.md5.importer.MD5Importer;

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
	public ArrayList<BoneAnimation> animations = new ArrayList<BoneAnimation>();
	public SkinNode skinNode;

	
	public AnimatedModelNode(String fileName, String animation, float speed, boolean importer)
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
		
	}
	public AnimatedModelNode(String fileName, String animation, float speed)
	{
		System.out.println("LOADING ANIMATED MODEL: "+fileName);
		try {
			Model bodyModel = loadModel(fileName);
			
			Animation runningAnimation = loadAnimation(animation);

			SkeletalModelInstance bodyInstance = new SkeletalModelInstance(bodyModel);
			boolean animated = false;
			AnimationAnimator runningAnimator = null;
			SkeletalAnimationController bodyAnimationController = null;
			try {
				bodyAnimationController = (SkeletalAnimationController) bodyInstance.addAnimationController();
				runningAnimator = bodyAnimationController.addAnimation(runningAnimation);
				animated = true;
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
	        
	        bodyInstance.setNormalsMode(SceneElement.NM_INHERIT);
	        bodyInstance.getLocalTranslation().set(0, 0, 0);
	        bodyInstance.setLocalScale(0.2f);
	        Quaternion q = new Quaternion();
			q.fromAngleNormalAxis(new Vector3f(1,0,0).normalize().angleBetween(new Vector3f(0,0,1).normalize()), new Vector3f(1,0,0).normalize());
			q.inverseLocal();
	        bodyInstance.setLocalRotation(q);
	        
	        if (animated) {
	        	runningAnimator.fadeIn(.5f);
	        	runningAnimator.setSpeed(speed==10f?0.2f+(float)(.15f*Math.random()):speed);
	        	//runningAnimator.fadeOut(.5f, false);
	        	//bodyAnimationController.setActive(false);
	        }
	        attachChild(bodyInstance);
	        setModelBound(new BoundingBox());
	        updateModelBound();
	        
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	public AnimatedModelNode(String fileName, String animation) 
	{
		this(fileName,animation,10f);		
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

    private Animation loadAnimation(String path) throws IOException {
    	InputStream in = new FileInputStream(new File(path));

        if (in == null) {
            throw new FileNotFoundException("Cannot find " + path);
        }

        MD5AnimReader animReader = new MD5AnimReader();

        Animation animation = animReader.readAnimation(in);

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
