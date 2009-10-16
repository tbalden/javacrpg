//attribute vec3 modelTangent;

varying vec3 viewDirection;
varying vec3 lightDirections[2];
varying vec2 texcoords;
varying float att[2];

void main(void)
{
    gl_Position = ftransform();
    texcoords = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
    
 	/* Transform vertex into viewspace */
	vec4 vertexViewSpace = gl_ModelViewMatrix * gl_Vertex;
	
	/* Get view and light directions in viewspace */
	vec3 localViewDirection = -vertexViewSpace.xyz;
    
    /* Calculate tangent info - stored in attributes */
    vec3 normal = gl_NormalMatrix * gl_Normal;
    //vec3 tangent = gl_NormalMatrix * modelTangent;
	vec3 tangent = gl_NormalMatrix * (gl_Color.xyz*2.0-1.0);
    vec3 binormal = cross( normal, tangent );
    
    mat3 tangentBinormalNormalMatrix = mat3( tangent, binormal, normal );
    
    /* Transform localViewDirection into texture space */
    viewDirection = tangentBinormalNormalMatrix *  localViewDirection;
    
    /*viewDirection.x = dot( tangent, -vertexViewSpace.xyz );
    viewDirection.y = dot( binormal, -vertexViewSpace.xyz );
    viewDirection.z = dot( normal, -vertexViewSpace.xyz );*/
    vec3 localLightDirection;
    vec3 localLightDist;
    float dist;
    
	for(int i = 0; i < 2; i++) {
	    //localLightDirection = gl_LightSource[i].position.xyz;// + localViewDirection;

		localLightDist = vertexViewSpace.xyz - gl_LightSource[i].position.xyz;
		dist = length(localLightDist.xyz);
		att[i] = 1.0 / (gl_LightSource[i].constantAttenuation + gl_LightSource[i].linearAttenuation * dist + gl_LightSource[i].quadraticAttenuation * dist * dist);	
	    localLightDirection = -normalize(localLightDist).xyz;
	    
        /*lightDirections[i].x = dot( tangent, localLightDirection );
        lightDirections[i].y = dot( binormal, localLightDirection );
        lightDirections[i].z = dot( normal, localLightDirection );*/
        lightDirections[i] = normalize( localLightDirection * tangentBinormalNormalMatrix );
        
	} // for
} // main