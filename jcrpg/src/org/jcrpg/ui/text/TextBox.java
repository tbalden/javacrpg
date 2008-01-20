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

package org.jcrpg.ui.text;

import java.util.ArrayList;

import org.jcrpg.ui.HUD;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Text;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;

public class TextBox {
	
	

	HUD hud;
	
	Text[] textLines;
	
	ArrayList<TextEntry> boxFullTextEntries = new ArrayList<TextEntry>();
	
	int maxLines = 1;
	int fontSize = 12;
	
	public TextBox(HUD hud, String name, float middleX, float middleY, float sizeX, float sizeY)
	{
	
		this.hud = hud;
		
		maxLines = (int)(hud.base.core.getDisplay().getHeight()*sizeY) / fontSize;
		textLines = new Text[maxLines];
		
		Node n = new Node("TextBoxNode_"+name);
	
		for (int i=0; i<maxLines; i++)
		{
			textLines[i] = Text.createDefaultTextLabel("TextBox_"+name, "");
			textLines[i].setCullMode( SceneElement.CULL_NEVER );
			textLines[i].setTextureCombineMode( TextureState.REPLACE );
			
			textLines[i].setLocalTranslation(hud.core.getDisplay().getWidth()*middleX, (hud.core.getDisplay().getHeight()*middleY)-i*fontSize, 0);
			n.attachChild(textLines[i]);
		}
		
		n.setRenderState( textLines[0].getRenderState( RenderState.RS_ALPHA ) );
        n.setRenderState( textLines[0].getRenderState( RenderState.RS_TEXTURE ) );
        n.setCullMode( SceneElement.CULL_NEVER );
		updateText(0);
		hud.hudNode.attachChild(n);
	}
	
	public void updateText(int backFrom)
	{
		synchronized(boxFullTextEntries) {
			StringBuffer buff = new StringBuffer();
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
	
	public void addEntry(String entry)
	{
		boxFullTextEntries.add(new TextEntry(entry,ColorRGBA.white));
		updateText(0);
	}
	public void addEntry(TextEntry entry)
	{
		boxFullTextEntries.add(entry);
		updateText(0);
	}
}
