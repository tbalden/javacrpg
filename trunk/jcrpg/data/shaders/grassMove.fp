!!FP1.0
    OPTION ARB_fog_linear;
    TEMP diffuse, lm, reflect;
    TEX diffuse, fragment.texcoord[0], texture[0], 2D;
    TEX lm,      fragment.texcoord[1], texture[1], 2D;
    TEX reflect, fragment.texcoord[2], texture[2], CUBE;

    MUL diffuse, diffuse, 2;
    MUL diffuse, diffuse, lm;

    LRP result.color, program.env[10], reflect, diffuse;

