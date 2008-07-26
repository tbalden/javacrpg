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

import org.jcrpg.space.Cube;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.world.place.World;

public class RenderedCube {

	
	public RenderedCube(Cube c, int x, int y, int z)
	{
		cube = c;
		renderedX = x;
		renderedY = y;
		renderedZ = z;
	}
	public boolean farview = false;
	public Cube cube;
	public int renderedX, renderedY, renderedZ;
	public World world;
	public ArrayList<NodePlaceholder> hsRenderedNodes = new ArrayList<NodePlaceholder>();
	
}
