varying vec3 Normal;
varying float depth;
void main( void )
{
   gl_FragColor = vec4(normalize(Normal),1.0f);
}