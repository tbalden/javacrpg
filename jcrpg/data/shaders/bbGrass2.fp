!!ARBfp1.0
#OPTION ARB_fog_linear;
TEMP finalColor;
#TEX result.color, fragment.texcoord[0], texture[0], 2D;

TEMP diffuse;
TEX diffuse, fragment.texcoord[0], texture[0], 2D;
MUL diffuse.xyz, diffuse, 1;
MUL finalColor, fragment.color, diffuse;
#TEX finalColor, fragment.texcoord[0], texture[0], 2D;

PARAM fogParams = state.fog.params;
PARAM p = {-0.03, 0.5, 0, 0};
#PARAM fogColor = {0.5,0.1,0.1,1};
PARAM fogColor = program.local[0];

TEMP fogFactor;
ATTRIB fogCoord = fragment.fogcoord;
MAD_SAT fogFactor.x, fogParams.w, fogCoord.w, p.y;
TEMP final2;
LRP final2, fogFactor.x, finalColor, fogColor;
MIN final2.w, 1, final2.w;
MOV result.color, final2;
END