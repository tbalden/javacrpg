!!ARBfp1.0
TEMP foggedColor;
TEMP finalColor;
TEMP diffuse;
TEX diffuse, fragment.texcoord[0], texture[0], 2D;
MUL diffuse.xyz, diffuse, 1;
MUL finalColor, fragment.color, diffuse;
PARAM fogColor = program.local[0];
PARAM fogFactor = program.local[1];
LRP foggedColor, fogFactor.x, finalColor, fogColor;
MUL foggedColor.xyz,foggedColor,1;
MOV result.color, foggedColor;
END