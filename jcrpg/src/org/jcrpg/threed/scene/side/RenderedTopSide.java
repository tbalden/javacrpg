/*
 *  This file is part of JavaCRPG.
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
package org.jcrpg.threed.scene.side;

import org.jcrpg.threed.scene.model.SimpleModel;

/**
 * Used with e.g. roofs bending over a Cube, 
 * nonEdgeObjects are only displayed if there is no roof on a neighbor square.
 * @author pali
 *
 */
public class RenderedTopSide extends RenderedSide {

	public SimpleModel[] nonEdgeObjects;
	
	/**
	 * 
	 * @param objects Objects always rendered.
	 * @param nonEdgeObjects Objects only rendered if not on the edge of an areatype.
	 */
	public RenderedTopSide(SimpleModel[] objects, SimpleModel[] nonEdgeObjects)
	{
		super(objects);
		this.nonEdgeObjects = nonEdgeObjects;
		type = RS_TOPSIDE;
	}
}
