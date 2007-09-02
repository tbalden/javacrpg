!!ARBfp1.0
OPTION ARB_fog_linear;
TEMP finalColor;
TEMP final2;
#TEX result.color, fragment.texcoord[0], texture[0], 2D;

TEMP diffuse;
TEX diffuse, fragment.texcoord[0], texture[0], 2D;
#MUL diffuse.xyz, diffuse, 1;
MUL finalColor, fragment.color, diffuse;
MUL result.color, finalColor, fragment.color.primary;

#TEX finalColor, fragment.texcoord[0], texture[0], 2D;



#        PARAM p = {2.4, 0, 0, 0};
#        PARAM fogColor = state.fog.color;
#PARAM fogColor = {0.5,0.1,0.1,1};
 #       TEMP fogFactor;
  #      ATTRIB fogCoord = fragment.fogcoord;
   #     MUL fogFactor.x, p.x, fogCoord.x;
    #    EX2_SAT fogFactor.x, -fogFactor.x;
     #   LRP result.color, fogFactor.x, finalColor, fogColor;





#PARAM fogParams = state.fog.params;
#PARAM p = {0.1, 0.7, 0, 0};
#PARAM fogColor = {0.5,0.1,0.1,1};
#PARAM fogColor = program.local[0];



#TEMP fogFactor;
#ATTRIB fogCoord = fragment.fogcoord;
##MAD_SAT fogFactor.x, p.x, fogCoord.x, p.y;
#MAD_SAT fogFactor.x, p.x, fragment.position, p.y;
#TEMP final2;
#LRP final2, fogFactor.x, finalColor, fogColor;
#MUL final2.xyz, final2, 1;
#MOV result.color, final2;
END