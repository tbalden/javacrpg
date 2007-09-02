!!ARBfp1.0
OPTION ARB_fog_linear;
OPTION ARB_precision_hint_fastest;
TEMP diffuse;
TEX diffuse, fragment.texcoord[0], texture[0], 2D;
MUL diffuse.xyz, diffuse, 1;
MUL result.color, fragment.color, diffuse;
END