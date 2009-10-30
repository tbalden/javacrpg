/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.threed.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.world.place.World;

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;

/**
 * Unit to hold a rendered/loaded state of a tile cube. Contains list of NodePlaceHolders which are actually visualized as jme nodes in renderToViewPort of J3DStandingEngine.
 * @author illes
 *
 */
public class RenderedCube implements Savable {

	public RenderedCube()
	{
		
	}
	
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
	
	public HashMap<Side, NodePlaceholder[]> hmNodePlaceholderForSide = new HashMap<Side, NodePlaceholder[]>();
	
	public void clear()
	{
		if (hsRenderedNodes!=null)
		{
			for (NodePlaceholder node:hsRenderedNodes)
			{
				node.clear();
			}
			hsRenderedNodes.clear();
			//hsRenderedNodes = null;
		}
		if (hmNodePlaceholderForSide!=null)
		{
			for (NodePlaceholder[] sides:hmNodePlaceholderForSide.values())
			{
				for (NodePlaceholder side:sides)
				{
					side.clear();
				}
			}
			hmNodePlaceholderForSide.clear();
			//hmNodePlaceholderForSide=null;
		}
		
		world = null;
		cube = null;
	}

	public Class getClassTag() {
		// TODO Auto-generated method stub
		return null;
	}

	public void read(JMEImporter arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void write(JMEExporter arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
