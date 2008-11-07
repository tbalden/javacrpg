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

package org.jcrpg.threed.scene.model;


/**
 * A set of models with distance to be seen from.
 * @author pali
 */
public class LODModel extends Model {

	public Model[] models;
	public float[][] distances;
	
	/**
	 * LODModel
	 * @param models The models to be put together
	 * @param distances The distance Min Max floats in an array
	 */
	public LODModel(String id,Model[] models, float[][] distances)
	{
		this.type = LODMODEL;
		this.id = id;
		this.models = models;
		this.distances = distances;
		
	}
}
