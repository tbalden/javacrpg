package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;

public class Side extends ChangingImpl {

	int DEFAULT_SUBTYPE = 0;
	
	public Side(int type)
	{
		this.type = type;
		this.subtype = DEFAULT_SUBTYPE;
	}

	public Side(int type, int subType)
	{
		this.type = type;
		this.subtype = subType;
	}
	
	/**
	 * id of the main side type for determining continousity
	 */
	public int type;
	
	/**
	 * side subtype for determining further precision
	 */
	public int subtype;

	
	
}
