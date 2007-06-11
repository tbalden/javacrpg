package org.jcrpg.threed.scene;


/**
 * coordinate hash rotated objects -> use it as a bottom!
 * @author pali
 *
 */
public class RenderedHashRotatedSide extends RenderedSide {

	
	/**
	 * @param objects Objects always rendered.
	 */
	public RenderedHashRotatedSide(SimpleModel[] objects)
	{
		super(objects);
	}
	
	public int rotation(int x,int y,int z)
	{
		//return Math.abs(((int)Math.round(Math.sin(x%2*y*z)*10f))%4);
		return Math.abs((x%2+y%2+z%2));
		
	}
}
