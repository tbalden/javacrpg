/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.threed;

import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
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
		VIEW_DISTANCE_SQR = J3DCore.VIEW_DISTANCE_SQR;
		//this.cam = cam;
	}
	
	
	//@Override
	public void drraw(Renderer r) {
	       if(children == null) {
	            return;
	        }
	        Spatial child;
	        for (int i = 0, cSize = children.size(); i < cSize; i++) {
	            child =  children.get(i);
	            if (child != null)
	            {
	            	if (child.getCullMode()==Spatial.CULL_NEVER || r.getCamera().getLocation().distanceSquared(child.getWorldTranslation())<VIEW_DISTANCE_SQR)
	            	{
	            		//if (System.currentTimeMillis()%10>4)
	            			child.onDraw(r);
	            	}
	            }
	        }
	}

	
    /**
     * Applies the stack of render states to each child by calling
     * updateRenderState(states) on each child.
     * 
     * @param states
     *            The Stack[] of render states to apply to each child.
     */
    /*protected void applyRenderState(Stack[] states) {
        if(children == null) {
            return;
        }
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial pkChild = getChild(i);
            if (pkChild != null) {
            	if (c.getLocation().distanceSquared(pkChild.getWorldTranslation())<VIEW_DISTANCE_SQR)
            		pkChild.updateRenderState();
            }
        }
    }*/
	
}
