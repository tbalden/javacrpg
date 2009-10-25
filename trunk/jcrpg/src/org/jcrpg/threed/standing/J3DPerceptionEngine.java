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

package org.jcrpg.threed.standing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.jme.ui.FadeController;
import org.jcrpg.threed.jme.ui.FadeController.FadeMode;
import org.jcrpg.threed.moving.J3DMovingEngine;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.fauna.PerceptedVisibleForm;

import com.jme.scene.Node;

public class J3DPerceptionEngine extends J3DMovingEngine {

	public J3DPerceptionEngine(J3DCore core) {
		super(core);
		needsFadeIn = true;
	}
	
	
	
	public HashSet<String> renderedIdentifiers = new HashSet<String>();
	
	public HashSet<Long> unitOccupiedCoordinates = new HashSet<Long>();
	
	
	
	/**
	 * Renders the moving units inside the render distance : looks for life forms in the World in reach of the player.
	 */
	public void renderPercepted(Collection<PerceptedVisibleForm> forms)
	{
		
		//clearPreviousUnits();
		int i=0;
		HashSet<String> newRenderedIds = new HashSet<String>();
		for (PerceptedVisibleForm form:forms)
		{
			if (form.notRendered) continue;
			if (!form.updateCoordinatesBasedOnOccupation(unitOccupiedCoordinates)) continue;
			newRenderedIds.add(form.getIdentifier());
			if (renderedIdentifiers.contains(form.getIdentifier())) {
				continue;
			}
			System.out.println("@@@@@@@@@@@@@@@@@@@@@ ADDING NEW UNIT: "+form.getIdentifier());
			RenderedMovingUnit unit = materializeLifeForm(form);
			unit.direction = (i%2==1?0:1);
			NodePlaceholder[] placeHolders = core.modelPool.loadMovingPlaceHolderObjects(unit, unit.models, false);
			renderNodes(placeHolders, unit);

			i++;			
		}
		renderedIdentifiers.removeAll(newRenderedIds);
		HashSet<String> toRemove = renderedIdentifiers;
		renderedIdentifiers = newRenderedIds;
		clearPreviousUnits(toRemove);
		
		renderToViewPort(0f);
	}

	public void clearPreviousUnits(HashSet<String> toRemove)
	{
		ArrayList<String> toClear = new ArrayList<String>();
		for (RenderedMovingUnit u:units.values())
		{
			if (toRemove.contains(((PerceptedVisibleForm)u.form).getIdentifier()))
			{
				((PerceptedVisibleForm)u.form).cleanOccupation(unitOccupiedCoordinates);
				toClear.add(u.form.uniqueId);
				System.out.println("//////////////////// REMOVING UNIT: "+((PerceptedVisibleForm)u.form).getIdentifier());
				for (NodePlaceholder n:u.nodePlaceholders)
				{
					PooledNode pooledRealNode = n.realNode;
					n.realNode = null;
					clearUnitTextNodes(u);
					if (pooledRealNode!=null) {
						Node realNode = (Node)pooledRealNode;
						//if (J3DCore.SETTINGS.SHADOWS) core.removeOccludersRecoursive(realNode);
						//realNode.removeFromParent();
						//core.modelPool.releaseNode(pooledRealNode);
						FadeController c = new FadeController(pooledRealNode,realNode,1f, FadeMode.FadingOut);
						J3DCore.controllers.add(c);
					}
				}
			}
		}
		for (String id:toClear)
		{
			units.remove(id);
		}
		
	}

}
