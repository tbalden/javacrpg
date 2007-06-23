/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.world.ai.flora.ground;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.flora.Flora;
import org.jcrpg.world.ai.flora.FloraDescription;

public class Sand extends Flora {

	public static final String TYPE_SAND = "SAND";
	public static final SideSubType SUBTYPE_SAND = new SideSubType(TYPE_SAND+"_SAND");
	
	static Side[][] SAND = new Side[][] { null, null, null,null,null,{new Side(TYPE_SAND,SUBTYPE_SAND)} };
	
	public Sand()
	{
		defaultDescription = new FloraDescription(new Cube(null,SAND,0,0,0),0,false,false);
	}
}
