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

public class ListSelect extends InputBase {

	public Object subject;
	
	public String[] ids;
	public String[] texts;
	public String[] tooltips;
	public Object[] objects;
	public Quad[] icons;
	
	public int selected = 0;
	public int fromCount = 0;
	public int maxCount = 0;
	public int maxVisible = 5;
	public boolean reloadNeeded = false;
	
	public ColorRGBA normal = null;
	public ColorRGBA highlighted = null;
	
	public Node deactivatedNode = null;
	public Node activatedNode = null;

	public float dCenterIconX = 0;
	
	public ArrayList<Node> textNodes = new ArrayList<Node>(); 
	/**
	 * The icon nodes visible currently.
	 */
	public ArrayList<Node> iconNodes = new ArrayList<Node>();

	public static final String defaultImage = "./data/ui/inputBase.png";
	public static final String defaultImageUp = "./data/ui/inputBaseScrollUp.png";
	public static final String defaultImageDown = "./data/ui/inputBaseScrollDown.png";
	public static final String defaultImageUpDown = "./data/ui/inputBaseScrollUD.png";
	public String bgImage = defaultImage;
	
	public float fontRatio = 400f;
	
	public ListSelect(String id, InputWindow w, Node parent, float fontRatio, String[] ids, String[] texts, ColorRGBA normal, ColorRGBA highlighted) {
		this(id,w,parent,fontRatio,ids,texts,null,normal, highlighted);
	}
	public ListSelect(String id, InputWindow w, Node parent, float fontRatio, String[] ids, String[] texts, Object[] objects, ColorRGBA normal, ColorRGBA highlighted) {
		this(id,w,parent,0f,fontRatio,ids,texts,objects,new Quad[0],normal,highlighted);
	}
	
	public ListSelect(String id, InputWindow w, Node parent, float iconX, float fontRatio, String[] ids, String[] texts, Object[] objects, Quad[] icons, ColorRGBA normal, ColorRGBA highlighted) 
	{
		super(id, w, parent);
		this.fontRatio = fontRatio;
		this.ids = ids;
		this.texts = texts;
		this.objects = objects;
		this.icons = icons;
		dCenterIconX = w.core.getDisplay().getWidth()*(iconX);
		maxCount = ids.length;		
		this.normal = normal;
		this.highlighted = highlighted;
		// other behavior on init
	}
	
	public ListSelect(String id, InputWindow w, Node parent, float centerX, float centerY, float sizeX, float sizeY, float fontRatio, String[] ids, String[] texts, ColorRGBA normal, ColorRGBA highlighted) {
		this(id,w,parent,centerX,centerY,sizeX,sizeY,fontRatio,ids,texts,null,normal, highlighted);
	}
	public ListSelect(String id, InputWindow w, Node parent, float centerX, float centerY, float sizeX, float sizeY, float fontRatio, String[] ids, String[] texts, Object[] objects, ColorRGBA normal, ColorRGBA highlighted) {
		this(id,w,parent,centerX,0f,centerY,sizeX,sizeY,fontRatio,ids,texts,objects,new Quad[0],normal,highlighted);
	}
	
	public ListSelect(String id, InputWindow w, Node parent, float centerX, float iconX, float centerY, float sizeX, float sizeY, float fontRatio, String[] ids, String[] texts, Object[] objects, Quad[] icons, ColorRGBA normal, ColorRGBA highlighted) 
	{
		super(id, w, parent, centerX, centerY, sizeX, sizeY);
		this.fontRatio = fontRatio;
		this.ids = ids;
		this.texts = texts;
		this.objects = objects;
		this.icons = icons;
		dCenterIconX = w.core.getDisplay().getWidth()*(iconX);
		maxCount = ids.length;		
		this.normal = normal;
		this.highlighted = highlighted;
		deactivate();
		w.base.addEventHandler("lookLeft", w);
		w.base.addEventHandler("lookRight", w);
		w.base.addEventHandler("enter", w);
		parent.updateRenderState();
	}

	@Override
	public void init(float centerX, float centerY, float sizeX, float sizeY) {
		super.init(centerX, centerY, sizeX, sizeY);
		if (baseNode!=null) {
			deactivate(); 
			w.base.addEventHandler("lookLeft", w);
			w.base.addEventHandler("lookRight", w);
			w.base.addEventHandler("enter", w);
			parentNode.updateRenderState();
		}
	}

	public void setSelected(int counter)
	{
		fromCount = (counter/maxVisible)*maxVisible;
		selected = counter%maxVisible;
		deactivate();
	}
	public boolean setSelected(Object o)
	{
		boolean ret = false;
		if (objects!=null)
		{
			int count = 0;
			for (Object oi:objects)
			{
				if (oi.equals(o))
				{
					setSelected(count);
					ret = true;
				}
				count++;
			}
		}
		deactivate();
		return ret;
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
				if (dSizeX==0)
				{
					try {
					throw new Exception();
					} catch (Exception ee)
					{
						ee.printStackTrace();
					}
				}
				//System.out.println("DEA: -- "+id+" ? "+bgImage+ " "+dSizeX+" / "+dCenterX);
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
		String text = null;
		if (selected+fromCount>=texts.length)
		{
			text = texts[selected-1+fromCount]; // handling '...' element upon mouse leave
		} else
		{
			text = texts[selected+fromCount]; //
		}
		Node slottextNode = null;
		if (J3DCore.NATIVE_FONT_RENDER)
		{
			Text t = createText(text);
			t.setTextColor(normalColor);
			slottextNode = new Node();
			slottextNode.attachChild(t);
			float scale = w.core.getDisplay().getWidth()/fontRatio/TEXT_PROP;
			slottextNode.setLocalScale(scale);
			slottextNode.updateWorldVectors();
			slottextNode.updateModelBound();
			slottextNode.setLocalTranslation(t.getCenterOrigoX(dCenterX,scale), - t.getHeight2()/2f*scale + dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		} else
		{
			slottextNode = FontUtils.textVerdana.createText(text, DEF_FONT_SIZE, normalColor,true);
			slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio/TEXT_PROP);
			currentTextNodes.put(slottextNode,FontUtils.textVerdana);
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
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ListSelect M = "+m.getName());
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
	
	boolean nextPageAvailable = false;
	

	public Text[] textInstances = new Text[maxVisible+1];
	
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
		nextPageAvailable = false;
		for (int i=0; i<maxVisible+1; i++) {
			if (i+fromCount<maxCount) {
				String text = "";
				if (i==maxVisible && i+fromCount<maxCount)
				{
					nextPageAvailable = true;
					text = "";
				} else {
					if (i==maxVisible)
					{
						continue; // we are at the "..." part we should continue instead of new element
					}
					text = texts[i+fromCount];
				}
				
				if (text==null) text = "## null ##";
				
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
					slottextNode = FontUtils.textVerdana.createText(text, DEF_FONT_SIZE, normalColor,true);
					slottextNode.setLocalTranslation(dCenterX, dCenterY - dSizeY*i,0);
					slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
					slottextNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio/TEXT_PROP);
					currentTextNodes.put(slottextNode,FontUtils.textVerdana);
				}
				if (i==selected && i!=maxVisible)
				{
					colorize(slottextNode, normalColor);
				} else
				{
					colorize(slottextNode, disabledColor);
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
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ListSelect M = "+m.getName());
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
		boolean singlePage = texts.length<=maxVisible;
		activatedNode.getChild(0).setLocalScale(new Vector3f(1f,singlePage?size:maxVisible,singlePage?size:maxVisible));//size,size));
		if (size>0)
		{
			//activatedNode.getChild(0).setLocalTranslation(dCenterX, dCenterY - ((size-1) * dSizeY)/2, 0);
			activatedNode.getChild(0).setLocalTranslation(dCenterX, dCenterY - (((singlePage?size:maxVisible)-1) * dSizeY)/2, 0);
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
				if (!active) setupActivated();
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
				if (!active) setupActivated();
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
			w.inputUsed(this , key);
			if (deactivateUponUse)
			{
				deactivate();
			}
		} else
		if (key.equals("negative"))
		{
			w.inputUsed(this , key);
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
	
	public Object storedState = null;
	@Override
	public void store()
	{
		storedState = getSelectedObject();
		super.store();
	}
	public void restore()
	{
		if (storedState!=null)
		{
			if (objects.length==0) return;
			if (setSelected(storedState)) 
			{
				w.inputChanged(this, "restore");
			}
		}
		if (!isStored()) return;
		
	}
	
	@Override
	public boolean handleMouse(UiMouseEvent mouseEvent) {
		if (isEnabled())
		{
			super.handleMouse(mouseEvent);
			
			if (focusUponMouseEnter)
			{
				if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_ENTERED)
				{
					if (isEnabled() && !active)
					{
						activate();
						return false;
					}
				} 
			}
			if (focusUponMouseEnter||!deactivateUponUse)
			{
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
					if (mouseEvent.getAreaSpatial().ratioX<=0.8f)
					{
						int countedSelected = (int)(mouseEvent.getAreaSpatial().ratioY*maxVisible);//size);
						if (countedSelected>=size) return false;
						selected = countedSelected;
						int i=0;
						for (Node n:textNodes)
						{
							colorize(n, i==selected?ColorRGBA.yellow:ColorRGBA.gray);
							i++;
						}
						w.inputChanged(this, "");
					}
				}
				
			} else
			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_PRESSED)
			{
				if (mouseEvent.isButtonPressed(UiMouseEvent.BUTTON_LEFT))
				{
					if (active)
					{
						
						if (mouseEvent.getAreaSpatial().ratioX>0.8f || nextPageAvailable && selected == size-1)
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
							boolean b = handleKey("enter");
							if (deactivateUponUse)
							{
								w.inputLeft(this, "");
								deactivate();
							}
							return b;
						}
					} else
					{
						activate();
						return true;
					}
				}
				if (mouseEvent.isButtonPressed(UiMouseEvent.BUTTON_RIGHT))
				{
					if (active)
					{
						
						if (mouseEvent.getAreaSpatial().ratioX>0.8f || nextPageAvailable && selected == size-1)
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
							boolean b = handleKey("negative");
							if (deactivateUponUse)
							{
								w.inputLeft(this, "");
								deactivate();
							}
							return b;
						}
					} else
					{
						activate();
						return true;
					}
				}
			}

			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_MOVED)
			{
				if (isEnabled() && active)
				{
					if (mouseEvent.getAreaSpatial().ratioX>0.8f)
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
			if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_RELEASED)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Node getDeactivatedNode() {
		return deactivatedNode;
	}
	
	
	@Override
	public String getTooltipText()
	{
		try {
			String tt= tooltips[fromCount+selected];
			if (tt==null) return globalTooltip;
			return tt;
		}catch (Exception ex) {return globalTooltip;}
	}
	
	@Override
	public boolean needsDeactivationInLayout() {
		return true;
	}

	
}
