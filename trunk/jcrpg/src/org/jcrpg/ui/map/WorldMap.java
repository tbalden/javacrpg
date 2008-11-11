/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.ui.map;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.place.water.River;
import org.jcrpg.world.time.Time;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jmex.awt.swingui.ImageGraphics;

public class WorldMap {
	public Quad mapQuad = new Quad("WORLD_MAP",2f,2f);
	public BillboardNode bbMap;
	public Sphere mapSphere = new Sphere("WORLD_MAP_SPHERE",new Vector3f(0,0,0),10,10,0.5f);
	public Image oceanImage;
	public Image positionImage; 
	public Image climateImage;
	public Image geoImage;
	public byte[] positionImageSet;
	
	public int[][] map;
	
	public Texture oceanTex, posTex, climateTex, geoTex;
	public TextureState baseTexState, posTexState, climateTexState, geoTexState;

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
	public static HashSet<Quad> updatedQuads = new HashSet<Quad>();
	
	public void registerQuad(Quad q)
	{
		updatedQuads.add(q);
	}
	
	public WorldMap(World w) {
		world = w;
	
		map = new int[w.sizeZ][w.sizeX];
		byte[] mapImage = new byte[w.sizeZ*w.sizeX*4];
		byte[] climateImage = new byte[w.sizeZ*w.sizeX*4];
		byte[] geoImageSet = new byte[w.sizeZ*w.sizeX*4];
		positionImageSet = new byte[w.sizeZ*w.sizeX*4];
		Collection<Geography> geos = world.geographies.values();
		for (int z = 0; z<w.sizeZ;z++)
		{
			for (int x=0; x<w.sizeX; x++)
			{
				map[z][x] = NOTHING;
				mapImage[((z*w.sizeX)+x)*4+1] = (byte)150;
				mapImage[((z*w.sizeX)+x)*4+0] = (byte)50;
				boolean oceanWater = false;
				boolean riverWater = false;
				boolean ecoFound = false;
				for (Water water :w.waters.values())
				{
					if (water instanceof Ocean && !riverWater)
					{
						oceanWater = ((Ocean)water).isWaterPointSpecial(x*((Ocean)water).magnification, ((Ocean)water).worldGroundLevel, z*((Ocean)water).magnification, false, false);
						if (oceanWater)
						{
							map[z][x] = (map[z][x]^WATER);
							System.out.print("W");
							mapImage[((z*w.sizeX)+x)*4+2] = (byte)100;
							mapImage[((z*w.sizeX)+x)*4+1] = (byte)0;
							geoImageSet[((z*w.sizeX)+x)*4+3] = (byte)0;
						} else {
							System.out.print(".");
							ClimateBelt belt = world.getClimate().getCubeClimate(new Time(), x*w.magnification, 0, z*w.magnification, false).getBelt();
							climateImage[((z*w.sizeX)+x)*4+0] = belt.colorBytes[0];
							climateImage[((z*w.sizeX)+x)*4+1] = belt.colorBytes[1];
							climateImage[((z*w.sizeX)+x)*4+2] = belt.colorBytes[2];
							mapImage[((z*w.sizeX)+x)*4+0] = belt.colorBytes[0];
							mapImage[((z*w.sizeX)+x)*4+1] = belt.colorBytes[1];
							mapImage[((z*w.sizeX)+x)*4+2] = belt.colorBytes[2];
							int wx = x*w.magnification;
							int wz = z*w.magnification;
							ArrayList<Object> economics = w.economyContainer.treeLocator.getElements(wx, w.getSeaLevel(1), wz);
							if (economics!=null)
							{
								for (Object o:economics)
								{
									Economic e = ((Economic)o);
									if (
											(e.getBoundaries().limitXMin>wx && e.getBoundaries().limitXMin>wx+w.magnification)
											||
											(e.getBoundaries().limitXMax<wx && e.getBoundaries().limitXMax<wx+w.magnification)
											||
											(e.getBoundaries().limitZMin>wz && e.getBoundaries().limitZMin>wz+w.magnification)
											||
											(e.getBoundaries().limitZMax<wz && e.getBoundaries().limitZMax<wz+w.magnification)
									) 
									{
										/*if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("# OUT "+
												e.getBoundaries().limitXMin+" > "+wx + " && " +
												e.getBoundaries().limitXMin+" > "+(wx+w.magnification) + " || " + 
												e.getBoundaries().limitXMax+" < "+wx + " && " +
												e.getBoundaries().limitXMax+" < "+(wx+w.magnification) + " || " + 
												e.getBoundaries().limitZMin+" > "+wz + " && " +
												e.getBoundaries().limitZMin+" > "+(wz+w.magnification) + " || " + 
												e.getBoundaries().limitZMax+" < "+wz + " && " +
												e.getBoundaries().limitZMax+" < "+(wz+w.magnification) 
										);*/
										continue;
									}
									// matching economic population!
									geoImageSet[((z*w.sizeX)+x)*4+0] = (byte)255;
									geoImageSet[((z*w.sizeX)+x)*4+1] = (byte)255;
									geoImageSet[((z*w.sizeX)+x)*4+2] = (byte)255;
									geoImageSet[((z*w.sizeX)+x)*4+3] = (byte)150;
									ecoFound = true;
									break;
								}
								
							}
							if (ecoFound) break;
							for (Geography g:geos)
							{
								if (g.getBoundaries().isInside(wx, g.worldGroundLevel, wz))
								{
									geoImageSet[((z*w.sizeX)+x)*4+0] = g.colorBytes[0];
									geoImageSet[((z*w.sizeX)+x)*4+1] = g.colorBytes[1];
									geoImageSet[((z*w.sizeX)+x)*4+2] = g.colorBytes[2];
									geoImageSet[((z*w.sizeX)+x)*4+3] = (byte)150;
									break;
								}
							}
						}
					} else
					if (water instanceof River) // TODO river into a new image for not overwriting geo things!!
					{
						
						int wx = x*w.magnification;
						int wz = z*w.magnification;
						riverWater = ((River)water).isWaterBlock(wx, world.worldGroundLevel, wz);
						if (riverWater) {
							System.out.print("!");
							mapImage[((z*w.sizeX)+x)*4+2] = (byte)200;
							mapImage[((z*w.sizeX)+x)*4+1] = (byte)30;
							mapImage[((z*w.sizeX)+x)*4+0] = (byte)30;
							climateImage[((z*w.sizeX)+x)*4+0] = 0;
							climateImage[((z*w.sizeX)+x)*4+1] = 0;
							climateImage[((z*w.sizeX)+x)*4+2] = 0;
							geoImageSet[((z*w.sizeX)+x)*4+3] = (byte)0;
						}
					}
					
				}
				mapImage[((z*w.sizeX)+x)*4+3] = (byte)150;
				
				
			}
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("");
		}
		oceanImage = new Image();
		oceanImage.setFormat(Image.Format.RGBA8);
		oceanImage.setHeight(w.sizeZ);
		oceanImage.setWidth(w.sizeX);
		ByteBuffer buffer = ByteBuffer.wrap(mapImage);
		oceanImage.setData(buffer);
		
		positionImage = new Image();
		positionImage.setFormat(Image.Format.RGBA8);
		positionImage.setHeight(w.sizeZ);
		positionImage.setWidth(w.sizeX);
		ByteBuffer buffer2 = ByteBuffer.wrap(positionImageSet);
		positionImage.setData(buffer2);
		
		geoImage = new Image();
		geoImage.setFormat(Image.Format.RGBA8);
		geoImage.setHeight(w.sizeZ);
		geoImage.setWidth(w.sizeX);
		ByteBuffer buffer3 = ByteBuffer.wrap(geoImageSet);
		geoImage.setData(buffer3);
		update(0, 0, 0);
	}
	
	int lastCx = -1 , lastCy = -1 , lastCz = -1;
	
	public ByteBuffer posBuffer = null;

	
	public static HashMap<Integer, Color> colorCache = new HashMap<Integer, Color>();
	public void paintPoint(ImageGraphics set, int x, int y, int r, int g, int b, int a)
	{
		a = 255;
		r = 255;
		int k = (r<<24)+(g<<16)+(b<<8)+a;
		Color c = colorCache.get(k);
		if (c==null)
		{
			c = new Color(Math.max(0, Math.min(r,255)),Math.max(0, Math.min(g,255)),Math.max(0, Math.min(b,255)),a);
			colorCache.put(k, c);
		} 
		set.setColor(c);
		set.drawRect(x, y, 0, 0);
	}
	
	public void update(int cx, int cy, int cz)
	{
		//if (true) return;
		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest(""+Math.min(world.sizeZ,++cz)+" "+Math.max(0,--cx));
		if (cx==lastCx && cz==lastCz) return;
		lastCx = cx;
		lastCz = cz;
		cz = Math.min(world.sizeZ,++cz);
		cx = Math.max(0,--cx);
		boolean newInstance = false;
		if (positionGraphics==null)
		{
			newInstance = true;
			positionGraphics = ImageGraphics.createInstance(world.sizeX, world.sizeZ, 0);
		}
		positionGraphics.setBackground(new Color(0,0,0,0));
		positionGraphics.clearRect(0, 0, positionGraphics.getImage().getWidth(), positionGraphics.getImage().getHeight());
		
		Jcrpg.LOGGER.info("UPDATE: "+cx+" "+cz);
		int dotSize = world.sizeX/70;
		{
			for (int i=-1*dotSize; i<=1*dotSize; i++)
			{
				for (int j=-1*dotSize; j<=1*dotSize; j++)
				{
					try {
						int red = (int)((((dotSize-Math.abs(j))*1d/dotSize)*((dotSize-Math.abs(i))*1d/dotSize)*355));
						paintPoint(positionGraphics, cx+j, cz+i, red , 0, 0, 255);
					} catch (ArrayIndexOutOfBoundsException aiex)
					{				
					}
				}
			}
		}
		try {
			paintPoint(positionGraphics, cx, cz, 255 , 0, 0, 255);
		} catch (ArrayIndexOutOfBoundsException aiex){
			
			aiex.printStackTrace();
		}
		
		if (newInstance)
		{
			posTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
			posTex = new Texture2D();
			posTex.setMagnificationFilter( Texture.MagnificationFilter.Bilinear);
			posTex.setMinificationFilter(Texture.MinificationFilter.NearestNeighborLinearMipMap);
			posTex.setImage(positionGraphics.getImage());
			posTexState.setTexture(posTex);
			posTexState.apply();
		}else
		{	positionGraphics.update(posTex,false);
			posTexState.apply();
		}
		for (Quad q:updatedQuads)
		{
			if (q.getRenderState(RenderState.RS_TEXTURE)!=posTexState) {
				q.setRenderState(posTexState);
			}
			q.updateRenderState();
		}
	}
	public ImageGraphics positionGraphics;

	public TextureState[] getMapTextures()
	{
		oceanTex = new Texture2D();
		oceanTex.setImage(oceanImage);
		geoTex = new Texture2D();
		geoTex.setImage(geoImage);
		baseTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		baseTexState.setTexture(oceanTex);
		geoTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		geoTexState.setTexture(geoTex);
		return new TextureState[]{baseTexState,null,geoTexState};
	}

	
}
