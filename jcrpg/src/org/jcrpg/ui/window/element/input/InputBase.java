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

package org.jcrpg.ui.window.element.input;

import java.io.IOException;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.mouse.UiMouseEvent;
import org.jcrpg.ui.mouse.UiMouseEvent.UiMouseEventType;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.text.Text;
import org.jcrpg.ui.window.InputWindow;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;


public abstract class InputBase implements Savable{
	
	public static final String UI_ELEMENT = "InputBaseNode";
	
	public static final int DEF_FONT_SIZE = 10;
	
	public float centerX, centerY;
	public float sizeX, sizeY;
	public float dCenterX, dCenterY;
	public float dSizeX, dSizeY;
	public float dOrigoX, dOrigoY;
	public float dOrigoXCenter, dOrigoYCenter;
	public InputWindow w;
	public Node baseNode;
	boolean active = false;
	boolean enabled = true;
	boolean updated = false;
	
	public boolean focusUponMouseEnter = false;
	public boolean deactivateUponUse = true;
	
	
	public Node parentNode = null;
	
	public String id = null;
	
	public static final String SOUND_INPUTSELECTED = "input_selected";
	
	public static final ColorRGBA DEF_NORMAL_COLOR = new ColorRGBA(0.9f,0.9f,0.1f,1f);
	public static final ColorRGBA DEF_OUTLINE_COLOR = new ColorRGBA(0.05f,0.05f,0.05f,1f);
	public static final ColorRGBA DEF_DEACTIVATED_COLOR = new ColorRGBA(0.5f,0.5f,0.1f,1f);
	public static final ColorRGBA DEF_DISABLED_COLOR = ColorRGBA.gray;
	
	protected ColorRGBA normalColor = DEF_NORMAL_COLOR;
	protected ColorRGBA outlineColor = DEF_OUTLINE_COLOR;
	protected ColorRGBA deactivatedColor = DEF_DEACTIVATED_COLOR;
	protected ColorRGBA disabledColor = DEF_DISABLED_COLOR;
	
	public InputBase(String id, InputWindow w, Node parentNode)
	{
		this.id = id;
		this.parentNode = parentNode;
		this.w = w;
		baseNode = new Node(UI_ELEMENT);
		baseNode.setUserData(UI_ELEMENT, this);
		parentNode.attachChild(baseNode);
		parentNode.updateRenderState();
	}
	
	public InputBase(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX, float sizeY)
	{
		this.id = id;
		this.parentNode = parentNode;
		this.w = w;
		init(centerX, centerY, sizeX, sizeY);
		baseNode = new Node(UI_ELEMENT);
		baseNode.setUserData(UI_ELEMENT, this);
		parentNode.attachChild(baseNode);
		parentNode.updateRenderState();
	}
	
	/** Useful for setting the element's dimensions from a Layout */
	public void init(float centerX, float centerY, float sizeX, float sizeY) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		dCenterX = J3DCore.getInstance().getDisplay().getWidth()*(centerX);
		dCenterY = J3DCore.getInstance().getDisplay().getHeight()*(1f-centerY);
		dSizeX =  J3DCore.getInstance().getDisplay().getWidth()*(sizeX);
		dSizeY =  J3DCore.getInstance().getDisplay().getHeight()*(sizeY);
		dOrigoX = dCenterX-dSizeX/2;
		dOrigoXCenter = dCenterX+dSizeX/2;
		dOrigoY = dCenterY+dSizeY/2;
	}
	
	/**
	 * make this input visible again, and enabled = true.
	 */
	public void reattach()
	{
		parentNode.attachChild(baseNode);
		enabled = true;
	}
	/**
	 * remove this input from visible elements and enabled = false.
	 */
	public void detach()
	{
		baseNode.removeFromParent();
		enabled = false;
	}

	public String value = "";
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	/**
	 * This is run when an input field is selected in an input window.
	 */
	public void activate()
	{
		if (w!=null) w.inputActivated(this);
		active = true;
	}
	/**
	 * This is run when an input field is deselected in an input window.
	 */
	public void deactivate()
	{
		active = false;
	}
	
	/**
	 * 
	 * @param key
	 */
	public boolean handleKey(String key)
	{
		return true;
	}

	public boolean handleMouse(UiMouseEvent mouseEvent)
	{
		if (!focusUponMouseEnter)
		{
				if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_ENTERED)
				{
					handleMouseHover(true);
				} else
				if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_EXITED)
				{
					handleMouseHover(false);
				}
		}		
		return false;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isActive()
	{
		return active;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public abstract void reset();
	
	boolean stored = false;
	public boolean isStored()
	{
		return stored;
	}
	public void store()
	{
		stored = true;
	}
	public void restore()
	{
	}
	
	public void colorizeOutlined(Node textNode, ColorRGBA color)
	{
		try {
			int cc=0;
			for (Spatial q: textNode.getChildren())
			{
				if (!J3DCore.NATIVE_FONT_RENDER)
				{
					if (cc%2==1) ((Quad)q).setDefaultColor(color);
				} else
				{
					((Text)q).setTextColor(color);
				} 
				cc++;
			}
		} catch (Exception ex) {}
		
	}
	public void colorize(Node textNode, ColorRGBA color)
	{
		try {
			int cc=0;
			for (Spatial q: textNode.getChildren())
			{
				if (!J3DCore.NATIVE_FONT_RENDER)
				{
					((Quad)q).setDefaultColor(color);
				} else
				{
					((Text)q).setTextColor(color);
				}
				cc++;
			}
		} catch (Exception ex) {}
		
	}

	public HashMap<Node,FontTT> currentTextNodes = new HashMap<Node,FontTT>();
	
	public void freeTextNodes()
	{
		for (Node n:currentTextNodes.keySet())
		{
			FontTT font = currentTextNodes.get(n);
			font.moveFreedToCache(n);
		}
		currentTextNodes.clear();
	}
	
	public Class getClassTag() {
		// TODO Auto-generated method stub
		return null;
	}
	public void read(JMEImporter arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
	public void write(JMEExporter arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public final static float TEXT_PROP = 1.55f;
	
	protected Text createText(String text)
	{
		Text t = Text.createDefaultTextLabel("TextBox_", text);
		t.setCullHint(CullHint.Never );
		t.setTextureCombineMode( TextureCombineMode.Replace );
		return t;
	}
	
	public void handleMouseHover(boolean entering)
	{
		if (!isActive() && isEnabled())
		{
			Node n = getDeactivatedNode();
			if (n!=null)
			if (entering)
			{
				if (n.getChild(0) instanceof Quad)
				{
					((Quad)n.getChild(0)).setSolidColor(ColorRGBA.white);
				}
			}
			else
			{
				if (n.getChild(0) instanceof Quad)
				{
					((Quad)n.getChild(0)).setSolidColor(ColorRGBA.gray);
				}
			}
		}
	

	}
	
	public abstract Node getDeactivatedNode();
	
	/**
	 * Setting this is fallback when no tooltip for selected item in the extender UI class.
	 */
	public String globalTooltip = null;

	public String getTooltipText()
	{
		return globalTooltip;
	}
		
	public boolean needsDeactivationInLayout()
	{
		return false;
	}
	
}
