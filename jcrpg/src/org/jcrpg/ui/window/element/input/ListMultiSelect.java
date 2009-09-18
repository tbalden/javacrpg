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

import java.io.File;
import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.mouse.UiMouseEvent;
import org.jcrpg.ui.mouse.UiMouseHandler;
import org.jcrpg.ui.mouse.UiMouseEvent.UiMouseEventType;
import org.jcrpg.ui.text.Text;
import org.jcrpg.ui.window.InputWindow;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class ListMultiSelect extends InputBase {

	public Object subject;
	
	public String[] ids;
	public String[] texts;
	public Object[] objects;
	public Quad[] icons;
	public boolean[] selectedItems;
	
	public int selected = 0;
	public int fromCount = 0;
	public int maxCount = 0;
	public int maxVisible = 5;
	public boolean reloadNeeded = false;
	
	public ColorRGBA normal = null;
	public ColorRGBA highlighted = null;
	
	public Node deactivatedNode = null;
	public Node activatedNode = null;
	
	/**
	 * The text nodes visible currently.
	 */
	public ArrayList<Node> textNodes = new ArrayList<Node>();
	/**
	 * The icon nodes visible currently.
	 */
	public ArrayList<Node> iconNodes = new ArrayList<Node>();
	/**
	 * Sign nodes visible currently.
	 */
	public ArrayList<Node> signNodes = new ArrayList<Node>(); 

	public static final String defaultImage = "./data/ui/inputBase.png";
	public static final String defaultImageUp = "./data/ui/inputBaseScrollUp.png";
	public static final String defaultImageDown = "./data/ui/inputBaseScrollDown.png";
	public static final String defaultImageUpDown = "./data/ui/inputBaseScrollUD.png";
	public String bgImage = defaultImage;
	
	public float fontRatio = 400f;
	public float dCenterSignX = 0;
	public float dCenterIconX = 0;
	public ListMultiSelect(String id, InputWindow w, Node parent, float centerX, float centerSignX, float centerY, float sizeX, float sizeY, float fontRatio, String[] ids, String[] texts, ColorRGBA normal, ColorRGBA highlighted) {
		this(id,w,parent,centerX,centerSignX, centerY,sizeX,sizeY,fontRatio,ids,texts,null,normal, highlighted);
	}
	
	public ListMultiSelect(String id, InputWindow w, Node parent, float centerX, float centerSignX, float centerY, float sizeX, float sizeY, float fontRatio, String[] ids, String[] texts, Object[] objects, ColorRGBA normal, ColorRGBA highlighted) {
		this(id,w,parent,centerX,centerSignX, 0f, centerY,sizeX,sizeY,fontRatio,ids,texts,objects,null,normal, highlighted);
	}
	public ListMultiSelect(String id, InputWindow w, Node parent, float centerX, float centerSignX, float centerIconX, float centerY, float sizeX, float sizeY, float fontRatio, String[] ids, String[] texts, Object[] objects, Quad[] icons, ColorRGBA normal, ColorRGBA highlighted) {
		super(id, w, parent, centerX, centerY, sizeX, sizeY);
		dCenterSignX = w.core.getDisplay().getWidth()*(centerSignX);
		dCenterIconX = w.core.getDisplay().getWidth()*(centerIconX);
		this.icons = icons;
		this.fontRatio = fontRatio;
		this.ids = ids;
		selectedItems = new boolean[ids.length];
		for (int i=0; i<ids.length; i++)
		{
			selectedItems[i] = false;
		}
		this.texts = texts;
		this.objects = objects;
		maxCount = ids.length;		
		this.normal = normal;
		this.highlighted = highlighted;
		deactivate();
		w.base.addEventHandler("lookLeft", w);
		w.base.addEventHandler("lookRight", w);
		w.base.addEventHandler("enter", w);
		w.base.addEventHandler("space", w);
		parent.updateRenderState();
	}

	public void setSelected(int counter)
	{
		fromCount = (counter/maxVisible)*maxVisible;
		selected = counter%maxVisible;
		deactivate();
	}
	public void setSelected(Object o)
	{
		if (objects!=null)
		{
			int count = 0;
			for (Object oi:objects)
			{
				if (oi.equals(o))
				{
					setSelected(count);
				}
				count++;
			}
		}
		deactivate();
	}
	
	public int getSelection()
	{
		return fromCount+selected;
	}
	public Object getSelectedObject()
	{
		if (objects==null || objects.length<=fromCount+selected) return null;
		return objects[fromCount+selected];
	}
	
	/**
	 * Get all selected objects of the multi-selection.
	 * @return
	 */
	public ArrayList<Object> getMultiSelection()
	{
		ArrayList<Object> selection = new ArrayList<Object>();
		for (int i=0; i<selectedItems.length; i++)
		{
			if (selectedItems[i])
			{
				selection.add(objects[i]);
			}
		}
		return selection;
	}
	
	Text dText, dFlag;
	
	public void setupDeactivated()
	{
		baseNode.removeFromParent();
		parentNode.attachChild(baseNode); // to foreground
		baseNode.detachAllChildren();
		freeTextNodes();
		if (deactivatedNode==null) 
		{
			deactivatedNode = new Node(""+id);
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.gray);
				deactivatedNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		baseNode.attachChild(deactivatedNode);
		if (maxCount==0) {
			baseNode.updateRenderState();
			return;			
		}
		String text = texts[selected+fromCount];

		Node slottextNode = null;
		if (J3DCore.NATIVE_FONT_RENDER)
		{
			if (dText==null)
			{
				dText = createText(text);
			} else
			{
				dText.print(text);
			}
			Text t = dText;
			slottextNode = new Node();
			slottextNode.attachChild(t);
			float scale = w.core.getDisplay().getWidth()/fontRatio/TEXT_PROP;
			slottextNode.setLocalScale(scale);
			slottextNode.setLocalTranslation(t.getCenterOrigoX(dCenterX,scale), - t.getHeight2()/2f*scale + dCenterY ,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			t.setTextColor(new ColorRGBA(1,1,0.1f,1f));
		} else
		{
			slottextNode = FontUtils.textVerdana.createText(text, DEF_FONT_SIZE, new ColorRGBA(1,1,0.1f,1f),true);
			slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio);
			currentTextNodes.put(slottextNode,FontUtils.textVerdana);
		}

		String flag = (selectedItems[selected+fromCount]?"X ":"_ ");
		if (flag!=null) 
		{
			Node signNode = null;
			if (J3DCore.NATIVE_FONT_RENDER)
			{
				if (dFlag==null)
				{
					dFlag = createText(flag);
				} else
				{
					dFlag.print(flag);
				}
				Text t = dFlag;
				signNode = new Node();
				signNode.attachChild(t);
				float scale = w.core.getDisplay().getWidth()/fontRatio/TEXT_PROP;
				signNode.setLocalScale(scale);
				signNode.setLocalTranslation(t.getCenterOrigoX(dCenterSignX,scale), - t.getHeight2()/2f*scale + dCenterY,0);
				signNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				t.setTextColor(new ColorRGBA(1,1,0.1f,1f));
			} else
			{
				signNode = FontUtils.textVerdana.createOutlinedText(flag, DEF_FONT_SIZE, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
				signNode.setLocalTranslation(dCenterSignX, dCenterY - dSizeY*0,0);
				signNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				signNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio);
				currentTextNodes.put(signNode,FontUtils.textVerdana);
			}
			baseNode.attachChild(signNode);
		}
		
		if (icons!=null && icons.length>0)
		{
			try {
				Quad m = icons[selected+fromCount];
				Node iconNode = new Node(""+id);
				iconNode.setLocalTranslation(dCenterIconX, dCenterY - dSizeY*0,0);
				iconNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				iconNode.setLocalScale(1f);//w.core.getDisplay().getWidth()/fontRatio);
				iconNode.attachChild(m);
				baseNode.attachChild(iconNode);
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ListMultiSelect M = "+m.getName());
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		baseNode.attachChild(slottextNode);
		deactivatedNode.setModelBound(new BoundingBox());
		baseNode.updateRenderState();
		baseNode.updateModelBound();
	}
	
	int size = 0;

	public Text[] textInstances = new Text[maxVisible+1];
	public Text[] flagInstances = new Text[maxVisible+1];

	Quad w1;
	Quad wU;
	Quad wD;
	Quad wUD;

	public void setupActivated()
	{
		size = 0;
		baseNode.removeFromParent();
		parentNode.attachChild(baseNode); // to foreground
		baseNode.detachAllChildren();
		freeTextNodes();
		if (activatedNode==null) 
		{
			activatedNode = new Node(""+id);
		}
		activatedNode.detachAllChildren();
		Quad wQ = null;
		try
		{
			if (texts.length>maxVisible)
			{
				if (fromCount==0)
				{
					if (wD==null)
					{
							wD = Window.loadImageToQuad(new File(defaultImageDown), dSizeX, dSizeY, dCenterX, dCenterY);
					}
					wQ = wD;
				} else
				if (fromCount+maxVisible>=maxCount)
				{
					if (wU==null)
					{
							wU = Window.loadImageToQuad(new File(defaultImageUp), dSizeX, dSizeY, dCenterX, dCenterY);
					}
					wQ = wU;
				} else
				{
					if (wUD==null)
					{
							wUD = Window.loadImageToQuad(new File(defaultImageUpDown), dSizeX, dSizeY, dCenterX, dCenterY);
					}
					wQ = wUD;
				}
			} else
			{
				if (w1==null)
				{
					w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				}
				wQ = w1;
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		wQ.setSolidColor(ColorRGBA.white);
		activatedNode.attachChild(wQ);
		baseNode.attachChild(activatedNode);
		
		if (maxCount==0 || texts.length==0)
		{
			baseNode.updateRenderState();
			return;
		}
		textNodes.clear();
		iconNodes.clear();
		for (int i=0; i<maxVisible+1; i++) {
			if (i+fromCount<maxCount) {
				String text = "";
				String flag = "";
				if (i==maxVisible && i+fromCount<maxCount)
				{
					text = "...";
					flag = null;
				} else {
					if (i>=maxVisible)
					{
						continue; // we are at the "..." part we should continue instead of new element
					}
					flag = (selectedItems[i+fromCount]?"X ":"_ ");
					text = texts[i+fromCount];
				}
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ListMultiSelect TEXT = "+i+" "+text+" max: "+maxCount);
				
				Node slottextNode = null;
				if (J3DCore.NATIVE_FONT_RENDER)
				{
					Text t = textInstances[i];
					if (t==null) 
					{
						t = createText(text);
						textInstances[i] = t;
					}
					else
					{
						t.print(text);
					}
					slottextNode = new Node();
					slottextNode.attachChild(t);
					float scale = w.core.getDisplay().getWidth()/fontRatio/TEXT_PROP;
					slottextNode.setLocalScale(scale);
					slottextNode.setLocalTranslation(t.getCenterOrigoX(dCenterX,scale), - t.getHeight2()/2f*scale + dCenterY - dSizeY*i,0);
					slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				} else
				{
					slottextNode = FontUtils.textVerdana.createText(text, DEF_FONT_SIZE, new ColorRGBA(1,1,0.1f,1f),true);
					slottextNode.setLocalTranslation(dCenterX, dCenterY - dSizeY*i,0);
					slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
					slottextNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio);
					currentTextNodes.put(slottextNode,FontUtils.textVerdana);
				}
				if (i==selected)
				{
					colorize(slottextNode, ColorRGBA.yellow);
				} else
				{
					colorize(slottextNode, ColorRGBA.gray);
				}
				if (flag!=null) 
				{
					Node signNode = null;
					if (J3DCore.NATIVE_FONT_RENDER)
					{
						Text t = flagInstances[i];
						if (t==null) 
						{
							t = createText(flag);
							flagInstances[i] = t;
						}
						else
						{
							t.print(flag);
						}
						signNode = new Node();
						signNode.attachChild(t);
						float scale = w.core.getDisplay().getWidth()/fontRatio/TEXT_PROP;
						signNode.setLocalScale(scale);
						signNode.setLocalTranslation(t.getCenterOrigoX(dCenterSignX,scale), - t.getHeight2()/2f*scale + dCenterY - dSizeY*i,0);
						signNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						t.setTextColor(new ColorRGBA(1,1,0.1f,1f));
					} else
					{
						signNode = FontUtils.textVerdana.createOutlinedText(flag, DEF_FONT_SIZE, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
						signNode.setLocalTranslation(dCenterSignX, dCenterY - dSizeY*i,0);
						signNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						signNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio);
						signNodes.add(signNode);
						currentTextNodes.put(signNode,FontUtils.textVerdana);		
					}
					baseNode.attachChild(signNode);
				}
				if (icons!=null && icons.length>0)
				{
					try {
						Quad m = icons[i+fromCount];
						Node iconNode = new Node(""+id);
						iconNode.setLocalTranslation(dCenterIconX, dCenterY - dSizeY*i,0);
						iconNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
						iconNode.setLocalScale(1f);//w.core.getDisplay().getWidth()/fontRatio);
						iconNode.attachChild(m);
						baseNode.attachChild(iconNode);
						iconNodes.add(iconNode);
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ListMultiSelect M = "+m.getName());
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
				textNodes.add(slottextNode);
				baseNode.attachChild(slottextNode);
				size++;
			}
		}
		activatedNode.getChild(0).setLocalScale(new Vector3f(1f,size,size));
		if (size>0)
		{
			activatedNode.getChild(0).setLocalTranslation(dCenterX, dCenterY - ((size-1) * dSizeY)/2, 0);
		}
		activatedNode.setModelBound(new BoundingBox());
		baseNode.updateRenderState();
		baseNode.updateModelBound();
	}

	public boolean select(boolean next)
	{
		if (!next) {
			selected--;
		} else
		{
			selected++;
		}
		
		if (selected+fromCount>=maxCount)
		{
			selected--;
		}
		if (selected>=maxVisible)
		{
			if (selected+fromCount<maxCount)
			{
				fromCount += selected;
				selected = 0;

				reloadNeeded = true;
			} 
		}
		if (selected<0) {
			if (fromCount-maxVisible>=0)
			{
				fromCount -= maxVisible;
				selected = maxVisible-1;
				reloadNeeded = true;
			} else {
				selected = 0;
			}
		}
		return reloadNeeded;
	}
	
	public boolean handleKey(String key) {
		if (!enabled) return false;
		if (key.equals("lookLeft"))
		{
			if (select(false))
			{
				setupActivated();
			} else
			{
				int i=0;
				for (Node n:textNodes)
				{
					colorize(n, i==selected?ColorRGBA.yellow:ColorRGBA.gray);
					i++;
				}
			}
			if (ids.length>0) 
			{
				setValue(ids[fromCount+selected]);
				w.inputChanged(this, key);
			}
		} else
		if (key.equals("lookRight"))
		{
			if (select(true))
			{
				setupActivated();
			} else
			{
				int i=0;
				for (Node n:textNodes)
				{
					colorize(n, i==selected?ColorRGBA.yellow:ColorRGBA.gray);
					i++;
				}
			}
			if (ids.length>0) 
			{
				setValue(ids[fromCount+selected]);
				w.inputChanged(this, key);
			}
		} else
		if (key.equals("enter"))
		{
			if (selectedItems.length>0) 
			{
				// inverting selection for current item...
				selectedItems[fromCount+selected]=!selectedItems[fromCount+selected]; 
				//updated = true;
				setupActivated();
				// move to the next item
				handleKey("lookRight");
			}
			return true;
		} else
		if (key.equals("space"))
		{
			w.inputUsed(this, key);
			return true;
		}
		return false;
	}

	@Override
	public void activate() {
		if (updated)
		{
			maxCount = ids.length;
			selected = 0;
			updated = false;
			selectedItems = new boolean[ids.length];
			for (int i=0; i<ids.length; i++)
			{
				selectedItems[i] = false;
			}
		}
		super.activate();
		setupActivated();
	}

	@Override
	public void deactivate() {
		super.deactivate();
		if (updated)
		{
			maxCount = ids.length;
			selected = 0;
			updated = false;
			selectedItems = new boolean[ids.length];
			for (int i=0; i<ids.length; i++)
			{
				selectedItems[i] = false;
			}
		}
		setupDeactivated();
	}

	@Override
	public void reset() {
		texts = new String[0];
		ids = new String[0];
		objects = new Object[0];
		selected = 0;
		maxCount = 0;
		fromCount = 0;
		setupDeactivated();
	}
	

	@Override
	public boolean handleMouse(UiMouseEvent mouseEvent) {
		if (isEnabled())
		{

			
			if (focusUponMouseEnter)
			{
				if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_ENTERED)
				{
					if (isEnabled() && !active)
					{
						activate();
						return false;
					}
				} else
				if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_EXITED)
				{
					if (isEnabled() && active)
					{
						deactivate();
						return false;
					}
				}
				
			}
			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_MOVED)
			{
				
				if (active)
				{
					if (mouseEvent.getAreaSpatial().ratioX<=0.7f)
					{
						selected = (int)(mouseEvent.getAreaSpatial().ratioY*size);
						int i=0;
						for (Node n:textNodes)
						{
							colorize(n, i==selected?ColorRGBA.yellow:ColorRGBA.gray);
							i++;
						}
					}
				}
				
			} else
			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_PRESSED)
			{
				if (mouseEvent.isButtonPressed(UiMouseEvent.BUTTON_LEFT))
				{
					if (active)
					{
						
						if (mouseEvent.getAreaSpatial().ratioX>0.7f)
						{
							if (mouseEvent.getAreaSpatial().ratioY>0.5f)
							{
								selected = size-2;
								return handleKey("lookRight");
							} else
							{
								selected = 0;
								return handleKey("lookLeft");
							}

						} else
						{
							return handleKey("enter");
						}
					} else
					{
						activate();
						return true;
					}
				}
				if (mouseEvent.isButtonPressed(UiMouseEvent.BUTTON_RIGHT))
				{
					//if (mouseEvent.getAreaSpatial().ratioX>0.7f)
					{
						if (mouseEvent.getAreaSpatial().ratioY>0.5f)
						{
							selected = size-2;
							return handleKey("lookRight");
						} else
						{
							selected = 0;
							return handleKey("lookLeft");
						}

					}
				}
			}

			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_MOVED)
			{
				if (isEnabled() && active)
				{
					if (mouseEvent.getAreaSpatial().ratioX>0.7f)
					{
						if (mouseEvent.getAreaSpatial().ratioY>0.5f)
						{
							UiMouseHandler.cursorDown();
							return true;
						} else
						{
							UiMouseHandler.cursorUp();
							return true;
						}
					}
						//MouseInput.get().setHardwareCursor(arg0)
				}
			} 

		}
		return false;
	}

	@Override
	public Node getDeactivatedNode() {
		return deactivatedNode;
	}

	public String[] tooltips;

	@Override
	public String getTooltipText()
	{
		try {
			String tt= tooltips[fromCount+selected];
			if (tt==null) return globalTooltip;
			return tt;
		}catch (Exception ex) {return globalTooltip;}
	}

}
