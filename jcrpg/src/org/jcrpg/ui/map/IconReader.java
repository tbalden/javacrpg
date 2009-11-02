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

package org.jcrpg.ui.map;

import java.io.File;
import java.io.FileReader;

import com.jme.renderer.ColorRGBA;

/**
 * Reads a fixed size of icon from .ico format.
 * @author illes
 *
 */
public class IconReader {

	static int byteToInt(byte b)
	{
		if ((int)b<0)
		{
			return 256+(int)b;
		}
		return b;
	}

	public static ColorRGBA[][] readMapIconFile(String path)
	{
		try 
		{
			//System.out.println("___ "+path);
			File file = new File(path);
			
			FileReader r = new FileReader(file);
			char[] bytes = new char[4*WorldMap.PIXELS_PER_BLOCK*WorldMap.PIXELS_PER_BLOCK];
			ColorRGBA[][] pattern = new ColorRGBA[WorldMap.PIXELS_PER_BLOCK][WorldMap.PIXELS_PER_BLOCK];
			r.read(bytes, 0, 0x3e);
			r.read(bytes, 0, bytes.length);
			
			for (int x=0; x<WorldMap.PIXELS_PER_BLOCK; x++)
			{
				for (int y=0; y<WorldMap.PIXELS_PER_BLOCK; y++)
				{
					ColorRGBA c = new ColorRGBA();
					int count=0;
					int blue, green, red, alpha;
					blue = (byteToInt((byte)bytes[y*WorldMap.PIXELS_PER_BLOCK*4+x*4 + count++]));
					green = (byteToInt((byte)bytes[y*WorldMap.PIXELS_PER_BLOCK*4+x*4 + count++]));
					red = (byteToInt((byte)bytes[y*WorldMap.PIXELS_PER_BLOCK*4+x*4 + count++]));
					alpha = (byteToInt((byte)bytes[y*WorldMap.PIXELS_PER_BLOCK*4+x*4 + count++]));
					//System.out.print(blue+ " "+green+ " "+red+" -- ");
					c.b = blue/255f;
					c.g = green/255f;
					c.r = red/255f;
					c.a = alpha/255f;
					if (c.a>0.1) ; else c.a=0;
					//System.out.print(c.b+ " "+c.g+ " "+c.r+" | ");
					pattern[WorldMap.PIXELS_PER_BLOCK-1-y][x] = c;
				}
				System.out.println();
			}
			r.close();
			return pattern;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
}
