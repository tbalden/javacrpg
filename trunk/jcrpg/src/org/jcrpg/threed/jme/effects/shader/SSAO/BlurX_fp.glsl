uniform sampler2D AOMap;
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
    float c = texture2D(AOMap,vTexCoord + vec2(r*g_InvResolutionFull.x , 0)).r;
    
    float d = fetchEyeZ(vTexCoord + vec2(r*g_InvResolutionFull.x , 0));

    float ddiff = d - center_d;
    float w = exp(-r*r*g_BlurFalloff - ddiff*ddiff*g_Sharpness);
    w_total += w;

    return w*c;
}

float blurX(void)
{
    float b = texture2D(AOMap,vTexCoord).r;
    w_total = 1.0;
    
    float center_d = fetchEyeZ(vTexCoord);
    
    for (float r = -g_BlurRadius; r <= g_BlurRadius; ++r)
    {
        //vec2 uv = vTexCoord + vec2(r*g_InvResolution.x , 0);
        b += blurFunction(r, center_d);   
    }

    return b/w_total;
}

void main(void)
{
	gl_FragColor = blurX();
	//float b = blurX();
   //gl_FragColor = vec4(b,b,b,b);
}