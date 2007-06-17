package org.jcrpg.world.place.geography;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Geography;


public class Mountain extends Geography {

	public static final String TYPE_MOUNTAIN = "MOUNTAIN";
	public static final SideSubType SUBTYPE_GROUND_STEEP = new SideSubType(TYPE_MOUNTAIN+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_GROUND_NORMAL = new SideSubType(TYPE_MOUNTAIN+"_GROUND_NORMAL");
	public static final SideSubType SUBTYPE_TREE = new SideSubType(TYPE_MOUNTAIN+"_TREE");

	static Side[] NORMAL = {new Side(TYPE_MOUNTAIN,SUBTYPE_GROUND_NORMAL)};
	static Side[] NORMAL_TREE = {new Side(TYPE_MOUNTAIN,SUBTYPE_GROUND_NORMAL),new Side(TYPE_MOUNTAIN,SUBTYPE_TREE)};
	
	static Side[][] GROUND_NORMAL = new Side[][] { null, null, null,null,null,NORMAL };
	static Side[][] GROUND_NORMAL_TREE = new Side[][] { null, null, null,null,null,NORMAL_TREE };
//	static Side[][] GROUND_STEEP = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GROUND_STEEP)} };


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
		int relX = worldX-origoX*magnification;
		int relY = worldY-origoY*magnification;
		int relZ = worldZ-origoZ*magnification;
		int remainingX = sizeX*magnification-relX;
		int remainingY = sizeY*magnification-relY;
		int remaningZ = sizeZ*magnification-relZ;
		int realSizeX = sizeX*magnification-1;
		int realSizeY = sizeY*magnification;
		int realSizeZ = sizeZ*magnification-1;
		
		System.out.println("MOUNTAIN GETC: "+relX+" "+relY+" "+relZ+" L: "+remainingX+" "+remainingY+" "+remaningZ);
		
		int proportionateXSizeOnLevelY = realSizeX - (int)(realSizeX * ((relY*1d)/(realSizeY)));
		int proportionateZSizeOnLevelY = realSizeZ - (int)(realSizeZ * ((relY*1d)/(realSizeY)));
		int gapX = ((realSizeX) - proportionateXSizeOnLevelY)/2;
		int gapZ = ((realSizeZ) - proportionateZSizeOnLevelY)/2;
		int proportionateXSizeOnLevelYNext = realSizeX - (int)(realSizeX * (((relY+1)*1d)/(realSizeY)));
		int proportionateZSizeOnLevelYNext = realSizeZ - (int)(realSizeZ * (((relY+1)*1d)/(realSizeY)));
		int gapXNext = ((realSizeX) - proportionateXSizeOnLevelYNext)/2;
		int gapZNext = ((realSizeZ) - proportionateZSizeOnLevelYNext)/2;
		
		
		boolean returnCube = false;
		// NORMAL
		if (relX>=gapX && relX<=gapXNext && relZ>=gapZ && relZ<=realSizeZ-gapZ)
		{
			returnCube = true;
		}
		if (relX<=realSizeX-gapX && relX>=realSizeX-gapXNext && relZ>=gapZ && relZ<=realSizeZ-gapZ)
		{
			returnCube = true;
		}
		if (relZ>=gapZ && relZ<=gapZNext && relX>=gapX &&  relX<=realSizeX-gapX)
		{
			returnCube = true;
		}
		if (relZ<=realSizeZ-gapZ && relZ>=realSizeZ-gapZNext && relX>=gapX && relX<=realSizeX-gapX)
		{
			returnCube = true;
		}
		if (!returnCube) return null;
		boolean cubeAbove = getCube( worldX,  worldY+1,  worldZ)!=null;
		Side[][] s = (worldX+worldZ)%8==0&&!cubeAbove?GROUND_NORMAL_TREE:GROUND_NORMAL;
		Cube c = null;
		c = new Cube(this,s,worldX,worldY,worldZ);
		return c;
	}
	
	

}
