package org.jcrpg.world.place.geography;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Geography;


public class Mountain extends Geography {

	public static final String TYPE_PLAIN = "MOUNTAIN";
	public static final SideSubType SUBTYPE_GROUND_STEEP = new SideSubType(TYPE_PLAIN+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_GROUND_NORMAL = new SideSubType(TYPE_PLAIN+"_GROUND_NORMAL");

	static Side[][] GROUND_NORMAL = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GROUND_NORMAL)} };
	static Side[][] GROUND_STEEP = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GROUND_STEEP)} };


	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	
	public Mountain(String id, Place parent, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}

	

	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		float iX = worldX-origoX*magnification;
		float iY = worldY-origoY*magnification;
		float iZ = worldZ-origoZ*magnification;
		float lX = sizeX*magnification-iX;
		float lY = sizeY*magnification-iY;
		float lZ = sizeZ*magnification-iZ;
		
		float proportionateXSizeOnLevelY = lX / (sizeY*1f*magnification/lY);
		float proportionateZSizeOnLevelY = lY / (sizeY*1f*magnification/lY);
		float gapX = ((sizeX*1f*magnification) - proportionateXSizeOnLevelY)/2f;
		float gapZ = ((sizeZ*1f*magnification) - proportionateZSizeOnLevelY)/2f;
		
		if (iX>gapX && iZ>gapZ && iX<lX-gapX && iZ<lZ-gapZ)
		{
			return new Cube(this,GROUND_NORMAL,worldX,worldY,worldZ);
		}

		if (iZ==(int)gapZ && iX>gapX && iX<lX-gapX)
		{
			return new Cube(this,GROUND_STEEP,worldX,worldY,worldZ);
		}
		if (iX==(int)gapX && iZ>gapZ && iZ<lZ-gapZ)
		{
			return new Cube(this,GROUND_STEEP,worldX,worldY,worldZ);
		}

		if (iZ==(int)(lZ-gapZ)+1 && iX>gapX && iX<lX-gapX)
		{
			return new Cube(this,GROUND_STEEP,worldX,worldY,worldZ);
		}
		if (iX==(int)(lX-gapX)+1 && iZ>gapZ && iZ<lZ-gapZ)
		{
			return new Cube(this,GROUND_STEEP,worldX,worldY,worldZ);
		}

		//if (iZ%4==3 && iY==iZ/4) return new Cube(this,GROUND_STEEP,worldX,worldY,worldZ);
		return null;
	}
	
	

}
