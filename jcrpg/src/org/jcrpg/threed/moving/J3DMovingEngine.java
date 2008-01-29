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

package org.jcrpg.threed.moving;

import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.scene.config.MovingTypeModels;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.ai.fauna.mammals.gorilla.GorillaHorde;
import org.jcrpg.world.ai.fauna.mammals.warthog.Warthogs;
import org.jcrpg.world.ai.fauna.mammals.wolf.WolfPack;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

/**
 * Moving units 3d display part.
 * @author illes
 */
public class J3DMovingEngine {

	
	J3DCore core = null;
	
	MovingTypeModels movingTypeModels = null;
	
	HashMap<String, RenderedMovingUnit> units = new HashMap<String, RenderedMovingUnit>();
	
	public J3DMovingEngine(J3DCore core)
	{
		this.core = core;
		movingTypeModels = new MovingTypeModels();
	}
	
	boolean firstRender = true;
	
	
	/**
	 * Renders a set of node into 3d space, rotating, positioning them.
	 * @param n Nodes
	 * @param unit the r.cube parent of the nodes, needed for putting the rendered node as child into it.
	 * @param x X cubesized distance from current relativeX
	 * @param y Y cubesized distance from current relativeY
	 * @param z Z cubesized distance from current relativeZ
	 * @param direction Direction
	 * @param horizontalRotation Horizontal rotation
	 * @param scale Scale
	 */
	private void renderNodes(NodePlaceholder[] n, RenderedMovingUnit unit)
	{
		if (n==null) return;
	
		for (int i=0; i<n.length; i++) {
			n[i].setLocalTranslation(new Vector3f(unit.c3dX,unit.c3dY,unit.c3dZ));
			n[i].getLocalTranslation().subtractLocal(new Vector3f(core.origoX,core.origoY,core.origoZ).mult(J3DCore.CUBE_EDGE_SIZE));
			if ((unit.models[0].type==Model.MOVINGMODEL) && ((MovingModel)unit.models[0]).modelName.endsWith(".obj"))
			{
				n[i].getLocalTranslation().subtractLocal(new Vector3f(0,-.5f,0).mult(J3DCore.CUBE_EDGE_SIZE));
			}
			Quaternion q = new Quaternion();
			Quaternion qC = null;
			if (n[i].model.noSpecialSteepRotation) {
				qC = new Quaternion(q); // base rotation
			} else
			{
				qC = new Quaternion();
			}
			
			{				
				//n[i].setLocalScale(needsFarviewScale?scale*FARVIEW_GAP:scale);
			}
			
			n[i].setLocalRotation(qC);
			n[i].setLocalScale(1f);

			unit.nodePlaceholders.add((NodePlaceholder)n[i]);
			//liveNodes++;
		}
	}
	
	/**
	 * Renders the moving units inside the render distance : looks for life forms in the World in reach of the player.
	 */
	public void render()
	{
		if (firstRender)
		{
			// TODO this only testing code! :-)
			firstRender = false;
			GorillaHorde horde = new GorillaHorde(core.world,core.ecology,"One",10,1,1,1);
			WolfPack pack = new WolfPack(core.world,core.ecology,"One",10,1,1,1);
			Warthogs hogs = new Warthogs(core.world,core.ecology,"One",10,1,1,1);
			for (int i=0; i<3; i++) {
				VisibleLifeForm form = i%3==0?horde.getOne():i%3==1?pack.getOne():hogs.getOne();
				RenderedMovingUnit unit = materializeLifeForm(form, core.viewPositionX+i%3, core.viewPositionY-1, core.viewPositionZ-3-(i%2)/2);
				unit.direction = (i%2==1?0:1);
				NodePlaceholder[] placeHolders = core.modelPool.loadMovingPlaceHolderObjects(unit, unit.models, false);
				renderNodes(placeHolders, unit);
			}
		}
		// TODO 
	}
	
	boolean firstRenderToViewPort = true;
	/**
	 * Set in view units visible / out of view units invisible on every movement of player or every new turn if no
	 * movement. 
	 * @param refAngle
	 */
	public void renderToViewPort(float refAngle)
	{
		if (firstRenderToViewPort) // TODO check if it's visible...
		{
			firstRenderToViewPort = false;
			// TODO check all rendered moving units if they are in view and set them visible
			for (RenderedMovingUnit unit: units.values())
			{
				for (NodePlaceholder n : unit.nodePlaceholders)
				{
					Node realPooledNode = (Node)core.modelPool.getMovingModel(unit, n.model, n);
					n.realNode = (PooledNode)realPooledNode;
					realPooledNode.setLocalTranslation(n.getLocalTranslation());
					//System.out.println("LOCALTRANS: "+realPooledNode.getLocalTranslation());
					realPooledNode.setLocalRotation(n.getLocalRotation());
					realPooledNode.setLocalScale(n.getLocalScale());
					if (unit.internal) {
						core.intRootNode.attachChild((Node)realPooledNode);
					} else 
					{
						core.extRootNode.attachChild((Node)realPooledNode);
					}
					
				}
			}
		}
	}
	
	int moveCount = 0;
	
	/**
	 * Updates moving units 3d embodiment in real time.
	 * @param timePerFrame Value passed since last frame.
	 */
	public void updateScene(float timePerFrame)
	{
		// TODO check all visible rendered moving units and update their coordinates etc.
		for (RenderedMovingUnit unit: units.values())
		{
			for (NodePlaceholder n : unit.nodePlaceholders)
			{
				if (n.realNode!=null)
				{
					if (unit.state.equals(RenderedMovingUnit.STATE_STANDING))
					{
						if (unit.direction==0)
						{
							unit.startToMoveOneCube(20f, unit.worldX, unit.worldY, unit.worldZ+1, false, false);
							unit.direction=1;
						} else
						if (unit.direction==1)
						{
							unit.startToMoveOneCube(20f, unit.worldX+1, unit.worldY, unit.worldZ, false, false);
							unit.direction=2;
						} else
						if (unit.direction==2)
						{
							unit.startToMoveOneCube(20f, unit.worldX, unit.worldY, unit.worldZ-1, false, false);
							unit.direction=3;
						} else
						if (unit.direction==3)
						{
							unit.startToMoveOneCube(20f, unit.worldX-1, unit.worldY, unit.worldZ, false, false);
							unit.direction=0;
						}
						
					} else
					if (unit.state.equals(RenderedMovingUnit.STATE_WALKING))
					{
						//float sX = (unit.startCoordX - (core.origoX))*J3DCore.CUBE_EDGE_SIZE;
						//float sY = -(0.5f*J3DCore.CUBE_EDGE_SIZE)+(unit.startCoordY - (core.origoY))*J3DCore.CUBE_EDGE_SIZE;
						//float sZ = (unit.startCoordZ - (core.origoZ))*J3DCore.CUBE_EDGE_SIZE;
						float eX = (unit.endCoordX - (core.origoX))*J3DCore.CUBE_EDGE_SIZE;
						float eY = (unit.endCoordY - (core.origoY))*J3DCore.CUBE_EDGE_SIZE;
						float eZ = (unit.endCoordZ - (core.origoZ))*J3DCore.CUBE_EDGE_SIZE;
						if ((unit.models[0].type==Model.MOVINGMODEL) && ((MovingModel)unit.models[0]).modelName.endsWith(".obj"))
						{
							eY-=.5f*J3DCore.CUBE_EDGE_SIZE;
						}
						/*float cX = unit.c3dX;
						float cY = unit.c3dY;
						float cZ = unit.c3dZ;*/
						
						Vector3f cVec = new Vector3f(n.getLocalTranslation());
						//Vector3f sVec = new Vector3f(sX,sY,sZ);
						Vector3f eVec = new Vector3f(eX,eY,eZ);
						/*System.out.println("START: "+sVec.x+" "+sVec.y+" "+sVec.z);
						System.out.println("END: "+eVec.x+" "+eVec.y+" "+eVec.z);
						System.out.println("C: "+n.getLocalTranslation().x+" "+n.getLocalTranslation().y+" "+n.getLocalTranslation().z);*/
						Vector3f mVec = eVec.subtract(cVec).normalize().mult(unit.movingSpeed * 0.1f*timePerFrame);
						n.getLocalTranslation().addLocal(mVec);
						//System.out.println(unit.c3dX+" "+unit.c3dY+" "+unit.c3dZ);
						((Node)n.realNode).setLocalTranslation(n.getLocalTranslation());
						Vector3f m = new Vector3f(mVec);
						m.y=0;
						Quaternion q = new Quaternion();
						q.fromAngleNormalAxis( m.normalize().angleBetween(new Vector3f(0,0,1f).normalize()), new Vector3f(0f,-1f,0f).normalize() );
						Matrix3f m3f = new Matrix3f();
						
						m3f.fromStartEndVectors(new Vector3f(0,0,1f).normalize(), m.normalize());
						//q.fromRotationMatrix(m3f);
						//q.oppositeLocal();
						//if (unit.direction== 0) 
						//if (q.mult(mVec).x>0 && q.mult(mVec).z>0) q.oppositeLocal();
						
						// trick for quaternion fixing, opposite local if ...
						m.normalizeLocal();
						if (m.x>0 && m.z>-0.2f && m.z<0.2f) q.oppositeLocal();
						
						Quaternion current = ((Node)n.realNode).getLocalRotation();
						Quaternion between = new Quaternion(current);
						between.slerp(q, 1f);
						current.slerp(between, 0.1f);
						
						((Node)n.realNode).setLocalRotation(current);//getLocalRotation().set(mVec.x, 0, mVec.z,1);
						float dist = eVec.distance(n.getLocalTranslation());
						//System.out.println("######### "+dist);
						
						if (dist<0.1f)
						{
							//System.out.println("TURN");
							unit.endMoveOneCube();
						}
					}
				}
			}
		}
	}
	
	public RenderedMovingUnit materializeLifeForm(VisibleLifeForm form, int worldX, int worldY, int worldZ)
	{
		RenderedMovingUnit unit = movingTypeModels.getRenderedUnit(form.typeId).instantiate(form.uniqueId, form, worldX, worldY, worldZ);
		units.put(form.uniqueId, unit);
		return unit;
	}
	
}