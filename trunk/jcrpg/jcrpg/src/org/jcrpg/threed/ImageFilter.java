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

package org.jcrpg.threed;

import java.nio.ByteBuffer;

import com.jme.image.Image;
import com.jme.util.geom.BufferUtils;

/**
 * Abstract functionality of any class that wants to filter from one JME image to another
 * JME image.
 * 
 * @author kevin
 */
public abstract class ImageFilter {
        /** The red channel index */
        protected static final int RED = 0;
        /** The green channel index */
        protected static final int GREEN = 1;
        /** The blue channel index */
        protected static final int BLUE = 2;
        /** The alpha channel index */
        protected static final int ALPHA = 3;
        
        /** The source image data */
        protected ByteBuffer source;
        /** The destination image data */
        protected ByteBuffer dest;
        /** The width of the image */
        protected int width;
        /** The height of the image */ 
        protected int height;
        /** The number of components to each pixel */
        protected int componentCount;
        
        /**
         * Apply this filter to a given source image. The filtered image
         * is returned.
         * 
         * @param sourceImage The source image to be filtered
         * @return The filtered result
         */
        public Image apply(Image sourceImage) {
                width = sourceImage.getWidth();
                height = sourceImage.getHeight();
                componentCount = 3;
                if (sourceImage.getType() == Image.RGBA8888) {
                        componentCount = 4;
                }
                
                source = sourceImage.getData();
                dest = BufferUtils.createByteBuffer(sourceImage.getData().limit());
                Image destImage = new Image(sourceImage.getType(), width, height, dest);
                
                filter();
                
                source.rewind();
                dest.rewind();
                
                return destImage;
        }

        /**
         * Write a single pixel into the source image
         * 
         * @param x The x coordinate to write to 
         * @param y The y coordinate to write to
         * @param pix The pixel to write (array of components, 0-255 values)
         */
        protected void writeSourcePixel(int x,int y,int[] pix) {
                byte[] value = new byte[pix.length];
                for (int i=0;i<value.length;i++) {
                        value[i] = (byte) pix[i];
                }
                
                source.position((x+(y*width))*componentCount);
                source.put(value,0,componentCount);
        }	

        /**
         * Read a single pixel from the source image
         * 
         * @param x The x coordinate to read from
         * @param y The y coordinate to read from
         * @return The pixel read from the image (array of components, 0-255 values)
         */
        protected int[] readSourcePixel(int x, int y) {
                byte[] pix = new byte[componentCount];
                
                source.position((x+(y*width))*componentCount);
                source.get(pix,0,componentCount);
                
                int[] ret = new int[pix.length];
                for (int i=0;i<ret.length;i++) {
                        ret[i] = pix[i] & 0xff;
                }
                
                return ret;
        }

        /**
         * Write a single pixel into the destination image
         * 
         * @param x The x coordinate to write to 
         * @param y The y coordinate to write to
         * @param pix The pixel to write (array of components, 0-255 values)
         */
        protected void writeDestPixel(int x,int y,int[] pix) {
                byte[] value = new byte[pix.length];
                for (int i=0;i<value.length;i++) {
                        value[i] = (byte) pix[i];
                }
                
                dest.position((x+(y*width))*componentCount);
                dest.put(value,0,componentCount);
        }	

        /**
         * Read a single pixel from the destination image
         * 
         * @param x The x coordinate to read from
         * @param y The y coordinate to read from
         * @return The pixel read from the image (array of components, 0-255 values)
         */
        protected int[] readDestPixel(int x, int y) {
                byte[] pix = new byte[componentCount];
                
                dest.position((x+(y*width))*componentCount);
                dest.get(pix,0,componentCount);
                
                int[] ret = new int[pix.length];
                for (int i=0;i<ret.length;i++) {
                        ret[i] = pix[i] & 0xff;
                }
                
                return ret;
        }

        /**
         * Filter the source image and place the results in the destination. This
         * method can utilise the readXXXPixel and writeXXXPixel to manipulate
         * the images
         */
        protected abstract void filter();
}