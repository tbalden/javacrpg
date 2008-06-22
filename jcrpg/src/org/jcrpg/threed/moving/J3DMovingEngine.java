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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.scene.config.MovingTypeModels;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.EntityScaledRelationType;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BillboardNode;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;

/**
 * Moving units 3d display part.
 * @author illes
 */
public class J3DMovingEngine {

	
	J3DCore core = null;
	
	MovingTypeModels movingTypeModels = null;
	
	HashMap<String, RenderedMovingUnit> units = new HashMap<String, RenderedMovingUnit>();
	
	public static HashSet<RenderedMovingUnit> activeUnits = new HashSet<RenderedMovingUnit>();
	
	
	public J3DMovingEngine(J3DCore core)
	{
		this.core = core;
		movingTypeModels = new MovingTypeModels();
	}
	
	boolean firstRender = false;
	
	
	/**
	 * Renders a set of node into 3d space, rotating, positioning them.
	 * @param n Nodes
	 * @param unit the r.cube parent of the nodes, needed for putting the rendered node as child into it.
	 */
	private void renderNodes(NodePlaceholder[] n, RenderedMovingUnit unit)
	{
		if (n==null) return;
	
		for (int i=0; i<n.length; i++) {
			n[i].setLocalTranslation(new Vector3f(unit.c3dX,unit.c3dY,unit.c3dZ));
			n[i].getLocalTranslation().subtractLocal(new Vector3f(core.gameState.origoX,core.gameState.origoY,core.gameState.origoZ).mult(J3DCore.CUBE_EDGE_SIZE));
			Quaternion q = new Quaternion();
			Quaternion qC = null;
			if (n[i].model.noSpecialSteepRotation) {
				qC = new Quaternion(q); // base rotation
			} else
			{
				qC = new Quaternion();
			}
			
			n[i].setLocalRotation(qC);
			//System.out.println("- "+unit.id+" "+unit.form.member);
			VisibleLifeForm form = unit.form;
			//EntityMemberInstance member = form.member;
			EntityMember desc = form.type;
			//System.out.println("DESC: "+desc.getClass().getSimpleName()+" "+desc.getScale()[0]);
			float[] scale = desc.getScale();
			scale[0] *= n[i].model.scale[0];
			scale[1] *= n[i].model.scale[1];
			scale[2] *= n[i].model.scale[2];
			n[i].setLocalScale(new Vector3f(scale[0],scale[1],scale[2]));
			if ((unit.models[0].type==Model.MOVINGMODEL) && !((MovingModel)unit.models[0]).animatedModel)
			{
				n[i].getLocalTranslation().subtractLocal(new Vector3f(0,.5f,0).mult(J3DCore.CUBE_EDGE_SIZE));
				
			} else
			{
				// scaling for md5 needs substraction
				n[i].getLocalTranslation().subtractLocal(new Vector3f(0,(1-scale[2])*0.4f,0).mult(J3DCore.CUBE_EDGE_SIZE));
				float[] d = (unit.models[0]).disposition;
				n[i].getLocalTranslation().addLocal(d[0],d[1],d[2]);
			}
			if (unit.onSteep)
			{
				n[i].getLocalTranslation().addLocal(new Vector3f(0,.5f,0).mult(J3DCore.CUBE_EDGE_SIZE));
			}
			
			unit.nodePlaceholders.add((NodePlaceholder)n[i]);
			//liveNodes++;
		}
	}
	
	public void clearUnitTextNodes(RenderedMovingUnit u)
	{
		if (u.circleNode!=null)
		{
			u.circleNode.removeFromParent();
		}
		if (u.sizeTextNode!=null)
		{
			u.sizeTextNode.removeFromParent();
		}
		if (u.memberTypeNameNode!=null)
		{
			u.memberTypeNameNode.removeFromParent();
		}
	}
	
	public void clearUnit(RenderedMovingUnit u)
	{
		for (NodePlaceholder n:u.nodePlaceholders)
		{
			PooledNode pooledRealNode = n.realNode;
			n.realNode = null;
			if (u.circleNode!=null)
			{
				u.circleNode.removeFromParent();
			}
			if (u.sizeTextNode!=null)
			{
				u.sizeTextNode.removeFromParent();
			}
			if (u.memberTypeNameNode!=null)
			{
				u.memberTypeNameNode.removeFromParent();
			}
			if (pooledRealNode!=null) {
				Node realNode = (Node)pooledRealNode;
				if (J3DCore.SHADOWS) core.removeOccludersRecoursive(realNode);
				realNode.removeFromParent();
				core.modelPool.releaseNode(pooledRealNode);
			}
		}
		units.remove(u);
		activeUnits.remove(u);
		
	}
	
	public void clearPreviousUnits()
	{
		for (RenderedMovingUnit u:units.values())
		{
			for (NodePlaceholder n:u.nodePlaceholders)
			{
				PooledNode pooledRealNode = n.realNode;
				n.realNode = null;
				if (u.circleNode!=null)
				{
					u.circleNode.removeFromParent();
				}
				if (u.sizeTextNode!=null)
				{
					u.sizeTextNode.removeFromParent();
				}
				if (u.memberTypeNameNode!=null)
				{
					u.memberTypeNameNode.removeFromParent();
				}
				if (pooledRealNode!=null) {
					Node realNode = (Node)pooledRealNode;
					if (J3DCore.SHADOWS) core.removeOccludersRecoursive(realNode);
					realNode.removeFromParent();
					core.modelPool.releaseNode(pooledRealNode);
				}
			}
		}
		units.clear();
	}
	
	/**
	 * Renders the moving units inside the render distance : looks for life forms in the World in reach of the player.
	 */
	public void render(Collection<VisibleLifeForm> forms)
	{
		
		clearPreviousUnits();
		int i=0;
		for (VisibleLifeForm form:forms)
		{
			if (form.notRendered) continue;
			RenderedMovingUnit unit = materializeLifeForm(form);
			unit.direction = (i%2==1?0:1);
			NodePlaceholder[] placeHolders = core.modelPool.loadMovingPlaceHolderObjects(unit, unit.models, false);
			renderNodes(placeHolders, unit);
			i++;			
		}
		renderToViewPort(0f);
	}
	
	public static ArrayList<ColorRGBA> lineupColors = new ArrayList<ColorRGBA>();
	static 
	{
		lineupColors.add(ColorRGBA.white);
		lineupColors.add(ColorRGBA.lightGray);
		lineupColors.add(ColorRGBA.darkGray);
		lineupColors.add(ColorRGBA.black);
	}
	
	
	public void getVisibleFormBillboardNodes(RenderedMovingUnit unit)
	{
		if (unit.form.forGroup()) {
			BillboardNode n = new BillboardNode("name");
			n.setAlignment(BillboardNode.SCREEN_ALIGNED);
			Node slottextNode = FontUtils.textNonBoldVerdana.createOutlinedText(""+unit.form.getSize(), 1, new ColorRGBA(0.9f,0.9f,0.9f,1f),new ColorRGBA(0.8f,0.8f,0.8f,1f),false);
			n.attachChild(slottextNode);
			slottextNode.setCullMode( SceneElement.CULL_NEVER );
			slottextNode.setTextureCombineMode( TextureState.REPLACE );
			ZBufferState s = J3DCore.getInstance().getDisplay().getRenderer().createZBufferState();
			s.setFunction(ZBufferState.CF_ALWAYS);
			//slottextNode.setRenderState(s);
			n.setRenderState(s);
			
			//n.setLocalTranslation(new Vector3f(0,2.5f,0));
			n.setLocalTranslation(new Vector3f(0.5f,0.1f,0));
			n.setLocalScale(0.17f);
			unit.sizeTextNode = n;
		}
		{		
			
			ColorRGBA c = new ColorRGBA(0.9f,0.9f,0.9f,1f);
			int relation = unit.form.entity.relations.getRelationLevel(core.gameState.player.theFragment);
			if (relation>EntityScaledRelationType.NEUTRAL)
			{
				c = ColorRGBA.green;
			} else
			if (relation<EntityScaledRelationType.NEUTRAL)
			{
				c = ColorRGBA.red;
			}
			
			BillboardNode n = new BillboardNode("name2");
			n.setAlignment(BillboardNode.SCREEN_ALIGNED);
			Node slottextNode = FontUtils.textNonBoldVerdana.createOutlinedText((unit.form.getLineupLine()+1)+". "+(unit.form.forGroup()?unit.form.type.getName():unit.form.member.getName()), 1, new ColorRGBA(0.9f,0.9f,0.9f,1f),c,true);
			n.attachChild(slottextNode);
			slottextNode.setCullMode( SceneElement.CULL_NEVER );
			ZBufferState s = J3DCore.getInstance().getDisplay().getRenderer().createZBufferState();
			s.setFunction(ZBufferState.CF_ALWAYS);
			//slottextNode.setRenderState(s);
			n.setRenderState(s);
			
			//n.setLocalTranslation(new Vector3f(0,2.5f,0));
			n.setLocalTranslation(new Vector3f(0.2f,0.3f,0));
			n.setLocalScale(0.1f);
			unit.memberTypeNameNode= n;
		}

		
		if (true==false)
		{
			// creating circle around the unit, currently unused
			
			Node n = new Node("name2");
			//n.setAlignment(BillboardNode.AXIAL_Z);
			Node slottextNode = FontUtils.textNonBoldVerdana.createOutlinedText("o", 1, new ColorRGBA(0.3f,0.9f,0.3f,1f),new ColorRGBA(0.1f,0.6f,0.1f,1f),true);
			n.attachChild(slottextNode);
			slottextNode.setCullMode( SceneElement.CULL_NEVER );
			ZBufferState s = J3DCore.getInstance().getDisplay().getRenderer().createZBufferState();
			s.setFunction(ZBufferState.CF_ALWAYS);
			//slottextNode.setRenderState(s);
			n.setRenderState(s);
			n.setLocalRotation(J3DCore.qT);
			
			//n.setLocalTranslation(new Vector3f(0,2.5f,0));
			n.setLocalTranslation(new Vector3f(0.0f,0.05f,0));
			n.setLocalScale(1.1f);
			unit.circleNode = n;
		}
	}
	
	public void updateUnitTextNodes(RenderedMovingUnit unit)
	{
		clearUnitTextNodes(unit);
		getVisibleFormBillboardNodes(unit);
		NodePlaceholder n = unit.nodePlaceholders.iterator().next();
		Node realPooledNode = (Node)n.realNode;
		if (realPooledNode!=null) {
			if (unit.sizeTextNode!=null)
			{
				realPooledNode.attachChild(unit.sizeTextNode);	
			}
			realPooledNode.attachChild(unit.memberTypeNameNode);
		}
		realPooledNode.updateRenderState();
	}
	
	
	boolean firstRenderToViewPort = true;
	/**
	 * Set in view units visible / out of view units invisible on every movement of player or every new turn if no
	 * movement. 
	 * @param refAngle
	 */
	public void renderToViewPort(float refAngle)
	{
			for (RenderedMovingUnit unit: units.values())
			{
				for (NodePlaceholder n : unit.nodePlaceholders)
				{
					Node realPooledNode = (Node)core.modelPool.getMovingModel(unit, n.model, n);
					n.realNode = (PooledNode)realPooledNode;
					realPooledNode.setLocalTranslation(n.getLocalTranslation());
					realPooledNode.setLocalRotation(n.getLocalRotation());
					realPooledNode.setLocalScale(n.getLocalScale());
					//realPooledNode.attachChild(unit.circleNode);
					
					
					if (unit.internal) {
						core.intRootNode.attachChild((Node)realPooledNode);
					} else 
					{
						core.extRootNode.attachChild((Node)realPooledNode);
					}
					realPooledNode.updateRenderState();
					
				}
				updateUnitTextNodes(unit);
				unit.changeToAnimation(MovingModelAnimDescription.ANIM_IDLE);
			}
	}
	
	int moveCount = 0;
	
	/**
	 * Updates moving units 3d embodiment in real time.
	 * @param timePerFrame Value passed since last frame.
	 */
	public void updateScene(float timePerFrame)
	{
		VisibleLifeForm playerFakeForm = new VisibleLifeForm("player",null,null,null);
		playerFakeForm.worldX = core.gameState.player.theFragment.roamingBoundary.posX;
		playerFakeForm.worldY = core.gameState.player.theFragment.roamingBoundary.posY;
		playerFakeForm.worldZ = core.gameState.player.theFragment.roamingBoundary.posZ;
		for (RenderedMovingUnit unit: units.values())
		{
			for (NodePlaceholder n : unit.nodePlaceholders)
			{
				if (n.realNode!=null)
				{
					if (unit.state.equals(MovingModelAnimDescription.ANIM_IDLE))
					{
						
						VisibleLifeForm target = unit.form.targetForm==null?playerFakeForm:unit.form.targetForm;
						float eX = (target.worldX - (core.gameState.origoX))*J3DCore.CUBE_EDGE_SIZE;
						float eY = (target.worldY - (core.gameState.origoY))*J3DCore.CUBE_EDGE_SIZE;
						int origoZ = core.gameState.origoZ;
						int endCoordZCorrect = (origoZ-(target.worldZ-origoZ));
						float eZ = ( endCoordZCorrect - (core.gameState.origoZ) )*J3DCore.CUBE_EDGE_SIZE;
	
						if ((unit.models[0].type==Model.MOVINGMODEL) && !((MovingModel)unit.models[0]).animatedModel)
						{
							eY-=.5f*J3DCore.CUBE_EDGE_SIZE;
						}
						float[] d = (unit.models[0]).disposition;
						eY+=d[1];
						
						if (unit.toSteep)
						{
							eY+=.5f*J3DCore.CUBE_EDGE_SIZE;
							float[] scale = unit.form.type.getScale();
							// scaling for md5 needs substraction
							eY-=(1-scale[2])*0.4f*J3DCore.CUBE_EDGE_SIZE;
						}
						
						Vector3f cVec = new Vector3f(n.getLocalTranslation());
						Vector3f eVec = new Vector3f(eX,eY,eZ);
						//System.out.println("-- "+cVec +" "+eVec);
						Vector3f mVec = eVec.subtract(cVec).normalize().mult(20f * 0.1f*timePerFrame);
						//n.getLocalTranslation().addLocal(mVec);
						//System.out.println(unit.c3dX+" "+unit.c3dY+" "+unit.c3dZ);
						//((Node)n.realNode).setLocalTranslation(n.getLocalTranslation());
						Vector3f m = new Vector3f(mVec);
						m.y=0;
						Quaternion q = new Quaternion();
						q.fromAngleNormalAxis( m.normalize().angleBetween(new Vector3f(0,0,1f).normalize()), new Vector3f(0f,-1f,0f).normalize() );
						m.normalizeLocal();
						if (m.x>0 && m.z>-0.4f && m.z<0.4f) q.oppositeLocal();
						
						Quaternion current = ((Node)n.realNode).getLocalRotation();
						Quaternion between = new Quaternion(current);
						between.slerp(q, 1f);
						current.slerp(between, 0.1f);
						
						((Node)n.realNode).setLocalRotation(current);//getLocalRotation().set(mVec.x, 0, mVec.z,1);
						
						if (unit.direction==0)
						{
							//unit.startToMoveOneCube(20f, unit.worldX, unit.worldY, unit.worldZ+1, false, false);
							unit.direction=1;
						} else
						if (unit.direction==1)
						{
							//unit.startToMoveOneCube(20f, unit.worldX+1, unit.worldY, unit.worldZ, false, false);
							unit.direction=2;
						} else
						if (unit.direction==2)
						{
							//unit.startToMoveOneCube(20f, unit.worldX, unit.worldY, unit.worldZ-1, false, false);
							unit.direction=3;
						} else
						if (unit.direction==3)
						{
							//unit.startToMoveOneCube(20f, unit.worldX-1, unit.worldY, unit.worldZ, false, false);
							unit.direction=0;
						}
						
					} else
					if (unit.state.equals(MovingModelAnimDescription.ANIM_DEATH_NORMAL) ||
							unit.state.equals(MovingModelAnimDescription.ANIM_DEATH_QUICK) ||
							unit.state.equals(MovingModelAnimDescription.ANIM_DEATH_SLOW) ||
							unit.state.equals(MovingModelAnimDescription.ANIM_PAIN) || 
							unit.state.equals(MovingModelAnimDescription.ANIM_ATTACK_UPPER) || unit.state.equals(MovingModelAnimDescription.ANIM_ATTACK_LOWER))
					{
						
						VisibleLifeForm target = unit.form.targetForm==null?playerFakeForm:unit.form.targetForm;
						
						if (unit.playingAnimation==null)
						{
							System.out.println("+++++++++++++++++");
							unit.startPlayingAnimation(unit.state);
							unit.playingAnimation = unit.state;
						} else
						{
							if (unit.isFinishedPlaying())
							{
								System.out.println("---------------------");
								unit.playingAnimation = null;
								unit.stateFinished();
								continue;
							} else
							{
								//System.out.println("STILL PLAYING...");
							}
						}
						
						
						float eX = (target.worldX - (core.gameState.origoX))*J3DCore.CUBE_EDGE_SIZE;
						float eY = (target.worldY - (core.gameState.origoY))*J3DCore.CUBE_EDGE_SIZE;
						int origoZ = core.gameState.origoZ;
						int endCoordZCorrect = (origoZ-(target.worldZ-origoZ));
						float eZ = ( endCoordZCorrect - (core.gameState.origoZ) )*J3DCore.CUBE_EDGE_SIZE;
	
						if ((unit.models[0].type==Model.MOVINGMODEL) && !((MovingModel)unit.models[0]).animatedModel)
						{
							eY-=.5f*J3DCore.CUBE_EDGE_SIZE;
						}
						float[] d = (unit.models[0]).disposition;
						eY+=d[1];
						
						if (unit.toSteep)
						{
							eY+=.5f*J3DCore.CUBE_EDGE_SIZE;
							float[] scale = unit.form.type.getScale();
							// scaling for md5 needs substraction
							eY-=(1-scale[2])*0.4f*J3DCore.CUBE_EDGE_SIZE;
						}
						
						Vector3f cVec = new Vector3f(n.getLocalTranslation());
						Vector3f eVec = new Vector3f(eX,eY,eZ);
						//System.out.println("-- "+cVec +" "+eVec);
						Vector3f mVec = eVec.subtract(cVec).normalize().mult(20f * 0.1f*timePerFrame);
						//n.getLocalTranslation().addLocal(mVec);
						//System.out.println(unit.c3dX+" "+unit.c3dY+" "+unit.c3dZ);
						//((Node)n.realNode).setLocalTranslation(n.getLocalTranslation());
						Vector3f m = new Vector3f(mVec);
						m.y=0;
						Quaternion q = new Quaternion();
						q.fromAngleNormalAxis( m.normalize().angleBetween(new Vector3f(0,0,1f).normalize()), new Vector3f(0f,-1f,0f).normalize() );
						m.normalizeLocal();
						if (m.x>0 && m.z>-0.4f && m.z<0.4f) q.oppositeLocal();
						
						Quaternion current = ((Node)n.realNode).getLocalRotation();
						Quaternion between = new Quaternion(current);
						between.slerp(q, 1f);
						current.slerp(between, 0.1f);
						
						((Node)n.realNode).setLocalRotation(current);//getLocalRotation().set(mVec.x, 0, mVec.z,1);
						
						if (unit.direction==0)
						{
							//unit.startToMoveOneCube(20f, unit.worldX, unit.worldY, unit.worldZ+1, false, false);
							unit.direction=1;
						} else
						if (unit.direction==1)
						{
							//unit.startToMoveOneCube(20f, unit.worldX+1, unit.worldY, unit.worldZ, false, false);
							unit.direction=2;
						} else
						if (unit.direction==2)
						{
							//unit.startToMoveOneCube(20f, unit.worldX, unit.worldY, unit.worldZ-1, false, false);
							unit.direction=3;
						} else
						if (unit.direction==3)
						{
							//unit.startToMoveOneCube(20f, unit.worldX-1, unit.worldY, unit.worldZ, false, false);
							unit.direction=0;
						}
						
					} else
					if (unit.state.equals(MovingModelAnimDescription.ANIM_WALK))
					{
						//float sX = (unit.startCoordX - (core.gameState.origoX))*J3DCore.CUBE_EDGE_SIZE;
						//float sY = -(0.5f*J3DCore.CUBE_EDGE_SIZE)+(unit.startCoordY - (core.gameState.origoY))*J3DCore.CUBE_EDGE_SIZE;
						//float sZ = (unit.startCoordZ - (core.gameState.origoZ))*J3DCore.CUBE_EDGE_SIZE;
						float eX = (unit.endCoordX - (core.gameState.origoX))*J3DCore.CUBE_EDGE_SIZE;
						float eY = (unit.endCoordY - (core.gameState.origoY))*J3DCore.CUBE_EDGE_SIZE;
						int origoZ = core.gameState.origoZ;
						int endCoordZCorrect = (origoZ-(unit.endCoordZ-origoZ));
						float eZ = ( endCoordZCorrect - (core.gameState.origoZ) )*J3DCore.CUBE_EDGE_SIZE;
						//float eZ = ( ((core.gameState.origoZ)*2) - unit.endCoordZ )*J3DCore.CUBE_EDGE_SIZE;
						if ((unit.models[0].type==Model.MOVINGMODEL) && !((MovingModel)unit.models[0]).animatedModel)
						{
							eY-=.5f*J3DCore.CUBE_EDGE_SIZE;
						}
						float[] d = (unit.models[0]).disposition;
						eY+=d[1];
						
						if (unit.toSteep)
						{
							eY+=.5f*J3DCore.CUBE_EDGE_SIZE;
							float[] scale = unit.form.type.getScale();
							// scaling for md5 needs substraction
							eY-=(1-scale[2])*0.4f*J3DCore.CUBE_EDGE_SIZE;
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
						//Matrix3f m3f = new Matrix3f();
						//m3f.fromStartEndVectors(new Vector3f(0,0,1f).normalize(), m.normalize());

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
	
	public void setAnimationForRenderedUnit(VisibleLifeForm form, String anim)
	{
		form.unit.changeToAnimation(anim);
	}
	
	public RenderedMovingUnit materializeLifeForm(VisibleLifeForm form)
	{
		RenderedMovingUnit unit = movingTypeModels.getRenderedUnit(form.type.visibleTypeId).instantiate(form.uniqueId, form, form.worldX, form.worldY, form.worldZ);
		unit.onSteep = form.onSteep;
		form.unit = unit;
		units.put(form.uniqueId, unit);
		return unit;
	}
	
	public void clearAll()
	{
		units.clear();
	}
	
}
