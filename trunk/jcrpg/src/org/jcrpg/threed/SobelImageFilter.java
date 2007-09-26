/*
 */

package org.jcrpg.threed;


/** 
 * A sobel filter image implementation used to generate normal maps from gray scale 
 * height fields.
 * 
 * Usage:
 * 
 * Image heightImage = TextureManager.loadImage(ResourceLoader.getResourceURL("heightfield.jpg"),true);
 * Image bumpImage = new SobelImageFilter().apply(heightImage);
 * Texture bumpMap = TextureManager.createTexture(bumpImage, Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
 *   		
 * @author kevin
 */
public class SobelImageFilter extends ImageFilter {
        /** The scale factor to apply to the bumping */
        private float scale = 1;
        
        /**
         * Create a new filter with a scale on bump mapping
         * 
         * @param scale The scale to apply
         */
        public SobelImageFilter(float scale) {
                this.scale = scale;
        }
        
        /**
         * Create a new filter 
         */
        public SobelImageFilter() {
        }

        /**
         * @see org.newdawn.jme.filter.ImageFilter#filter()
         */
        @Override
        protected void filter() {
                float dx;
                float dy;
                int[] pix;
                float nx;
                float ny;
                float nz;
                float length;

                for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                                // Do Y Sobel filter
                                pix = readSourcePixel((x - 1 + width) % width, (y + 1) % height);
                                dy = pix[RED] / 255.0f * -1.0f;
                                
                                pix = readSourcePixel(x % width, (y + 1) % height);
                                dy += pix[RED] / 255.0f * -2.0f;

                                pix = readSourcePixel((x + 1) % width, (y + 1) % height);
                                dy += pix[RED] / 255.0f * -1.0f;

                                pix = readSourcePixel((x - 1 + width) % width, (y - 1 + height) % height);
                                dy += pix[RED] / 255.0f * 1.0f;
                                
                                pix = readSourcePixel(x % width, (y - 1 + height) % height);
                                dy += pix[RED] / 255.0f * 2.0f;

                                pix = readSourcePixel((x + 1) % width, (y - 1 + height) % height);
                                dy += pix[RED] / 255.0f * 1.0f;
                                
                                // Do X Sobel filter
                                pix = readSourcePixel((x - 1 + width) % width, (y - 1 + height) % height);
                                dx = pix[RED] / 255.0f * -1.0f;

                                pix = readSourcePixel((x - 1 + width) % width, y % height);
                                dx += pix[RED] / 255.0f * -2.0f;

                                pix = readSourcePixel((x - 1 + width) % width, (y + 1) % height);
                                dx += pix[RED] / 255.0f * -1.0f;

                                pix = readSourcePixel((x + 1) % width, (y - 1 + height) % height);
                                dx += pix[RED] / 255.0f * 1.0f;

                                pix = readSourcePixel((x + 1) % width, y % height);
                                dx += pix[RED] / 255.0f * 2.0f;

                                pix = readSourcePixel((x + 1) % width, (y + 1) % height);
                                dx += pix[RED] / 255.0f * 1.0f;

                                // Cross Product of components of gradient reduces to
                                nx = -dx;
                                ny = -dy;
                                nz = 1/scale;
                                
                                // Normalize
                                length = 1.0f / ((float) Math.sqrt((nx * nx) + (ny * ny) + (nz * nz)));
                                nx *= length;
                                ny *= length;
                                nz *= length;

                                pix[RED] = floatToByte(nx);
                                pix[GREEN] = floatToByte(ny);
                                pix[BLUE] = floatToByte(nz);

                                writeDestPixel(x, y, pix);
                        }
                }
        }

        /**
         * Convert the floating point value to an int to store back into the image
         * 
         * @param in The floating point value generated from the filter
         * @return The int value to write back to the image
         */
        private int floatToByte(float in) {
                return (int) ((in + 1.0f) / 2.0f * 255.0f);
        }

}