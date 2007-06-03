package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;

public class Side extends ChangingImpl {

	public Side(int type)
	{
		this.type = type;
	}
	
	/**
	 * id of the side type
	 */
	public int type;
	
}
