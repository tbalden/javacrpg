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
