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
package org.jcrpg.world.ai.fauna;

import java.util.HashMap;

import org.jcrpg.world.ai.abs.Feeling;
import org.jcrpg.world.ai.abs.Goal;
import org.jcrpg.world.ai.abs.Thinking;

public class Intelligent {

	public String id;
	public HashMap<String,Goal> orders, ownGoals, scriptedGoals;
	public HashMap<String,Feeling> ownFeelings, scriptedFeelings;
	public HashMap<String,Thinking> learnedThinkings, scriptedThinkings;
	
	
}
