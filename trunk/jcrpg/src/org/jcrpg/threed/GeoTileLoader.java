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

import java.net.URL;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.ModelPool.PoolItemContainer;
import org.jcrpg.threed.jme.TiledTerrainBlock;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.model.SimpleModel;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.PassNode;
import com.jme.scene.PassNodeState;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;

public class GeoTileLoader {
	AlphaState as2; 
	AlphaState as;
	ModelLoader l = null;
	public GeoTileLoader(ModelLoader l)
	{
		this.l = l;
        as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setTestEnabled(true);
        as.setTestFunction(AlphaState.TF_GREATER);
        as.setEnabled(true);
        // alpha used for blending the lightmap
        as2 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as2.setBlendEnabled(true);
        as2.setSrcFunction(AlphaState.SB_DST_COLOR);
        as2.setDstFunction(AlphaState.DB_SRC_COLOR);
        as2.setTestEnabled(true);
        as2.setTestFunction(AlphaState.TF_GREATER);
        as2.setEnabled(true);
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

		public void update(NodePlaceholder place) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class PooledPassNode extends PassNode implements PooledNode
	{

		TiledTerrainBlock block;
		public PooledPassNode(TiledTerrainBlock block) {
			super();
			this.block = block;
		}

		public PooledPassNode(String arg0,TiledTerrainBlock block) {
			super(arg0);
			this.block = block;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		PoolItemContainer pic;
		public PoolItemContainer getPooledContainer() {
			
			return pic;
		}

		public void setPooledContainer(PoolItemContainer cont) {
			pic = cont;			
		}

		public void update(NodePlaceholder place) {
			if (place.neighborCubeData==null)
			{
				place.neighborCubeData = getNeighborCubes(place);
			}
			int[][] maps = getHeightMaps(place);
			block.setHeightMaps(maps);
			block.updateFromHeightMap();			
		}
		
	}
	
	public PooledNode loadNode(NodePlaceholder nodePlaceholder)
	{
		TiledTerrainBlockAndPassNode block = loadNodeOriginal(nodePlaceholder, true);
		block.passNode.attachChild(block.block);
		return (PooledNode)block.passNode;
	}

	public static boolean checkHeightOpposite(float[] cornerHeights, float[] opp, float oppDeltaY)
	{
		if (Math.abs(cornerHeights[1]-(opp[0]+oppDeltaY))>0.1f) return false;
		//if (Math.abs(cornerHeights[3]-(opp[2]+oppDeltaY))>0.1f) return false;
		return true;
		
	}
	public static boolean checkHeightAdj(float[] cornerHeights, float[] adj, float adjDeltaY)
	{
		if (Math.abs(cornerHeights[2]-(adj[0]+adjDeltaY))>0.1f) return false;
		//if (Math.abs(cornerHeights[3]-(adj[1]+adjDeltaY))>0.1f) return false;
		return true;
		
	}
	public static boolean checkHeightOppAdj(float[] cornerHeights, float[] adj, float adjDeltaY)
	{
		if (Math.abs(cornerHeights[3]-(adj[0]+adjDeltaY))>0.1f) return false;
		return true;
		
	}
	
	public static NeighborCubeData getNeighborCubes(NodePlaceholder nodePlaceholder)
	{
		float[] cornerHeights = nodePlaceholder.cube.cube.cornerHeights;
		if (cornerHeights==null) cornerHeights = new float[9];
		RenderedArea cache = J3DCore.getInstance().gameState.getCurrentStandingEngine().renderedArea;
		int worldX = nodePlaceholder.cube.cube.x;
		int worldY = nodePlaceholder.cube.cube.y;
		int worldZ = nodePlaceholder.cube.cube.z;
		//NW NE -> EAST OPPOSITE : NW (NE-1)!
		
		//SW SE -> EAST OPPOSITE : SW (SE-3)!
		
		// SOUTH ADJACENT
		// NW NE
		// (SW-2)! (SE-3)!
		
		int yGap = (nodePlaceholder.farView?J3DCore.FARVIEW_GAP:1);
		
		int oppositeXDir = +1*(nodePlaceholder.farView?J3DCore.FARVIEW_GAP:1); // WEST -> EAST		
		float oppDeltaY = 0f;
		//boolean oppositeGood = true;
		RenderedCube opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY, worldZ, nodePlaceholder.farView);
		if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
		{
			opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY+1*yGap, worldZ, nodePlaceholder.farView);
			oppDeltaY=1f*yGap;
			if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
			{
				opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY-1*yGap, worldZ, nodePlaceholder.farView);
				oppDeltaY=-1f*yGap;
				if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
				{
					opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY+2*yGap, worldZ, nodePlaceholder.farView);
					oppDeltaY=2f;
					if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
					{
						opposite = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppositeXDir, worldY-2*yGap, worldZ, nodePlaceholder.farView);
						oppDeltaY=-2f*yGap;
						if (opposite==null || opposite.cube==null || opposite.cube.cornerHeights==null || !checkHeightOpposite(cornerHeights, opposite.cube.cornerHeights, oppDeltaY))
						{
							//oppositeGood = false;
							opposite = null;
						}
					} 
				}
			}
		}

		int adjacentZDir = -1*(nodePlaceholder.farView?J3DCore.FARVIEW_GAP:1); // NORTH -> SOUTH		
		float adjDeltaY = 0f;
		//boolean adjacentGood = true;
		RenderedCube adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY, worldZ+adjacentZDir, nodePlaceholder.farView);
		if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null || !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
		{
			adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY+yGap, worldZ+adjacentZDir, nodePlaceholder.farView);
			adjDeltaY=yGap;
			if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null|| !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
			{
				adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY-yGap, worldZ+adjacentZDir, nodePlaceholder.farView);
				adjDeltaY=-yGap;
				if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null || !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
				{
					adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY+2*yGap, worldZ+adjacentZDir, nodePlaceholder.farView);
					adjDeltaY=2*yGap;
					if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null|| !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
					{
						adjacent = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX, worldY-2*yGap, worldZ+adjacentZDir, nodePlaceholder.farView);
						adjDeltaY=-2*yGap;
						if (adjacent==null || adjacent.cube==null || adjacent.cube.cornerHeights==null|| !checkHeightAdj(cornerHeights, adjacent.cube.cornerHeights, adjDeltaY))
						{
							//adjacentGood = false;
							adjacent = null;
						}
					} 
				}
			}
		} 
		RenderedCube oppAdj = null;
		float oppAdjDeltaY = 0f;
		if (J3DCore.TEXTURE_SPLATTING)
		{
			
			int oppAdjXDir = +1*(nodePlaceholder.farView?J3DCore.FARVIEW_GAP:1); // WEST -> EAST		
			int oppAdjZDir = -1*(nodePlaceholder.farView?J3DCore.FARVIEW_GAP:1); // WEST -> EAST
			
			//boolean oppAdjGood = true;
			oppAdj = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppAdjXDir, worldY, worldZ+oppAdjZDir, nodePlaceholder.farView);
			if (oppAdj==null || oppAdj.cube==null || oppAdj.cube.cornerHeights==null || !checkHeightOppAdj(cornerHeights, oppAdj.cube.cornerHeights, oppAdjDeltaY))
			{
				oppAdj = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppAdjXDir, worldY+yGap, worldZ+oppAdjZDir, nodePlaceholder.farView);
				oppAdjDeltaY=yGap;
				if (oppAdj==null || oppAdj.cube==null || oppAdj.cube.cornerHeights==null || !checkHeightOppAdj(cornerHeights, oppAdj.cube.cornerHeights, oppAdjDeltaY))
				{
					oppAdj = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppAdjXDir, worldY-yGap, worldZ+oppAdjZDir, nodePlaceholder.farView);
					oppAdjDeltaY=-yGap;
					if (oppAdj==null || oppAdj.cube==null || oppAdj.cube.cornerHeights==null || !checkHeightOppAdj(cornerHeights, oppAdj.cube.cornerHeights, oppAdjDeltaY))
					{
						oppAdj = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppAdjXDir, worldY+2*yGap, worldZ+oppAdjZDir, nodePlaceholder.farView);
						oppAdjDeltaY=2*yGap;
						if (oppAdj==null || oppAdj.cube==null || oppAdj.cube.cornerHeights==null || !checkHeightOppAdj(cornerHeights, oppAdj.cube.cornerHeights, oppAdjDeltaY))
						{
							oppAdj = cache.getCubeAtPosition(nodePlaceholder.cube.world, worldX+oppAdjXDir, worldY-2*yGap, worldZ+oppAdjZDir, nodePlaceholder.farView);
							oppAdjDeltaY=-2*yGap;
							if (oppAdj==null || oppAdj.cube==null || oppAdj.cube.cornerHeights==null || !checkHeightOppAdj(cornerHeights, oppAdj.cube.cornerHeights, oppAdjDeltaY))
							{
								//oppAdjGood = false;
								oppAdj = null;
							}
						} 
					}
				}
			}
		}
		
		
		return new NeighborCubeData(nodePlaceholder.cube,opposite,adjacent,oppAdj,oppDeltaY,adjDeltaY,oppAdjDeltaY);
	}
	
	public static class NeighborCubeData
	{
		public RenderedCube opposite;
		public RenderedCube adjacent;
		public RenderedCube oppAdj;
		public float oppositeDeltaY;
		public float adjacentDeltaY;
		public float oppAdjDeltaY;
		
		public String ownGroundTexture = null;
		public String adjacentGroundTexture = null;
		public String oppositeGroundTexture = null;
		public String oppAdjGroundTexture = null;

		public String adjacentGroundBaseTexture = null;
		public String oppositeGroundBaseTexture = null;
		public String oppAdjGroundBaseTexture = null;

		public String[] getGeneratedGeoTileTextureNameFromCube(RenderedCube c)
		{
			if (c==null) return null;
			for (NodePlaceholder n:c.hsRenderedNodes)
			{
				if (n.model!=null && n.model.type == SimpleModel.SIMPLEMODEL)
				{
					SimpleModel sm = (SimpleModel)n.model;
					if (sm.generatedGroundModel)
					{
						return new String[]{sm.getBlendTextureKey(n),sm.getTexture(n)};
					}
				}
			}
			return null;
		}
		
		String textureKeyPart = null;
		
		public NeighborCubeData(RenderedCube original, RenderedCube opposite, RenderedCube adjacent,
				RenderedCube oppAdj, float oppositeDeltaY,
				float adjacentDeltaY, float oppAdjDeltaY) {
			super();
			this.opposite = opposite;
			this.adjacent = adjacent;
			this.oppAdj = oppAdj;
			String[] s = null;
			s = getGeneratedGeoTileTextureNameFromCube(original);
			String originalTexture = s==null?null:s[1];
			String originalBaseTexture = s==null?null:s[0];
			ownGroundTexture = originalTexture;
			if (J3DCore.TEXTURE_SPLATTING && !original.farview)
			{
				// TODO with farview the generated ground tiles are not correctly removed from
				// scenario. This 'if' above can be removed if solution found...
				// Seems like they are wrongly positioned, generated in farview???
				
				s = getGeneratedGeoTileTextureNameFromCube(opposite);
				if (s!=null)
				{
					oppositeGroundTexture = s[1];
					oppositeGroundBaseTexture = s[0];
				}
				s = getGeneratedGeoTileTextureNameFromCube(adjacent);
				if (s!=null)
				{
					adjacentGroundTexture = s[1];
					adjacentGroundBaseTexture = s[0];
				}
				s = getGeneratedGeoTileTextureNameFromCube(oppAdj);
				if (s!=null)
				{
					oppAdjGroundTexture = s[1];
					oppAdjGroundBaseTexture = s[0];
				}
				if (oppAdjGroundBaseTexture!=null && !originalBaseTexture.equals(oppAdjGroundBaseTexture) || 
						oppositeGroundBaseTexture!=null && !originalBaseTexture.equals(oppositeGroundBaseTexture) || 
						adjacentGroundBaseTexture!=null && !originalBaseTexture.equals(adjacentGroundBaseTexture) )
				{
					textureKeyPart=oppositeGroundTexture+adjacentGroundTexture+oppAdjGroundTexture;
				}
			}
			this.oppositeDeltaY = oppositeDeltaY;
			this.adjacentDeltaY = adjacentDeltaY;
			this.oppAdjDeltaY = oppAdjDeltaY;
		}
		
		public String getTextureKeyPartForBatch()
		{
			
			return textureKeyPart;
		}
		
	}
	
	public class TiledTerrainBlockAndPassNode
	{
		public TiledTerrainBlock block;
		public PassNode passNode;
		public TiledTerrainBlockAndPassNode(TiledTerrainBlock block,
				PassNode passNode) {
			super();
			this.block = block;
			this.passNode = passNode;
		}
		
	}
	
	public int[][] getHeightMaps(NodePlaceholder nodePlaceholder)
	{
		SimpleModel model = (SimpleModel)nodePlaceholder.model;
		RenderedCube rCube = nodePlaceholder.cube;
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
		
		
		NeighborCubeData data = nodePlaceholder.neighborCubeData;//getNeighborCubes(nodePlaceholder);
		//if (data.getTextureKeyPartForBatch()!=null) System.out.println(data.getTextureKeyPartForBatch());
		RenderedCube opposite = data.opposite;
		RenderedCube adjacent = data.adjacent;
		float oppDeltaY = data.oppositeDeltaY;
		float adjDeltaY = data.adjacentDeltaY;
		//float oppAdjDeltaY = data.oppAdjDeltaY;
		//RenderedCube oppAadjacent = cubes[1];
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
						if (opposite!=null)
						{
							bigMap[row*3+col] = (int)((opposite.cube.cornerHeights[1]+oppDeltaY)*1000000f);
						} else
						{
							//System.out.println("NO OPP");
						}
					} else
					if (row ==1)
					{
						if (opposite!=null)
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
							if (adjacent!=null)
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
							if (adjacent!=null)
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
		return new int[][]{map,bigMap};
		
	}
	public TiledTerrainBlockAndPassNode loadNodeOriginal(NodePlaceholder nodePlaceholder, boolean splatNodeNeeded)
	{

		NeighborCubeData data = nodePlaceholder.neighborCubeData;//getNeighborCubes(nodePlaceholder);
		
		int[][] heightMaps = getHeightMaps(nodePlaceholder);
		
		TiledTerrainBlock block = new TiledTerrainBlock("1",2,new Vector3f(2,0.0000020f,2),heightMaps[0],heightMaps[1],new Vector3f(0f,0,0f),false);
		SimpleModel model = (SimpleModel)nodePlaceholder.model;
		RenderedCube rCube = nodePlaceholder.cube;
		
		String ownTexture = data.ownGroundTexture;
		String oppositeTexture = data.oppositeGroundTexture;
		String adjacentTexture = data.adjacentGroundTexture;
		String oppAdjTexture = data.oppAdjGroundTexture;
		
		
		SimpleModel o = model;
		Spatial spatial = block;
		
		PassNode splattingPassNode = null;
		
		if (ownTexture!=null)
		{
			if (data.getTextureKeyPartForBatch()==null)
			{
			
				Texture texture = (Texture)ModelLoader.textureCache.get(ownTexture);
				
				if (texture==null) {
					texture = TextureManager.loadTexture("./data/textures/"+l.TEXDIR+ownTexture,Texture.MM_LINEAR,
		                    Texture.FM_LINEAR);
	
					texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
					texture.setApply(Texture.AM_MODULATE);
					texture.setRotation(J3DCore.qTexture);
					ModelLoader.textureCache.put(ownTexture, texture);
				}
	
				TextureState ts = l.core.getDisplay().getRenderer().createTextureState();
				ts.setTexture(texture, 0);
				
	            ts.setEnabled(true);
	            spatial.setRenderState(ts);
			} else
			if (splatNodeNeeded)
			{
				splattingPassNode = new PooledPassNode("SplatPassNode",block);
				//l.core.gameState.getCurrentStandingEngine().extRootNode
			    TextureState ts1 = createSplatTextureState(
			    		ownTexture, null);

		        TextureState tsOpp = null;
		        
		        if (oppositeTexture!=null && !oppositeTexture.equals(ownTexture)) {
			        tsOpp = createSplatTextureState(
			                oppositeTexture,
			                "blendAlphaAdj1a.png");
		        }
		        TextureState tsAdj = null;
		        
		        if (adjacentTexture!=null && !adjacentTexture.equals(ownTexture)) {
		        	tsAdj = createSplatTextureState(
			        		adjacentTexture,
			                "blendAlphaOpp1a.png");
		        }
		        TextureState tsOppAdj = null;
		        
		        if (oppAdjTexture!=null && !oppAdjTexture.equals(ownTexture)) {
		        	tsOppAdj = createSplatTextureState(
			        		oppAdjTexture,
			                "blendAlphaOppAdj1.png");
		        }
			        //TextureState ts6 = createLightmapTextureState("./data/test/lightmap.jpg");


			        PassNodeState passNodeState = new PassNodeState();
			        passNodeState.setPassState(ts1);
			        splattingPassNode.addPass(passNodeState);
			        
			        if (tsOpp!=null)
			        {
				        passNodeState = new PassNodeState();
				        passNodeState.setPassState(tsOpp);
				        passNodeState.setPassState(as);
				        splattingPassNode.addPass(passNodeState);
			        }
			        if (tsAdj!=null)
			        {
				        passNodeState = new PassNodeState();
				        passNodeState.setPassState(tsAdj);
				        passNodeState.setPassState(as);
				        splattingPassNode.addPass(passNodeState);
			        }
			        if (tsOppAdj!=null)
			        {
				        passNodeState = new PassNodeState();
				        passNodeState.setPassState(tsOppAdj);
				        passNodeState.setPassState(as);
				        splattingPassNode.addPass(passNodeState);
			        }

			        /*passNodeState = new PassNodeState();
			        passNodeState.setPassState(ts6);
			        passNodeState.setPassState(as2);
			        splattingPassNode.addPass(passNodeState);*/
			        // //////////////////// PASS STUFF END

			        // lock some things to increase the performance
			        splattingPassNode.lockBounds();
			        splattingPassNode.lockTransforms();
			        splattingPassNode.lockShadows();

			        block.copyTextureCoords(0, 0, 1, 0.999f);
			}
			
		} else 
		{
			Jcrpg.LOGGER.info(o.modelName);
			//setTextures(node,o.mipMap);
		}
		
		return new TiledTerrainBlockAndPassNode(block,splattingPassNode);//splattingPassNode);
	}
	
	public Texture loadTexture(String textureName)
	{
		Texture texture = (Texture)ModelLoader.textureCache.get(textureName);
		
		if (texture==null) {
			URL u = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, textureName);
			if (u==null)return null;
			texture = TextureManager.loadTexture(u,Texture.MM_LINEAR,
                    Texture.FM_LINEAR);

			texture.setWrap(Texture.WM_WRAP_S_WRAP_T);
			texture.setApply(Texture.AM_COMBINE);
			//texture.setCombineFuncAlpha(combineFuncAlpha)
			texture.setRotation(J3DCore.qTexture);
			ModelLoader.textureCache.put(textureName, texture);
		}
		return texture;
	}
	
	public HashMap<String, TextureState> stateCache = new HashMap<String, TextureState>();
	public HashMap<String, Texture> alphaTextureCache = new HashMap<String, Texture>();
	public HashMap<String, Texture> splatTextureCache = new HashMap<String, Texture>();
	
    private TextureState createSplatTextureState(String texture, String alpha) {
    	String key = texture+alpha;
		TextureState state = stateCache.get(key);
		if (state == null)
		{
	        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	
	    	Texture t0 = splatTextureCache.get(texture);
	    	if (t0==null)
	    	{
		        URL u = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, texture);
		        t0 = TextureManager.loadTexture(u,
		                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		        //t0.setWrap(Texture.WM_CLAMP_S_CLAMP_T);//WM_WRAP_S_WRAP_T);
		        t0.setApply(Texture.AM_MODULATE);
		        //t0.setScale(new Vector3f(1f, 1f, 1.0f));
		        splatTextureCache.put(texture, t0);
	    	} 
	        ts.setTexture(t0, 0);
	
	        if (alpha != null) {
	            addAlphaSplat(ts, alpha);
	        }
	        state = ts;
	        stateCache.put(key, state);
		}

        return state;
    }

    private void addAlphaSplat(TextureState ts, String alpha) {
    	Texture t1 = alphaTextureCache.get(alpha);
    	if (t1==null)
    	{
        	URL u = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, alpha);
        	if (u!=null)
        	{
        		
    	        t1 = TextureManager.loadTexture(u, Texture.MM_LINEAR_LINEAR,
    	                Texture.FM_LINEAR);
    	        //t1.setWrap(Texture.WM_WRAP_S_WRAP_T);
    	        t1.setApply(Texture.AM_COMBINE);
    	        t1.setCombineFuncRGB(Texture.ACF_REPLACE);
    	        t1.setCombineSrc0RGB(Texture.ACS_PREVIOUS);
    	        t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
    	        t1.setCombineFuncAlpha(Texture.ACF_REPLACE);
        	}
        	alphaTextureCache.put(alpha, t1);
    	}
        ts.setTexture(t1, ts.getNumberOfSetTextures());
    }

    public static final float getHeight(Vector3f displacementFromCenter, NodePlaceholder place)
    {
    	float x = (displacementFromCenter.x + J3DCore.CUBE_EDGE_SIZE/2f)/J3DCore.CUBE_EDGE_SIZE;
    	float z = -(displacementFromCenter.z - J3DCore.CUBE_EDGE_SIZE/2f)/J3DCore.CUBE_EDGE_SIZE;
    	return getHeight(x, z, place);
    }

    public static final float getHeight(float xPerc, float zPerc, NodePlaceholder place)
    {
		float NW = place.cube.cube.cornerHeights[0];
		float NE = place.cube.cube.cornerHeights[1];
		float SW = place.cube.cube.cornerHeights[2];
		float SE = place.cube.cube.cornerHeights[3];
		float heightPercent = 
			(
			( NW * ((     xPerc  +      zPerc) / 2f) ) +
			( NE * ((1f - xPerc  +      zPerc) / 2f) ) +
			( SW * ((     xPerc  + 1f - zPerc) / 2f) ) +
			( SE * ((1f - xPerc  + 1f - zPerc) / 2f) )
			)
			;
		return heightPercent;

    }
	
}


