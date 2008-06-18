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

package org.jcrpg.threed.scene.moving;

import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.jme.moving.AnimatedModelNode;
import org.jcrpg.threed.scene.config.MovingTypeModels;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;

import com.jme.scene.Node;

/**
 * Class for rendered moving life forms with info about its movement/action states
 * @author illes
 */
public class RenderedMovingUnit {
	
	public static final String STATE_STANDING = "S_STANDING";
	public static final String STATE_WALKING = "S_WALKING";
	public static final String STATE_TURNING = "S_TURNING";
	public static final String STATE_RUNNING = "S_RUNNING";
	public static final String STATE_FALLING = "S_FALLING"; // between stand and lay - falling :-)
	public static final String STATE_LAYING = "S_LAYING";
	public static final String STATE_ATTACK_1 = "S_ATTACK_1";
	public static final String STATE_ATTACK_2 = "S_ATTACK_2";
	
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
	
	public Model[] models = null;
	public VisibleLifeForm form = null;
	
	public String state = STATE_STANDING;
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
		int origoZ = J3DCore.getInstance().gameState.origoZ;
		c3dZ = (origoZ-(worldZ-origoZ)) * J3DCore.CUBE_EDGE_SIZE;
	}
	public RenderedMovingUnit instantiate(String uniqueId, VisibleLifeForm form, int worldX, int worldY, int worldZ)
	{
		System.out.println(uniqueId+" INST: "+worldX+" "+worldY+" "+worldZ);
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
	public String stateAfterMovement = STATE_STANDING;
	
	// TODO function with moveToDirection!
	
	
	public void startToMoveOneCube(float speed, int toX, int toY, int toZ, boolean fromSteep, boolean toSteep)
	{
		changeToAnimation(MovingModelAnimDescription.ANIM_WALK);
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
		state = STATE_WALKING;
	}
	
	public void turn(float speed, int afterDirection, int toX, int toY, int toZ)
	{
		endCoordX = toX;
		endCoordY = toY;
		endCoordZ = toZ;
		movingSpeed = speed;
		direction = afterDirection;
		state = STATE_TURNING;
	}
	public void endTurn()
	{
		state = stateAfterMovement;
	}
	
	public void endMoveOneCube()
	{
		changeToAnimation(MovingModelAnimDescription.ANIM_IDLE);
		worldX = endCoordX; // shrink to world TODO
		worldY = endCoordY;
		worldZ = endCoordZ;
		state = stateAfterMovement;
		resetOrigo3DCoords();
	}
	
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
	
	
	
}
