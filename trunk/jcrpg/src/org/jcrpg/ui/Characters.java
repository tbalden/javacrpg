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
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;

import com.jme.math.Vector3f;
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
		createBarQuads();
		this.hud = hud;
		text = FontUtils.textVerdana;
	}
	
	public void hide()
	{
		node.removeFromParent();
	}
	
	public void updateForPartyCreation(ArrayList<PersistentMemberInstance> orderedParty)
	{
		node.detachAllChildren();
		addMembers(orderedParty);
		node.updateRenderState();
	}
	
	public ArrayList<ArrayList<Quad>> bars = new ArrayList<ArrayList<Quad>>();
	public ArrayList<ArrayList<Float>> barOrigoYPositions = new ArrayList<ArrayList<Float>>();
	
	public static final int BAR_HEALTH = 0; 
	public static final int BAR_STAMINA = 1;
	public static final int BAR_MORALE = 2;
	public static final int BAR_SANITY = 3;
	public static final int BAR_MANA = 4;
	public static final int BAR_MAX= 4;
	
	public ArrayList<ColorRGBA> pointQuadData = new ArrayList<ColorRGBA>();
	public void createBarQuads()
	{
		pointQuadData.add(ColorRGBA.red);
		pointQuadData.add(ColorRGBA.yellow);
		pointQuadData.add(ColorRGBA.brown);
		pointQuadData.add(ColorRGBA.orange);
		pointQuadData.add(ColorRGBA.blue);

	}
	
	float barScreenRatio = 13f;
	
	public void addMembers(ArrayList<PersistentMemberInstance> orderedParty)
	{
		bars.clear();
		barOrigoYPositions.clear();
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
						
						addNextPointBars(sideYMulFont*hud.core.getDisplay().getWidth()/50+sideYBars*hud.core.getDisplay().getWidth()/13, startY-stepY*(counter-1)-stepY*0.425f + hud.core.getDisplay().getWidth()/(barScreenRatio*2f) , p);
						
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
	
	public void addNextPointBars(float origoX, float origoY, MemberPerson p)
	{
		ArrayList<Quad> barQuads = new ArrayList<Quad>();
		ArrayList<Float> barPos = new ArrayList<Float>();
		for (int i=0; i<=BAR_MAX; i++)
		{
			Quad q = new Quad("BAR_"+i+p.foreName+p.surName,hud.core.getDisplay().getWidth()/230f,hud.core.getDisplay().getHeight()/barScreenRatio);
			q.setSolidColor(pointQuadData.get(i));
			q.setLightCombineMode(LightState.OFF);
			q.setLocalTranslation(origoX+i*hud.core.getDisplay().getWidth()/230f,origoY,0f);
			q.setLocalScale(1f);
			barPos.add(origoY);
			barQuads.add(q);
		}
		bars.add(barQuads);
		barOrigoYPositions.add(barPos);
	}
	
	public void updatePoints()
	{
		updatePoints(null);
	}
	public void updatePoints(EntityMemberInstance member)
	{
		if (hud.core.gameLost == true || hud.core.gameState==null || hud.core.gameState.player==null || hud.core.gameState.player.orderedParty==null) 
		{
			//System.out.println("############################__________________________ no possible update...");
			return;
		}
		int counter = 0;
		
		if (bars.size()!=0)
		for (EntityMemberInstance p:hud.core.gameState.player.orderedParty)
		{
			if (member!=null && member!=p) {
				counter++;
				continue;
			}
			//System.out.println("UPDATE ____________ "+p);
			float multiplierHealth = p.memberState.maxHealthPoint==0?0.0001f:Math.max(0f,p.memberState.healthPoint*1f)/p.memberState.maxHealthPoint; 
			float multiplierStamina = p.memberState.maxStaminaPoint==0?0.0001f:Math.max(0f,p.memberState.staminaPoint*1f)/p.memberState.maxStaminaPoint;
			float multiplierMorale = p.memberState.maxMoralePoint==0?0.0001f:Math.max(0f,p.memberState.moralePoint*1f)/p.memberState.maxMoralePoint;
			float multiplierSanity = p.memberState.maxSanityPoint==0?0.0001f:Math.max(0f,p.memberState.sanityPoint*1f)/p.memberState.maxSanityPoint;
			float multiplierMana = p.memberState.maxManaPoint==0?0.0001f:Math.max(0f,p.memberState.manaPoint*1f)/p.memberState.maxManaPoint;
		
			float[] mul = new float[] {multiplierHealth,multiplierStamina,multiplierMorale,multiplierSanity,multiplierMana};
			
			ArrayList<Quad> m = bars.get(counter);
			ArrayList<Float> pos = barOrigoYPositions.get(counter);
			for (int i=0; i<=BAR_MAX; i++)
			{
				//System.out.println(m.get(i));
				m.get(i).setLocalScale(new Vector3f(1f,mul[i]*1f,1f));
				m.get(i).updateRenderState();
				m.get(i).removeFromParent();
				Float origoY = pos.get(i);
				if (mul[i]!=0) {
					m.get(i).getLocalTranslation().setY( origoY - ( (hud.core.getDisplay().getHeight()/(barScreenRatio*2f)) * (1f - mul[i]) ) );
				}
				node.attachChild(m.get(i));
				node.updateRenderState();
			}
			counter++;
		}
		
		
	}
	
	public void update()
	{ 
		node.detachAllChildren();
		addMembers(hud.core.gameState.player.orderedParty);
		updatePoints();
		node.updateRenderState();
	}
	
	public void show()
	{
		updatePoints();
		hud.hudNode.attachChild(node);
		node.updateRenderState();
	}
}
