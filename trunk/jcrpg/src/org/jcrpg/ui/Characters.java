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

package org.jcrpg.ui;

import java.io.File;
import java.util.ArrayList;

import org.jcrpg.ui.text.FontTT;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

public class Characters {

	public ArrayList<org.jcrpg.ui.window.element.Character> characterData = new ArrayList<org.jcrpg.ui.window.element.Character>();

	FontTT text;
	
	Node node = new Node();
	HUD hud = null;
	public Characters(HUD hud)
	{
		this.hud = hud;
		text = FontUtils.textVerdana;
 		String[] pics =
		{
				"./data/portraits/human/male/balzac.png", 
				"./data/portraits/human/male/fred2.png", 
				"./data/portraits/human/male/friedrich.png", 
				"./data/portraits/human/female/baran.png", 
				"./data/portraits/human/female/marie.png", 
				"./data/portraits/human/male/max.png" 
		};
		String[] names = 
		{
				"Balzac", "Fred", "Friedrich", "Baran", "Marie", "Max"
		};
		String[] classes = 
		{
				"BARD", "ROGUE", "FIGHTER", "PRIESTESS", "ALCHEMIST", "WIZARD"
		};
		
		int counter = 0, c2 = 0;
		int sideYMul = 1, sideYMulFont = 1;
		float stepY = hud.core.getDisplay().getHeight()/6.6f;
		float startY = hud.core.getDisplay().getHeight()*0.8f;
		Quad frame  = null;
		try {
			frame = Window.loadImageToQuad(new File("./data/ui/portraitFrame.png"), 1.025f*hud.core.getDisplay().getWidth()/13, 1.037f*hud.core.getDisplay().getHeight()/10.3f, sideYMul*hud.core.getDisplay().getWidth()/20, 0.9988f*startY-stepY*counter);
		} catch (Exception ex)
		{
			
		}
		for (String pic:pics)
		{
			org.jcrpg.ui.window.element.Character c = new org.jcrpg.ui.window.element.Character();
			try 
			{
				SharedMesh sm = new SharedMesh("1",frame);
				sm.setLocalTranslation(sideYMul*hud.core.getDisplay().getWidth()/20, 0.999f*startY-stepY*counter,0);
				
				Quad q = Window.loadImageToQuad(new File(pic), hud.core.getDisplay().getWidth()/13, hud.core.getDisplay().getHeight()/10.3f, sideYMul*hud.core.getDisplay().getWidth()/20, startY-stepY*counter++);
				
				Node nametextNode = this.text.createText(names[c2], 9, new ColorRGBA(1,1,0.6f,1f),false);
				
				nametextNode.setLocalTranslation(sideYMulFont*hud.core.getDisplay().getWidth()/50, startY-stepY*(counter-1)-stepY*0.37f,0);
				
				nametextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				nametextNode.setLocalScale(hud.core.getDisplay().getWidth()/1000f);
				
				Node classtextNode = this.text.createText(classes[c2++], 9, new ColorRGBA(0.5f,0.5f,0.9f,1f),false);
				
				classtextNode.setLocalTranslation(sideYMulFont*hud.core.getDisplay().getWidth()/50, startY-stepY*(counter-1)-stepY*0.425f,0);
				
				classtextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				classtextNode.setLocalScale(hud.core.getDisplay().getWidth()/1000f);

				node.attachChild(nametextNode);
				node.attachChild(classtextNode);
				node.attachChild(sm);
				node.attachChild(q);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			if (counter==3)
			{
				counter = 0;
				sideYMul = 19;
				sideYMulFont = 46;
			}
			characterData.add(c);
		}
	}
	
	public void hide()
	{
		node.removeFromParent();
	}
	public void show()
	{
		hud.hudNode.attachChild(node);
		node.updateRenderState();
	}
}
