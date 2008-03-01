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

import org.jcrpg.space.Side;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.world.place.World;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;

public class LocalMap {
	public Quad mapQuad = new Quad("WORLD_MAP",2f,2f);
	public BillboardNode bbMap;
	public Sphere mapSphere = new Sphere("WORLD_MAP_SPHERE",new Vector3f(0,0,0),10,10,0.5f);
	public Image oceanImage;
	public Image staticLayer; 
	public Image climateImage;
	public Image geoImage;
	public byte[] positionImageSet;
	
	public int[][] map;
	
	public Texture oceanTex, staticLayerTex,staticLayerTex2, climateTex, geoTex;
	public TextureState baseTexState, staticTexState, climateTexState, geoTexState;

	public RenderedArea area;
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
	
	public static int localMapSizeX = 13; 
	public static int localMapSizeY = 13;
	public static int centerX = 7;
	public static int centerY = 7;
	public int centerXPlus = 8;
	public int centerYPlus = 7;
	public int pointSizeX =5, pointSizeY = 5;
	public int pointSize = pointSizeX*pointSizeY;
	
	public byte[] staticLayerSet = new byte[localMapSizeX*localMapSizeY*4*pointSize];
	public byte[] dynamicLayerSet1 = new byte[localMapSizeX*localMapSizeY*4*pointSize];
	public byte[] dynamicLayerSet2 = new byte[localMapSizeX*localMapSizeY*4*pointSize];
	
	public int RED = 0, GREEN = 1, BLUE = 2, ALPHA = 3;
	

	public void paintPointAllSides(byte[] set, int x, int y, byte r, byte g, byte b, byte a)
	{
		for (int i=0; i<6; i++)
		{
			paintPoint(set, x, y, i, r, g, b, a);
		}
	}
	public void paintPoint(byte[] set, int x, int y, int side, byte r, byte g, byte b, byte a)
	{
		for (byte[] p : sideOffsets[side])
		{
			int offset = (((y*pointSizeY+p[0])*(localMapSizeX*pointSizeX)+x*pointSizeX+p[1]))*4;
			if (offset<0 || offset+ALPHA>=set.length) return;
			set[offset+RED] = r;
			set[offset+GREEN] = g;
			set[offset+BLUE] = b;
			set[offset+ALPHA] = a;
		}
	}
	
	
	public byte[][][] sideOffsets = new byte[][][]{
			//North 0
			{{-2,2},{-1,2},{0,2},{1,2},{2,2}},
			//East 1
			{{2,-1},{2,0},{2,1}},
			//South 2
			{{-2,-2},{-1,-2},{0,-2},{1,-2},{2,-2}},
			//West 3
			{{-2,-1},{-2,0},{-2,1}},
			//Top 4
			{},
			//Bottom 5
			{
				{-1,-1},{-1,0},{-1,1},
				{0,-1},{0,0},{0,1},
				{1,-1},{1,0},{1,1}
			},
	};
	
	public void update() {
		try {
			int dir = J3DCore.getInstance().gameState.viewDirection;
			if (dir == J3DCore.NORTH) {
				centerYPlus = centerY + 1;
				centerXPlus = centerX;
			} else if (dir == J3DCore.SOUTH) {
				centerYPlus = centerY - 1;
				centerXPlus = centerX;
			} else if (dir == J3DCore.EAST) {
				centerYPlus = centerY;
				centerXPlus = centerX + 1;
			} else if (dir == J3DCore.WEST) {
				centerYPlus = centerY;
				centerXPlus = centerX - 1;
			}
			int wX = J3DCore.getInstance().gameState.viewPositionX-centerX;
			int wY = J3DCore.getInstance().gameState.viewPositionY;
			int wZ = J3DCore.getInstance().gameState.viewPositionZ-centerY;
			for (int z = 0; z<localMapSizeY;z++)
			{
				for (int x=0; x<localMapSizeX; x++)
				{
					//int offset = ((z*localMapSizeX)+x)*4;
					if (x==centerX && z==centerY)
					{
						paintPointAllSides(staticLayerSet, x, z, (byte)235, (byte)20, (byte)20, (byte)110);
						continue;
					}
					if (x==centerXPlus && z==centerYPlus)
					{
						paintPointAllSides(staticLayerSet, x, z, (byte)255, (byte)100, (byte)100, (byte)110);
						continue;
					}
					RenderedCube c = area.getCubeAtPosition(world,wX+x,wY,wZ+z);
					RenderedCube cBelow = area.getCubeAtPosition(world,wX+x,wY-1,wZ+z);
					boolean water = cBelow!=null && cBelow.cube!=null && cBelow.cube.waterCube;
					if (c==null)
					{
						if (water) {
							paintPointAllSides(staticLayerSet, x, z, (byte)0, (byte)0, (byte)200, (byte)90);
						} else
						{
							paintPointAllSides(staticLayerSet, x, z, (byte)0, (byte)0, (byte)0, (byte)70);
						}
					} else
					{
						if (c.cube!=null && c.cube.bottom!=null) {
							boolean colorized = false;
							byte[] neutralColor = new byte[] {(byte)255, (byte)255, (byte)255, (byte)70};
							int[] neutralColorSum = new int[] {0,0,0};
							int colorsAdded = 0;
							for (Side side:c.cube.bottom)
							{
								byte[] b = side.subtype.colorBytes;
								if (!colorized || side.subtype.colorOverwrite) {
									neutralColor[RED] = b[RED];
									neutralColor[GREEN] = b[GREEN];
									neutralColor[BLUE] = b[BLUE];
									if (side.subtype.colorOverwrite) {
										neutralColorSum[RED]+= ((int)b[RED]+256)%256;
										neutralColorSum[GREEN]+= ((int)b[GREEN]+256)%256;
										neutralColorSum[BLUE]+= ((int)b[BLUE]+256)%256;
										colorsAdded++;
										/*if (colorsAdded==2)
										{
											System.out.println(side.subtype.id);
											System.out.println("C: "+c.cube);
											System.out.println("C: "+neutralColorSum[GREEN]);
											System.out.println("C: "+(byte)(neutralColorSum[GREEN]/colorsAdded));
										}*/
									}
								}
								colorized = true;
							}
							if (colorsAdded==0) colorsAdded=1;
							neutralColor[RED] = (byte)(neutralColorSum[RED]/colorsAdded);
							neutralColor[GREEN] = (byte)(neutralColorSum[GREEN]/colorsAdded);
							neutralColor[BLUE] = (byte)(neutralColorSum[BLUE]/colorsAdded);
							paintPoint(staticLayerSet, x, z, 5, (byte)(neutralColorSum[RED]/colorsAdded), (byte)(neutralColorSum[GREEN]/colorsAdded), (byte)(neutralColorSum[BLUE]/colorsAdded), (byte)80);
							for (int i=0; i<4; i++) {
								colorized = false;
								if (c.cube.sides!=null && c.cube.sides[i]!=null)
								for (Side side:c.cube.sides[i])
								{
									byte[] b = side.subtype.colorBytes;
									if (!colorized || colorized && side.subtype.colorOverwrite) {
										paintPoint(staticLayerSet, x, z, i, b[RED], b[GREEN], b[BLUE], (byte)80);
									}
									colorized = true;
								}
								if (!colorized) { // color this with neutral (ground) color
									paintPoint(staticLayerSet, x, z, i, neutralColor[RED], neutralColor[GREEN], neutralColor[BLUE], (byte)80);
								}
							}
						} else
						{
							//System.out.println("WX "+(wX+x)+" - "+(wZ+z)+" "+c.cube);
							paintPointAllSides(staticLayerSet, x, z, (byte)255, (byte)255, (byte)255, (byte)70);
						}
					}
				}
			}
			ByteBuffer buffer2 = ByteBuffer.wrap(staticLayerSet);
			staticLayer.setData(buffer2);
			if (staticTexState==null) 
			{
				System.out.println("NEW STATIC TEX STATE");
				staticTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
				staticLayerTex = new Texture();
				staticTexState.setTexture(staticLayerTex);
			} 
			staticTexState.getTexture().setImage(staticLayer);
			staticTexState.setNeedsRefresh(true);
			staticTexState.load();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public LocalMap(World world, RenderedArea area) {
		this.area = area;
		this.world = world;
		
		staticLayer = new Image();
		staticLayer.setType(Image.RGBA8888);
		staticLayer.setHeight(localMapSizeX*pointSizeX);
		staticLayer.setWidth(localMapSizeY*pointSizeY);
		
		update();
	}
	
	int lastCx = -1 , lastCy = -1 , lastCz = -1, lastDir = -1;
	
	public void update(int cx, int cy, int cz, int dir)
	{
		if (cx==lastCx && cy==lastCy && cz==lastCz && dir==lastDir) return;
		update();
		
	}

	public TextureState[] getMapTextures()
	{
		staticTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		staticTexState.setTexture(staticLayerTex);
		return new TextureState[]{staticTexState};
	}

	
}
