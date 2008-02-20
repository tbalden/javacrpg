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

package org.jcrpg.ui.window.element.input;

import java.io.File;
import java.util.HashSet;

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.InputWindow;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class TextInputField extends InputBase {

	public String text;
	public int cursorPosition = 0;
	public int maxLength = 20;
	public boolean capsLock = true;
	
	public static final String defaultImage = "./data/ui/buttonBase.png";
	public String bgImage = defaultImage; 
	public float textProportion = 400f;
	public TextInputField(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion, String text, int maxLength) {
		super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
		this.text = text;
		this.maxLength = maxLength;
		this.textProportion = textProportion;
		boolean addListened = false;
		if (listenedKeys.size()==0) addListened = true;
		for (int i=0; i<chars.length(); i++)
		{
			w.base.addEventHandler(""+chars.charAt(i),w);
			if (addListened) listenedKeys.add(""+chars.charAt(i));
		}
		w.base.addEventHandler("space", w);
		if (addListened) listenedKeys.add("space");
		w.base.addEventHandler("back", w);
		w.base.addEventHandler("lookLeft", w);
		w.base.addEventHandler("lookRight", w);
		w.base.addEventHandler("shift", w);
		deactivate();
	}
	
	Node activeNode = null;
	Node deactiveNode = null;
	
	String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static HashSet<String> listenedKeys = new HashSet<String>();

	@Override
	public void activate() {
		baseNode.detachAllChildren();
		//if (activeNode==null ) 
		{
			activeNode = new Node();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.white);
				activeNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(0.6f,0.6f,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),true);
			slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
			activeNode.attachChild(slottextNode);
		}
		baseNode.attachChild(activeNode);
		baseNode.updateRenderState();
		super.activate();
	}

	@Override
	public void deactivate() {
		baseNode.detachAllChildren();
		//if (deactiveNode==null ) 
		{
			deactiveNode = new Node();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.gray);
				deactiveNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(0.5f,0.5f,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),true);
			slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
			deactiveNode.attachChild(slottextNode);
		}
		baseNode.attachChild(deactiveNode);
		baseNode.updateRenderState();
		super.deactivate();
	}

	@Override
	public boolean handleKey(String key) {
		if (key.equals("enter"))
		{
			w.core.audioServer.play(SOUND_INPUTSELECTED);
			w.inputUsed(this, key);
			return true;
		} else
		if (key.equals("back"))
		{
			if (text.length()>0 && cursorPosition>0 )
			{
				StringBuffer newText = new StringBuffer();
				for (int i=0; i<text.length(); i++)
				{
					if (i==cursorPosition-1) continue;
					newText.append(text.charAt(i));
				}
				if (cursorPosition>0) cursorPosition--;
				text = newText.toString();
				updated = true;
				activate();
			}
			return true;
		}
		if (key.equals("delete"))
		{
			if (text.length()>0 && cursorPosition<text.length()-1 )
			{
				StringBuffer newText = new StringBuffer();
				for (int i=0; i<text.length(); i++)
				{
					if (i==cursorPosition) continue;
					newText.append(text.charAt(i));
				}
				text = newText.toString();
				updated = true;
				activate();
			}
			return true;
		}
		if (key.equals("lookLeft"))
		{
			if (cursorPosition==0) return true;
			cursorPosition--;
			updated = true;
			activate();
			return true;
		}
		if (key.equals("lookRight"))
		{
			if (cursorPosition==text.length()-1) return true;
			cursorPosition++;
			updated = true;
			activate();
			return true;
		}
		if (key.equals("shift"))
		{
			capsLock=!capsLock;
			return true;
		}
		if (listenedKeys.contains(key))
		{
			if (text.length()==maxLength) return true;
			System.out.println("KEY PRESSED : "+key);
			String appended = key;
			if (key.equals("space"))
			{
				appended = " ";
			}
			if (!capsLock) appended = appended.toLowerCase();
			StringBuffer newText = new StringBuffer();
			for (int i=0; i<text.length()+1; i++)
			{
				if (i==cursorPosition) 
				{
					newText.append(appended);
				} else
				if (i<text.length())
					newText.append(text.charAt(i));
			}
			if (newText.length()==0)
			{
				newText.append(appended);
			}
			cursorPosition++;
			text = newText.toString();
			updated = true;
			activate();
		}
		return false;
	}

}
