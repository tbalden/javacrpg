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

package org.jcrpg.ui;

import java.io.File;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicInputHandler;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;
import org.jcrpg.threed.jme.ui.ZoomingQuad;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.world.ai.EntityMemberInstance;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;

public abstract class Window {

	protected boolean visible = false;
	public Node windowNode;
	
	public UIBase base;
	public J3DCore core;
	
	static int windowCounter = 0;
	
	public Window(UIBase base)
	{
		this.base = base;
		core = base.core;
		windowNode = new Node("windowNode");
		windowNode.setModelBound(new BoundingBox());

	}
	boolean storedEnginePauseState = false; 
	public synchronized void toggle()
	{
		if (visible)
		{
			core.gameState.engine.setPause(storedEnginePauseState);
			windowCounter--;
			base.catchEventFromSpreading();
			if (windowCounter==0) ((ClassicKeyboardLookHandler)core.getInputHandler().getFromAttachedHandlers(0)).unlockSecondaryHandling();
			core.setFlare(true);
			base.activeWindows.remove(this);
			((ClassicInputHandler)base.core.getInputHandler()).setRootNode(null);
			hide();
		} else
		{
			windowCounter++;
			((ClassicKeyboardLookHandler)core.getInputHandler().getFromAttachedHandlers(0)).lockSecondaryHandling();
			storedEnginePauseState = core.gameState.engine.isPause();
			core.setFlare(false);
			core.gameState.engine.setPause(true);
			base.activeWindows.add(this);
			((ClassicInputHandler)base.core.getInputHandler()).setRootNode(windowNode);
			show();
		}
		visible=!visible;
	}
	public static Quad loadImageToQuad(String fileName, float sizeX, float sizeY,
			float posX, float posY) throws Exception {
		return loadImageToQuad(new File(fileName), sizeX, sizeY, posX, posY);
	}	
	
	
	
	public static Quad loadImageToQuad(File file, float sizeX, float sizeY,
			float posX, float posY) throws Exception {
		
		Quad hudQuad = UIImageCache.getImage(file.getPath(), true, sizeX, sizeY);
	
		hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		hudQuad.setLocalTranslation(new Vector3f(posX, posY, 0));

		return hudQuad;
	}

	public static ZoomingQuad loadImageToZoomingQuad(File file, float sizeX, float sizeY,
			float posX, float posY) throws Exception {
		
		ZoomingQuad hudQuad = UIImageCache.getImageZoomingQuad(file.getPath(), true, sizeX, sizeY);
	
		hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		hudQuad.setLocalTranslation(new Vector3f(posX, posY, 0));

		return hudQuad;
	}
	
	int lockers = 0;
	
	public synchronized void lockLookAndMove(boolean value)
	{
		if (value) {
			lockers++;
		} else
		{
			lockers--;
		}
		if (value || !value && lockers == 0) 
		{
			((ClassicKeyboardLookHandler)core.getInputHandler().getFromAttachedHandlers(0)).lock = value;
		}
	}
	
	public abstract void hide();
	public abstract void show();

	/**
	 * Call this back if portrait is 'used' (clicked etc.)
	 * @param count Number of member in party
	 * @param member Member object
	 * @param secondaryWay Was it a secondary input
	 */
	public void characterSelected(int count, EntityMemberInstance member, int inputType)
	{
		// 
	}
	
	public class TooltipBox
	{
		
		public Node tooltipNode = new Node();
		public Node ttTextBaseNode = new Node();
		public final float ttXSizeRatio = 0.33f;
		public final float ttYSizeRatio = 0.13f;
		public final float ttXPosRatio = 0.75f;
		public final float ttYPosRatio = 0.93f;
		
		public final float fontSizeRatio = 0.01f;
		
		
		private float xSize = 0f;
		private float ySize = 0f;
		private float xPos = 0f;
		private float yPos = 0f;
		
		Quad bgImage;
		
		public TooltipBox() throws Exception 
		{
			int width = DisplaySystem.getDisplaySystem().getWidth();
			int height = DisplaySystem.getDisplaySystem().getHeight();
			xSize = width * ttXSizeRatio;
			ySize = height * ttYSizeRatio;
			xPos = width * ttXPosRatio;
			yPos = width * (1f-ttYPosRatio);
			bgImage = loadImageToQuad(TextButton.defaultImage, xSize, ySize, xPos, yPos);
			
			if (ttTextBaseNode.getParent()==null)
			{
				tooltipNode.attachChild(bgImage);
				tooltipNode.attachChild(ttTextBaseNode);
			}
		}
		
		public void toggleTooltip(String text, Node parent)
		{
			if (text==null)
			{
				tooltipNode.removeFromParent();
			} else
			{
				parent.attachChild(tooltipNode);
			}
		}
	}
	
	public TooltipBox tooltipBox = null;
	
	public void toggleTooltip(String text)
	{
		if (tooltipBox==null)
		{
			try {
			tooltipBox = new TooltipBox();} catch (Exception ex){ex.printStackTrace();}
		}
		if (tooltipBox!=null)
		tooltipBox.toggleTooltip(text, windowNode);
	}
	
	
	
	
}
