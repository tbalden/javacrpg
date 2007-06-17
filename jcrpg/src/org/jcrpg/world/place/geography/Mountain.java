package org.jcrpg.world.place.geography;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Geography;


public class Mountain extends Geography {

	public static final String TYPE_PLAIN = "MOUNTAIN";
	public static final SideSubType SUBTYPE_GROUND_STEEP = new SideSubType(TYPE_PLAIN+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_GROUND_NORMAL = new SideSubType(TYPE_PLAIN+"_GROUND_NORMAL");

	static Side[][] GROUND_NORMAL = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GROUND_NORMAL)} };
	static Side[][] GROUND_STEEP = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GROUND_STEEP)} };


	int sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	
	public Mountain(String id, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception {
		super(id, loc);
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
		int iX = worldX-origoX;
		int iY = worldY-origoY;
		int iZ = worldZ-origoZ;
		int lX = sizeX-iX;
		int lY = sizeY-iY;
		int lZ = sizeZ-iZ;
		if (iZ%2==0&&iY==1) return new Cube(this,GROUND_NORMAL,worldX,worldY,worldZ);
		if (iZ%2==1&&iY==0) return new Cube(this,GROUND_STEEP,worldX,worldY,worldZ);
		return null;
	}
	
	

}
