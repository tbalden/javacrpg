/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.threed.jme;

/*
* Copyright (c) 2003-2006 jMonkeyEngine
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*
* * Redistributions of source code must retain the above copyright
*   notice, this list of conditions and the following disclaimer.
*
* * Redistributions in binary form must reproduce the above copyright
*   notice, this list of conditions and the following disclaimer in the
*   documentation and/or other materials provided with the distribution.
*
* * Neither the name of 'jMonkeyEngine' nor the names of its contributors
*   may be used to endorse or promote products derived from this software
*   without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jcrpg.threed.NodePlaceholder;
import org.jcrpg.threed.PooledNode;
import org.jcrpg.threed.ModelPool.PoolItemContainer;
import org.jcrpg.threed.jme.geometryinstancing.ExactBufferPool;

import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * TiledTerrainBlock addition by Paul Illes (c) 2008.
 * It adds a plus one size bigger heightmap info that's bigger then the Tile's heightmap
 * by one size, so normals can be obtained from the bigger heighmap's vertex data generated
 * in constructor by buildHelperVertices.
 * 
 * <code>TerrainBlock</code> defines the lowest level of the terrain system.
 * <code>TerrainBlock</code> is the actual part of the terrain system that
 * renders to the screen. The terrain is built from a heightmap defined by a one
 * dimenensional int array. The step scale is used to define the amount of units
 * each block line will extend. Clod can be used to allow for level of detail
 * control. By directly creating a <code>TerrainBlock</code> yourself, you can
 * generate a brute force terrain. This is many times sufficient for small
 * terrains on modern hardware. If terrain is to be large, it is recommended
 * that you make use of the <code>TerrainPage</code> class.
 * 
 * @author Mark Powell
 * @version $Id: TerrainBlock.java,v 1.30 2006/11/19 16:09:53 renanse Exp $
 */
public class TiledTerrainBlock extends TriMesh implements PooledNode {

    private static final long serialVersionUID = 1L;

    // size of the block, totalSize is the total size of the heightmap if this
    // block is just a small section of it.
    private int size;

    private int totalSize;

    private short quadrant = 1;

    // x/z step
    private Vector3f stepScale;

    // use lod or not
    private boolean useClod;

    // center of the block in relation to (0,0,0)
    private Vector2f offset;

    // amount the block has been shifted.
    private float offsetAmount;

    // heightmap values used to create this block
    private int[] heightMap;

    private int[] oldHeightMap;

    private static Vector3f calcVec1 = new Vector3f();

    private static Vector3f calcVec2 = new Vector3f();

    private static Vector3f calcVec3 = new Vector3f();

    /**
     * Empty Constructor to be used internally only.
     */
    public TiledTerrainBlock() {
    }

    /**
     * For internal use only. Creates a new Terrainblock with the given name by
     * simply calling super(name)
     * 
     * @param name
     *            The name.
     * @see com.jme.scene.lod.AreaClodMesh#AreaClodMesh(java.lang.String)
     */
    public TiledTerrainBlock(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>TerrainBlock</code> object. The
     * parameters and heightmap data are then processed to generate a
     * <code>TriMesh</code> object for renderering.
     * 
     * @param name
     *            the name of the terrain block.
     * @param size
     *            the size of the heightmap.
     * @param stepScale
     *            the scale for the axes.
     * @param heightMap
     *            the height data.
     * @param heightMapBig
     *            the height data with the next Tile's heights too for tricky normal generation.
     * @param origin
     *            the origin offset of the block.
     * @param clod
     *            true will use level of detail, false will not.
     */
    public TiledTerrainBlock(String name, int size, Vector3f stepScale,
            int[] heightMap, int[] heightMapBig, Vector3f origin, boolean clod) {
        this(name, size, stepScale, heightMap, heightMapBig, origin, clod, size,
                new Vector2f(), 0);
    }

    
    /**
     * Constructor instantiates a new <code>TerrainBlock</code> object. The
     * parameters and heightmap data are then processed to generate a
     * <code>TriMesh</code> object for renderering.
     * 
     * @param name
     *            the name of the terrain block.
     * @param size
     *            the size of the block.
     * @param stepScale
     *            the scale for the axes.
     * @param heightMap
     *            the height data.
     * @param origin
     *            the origin offset of the block.
     * @param clod
     *            true will use level of detail, false will not.
     * @param totalSize
     *            the total size of the terrain. (Higher if the block is part of
     *            a <code>TerrainPage</code> tree.
     * @param offset
     *            the offset for texture coordinates.
     * @param offsetAmount
     *            the total offset amount. Used for texture coordinates.
     */
    public TiledTerrainBlock(String name, int size, Vector3f stepScale,
            int[] heightMap, int[] heightMapBig, Vector3f origin, boolean clod, int totalSize,
            Vector2f offset, float offsetAmount) {
        super(name);
        this.useClod = clod;
        this.size = size;
        
        this.helperSize = size+1;
        this.helperHeightMap = heightMapBig;
        
        this.stepScale = stepScale;
        this.totalSize = totalSize;
        this.offsetAmount = offsetAmount;
        this.offset = offset;
        this.heightMap = heightMap;

        setLocalTranslation(origin);

        buildVertices();
        buildHelperVertices();
        buildTextureCoordinates();
        buildNormals();
        buildColors();
        //VBOInfo vbo = new VBOInfo(true);
        //batch.setVBOInfo(vbo);

    }
    
    TriMesh helperBatch = new TriMesh();
    int[] helperHeightMap = null;
    int helperSize = 0;
    
    


    
    /**
     * <code>setDetailTexture</code> copies the texture coordinates from the
     * first texture channel to another channel specified by unit, mulitplying
     * by the factor specified by repeat so that the texture in that channel
     * will be repeated that many times across the block.
     * 
     * @param unit
     *            channel to copy coords to
     * @param repeat
     *            number of times to repeat the texture across and down the
     *            block
     */
    public void setDetailTexture(int unit, float repeat) {
        copyTextureCoordinates(0, unit, repeat);
    }

    /**
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     * 
     * @param position
     *            the vector representing the height location to check.
     * @return the height at the provided location.
     */
    public float getHeight(Vector2f position) {
        return getHeight(position.x, position.y);
    }

    /**
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     * 
     * @param position
     *            the vector representing the height location to check. Only the
     *            x and z values are used.
     * @return the height at the provided location.
     */
    public float getHeight(Vector3f position) {
        return getHeight(position.x, position.z);
    }

    /**
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     * 
     * @param x
     *            the x coordinate to check.
     * @param z
     *            the z coordinate to check.
     * @return the height at the provided location.
     */
    public float getHeight(float x, float z) {
        x /= stepScale.x;
        z /= stepScale.z;
        float col = FastMath.floor(x);
        float row = FastMath.floor(z);

        if (col < 0 || row < 0 || col >= size - 1 || row >= size - 1) {
            return Float.NaN;
        }
        float intOnX = x - col, intOnZ = z - row;

        float topLeft, topRight, bottomLeft, bottomRight;

        int focalSpot = (int) (col + row * size);

        // find the heightmap point closest to this position (but will always
        // be to the left ( < x) and above (< z) of the spot.
        topLeft = heightMap[focalSpot] * stepScale.y;

        // now find the next point to the right of topLeft's position...
        topRight = heightMap[focalSpot + 1] * stepScale.y;

        // now find the next point below topLeft's position...
        bottomLeft = heightMap[focalSpot + size] * stepScale.y;

        // now find the next point below and to the right of topLeft's
        // position...
        bottomRight = heightMap[focalSpot + size + 1] * stepScale.y;

        // Use linear interpolation to find the height.
        return FastMath.LERP(intOnZ, FastMath.LERP(intOnX, topLeft, topRight),
                FastMath.LERP(intOnX, bottomLeft, bottomRight));
    }

    /**
     * <code>getHeightFromWorld</code> returns the height of an arbitrary
     * point on the terrain when given world coordinates. If the point is
     * between height point values, the height is linearly interpolated. This
     * provides smooth height calculations. If the point provided is not within
     * the bounds of the height map, the NaN float value is returned
     * (Float.NaN).
     * 
     * @param position
     *            the vector representing the height location to check.
     * @return the height at the provided location.
     */
    public float getHeightFromWorld(Vector3f position) {
        Vector3f locationPos = calcVec1.set(position).subtractLocal(
                localTranslation);

        return getHeight(locationPos.x, locationPos.z);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     * 
     * @param position
     *            the vector representing the location to find a normal at.
     * @param store
     *            the Vector3f object to store the result in. If null, a new one
     *            is created.
     * @return the normal vector at the provided location.
     */
    public Vector3f getSurfaceNormal(Vector2f position, Vector3f store) {
        return getSurfaceNormal(position.x, position.y, store);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     * 
     * @param position
     *            the vector representing the location to find a normal at. Only
     *            the x and z values are used.
     * @param store
     *            the Vector3f object to store the result in. If null, a new one
     *            is created.
     * @return the normal vector at the provided location.
     */
    public Vector3f getSurfaceNormal(Vector3f position, Vector3f store) {
        return getSurfaceNormal(position.x, position.z, store);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     * 
     * @param x
     *            the x coordinate to check.
     * @param z
     *            the z coordinate to check.
     * @param store
     *            the Vector3f object to store the result in. If null, a new one
     *            is created.
     * @return the normal unit vector at the provided location.
     */
    public Vector3f getSurfaceNormal(float x, float z, Vector3f store) {
        x /= stepScale.x;
        z /= stepScale.z;
        float col = FastMath.floor(x);
        float row = FastMath.floor(z);

        if (col < 0 || row < 0 || col >= size - 1 || row >= size - 1) {
            return null;
        }
        float intOnX = x - col, intOnZ = z - row;

        if (store == null)
            store = new Vector3f();

        Vector3f topLeft = store, topRight = calcVec1, bottomLeft = calcVec2, bottomRight = calcVec3;

        int focalSpot = (int) (col + row * size);
        TriMesh batch = this;

        // find the heightmap point closest to this position (but will always
        // be to the left ( < x) and above (< z) of the spot.
        BufferUtils.populateFromBuffer(topLeft, batch.getNormalBuffer(),
                focalSpot);

        // now find the next point to the right of topLeft's position...
        BufferUtils.populateFromBuffer(topRight, batch.getNormalBuffer(),
                focalSpot + 1);

        // now find the next point below topLeft's position...
        BufferUtils.populateFromBuffer(bottomLeft, batch.getNormalBuffer(),
                focalSpot + size);

        // now find the next point below and to the right of topLeft's
        // position...
        BufferUtils.populateFromBuffer(bottomRight, batch.getNormalBuffer(),
                focalSpot + size + 1);

        // Use linear interpolation to find the height.
        topLeft.interpolate(topRight, intOnX);
        bottomLeft.interpolate(bottomRight, intOnX);
        topLeft.interpolate(bottomLeft, intOnZ);
        return topLeft.normalizeLocal();
    }
    
    
    public static IntBuffer COMMON_INDEX_BUFFER = null;
    public static int COMMON_SIZE = 2;

    /**
     * <code>buildVertices</code> sets up the vertex and index arrays of the
     * TriMesh.
     */
    private void buildVertices() {
    	TriMesh batch = this;
        batch.setVertexCount(heightMap.length);
        if (batch.getVertexBuffer()==null || !(batch.getVertexBuffer().limit()==batch.getVertexCount()*3))
        {
        	if (batch.getVertexBuffer()!=null)
        	{
        		ExactBufferPool.releaseVector3Buffer(batch.getVertexBuffer());
        	}
        	batch.setVertexBuffer(ExactBufferPool.getVector3Buffer(batch.getVertexCount()));
        }
        //batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch
          //      .getVertexBuffer(), batch.getVertexCount()));
        Vector3f point = new Vector3f();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                point.set(x * stepScale.x, heightMap[x + (y * size)]
                        * stepScale.y, y * stepScale.z);
                BufferUtils.setInBuffer(point, batch.getVertexBuffer(),
                        (x + (y * size)));
            }
        }

        // set up the indices
        batch.setTriangleQuantity(((size - 1) * (size - 1)) * 2);

        // for common size, use the common index buffer
        if (size==COMMON_SIZE)
        {
	        if (COMMON_INDEX_BUFFER==null)
	        {
	        	COMMON_INDEX_BUFFER = ExactBufferPool.getIntBuffer(batch
	                    .getTriangleCount() * 3); 
	
	            // go through entire array up to the second to last column.
	            for (int i = 0; i < (size * (size - 1)); i++) {
	                // we want to skip the top row.
	                if (i % ((size * (i / size + 1)) - 1) == 0 && i != 0) {
	                    continue;
	                }
	                // set the top left corner.
	                COMMON_INDEX_BUFFER.put(i);
	                // set the bottom right corner.
	                COMMON_INDEX_BUFFER.put((1 + size) + i);
	                // set the top right corner.
	                COMMON_INDEX_BUFFER.put(1 + i);
	                // set the top left corner
	                COMMON_INDEX_BUFFER.put(i);
	                // set the bottom left corner
	                COMMON_INDEX_BUFFER.put(size + i);
	                // set the bottom right corner
	                COMMON_INDEX_BUFFER.put((1 + size) + i);
	            }
	        }
	        batch.setIndexBuffer(COMMON_INDEX_BUFFER);
        } else
        {
        	batch.setIndexBuffer(ExactBufferPool.getIntBuffer(batch
                    .getTriangleCount() * 3)); 

            // go through entire array up to the second to last column.
            for (int i = 0; i < (size * (size - 1)); i++) {
                // we want to skip the top row.
                if (i % ((size * (i / size + 1)) - 1) == 0 && i != 0) {
                    continue;
                }
                // set the top left corner.
                batch.getIndexBuffer().put(i);
                // set the bottom right corner.
                batch.getIndexBuffer().put((1 + size) + i);
                // set the top right corner.
                batch.getIndexBuffer().put(1 + i);
                // set the top left corner
                batch.getIndexBuffer().put(i);
                // set the bottom left corner
                batch.getIndexBuffer().put(size + i);
                // set the bottom right corner
                batch.getIndexBuffer().put((1 + size) + i);
            }
        }
        
    }
    
    public void releaseExtraBuffers()
    {
    	ExactBufferPool.releaseVector3Buffer(helperBatch.getVertexBuffer());
    	helperBatch.setVertexBuffer(null);
    }

    private void buildHelperVertices() {
    	int size = helperSize;
    	int[] heightMap = helperHeightMap;
        TriMesh batch = helperBatch;
        batch.setVertexCount(heightMap.length);
        if (batch.getVertexBuffer()==null || !(batch.getVertexBuffer().limit()==batch.getVertexCount()*3))
        {
        	if (batch.getVertexBuffer()!=null)
        	{
        		ExactBufferPool.releaseVector3Buffer(batch.getVertexBuffer());
        	}
        	batch.setVertexBuffer(ExactBufferPool.getVector3Buffer(batch.getVertexCount()));
        }
        //batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch
          //      .getVertexBuffer(), batch.getVertexCount()));
        Vector3f point = new Vector3f();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                point.set(x * stepScale.x, heightMap[x + (y * size)]
                        * stepScale.y, y * stepScale.z);
                BufferUtils.setInBuffer(point, batch.getVertexBuffer(),
                        (x + (y * size)));
            }
        }

    }

    /**
     * <code>buildTextureCoordinates</code> calculates the texture coordinates
     * of the terrain.
     */
    public void buildTextureCoordinates() {
        float offsetX = offset.x + (offsetAmount * stepScale.x);
        float offsetY = offset.y + (offsetAmount * stepScale.z);
        TriMesh batch = this;

        FloatBuffer texs = null;
        if (batch.getTextureCoords(0).coords==null || !(batch.getTextureCoords(0).coords.limit()==batch.getVertexCount()*2))
        {
        	if (batch.getTextureCoords(0).coords!=null)
        	{
        		ExactBufferPool.releaseVector2Buffer(batch.getTextureCoords(0).coords);
        	}
        	batch.getTextureCoords(0).coords = ExactBufferPool.getVector2Buffer(batch.getVertexCount());
        }
        texs = batch.getTextureCoords(0).coords;
        //FloatBuffer texs = BufferUtils.createVector2Buffer(batch
          //      .getTextureBuffers().get(0), batch.getVertexCount());
        //batch.getTextureBuffers().set(0, texs);
        texs.clear();

        batch.getVertexBuffer().rewind();
        for (int i = 0; i < batch.getVertexCount(); i++) {
            texs.put((batch.getVertexBuffer().get() + offsetX)
                    / (stepScale.x * (totalSize - 1)));
            batch.getVertexBuffer().get(); // ignore vert y coord.
            texs.put((batch.getVertexBuffer().get() + offsetY)
                    / (stepScale.z * (totalSize - 1)));
        }
    }

    /**
     * <code>buildNormals</code> calculates the normals of each vertex that
     * makes up the block of terrain.
     */
    private void buildNormals() {
    	// here's the tricky part -->
    	// we use Big Sized VertexBuffer with the additional plus vertex for the next adj/opp Cube height
    	// but put the normals into the small sized normals map
    	// (check helperNormalIndex trick, and checking the plus column skip.)
    	
        TriMesh batch = this;

        if (batch.getNormalBuffer()==null || !(batch.getNormalBuffer().limit()==batch.getVertexCount()*3))
        {
        	if (batch.getNormalBuffer()!=null)
        	{
        		ExactBufferPool.releaseVector3Buffer(batch.getNormalBuffer());
        	}
        	batch.setNormalBuffer(ExactBufferPool.getVector3Buffer(batch.getVertexCount()));
        }
        //batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch
          //      .getNormalBuffer(), batch.getVertexCount()));
        Vector3f oppositePoint = new Vector3f();
        Vector3f adjacentPoint = new Vector3f();
        Vector3f rootPoint = new Vector3f();
        Vector3f tempNorm = new Vector3f();
        int size = helperSize;
        int adj = 0, opp = 0, normalIndex = 0;
        int helperNormalIndex = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
            	if (row==size-1)
            	{
            		break;
            	}
            	if (col==size-1)
            	{
            		helperNormalIndex++;
            		continue;
            	}
                BufferUtils.populateFromBuffer(rootPoint, batch
                        .getVertexBuffer(), normalIndex);
                if (row == size - 1) {
                    if (col == size - 1) { // last row, last col
                        // up cross left
                        adj = helperNormalIndex - size;
                        opp = helperNormalIndex - 1;
                    } else { // last row, except for last col
                        // right cross up
                        adj = helperNormalIndex + 1;
                        opp = helperNormalIndex - size;
                    }
                } else {
                    if (col == size - 1) { // last column except for last row
                        // left cross down
                        adj = helperNormalIndex - 1;
                        opp = helperNormalIndex + size;
                    } else { // most cases
                        // down cross right
                        adj = helperNormalIndex + size;
                        opp = helperNormalIndex + 1;
                    }
                }
                
                //  X   X
                //
                //  X   X
                //
                BufferUtils.populateFromBuffer(adjacentPoint, helperBatch
                        .getVertexBuffer(), adj);
                BufferUtils.populateFromBuffer(oppositePoint, helperBatch
                        .getVertexBuffer(), opp);
                
                tempNorm.set(adjacentPoint).subtractLocal(rootPoint)
                        .crossLocal(oppositePoint.subtractLocal(rootPoint))
                        .normalizeLocal();
                BufferUtils.setInBuffer(tempNorm, batch.getNormalBuffer(),
                        normalIndex);
                normalIndex++;helperNormalIndex++;
                }
        }
        // free the vertex buffer of the helperbatch, not needed anymore
        ExactBufferPool.releaseVector3Buffer(helperBatch.getVertexBuffer());
        helperBatch.setVertexBuffer(null);
    }
    
    /**
     * Sets the colors for each vertex to the color white.
     */
    private void buildColors() {
        setDefaultColor(ColorRGBA.white);
    }

    /**
     * Returns the height map this terrain block is using.
     * 
     * @return This terrain block's height map.
     */
    public int[] getHeightMap() {
        return heightMap;
    }

    /**
     * Returns the offset amount this terrain block uses for textures.
     * 
     * @return The current offset amount.
     */
    public float getOffsetAmount() {
        return offsetAmount;
    }

    /**
     * Returns the step scale that stretches the height map.
     * 
     * @return The current step scale.
     */
    public Vector3f getStepScale() {
        return stepScale;
    }

    /**
     * Returns the total size of the terrain.
     * 
     * @return The terrain's total size.
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * Returns the size of this terrain block.
     * 
     * @return The current block size.
     */
    public int getSize() {
        return size;
    }

    /**
     * If true, the terrain is created as a ClodMesh. This is only usefull as a
     * call after the default constructor.
     * 
     * @param useClod
     */
    public void setUseClod(boolean useClod) {
        this.useClod = useClod;
    }

    /**
     * Returns the current offset amount. This is used when building texture
     * coordinates.
     * 
     * @return The current offset amount.
     */
    public Vector2f getOffset() {
        return offset;
    }

    /**
     * Sets the value for the current offset amount to use when building texture
     * coordinates. Note that this does <b>NOT </b> rebuild the terrain at all.
     * This is mostly used for outside constructors of terrain blocks.
     * 
     * @param offset
     *            The new texture offset.
     */
    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    /**
     * Returns true if this TerrainBlock was created as a clod.
     * 
     * @return True if this terrain block is a clod. False otherwise.
     */
    public boolean isUseClod() {
        return useClod;
    }

    /**
     * Sets the size of this terrain block. Note that this does <b>NOT </b>
     * rebuild the terrain at all. This is mostly used for outside constructors
     * of terrain blocks.
     * 
     * @param size
     *            The new size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Sets the total size of the terrain . Note that this does <b>NOT </b>
     * rebuild the terrain at all. This is mostly used for outside constructors
     * of terrain blocks.
     * 
     * @param totalSize
     *            The new total size.
     */
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * Sets the step scale of this terrain block's height map. Note that this
     * does <b>NOT </b> rebuild the terrain at all. This is mostly used for
     * outside constructors of terrain blocks.
     * 
     * @param stepScale
     *            The new step scale.
     */
    public void setStepScale(Vector3f stepScale) {
        this.stepScale = stepScale;
    }

    /**
     * Sets the offset of this terrain texture map. Note that this does <b>NOT
     * </b> rebuild the terrain at all. This is mostly used for outside
     * constructors of terrain blocks.
     * 
     * @param offsetAmount
     *            The new texture offset.
     */
    public void setOffsetAmount(float offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    /**
     * Sets the terrain's height map. Note that this does <b>NOT </b> rebuild
     * the terrain at all. This is mostly used for outside constructors of
     * terrain blocks.
     * 
     * @param heightMap
     *            The new height map.
     */
    public void setHeightMap(int[] heightMap) {
        this.heightMap = heightMap;
    }
    public void setHeightMaps(int[][] heightMaps) {
        this.heightMap = heightMaps[0];
        this.helperHeightMap = heightMaps[1];
    }

    /**
     * Updates the block's vertices and normals from the current height map
     * values.
     */
    public void updateFromHeightMap() {
        if (!hasChanged())
            return;
        TriMesh batch = this;

        Vector3f point = new Vector3f();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                point.set(x * stepScale.x, heightMap[x + (y * size)]
                        * stepScale.y, y * stepScale.z);
                BufferUtils.setInBuffer(point, batch.getVertexBuffer(),
                        (x + (y * size)));
            }
        }

        // check if the helperBatch vertex buffer was released before, refill if so...
        helperBatch.setVertexCount(helperHeightMap.length);
        if (helperBatch.getVertexBuffer()==null || !(helperBatch.getVertexBuffer().limit()==helperBatch.getVertexCount()*3))
        {
        	if (helperBatch.getVertexBuffer()!=null)
        	{
        		ExactBufferPool.releaseVector3Buffer(helperBatch.getVertexBuffer());
        	}
        	helperBatch.setVertexBuffer(ExactBufferPool.getVector3Buffer(helperBatch.getVertexCount()));
        }
        
        for (int x = 0; x < helperSize; x++) {
            for (int y = 0; y < helperSize; y++) {
                point.set(x * stepScale.x, helperHeightMap[x + (y * helperSize)]
                        * stepScale.y, y * stepScale.z);
                BufferUtils.setInBuffer(point, helperBatch.getVertexBuffer(),
                        (x + (y * helperSize)));
            }
        }
        
        buildNormals();

        /*if (batch.getVBOInfo() != null) {
            batch.getVBOInfo().setVBOVertexID(-1);
            batch.getVBOInfo().setVBONormalID(-1);
            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(
                    getVertexBuffer(0));
            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(
                    getNormalBuffer(0));
        }*/
    }

    /**
     * <code>setHeightMapValue</code> sets the value of this block's height
     * map at the given coords
     * 
     * @param x
     * @param y
     * @param newVal
     */
    public void setHeightMapValue(int x, int y, int newVal) {
        heightMap[x + (y * size)] = newVal;
    }

    /**
     * <code>setHeightMapValue</code> adds to the value of this block's height
     * map at the given coords
     * 
     * @param x
     * @param y
     * @param toAdd
     */
    public void addHeightMapValue(int x, int y, int toAdd) {
        heightMap[x + (y * size)] += toAdd;
    }

    /**
     * <code>setHeightMapValue</code> multiplies the value of this block's
     * height map at the given coords by the value given.
     * 
     * @param x
     * @param y
     * @param toMult
     */
    public void multHeightMapValue(int x, int y, int toMult) {
        heightMap[x + (y * size)] *= toMult;
    }

    protected boolean hasChanged() {
        boolean update = false;
        if (oldHeightMap == null) {
            oldHeightMap = new int[heightMap.length];
            update = true;
        }

        for (int x = 0; x < oldHeightMap.length; x++)
            if (oldHeightMap[x] != heightMap[x] || update) {
                update = true;
                oldHeightMap[x] = heightMap[x];
            }

        return update;
    }

    /**
     * @return Returns the quadrant.
     */
    public short getQuadrant() {
        return quadrant;
    }

    /**
     * @param quadrant
     *            The quadrant to set.
     */
    public void setQuadrant(short quadrant) {
        this.quadrant = quadrant;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(size, "size", 0);
        capsule.write(totalSize, "totalSize", 0);
        capsule.write(quadrant, "quadrant", (short) 1);
        capsule.write(stepScale, "stepScale", Vector3f.ZERO);
        capsule.write(useClod, "useClod", false);
        capsule.write(offset, "offset", new Vector2f());
        capsule.write(offsetAmount, "offsetAmount", 0);
        capsule.write(heightMap, "heightMap", null);
        capsule.write(oldHeightMap, "oldHeightMap", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        size = capsule.readInt("size", 0);
        totalSize = capsule.readInt("totalSize", 0);
        quadrant = capsule.readShort("quadrant", (short) 1);
        stepScale = (Vector3f) capsule.readSavable("stepScale", new Vector3f(
                Vector3f.ZERO));
        useClod = capsule.readBoolean("useClod", false);
        offset = (Vector2f) capsule.readSavable("offset", new Vector2f());
        offsetAmount = capsule.readFloat("offsetAmount", 0);
        heightMap = capsule.readIntArray("heightMap", null);
        oldHeightMap = capsule.readIntArray("oldHeightMap", null);
    }
    
    PoolItemContainer pic;

	public PoolItemContainer getPooledContainer() {
		return pic;
	}

	public void setPooledContainer(PoolItemContainer cont) {
		pic = cont;
		
	}

	public void update(NodePlaceholder place) {
		// TODO Auto-generated method stub
		
	}
}