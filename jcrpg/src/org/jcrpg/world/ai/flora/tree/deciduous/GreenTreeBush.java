/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.world.ai.flora.tree.deciduous;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.StickingOut;
import org.jcrpg.world.ai.flora.FloraDescription;
import org.jcrpg.world.ai.flora.middle.MiddlePlant;
import org.jcrpg.world.ai.flora.tree.Tree;

public class GreenTreeBush extends Tree {
	
	public static final String TYPE_BUSH = "GREEN";
	public static final StickingOut SUBTYPE_BUSH = new StickingOut(TYPE_BUSH+"_TREEBUSH",TREE_COLOR);
	
	static Side[][] BUSH = new Side[][] { null, null, null,null,null,{new Side(TYPE_BUSH,SUBTYPE_BUSH)} };
	
	public GreenTreeBush()
	{
		super();
		defaultDescription = new FloraDescription(new Cube(null,BUSH,0,0,0),0,false,false);
	}

}
