// "Depth of Field" demo for Ogre
// Copyright (C) 2006  Christian Lindequist Larsen
//
// This code is in the public domain. You may do whatever you want with it.

#define NUM_TAPS 12						// number of taps the shader will use

// uniform vec2 pixelSizeScene;			// pixel size of full resolution image
// uniform vec2 pixelSizeBlur;				// pixel size of downsampled and blurred image

uniform sampler2D scene;				// full resolution image
uniform sampler2D depth;				// full resolution image with depth values
// uniform sampler2D blur;					// downsampled and blurred image

//vec2 poisson[NUM_TAPS];					// containts poisson-distributed positions on the unit circle

// vec2 maxCoC = vec2(1.0, 2.0);			// maximum circle of confusion (CoC) radius
										// and diameter in pixels

// float radiusScale = 1.0;				// scale factor for minimum CoC size on low res. image

varying vec4 viewCoords;
varying vec2 vTexCoord;


void main()
{

   vec2 samples00 = vec2(-0.326212, -0.405805);
   vec2 samples01 = vec2(-0.840144, -0.073580);
   vec2 samples02 = vec2(-0.695914,  0.457137);
   vec2 samples03 = vec2(-0.203345,  0.620716);
   vec2 samples04 = vec2( 0.962340, -0.194983);
   vec2 samples05 = vec2( 0.473434, -0.480026);
   vec2 samples06 = vec2( 0.519456,  0.767022);
   vec2 samples07 = vec2( 0.185461, -0.893124);
   vec2 samples08 = vec2( 0.507431,  0.064425);
   vec2 samples09 = vec2( 0.896420,  0.412458);
   vec2 samples10 = vec2(-0.321940, -0.932615);
   vec2 samples11 = vec2(-0.791559, -0.597705);

   vec2 newCoord;
   vec4 sum = texture2D(scene, vTexCoord);

	 
  vec4 d = texture2D(depth, vTexCoord);
  
//  if (d.r<0)
//  {
  		//sum = 0;
	//	gl_FragColor =  sum;
  
  //} else
  {
  
  float sampleDist0 = (d.r)*0.013;
	

   newCoord = vTexCoord + sampleDist0 * samples00;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples01;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples02;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples03;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples04;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples05;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples06;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples07;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples08;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples09;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples10;
   sum += texture2D(scene, newCoord);

   newCoord = vTexCoord + sampleDist0 * samples11;
   sum += texture2D(scene, newCoord);

	   sum /= 13.0;
		sum.a = d.r*2;
		gl_FragColor =  sum;
		
	}
		
}
