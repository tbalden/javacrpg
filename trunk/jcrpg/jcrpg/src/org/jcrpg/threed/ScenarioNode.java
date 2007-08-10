/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.threed;

import java.util.Stack;

import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

public class ScenarioNode extends Node {
	
	public float VIEW_DISTANCE_SQR = 10f;
	public Camera cam;

	public ScenarioNode(float viewDistance, Camera cam)
	
	{
		VIEW_DISTANCE_SQR = viewDistance*viewDistance;
		this.cam = cam;
	}
	
	@Override
	public void draw(Renderer r) {
	       if(children == null) {
	            return;
	        }
	        Spatial child;
	        for (int i = 0, cSize = children.size(); i < cSize; i++) {
	            child =  children.get(i);
	            if (child != null)
	            {
	            	if (cam.getLocation().distanceSquared(child.getWorldTranslation())<VIEW_DISTANCE_SQR)
	            	{
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
    protected void applyRenderState(Stack[] states) {
        if(children == null) {
            return;
        }
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial pkChild = getChild(i);
            if (pkChild != null) {
            	if (cam.getLocation().distanceSquared(pkChild.getWorldTranslation())<VIEW_DISTANCE_SQR)
            		pkChild.updateRenderState();
            }
        }
    }
	
}
