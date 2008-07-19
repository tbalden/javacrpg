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
package org.jcrpg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.element.EncounterPhaseLineup;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.game.element.TurnActUnitTopology;
import org.jcrpg.game.logic.EvaluatorBase;
import org.jcrpg.game.logic.Impact;
import org.jcrpg.threed.input.action.CKeyAction;
import org.jcrpg.threed.moving.J3DMovingEngine;
import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.ui.window.interaction.TurnActWindow.TurnActPlayerChoiceInfo;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnit;
import org.jcrpg.world.ai.EncounterUnitData;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityScaledRelationType;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.object.BonusSkillActFormDesc;
import org.jcrpg.world.object.Weapon;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

public class EncounterLogic {
	
	public static final int ENCOUTNER_PHASE_CONTINUE = 0;
	public static final int ENCOUTNER_PHASE_RESULT_COMBAT = 1;
	public static final int ENCOUTNER_PHASE_RESULT_SOCIAL_RIVALRY = 2;
	
	GameLogic gameLogic;
	
	public EncounterLogic(GameLogic gameLogic)
	{
		this.gameLogic = gameLogic;
	}

	/**
	 * Checks if the encounter can be left by a given fragment.
	 * @param fragment
	 * @param encounters
	 * @return true if can.
	 */
	public boolean checkLeaveEncounterPhase(EntityFragment fragment, EncounterInfo encounters)
	{
		// TODO do check if instance can leave the encounter (forced to stay, or too much tension)
		if (encounters.getPhase()==Ecology.PHASE_TURNACT_COMBAT)
		{
			//ArrayList<EncounterUnitData> list = encounters.getEncounterUnitDataList(fragment);
			//long seed = getSeed(encounters);
			// TODO
			return true;
		} else
		if (encounters.getPhase()==Ecology.PHASE_TURNACT_SOCIAL_RIVALRY)
		{
			return true;
		}
		return true;
	}
	
	public long getSeed(EncounterInfo encounter)
	{
		long seed = -1;
		if (encounter.getPhase()==Ecology.PHASE_TURNACT_COMBAT)
		{
			seed = ((long)currentTurnActTurn)<<8 + gameLogic.core.gameState.engine.getNumberOfTurn();
		}
		if (encounter.getPhase()==Ecology.PHASE_TURNACT_SOCIAL_RIVALRY)
		{
			seed = ((long)currentTurnActTurn)<<8 + gameLogic.core.gameState.engine.getNumberOfTurn();
		} else
		{
			seed = ((long)currentEncounterRound)<<8 + gameLogic.core.gameState.engine.getNumberOfTurn();
		}
		return seed;
	}
	

	// encounter state related
	
	public class EncounterRoundState {
		public EntityMemberInstance initiatorSkillUser;
		public SkillInstance initiatorSkill;
		public EncounterInfo encounter;
		public int nextEventCount = 0;
		//public int maxEventCount = 0;
		public ArrayList<PlannedEncounterEvent> plan = new ArrayList<PlannedEncounterEvent>();
		public boolean playing = false;
		public long playStart = 0;
		public long maxTime = 0;
	}
	public class PlannedEncounterEvent
	{
		public static final int TYPE_PAUSE = 1;
		public int type = 0;
		
	}
	EncounterRoundState encounterRoundState = null;
	
	public void doEncounterRound(EntityMemberInstance initiatorSkillUser, SkillInstance initiatorSkill, EncounterInfo encounter)
	{
		encounterRoundState = new EncounterRoundState();
		encounterRoundState.initiatorSkillUser = initiatorSkillUser;
		encounterRoundState.initiatorSkill = initiatorSkill;
		encounterRoundState.encounter = encounter;
		
		
		// TODO do a preliminary skill usage plan into state with eventCount
		
		// TODO skill use, and check tension levels if combat or social rivalry happens.

		// demo pause
		PlannedEncounterEvent p = new PlannedEncounterEvent();
		p.type = PlannedEncounterEvent.TYPE_PAUSE;
		encounterRoundState.plan.add(p);

		playEncStep();
	}
	public void playEncStep()
	{
		if (encounterRoundState.nextEventCount>=encounterRoundState.plan.size())
		{
			// ending screenplay..check result:			
			int result = ENCOUTNER_PHASE_RESULT_COMBAT;
			if (result==EncounterLogic.ENCOUTNER_PHASE_RESULT_COMBAT)
			{				
				gameLogic.core.gameState.gameLogic.newEncounterPhase(encounterRoundState.encounter, Ecology.PHASE_TURNACT_COMBAT, true);
			} else
			if (result==EncounterLogic.ENCOUTNER_PHASE_RESULT_SOCIAL_RIVALRY)
			{
				gameLogic.core.gameState.gameLogic.newEncounterPhase(encounterRoundState.encounter, Ecology.PHASE_TURNACT_SOCIAL_RIVALRY, true);
			} else
			if (result==EncounterLogic.ENCOUTNER_PHASE_CONTINUE)
			{
				gameLogic.core.uiBase.hud.mainBox.addEntry("Next encounter round...");
				gameLogic.core.encounterWindow.toggle();
				encounterRoundState = null;
			}			
		} else
		{
			if (encounterRoundState.plan.get(encounterRoundState.nextEventCount).type == PlannedEncounterEvent.TYPE_PAUSE) 
			{
				encounterRoundState.playing = true;
				encounterRoundState.playStart = System.currentTimeMillis();
				encounterRoundState.maxTime = 1000;
				encounterRoundState.nextEventCount++;
			} else {
				// unknown step type...
				encounterRoundState.nextEventCount++;
				playEncStep();
			}
		}
	}
	
	public void checkEncounterCallbackNeed()
	{
		if (encounterRoundState!=null && encounterRoundState.playing)
		{
			if (encounterRoundState.maxTime>0 && encounterRoundState.maxTime<System.currentTimeMillis()-encounterRoundState.playStart)
			{
				encounterRoundState.playing = false;
				playEncStep();
			}
		} 
	}
	

	public class TurnActTurnState {
		public TurnActPlayerChoiceInfo choiceInfo = null;
		public EncounterInfo encounter;
		public int nextEventCount = -1;
		//public int maxEventCount = 0;
		
		public Object currentActor = null;
		public Object currentTarget = null;
		
		public HashMap<EntityMemberInstance, TurnActMemberChoice> memberChoices = new HashMap<EntityMemberInstance, TurnActMemberChoice>();
		
		public ArrayList<PlannedTurnActEvent> plan = new ArrayList<PlannedTurnActEvent>();
		public boolean playing = false;
		public long playStart = 0;
		public long maxTime = 0;
		
		public TurnActTurnState(EncounterInfo encounter, TurnActPlayerChoiceInfo info)
		{
			this.choiceInfo = info;
			this.encounter = encounter;
		}
		
		public PlannedTurnActEvent getCurrentEvent()
		{
			return plan.get(nextEventCount);
		}
		
		public void highlightActor(boolean on)
		{
			if (currentActor==null) return;
			if (currentActor instanceof EntityMemberInstance)
			{
				EntityMemberInstance i = (EntityMemberInstance)currentActor;
				if (i.parentFragment == gameLogic.core.gameState.player.theFragment)
				{
					gameLogic.core.uiBase.hud.characters.highlightCharacter(i, on);
				}
			}
		}
		public void highlightTarget(boolean on)
		{
			if (currentTarget==null) return;
			if (currentTarget instanceof EntityMemberInstance)
			{
				EntityMemberInstance i = (EntityMemberInstance)currentTarget;
				if (i.parentFragment == gameLogic.core.gameState.player.theFragment)
				{
					gameLogic.core.uiBase.hud.characters.targetCharacter(i, on);
				}
			}
		}
		
	}
	public class PlannedTurnActEvent
	{
		public TurnActMemberChoice choice = null;
		public static final int TYPE_PAUSE = 1;
		public static final int TYPE_MEMBER_CHOICE = 2;
		
		public static final int STATE_INIT = 0;
		public static final int STATE_RESULT = 1;
		
		public String initMessage = null;
		public int type = 0;
		
		public int minTime = 0;
		
		public int internalState = STATE_INIT;
		
	}
	TurnActTurnState turnActTurnState = null;
	
	public int currentEncounterRound = 0;
	public int currentTurnActTurn = 0;
	
	public void doTurnActTurn(TurnActPlayerChoiceInfo info, EncounterInfo encountered)
	{
		turnActTurnState = new TurnActTurnState(encountered,info);
		turnActTurnState.encounter = encountered;
		long seed = getSeed(encountered);
		
		ArrayList<EncounterUnitData> dataList = encountered.getEncounterUnitDataList(null);
		
		TreeMap<Float, EntityMemberInstance> orderedActors = new TreeMap<Float,EntityMemberInstance>();

		// going through encountered units, updating effects.
		for (EncounterUnitData data:dataList)
		{
			data.updateMemberStateEffects((int)seed, gameLogic.core.gameState.engine.getWorldMeanTime().getTimeInRound()+currentTurnActTurn, gameLogic.core.gameState.engine.getWorldMeanTime());
		}		
		
		// going through encountered units.
		for (EncounterUnitData data:dataList)
		{
			ArrayList<EntityMemberInstance> instances = data.generatedMembers;
			if (instances!=null)
			for (EntityMemberInstance mi: instances)
			{
				mi.encounterData = data;
				TurnActMemberChoice c = mi.makeTurnActChoice(data, encountered);
				turnActTurnState.memberChoices.put(mi, c);
				if (c==null) {
					c = new TurnActMemberChoice();
					c.member = mi;
					c.doNothing = true;
				}
				if (!c.doNothing) {
					float[] speeds = EvaluatorBase.evaluateActFormTimesWithSpeed((int)seed++, mi, c.skill, c.skillActForm, c.usedObject);
					for (float s:speeds) {
						while (orderedActors.get(s)!=null)
						{
							s+=0.0001f;
						}
						orderedActors.put(s, mi);
					}
				} else
				{
					float s = 1000f;
					while (orderedActors.get(s)!=null)
					{
						s+=0.0001f;
					}
					orderedActors.put(s, mi); // resters to the end of round
				}
			}
		}
		for (TurnActMemberChoice playerChoice:info.getChoices())
		{
			Jcrpg.LOGGER.finer("PLAYER CHOICE TIME... "+playerChoice.member.description.getName());
			turnActTurnState.memberChoices.put(playerChoice.member, playerChoice);
			if (!playerChoice.doNothing) {
				float[] speeds = EvaluatorBase.evaluateActFormTimesWithSpeed((int)seed++, playerChoice);
				for (float s:speeds) {
					while (orderedActors.get(s)!=null)
					{
						s+=0.0001f;
					}
					orderedActors.put(s, playerChoice.member);
				}
			} else
			{
				float s = 1000f;
				while (orderedActors.get(s)!=null)
				{
					s+=0.0001f;
				}
				orderedActors.put(s, playerChoice.member); // resters to the end of round
			}
		}
		int step = 0;
		for (Float miSpeed:orderedActors.keySet())
		{
			step++;
			EntityMemberInstance mi = orderedActors.get(miSpeed);
			TurnActMemberChoice c = turnActTurnState.memberChoices.get(mi);
			String message = "";
			if (c!=null) 
			{
				message = c.getInitMessage();				
				PlannedTurnActEvent p = new PlannedTurnActEvent();
				p.type = PlannedTurnActEvent.TYPE_MEMBER_CHOICE;
				p.choice = c;
				p.initMessage = message;
				p.minTime = 600;
				turnActTurnState.plan.add(p);
				// adding a bit of pause
				p = new PlannedTurnActEvent();
				p.minTime = 800;
				p.type = PlannedTurnActEvent.TYPE_PAUSE;
				turnActTurnState.plan.add(p);
			}
			else 
			{
				PlannedTurnActEvent p = new PlannedTurnActEvent();
				p.type = PlannedTurnActEvent.TYPE_PAUSE;
				p.minTime = 1000;
				message = step+". "+mi.description.getName() + " inactive.";
				p.initMessage = message;
				turnActTurnState.plan.add(p);
			}
			
		}

		playTurnActStep();
	}

	public void checkTurnActCallbackNeed()
	{
		synchronized (mutex) { 
			if (turnActTurnState!=null && turnActTurnState.playing)
			{
				if (turnActTurnState.maxTime>0 && turnActTurnState.maxTime<System.currentTimeMillis()-turnActTurnState.playStart)
				{
					int eventType = turnActTurnState.getCurrentEvent().type;
					if (eventType==PlannedTurnActEvent.TYPE_MEMBER_CHOICE)
					{
						
						if (turnActTurnState.getCurrentEvent().internalState==PlannedTurnActEvent.STATE_INIT)
						{
							if (J3DMovingEngine.isEnginePlaying()) 
							{
								return;
							}
							
							turnActTurnState.getCurrentEvent().internalState++;
							
							TurnActMemberChoice choice = turnActTurnState.getCurrentEvent().choice;
							if (!choice.doNothing && (choice.doUse || choice.skill!=null))
							{
								long seed = ((long)currentTurnActTurn)<<8 + gameLogic.core.gameState.engine.getNumberOfTurn();
								Impact impact = EvaluatorBase.evaluateActFormSuccessImpact((int)seed+turnActTurnState.nextEventCount, choice, turnActTurnState);
								//impact.notifyUI(gameLogic.core.uiBase.hud.mainBox);
								if (impact.additionalEffectsToPlay!=null)
								{
									for (BonusSkillActFormDesc desc:impact.additionalEffectsToPlay)
									{
										EffectProgram ePB = desc.form.getEffectProgram();
										if (ePB!=null)
										{
											try 
											{
												gameLogic.core.mEngine.playEffectProgram(ePB, choice.member.encounterData.visibleForm, choice.target.visibleForm);
											} catch (Exception ex)
											{
												ex.printStackTrace();
											}
										}
									}
								}
								
								for (TextEntry m:impact.messages)
								{
									gameLogic.core.uiBase.hud.mainBox.addEntry(m);
								}
								if (impact.success)
								{
									gameLogic.core.uiBase.hud.mainBox.addEntry(new TextEntry("HIT!",ColorRGBA.red));
								} else
								{
									gameLogic.core.uiBase.hud.mainBox.addEntry("Miss.");
								}
								if (choice.usedObject!=null)
								{
									if (choice.usedObject.description instanceof Weapon)
									{
										if (impact.success) // check hit
										{
											String sound = ((Weapon)choice.usedObject.description).getHitSound();
											if (sound!=null)
											{
												gameLogic.core.audioServer.playLoading(sound, "objects");
											}
											sound = null;											
											
										} else
										{
											String sound = ((Weapon)choice.usedObject.description).getMissSound();
											if (sound!=null)
											{
												gameLogic.core.audioServer.playLoading(sound, "objects");
											}
											
										}
									}
								}
								choice.member.applyImpactUnit(impact.actCost);
								
								if (choice.targetMember.encounterData.visibleForm!=null)
								{
									try 
									{
										Vector3f place = gameLogic.core.mEngine.calculatePositionVector(choice.targetMember.encounterData.visibleForm.unit,choice.targetMember.encounterData.visibleForm,true);
										/*NodePlaceholder ph = choice.targetMember.encounterData.visibleForm.unit.nodePlaceholders.iterator().next();
										Vector3f place = ph.getLocalTranslation();
										place = place.add(ph.model.disposition[0],ph.model.disposition[1],ph.model.disposition[2]);
										place.addLocal(0f,-0.9f,0f);*/
										CKeyAction.setCameraDirection(gameLogic.core.getCamera(), place.x, place.y, place.z);
									} catch (Exception ex)
									{
										//ex.printStackTrace();
									}
								}
								
								if (impact.success) {
									
									int[] counters = choice.target.applyImpactUnit(impact);
									for (TextEntry m:impact.applyMessages)
									{
										gameLogic.core.uiBase.hud.mainBox.addEntry(m);
									}
									if (choice.member instanceof PersistentMemberInstance)
									{
										// increasing kill/neut. counters
										((PersistentMemberInstance)choice.member).killCount+=counters[0];
										((PersistentMemberInstance)choice.member).neutralizeCount+=counters[1];
									}
									
									if (choice.isWithImpact())
									{
										String sound = null;
										if (!choice.target.destroyed && choice.isDestructive())
										{											
											sound = choice.target.getSound(AudioDescription.T_PAIN);
										}
										if (!choice.target.destroyed && choice.isConstructive())
										{
											sound = choice.target.getSound(AudioDescription.T_JOY);
										}
										if (choice.target.destroyed)
										{
											sound = choice.target.getSound(AudioDescription.T_DEATH);
										}
										if (sound!=null)
										{
											gameLogic.core.audioServer.playLoading(sound, "ai");
										} else
										{
											// party group might be effected in this case, check for
											// collected sounds in impact
											if (impact.soundsToPlay!=null && impact.soundsToPlay.size()>0)
											{
												sound = impact.soundsToPlay.get(0);
												gameLogic.core.audioServer.playLoading(sound, "ai");
											}
										}
									}
									if (!choice.target.destroyed)
									{
										if (choice.target.isRendered()) {
											if (choice.isDestructive()) {
												choice.target.visibleForm.unit.startPain(choice.member.encounterData.visibleForm);
											}
										}
										choice.target.updateNameInTurnActPhase();
									} else
									{
										// destroyed...
										if (choice.target.isRendered()) {
											choice.target.visibleForm.unit.startDeath(choice.member.encounterData.visibleForm,null);
										}
									}
								} else
								{
									if (choice.target.isRendered()) {
										if (choice.isDestructive()) {
											choice.target.visibleForm.unit.startDefense(choice.member.encounterData.visibleForm,null);
										}
									}
								}
							}
						} else
						{
							if (J3DMovingEngine.isEnginePlaying()) 
							{
								return;
							}
							TurnActMemberChoice choice = turnActTurnState.getCurrentEvent().choice;
							if (choice.target!=null)
							{
								if (choice.target.destroyed)
								{
									choice.target.clearUnitOut();
								}
							}
							
							turnActTurnState.playing = false;
							Jcrpg.LOGGER.finer("FINISHED RESULT INTERNAL STEP...");
							playTurnActStep();
						}
					}
					else
					if (eventType==PlannedTurnActEvent.TYPE_PAUSE)
					{
						turnActTurnState.playing = false;
						Jcrpg.LOGGER.finer("FINISHED PAUSE STEP...");
						playTurnActStep();
					}
				}
			}
		}
	}
	
	public Object mutex = new Object();
	
	/**
	 * Tells if an encounter info is still with enemy members (except player of course) - so if it is finished winning.
	 * or not.
	 * @param encounter
	 * @return
	 */
	public boolean isEncounterFinishedWinning(EncounterInfo encounter)
	{
		ArrayList<EncounterUnitData> list = turnActTurnState.encounter.getEncounterUnitDataList(gameLogic.player.theFragment);
		if (list==null) return true;
		for (EncounterUnitData data:list)
		{
			if (!data.friendly && !data.isNeutralized()) return false; // there's still enemy...
		}
		return true; // no enemy left
	}
	
	public boolean isEncounterFinishedNeutralized(EncounterInfo encounter)
	{
		for (EntityMemberInstance m:encounter.playerIfPresent.getFollowingMembers())
		{
			if (!m.memberState.isNeutralized())
				// there's still someone non-neutral, no end yet.
				return false;
		}
		// all neutral, lost!
		return true;
	}
	
	
	public boolean isEncounterFinishedLosing(EncounterInfo encounter)
	{
		for (EntityMemberInstance m:encounter.playerIfPresent.getFollowingMembers())
		{
			if (!m.isDead())
				// there's still someone alive, no losing yet.
				return false;
		}
		// all dead, lost!
		return true;
	}
	
	/**
	 * Finish encounter prevailing player...
	 * @param encounter
	 */
	public void finishEncounterWin(EncounterInfo encounter)
	{
		gameLogic.core.uiBase.hud.mainBox.addEntry("Your party has prevailed!");
		gameLogic.core.uiBase.hud.mainBox.addEntry(new TextEntry("Encounters finished", ColorRGBA.yellow));
		gameLogic.endPlayerEncounters();
		gameLogic.core.getKeyboardHandler().noToggleWindowByKey=false;
		gameLogic.core.gameState.switchToEncounterScenario(false, null);
		gameLogic.core.postEncounterWindow.setPageData((PartyInstance)encounter.playerIfPresent.instance, encounter);
		gameLogic.core.postEncounterWindow.toggle();
	}

	/**
	 * Finish encounter neutralized player...
	 * @param encounter
	 */
	public void finishEncounterNeutralized(EncounterInfo encounter)
	{
		// TODO relationship settings?
		gameLogic.core.uiBase.hud.mainBox.addEntry("Your party run out of incentive!");
		gameLogic.core.uiBase.hud.mainBox.addEntry(new TextEntry("Encounters finished", ColorRGBA.yellow));
		gameLogic.endPlayerEncounters();
		gameLogic.core.gameState.engine.turnFinishedForPlayer();
		gameLogic.core.getKeyboardHandler().noToggleWindowByKey=false;
		gameLogic.core.gameState.switchToEncounterScenario(false, null);
	}
	
	/**
	 * Finish encounter escaping...
	 * @param encounter
	 */
	public void finishEncounterEscaping(EncounterInfo encounter)
	{
		// TODO relationship settings?
		gameLogic.core.uiBase.hud.mainBox.addEntry("Your party run away!");
		gameLogic.core.uiBase.hud.mainBox.addEntry(new TextEntry("Encounters finished", ColorRGBA.yellow));
		gameLogic.endPlayerEncounters();
		gameLogic.core.gameState.engine.turnFinishedForPlayer();
		gameLogic.core.getKeyboardHandler().noToggleWindowByKey=false;
		gameLogic.core.gameState.switchToEncounterScenario(false, null);
	}

	public void finishEncounterLose(EncounterInfo encounter)
	{
		gameLogic.core.uiBase.hud.mainBox.addEntry("YOUR PARTY IS DEAD!");
		gameLogic.core.uiBase.hud.mainBox.addEntry("Game Over.");
		gameLogic.core.uiBase.hud.characters.hide();
		gameLogic.ecology.gameLost();
		gameLogic.core.gameLost = true;
		gameLogic.core.getKeyboardHandler().noToggleWindowByKey=false;
		gameLogic.core.mainMenu.toggle();
	}

	/**
	 * Play next turn act plan step.
	 */
	public void playTurnActStep()
	{
		synchronized (mutex) {
			Jcrpg.LOGGER.finer("playTurnActStep ");
			if (turnActTurnState!=null) 
			{
				turnActTurnState.highlightTarget(false);
				turnActTurnState.highlightActor(false);
				turnActTurnState.currentActor = null;
				turnActTurnState.currentTarget = null;
			}
			turnActTurnState.nextEventCount++;
			if (turnActTurnState.nextEventCount>=turnActTurnState.plan.size())
			{
				currentTurnActTurn++;

				// removing neutralized
				ArrayList<EncounterUnit> leaving = turnActTurnState.encounter.filterNeutralsForSubjectBeforeTurnAct(true,(PartyInstance)turnActTurnState.encounter.playerIfPresent.instance);
				postLeaversMessage(leaving);
				
				if (isEncounterFinishedLosing(turnActTurnState.encounter))
				{
					finishEncounterLose(turnActTurnState.encounter);
				} else
				if (isEncounterFinishedWinning(turnActTurnState.encounter))
				{
					finishEncounterWin(turnActTurnState.encounter);
				} else
				if (isEncounterFinishedNeutralized(turnActTurnState.encounter))
				{
					finishEncounterNeutralized(turnActTurnState.encounter);
				} else
				if (turnActTurnState.choiceInfo.isEscaping())
				{
					finishEncounterEscaping(turnActTurnState.encounter);
				} else
				{
					gameLogic.core.uiBase.hud.mainBox.addEntry("Next turn comes...");
					gameLogic.core.turnActWindow.toggle();
				}
				turnActTurnState = null;
				
			} else
			{
				PlannedTurnActEvent event = turnActTurnState.getCurrentEvent();
				if (event.type == PlannedTurnActEvent.TYPE_PAUSE) 
				{
					
					// PAUSE EVENT
					
					turnActTurnState.maxTime = turnActTurnState.getCurrentEvent().minTime;
					turnActTurnState.playing = true;
					turnActTurnState.playStart = System.currentTimeMillis();
					
				} else
				if (turnActTurnState.getCurrentEvent().type == PlannedTurnActEvent.TYPE_MEMBER_CHOICE) 
				{
					
					// MEMBER CHOICE EVENT
					
					TurnActMemberChoice choice = event.choice;
					
					// dead cannot do things...
					if (choice.member.isDead() || choice.member.memberState.isNeutralized()) 
					{
						playTurnActStep();
						return;
					}
					
					turnActTurnState.currentActor = choice.member;
					turnActTurnState.currentTarget = choice.targetMember;
					
					// setting the camera to actor...
					if (choice.member.encounterData.visibleForm!=null)
					{
						try 
						{
							Vector3f place = gameLogic.core.mEngine.calculatePositionVector(choice.member.encounterData.visibleForm.unit,choice.member.encounterData.visibleForm,true);
							/*NodePlaceholder ph = choice.targetMember.encounterData.visibleForm.unit.nodePlaceholders.iterator().next();
							Vector3f place = ph.getLocalTranslation();
							place = place.add(ph.model.disposition[0],ph.model.disposition[1],ph.model.disposition[2]);
							place.addLocal(0f,-0.9f,0f);*/
							CKeyAction.setCameraDirection(gameLogic.core.getCamera(), place.x, place.y, place.z);
						} catch (Exception ex)
						{
							//ex.printStackTrace();
						}
					} else
					{
						// no visible actor, set to target..
						if (choice.targetMember!=null && choice.targetMember.encounterData.visibleForm!=null)
						{
							try 
							{
								Vector3f place = gameLogic.core.mEngine.calculatePositionVector(choice.targetMember.encounterData.visibleForm.unit,choice.targetMember.encounterData.visibleForm,true);
								/*NodePlaceholder ph = choice.targetMember.encounterData.visibleForm.unit.nodePlaceholders.iterator().next();
								Vector3f place = ph.getLocalTranslation();
								place = place.add(ph.model.disposition[0],ph.model.disposition[1],ph.model.disposition[2]);
								place.addLocal(0f,-0.9f,0f);*/
								CKeyAction.setCameraDirection(gameLogic.core.getCamera(), place.x, place.y, place.z);
							} catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
					
					turnActTurnState.highlightActor(true);
					turnActTurnState.highlightTarget(true);
					
					// check state effects:
					if (choice.skillActForm!=null || choice.doUse)
					{
						if (!choice.member.memberState.isItDoableWithEffects(choice.skillActForm))
						{
							gameLogic.core.uiBase.hud.mainBox.addEntry(new TextEntry(choice.member.description.getName() + " " + choice.member.memberState.getBlockingEffectText(choice.skillActForm),ColorRGBA.magenta));
							playTurnActStep();
							return;
						}
					}
					
					// cannot do things on dead target... TODO necromancy override!
					if (choice.target!=null && choice.target.isDead()) 
					{
						gameLogic.core.uiBase.hud.mainBox.addEntry(choice.member.description.getName() + "'s target is no more.");
						Jcrpg.LOGGER.finer("# NOT LIVING TARGET: "+choice.target.getName());
						playTurnActStep();
						return;
					}
					if (choice.target!=null && choice.target.isNeutralized()) 
					{
						gameLogic.core.uiBase.hud.mainBox.addEntry(choice.member.description.getName() + "'s target is leaving as neutral.");
						Jcrpg.LOGGER.finer("# NOT LIVING TARGET: "+choice.target.getName());
						playTurnActStep();
						return;
					}
					
					gameLogic.core.uiBase.hud.mainBox.addEntry(choice.member.encounterData.getName());
					if (choice.doNothing && !choice.member.memberState.isExhausted() && !choice.member.memberState.isNeutralized()) {
						gameLogic.core.uiBase.hud.mainBox.addEntry(event.initMessage);
					} else
					{
						gameLogic.core.uiBase.hud.mainBox.addEntry(event.initMessage);
						if (choice.member.memberState.isExhausted())
						{
							gameLogic.core.uiBase.hud.mainBox.addEntry(choice.member.description.getName() + " is exhausted & fails.");
						}
						if (choice.member.memberState.isNeutralized())
						{
							gameLogic.core.uiBase.hud.mainBox.addEntry(choice.member.description.getName() + " is watching neutrally.");
							playTurnActStep();
							return;
						}
					}
					
					if (choice.doNothing || choice.member.memberState.isExhausted())
					{
						turnActTurnState.maxTime = event.minTime;
						turnActTurnState.playing = true;
						turnActTurnState.playStart = System.currentTimeMillis();
						Jcrpg.LOGGER.finer("### MEMBER_CHOICE "+turnActTurnState.nextEventCount);
						choice.member.memberState.replenishInOneRound();
						gameLogic.core.uiBase.hud.characters.updatePoints(choice.member);
						playTurnActStep();
						return;
					}
					else					
					if (choice.skillActForm!=null)
					{
						
						if (choice.usedObject!=null)
						{
							// ammunition check.
							if (choice.usedObject.description.needsAttachmentDependencyForSkill())
							{
								if (!choice.usedObject.hasAttachedDependencies() || !choice.member.inventory.hasOneOfTypes(choice.usedObject.getAttachedDependencies()))
								{
									gameLogic.core.uiBase.hud.mainBox.addEntry(choice.member.description.getName() + " run out of ammunition.");
									playTurnActStep();
									return;
								}
							}
						}
						
						EffectProgram eProgram = choice.getEffectProgram();
						if (eProgram!=null)
						{
							try 
							{
								gameLogic.core.mEngine.playEffectProgram(eProgram, choice.member.encounterData.visibleForm, choice.target.visibleForm);
							} catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
						
						if (choice.isDestructive()) {
							String sound = choice.member.getSound(AudioDescription.T_ATTACK);
							if (sound!=null)
							{
								gameLogic.core.audioServer.playLoading(sound, "ai");
							}
						}
						String sound = choice.skillActForm.getSound();
						if (sound!=null)
						{
							gameLogic.core.audioServer.playLoading(sound, "skills");
						}
						if (choice.member.isRendered()) {
							String anim = choice.skillActForm.animationType;
							choice.member.encounterData.visibleForm.unit.startAttack(choice.target.visibleForm, anim);
						}
					}
					
					turnActTurnState.maxTime = event.minTime;
					turnActTurnState.playing = true;
					turnActTurnState.playStart = System.currentTimeMillis();
					Jcrpg.LOGGER.finer("### MEMBER_CHOICE "+turnActTurnState.nextEventCount);
				} else
				{
					// unknown step type...
					
					playTurnActStep();
				}
			}
		}
	}
	
	public void fillInitEncounterPhaseLineup(EncounterInfo info)
	{
		EncounterPhaseLineup lineup = new EncounterPhaseLineup(info);
		info.setEncounterPhaseLineup(lineup);
		Jcrpg.LOGGER.finer("{ fillInitEncounterPhaseLineup }");
		for (EncounterUnitData d:info.getEncounterUnitDataList(gameLogic.player.theFragment))
		{
			info.getEncounterPhaseLineup().addUnit(d,d.getEncPhasePriority(info));	
		}
	}

	public void fillInitTurnActPhaseLineup(EncounterInfo info)
	{
		TurnActUnitTopology topology = new TurnActUnitTopology(info);
		info.setTopology(topology);
		Jcrpg.LOGGER.finer("{ fillInitTurnActPhaseLineup }");
		for (EncounterUnitData unit:info.getEncounterUnitDataList(null))
		{
			int line = 0;
			int level = unit.getRelationLevel(info.playerIfPresent);
			if (level<EntityScaledRelationType.NEUTRAL)
			{
				unit.friendly = false;
			} else
			if (level==EntityScaledRelationType.NEUTRAL) // TODO debug only remove this, no neutrals allowed
			{
				if (unit.parent == info.playerIfPresent)
				{
					unit.friendly = true;
					unit.partyMember = true;
					// looking up party member's index for calculation of line
					PartyInstance party = (PartyInstance)((EntityFragment)unit.parent).instance;
					int index = party.orderedParty.indexOf(unit.getFirstLivingMember());
					line = index/2;
				} else
				{
					unit.friendly = false;
				}
			}
			if (level>EntityScaledRelationType.NEUTRAL)
			{
				unit.friendly = true;
			}
			// TODO for player use orderedParty arraylist

			info.getTopology().addUnitPushing(unit,line);	
		}
		
		
	}

	public void postLeaversMessage(ArrayList<EncounterUnit> units)
	{
		if (units!=null)
		for (EncounterUnit unit:units)
		{
			gameLogic.core.uiBase.hud.mainBox.addEntry(unit.getName()+" leave(s).");
		}
	}


}
