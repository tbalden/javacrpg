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

package org.jcrpg.ui;

import java.io.File;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public abstract class Window {

	protected boolean visible = false;
	protected Node windowNode;
	
	public UIBase base;
	public J3DCore core;
	
	static int windowCounter = 0;
	
	public Window(UIBase base)
	{
		this.base = base;
		core = base.core;
		windowNode = new Node("windowNode");

	}
	boolean storedEnginePauseState = false; 
	public synchronized void toggle()
	{
		if (visible)
		{
			core.gameState.engine.setPause(storedEnginePauseState);
			windowCounter--;
			if (windowCounter==0) ((ClassicKeyboardLookHandler)core.getInputHandler().getFromAttachedHandlers(0)).unlockSecondaryHandling();
			hide();
		} else
		{
			windowCounter++;
			((ClassicKeyboardLookHandler)core.getInputHandler().getFromAttachedHandlers(0)).lockSecondaryHandling();
			storedEnginePauseState = core.gameState.engine.isPause();
			core.gameState.engine.setPause(true);
			show();
		}
		visible=!visible;
	}
	
	public Quad loadImageToQuad(String fileName, float sizeX, float sizeY,
			float posX, float posY) throws Exception {
		Quad hudQuad = new Quad(fileName, sizeX, sizeY);
		hudQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		hudQuad.setLocalTranslation(new Vector3f(posX, posY, 0));

		Image hudImage = TextureManager.loadImage(new File(fileName).toURI()
				.toURL(), true);
		/*Image hudImage2 = TextureManager.loadImage(new File("./data/ui/white.png").toURI()
				.toURL(), true);*/

		TextureState state = core.getDisplay().getRenderer()
				.createTextureState();
		Texture texture = new Texture();
		texture.setImage(hudImage);

		state.setTexture(texture,0);
		hudQuad.setRenderState(state);

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
	
}
