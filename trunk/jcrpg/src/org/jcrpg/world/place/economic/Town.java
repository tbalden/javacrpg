package org.jcrpg.world.place.economic;

import java.util.ArrayList;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.World;

/**
 * The meta class for joined neighbor districts.
 * @author pali
 *
 */
public class Town {
	
	public ArrayList<Population> subPopulations = new ArrayList<Population>();
	public String foundationName = null;
	

	public Town()
	{
		//System.out.println("NEW TOWN");
	}
	
	int[] retVal = null;

	/**
	 * Town size.
	 * @return Town size - no. of subpopulations
	 */
	public int getSize()
	{
		return subPopulations.size();
	}
	
	/**
	 * x, z
	 * @return
	 */
	public int[] getCenterCoordinates()
	{
		if (retVal!=null)
		{
			return retVal;
		} else
		{
			update();
		}
		return retVal; 
	}
	
	public void update()
	{
		long lcenterX = 0 , lcenterZ = 0;
		
		if (subPopulations.size()==0)
		{
			retVal = null;
			return;
		}
		retVal = new int[2];
		
		for (Population p:subPopulations)
		{
			lcenterX+=p.centerX;
			lcenterZ+=p.centerZ;
		}
		lcenterX/=subPopulations.size();
		lcenterZ/=subPopulations.size();
		retVal[0] = (int)lcenterX;
		retVal[1] = (int)lcenterZ;
		
	}
	

}
