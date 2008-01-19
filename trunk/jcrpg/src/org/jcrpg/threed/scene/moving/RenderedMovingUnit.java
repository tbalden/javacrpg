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

import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.place.World;

/**
 * Class for rendered moving life forms with info about its movement/action states
 * @author illes
 */
public class RenderedMovingUnit {
	
	public static final String STATE_STANDING = "S_STANDING";
	public static final String STATE_WALKING = "S_WALKING";
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
	
	public HashSet<NodePlaceholder> nodePlaceholders = new HashSet<NodePlaceholder>();
	
	
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
		c3dZ = worldZ * J3DCore.CUBE_EDGE_SIZE;
	}
	public RenderedMovingUnit instanciate(String uniqueId, VisibleLifeForm form, int worldX, int worldY, int worldZ)
	{
		System.out.println(uniqueId+" INST: "+worldX+" "+worldY+" "+worldZ);
		RenderedMovingUnit instance = new RenderedMovingUnit(uniqueId,worldX,worldY,worldZ,models);
		instance.form = form;
		instance.state = state;
		instance.resetOrigo3DCoords();
		return instance;
	}
	
	public boolean onSteep;
	public float movingSpeed;
	public float startPositionX, startPositionY, startPositionZ;
	public float endPositionX, endPositionY, endPositionZ;
	public int startCoordX, startCoordY, startCoordZ;
	public int endCoordX, endCoordY, endCoordZ;
	public String stateAfterMovement = STATE_STANDING;
	
	// TODO function with moveToDirection!
	
	
	public void startToMoveOneCube(float speed, int toX, int toY, int toZ, boolean fromSteep, boolean toSteep)
	{
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
		state = STATE_WALKING;
	}
	
	public void endMoveOneCube()
	{
		worldX = endCoordX; // shrink to world TODO
		worldY = endCoordY;
		worldZ = endCoordZ;
		state = stateAfterMovement;
		resetOrigo3DCoords();
	}
	
	
	
}
