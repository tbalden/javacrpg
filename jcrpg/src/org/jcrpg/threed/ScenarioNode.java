/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.threed;

import java.util.HashMap;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

public class ScenarioNode extends Node {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public float VIEW_DISTANCE_SQR = 10f;

	Camera c;
	public ScenarioNode(Camera c)
	
	{
		this.c = c;
		VIEW_DISTANCE_SQR = J3DCore.SETTINGS.VIEW_DISTANCE_SQR;
		//this.cam = cam;
	}
	
	public static final int PART_SIZE = 30;
	
	public HashMap<Long, Node> space = new HashMap<Long, Node>();
	
	public long getKey(Vector3f v)
	{
		int x = (int)(v.x/PART_SIZE);
		int y = (int)(v.y/PART_SIZE);
		int z = (int)(v.z/PART_SIZE);
		long s = (((long)x) << 32) + ((z) << 16) + (y);
		return s;
	}
	
	public class SubNode extends Node
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SubNode() {
			super();
			// TODO Auto-generated constructor stub
		}

		public SubNode(String arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}
		
		public int detachChild(Spatial arg0) {
			int r = super.detachChild(arg0);
			if (r==0)
			{
				removeFromParent();
			}
			return r;
		}		
		
		
	}
	
	@Override
	public int attachChild(Spatial arg0) {
		Vector3f v = arg0.getWorldTranslation();
		long k = getKey(v);
		Node n = space.get(k);
		if (n==null)
		{
			n = new SubNode();
			super.attachChild(n);
			updateRenderState();
			n.setModelBound(new BoundingBox());
			space.put(k, n);
		} else
		{
			if (n.getParent()==null)
			{
				super.attachChild(n);
				updateRenderState();
			}

		}
		n.attachChild(arg0);
		n.updateModelBound();
		return n.getChildren().size();
	}
	@Override
	public int detachChild(Spatial arg0) {
		if (arg0 instanceof SubNode)
		{
			super.detachChild(arg0);
			return 0;
		}
		Vector3f v = arg0.getWorldTranslation();
		long k = getKey(v);
		Node n = space.get(k);
		if (n==null)
		{
			return 0;
		}
		n.detachChild(arg0);
		if (n.getChildren()==null || n.getChildren().size()==0)
		{
			super.detachChild(n);
			return 0;
		}
		n.updateModelBound();
		return n.getChildren().size();
	}
	
	
	
}
