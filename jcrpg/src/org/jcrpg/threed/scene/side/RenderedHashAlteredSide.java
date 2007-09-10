package org.jcrpg.threed.scene.side;

import java.util.ArrayList;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.util.HashUtil;


/**
 * coordinate hash altered objects
 * @author pali
 *
 */
public class RenderedHashAlteredSide extends RenderedSide {

	public boolean scaleFix = false;
	
	Model[][] alteredObjectLists;
	
	Model[] returnModels = null;
	
	/**
	 * @param objects Objects always rendered.
	 */
	public RenderedHashAlteredSide(Model[] objects, Model[][] alteredObjectLists)
	{
		super(objects);
		this.alteredObjectLists = alteredObjectLists;
		type = RS_HASHALTERED;
	}
	public RenderedHashAlteredSide(Model[] objects, Model[][] alteredObjectLists, boolean scaleFix)
	{
		super(objects);
		this.alteredObjectLists = alteredObjectLists;
		this.scaleFix = scaleFix;
		type = RS_HASHALTERED;
	}
	
	public Model[] getRenderedModels(int x,int y,int z)
	{
		//if (returnModels==null) {
			ArrayList<Model> models = new ArrayList<Model>();
			for (int i=0; i<alteredObjectLists.length; i++)
			{
				Model[] objectList = alteredObjectLists[i];
				int p = HashUtil.mix(x+i, y, z)%objectList.length;
				models.add(objectList[p]);
				
			}
			returnModels = models.toArray(new Model[0]);
		//}
		return returnModels;
	}
	
	public float scale(int x,int y,int z)
	{
		if (scaleFix) return 1f;
		return 1f+(HashUtil.mix(x, y, z)%100)*0.003f;
	}
}
