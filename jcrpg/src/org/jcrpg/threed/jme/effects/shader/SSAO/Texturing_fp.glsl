uniform sampler2D texture;

varying vec2 vTexCoord;

void main(void)
{
   // output the result
   gl_FragColor = texture2D(texture,vTexCoord);
}