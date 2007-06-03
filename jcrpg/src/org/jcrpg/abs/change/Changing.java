package org.jcrpg.abs.change;


public interface Changing {
	
	public void addChangeWaiter(ChangeWaiter waiter);
	public void change();

}
