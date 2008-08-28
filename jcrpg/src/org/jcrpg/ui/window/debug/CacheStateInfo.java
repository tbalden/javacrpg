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

package org.jcrpg.ui.window.debug;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.ModelLoader;
import org.jcrpg.threed.ModelPool;
import org.jcrpg.threed.VegetationSetup;
import org.jcrpg.threed.jme.GeometryBatchHelper;
import org.jcrpg.threed.jme.ModelGeometryBatch;
import org.jcrpg.threed.jme.TrimeshGeometryBatch;
import org.jcrpg.threed.jme.vegetation.BillboardPartVegetation;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.scene.shape.Quad;
import com.jme.util.TextureManager;

public class CacheStateInfo extends InputWindow {

	TextLabel extIntNodes;
	TextLabel trimeshGeoCacheSize;
	TextLabel modelGeoCacheSize;
	TextLabel poolSize;
	TextLabel bbCacheSize;
	TextLabel modelCacheSize;
	
	public CacheStateInfo(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.png", 0.8f*core.getDisplay().getWidth(), 1.65f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
			hudQuad.setRenderState(base.hud.hudAS);
	    	windowNode.attachChild(hudQuad);
	    	new TextLabel("",this,windowNode, 0.20f, 0.08f, 0.3f, 0.06f,600f,"Ext/Int node number:",false); 
	    	extIntNodes = new TextLabel("",this,windowNode, 0.57f, 0.08f, 0.3f, 0.06f,600f,"",false); 
	    	new TextLabel("",this,windowNode, 0.20f, 0.13f, 0.3f, 0.06f,600f,"Trim.GeoBatch no / parentnode:",false); 
	    	trimeshGeoCacheSize = new TextLabel("",this,windowNode, 0.57f, 0.13f, 0.3f, 0.06f,600f,"",false); 
	    	new TextLabel("",this,windowNode, 0.20f, 0.18f, 0.3f, 0.06f,600f,"ModelGeoBatch no / parentnode:",false); 
	    	modelGeoCacheSize = new TextLabel("",this,windowNode, 0.57f, 0.18f, 0.3f, 0.06f,600f,"",false); 
	    	new TextLabel("",this,windowNode, 0.20f, 0.23f, 0.3f, 0.06f,600f,"Pool size:",false); 
	    	poolSize = new TextLabel("",this,windowNode, 0.57f, 0.23f, 0.3f, 0.06f,600f,"",false); 
	    	new TextLabel("",this,windowNode, 0.20f, 0.28f, 0.3f, 0.06f,600f,"BBPartVeg Cache size:",false); 
	    	bbCacheSize = new TextLabel("",this,windowNode, 0.57f, 0.28f, 0.3f, 0.06f,600f,"",false); 
	    	new TextLabel("",this,windowNode, 0.20f, 0.32f, 0.3f, 0.06f,600f,"Model Cache sizes:",false); 
	    	modelCacheSize = new TextLabel("",this,windowNode, 0.57f, 0.32f, 0.3f, 0.06f,600f,"",false); 
	    	
		} catch (Exception ex)
		{
			
		}
	}

	
	
	@Override
	public void hide() {
		super.hide();
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
	}

	public void update()
	{
		try {
			extIntNodes.text = ""+(J3DCore.getInstance().extRootNode.getChildren()==null?"0":J3DCore.getInstance().extRootNode.getChildren().size()) + " / "+(J3DCore.getInstance().intRootNode.getChildren()==null?"0":J3DCore.getInstance().intRootNode.getChildren().size());
			extIntNodes.text+=" / "+(J3DCore.getInstance().groundParentNode.getChildren()==null?"0":J3DCore.getInstance().groundParentNode.getChildren().size());
			extIntNodes.text+=" / "+(J3DCore.getInstance().skyParentNode.getChildren()==null?"0":J3DCore.getInstance().skyParentNode.getChildren().size());
			extIntNodes.activate();
			//trimeshGeoCacheSize.text = ""+GeometryBatchHelper.trimeshBatchMap.size()+" / " +TrimeshGeometryBatch.sharedParentCache.size();
			trimeshGeoCacheSize.activate();
			// TODO modelGeoCacheSize.text = ""+GeometryBatchHelper.modelBatchMap.size()+" / " +ModelGeometryBatch.sharedParentCache.size()+ " / Trimesh model cache: "+ModelGeometryBatch.cache.size();
			modelGeoCacheSize.activate();
			poolSize.text = ""+ModelPool.getPooledSize()+ " (Types: "+ModelPool.pool.size()+")"+" VegSetupQuadC.: "+VegetationSetup.quadCache.size();
			poolSize.activate();
			bbCacheSize.text = ""+BillboardPartVegetation.quadCache.size();
			bbCacheSize.activate();
			modelCacheSize.text = "SharedN:"+ModelLoader.sharedNodeCache.size()+" TxSt:"+ModelLoader.textureStateCache.size()+" Bin:"+ModelLoader.binaryCache.size()+" Tx:"+ModelLoader.textureCache.size();
			modelCacheSize.activate();
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public void show() {
		update();
		super.show();
		core.getUIRootNode().attachChild(windowNode);
		core.getUIRootNode().updateRenderState();
	}



	@Override
	public boolean inputChanged(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputLeft(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

}
