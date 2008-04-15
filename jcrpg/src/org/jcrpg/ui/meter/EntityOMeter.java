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

package org.jcrpg.ui.meter;

import java.util.Collection;
import java.util.HashMap;

import org.jcrpg.ui.HUD;
import org.jcrpg.ui.Window;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

public class EntityOMeter {

	public HUD hud;
	
	public static HashMap<String, Quad> entityPics = new HashMap<String, Quad>();
	
	public Node node = new Node();
	
	public float iconSizeX = 10f;
	
	public EntityOMeter(HUD hud) throws Exception
	{
		this.hud = hud;
		iconSizeX = hud.core.getDisplay().getWidth()/15;
	}
	
	public void update(Collection<String> entityPics)
	{
		hud.hudNode.detachChild(node);
		node.detachAllChildren();
		int count = 0;
		for (String p:entityPics)
		{
			Quad q = loadQuad(p);
			if (q==null) continue;
			if (++count>5) break;
			SharedMesh sq = new SharedMesh(""+p,q);
			//sq.setLocalScale(1,1,1);
			sq.setLocalTranslation(new Vector3f(hud.core.getDisplay().getWidth()/1.9f+count*iconSizeX , hud.core.getDisplay().getHeight()/17,0));
			node.attachChild(sq);
		}
		hud.hudNode.attachChild(node);
		node.updateRenderState();
		hud.hudNode.updateRenderState();
	}
	
	public Quad loadQuad(String pic)
	{
		
		Quad q = null;
		q = entityPics.get(pic);
		if (q==null)
		{
			try {
				q = Window.loadImageToQuad("./data/icons/entities/"+pic+".png", hud.core.getDisplay().getWidth()/18, hud.core.getDisplay().getHeight()/14,0, 0);
				entityPics.put(pic, q);
			} catch (Exception ex)
			{
			}
		}

		return q;
	}
	
}
