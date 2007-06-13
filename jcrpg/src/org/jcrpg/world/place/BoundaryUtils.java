package org.jcrpg.world.place;

public class BoundaryUtils {

	
	public static Boundaries createCubicBoundaries(int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ)
	{
		Boundaries b = new Boundaries();
		for (int x=-1*sizeX; x<sizeX;x++)
			for (int y=-1*sizeY; y<sizeY;y++)
				for (int z=-1*sizeZ; z<sizeZ;z++)
				{
					b.addCube(origoX+x, origoY+y, origoZ+z);
				}
		return b;
			
	}
	
	
}
