/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.ui.text;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.HUD;
import org.jcrpg.ui.KeyListener;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.state.RenderState.StateType;

public class TextBox implements KeyListener {
	
	

	HUD hud;
	
	Text[] textLines;
	
	ArrayList<TextEntry> boxFullTextEntries = new ArrayList<TextEntry>();
	
	int maxLines = 10;
	int fontSize = 12;
	
	int backFrom = 0;
	Node n = null;
	static int counter  = 0;
	public TextBox(HUD hud, String name, float middleX, float middleY, float sizeX, float sizeY)
	{
	
		this.hud = hud;
		
		//maxLines = (int)(hud.base.core.getDisplay().getHeight()*sizeY) / fontSize;
		textLines = new Text[maxLines];
		
		n = new Node("TextBoxNode_"+name+"_"+counter++);
	
		int pixels = (int)(hud.base.core.getDisplay().getHeight()*sizeY);
		int neededVirtualPixels = maxLines*fontSize;
		float stretch = pixels*1f/neededVirtualPixels;
		for (int i=0; i<maxLines; i++)
		{
			textLines[i] = Text.createDefaultTextLabel("TextBox_"+name, "");
			textLines[i].setCullHint(CullHint.Never );
			textLines[i].setTextureCombineMode( TextureCombineMode.Replace );
			
			textLines[i].setLocalTranslation(hud.core.getDisplay().getWidth()*middleX, (hud.core.getDisplay().getHeight()*middleY)-i*fontSize*stretch, 0);
			textLines[i].setLocalScale(stretch);
			n.attachChild(textLines[i]);
		}
		
		n.setRenderState( textLines[0].getRenderState( StateType.Blend ) );
        n.setRenderState( textLines[0].getRenderState( StateType.Texture ) );
        n.setCullHint(CullHint.Never );
		updateText();
		//hud.hudNode.attachChild(n);
	}
	
	public void updateText()
	{
		synchronized(boxFullTextEntries) {
			int size = boxFullTextEntries.size();
			int end = Math.max(0, size-backFrom);
			int start = Math.max(0, size-backFrom-maxLines);
			for (int i=start; i<end; i++)
			{
				textLines[i-start].print(boxFullTextEntries.get(i).text);
				textLines[i-start].setTextColor(boxFullTextEntries.get(i).color);
			}
		}
	}
	
	ColorRGBA black = new ColorRGBA(0,0,0,1f);
	public void addEntry(String entry)
	{
		addEntry(new TextEntry(entry,black));
	}
	
	public int wrapLimit = 55; 
	public void addEntry(TextEntry entry)
	{
		if (entry.text.length()>wrapLimit)
		{
			
			addEntry(new TextEntry(entry.text.substring(0,wrapLimit),entry.color));
			entry.text = entry.text.substring(wrapLimit);
			addEntry(entry);
			return;
		}
		boxFullTextEntries.add(entry);
		updateText();
	}

	public boolean handleKey(String key) {
		if (key.equals("logUp"))
		{
			if (!(backFrom>=boxFullTextEntries.size()-maxLines))
			{
				backFrom++;
			}
		} else
		if (key.equals("logDown"))
		{
			backFrom--;
		}
		if (backFrom<0) backFrom = 0;
		updateText();
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("--- BACKFROM = "+backFrom);
		return true;
	}
	public void hide()
	{
		hud.hudNode.detachChild(n);
		hud.hudNode.updateRenderState();
	}
	public void show()
	{
		hud.hudNode.attachChild(n);
		hud.hudNode.updateRenderState();
	}
}
