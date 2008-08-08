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
package org.jcrpg.threed.scene.side;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.util.HashUtil;


/**
 * coordinate hash rotated objects -> use it as a bottom!
 * @author pali
 *
 */
public class RenderedHashRotatedSide extends RenderedSide {

	public boolean scaleFix = false;
	
	/**
	 * @param objects Objects always rendered.
	 */
	public RenderedHashRotatedSide(Model[] objects)
	{
		super(objects);
		type = RS_HASHROTATED;
	}
	public RenderedHashRotatedSide(Model[] objects, boolean scaleFix)
	{
		super(objects);
		this.scaleFix = scaleFix;
		type = RS_HASHROTATED;
	}
	
	public int rotation(int x,int y,int z)
	{
		return (HashUtil.mix(x, y, z)%4);
	}
	
	public float scale(int x,int y,int z)
	{
		if (scaleFix) return 1f;
		return 0.9f+(HashUtil.mix(x, y, z)%100)/140f;
	}
}
