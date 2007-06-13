package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;

public class Side extends ChangingImpl {

	public static final String DEFAULT_SUBTYPE = "0";
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

	public Side(String type, String subType)
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
	public String subtype;

	
	
}
