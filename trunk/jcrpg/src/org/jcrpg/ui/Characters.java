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
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;

public class Characters {

	public ArrayList<org.jcrpg.ui.window.element.Character> characterData = new ArrayList<org.jcrpg.ui.window.element.Character>();
	

	FontTT text;
	
	Node node = new Node();
	HUD hud = null;
	public Characters(HUD hud)
	{
		
		this.hud = hud;
		text = FontUtils.textVerdana;
	}
	
	public void hide()
	{
		node.removeFromParent();
	}
	
	public void updateForPartyCreation(ArrayList<EntityMemberInstance> orderedParty)
	{
		node.detachAllChildren();
		addMembers(orderedParty);
		node.updateRenderState();
	}
	
	public ArrayList<ArrayList<SharedMesh>> bars = new ArrayList<ArrayList<SharedMesh>>();
	
	public static final int BAR_HEALTH = 0; 
	public static final int BAR_STAMINA = 1;
	public static final int BAR_MORALE = 2;
	public static final int BAR_SANITY = 3;
	public static final int BAR_MANA = 4;
	public static final int BAR_MAX= 4;
	
	public static ArrayList<Quad> pointQuads = new ArrayList<Quad>();
	static 
	{
		Quad qH = new Quad("HEALTH",0.3f,4f);
		qH.setLightCombineMode(LightState.OFF);
		qH.setSolidColor(ColorRGBA.red);
		pointQuads.add(qH);

		qH = new Quad("STAMINA",0.3f,4f);
		qH.setLightCombineMode(LightState.OFF);
		qH.setSolidColor(ColorRGBA.yellow);
		pointQuads.add(qH);

		qH = new Quad("MORALE",0.3f,4f);
		qH.setLightCombineMode(LightState.OFF);
		qH.setSolidColor(ColorRGBA.brown);
		pointQuads.add(qH);

		qH = new Quad("SANITY",0.3f,4f);
		qH.setLightCombineMode(LightState.OFF);
		qH.setSolidColor(ColorRGBA.orange);
		pointQuads.add(qH);

		qH = new Quad("HEALTH",0.3f,4f);
		qH.setLightCombineMode(LightState.OFF);
		qH.setSolidColor(ColorRGBA.blue);
		pointQuads.add(qH);

	}
	
	public void addNextPointBars(float origoX, float origoY, MemberPerson p)
	{
		ArrayList<SharedMesh> barQuads = new ArrayList<SharedMesh>();
		for (int i=0; i<=BAR_MAX; i++)
		{
			SharedMesh q = new SharedMesh("BAR_"+i,pointQuads.get(i));
			q.setLocalTranslation(origoX+i*2.8f,origoY,0f);
			node.attachChild(q);
			q.setLocalScale(10);
			barQuads.add(q);
		}
		bars.add(barQuads);
	}
	
	public void addMembers(ArrayList<EntityMemberInstance> orderedParty)
	{
		try {
			int counter = 0;
			int sideYMul = 1, sideYMulFont = 1;
			float sideYBars = 1;
			float stepY = hud.core.getDisplay().getHeight()/6.6f;
			float startY = hud.core.getDisplay().getHeight()*0.8f;
			Quad frame  = null;
			try {
				frame = Window.loadImageToQuad(new File("./data/ui/portraitFrame.png"), 1.025f*hud.core.getDisplay().getWidth()/13, 1.037f*hud.core.getDisplay().getHeight()/10.3f, sideYMul*hud.core.getDisplay().getWidth()/20, 0.9988f*startY-stepY*counter);
			} catch (Exception ex)
			{
				
			}
			for (EntityMemberInstance i:orderedParty)
			{
				if (i.description instanceof MemberPerson)
				{
					MemberPerson p = (MemberPerson)i.description;
					try 
					{
						SharedMesh sm = new SharedMesh("1",frame);
						sm.setLocalTranslation(sideYMul*hud.core.getDisplay().getWidth()/20, 0.999f*startY-stepY*counter,0);
						
						Quad q = Window.loadImageToQuad(new File(p.getPicturePath()), hud.core.getDisplay().getWidth()/13, hud.core.getDisplay().getHeight()/10.3f, sideYMul*hud.core.getDisplay().getWidth()/20, startY-stepY*counter++);
						
						Node nametextNode = this.text.createText(p.foreName, 9, new ColorRGBA(1,1,0.6f,1f),false);
						
						nametextNode.setLocalTranslation(sideYMulFont*hud.core.getDisplay().getWidth()/50, startY-stepY*(counter-1)-stepY*0.37f,0);
						
						nametextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						nametextNode.setLocalScale(hud.core.getDisplay().getWidth()/800f);
						
						Node classtextNode = this.text.createText(p.professions.get(0).getSimpleName(), 9, new ColorRGBA(0.5f,0.5f,0.9f,1f),false);
						
						classtextNode.setLocalTranslation(sideYMulFont*hud.core.getDisplay().getWidth()/50, startY-stepY*(counter-1)-stepY*0.425f,0);
						
						addNextPointBars(sideYMulFont*hud.core.getDisplay().getWidth()/50+sideYBars*hud.core.getDisplay().getWidth()/13, startY-stepY*(counter-1)-stepY*0.425f + hud.core.getDisplay().getWidth()/20 , p);
						
						classtextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						classtextNode.setLocalScale(hud.core.getDisplay().getWidth()/800f);
	
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
						sideYBars = -0.4f;
					}
					
				}
				
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	
	}
	
	public void updatePoints()
	{
		
	}
	
	public void update()
	{ 
		node.detachAllChildren();
		addMembers(hud.core.gameState.player.orderedParty);
		node.updateRenderState();
	}
	
	public void show()
	{
		hud.hudNode.attachChild(node);
		node.updateRenderState();
	}
}
