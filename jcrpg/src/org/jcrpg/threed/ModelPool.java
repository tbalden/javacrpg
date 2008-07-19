/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.LODModel;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.scene.Node;
import com.jme.scene.TriMesh;

public class ModelPool {

	public J3DCore core;
	
	public static int POOL_NUMBER_OF_UNUSED_TO_KEEP = 5; 
	public static int POOL_NUMBER_OF_UNUSED_TO_KEEP_MOVING_MODEL = 1;
	
	public class PoolItemContainer {
		public int numberOfUnusedToKeep = 5;
		public String id;
		public HashSet<PooledNode> used = new HashSet<PooledNode>();
		public HashSet<PooledNode> notUsed = new HashSet<PooledNode>();
		public PoolItemContainer(String id, int toKeep) {
			super();
			this.id = id;
			this.numberOfUnusedToKeep = toKeep;
		}
	}
	
	public ModelPool(J3DCore core)
	{
		this.core = core;
		POOL_NUMBER_OF_UNUSED_TO_KEEP = 5;
	}

	public static HashMap<String, PoolItemContainer> pool = new HashMap<String, PoolItemContainer>();
	
	public PooledNode getModel(RenderedCube rc, Model model, NodePlaceholder place) {
		if (model.type == Model.LODMODEL)
		{
			if ( ((LODModel)model).models[0].type == Model.TEXTURESTATEVEGETATION) {
				if (rc.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP)
				{
					// do not put texturestate veg on steeps
					//return null;
				}
			}
		}
		
		boolean rotated = false;
		if (place.horizontalRotation==J3DCore.horizontalEReal || place.horizontalRotation==J3DCore.horizontalWReal )
		{
			// these directions may need rotation when billboarding
			rotated = true;
		}
		
		String key = model.id+(rotated)+rc.cube.internalCube;
		
		PoolItemContainer cont = pool.get(key);
		synchronized (pool) {
			if (cont == null) {
				cont = new PoolItemContainer(key,POOL_NUMBER_OF_UNUSED_TO_KEEP);
				pool.put(key, cont);
			} else {
				if (cont.notUsed.iterator().hasNext()) {
					// if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("++ FROM POOL MODEL!"+model.id);
					PooledNode n = cont.notUsed.iterator().next();
					cont.notUsed.remove(n);
					cont.used.add(n);
					return n;
				}
			}
			PooledNode n = core.modelLoader.loadObject(rc, model, rotated);
			// if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("LOADING MODEL!"+model.id);
			n.setPooledContainer(cont);
			cont.used.add(n);
			int toCreate = POOL_NUMBER_OF_UNUSED_TO_KEEP - (cont.used.size()+cont.notUsed.size());
			
			if ( toCreate>0)
			{
				for (int i=0; i<toCreate; i++)
				{
					PooledNode unused = core.modelLoader.loadObject(rc, model, rotated);
					unused.setPooledContainer(cont);
					cont.notUsed.add(unused);
				}
			}
			return n;
		}
	}

	public PooledNode getMovingModel(RenderedMovingUnit rmu, Model model, NodePlaceholder place) {
		
		boolean rotated = false;
		if (place.horizontalRotation==J3DCore.horizontalEReal || place.horizontalRotation==J3DCore.horizontalWReal )
		{
			// these directions may need rotation when billboarding
			rotated = true;
		}
		
		String key = model.id+(rotated)+rmu.internal;
		
		PoolItemContainer cont = pool.get(key);
		synchronized (pool) {
			if (cont == null) {
				cont = new PoolItemContainer(key,POOL_NUMBER_OF_UNUSED_TO_KEEP_MOVING_MODEL);
				pool.put(key, cont);
			} else {
				if (cont.notUsed.iterator().hasNext()) {
					// if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("++ FROM POOL MODEL!"+model.id);
					PooledNode n = cont.notUsed.iterator().next();
					cont.notUsed.remove(n);
					cont.used.add(n);
					return n;
				}
			}
			PooledNode n = core.modelLoader.loadObject(rmu, model, rotated);
			// if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("LOADING MODEL!"+model.id);
			n.setPooledContainer(cont);
			cont.used.add(n);
			int toCreate = POOL_NUMBER_OF_UNUSED_TO_KEEP_MOVING_MODEL - (cont.used.size()+cont.notUsed.size());
			if (model instanceof MovingModel) toCreate = 1;
			if ( toCreate>0)
			{
				for (int i=0; i<toCreate; i++)
				{
					PooledNode unused = core.modelLoader.loadObject(rmu, model, rotated);
					unused.setPooledContainer(cont);
					cont.notUsed.add(unused);
				}
			}
			return n;
		}
	}
	
	public void releaseNode(PooledNode n)
	{
		synchronized (pool) {
			n.getPooledContainer().used.remove(n);
			n.getPooledContainer().notUsed.add(n);
		}
	}
	
	public PooledNode[] loadObjects(RenderedCube cube,Model[] objects, NodePlaceholder place, boolean fakeLoadForCacheMaint)
	{
		if (fakeLoadForCacheMaint) return null; 
		PooledNode[] retNodes = new PooledNode[objects.length];
		int count = 0;
		for (Model objModel : objects)
		{
			retNodes[count++]=getModel(cube, objModel, place);
		}
		return retNodes;
		
	}

	public NodePlaceholder[] loadPlaceHolderObjects(RenderedCube cube,Model[] objects,boolean fakeLoadForCacheMaint)
	{
		if (fakeLoadForCacheMaint) return null; 
		NodePlaceholder[] retNodes = new NodePlaceholder[objects.length];
		int count = 0;
		for (Model objModel : objects)
		{
			retNodes[count++] = new NodePlaceholder();
			retNodes[count-1].model = objModel;
			retNodes[count-1].cube = cube;
			retNodes[count-1].farView = cube.farview;
			if (objModel.rotateOnSteep) {
				(retNodes[count-1]).setUserData("rotateOnSteep", new TriMesh(""));
			}
			if (core.sPass!=null && objModel.shadowCaster)
			{
				if (retNodes[count-1]!=null) {
					//if (!J3DCore.GEOMETRY_BATCH)// || retNodes[count-1].model.type != Model.SIMPLEMODEL)
						core.possibleOccluders.add(retNodes[count-1]);
				}
			}
			
		}
		return retNodes;
		
	}

	public NodePlaceholder[] loadMovingPlaceHolderObjects(RenderedMovingUnit cube,Model[] objects,boolean fakeLoadForCacheMaint)
	{
		if (fakeLoadForCacheMaint) return null; 
		NodePlaceholder[] retNodes = new NodePlaceholder[objects.length];
		int count = 0;
		for (Model objModel : objects)
		{
			retNodes[count++] = new NodePlaceholder();
			retNodes[count-1].model = objModel;
			retNodes[count-1].unit = cube;
			if (objModel.rotateOnSteep) {
				(retNodes[count-1]).setUserData("rotateOnSteep", new TriMesh(""));
			}
			if (core.sPass!=null && objModel.shadowCaster)
			{
				if (retNodes[count-1]!=null) {
					//if (!J3DCore.GEOMETRY_BATCH)// || retNodes[count-1].model.type != Model.SIMPLEMODEL)
						core.possibleOccluders.add(retNodes[count-1]);
				}
			}
			
		}
		return retNodes;
		
	}

	public void cleanPools()
	{
		ArrayList<PooledNode> removed = new ArrayList<PooledNode>();
		ArrayList<PoolItemContainer> removedPoolCont = new ArrayList<PoolItemContainer>();
		for (PoolItemContainer pic: pool.values())
		{
			int toDelete = pic.notUsed.size()-pic.numberOfUnusedToKeep;
			if (toDelete>0) {
				removed.clear();
				Iterator<PooledNode> it = pic.notUsed.iterator();
				for (int i=0; i<toDelete; i++)
				{
					PooledNode node = it.next();
					core.removeSolidColorQuadsRecoursive((Node)node);
					if (node instanceof BillboardPartVegetation)
					{
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("REMOVING BBPART VEG:");
						{
							((BillboardPartVegetation)node).batch.parent.removeFromParent();
						}
					}
					removed.add(node);
				}
				pic.notUsed.removeAll(removed);
				//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(("ModelPool.cleanPools: removing poolnodes "+pic.id+" : "+toDelete));
				Jcrpg.LOGGER.info("ModelPool.cleanPools: removing poolnodes "+pic.id+" : "+toDelete);
			}
			if (pic.used.size()==0 && pic.notUsed.size()==0)
			{
				removedPoolCont.add(pic);
				Jcrpg.LOGGER.info("ModelPool.cleanPools: removing pool "+pic.id);
			}
		}
		for (PoolItemContainer cont:removedPoolCont)
		{
			pool.remove(cont.id);
		}
	}
	
	public static int getPooledSize()
	{
		int c = 0;
		for (PoolItemContainer pic: pool.values())
		{
				c += (pic.used!=null?pic.used.size():0) + (pic.notUsed!=null?pic.notUsed.size():0);
		}
		return c;
	}
	
	public void cleanAll()
	{
		for (PoolItemContainer pic: pool.values())
		{
			pic.notUsed.clear();
			pic.used.clear();
		}
	}
}
