uniform sampler2D AOBlurXMap;
uniform sampler2D normalMap;

varying vec2 vTexCoord;

float w_total;

uniform vec2 g_InvResolutionFull;
uniform float g_BlurFalloff;
uniform float g_Sharpness;
uniform float g_BlurRadius;

// used to get the eye z coordinate
float fetchEyeZ(vec2 uv)
{
    float z = texture2D(normalMap,uv).a;
    return z;
}

float blurFunction(float r, float center_d)
{
    float c = texture2D(AOBlurXMap,vTexCoord + vec2(0.0 , r*g_InvResolutionFull.y)).r;
    
    float d = fetchEyeZ(vTexCoord + vec2(0.0 , r*g_InvResolutionFull.y));

    float ddiff = d - center_d;
    float w = exp(-r*r*g_BlurFalloff - ddiff*ddiff*g_Sharpness);
    w_total += w;

    return w*c;
}

float blurY(void)
{
    float b = texture2D(AOBlurXMap,vTexCoord).r;
    w_total = 1.0;
    
    float center_d = fetchEyeZ(vTexCoord);
    
    for (float r = -g_BlurRadius; r <= g_BlurRadius; ++r)
    {
        //vec2 uv = vTexCoord + vec2(0.0 , r*g_InvResolution.y);
        b += blurFunction(r, center_d);   
    }

    return b/w_total;
}

void main(void)
{
	gl_FragColor = blurY();
	//float b = blurY();
   //gl_FragColor = vec4(b,b,b,b);

   // calculate the y blur and combine with the x blur
   //gl_FragColor = blurY();//texture2D(AOBlurXMap,vTexCoord);//(blurY() + texture2D(AOBlurXMap,vTexCoord))/2.0;
	//gl_FragColor.rgb = (texture2D(normalMap,vTexCoord).xyz+1.0)/2.0;
	//gl_FragColor.r = texture2D(normalMap,vTexCoord).a;
}