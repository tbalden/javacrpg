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
		return 1f+(HashUtil.mix(x, y, z)%100)*0.003f;
	}
}
