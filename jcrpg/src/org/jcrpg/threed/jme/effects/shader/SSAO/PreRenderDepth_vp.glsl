varying vec3 Normal;
varying float depth; // in eye space


uniform float zFar; 

void main( void )
{
	vec4 viewPos = gl_ModelViewMatrix * gl_Vertex;
	depth = -viewPos.z/zFar;

  // normal in eye space
   Normal = gl_NormalMatrix * gl_Normal;
   
   gl_Position = ftransform();
   gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;

}