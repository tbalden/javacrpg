uniform sampler2D baseMap;
uniform sampler2D normalMap;
uniform sampler2D specularMap;
uniform sampler2D heightMap;

varying vec3 viewDirection;
varying vec3 lightDirections[2];
varying vec2 texcoords;
varying float att[2];
varying float fogFactor; 


uniform float heightValue;

void main(void)
{
	/* Extract colors from baseMap and specularMap */
	//vec4  baseColor = texture2D( baseMap, texcoords );
    vec3  normal = normalize( ( texture2D( normalMap, texcoords ).xyz * 2.0 ) - 1.0 );
	vec4  specularColor = texture2D( specularMap, texcoords );
	vec4  heightColor  = texture2D( heightMap, texcoords );
	
    vec3 normalizedViewDirection = normalize( viewDirection );

	float height = length(heightColor.xyz) * heightValue - heightValue * 0.5;
	vec2 newTexcoord = texcoords - normalizedViewDirection.xy * height;
	vec4 baseColor = texture2D( baseMap, newTexcoord );
		
	/* Sum up lighting models with OpenGL provided light/material properties */
    vec4 totalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;  // init with global ambient
    vec4 totalDiffuse; 
    vec4 totalSpecular;

    // ----------------------- LIGHTS -----------------------
    for(int i = 0; i < 2; i++) {
        vec3 normalizedLightDirection = normalize( lightDirections[i] );
        float NDotL = dot( normal, normalizedLightDirection );
        vec3 reflection = normalize( ( ( 2.0 * normal ) * NDotL ) - normalizedLightDirection ); 
 		vec4 attMul = vec4(att[i],att[i],att[i],1.0);
           
        /* Sum up lighting models with OpenGL provided light/material properties */
        totalAmbient  += gl_FrontLightProduct[i].ambient * attMul; 
        totalDiffuse  += clamp( gl_FrontLightProduct[i].diffuse * max( 0.0, NDotL ), 0.0, 1.0 ) * attMul; 
        totalSpecular += gl_FrontLightProduct[i].specular * specularColor * ( pow( max( 0.0, dot( reflection, normalizedViewDirection ) ), gl_FrontMaterial.shininess ) ) * attMul;
    } // for

	/* Set final pixel color as sum of lighting models */
	//gl_FragColor = totalAmbient * baseColor + totalDiffuse * baseColor + totalSpecular;
	
    vec4 finalColor = totalAmbient * baseColor + totalDiffuse * baseColor + totalSpecular;
    vec4 retCol = mix(gl_Fog.color, finalColor, fogFactor );
    retCol.w = baseColor.w;
    gl_FragColor = retCol;
    
}