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

package org.jcrpg.threed.scene.moving;

import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.moving.AnimatedModelNode;
import org.jcrpg.threed.moving.J3DMovingEngine;
import org.jcrpg.threed.scene.config.MovingTypeModels;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;

import com.jme.scene.Node;

/**
 * Class for rendered moving life forms with info about its movement/action states
 * @author illes
 */
public class RenderedMovingUnit {
	
	
	/**
	 * The direction the unit is facing.
	 */
	public int direction = 0;

	/**
	 * Unique id of the unit.
	 */
	public String id = null;
	public int worldX, worldY, worldZ;
	public float c3dX, c3dY, c3dZ;
	public float walkHeight = 0f;
	
	public Model[] models = null;
	public VisibleLifeForm form = null;
	
	public String state = MovingModelAnimDescription.ANIM_IDLE;
	public boolean internal = false;
	
	public transient HashSet<NodePlaceholder> nodePlaceholders = new HashSet<NodePlaceholder>();
	
	
	/**
	 * billboard node for group size text
	 */
	public transient Node sizeTextNode = null;
	/**
	 * billboard node for membertype
	 */
	public transient Node memberTypeNameNode = null;
	
	/**
	 * billboard circle node around units leg
	 */
	public transient Node circleNode = null;
	
	public RenderedMovingUnit(Model[] models) {
		super();
		this.id = MovingTypeModels.NON_INSTANCE;
		this.worldX = 0;
		this.worldY = 0;
		this.worldZ = 0;
		this.models = models;
	}
	
	public RenderedMovingUnit(String id, int worldX, int worldY, int worldZ, Model[] models) {
		super();
		this.id = id;
		this.worldX = worldX;
		this.worldY = worldY;
		this.worldZ = worldZ;
		this.models = models;
	}
	public void resetOrigo3DCoords()
	{
		c3dX = worldX * J3DCore.CUBE_EDGE_SIZE;
		c3dY = worldY * J3DCore.CUBE_EDGE_SIZE;
		int origoZ = J3DCore.getInstance().gameState.getCurrentRenderPositions().origoZ;
		Cube c = J3DCore.getInstance().gameState.getCurrentStandingEngine().world.getCube(-1, worldX, worldY, worldZ, false);
		if (c!=null)
		{
			if (c.walkHeight!=0)
			{
				c3dY+=c.walkHeight;
				walkHeight = c.walkHeight;
			} else
			{
				c3dY+=c.middleHeight;
				walkHeight = c.middleHeight;
			}
		}
		c3dZ = (origoZ-(worldZ-origoZ)) * J3DCore.CUBE_EDGE_SIZE;
	}
	public RenderedMovingUnit instantiate(String uniqueId, VisibleLifeForm form, int worldX, int worldY, int worldZ)
	{
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("RenderedMovingUnit.instantiate : "+uniqueId+" INST: "+worldX+" "+worldY+" "+worldZ);
		RenderedMovingUnit instance = new RenderedMovingUnit(uniqueId,worldX,worldY,worldZ,models);
		instance.form = form;
		instance.state = state;
		instance.resetOrigo3DCoords();
		return instance;
	}
	
	public boolean onSteep;
	public boolean toSteep;
	public float movingSpeed;
	public float startPositionX, startPositionY, startPositionZ;
	public float endPositionX, endPositionY, endPositionZ;
	public int startCoordX, startCoordY, startCoordZ;
	public int endCoordX, endCoordY, endCoordZ;
	public String stateAfterMovement = MovingModelAnimDescription.ANIM_IDLE;
	
	public String getIdleStateName()
	{
		if (form!=null && stateAfterMovement == MovingModelAnimDescription.ANIM_IDLE && form.inEncounterPhase==Ecology.PHASE_TURNACT_COMBAT)
		{
			return MovingModelAnimDescription.ANIM_IDLE_COMBAT;
		}
		return stateAfterMovement;
	}
	
	// TODO function with moveToDirection!
	
	//public void 
	
	public boolean startToMoveOneCube(float speed, int toX, int toY, int toZ, boolean fromSteep, boolean toSteep)
	{
		if (form.notRendered) return false;	
		startCoordX = worldX;
		startCoordY = worldY;
		startCoordZ = worldZ;
		startPositionX = c3dX;
		startPositionY = c3dY;
		startPositionZ = c3dZ;
		endCoordX = toX;
		endCoordY = toY;
		endCoordZ = toZ;
		movingSpeed = speed;
		this.toSteep = toSteep;
		state = MovingModelAnimDescription.ANIM_WALK;
		changeToAnimation(state);
		
		J3DMovingEngine.activeUnits.add(this);
		return true;
		
	}
	
	public void turn(float speed, int afterDirection, int toX, int toY, int toZ)
	{
		endCoordX = toX;
		endCoordY = toY;
		endCoordZ = toZ;
		movingSpeed = speed;
		direction = afterDirection;
		state = MovingModelAnimDescription.ANIM_TURN;
		changeToAnimation(state);
	}
	public void endTurn()
	{
		state = stateAfterMovement;
	}
	
	public void endMoveOneCube()
	{
		worldX = endCoordX; // shrink to world TODO
		worldY = endCoordY;
		worldZ = endCoordZ;
		state = stateAfterMovement;
		changeToAnimation(state);
		resetOrigo3DCoords();
		J3DMovingEngine.activeUnits.remove(this);
	}
	
	public boolean startAttack(VisibleLifeForm target, String anim)
	{
		if (form.notRendered) return false;
		J3DMovingEngine.activeUnits.add(this);
		if (target!=null)
			form.targetForm = target;
		state = anim;		
		return true;
	}
	public boolean startDefense(VisibleLifeForm target, String anim)
	{
		if (form.notRendered) return false;
		J3DMovingEngine.activeUnits.add(this);
		if (target!=null)
			form.targetForm = target;
		state = anim==null?MovingModelAnimDescription.ANIM_DEFEND_UPPER:anim;		
		return true;
	}

	public boolean startPain(VisibleLifeForm target)
	{
		if (form.notRendered) return false;
		J3DMovingEngine.activeUnits.add(this);
		if (target!=null)
			form.targetForm = target;
		state = MovingModelAnimDescription.ANIM_PAIN;		
		return true;
	}
	public boolean startDeath(VisibleLifeForm target, String anim)
	{
		if (form.notRendered) return false;
		J3DMovingEngine.activeUnits.add(this);
		if (target!=null)
			form.targetForm = target;
		stateAfterMovement = MovingModelAnimDescription.ANIM_DEAD;
		state = anim==null?MovingModelAnimDescription.ANIM_DEATH_NORMAL:anim;
		return true;
	}

	public void stateFinished()
	{
		state = getIdleStateName();
		if (!state.equals(MovingModelAnimDescription.ANIM_IDLE) && !state.equals(MovingModelAnimDescription.ANIM_IDLE_COMBAT))
		{
			// if not idle anim, we should change to it, otherwise idle animation should be already played. 
			changeToAnimation(state);
		}
		J3DMovingEngine.activeUnits.remove(this);
	}
	
	public String playingAnimation = null;
	
	public void changeToAnimation(String animationType)
	{
		if (form.notRendered) return;
		for (NodePlaceholder n:nodePlaceholders)
		{
			if (n.model instanceof MovingModel)
			{
				if (n.realNode instanceof AnimatedModelNode)
				{
					((AnimatedModelNode)(n.realNode)).changeToAnimation(animationType);
				}				
			}
		}
		
	}
	
	public boolean isFinishedPlaying()
	{
		if (form.notRendered) return true;
		for (NodePlaceholder n:nodePlaceholders)
		{
			if (n.model instanceof MovingModel)
			{
				if (n.realNode instanceof AnimatedModelNode)
				{
					if (!((AnimatedModelNode)(n.realNode)).isFinishedPlaying()) return false;
				}				
			}
		}
		return true;
		
	}
	
	public void startPlayingAnimation(String animationType)
	{
		startPlayingAnimation(animationType,null);
	}
	public void startPlayingAnimation(String animationType,String afterAnim)
	{
		if (form.notRendered) return;
		for (NodePlaceholder n:nodePlaceholders)
		{
			if (n.model instanceof MovingModel)
			{
				if (n.realNode instanceof AnimatedModelNode)
				{
					((AnimatedModelNode)(n.realNode)).playAnimation(animationType,afterAnim);
				}				
			}
		}
		
	}
	
	public HashMap<NodePlaceholder,MovingModelAnimDescription> getMovingModelAnimationDescriptions()
	{
		if (form.notRendered) return null;
		HashMap<NodePlaceholder,MovingModelAnimDescription> list = new HashMap<NodePlaceholder,MovingModelAnimDescription>();
		for (NodePlaceholder n:nodePlaceholders)
		{
			if (n.model instanceof MovingModel)
			{
				if (((MovingModel)n.model).animation!=null)
				{
					list.put(n,((MovingModel)n.model).animation);
				}
			}
		}
		return list;
	}
	
	/**
	 * Draw flying impact points for 3d unit.
	 * @param unit
	 */
	public void visualizeImpactPoints(ImpactUnit unit)
	{
		J3DCore.getInstance().mEngine.visualizeImpactPoints(this, unit);
	}
	
	public void highlightUnit()
	{
		J3DCore.getInstance().mEngine.highlightUnitTemporarily(this);
	}
	
}
