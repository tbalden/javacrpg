package org.jcrpg.threed.scene;

import org.jcrpg.space.Cube;

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
}
