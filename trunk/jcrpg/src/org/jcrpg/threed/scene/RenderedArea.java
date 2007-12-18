/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.threed.scene;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.World;

public class RenderedArea {
	
	public HashMap<Integer, RenderedCube> worldCubeCache = new HashMap<Integer, RenderedCube>(); 
	public HashMap<Integer, RenderedCube> worldCubeCacheNext = new HashMap<Integer, RenderedCube>(); 
	
	public RenderedCube[][] getRenderedSpace(World world, int x, int y, int z, int direction, boolean farView)
	{
		int distance = J3DCore.RENDER_DISTANCE;
		
		float xPlusMult = 1;
		float xMinusMult = -1;
		float zPlusMult = 1;
		float zMinusMult = -1;
		float viewPercent = 1f;//0.3f;
		
		if (J3DCore.OPTIMIZED_RENDERING) {
			if (direction==J3DCore.NORTH)
			{
				zPlusMult = viewPercent;
				
			}
			if (direction==J3DCore.SOUTH)
			{
				zMinusMult = -viewPercent;
				
			}
			if (direction==J3DCore.WEST)
			{
				xPlusMult = viewPercent;
				
			}
			if (direction==J3DCore.EAST)
			{
				xMinusMult = -viewPercent;
				
			}
		}
		worldCubeCacheNext = new HashMap<Integer, RenderedCube>();
		ArrayList<RenderedCube> elements = new ArrayList<RenderedCube>();
		for (int z1=Math.round(zMinusMult*distance); z1<=zPlusMult*distance; z1++)
		{
			for (int y1=-1*Math.min(distance,20); y1<=1*Math.min(distance,20); y1++)
			{
				for (int x1=Math.round(xMinusMult*distance); x1<=xPlusMult*distance; x1++)
				{
					int worldX = x+x1;
					int worldY = y+y1;
					int worldZ = z-z1;
					worldX = world.shrinkToWorld(worldX);
					worldZ = world.shrinkToWorld(worldZ);
					int key = ((worldX)<< 16) + ((worldY) << 8) + ((worldZ));
					RenderedCube c = worldCubeCache.remove(key);
					if (c==null) {
						Cube cube = world.getCube(world.engine.getWorldMeanTime(),worldX, worldY, worldZ, farView);
						if (cube!=null)
						{
							c = new RenderedCube(cube,x1,y1,z1);
						}
					}
					worldCubeCacheNext.put(key, c);
					if (c!=null) 
					{	
						elements.add(c);
					}
				}
			}
		}
		
		RenderedCube[] removable =  worldCubeCache.values().toArray(new RenderedCube[0]);
		worldCubeCache.clear();
		worldCubeCache = worldCubeCacheNext;
		
		return new RenderedCube[][]{(RenderedCube[])elements.toArray(new RenderedCube[0]),removable};
		
	}
	
	
}
