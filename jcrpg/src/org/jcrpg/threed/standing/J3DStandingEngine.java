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

package org.jcrpg.threed.standing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.ModelLoader;
import org.jcrpg.threed.ModelPool;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.PooledSharedNode;
import org.jcrpg.threed.jme.ModelGeometryBatch;
import org.jcrpg.threed.jme.TrimeshGeometryBatch;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;
import org.jcrpg.threed.scene.config.SideTypeModels;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.side.RenderedClimateDependentSide;
import org.jcrpg.threed.scene.side.RenderedContinuousSide;
import org.jcrpg.threed.scene.side.RenderedHashAlteredSide;
import org.jcrpg.threed.scene.side.RenderedHashRotatedSide;
import org.jcrpg.threed.scene.side.RenderedSide;
import org.jcrpg.threed.scene.side.RenderedTopSide;
import org.jcrpg.ui.UIBase;
import org.jcrpg.world.Engine;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.time.Time;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.Spatial;

/**
 * Static elements 3d display part.
 * @author pali
 *
 */
public class J3DStandingEngine {

	public J3DCore core;
	public ModelLoader modelLoader;
	public ModelPool modelPool;
	public UIBase uiBase;
	public Engine engine;
	public World world;
	public RenderedArea renderedArea;
	
	public Node intRootNode = null;
	public Node extRootNode = null;
	
	public J3DStandingEngine(J3DCore core)
	{
		this.core = core;
		this.modelLoader = core.modelLoader;
		this.uiBase = core.uiBase;
		this.engine = core.gameState.engine;
		this.world = core.gameState.world;
		renderedArea = core.renderedArea;
		modelPool = core.modelPool;
		intRootNode = core.intRootNode;
		extRootNode = core.extRootNode;
	}
	/**
	 * Start this when newly initializing core.
	 */
	public void reinit()
	{
		this.uiBase = core.uiBase;
		this.engine = core.gameState.engine;
		this.world = core.gameState.world;
		renderedArea = core.renderedArea;
		modelPool = core.modelPool;
		intRootNode = core.intRootNode;
		extRootNode = core.extRootNode;
	}

	HashMap<Long, RenderedCube> hmCurrentCubes = new HashMap<Long, RenderedCube>();
	ArrayList<RenderedCube> alCurrentCubes = new ArrayList<RenderedCube>();
	
	HashMap<Long, RenderedCube> hmCurrentCubes_FARVIEW = new HashMap<Long, RenderedCube>();
	ArrayList<RenderedCube> alCurrentCubes_FARVIEW = new ArrayList<RenderedCube>();
	
	
	/**
	 * Renders the scenario, adds new jme Nodes, removes outmoved nodes and keeps old nodes on scenario.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<RenderedCube>[] render(int viewPositionX, int viewPositionY, int viewPositionZ, boolean rerender)
	{
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("RENDERING...");
		HashSet<RenderedCube> detacheable = new HashSet<RenderedCube>();
		HashSet<RenderedCube> detacheable_FARVIEW = new HashSet<RenderedCube>();
		//modelLoader.setLockForSharedNodes(false);
    	//loadingText(0,true);
		
		uiBase.hud.sr.setVisibility(true, "LOAD");
		uiBase.hud.mainBox.addEntry("Loading Geo at X/Z "+core.gameState.getCurrentRenderPositions().viewPositionX+"/"+core.gameState.getCurrentRenderPositions().viewPositionZ+"...");
    	if (!J3DCore.CONTINUOUS_LOAD) core.updateDisplay(null);
		//core.do3DPause(true);

		/*lastRenderX = viewPositionX;
		lastRenderY = viewPositionY;
		lastRenderZ = viewPositionZ;*/
    	core.lastRenderX = core.gameState.getCurrentRenderPositions().relativeX;
    	core.lastRenderY = core.gameState.getCurrentRenderPositions().relativeY;
    	core.lastRenderZ = core.gameState.getCurrentRenderPositions().relativeZ;

		// start to collect the nodes/binaries which this render will use now
		modelLoader.startRender();
		
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("**** RENDER ****");
		

		Time localTime = engine.getWorldMeanTime().getLocalTime(world, viewPositionX, viewPositionY, viewPositionZ);
		CubeClimateConditions conditions = world.climate.getCubeClimate(localTime, viewPositionX, viewPositionY, viewPositionZ, false);
		
		
		if (conditions!=null) if (J3DCore.LOGGING) Jcrpg.LOGGER.info("- "+conditions.getBelt()+" \n - "+ conditions.getSeason()+" \n"+ conditions.getDayTime());

		
		/*
		 * Render cubes
		 */
		
    	// get a specific part of the area to render
		long time = System.currentTimeMillis();
		RenderedCube[][] newAndOldCubes = renderedArea.getRenderedSpace(world, viewPositionX, viewPositionY, viewPositionZ,core.gameState.getCurrentRenderPositions().viewDirection, J3DCore.FARVIEW_ENABLED,rerender);
    	if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("RENDER AREA TIME: "+ (System.currentTimeMillis()-time));
    	
    	RenderedCube[] cubes = newAndOldCubes[0];
    	RenderedCube[] removableCubes = newAndOldCubes[1];
		
    	detacheable = doRender(cubes, removableCubes, hmCurrentCubes);
    	if (J3DCore.FARVIEW_ENABLED)
    	{
        	cubes = newAndOldCubes[2];
        	removableCubes = newAndOldCubes[3];
        	detacheable_FARVIEW = doRender(cubes, removableCubes, hmCurrentCubes_FARVIEW);
    	}
    	

		//modelLoader.setLockForSharedNodes(true);
		
		// stop to collect and clean the nodes/binaries which this render will not use now
		modelLoader.stopRenderAndClear();

    	//updateDisplay(null);

		//TextureManager.clearCache();
		//System.gc();
		//core.do3DPause(false);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info(" ######################## LIVE NODES = "+liveNodes + " --- LIVE HM QUADS "+J3DCore.hmSolidColorSpatials.size());
		uiBase.hud.sr.setVisibility(false, "LOAD");
		uiBase.hud.mainBox.addEntry("Load Complete.");
		HashSet<RenderedCube>[] ret = new HashSet[] {detacheable,detacheable_FARVIEW};
		return ret;
	}
	
	/**
	 * Renders all the cubes' sides' placeholders with renderSide.
	 * @param cubes
	 * @param removableCubes
	 * @param hmCurrentCubes
	 * @return
	 */
	public HashSet<RenderedCube> doRender(RenderedCube[] cubes,RenderedCube[] removableCubes, HashMap<Long, RenderedCube> hmCurrentCubes)
	{
		int already = 0;
		int newly = 0;
		int removed = 0;
 		long timeS = System.currentTimeMillis();

 		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("1-RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));
		HashSet<RenderedCube> detacheable = new HashSet<RenderedCube>();
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("!!!! REMOVABLE CUBES = "+removableCubes.length);
    	for (RenderedCube c:removableCubes)
    	{
    		if (c==null) continue;
    		Long cubeKey = Boundaries.getKey(c.cube.x,c.cube.y,c.cube.z);
    		c = hmCurrentCubes.remove(cubeKey);
    		if (c!=null && c.hsRenderedNodes!=null) {
    			detacheable.add(c);
    			liveNodes-= c.hsRenderedNodes.size();
    		}
    	}
    	
    	if (J3DCore.LOGGING) Jcrpg.LOGGER.info("1-RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));

    	if (J3DCore.LOGGING) Jcrpg.LOGGER.info("getRenderedSpace size="+cubes.length);
		
		//HashMap<Long, RenderedCube> hmNewCubes = new HashMap<Long, RenderedCube>();

		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("hmCurrentCubes: "+hmCurrentCubes.keySet().size());
		
	    for (int i=0; i<cubes.length; i++)
		{
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.info("CUBE "+i);
			RenderedCube c = cubes[i];
			Long cubeKey = Boundaries.getKey(c.cube.x,c.cube.y,c.cube.z);
			if (hmCurrentCubes.containsKey(cubeKey))
			{
				already++;
				// yes, we have it rendered...
				continue;				
			}
			newly++;
			// render the cube newly
			Side[][] sides = c.cube.sides;
			for (int j=0; j<sides.length; j++)
			{
				if (sides[j]!=null) {
					for (int k=0; k<sides[j].length; k++) {
						renderSide(c,c.renderedX, c.renderedY, c.renderedZ, j, sides[j][k],false); // fake = false !
					}
				}
			}
			// store it to new cubes hashmap
			hmCurrentCubes.put(cubeKey,c);
		}
	    if (J3DCore.LOGGING) Jcrpg.LOGGER.info("hmCurrentCubes: "+hmCurrentCubes.keySet().size());
	    for (RenderedCube cToDetach:detacheable)
	    {
			removed++;
    		if (!cToDetach.farview) {
    			inViewPort.remove(cToDetach);
        		outOfViewPort.remove(cToDetach);
    		}
    		if (cToDetach.farview) {
    			inFarViewPort.remove(cToDetach);
        		outOfFarViewPort.remove(cToDetach);
    		}
	    }
	    if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("RSTAT = N"+newly+" A"+already+" R"+removed+" -- time: "+(System.currentTimeMillis()-timeS));
		return detacheable;
	}

	/**
	 * Renders a set of node into 3d space, rotating, positioning them.
	 * @param n Nodes
	 * @param cube the r.cube parent of the nodes, needed for putting the rendered node as child into it.
	 * @param x X cubesized distance from current relativeX
	 * @param y Y cubesized distance from current relativeY
	 * @param z Z cubesized distance from current relativeZ
	 * @param direction Direction
	 * @param horizontalRotation Horizontal rotation
	 * @param scale Scale
	 */
	private void renderNodes(NodePlaceholder[] n, RenderedCube cube, int x, int y, int z, int direction, int horizontalRotation, float scale)
	{
		
		if (n==null) return;
		Object[] f = (Object[])J3DCore.directionAnglesAndTranslations.get(direction);
		float cX = ((x+core.gameState.getCurrentRenderPositions().relativeX)*J3DCore.CUBE_EDGE_SIZE+1f*((int[])f[1])[0]*(cube.farview?J3DCore.FARVIEW_GAP:1));//+0.5f;
		float cY = ((y+core.gameState.getCurrentRenderPositions().relativeY)*J3DCore.CUBE_EDGE_SIZE+1f*((int[])f[1])[1]*(cube.farview?J3DCore.FARVIEW_GAP:1));//+0.5f;
		float cZ = ((z-core.gameState.getCurrentRenderPositions().relativeZ)*J3DCore.CUBE_EDGE_SIZE+1f*((int[])f[1])[2]*(cube.farview?J3DCore.FARVIEW_GAP:1));//+25.5f;
		if (cube.farview)
		{
			cY+=J3DCore.CUBE_EDGE_SIZE*1.5f;
		}
		Quaternion hQ = null;
		Quaternion hQReal = null;
		if (horizontalRotation!=-1) {
			hQ = J3DCore.horizontalRotations.get(horizontalRotation);
			hQReal = J3DCore.horizontalRotationsReal.get(horizontalRotation);
		}
		
		//Node sideNode = new Node();
		boolean needsFarviewScale = true;
	
		for (int i=0; i<n.length; i++) {
			needsFarviewScale = true&&cube.farview;
			if (n[i].model.type == Model.PARTLYBILLBOARDMODEL) {
				needsFarviewScale = false;
			}
			n[i].setLocalTranslation(new Vector3f(cX,cY,cZ));
			Quaternion q = (Quaternion)f[0];
			Quaternion qC = null;
			if (n[i].model.noSpecialSteepRotation) {
				qC = new Quaternion(q); // base rotation
			} else
			{
				qC = new Quaternion();
			}
			if (hQ!=null)
			{
				n[i].horizontalRotation = hQReal;
				// horizontal rotation
				qC.multLocal(hQ);
			} 

			// the necessary local translation : half cube up
			//System.out.println("MH: "+cube.cube.middleHeight);
			if (!(n[i].model instanceof SimpleModel) || !((SimpleModel)n[i].model).generatedGroundModel)
			{
				if (n[i].model.elevateOnSteep)
				{
					Vector3f newTrans = n[i].getLocalTranslation().add(0f,(J3DCore.CUBE_EDGE_SIZE*cube.cube.middleHeight - J3DCore.CUBE_EDGE_SIZE * 0.15f)*(cube.farview?J3DCore.FARVIEW_GAP:1),0f);
					n[i].setLocalTranslation(newTrans);
				}
			}
			
			// steep rotation part...
			if (n[i].getUserData("rotateOnSteep")!=null) {
				// model loader did set a rotateOnSteep object, which means, that node can be rotated on a steep,
				// so let's do it if we are on a steep...

				
				if (cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP)
				{
					// yes, this is a steep:
					
					// mult with steep rotation quaternion for the steep direction...
					if (n[i].model.noSpecialSteepRotation) 
					{	try {
							qC.multLocal(J3DCore.steepRotations.get(cube.cube.steepDirection));
						}catch (Exception ex)
						{
							if (J3DCore.LOGGING) Jcrpg.LOGGER.info(cube.cube + " --- "+cube.cube.steepDirection);
						}
					} else 
					{
						qC = J3DCore.steepRotations_special.get(cube.cube.steepDirection);
					}

					// square root 2 is the scaling for that side, so we will set it depending on N-S or E-W steep direction
					if (cube.cube.steepDirection==J3DCore.NORTH||cube.cube.steepDirection==J3DCore.SOUTH)
					{
						// NORTH-SOUTH steep...
						if (n[i].model.noSpecialSteepRotation) 
						{
							n[i].setLocalScale(new Vector3f(1f,1.41421356f,1f).multLocal(needsFarviewScale?J3DCore.FARVIEW_GAP:1));
						} else
						{
							n[i].setLocalScale(new Vector3f(1.41421356f,1,1f).multLocal(needsFarviewScale?J3DCore.FARVIEW_GAP:1));							
						}
					}
					else
					{
						// EAST-WEST steep...
						n[i].setLocalScale(new Vector3f(1.41421356f,1,1f).multLocal(needsFarviewScale?scale*J3DCore.FARVIEW_GAP:1));
					}
				} else
				{
					n[i].setLocalScale(needsFarviewScale?scale*J3DCore.FARVIEW_GAP:1);
				}
			} else
			{				
				n[i].setLocalScale(needsFarviewScale?scale*J3DCore.FARVIEW_GAP:scale);
			}
			
			n[i].setLocalRotation(qC);

			cube.hsRenderedNodes.add((NodePlaceholder)n[i]);
			liveNodes++;
			
		}
	}

	
	
	
	
	HashSet<RenderedCube> inViewPort = new HashSet<RenderedCube>();
	HashSet<RenderedCube> inFarViewPort = new HashSet<RenderedCube>();
	HashSet<RenderedCube> outOfViewPort = new HashSet<RenderedCube>();
	HashSet<RenderedCube> outOfFarViewPort = new HashSet<RenderedCube>();
	
	int cullVariationCounter = 0;
	

	public boolean optimizeAngle = J3DCore.OPTIMIZE_ANGLES;
	
	/**
	 * Rendering standing nodes to viewport. Converting nodePlaceHolders to actual Nodes if they are visible and removing non-visible nodes. (Using modelPool.)
	 */
	public void renderToViewPort()
	{
		renderToViewPort(optimizeAngle?1.1f:3.14f);
	}
	public void renderToViewPort(int segmentCount, int segments)
	{
		renderToViewPort(optimizeAngle?1.1f:.14f, true, segmentCount, segments);
	}
	public void renderToViewPort(float refAngle)
	{
		renderToViewPort(refAngle, false, 0,0);
	}
	
	/**
	 * list to store the newly rendered nodes during renderToViewPort for special processing: 
	 * first render with CULL_NEVER then update it to CULL_INHERIT, plus updateRenderState call.
	 */
	public static ArrayList<Node> newNodesToSetCullingDynamic = new ArrayList<Node>();
	
	protected int fragmentedViewDivider = 4;
	
	/**
	 * Set this to true if you want a full rerender of the surroundings, renderedArea won't use it's cache and will return you all previous cubes to remove.
	 */
	public boolean rerender = false;
	/**
	 * Rendering standing nodes into viewport. Converting nodePlaceHolders to actual Nodes if they are visible. (Using modelPool.)
	 * @param refAngle
	 * @param segmented
	 * @param segmentCount
	 * @param segments
	 */
	public void renderToViewPort(float refAngle, boolean segmented, int segmentCount, int segments)
	{
		long t1 = System.currentTimeMillis(); 
		synchronized(Engine.mutex) {
			
			
			/*
			if (extRootNode!=null && extRootNode.getChildren()!=null) {
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("   -    "+ extRootNode.getChildren().size());
				//if (true == false)
				for (Spatial s:extRootNode.getChildren())
				{
					if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("CM: "+s.getCullMode());
					if (true==false && s instanceof SharedNode)
					{
						Spatial s1 = ((SharedNode)s).getChild(0);
						if (s1 instanceof TrimeshGeometryBatch)
						{
							//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(s1.getCullMode());
							//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("TRIMESH SIZE = "+((TrimeshGeometryBatch)s1).visible.size()+ " - "+((TrimeshGeometryBatch)s1).model.id);
						} else
						if (s1 instanceof ModelGeometryBatch)
						{
							//if ((GeometryBatchHelper.modelBatchMap.get(((ModelGeometryBatch)s1).key)==null))
							//	if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("ModelGeometryBatch SIZE = "+((ModelGeometryBatch)s1).visible.values().iterator().next().size()+ " "+((ModelGeometryBatch)s1).model.id + " "+((ModelGeometryBatch)s1).key+" - "+(GeometryBatchHelper.modelBatchMap.get(((ModelGeometryBatch)s1).key)==null));
							//;
						} //else
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(s1.getName()+ " "+s1);
					} //else
					//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(s.getName()+ " "+s);
				}
			}*/
			
			
			boolean storedPauseState = engine.isPause();
			engine.pauseForRendering();
			
			if (J3DCore.GEOMETRY_BATCH) core.batchHelper.unlockAll();
			
			Vector3f lastLoc = new Vector3f(core.lastRenderX*J3DCore.CUBE_EDGE_SIZE,core.lastRenderY*J3DCore.CUBE_EDGE_SIZE,core.lastRenderZ*J3DCore.CUBE_EDGE_SIZE);
			Vector3f currLoc = new Vector3f(core.gameState.getCurrentRenderPositions().relativeX*J3DCore.CUBE_EDGE_SIZE,core.gameState.getCurrentRenderPositions().relativeY*J3DCore.CUBE_EDGE_SIZE,core.gameState.getCurrentRenderPositions().relativeZ*J3DCore.CUBE_EDGE_SIZE);
			int mulWalkDist = 1;
			if (J3DCore.CONTINUOUS_LOAD && core.renderResult!=null) 
			{
				long t0 = System.currentTimeMillis();
				HashSet<RenderedCube>[] detacheable = core.renderResult;
				core.renderResult = null;

				if (true)  {
					
					for (int i=0; i<detacheable.length; i++)
					// removing the unneeded.
					for (RenderedCube c:detacheable[i]) { 
			    		if (c!=null) {
		    	    		inViewPort.remove(c);
		    	    		inFarViewPort.remove(c);
		    	    		outOfViewPort.remove(c);
			    	    	for (Iterator<NodePlaceholder> itNode = c.hsRenderedNodes.iterator(); itNode.hasNext();)
			    	    	{
			    	    		NodePlaceholder n = itNode.next();
			    				if (J3DCore.GEOMETRY_BATCH && n.model.batchEnabled && 
			    						(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
			    								|| J3DCore.GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
			    					 )
			    				{
			    					if (n!=null && n.batchInstance!=null)
			    						core.batchHelper.removeItem(c.cube.internalCube, n.model, n, n.farView);
			    				} else 
			    				{ 
									PooledNode pooledRealNode = n.realNode;
									
									n.realNode = null;
									if (pooledRealNode!=null) {
										Node realNode = (Node)pooledRealNode;
										if (J3DCore.SHADOWS) core.removeOccludersRecoursive(realNode);
										realNode.removeFromParent();
										modelPool.releaseNode(pooledRealNode);
									}
			    				}
			    				n.farView = false;
			    	    	}
			    		}
					}
				}		
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("DETACH TIME = "+(System.currentTimeMillis()-t0));
			} else
			//if (J3DCore.FARVIEW_ENABLED) mulWalkDist = 2; // if farview , more often render is added by this multiplier
			if (rerender || lastLoc.distance(currLoc)*mulWalkDist > (J3DCore.RENDER_DISTANCE*J3DCore.CUBE_EDGE_SIZE)-J3DCore.VIEW_DISTANCE)
			{
				// doing the render, getting the unneeded renderedCubes too.
				long t0 = System.currentTimeMillis();
				HashSet<RenderedCube>[] detacheable = render(core.gameState.getCurrentRenderPositions().viewPositionX,core.gameState.getCurrentRenderPositions().viewPositionY,core.gameState.getCurrentRenderPositions().viewPositionZ,rerender);
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("DO RENDER TIME : "+ (System.currentTimeMillis()-t0));

				for (int i=0; i<detacheable.length; i++)
				// removing the unneeded.
				for (RenderedCube c:detacheable[i]) { 
		    		if (c!=null) {
	    	    		inViewPort.remove(c);
	    	    		inFarViewPort.remove(c);
	    	    		outOfViewPort.remove(c);
		    	    	for (Iterator<NodePlaceholder> itNode = c.hsRenderedNodes.iterator(); itNode.hasNext();)
		    	    	{
		    	    		NodePlaceholder n = itNode.next();
		    				if (J3DCore.GEOMETRY_BATCH && n.model.batchEnabled && 
		    						(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
		    								|| J3DCore.GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
		    					 )
		    				{
		    					if (n!=null && n.batchInstance!=null)
		    						core.batchHelper.removeItem(c.cube.internalCube, n.model, n, n.farView);
		    				} else 
		    				{ 
								PooledNode pooledRealNode = n.realNode;
								
								n.realNode = null;
								if (pooledRealNode!=null) {
									Node realNode = (Node)pooledRealNode;
									if (J3DCore.SHADOWS) core.removeOccludersRecoursive(realNode);
									realNode.removeFromParent();
									modelPool.releaseNode(pooledRealNode);
								}
		    				}
		    				n.farView = false;
		    	    	}
		    		}
				}
	
			}
			
			long sysTime = System.currentTimeMillis();
			
			int visibleNodeCounter = 0;
			int nonVisibleNodeCounter = 0;
			int addedNodeCounter = 0;
			int removedNodeCounter = 0;
			
			long t2 = System.currentTimeMillis();
			
			if (segmented && segmentCount==0 || !segmented)
			{
				// clearing newly placed nodes list...
				newNodesToSetCullingDynamic.clear();

				// clearing current cubes...
				alCurrentCubes.clear();
				alCurrentCubes.addAll(hmCurrentCubes.values());
				if (J3DCore.FARVIEW_ENABLED)
				{
					alCurrentCubes_FARVIEW.clear();
					alCurrentCubes_FARVIEW.addAll(hmCurrentCubes_FARVIEW.values());
				}
			}
			int fromCubeCount = 0; int toCubeCount = alCurrentCubes.size();
			int fromCubeCount_FARVIEW = 0; int toCubeCount_FARVIEW = alCurrentCubes_FARVIEW.size();
			if (segmented)
			{
				int sSize = alCurrentCubes.size()/segments;
				fromCubeCount = sSize*segmentCount;
				toCubeCount = sSize*(segmentCount+1);
				if (toCubeCount>alCurrentCubes.size())
				{
					toCubeCount = alCurrentCubes.size();
				}
			}
			if (segmented && J3DCore.FARVIEW_ENABLED)
			{
				int sSize = alCurrentCubes_FARVIEW.size()/segments;
				fromCubeCount_FARVIEW = sSize*segmentCount;
				toCubeCount_FARVIEW = sSize*(segmentCount+1);
				if (toCubeCount_FARVIEW>alCurrentCubes_FARVIEW.size())
				{
					toCubeCount_FARVIEW = alCurrentCubes_FARVIEW.size();
				}
			}
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("ARRAY COPIES = "+(System.currentTimeMillis()-t2));

			float maxFarViewDist = (J3DCore.CUBE_EDGE_SIZE*J3DCore.CUBE_EDGE_SIZE)*J3DCore.RENDER_DISTANCE_FARVIEW*J3DCore.RENDER_DISTANCE_FARVIEW;
			float minAngleCalc = J3DCore.CUBE_EDGE_SIZE*J3DCore.CUBE_EDGE_SIZE*6;
			
			long sumAddRemoveBatch = 0;
			ModelGeometryBatch.sumBuildMatricesTime = 0;
			TrimeshGeometryBatch.sumAddItemReal = 0;
			
			for (int cc = fromCubeCount; cc<toCubeCount; cc++)
			{
				RenderedCube c = alCurrentCubes.get(cc);
				// TODO farview selection, only every 10th x/z based on coordinates -> do scale up in X/Z direction only
				if (c.hsRenderedNodes.size()>0)
				{
					boolean found = false;
					//boolean foundFar = false;
					// OPTIMIZATION: if inside and not insidecube is checked, or outside and not outsidecube -> view distance should be fragmented:
					boolean fragmentViewDist = false;
					if (c.cube!=null) {
						fragmentViewDist = c.cube.internalCube&&(!core.gameState.getCurrentRenderPositions().insideArea) || (!c.cube.internalCube)&&core.gameState.getCurrentRenderPositions().insideArea;
					}
	
					int checkDistCube = (fragmentViewDist?J3DCore.VIEW_DISTANCE/fragmentedViewDivider : J3DCore.VIEW_DISTANCE/2);
					boolean checked = false;
					int distX = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionX-c.cube.x);
					int distY = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionY-c.cube.y);
					int distZ = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionZ-c.cube.z);
					
					// handling the globe world border cube distances...
					if (distX>world.realSizeX/2)
					{
						if (core.gameState.getCurrentRenderPositions().viewPositionX<world.realSizeX/2) {
							distX = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionX - (c.cube.x - world.realSizeX) );
						} else
						{
							distX = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionX - (c.cube.x + world.realSizeX) );
						}
					}
					if (distZ>world.realSizeZ/2)
					{
						if (core.gameState.getCurrentRenderPositions().viewPositionZ<world.realSizeZ/2) {
							distZ = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionZ - (c.cube.z - world.realSizeZ) );
						} else
						{
							distZ = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionZ - (c.cube.z + world.realSizeZ) );	
						}
					}
					
					
					// checking the view distance of the cube from viewpoint
					if (distX<=checkDistCube && distY<=checkDistCube && distZ<=checkDistCube)
					{
						// inside view dist...
						checked = true;
					} else
					{
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.info("DIST X,Z: "+distX+" "+distZ);
					}
					//checked = true;
					
					
					for (NodePlaceholder n : c.hsRenderedNodes)
					{
						if (checked)
						{
							float dist = n.getLocalTranslation().distanceSquared(core.getCamera().getLocation());
	
							if (dist<minAngleCalc) {
								found = true;
								break;
							}
							Vector3f relative = n.getLocalTranslation().subtract(core.getCamera().getLocation()).normalize();
							float angle = core.getCamera().getDirection().normalize().angleBetween(relative);
							//if (J3DCore.LOGGING) Jcrpg.LOGGER.info("RELATIVE = "+relative+ " - ANGLE = "+angle);
							if (angle<refAngle) {
								found = true;
							}
							break;
						}
					}
					
					
					if (found)
					{
						visibleNodeCounter++;
						if (!inViewPort.contains(c)) 
						{
							addedNodeCounter++;
							inViewPort.add(c);
							if (inFarViewPort.contains(c))
							{
								removedNodeCounter++;							
								for (NodePlaceholder n : c.hsRenderedNodes)
								{
									if (!n.model.farViewEnabled) continue;
									if (J3DCore.GEOMETRY_BATCH && n.model.batchEnabled && 
											(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
													|| J3DCore.GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
										 )
									{
										if (n!=null) {
											long t0 = System.currentTimeMillis();
											core.batchHelper.removeItem(c.cube.internalCube, n.model, n, true);
											sumAddRemoveBatch+=System.currentTimeMillis()-t0;
										}
									} else 
									{
										PooledNode pooledRealNode = n.realNode;
										
										n.realNode = null;
										if (pooledRealNode!=null) {
											Node realNode = (Node)pooledRealNode;
											if (J3DCore.SHADOWS) core.removeOccludersRecoursive(realNode);
											realNode.removeFromParent();
											modelPool.releaseNode(pooledRealNode);
										}
									}
									n.farView = false;
								}
							}
	
							inFarViewPort.remove(c);
							outOfViewPort.remove(c);
							for (NodePlaceholder n : c.hsRenderedNodes)
							{
								n.farView = false;
								if (J3DCore.GEOMETRY_BATCH && n.model.batchEnabled && 
										(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
										//(n.model.type == Model.SIMPLEMODEL
												|| J3DCore.GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
									) 
								{
									if (n.batchInstance==null) 
									{
										long t0 = System.currentTimeMillis();
										core.batchHelper.addItem(this,c.cube.internalCube, n.model, n, false);
										sumAddRemoveBatch+=System.currentTimeMillis()-t0;
									}
								} else 
								{
									Node realPooledNode = (Node)modelPool.getModel(c, n.model, n);
									if (realPooledNode==null) continue;
									n.realNode = (PooledNode)realPooledNode;
								
									// unlock
									boolean sharedNode = false;
									if (realPooledNode instanceof SharedNode)
									{	
										realPooledNode.unlockMeshes();
										sharedNode = true;
									}
									{
										if (n.model.type==Model.PARTLYBILLBOARDMODEL)
										{
											if (realPooledNode.getChildren()!=null)
											for (Spatial s:realPooledNode.getChildren())
											{
												if (s instanceof PooledSharedNode)
												{
													s.unlockMeshes();
												}
											}
										}
										//realPooledNode.unlockMeshes();
										realPooledNode.unlockShadows();
										realPooledNode.unlockTransforms();
										realPooledNode.unlockBounds();
										realPooledNode.unlockBranch();
									}
								
									// set data from placeholder
									realPooledNode.setLocalTranslation(n.getLocalTranslation());
									// detailed loop through children, looking for TrimeshGeometryBatch preventing setting localRotation
									// on it, because its rotation is handled by the TrimeshGeometryBatch's billboarding.
									if (realPooledNode.getChildren()!=null)
									for (Spatial s:realPooledNode.getChildren()) {
										if ( (s.getType()&Node.NODE)>0 )
										{
											if (((Node)s).getChildren()!=null)
											for (Spatial s2:((Node)s).getChildren())
											{	
												if ( (s2.getType()&Node.NODE)>0 )
												{
													for (Spatial s3:((Node)s2).getChildren())
													{
														if (s3 instanceof TrimeshGeometryBatch) {
															// setting separate horizontalRotation for trimeshGeomBatch
															((TrimeshGeometryBatch)s3).horizontalRotation = n.horizontalRotation;
														}												
													}												
												}
												s2.setLocalScale(n.getLocalScale());
												if (s2 instanceof TrimeshGeometryBatch) {
													// setting separate horizontalRotation for trimeshGeomBatch
													((TrimeshGeometryBatch)s2).horizontalRotation = n.horizontalRotation;
												} else {
													s2.setLocalRotation(n.getLocalRotation());
												}
											}
										} else {
											s.setLocalRotation(n.getLocalRotation());
											s.setLocalScale(n.getLocalScale());
										}
									}
								
									if (c.cube.internalCube) {
										intRootNode.attachChild((Node)realPooledNode);
									} else 
									{
										extRootNode.attachChild((Node)realPooledNode);
									}
									realPooledNode.setCullMode(Node.CULL_NEVER);
									newNodesToSetCullingDynamic.add(realPooledNode);
									if (sharedNode)
									{	
										realPooledNode.lockMeshes();
										
									}
									{
										/*if (n.model.type==Model.PARTLYBILLBOARDMODEL)
										{
											if (realPooledNode.getChildren()!=null)
											for (Spatial s:realPooledNode.getChildren())
											{
												if (s instanceof PooledSharedNode)
												{
													//s.lockMeshes();
												}
											}
										}*/
										// you shouldnt lock meshes -> tree foliage is not moved properly from pool...
										realPooledNode.lockShadows();
										realPooledNode.lockTransforms();								
										realPooledNode.lockBranch();
										realPooledNode.lockBounds();
									}
								}
							}
						} 
					}
					else
					{
						 nonVisibleNodeCounter++;
						 if (!outOfViewPort.contains(c)) 
						 {
							removedNodeCounter++;
							outOfViewPort.add(c);
							inViewPort.remove(c);
							inFarViewPort.remove(c);
							for (NodePlaceholder n : c.hsRenderedNodes)
							{
								if (J3DCore.GEOMETRY_BATCH && n.model.batchEnabled && 
										(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
												|| J3DCore.GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
									 )
								{
									if (n!=null)
									{
										/*if (n.model.type == Model.TEXTURESTATEVEGETATION)
										{
											if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("REMOVING TEXSTATE VEG FROM VIEW: "+n.model.id);
										}*/
										long t0 = System.currentTimeMillis();
										core.batchHelper.removeItem(c.cube.internalCube, n.model, n, n.farView);
										sumAddRemoveBatch+=System.currentTimeMillis()-t0;
									}
								} else 
								{
									PooledNode pooledRealNode = n.realNode;
									
									n.realNode = null;
									if (pooledRealNode!=null) {
										Node realNode = (Node)pooledRealNode;
										if (J3DCore.SHADOWS) core.removeOccludersRecoursive(realNode);
										realNode.removeFromParent();
										modelPool.releaseNode(pooledRealNode);
									}
								}
								n.farView = false;
							}
							
						 }
					}
				}
			}
			
			if (J3DCore.FARVIEW_ENABLED)
			for (int cc = fromCubeCount_FARVIEW; cc<toCubeCount_FARVIEW; cc++)
			{
				RenderedCube c = alCurrentCubes_FARVIEW.get(cc);
				// TODO farview selection, only every 10th x/z based on coordinates -> do scale up in X/Z direction only
				if (c.hsRenderedNodes.size()>0)
				{
					//boolean found = false;
					boolean foundFar = false;
					// OPTIMIZATION: if inside and not insidecube is checked, or outside and not outsidecube -> view distance should be fragmented:
					boolean fragmentViewDist = false;
					if (c.cube!=null) {
						fragmentViewDist = c.cube.internalCube&&(!core.gameState.getCurrentRenderPositions().insideArea) || (!c.cube.internalCube)&&core.gameState.getCurrentRenderPositions().insideArea;
					}
	
					int checkDistCube = (fragmentViewDist?J3DCore.VIEW_DISTANCE/4 : J3DCore.VIEW_DISTANCE/2);
					boolean checked = false;
					int distX = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionX-c.cube.x);
					int distY = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionY-c.cube.y);
					int distZ = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionZ-c.cube.z);
					
					// handling the globe world border cube distances...
					if (distX>world.realSizeX/2)
					{
						if (core.gameState.getCurrentRenderPositions().viewPositionX<world.realSizeX/2) {
							distX = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionX - (c.cube.x - world.realSizeX) );
						} else
						{
							distX = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionX - (c.cube.x + world.realSizeX) );
						}
					}
					if (distZ>world.realSizeZ/2)
					{
						if (core.gameState.getCurrentRenderPositions().viewPositionZ<world.realSizeZ/2) {
							distZ = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionZ - (c.cube.z - world.realSizeZ) );
						} else
						{
							distZ = Math.abs(core.gameState.getCurrentRenderPositions().viewPositionZ - (c.cube.z + world.realSizeZ) );	
						}
					}
					
					
					// checking the view distance of the cube from viewpoint
					if (distX<=checkDistCube && distY<=checkDistCube && distZ<=checkDistCube)
					{
						// inside view dist...
						checked = true;
					} else
					{
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.info("DIST X,Z: "+distX+" "+distZ);
					}
					//checked = true;
					
					// this tells if a not in farview cube can be a farview cube
					// regardless its position, to cover the gap between farview part and normal view part:
					boolean farviewGapFiller = false; 
					
					if (checked && J3DCore.FARVIEW_ENABLED)
					{
						int viewDistFarViewModuloX = core.gameState.getCurrentRenderPositions().viewPositionX%J3DCore.FARVIEW_GAP;
						int viewDistFarViewModuloZ = core.gameState.getCurrentRenderPositions().viewPositionZ%J3DCore.FARVIEW_GAP;
						
						if (Math.abs(checkDistCube-distX)<=viewDistFarViewModuloX)
						{
							farviewGapFiller = true;
						}
						if (Math.abs(checkDistCube-distZ)<=viewDistFarViewModuloZ)
						{
							farviewGapFiller = true;
						}
						if (c.cube.x%J3DCore.FARVIEW_GAP==0 && c.cube.z%J3DCore.FARVIEW_GAP==0)
						{
							// this can be a gapfiller magnified farview cube.
						} else
						{
							//this cannot be
							farviewGapFiller = false;
						}
					} 
					for (NodePlaceholder n : c.hsRenderedNodes)
					{
						if (checked && !farviewGapFiller)
						{
							float dist = n.getLocalTranslation().distanceSquared(core.getCamera().getLocation());
	
							if (dist<minAngleCalc) {
								break;
							}
						} else
						{
							// check if farview enabled
							if (!J3DCore.FARVIEW_ENABLED || fragmentViewDist) break;
							float dist = n.getLocalTranslation().distanceSquared(core.getCamera().getLocation());
							if (dist>maxFarViewDist)
							{
								// out of far view
								break;
							}
							
							// enabled, we can check for the cube coordinates in between the gaps...
							if (c.cube.x%J3DCore.FARVIEW_GAP==0 && c.cube.z%J3DCore.FARVIEW_GAP==0 && c.cube.y%J3DCore.FARVIEW_GAP==0)// || c.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP)
							{
								// looking for farview enabled model on the cube...
								if (n.model.farViewEnabled)
								{								
									//if (c.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP) {
										//foundFar = false;
										//found = true;
										//break;
									//}
									// found one... checking for angle:								
									Vector3f relative = n.getLocalTranslation().subtract(core.getCamera().getLocation()).normalize();
									float angle = core.getCamera().getDirection().normalize().angleBetween(relative);
									//if (J3DCore.LOGGING) Jcrpg.LOGGER.info("RELATIVE = "+relative+ " - ANGLE = "+angle);
									if (angle<refAngle) {
										// angle is good, we can enable foundFar for this cube
										foundFar = true;
									}
									break;
								} else
								{
									// continue to check all the other nodes of the cube in this farview place.
									continue;
								}
							}
							break;
						}
					}
					
					
					// farview
					if (foundFar)
					{
						visibleNodeCounter++;
						if (!inFarViewPort.contains(c)) 
						{
							addedNodeCounter++;
							inFarViewPort.add(c);
							
							outOfFarViewPort.remove(c);
							
							// add all far view enabled model nodes to the scenario
							for (NodePlaceholder n : c.hsRenderedNodes)
							{
								if (!n.model.farViewEnabled) continue;
								n.farView = true;
								if (J3DCore.GEOMETRY_BATCH && n.model.batchEnabled && 
										(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
												|| J3DCore.GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
									) 
								{
									
									if (n.batchInstance==null)
										core.batchHelper.addItem(this,c.cube.internalCube, n.model, n, true);
								} else 
								{
									Node realPooledNode = (Node)modelPool.getModel(c, n.model, n);
									if (realPooledNode==null) continue;
									n.realNode = (PooledNode)realPooledNode;
								
									// unlock
									boolean sharedNode = false;
									if (realPooledNode instanceof SharedNode)
									{	
										realPooledNode.unlockMeshes();
										sharedNode = true;
									}
									{
										if (n.model.type==Model.PARTLYBILLBOARDMODEL)
										{
											for (Spatial s:realPooledNode.getChildren())
											{
												if (s instanceof PooledSharedNode)
												{
													s.unlockMeshes();
												}
											}
										}
										realPooledNode.unlockShadows();
										realPooledNode.unlockTransforms();
										realPooledNode.unlockBounds();
										realPooledNode.unlockBranch();
									}
								
									// set data from placeholder
									realPooledNode.setLocalTranslation(n.getLocalTranslation());
									// detailed loop through children, looking for TrimeshGeometryBatch preventing setting localRotation
									// on it, because its rotation is handled by the TrimeshGeometryBatch's billboarding.
									for (Spatial s:realPooledNode.getChildren()) {
										if ( (s.getType()&Node.NODE)>0 )
										{
											for (Spatial s2:((Node)s).getChildren())
											{	
												if ( (s2.getType()&Node.NODE)>0 )
												{
													for (Spatial s3:((Node)s2).getChildren())
													{
														if (s3 instanceof TrimeshGeometryBatch) {
															// setting separate horizontalRotation for trimeshGeomBatch
															((TrimeshGeometryBatch)s3).horizontalRotation = n.horizontalRotation;
														}												
													}												
												}
												s2.setLocalScale(n.getLocalScale());
												if (s2 instanceof TrimeshGeometryBatch) {
													// setting separate horizontalRotation for trimeshGeomBatch
													((TrimeshGeometryBatch)s2).horizontalRotation = n.horizontalRotation;
												} else {
													s2.setLocalRotation(n.getLocalRotation());
												}
											}
										} else {
											s.setLocalRotation(n.getLocalRotation());
											Vector3f scale = new Vector3f(n.getLocalScale());
											scale.x*=J3DCore.FARVIEW_GAP;
											scale.z*=J3DCore.FARVIEW_GAP;
											s.setLocalScale(scale);
										}
									}
								
									if (c.cube.internalCube) {
										intRootNode.attachChild((Node)realPooledNode);
									} else 
									{
										extRootNode.attachChild((Node)realPooledNode);
									}
									realPooledNode.setCullMode(Node.CULL_NEVER);
									newNodesToSetCullingDynamic.add(realPooledNode);
									if (sharedNode)
									{	
										realPooledNode.lockMeshes();
									}
									{
										/*if (n.model.type==Model.PARTLYBILLBOARDMODEL)
										{
											for (Spatial s:realPooledNode.getChildren())
											{
												if (s instanceof PooledSharedNode)
												{
													//s.lockMeshes();
												}
											}
										}*/
										realPooledNode.lockShadows();
										realPooledNode.lockBranch();
										realPooledNode.lockBounds();
										realPooledNode.lockTransforms();																	
									}
								}
							}
						} 
					} 
					else
					{
						 nonVisibleNodeCounter++;
						 if (!outOfFarViewPort.contains(c)) 
						 {
							removedNodeCounter++;
							outOfFarViewPort.add(c);
							inFarViewPort.remove(c);
							for (NodePlaceholder n : c.hsRenderedNodes)
							{
								if (J3DCore.GEOMETRY_BATCH && n.model.batchEnabled && 
										(n.model.type == Model.QUADMODEL || n.model.type == Model.SIMPLEMODEL
												|| J3DCore.GRASS_BIG_BATCH && n.model.type == Model.TEXTURESTATEVEGETATION) 
									 )
								{
									if (n!=null)
										core.batchHelper.removeItem(c.cube.internalCube, n.model, n, n.farView);
								} else 
								{
									PooledNode pooledRealNode = n.realNode;
									
									n.realNode = null;
									if (pooledRealNode!=null) {
										Node realNode = (Node)pooledRealNode;
										if (J3DCore.SHADOWS) core.removeOccludersRecoursive(realNode);
										realNode.removeFromParent();
										modelPool.releaseNode(pooledRealNode);
									}
								}
								n.farView = false;
							}
							
						 }
					}
				}
			}
			
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("BATCH ADD-REM TIME = "+sumAddRemoveBatch);
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("BATCH ADD-REM TIME mod real = "+ModelGeometryBatch.sumBuildMatricesTime);
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("BATCH ADD-REM TIME tri real = "+TrimeshGeometryBatch.sumAddItemReal);
			
			if (segmentCount==segments-1 || !segmented) {
				
				
				if (J3DCore.LOGGING) Jcrpg.LOGGER.info("J3DCore.renderToViewPort: visilbe nodes = "+visibleNodeCounter + " nonV = "+nonVisibleNodeCounter+ " ADD: "+addedNodeCounter+ " RM: "+removedNodeCounter);
			    // handling possible occluders
			    if (J3DCore.SHADOWS) {
			    	if (J3DCore.LOGGING) Jcrpg.LOGGER.info("OCCS: "+core.sPass.occludersSize());
					for (NodePlaceholder psn : core.possibleOccluders) {
						if (psn.realNode != null) {
							Node n = (Node) psn.realNode;
							float dist = n.getWorldTranslation().distanceSquared(
									core.getCamera().getLocation());
							if (dist < J3DCore.RENDER_SHADOW_DISTANCE_SQR) {
								if (!core.sPass.containsOccluder(n))
								{
									if (J3DCore.LOGGING) Jcrpg.LOGGER.info("ADDING OCCLUDER: "+n.getName());
									core.sPass.addOccluder(n);
									
								}
							} else {
								core.removeOccludersRecoursive(n);
							}
						}
					}
			    }
			    
			    if (J3DCore.LOGGING) Jcrpg.LOGGER.info("rtoviewport time: "+(System.currentTimeMillis()-sysTime));
			    sysTime = System.currentTimeMillis();
			    
			    
			    core.updateTimeRelated();
		
				cullVariationCounter++;

				core.groundParentNode.setCullMode(Node.CULL_NEVER);
				
				// call an update to render new nodes correctly - workaround for culling problem - new nodes are set to CULL_NEVER..
				core.updateDisplayNoBackBuffer();
				// ...iterate through new nodes and set normal culling mode...
				for (Node n:newNodesToSetCullingDynamic)
				{
					n.setCullMode(Node.CULL_INHERIT);
					n.updateRenderState(); // update render state for the newly placed nodes...
				}
				
				core.groundParentNode.setCullMode(Node.CULL_INHERIT);
				
				// update geometry batches...
				if (J3DCore.GEOMETRY_BATCH) 
				{
					core.batchHelper.updateAll();
					core.batchHelper.lockAll();
				}
				
				if (J3DCore.LOGGING) Jcrpg.LOGGER.info("CAMERA: "+core.getCamera().getLocation()+ " NODES EXT: "+(extRootNode.getChildren()==null?"-":extRootNode.getChildren().size()));
				if (J3DCore.LOGGING) Jcrpg.LOGGER.info("crootnode cull update time: "+(System.currentTimeMillis()-sysTime));
				if (J3DCore.LOGGING) Jcrpg.LOGGER.info("hmSolidColorSpatials:"+J3DCore.hmSolidColorSpatials.size());
		
			    if (cullVariationCounter%20==0) {
					modelPool.cleanPools();
					System.gc();
				}
		
				// every 20 steps do a garbage collection
			    core.garbCollCounter++;
				if (core.garbCollCounter==20) {
					//
					core.garbCollCounter = 0;
				}
			} else
			{
			    core.updateTimeRelated();
			}
			
			engine.unpauseAfterRendering();
		}
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("######## FULL RTVP = "+( System.currentTimeMillis()-t1));
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("######## FULL RTVP = "+( System.currentTimeMillis()-t1));
	}
	
	
	private void renderNodes(NodePlaceholder[] n, RenderedCube cube, int x, int y, int z, int direction)
	{
		renderNodes(n, cube, x, y, z, direction, -1, 1f);
	}
	
	public int liveNodes = 0;
	
	/**
	 * Renders one side into 3d space percepting what kind of RenderedSide it is.
	 * @param cube
	 * @param x
	 * @param y
	 * @param z
	 * @param direction
	 * @param side
	 * @param fakeLoadForCacheMaint No true rendering if this is true, only fake loading the objects through model loader.
	 */
	public void renderSide(RenderedCube cube,int x, int y, int z, int direction, Side side, boolean fakeLoadForCacheMaint)
	{
		if (side==null||side.subtype==null) return;
		Integer n3dType = core.hmCubeSideSubTypeToRenderedSideId.get(side.subtype.id);
		if (n3dType==null) return;
		if (n3dType.equals(J3DCore.EMPTY_SIDE)) return;
		RenderedSide renderedSide = core.hm3dTypeRenderedSide.get(n3dType);
		
		
		NodePlaceholder[] n = modelPool.loadPlaceHolderObjects(cube,renderedSide.objects,fakeLoadForCacheMaint);
		if (!fakeLoadForCacheMaint) {
			if (renderedSide.type == RenderedSide.RS_HASHROTATED)
			{
				int rD = ((RenderedHashRotatedSide)renderedSide).rotation(cube.cube.x, cube.cube.y, cube.cube.z);
				float scale = ((RenderedHashRotatedSide)renderedSide).scale(cube.cube.x, cube.cube.y, cube.cube.z);
				renderNodes(n, cube, x, y, z, direction, rD,scale);
			} 
			else
			if (renderedSide.type == RenderedSide.RS_HASHALTERED)
			{
				renderNodes(n, cube, x, y, z, direction);
				Model[] m = ((RenderedHashAlteredSide)renderedSide).getRenderedModels(cube.cube.x, cube.cube.y, cube.cube.z, cube.cube.steepDirection!=SurfaceHeightAndType.NOT_STEEP);
				NodePlaceholder[] n2 = modelPool.loadPlaceHolderObjects(cube,m,fakeLoadForCacheMaint);
				if (n2.length>0)
					renderNodes(n2, cube, x, y, z, direction);
			} 
			else
				if (renderedSide.type == RenderedSide.RS_CLIMATEDEPENDENT)
				{
					renderNodes(n, cube, x, y, z, direction);
					Model[] m = ((RenderedClimateDependentSide)renderedSide).getRenderedModels(cube.cube.climateId);
					if (m!=null) {
						NodePlaceholder[] n2 = modelPool.loadPlaceHolderObjects(cube,m,fakeLoadForCacheMaint);
						if (n2.length>0)
							renderNodes(n2, cube, x, y, z, direction);
					}
				} 
				else
			{
				renderNodes(n, cube, x, y, z, direction);
			}
		}

		Cube checkCube = null;
		if (direction==J3DCore.TOP && renderedSide.type == RenderedSide.RS_TOPSIDE) // Top Side
		{
			if (cube.cube.getNeighbour(J3DCore.TOP)!=null)
			{
				// if there is a side of the same kind above the current side, 
				//  we don't need continuous side rendering
				if (cube.cube.getNeighbour(J3DCore.TOP).hasSideOfType(direction,side.type))
				{
					return;					
				}
				
			} else {
				//if (J3DCore.LOGGING) Jcrpg.LOGGER.info("# TOP IS NULL!");
			}
			boolean render = true;
			// Check if there is no same cube side type near in any direction, so we can safely put the Top objects on, no bending roofs are near...
			for (int i=J3DCore.NORTH; i<=J3DCore.WEST; i++)
			{
				Cube n1 = cube.cube.getNeighbour(i);
				if (n1!=null)
				{
					if (n1.hasSideOfType(i,side.type) || n1.hasSideOfType(J3DCore.oppositeDirections.get(new Integer(i)).intValue(),side.type))
					{
						render = false; break;
					}
				} 
			}
			if (render)
			{
				n= modelPool.loadPlaceHolderObjects(cube,((RenderedTopSide)renderedSide).nonEdgeObjects,fakeLoadForCacheMaint);
				if (!fakeLoadForCacheMaint)
				{
					renderNodes(n, cube, x, y, z, direction);
				}
			}
		}
		if (direction!=J3DCore.TOP && direction!=J3DCore.BOTTOM && renderedSide.type == RenderedSide.RS_CONTINUOUS) // Continuous side
		{
			int dir = J3DCore.nextDirections.get(new Integer(direction)).intValue();
			if (cube.cube.getNeighbour(J3DCore.TOP)!=null)
			{
				// if there is a side of the same kind above the current side, 
				//  we don't need continuous side rendering
				if (cube.cube.getNeighbour(J3DCore.TOP).hasSideOfType(direction,side.type))
				{
					return;					
				}
				
			}
			if (cube.cube.getNeighbour(dir)!=null)
			if (cube.cube.getNeighbour(dir).hasSideOfType(direction,side.type))
			{
				checkCube = cube.cube.getNeighbour(J3DCore.oppositeDirections.get(new Integer(dir)).intValue());
				if (checkCube !=null) {
					if (checkCube.hasSideOfType(direction,side.type))
					{
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).continuous, fakeLoadForCacheMaint );
						if (!fakeLoadForCacheMaint)
						{
							renderNodes(n, cube, x, y, z, direction);
						}
					} else
					{
						// normal direction is continuous
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousNormal, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint)
						{
							renderNodes(n, cube, x, y, z, direction);
						}
					}
				} else
				{
					// normal direction is continuous
					n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousNormal, fakeLoadForCacheMaint );
					if (!fakeLoadForCacheMaint)
					{
						renderNodes(n, cube, x, y, z, direction);
					}
				}
				
			} else 
			{
				checkCube = cube.cube.getNeighbour(J3DCore.oppositeDirections.get(new Integer(dir)).intValue());
				if (checkCube!=null)
				{
					if (checkCube.hasSideOfType(direction, side.type))
					{
						// opposite to normal direction is continuous 
						// normal direction is continuous
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
					
					}else
					{
						// no continuous side found
						n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).nonContinuous, fakeLoadForCacheMaint);
						if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
					}
				} else {
					// opposite to normal direction is continuous 
					// normal direction is continuous
					n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
					if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
				}
			} else {
				// opposite to normal direction is continuous 
				// normal direction is continuous
				n = modelPool.loadPlaceHolderObjects(cube, ((RenderedContinuousSide)renderedSide).oneSideContinuousOpposite, fakeLoadForCacheMaint);
				if (!fakeLoadForCacheMaint) renderNodes(n, cube, x, y, z, direction);
			}
			
		}
		
		
	}
	
	public void clearAll()
	{
		hmCurrentCubes.clear();
		hmCurrentCubes_FARVIEW.clear();
		alCurrentCubes.clear();
		alCurrentCubes_FARVIEW.clear();
		inFarViewPort.clear();
		inViewPort.clear();
		outOfViewPort.clear();
		outOfFarViewPort.clear();
		renderedArea.worldCubeCache.clear();
		renderedArea.worldCubeCache_FARVIEW.clear();
		renderedArea.worldCubeCacheNext.clear();
		renderedArea.worldCubeCacheNext_FARVIEW.clear();
	}
	
	public void switchOn(boolean on)
	{
		if (on)
		{
			core.getRootNode1().attachChild(extRootNode);
			core.getRootNode1().attachChild(intRootNode);
			core.getRootNode1().updateRenderState();
			extRootNode.setCullMode(Node.CULL_DYNAMIC);
			intRootNode.setCullMode(Node.CULL_DYNAMIC);
		} else
		{
			extRootNode.removeFromParent();
			intRootNode.removeFromParent();
			core.getRootNode1().updateRenderState();
			extRootNode.setCullMode(Node.CULL_ALWAYS);
			intRootNode.setCullMode(Node.CULL_ALWAYS);
		}
	}
	
}
