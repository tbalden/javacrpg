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
package org.jcrpg.threed.jme;

import java.awt.Color;
import java.util.HashMap;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jmex.awt.swingui.ImageGraphics;

public class TextureCreator {
	
	public static HashMap<Integer, Color> colorCache = new HashMap<Integer, Color>();
	public static void paintPoint(ImageGraphics set, int x, int y, int r, int g, int b, int a)
	{
		//a = 255;
		//r = 255;
		int k = (r<<24)+(g<<16)+(b<<8)+a;
		Color c = colorCache.get(k);
		if (c==null)
		{
			System.out.println(Math.max(0, Math.min(r,255))+" "+a);
			c = new Color(Math.max(0, Math.min(r,255)),Math.max(0, Math.min(g,255)),Math.max(0, Math.min(b,255)),a);
			colorCache.put(k, c);
		} 
		set.setColor(c);
		set.drawRect(x, y, 0, 0);
	}
		
	public static Texture newAlphaMaskTexture(boolean[][] data)
	{
		ImageGraphics graphics;
		graphics = ImageGraphics.createInstance(data.length, data[0].length, 0);
		graphics.setBackground(new Color(0,0,0,0));
		graphics.clearRect(0, 0, graphics.getImage().getWidth(), graphics.getImage().getHeight());

		for (int x=0; x<data.length; x++)
		{
			for (int y=0; y<data[0].length; y++)
			{
				//int r,g,b,a;
				int a = data[x][y]?100:255;
				//System.out.println("XY "+x+" "+y);
				int v = (int)((x*1f/data.length)*255);
				paintPoint(graphics, x, y, 255, v, v, v);//(int)((x*1f/data.length)*255));
			}
				
		}
		//TextureState staticTexState = J3DCore.getInstance().getDisplay().getRenderer().createTextureState();
		Texture texture = new Texture2D();
		texture.setMagnificationFilter( Texture.MagnificationFilter.NearestNeighbor);
		texture.setMinificationFilter(Texture.MinificationFilter.NearestNeighborNoMipMaps);
		graphics.update();
		texture.setImage(graphics.getImage());
		//texture.setScale(new Vector3f(10.1f,10.1f,10.1f));

		/*Image i = graphics.getImage();
		Texture alphatex0 = TextureManager.loadTexture(alpha0,Texture.MM_LINEAR_LINEAR,Texture.FM_LINEAR,0.0f,true);
		*/
		graphics.update(texture,false);
		//staticTexState.setTexture(staticLayerTex);
		//staticTexState.apply();		
		
		return texture;
	}
	
}
