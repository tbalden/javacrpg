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

package org.jcrpg.world.ai;

import org.jcrpg.world.ai.fauna.DistanceBasedBoundary;
import org.jcrpg.world.place.World;

/**
 * All moving beings's base class which should interact between group and individual intelligence.
 * @author pali
 *
 */
public class EntityDescription {
	public DistanceBasedBoundary roamingBoundary = null;
	public DistanceBasedBoundary domainBoundary = null;
	/**
	 * Unique id in the worlds.
	 */
	public String id;
	public int numberOfMembers = 1;
	public World world;

	public EntityDescription(World w, String id, int numberOfMembers, int startX, int startY, int startZ) {
		super();
		this.id = id;
		this.numberOfMembers = numberOfMembers;
		this.world = w;
		roamingBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,0);
		domainBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,0);
	}
	public void liveOneTurn()
	{
		System.out.println("LIVE ONE TURN "+this.getClass()+" "+id);
	}

	public static int visibleSequence = 0;
	public static Object mutex = new Object();

	public int nextVisibleSequence()
	{
		synchronized (mutex) {
			visibleSequence++;
		}
		return visibleSequence;
	}
	
	public static int getVisibleSequence() {
		return visibleSequence;
	}
	public static void setVisibleSequence(int visibleSequence) {
		EntityDescription.visibleSequence = visibleSequence;
	}
	
	public static void getInstance(World w, String id, int size, int startX, int startY, int startZ)
	{
		new EntityDescription(w,id,size,startX,startY,startZ);
	}

}
