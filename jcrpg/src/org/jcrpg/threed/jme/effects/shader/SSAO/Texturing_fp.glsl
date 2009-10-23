uniform sampler2D texture;

varying vec2 vTexCoord;

void main(void)
{
   // output the result
   vec4 color = texture2D(texture,vTexCoord);
   if (color.r<0.03)
   {
     color.r=1.0;
     color.g=1.0;
     color.b=1.0;
     color.a=1.0;
   }
   gl_FragColor = color;//texture2D(texture,vTexCoord);
}