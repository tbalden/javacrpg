!!ARBfp1.0
PARAM fadeAlpha = program.local[0];

ATTRIB tex = fragment.texcoord;
ATTRIB col = fragment.color.primary;
OUTPUT outColor = result.color;
TEMP tmp;
TXP tmp, tex, texture[0], 2D;
TEMP tmpTex;
MUL tmpTex, tmp, col;
MOV tmpTex.a,fadeAlpha.x;
#fadeAlpha[0];
MOV outColor,tmpTex;
END
