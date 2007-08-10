package org.jcrpg.abs.change;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class ChangingImpl implements Changing {
	
	public ArrayList<ChangeWaiter> waiters = new ArrayList<ChangeWaiter>();
	
	public void addChangeWaiter(ChangeWaiter waiter)
	{
		waiters.add(waiter);
	}
	
	public void change()
	{
		for (Iterator<ChangeWaiter> it = waiters.iterator();it.hasNext();)
		{
			it.next().change(this);
		}
	}

}
