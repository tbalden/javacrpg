uniform sampler2D rnm;
varying vec3 Normal;
varying float depth;
void main( void )
{
	vec4  baseColor      = texture2D( rnm, gl_TexCoord[0].xy );
	if (baseColor.a==0.0)
	{
		gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
	} else{
   		gl_FragColor = vec4(depth,0.0,0.0,0.0);
   	}
}