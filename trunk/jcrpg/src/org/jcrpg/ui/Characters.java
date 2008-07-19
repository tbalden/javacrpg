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
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.threed.jme.ui.TimedNode;
import org.jcrpg.threed.jme.ui.ZoomingQuad;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.humanoid.MemberPerson;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;

public class Characters {

	public ArrayList<org.jcrpg.ui.window.element.Character> characterData = new ArrayList<org.jcrpg.ui.window.element.Character>();
	

	FontTT text;
	
	Node node = new Node();
	HUD hud = null;
	ColorMaskState deadColor = null; 
	ColorMaskState neutralColor = null; 
	public Characters(HUD hud)
	{
		deadColor = hud.base.core.getDisplay().getRenderer().createColorMaskState();
		deadColor.setBlue(false);
		deadColor.setGreen(false);
		deadColor.setEnabled(true);

		neutralColor = hud.base.core.getDisplay().getRenderer().createColorMaskState();
		neutralColor.setRed(false);
		neutralColor.setBlue(false);
		neutralColor.setEnabled(true);

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
	
	public ArrayList<Float> effectIconOrigoXPositions = new ArrayList<Float>();
	public ArrayList<Float> effectIconOrigoYPositions = new ArrayList<Float>();
	
	public ArrayList<ArrayList<Quad>> bars = new ArrayList<ArrayList<Quad>>();
	public ArrayList<ArrayList<Float>> barOrigoYPositions = new ArrayList<ArrayList<Float>>();
	
	public static final int BAR_HEALTH = 0; 
	public static final int BAR_STAMINA = 1;
	public static final int BAR_MORALE = 2;
	public static final int BAR_SANITY = 3;
	public static final int BAR_MANA = 4;
	public static final int BAR_MAX= 4;
	
	public static ArrayList<ColorRGBA> pointQuadData = new ArrayList<ColorRGBA>();
	static 
	{
		pointQuadData.add(ColorRGBA.red);
		pointQuadData.add(ColorRGBA.yellow);
		pointQuadData.add(ColorRGBA.green);
		pointQuadData.add(ColorRGBA.orange);
		pointQuadData.add(ColorRGBA.blue);

	}
	
	public ArrayList<Node> centerNode = new ArrayList<Node>();
	public ArrayList<Node> deadlyNode = new ArrayList<Node>();
	public ArrayList<Quad> frameQuads = new ArrayList<Quad>();
	public ArrayList<ZoomingQuad> pictureQuads = new ArrayList<ZoomingQuad>();
	
	float barScreenRatio = 13f;
	
	public void addMembers(ArrayList<PersistentMemberInstance> orderedParty)
	{
		bars.clear();
		barOrigoYPositions.clear();
		pictureQuads.clear();
		frameQuads.clear();
		effectIconOrigoXPositions.clear();
		effectIconOrigoYPositions.clear();
		centerNode.clear();
		deadlyNode.clear();
		try {
			int counter = 0;
			int counterPair = 0;
			int sideYMul = 1, sideYMulFont = 1;
			float sideYBars = 1;
			float stepY = hud.core.getDisplay().getHeight()/5.0f;
			float maxSizeY = hud.core.getDisplay().getHeight()/6.4f;
			float startY = hud.core.getDisplay().getHeight()*0.8f;

			for (EntityMemberInstance i:orderedParty)
			{
				if (i.description instanceof MemberPerson)
				{
					MemberPerson p = (MemberPerson)i.description;
					try 
					{
						//SharedMesh sm = new SharedMesh("1",frame);
						Quad sm  = null;
						Node center = null;
						try {
							sm = Window.loadImageToQuad(new File("./data/ui/portraitFrame.png"), 1.025f*hud.core.getDisplay().getWidth()/13, 1.037f*hud.core.getDisplay().getHeight()/10.3f, sideYMul*hud.core.getDisplay().getWidth()/20, 0.9988f*startY-stepY*counter);
							sm.setLocalTranslation(sideYMul*hud.core.getDisplay().getWidth()/20, 0.999f*startY-stepY*counterPair,0);
							frameQuads.add(sm);
							Node n = new Node();
							n.setLocalTranslation(sm.getLocalTranslation().clone());
							Node dn = new Node();
							n.attachChild(dn);
							deadlyNode.add(dn);
							centerNode.add(n);
							center = n;
						} catch (Exception ex)
						{
							
						}
						
						effectIconOrigoXPositions.add(new Float(sideYMul*hud.core.getDisplay().getWidth()/20 - hud.core.getDisplay().getWidth()/(13*2.2f) ));
						effectIconOrigoYPositions.add(new Float(0.999f*startY-stepY*counterPair + hud.core.getDisplay().getHeight()/(10.3f*2.2f)));
						
						ZoomingQuad q = Window.loadImageToZoomingQuad(new File(p.getPicturePath()), hud.core.getDisplay().getWidth()/13, hud.core.getDisplay().getHeight()/10.3f, sideYMul*hud.core.getDisplay().getWidth()/20, startY-stepY*counterPair);
						pictureQuads.add(q);
						Node nametextNode = this.text.createOutlinedText(p.foreName, 9, new ColorRGBA(1,1,0.6f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
						
						nametextNode.setLocalTranslation(sideYMulFont*hud.core.getDisplay().getWidth()/50, startY-stepY*(counterPair)-maxSizeY*0.39f,0);
						
						nametextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						nametextNode.setLocalScale(hud.core.getDisplay().getWidth()/600f);
						
						Node classtextNode = this.text.createOutlinedText(p.professions.get(0).getSimpleName(), 9, new ColorRGBA(0.5f,0.5f,0.9f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
						
						classtextNode.setLocalTranslation(sideYMulFont*hud.core.getDisplay().getWidth()/50, startY-stepY*(counterPair)-maxSizeY*0.515f,0);
						
						addNextPointBars(sideYMulFont*hud.core.getDisplay().getWidth()/50+sideYBars*hud.core.getDisplay().getWidth()/13, startY-stepY*(counterPair)-maxSizeY*0.425f + hud.core.getDisplay().getWidth()/(barScreenRatio*2f) , p);
						
						classtextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						classtextNode.setLocalScale(hud.core.getDisplay().getWidth()/600f);
	
						node.attachChild(nametextNode);
						node.attachChild(classtextNode);
						node.attachChild(sm);
						node.attachChild(q);
						node.attachChild(center);
						counter ++;
						counterPair = counter / 2;
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
					if (counter%2==1)
					{
						sideYMul = 19;
						sideYMulFont = 46;
						sideYBars = -0.4f;
					} else
					{
						sideYMul = 1;
						sideYMulFont = 1;
						sideYBars = 1;
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
			try {
				Quad q = Window.loadImageToQuad(new File("./data/ui/bar_meter.png"), hud.core.getDisplay().getWidth()/230f, hud.core.getDisplay().getHeight()/barScreenRatio, 0,0);
				//Quad q = new Quad("BAR_"+i+p.foreName+p.surName,hud.core.getDisplay().getWidth()/230f,);
				q.setSolidColor(pointQuadData.get(i));
				q.setLightCombineMode(LightState.OFF);
				q.setLocalTranslation(origoX+i*hud.core.getDisplay().getWidth()/230f,origoY,0f);
				q.setLocalScale(1f);
				barPos.add(origoY);
				barQuads.add(q);
			} catch (Exception ex){ex.printStackTrace();}
		}
		bars.add(barQuads);
		barOrigoYPositions.add(barPos);
	}
	
	public void updatePoints()
	{
		updatePoints(null);
	}
	
	private HashMap<EntityMemberInstance, HashSet<Quad>> membersAndEffectIcons = new HashMap<EntityMemberInstance, HashSet<Quad>>();	
	
	public void updateEffectIcons(EntityMemberInstance member)
	{
		if (hud.core.gameLost == true || hud.core.gameState==null || hud.core.gameState.player==null || hud.core.gameState.player.orderedParty==null) 
		{
			return;
		}
		int counter = 0;
		
		//if (bars.size()!=0)
		for (EntityMemberInstance p:hud.core.gameState.player.orderedParty)
		{
			if (member!=null && member!=p) {
				counter++;
				continue;
			}
			
			HashSet<String> iconsAlreadyAdded = new HashSet<String>();
			
			HashSet<Quad> quads = membersAndEffectIcons.get(p);
			if (quads!=null)
			{
				for (Quad q:quads)
				{
					q.removeFromParent();
				}
				quads.clear();
			} else
			{
				quads = new HashSet<Quad>();
				membersAndEffectIcons.put(p, quads);
			}
			
			float origoX = effectIconOrigoXPositions.get(counter);
			float origoY = effectIconOrigoYPositions.get(counter);
			
			float iconSize = hud.core.getDisplay().getWidth()/60f;
			float iconPlacing = iconSize*1.1f;
			
			int count = 0;
			for (StateEffect effect:p.memberState.getStateEffects())
			{
				String file = effect.getIconFilePath();
				if (iconsAlreadyAdded.contains(file)) continue;
				Quad sm  = null;
				try {
					iconsAlreadyAdded.add(file);
					sm = Window.loadImageToQuad(new File(file), iconSize, iconSize, origoX+ count*iconPlacing, origoY);
					quads.add(sm);
					node.attachChild(sm);
					node.updateRenderState();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
				count++;
			}
		}		
	}
	
	public void updatePoints(EntityMemberInstance member)
	{
		if (hud.core.gameLost == true || hud.core.gameState==null || hud.core.gameState.player==null || hud.core.gameState.player.orderedParty==null) 
		{
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
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("UPDATE ____________ "+p);
			float multiplierHealth = p.memberState.maxHealthPoint==0?0.0001f:Math.max(0f,p.memberState.healthPoint*1f)/p.memberState.maxHealthPoint; 
			float multiplierStamina = p.memberState.maxStaminaPoint==0?0.0001f:Math.max(0f,p.memberState.staminaPoint*1f)/p.memberState.maxStaminaPoint;
			float multiplierMorale = p.memberState.maxMoralePoint==0?0.0001f:Math.max(0f,p.memberState.moralePoint*1f)/p.memberState.maxMoralePoint;
			float multiplierSanity = p.memberState.maxSanityPoint==0?0.0001f:Math.max(0f,p.memberState.sanityPoint*1f)/p.memberState.maxSanityPoint;
			float multiplierMana = p.memberState.maxManaPoint==0?0.0001f:Math.max(0f,p.memberState.manaPoint*1f)/p.memberState.maxManaPoint;
			
			if (p.isDead())
			{
				Quad q= pictureQuads.get(counter);				
				q.setRenderState(deadColor);
				try
				{
					Quad dead = Window.loadImageToQuad(new File("./data/ui/characters/skull.png"), hud.core.getDisplay().getWidth()/barScreenRatio, hud.core.getDisplay().getHeight()/barScreenRatio, 0,0);
					Node n = deadlyNode.get(counter);
					n.detachAllChildren();
					n.attachChild(dead);
				} catch (Exception ex)
				{
				}
			} else
			if (p.memberState.isNeutralized())
			{
				Quad q= pictureQuads.get(counter);				
				q.setRenderState(neutralColor);
				try
				{
					Quad dead = Window.loadImageToQuad(new File("./data/ui/characters/neutral.png"), hud.core.getDisplay().getWidth()/barScreenRatio, hud.core.getDisplay().getHeight()/barScreenRatio, 0,0);
					Node n = deadlyNode.get(counter);
					n.detachAllChildren();
					n.attachChild(dead);
				} catch (Exception ex)
				{
				}
			} else
			{
				Quad q= pictureQuads.get(counter);
				q.clearRenderState(RenderState.RS_COLORMASK_STATE);
			}
			highlightCharacter(p, false);
		
			float[] mul = new float[] {multiplierHealth,multiplierStamina,multiplierMorale,multiplierSanity,multiplierMana};
			
			ArrayList<Quad> m = bars.get(counter);
			ArrayList<Float> pos = barOrigoYPositions.get(counter);
			for (int i=0; i<=BAR_MAX; i++)
			{
				//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(m.get(i));
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
	
	public void highlightCharacter(EntityMemberInstance member, boolean highlightOn)
	{
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("//# HIGHLIGHTING: "+member.description.getName()+" "+highlightOn);
		int counter = 0;
		for (EntityMemberInstance p:hud.core.gameState.player.orderedParty)
		{
			if (p==member)
			{
				ZoomingQuad q= pictureQuads.get(counter);				
				Quad fq= frameQuads.get(counter);
				if (highlightOn)
				{
					q.setSolidColor(ColorRGBA.orange);
					fq.setSolidColor(ColorRGBA.red);
					q.startZoomCycle();
				} else
				{
					q.setSolidColor(ColorRGBA.white);
					fq.setSolidColor(ColorRGBA.white);
				}
			}
			counter++;
		}		
	}
	
	ColorRGBA targetColor = new ColorRGBA(0.9f,0.5f,0.5f,1f);

	public void targetCharacter(EntityMemberInstance member, boolean highlightOn)
	{
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("//# TARGET HIGHLIGHTING: "+member.description.getName()+" "+highlightOn);
		int counter = 0;
		for (EntityMemberInstance p:hud.core.gameState.player.orderedParty)
		{
			if (p==member)
			{
				ZoomingQuad q= pictureQuads.get(counter);				
				Quad fq= frameQuads.get(counter);
				if (highlightOn)
				{
					q.setSolidColor(targetColor);
					fq.setSolidColor(ColorRGBA.red);
					q.startZoomCycle();
				} else
				{
					q.setSolidColor(ColorRGBA.white);
					fq.setSolidColor(ColorRGBA.white);
				}
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
	
	/**
	 * Used when party creation. 
	 */
	public void showNoPointUpdate()
	{
		hud.hudNode.attachChild(node);
		node.updateRenderState();
	}

	public void show()
	{
		updatePoints();
		hud.hudNode.attachChild(node);
		node.updateRenderState();
	}
	
	public void visualizeImpact(EntityMemberInstance member, ImpactUnit u)
	{
		int count = 0;
		for (EntityMemberInstance p:hud.core.gameState.player.orderedParty)
		{
			if (p==member)
			{
				Node center = centerNode.get(count);
				
				Node n = new Node();
				int counter = 0;
				int addedCounter = 0;
				for (Integer i:u.orderedImpactPoints)
				{
					if (i!=null && i!=0)
					{
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("_#_#_#_ VISUALIZING "+member.description.getName()+" "+counter+" = "+i);
						ColorRGBA color = Characters.pointQuadData.get(counter);
						Node slottextNode = FontUtils.textNonBoldVerdana.createOutlinedText(""+i, 1,color,new ColorRGBA(0.8f,0.8f,0.8f,1f),true);
						slottextNode.setLocalTranslation(0,0,0);
						slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						slottextNode.setLocalScale(hud.core.getDisplay().getWidth()/30f);
						n.attachChild(slottextNode);
						n.updateRenderState();
						addedCounter++;
					}
					counter++;
				}
				n.setLocalTranslation(new Vector3f(0f,0f,0f));
				//n.setLocalScale(0.17f);
				TimedNode fn = new TimedNode();
				fn.attachChild(n);
				fn.updateRenderState();
				center.attachChild(fn);	
				center.updateRenderState();
				fn.startCounting();
				node.updateRenderState();
				
			}
			count ++;
		}
	}
	
	
}
