package org.jcrpg.world.place;

public class BoundaryUtils {

	
	public static Boundaries createCubicBoundaries(int magnification,int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception
	{
		Boundaries b = new Boundaries(magnification);
		for (int x=0; x<sizeX;x++)
			for (int y=0; y<sizeY;y++)
				for (int z=0; z<sizeZ;z++)
				{
					b.addCube(magnification,origoX+x, origoY+y, origoZ+z);
				}
		return b;
			
	}
	
	
}
