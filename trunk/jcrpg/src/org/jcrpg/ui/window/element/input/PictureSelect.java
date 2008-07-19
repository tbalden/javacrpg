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

package org.jcrpg.ui.window.element.input;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.InputWindow;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

/**
 * Numeric value selection.
 * @author pali
 *
 */
public class PictureSelect extends InputBase {

	public static final int UNDEFINED = -999999;
	public String text;
	
	public static final String defaultImage = "./data/ui/buttonBase.png";
	public String bgImage = defaultImage; 
	public float textProportion = 400f;
	
	public String picturesPath = null; 
	public int selected = 0;
	
	public PictureSelect(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion) {
		super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
		this.text = ""+value;
		this.textProportion = textProportion;
		deactivate();
		w.base.addEventHandler("lookLeft", w);
		w.base.addEventHandler("lookRight", w);
		w.base.addEventHandler("enter", w);
	}
	
	Node activeNode = null;
	Node deactiveNode = null;
	
	public int getSelection()
	{
		return selected;
	}
	
	public String getPictureId()
	{
		if (filesList!=null && filesList.size()>0)
			return filesList.get(selected).getName();
		return null;
	}
	
	ArrayList<File> filesList = new ArrayList<File>();
	HashMap<File, Quad> picQuads = new HashMap<File, Quad>();
	
	public void updateFiles()
	{
		picQuads.clear();
		filesList.clear();
		File f = new File(picturesPath);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("PicSelect # FILE: "+f.getAbsolutePath());
		String[] files = f.list();
		if (files!=null)
		for (String file:files)
		{
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("# FILE: "+file);
			if (file.endsWith(".png"))
			if (!new File(f.getAbsolutePath()+"/"+file).isDirectory())
			{
				filesList.add(new File(f.getAbsolutePath()+"/"+file));
			}
		}
		
	}

	@Override
	public void activate() {
		if (updated)
		{
			updated = false;
			updateFiles();
			selected = 0;
		}
		baseNode.detachAllChildren();
		{
			if (activeNode==null) {
				activeNode = new Node();
				try {
					Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
					w1.setSolidColor(ColorRGBA.white);
					activeNode.attachChild(w1);
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			if (filesList.size()!=0) {
				File f = filesList.get(selected);
				Quad q = picQuads.get(f);
				if (q==null)
				{
					try {
						q = Window.loadImageToQuad(f, dSizeX*0.96f, dSizeY*0.96f, dCenterX, dCenterY);
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
				q.setSolidColor(ColorRGBA.white);
				activeNode.attachChild(q);
			}
		}
		baseNode.attachChild(activeNode);
		baseNode.updateRenderState();
		super.activate();
	}

	@Override
	public void deactivate() {
		if (updated)
		{
			updated = false;
			updateFiles();
			selected = 0;
		}
		baseNode.detachAllChildren();
		{
			if (deactiveNode==null) {
				deactiveNode = new Node();
				try {
					Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
					w1.setSolidColor(ColorRGBA.gray);
					deactiveNode.attachChild(w1);
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			if (filesList.size()!=0) {
				File f = filesList.get(selected);
				Quad q = picQuads.get(f);
				if (q==null)
				{
					try {
						q = Window.loadImageToQuad(f, dSizeX*0.96f, dSizeY*0.96f, dCenterX, dCenterY);
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
				q.setSolidColor(ColorRGBA.gray);
				deactiveNode.attachChild(q);
			}
			
		}
		baseNode.attachChild(deactiveNode);
		baseNode.updateRenderState();
		super.deactivate();
	}

	@Override
	public boolean handleKey(String key) {
		if (key.equals("lookRight"))
		{
			if (selected == filesList.size()-1) return true;
			selected++;
			activate();
		} else
		if (key.equals("lookLeft"))
		{
			if (selected == 0) return true;
			selected--;
			activate();
		} 
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
