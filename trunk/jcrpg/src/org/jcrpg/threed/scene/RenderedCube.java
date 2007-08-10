package org.jcrpg.threed.scene;

import java.util.ArrayList;

import org.jcrpg.space.Cube;
import org.jcrpg.threed.PlaceholderNode;
import org.jcrpg.threed.PooledNode;

public class RenderedCube {

	public RenderedCube(Cube c, int x, int y, int z)
	{
		cube = c;
		renderedX = x;
		renderedY = y;
		renderedZ = z;
	}
	
	public Cube cube;
	public int renderedX, renderedY, renderedZ;
	public ArrayList<PlaceholderNode> hsRenderedNodes = new ArrayList<PlaceholderNode>();
	
}
