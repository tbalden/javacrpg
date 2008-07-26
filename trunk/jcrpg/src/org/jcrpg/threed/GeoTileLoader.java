/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.threed;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.ModelPool.PoolItemContainer;
import org.jcrpg.threed.jme.TiledTerrainBlock;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;

public class GeoTileLoader {

	ModelLoader l = null;
	public GeoTileLoader(ModelLoader l)
	{
		this.l = l;
	}
	
	public class TerrainBlockExt extends Node implements PooledNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		PoolItemContainer pic = null;
		public PoolItemContainer getPooledContainer() {
			return pic;
		}

		public void setPooledContainer(PoolItemContainer cont) {
			pic = cont;
		}
		
	}
	
	public TerrainBlockExt loadNode(SimpleModel model, RenderedCube rCube)
	{
		TerrainBlockExt ext = new TerrainBlockExt();
		TerrainBlock block = new TerrainBlock();
		float[] cornerHeights = rCube.cube.cornerHeights;
		block.setSize(2);
		int[] map = new int[4];
		for (int i=0; i<4; i++)
		{
			int h = (int)cornerHeights[i]*10;
			map[i] = h;
		}
		block.setHeightMap(map);
		block.updateFromHeightMap();
		ext.attachChild(block);
		ext.updateRenderState();
		return ext;
	}

	public boolean checkHeightOpposite(float[] cornerHeights, float[] opp, float oppDeltaY)
	{
		//System.out.println(oppDeltaY+ " : "+cornerHeights[1]+" OPP0 == "+(opp[0]+oppDeltaY));
		//System.out.println(oppDeltaY+ " : "+cornerHeights[3]+" OPP2 == "+(opp[2]+oppDeltaY));
		if (Math.abs(cornerHeights[1]-(opp[0]+oppDeltaY))>0.1f) return false;
		//if (Math.abs(cornerHeights[3]-(opp[2]+oppDeltaY))>0.1f) return false;
		return true;
		
	}
	public boolean checkHeightAdj(float[] cornerHeights, float[] adj, float adjDeltaY)
	{
		//System.out.println(adjDeltaY+ " : "+cornerHeights[2]+" ADJ0 == "+(adj[0]+adjDeltaY));
		//System.out.println(adjDeltaY+ " : "+cornerHeights[3]+" ADJ1 == "+(adj[1]+adjDeltaY));
		if (Math.abs(cornerHeights[2]-(adj[0]+adjDeltaY))>0.1f) return false;
		//if (Math.abs(cornerHeights[3]-(adj[1]+adjDeltaY))>0.1f) return false;
		return true;
		
	}
	
	
	public TiledTerrainBlock loadNodeOriginal(NodePlaceholder nodePlaceholder, SimpleModel model, RenderedCube rCube)
	{
		
		float[] cornerHeights = rCube.cube.cornerHeights;
		if (cornerHeights==null) cornerHeights = new float[9];
		int[] map = new int[4];
		for (int i=0; i<4; i++)
		{
			int h = (int)(cornerHeights[i%4]*1000000f);
			map[i] = h;
		}
		
		/*
		float northOverrideDiff = cornerHeights[4];
		float southOverrideDiff = cornerHeights[6];
		float eastOverrideDiff = cornerHeights[5];
		float westOverrideDiff = cornerHeights[7];
		*/
		
		RenderedArea cache = J3DCore.getInstance().gameState.getCurrentStandingEngine().renderedArea;
		int worldX = nodePlaceholder.cube.cube.x;
		int worldY = nodePlaceholder.cube.cube.y;
		int worldZ = nodePlaceholder.cube.cube.z;
		
		//NW NE -> EAST OPPOSITE : NW (NE-1)!
		
		//SW SE -> EAST OPPOSITE : SW (SE-3)!
		
		// SOUTH ADJACENT
		// NW NE
		// (SW-2)! (SE-3)!
		
		int oppositeXDir = +1; // WEST -> EAST		
		float oppDeltaY = 0f;
		boolean oppositeGood = true;
		RenderedCube opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY, worldZ);
		if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
		{
			opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY+1, worldZ);
			oppDeltaY=1f;
			if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
			{
				opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY-1, worldZ);
				oppDeltaY=-1f;
				if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
				{
					opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY+2, worldZ);
					oppDeltaY=2f;
					if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
					{
						opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY-2, worldZ);
						oppDeltaY=-2f;
						if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
						{
							oppositeGood = false;
						}
					} 
				}
			}
		}

		int adjacentZDir = -1; // NORTH -> SOUTH		
		float adjDeltaY = 0f;
		boolean adjacentGood = true;
		RenderedCube adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY, worldZ+adjacentZDir);
		if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null || !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
		{
			adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY+1, worldZ+adjacentZDir);
			adjDeltaY=1f;
			if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null|| !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
			{
				adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY-1, worldZ+adjacentZDir);
				adjDeltaY=-1f;
				if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null || !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
				{
					adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY+2, worldZ+adjacentZDir);
					adjDeltaY=2f;
					if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null|| !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
					{
						adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY-2, worldZ+adjacentZDir);
						adjDeltaY=-2f;
						if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null|| !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
						{
							adjacentGood = false;
						}
					} 
				}
			}
		} 
		
		int[] bigMap = new int[9];
		for (int row = 0; row<3; row++ )
		{
			for (int col = 0; col<3; col++)
			{
				if (col <2 && row<2)
				{
					bigMap[row*3+col] = map[row*2+col]; 
				} else
				{
					if (row ==0)
					{
						if (oppositeGood)
						{
							bigMap[row*3+col] = (int)((opposite.cube.cornerHeights[1]+oppDeltaY)*1000000f);
						} else
						{
							//System.out.println("NO OPP");
						}
					} else
					if (row ==1)
					{
						if (oppositeGood)
						{
							bigMap[row*3+col] = (int)((opposite.cube.cornerHeights[3]+oppDeltaY)*1000000f);
						}
						else
						{
							//System.out.println("NO OPP");
						}
					} else
					if (row == 2)
					{
						if (col==0)
						{
							if (adjacentGood)
							{
								bigMap[row*3+col] = (int)((adjacent.cube.cornerHeights[2]+adjDeltaY)*1000000f);
							}
							else
							{
								//System.out.println("NO ADJ");
							}
						} else
						if (col==1)
						{
							if (adjacentGood)
							{
								bigMap[row*3+col] = (int)((adjacent.cube.cornerHeights[3]+adjDeltaY)*1000000f);
							}
							else
							{
								//System.out.println("NO ADJ");
							}
						}
					}
				}
			}
		}
		
		TiledTerrainBlock block = new TiledTerrainBlock("1",2,new Vector3f(2,0.0000020f,2),map,bigMap,new Vector3f(0f,0,0f),false);
		
		SimpleModel o = model;
		Spatial spatial = block;
		if (o.textureName!=null)
		{
			Texture texture = (Texture)ModelLoader.textureCache.get(o.textureName);
			
			if (texture==null) {
				texture = TextureManager.loadTexture("./data/textures/"+l.TEXDIR+o.textureName,Texture.MM_LINEAR,
	                    Texture.FM_LINEAR);

				texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
				texture.setApply(Texture.AM_MODULATE);
				texture.setRotation(J3DCore.qTexture);
				ModelLoader.textureCache.put(o.textureName, texture);
			}

			TextureState ts = l.core.getDisplay().getRenderer().createTextureState();
			ts.setTexture(texture, 0);
			
            ts.setEnabled(true);
            spatial.setRenderState(ts);
			
		} else 
		{
			Jcrpg.LOGGER.info(o.modelName);
			//setTextures(node,o.mipMap);
		}
		
		return block;
	}
}
