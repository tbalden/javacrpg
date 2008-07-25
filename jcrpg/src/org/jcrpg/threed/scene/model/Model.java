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

package org.jcrpg.threed.scene.model;

public abstract class Model {
	
	public static final byte MODEL = 0;
	public static final byte LODMODEL = 1;
	public static final byte TEXTURESTATEVEGETATION = 2;
	public static final byte QUADMODEL = 4;
	public static final byte SIMPLEMODEL = 8;
	public static final byte PARTLYBILLBOARDMODEL = 16;
	public static final byte MOVINGMODEL = 32;
	
	public byte type = MODEL;

	public float[] disposition = new float[] {0,0,0};
	public float genericScale = 1f;
	public float[] scale = new float[] {1,1,1};

	public String id;
	public boolean poolable = true;
	/**
	 * Tells if this should be merged part of a GeometryBatcher. (Multitextured models cannot be used with geom batching!)
	 */
	public boolean batchEnabled = true;
	
	public boolean farViewEnabled = false;
	
	public boolean elevateOnSteep = false;
	
	/**
	 * Tells if the model is rotable on steep. Default value should be false. 
	 * Override it for quad grasses/ground plane.
	 */
	public boolean rotateOnSteep = false;
	/**
	 * Tells if the model casts shadow in shadow pass.
	 */
	public boolean shadowCaster = false;
	
	public boolean noSpecialSteepRotation = true;
	
	/**
	 * Tells if loader should load it into a ClodMesh.
	 */
	public boolean useClodMesh = false;
	
	public boolean cullNone = false;
	
	public boolean windAnimation = true;
	/**
	 * Tells GeometryBatchHelper if this model is to be shown not depending on distance
	 */
	public boolean alwaysRenderBatch = false;
}
