/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.ui.map;

import java.nio.ByteBuffer;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.time.Time;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;

public class WorldMap {
	public Quad mapQuad = new Quad("WORLD_MAP",2f,2f);
	public BillboardNode bbMap;
	public Sphere mapSphere = new Sphere("WORLD_MAP_SPHERE",new Vector3f(0,0,0),10,10,0.5f);
	public Image oceanImage;
	public Image positionImage; 
	public Image climateImage;
	public byte[] positionImageSet;
	
	public int[][] map;
	
	public Texture oceanTex, posTex, climateTex;
	public TextureState baseTexState, posTexState, climateTexState;

	public World world;
	
	public int NOTHING = 0;
	public int WATER = 1;
	// 2
	// 4
	// 8
	// 16
	// 32
	// 64
	// 128
	
	public WorldMap(World w) {
		world = w;
	
		map = new int[w.sizeZ][w.sizeX];
		byte[] mapImage = new byte[w.sizeZ*w.sizeX*4];
		byte[] climateImage = new byte[w.sizeZ*w.sizeX*4];
		positionImageSet = new byte[w.sizeZ*w.sizeX*4];
		for (int z = 0; z<w.sizeZ;z++)
		{
			for (int x=0; x<w.sizeX; x++)
			{
				map[z][x] = NOTHING;
				mapImage[((z*w.sizeX)+x)*4+1] = (byte)150;
				mapImage[((z*w.sizeX)+x)*4+0] = (byte)50;
				for (Water water :w.waters.values())
				{
					if (water instanceof Ocean)
					{
						boolean oceanWater = ((Ocean)water).isWaterPointSpecial(x*((Ocean)water).magnification, ((Ocean)water).worldGroundLevel, z*((Ocean)water).magnification, false);
						if (oceanWater)
						{
							map[z][x] = (map[z][x]^WATER);
							System.out.print("W");
							mapImage[((z*w.sizeX)+x)*4+2] = (byte)100;
							mapImage[((z*w.sizeX)+x)*4+1] = (byte)0;
						} else {
							System.out.print(".");
							ClimateBelt belt = world.getClimate().getCubeClimate(new Time(), x*w.magnification, 0, z*w.magnification, false).getBelt();
							climateImage[((z*w.sizeX)+x)*4+0] = belt.colorBytes[0];
							climateImage[((z*w.sizeX)+x)*4+1] = belt.colorBytes[1];
							climateImage[((z*w.sizeX)+x)*4+2] = belt.colorBytes[2];
							mapImage[((z*w.sizeX)+x)*4+0] = belt.colorBytes[0];
							mapImage[((z*w.sizeX)+x)*4+1] = belt.colorBytes[1];
							mapImage[((z*w.sizeX)+x)*4+2] = belt.colorBytes[2];
						}
					}
					
				}
				mapImage[((z*w.sizeX)+x)*4+3] = (byte)150;
				
			}
			System.out.println("");
		}
		oceanImage = new Image();
		oceanImage.setType(Image.RGBA8888);
		oceanImage.setHeight(w.sizeZ);
		oceanImage.setWidth(w.sizeX);
		ByteBuffer buffer = ByteBuffer.wrap(mapImage);
		oceanImage.setData(buffer);
		
		positionImage = new Image();
		positionImage.setType(Image.RGBA8888);
		positionImage.setHeight(w.sizeZ);
		positionImage.setWidth(w.sizeX);
		ByteBuffer buffer2 = ByteBuffer.wrap(positionImageSet);
		positionImage.setData(buffer2);
	}
	
	int lastCx = -1 , lastCy = -1 , lastCz = -1;
	
	public void update(int cx, int cy, int cz)
	{
		if (cx==lastCx && cy==lastCy && cz==lastCz) return;
		
		lastCx = cx;
		lastCy = cy;
		lastCz = cz;			
		
		System.out.println("UPDATE: "+cx+" "+cz);
		for (int z = 0; z<world.sizeZ;z++)
		{
			for (int x=0; x<world.sizeX; x++)
			{
				positionImageSet[((z*world.sizeX)+x)*4+0] = (byte)0;
				positionImageSet[((z*world.sizeX)+x)*4+1] = (byte)0;
				positionImageSet[((z*world.sizeX)+x)*4+2] = (byte)0;
				positionImageSet[((z*world.sizeX)+x)*4+3] = (byte)0;
			}
		}
		int dotSize = world.sizeX/40;
		{
			for (int i=-1*dotSize; i<=1*dotSize; i++)
			{
				for (int j=-1*dotSize; j<=1*dotSize; j++)
				{
					try {
						positionImageSet[(((cz+i)*world.sizeX)+cx+j)*4+0] = (byte)((Math.abs(dotSize-j)*1d/dotSize)*(Math.abs(dotSize-i)*1d/dotSize)*355);
						positionImageSet[(((cz+i)*world.sizeX)+cx+j)*4+1] = (byte)0;
						positionImageSet[(((cz+i)*world.sizeX)+cx+j)*4+2] = (byte)0;
						positionImageSet[(((cz+i)*world.sizeX)+cx+j)*4+3] = (byte)255;
					} catch (ArrayIndexOutOfBoundsException aiex)
					{				
					}
				}
			}
		}
		positionImageSet[((cz*world.sizeX)+cx)*4+0] = (byte)255;
		positionImageSet[((cz*world.sizeX)+cx)*4+3] = (byte)255;

		ByteBuffer buffer2 = ByteBuffer.wrap(positionImageSet);
		positionImage.setData(buffer2);
		posTex = new Texture();
		posTex.setImage(positionImage);
		posTexState.setTexture(posTex);
		posTexState.setNeedsRefresh(true);
	}

	public TextureState[] getMapTextures()
	{
		oceanTex = new Texture();
		oceanTex.setImage(oceanImage);
		posTex = new Texture();
		posTex.setImage(positionImage);
		baseTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		baseTexState.setTexture(oceanTex);
		posTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		posTexState.setTexture(posTex);
		return new TextureState[]{baseTexState,posTexState};
	}

	
}
