//********************************************************************************
// Billboard grass fragment shader
//********************************************************************************

uniform sampler2D Texture;

varying vec4 VertexColor;
varying float Fog;

void main(void)
{
    vec4  Color   = texture2D(Texture, gl_TexCoord[0].st); // Diffuse texture
    Color.xyz  = mix( gl_Fog.color.xyz, Color.xyz, Fog ); // Add fog
   
    gl_FragColor = Color; // Final color
}