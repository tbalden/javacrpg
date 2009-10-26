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

package org.jcrpg.world.ai.fauna;

import java.util.ArrayList;
import java.util.HashSet;

import org.jcrpg.space.Cube;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.DescriptionBase;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.economic.Population;

public class PerceptedVisibleForm extends VisibleLifeForm {

	public EntityFragment fragment;
	
	public Economic enteredEconomic;
	public Population enteredPopulation;
	public Geography nearGeography;
	
	public ArrayList<int[][]> possiblePlaces;
	
	public ArrayList<int[][]> getPossiblePlaces() {
		return possiblePlaces;
	}
	
	private transient HashSet<Long> coordinates = new HashSet<Long>();

	public void setPossiblePlaces(ArrayList<int[][]> possilbePlaces) {
		coordinates.clear();
		this.possiblePlaces = possilbePlaces;
		for (int[][] area:possilbePlaces)
		{
			for (int x = area[0][0]; x <= area[1][0]; x++)
			{
				for (int y = area[0][1]; y <= area[1][1]; y++)
				{
					for (int z = area[0][2]; z <= area[1][2]; z++)
					{
						coordinates.add(Boundaries.getKey(x, y, z));
					}
				}
			}
		}
	}
	
	public PerceptedVisibleForm(String uniqueId, EntityMember type, EntityInstance entity, EntityFragment fragment,PersistentMemberInstance member) {
		super(uniqueId,type,entity,member);
		this.fragment = fragment;
		
	}
	public PerceptedVisibleForm(String uniqueId, EntityMember type,
			EntityInstance entity, EntityFragment fragment, int groupId) {
		super(uniqueId, type, entity, groupId);
		this.fragment = fragment;
	}

	String id = null;
	public int uniqueId = 0;
	public String getIdentifier()
	{
		if (id==null)
		{
			id = fragment.getNumericId()+" " +(member==null?"null":member.getNumericId())+ " "+type.getName()+" "+groupId +" "+uniqueId;
		}
		return id;
	}
	
	
	private int[] getNextPossibleCoordinates(int counter)
	{
		int[] newCoordinates = null;
		if (possiblePlaces!=null && coordinates.size()>0)
		{
			int maxSize = coordinates.size();
			int random = HashUtil.mix(worldX+worldY+worldZ, counter+uniqueId, entity.getNumericId())%maxSize;
			newCoordinates = Boundaries.fromKey(coordinates.toArray(new Long[0])[random]);
		} else
		{
			int rx = -2 + HashUtil.mix(worldX+worldY+worldZ, counter+uniqueId, entity.getNumericId())%4;
			int rz = -2 + HashUtil.mix(worldX+worldY+worldZ+1, counter+uniqueId, entity.getNumericId())%4;
			newCoordinates = new int[]{rx,worldY,rz};
			
		}
		// positioning on surface
		ArrayList<SurfaceHeightAndType[]> surface = fragment.instance.world.getSurfaceData(newCoordinates[0], newCoordinates[2]);
		for (SurfaceHeightAndType[] type: surface)
		{
			for (SurfaceHeightAndType s:type)
			{
				if (enteredPopulation!=null)
				{
					if (s.self == enteredPopulation.soilGeo)
					{
						newCoordinates[1] = s.surfaceY;
						break;
					}
				} else
				if (nearGeography == s.self)
				{
					newCoordinates[1] = s.surfaceY;
					break;
				} else
				{
					newCoordinates[1] = s.surfaceY;
				}
			}
		}		
		
		return newCoordinates;
	}
	
	public DescriptionBase getDescriptionBase()
	{
		if (member!=null && member.description!=null)
		{
			return  member.description;
		} else
		{
			return fragment.instance.description;
		}

	}
	
	/**
	 * Try and Validate some coordinates, set occupation.
	 * @param occupied
	 * @return True if found, false if shouldnt render.
	 */
	public boolean updateCoordinatesBasedOnOccupation(HashSet<Long> occupied)
	{
		int maxTries = 10;
		int counter = 0;
		int cX = worldX, cY = worldY, cZ = worldZ;
		while (true)
		{
			if (occupied.contains(Boundaries.getKey(cX, cY, cZ)))
			{
				int[] c = getNextPossibleCoordinates(counter++);
				cX = c[0];
				cY = c[1];
				cZ = c[2];
			} else
			{
				Cube cb = fragment.instance.world.getCube(-1, cX, cY, cZ, false);
				boolean fine = true;
				DescriptionBase dB = getDescriptionBase();
				
				if (cb==null) 
				{
					if (!dB.isAirDweller())
						fine = false;
				} else
				if (!cb.canHoldUnit)
				{
					fine = false;
				} else
				{
					
					if (cb.waterCube)
					{
						if (!dB.isWaterDweller())
						{
							fine = false;
						}
					}
				}
				if (!fine)
				{
					int[] c = getNextPossibleCoordinates(counter++);
					cX = c[0];
					cY = c[1];
					cZ = c[2];

				} else
				{
					worldX = cX;
					worldY = cY;
					worldZ = cZ;
					occupied.add(Boundaries.getKey(worldX, worldY, worldZ));
					return true;
				}
			}
			if (maxTries<counter)
			{
				return false;
			}
		}
	}
	
	public void cleanOccupation(HashSet<Long> occupied)
	{
		occupied.remove(Boundaries.getKey(worldX, worldY, worldZ));
	}
}
