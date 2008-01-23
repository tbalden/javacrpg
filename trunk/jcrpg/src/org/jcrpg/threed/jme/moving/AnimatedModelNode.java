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

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.SkinNode;
import com.jme.bounding.BoundingBox;
import com.jme.scene.Node;

public class AnimatedModelNode extends Node {

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
	
	public AnimatedModelNode(String fileName) 
	{
		System.out.println("LOADING ANIMATED MODEL: "+fileName);
		try {
			Model bodyModel = loadModel(fileName);
			
			Animation runningAnimation = loadAnimation("data/models/fauna/gorilla1anim.md5");

			SkeletalModelInstance bodyInstance = new SkeletalModelInstance(bodyModel);
			SkeletalAnimationController bodyAnimationController = (SkeletalAnimationController) bodyInstance.addAnimationController();
	        AnimationAnimator runningAnimator = bodyAnimationController.addAnimation(runningAnimation);

	        bodyInstance.getLocalTranslation().set(0, 0, 0);
	        bodyInstance.updateGeometricState(0, false);
	        bodyInstance.setModelBound(new BoundingBox());
	        bodyInstance.updateModelBound();

	        runningAnimator.fadeIn(.5f);
			
	        attachChild(bodyInstance);
	        
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
    private Model loadModel(String path) throws IOException {
        InputStream in = new FileInputStream(new File(path));

        if (in == null) {
            throw new FileNotFoundException("Cannot find " + path);
        }

        MD5MeshReader reader = new MD5MeshReader();

        reader.setProperty(MD5MeshReader.CLASSLOADER, getClass().getClassLoader());

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


}
