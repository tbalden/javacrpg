package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;
import org.jcrpg.space.sidetype.SideSubType;

public class Side extends ChangingImpl {

	public static final SideSubType DEFAULT_SUBTYPE = new SideSubType("0");
	public static final String DEFAULT_TYPE = "0";

	public Side()
	{
		this.type = DEFAULT_TYPE;
		this.subtype = DEFAULT_SUBTYPE;
	}

	public Side(String type)
	{
		this.type = type;
		this.subtype = DEFAULT_SUBTYPE;
	}

	public Side(String type, SideSubType subType)
	{
		this.type = type;
		this.subtype = subType;
	}
	
	/**
	 * id of the main side type for determining continousity
	 */
	public String type;
	
	/**
	 * side subtype for determining further precision
	 */
	public SideSubType subtype;

	
	
}
