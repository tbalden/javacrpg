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
package org.jcrpg.game.trigger;

import java.util.ArrayList;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.trigger.StorageObjectSideSubType;
import org.jcrpg.space.sidetype.trigger.TriggerBaseSideSubtype;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.jme.moving.TriggeredModelNode;
import org.jcrpg.threed.scene.RenderedCube;

public class StorageObjectHandler extends TriggerHandler {

	@Override
	public boolean handlesType(Cube enteredCube, RenderedCube renderedEnteredCube, Cube leftCube, RenderedCube renderedLeftCube)
	{
		ArrayList<Side> triggerSides = null;
		if (enteredCube!=null)
		{
			triggerSides = enteredCube.getTriggerSides();	
		}		
		if (triggerSides!=null)
		{
			for (Side s:triggerSides)
			{
				Object object = s.subtype;
				if (object instanceof StorageObjectSideSubType) return true;
			}
		}
		if (leftCube!=null)
		{
			triggerSides = leftCube.getTriggerSides();	
		}		
		if (triggerSides!=null)
		{
			for (Side s:triggerSides)
			{
				Object object = s.subtype;
				if (object instanceof StorageObjectSideSubType) return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean handleStaticTriggerSides(Cube enteredCube, RenderedCube renderedEnteredCube, Cube leftCube, RenderedCube renderedLeftCube)
	{
		ArrayList<Side> triggerSides = null;
		if (enteredCube!=null)
		{
			triggerSides = enteredCube.getTriggerSides();	
		}		
		if (triggerSides!=null)
		{
			System.out.println("$$$$$$$$$$$$$$ TRIGGER SIDE $$$$$$$$$$$$$$$$");
			RenderedCube rc = renderedEnteredCube;
			if (rc!=null)
			{
				System.out.println("RENDERED CUBE OKAY");
				for (Side s:triggerSides)
				{
					String[] anim = ((TriggerBaseSideSubtype)s.subtype).getEffectOnEnter();
					if (anim!=null)
					{
						System.out.println("ANIM "+anim);
						NodePlaceholder[] list = rc.hmNodePlaceholderForSide.get(s);
						if (list!=null)
						{
							System.out.println("NODE LIST "+list.length);
							for (NodePlaceholder n:list)
							{
								if (n.realNode!=null)
								{
									System.out.println("REALNODE = "+n.realNode);
									PooledNode pN = n.realNode;
									if (pN instanceof TriggeredModelNode)
									{
										if (anim.length==2)
										{
											((TriggeredModelNode) pN).playAnimation(anim[0],anim[1]);
										} else
										if (anim.length==1)
										{
											((TriggeredModelNode) pN).playAnimation(anim[0],TriggerBaseSideSubtype.TRIGGER_EFFECT_OPEN);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		
		
		triggerSides = null;
		if (leftCube!=null)
		{
			triggerSides = leftCube.getTriggerSides();	
		}		
		if (triggerSides!=null)
		{
			System.out.println("$$$$$$$$$$$$$$ LEFT TRIGGER SIDE $$$$$$$$$$$$$$$$");
			RenderedCube rc = renderedLeftCube;
			if (rc!=null)
			{
				System.out.println("RENDERED CUBE OKAY");
				for (Side s:triggerSides)
				{
					String[] anim = ((TriggerBaseSideSubtype)s.subtype).getEffectOnLeave();
					if (anim!=null)
					{
						System.out.println("ANIM "+anim);
						NodePlaceholder[] list = rc.hmNodePlaceholderForSide.get(s);
						if (list!=null)
						{
							System.out.println("NODE LIST "+list.length);
							for (NodePlaceholder n:list)
							{
								if (n.realNode!=null)
								{
									System.out.println("REALNODE = "+n.realNode);
									PooledNode pN = n.realNode;
									if (pN instanceof TriggeredModelNode)
									{
										if (anim.length==2)
										{
											((TriggeredModelNode) pN).playAnimation(anim[0],anim[1]);
										} else
										if (anim.length==1)
										{
											((TriggeredModelNode) pN).playAnimation(anim[0],TriggerBaseSideSubtype.TRIGGER_EFFECT_CLOSED);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return true;
		
	}
	
	

}
