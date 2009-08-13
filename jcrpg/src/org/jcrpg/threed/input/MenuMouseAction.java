/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2009
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

package org.jcrpg.threed.input;

import java.util.ArrayList;

import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.bounding.BoundingBox;
import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * <code>MenuMouseAction</code> defines a mouse action that detects mouse clicks
 * and converts it into hits on menu objects.
 * 
 * @version $Id$
 * @author mkienenb
 * @author paul.illes
 **/
public class MenuMouseAction extends MouseInputAction {

    //the event to distribute to the looking actions.
    private Node rootNode;
    
    private boolean buttonPressRequired = true;
    
    private int mouseButtonForRequired = 0;

    /**
     * Constructor creates a new <code>MenuMouseAction</code> object. It takes the
     * mouse, camera and speed of the looking.
     * 
     * @param mouse
     *            the mouse to calculate view changes.
     * @param camera
     *            the camera to move.
     * @param speed
     *            the speed at which to alter the camera.
     */
    public MenuMouseAction(Mouse mouse) {
        this.mouse = mouse;
    }

    /**
     * <code>performAction</code> checks for any movement of the mouse, and
     * calls the appropriate method to alter the camera's orientation when
     * applicable.
     * 
     * @see com.jme.input.action.MouseInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
    	if (rootNode==null) return;
        if(!buttonPressRequired || MouseInput.get().isButtonDown(mouseButtonForRequired))
        {
        	mousePick(rootNode);
        }

    }

	private void mousePick(Node rootNode) {
	    ArrayList<Spatial> picked = new ArrayList<Spatial>();
	    ArrayList<Spatial> loop = new ArrayList<Spatial>();
	    pickUI(picked, loop, rootNode, MouseInput.get().getXAbsolute(), MouseInput.get().getYAbsolute());
	    for (Spatial p:picked)
	    {
	    	System.out.println("Hit: "+p.getName()+" "+p.getClass());
	    	Node parent = (Node)p.getParent();
	    	InputBase base = (InputBase)parent.getUserData(InputBase.UI_ELEMENT);
	    	if (base.isEnabled())
	    	{
	    		base.handleKey("enter"); // TODO more sophisticated per UI element type ---> implement total handling in the given Class! NOT here
	    	}
	    }
	}
	
	/**
	 * jcrpg specific UI picking
	 * @author paul.illes
	 * @param list
	 * @param loopDetection
	 * @param node
	 * @param X
	 * @param Y
	 */
	private void pickUI(ArrayList<Spatial> list, ArrayList<Spatial> loopDetection, Node node, int X, int Y)
	{
	    for (Spatial s:node.getChildren())
	    {
	    	if (s.getWorldBound()!=null && s.getWorldBound() instanceof BoundingBox)
	    	{
	    		BoundingBox b = (BoundingBox)s.getWorldBound();
	    		float x = b.getCenter().getX();
	    		float y = b.getCenter().getY();
	    		Vector3f v = b.getExtent(null);
	    		x -= v.getX();
	    		y -= v.getY();
	    		float x2 = x+v.getX()*2;
	    		float y2 = y+v.getY()*2;
	    		if (x<=X && y<=Y && x2>=X && y2>=Y)
	    		{
    				if (s.getParent()!=null && InputBase.UI_ELEMENT.equals(s.getParent().getName()))
    				{
    					list.add(s);
    				}
	    		}
	    		
	    		if (s instanceof Node)
	    		{
	    			if (loopDetection.contains(s))
	    			{
	    				System.out.println("######### LOOP IN UI-SCENEGRAPH!! "+s);	
	    			} else
	    			{
	    				loopDetection.add(s);
	    				pickUI(list, loopDetection, (Node)s, X, Y);
	    			}
	    		}
	    	}
	    }	
	}

	/**
	 * @return the mouseButtonForRequired
	 */
	public int getMouseButtonForRequired() {
		return mouseButtonForRequired;
	}

	/**
	 * @param mouseButtonForRequired the mouseButtonForRequired to set
	 */
	public void setMouseButtonForRequired(int mouseButtonForRequired) {
		this.mouseButtonForRequired = mouseButtonForRequired;
	}

	public boolean isButtonPressRequired() {
        return buttonPressRequired;
    }

    public void setButtonPressRequired(boolean buttonPressRequired) {
        this.buttonPressRequired = buttonPressRequired;
    }

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
}