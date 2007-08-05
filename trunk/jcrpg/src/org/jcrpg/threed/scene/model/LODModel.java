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

package org.jcrpg.threed.scene.model;


/**
 * A set of models with distance to be seen from.
 * @author pali
 */
public class LODModel extends Model {

	public String id;
	public Model[] models;
	public float[][] distances;
	
	/**
	 * LODModel
	 * @param models The models to be put together
	 * @param distances The distance Min Max floats in an array
	 */
	public LODModel(String id,Model[] models, float[][] distances)
	{
		this.id = id;
		this.models = models;
		this.distances = distances;
		
	}
}
