/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
