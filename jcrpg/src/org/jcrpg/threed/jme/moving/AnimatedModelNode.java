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
import java.io.InputStream;
import java.util.ArrayList;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.SkinNode;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jmex.model.collada.ColladaImporter;

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
		try {
			InputStream stream = new FileInputStream(new File(fileName));
			
		    ColladaImporter.load(stream, "model");
		    for (int i=0; i<ColladaImporter.getSkinNodeNames().size(); i++)
		    {
		    	System.out.println("SKIN NODE NAME:"+ColladaImporter.getSkinNodeNames().get(i));
		    }
		    skinNode = ColladaImporter.getSkinNode(ColladaImporter.getSkinNodeNames()
	                .get(0));
		    
		    //modelNode = ColladaImporter.getModel();
		    //Geometry g = ColladaImporter.getGeometry(ColladaImporter.getGeometryNames().get(0));
		    //modelNode.attachChild(g);
		    for (int i=0; i<ColladaImporter.getSkeletonNames().size(); i++)
		    {
		    	System.out.println("SKELETON NODE NAME:"+ColladaImporter.getSkeletonNames().get(i));
		    }
	        bone = ColladaImporter.getSkeleton(ColladaImporter
	                .getSkeletonNames().get(0));
		    
	        animationNames = ColladaImporter.getControllerNames();
	        
	        if (animationNames!=null) {
		        System.out.println("Number of animations: " + animationNames.size());
	
		        // set up a new animation controller with our BoneAnimation
		        AnimationController ac = new AnimationController();
		        ac.setRepeatType(Controller.RT_CYCLE);
		        ac.setActive(true);
	
		        for (int i = 0; i < animationNames.size(); i++) {
		            System.out.println(animationNames.get(i));
			        BoneAnimation anim = ColladaImporter.getAnimationController(animationNames
			                .get(i));
			        ac.addAnimation(anim);
			        animations.add(anim);
		        }
	
		        if (animations.size()>0)
		        	ac.setActiveAnimation(0);
		        
	
		        // assign the animation controller to our skeleton
		        bone.addController(ac);
	        }
	        //attachChild(modelNode);
	        attachChild(skinNode);
	        Box box = new Box("a",new Vector3f(0,0,0),1,1,1);
	        //attachChild(box);
	        //attachChild(bone);

	        ColladaImporter.cleanUp();


		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
}
