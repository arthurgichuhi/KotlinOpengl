#version 300 es
in vec3 position;
in vec3 color;
in vec2 tex;
in vec3 normal;

out vec3 oColor;
out vec2 oTex;
out vec3 oNorm;
out vec3 oFragPos;

flat out int oHasColor;
flat out int oHasTex;
flat out int oHasNormal;

uniform int hasColor;
uniform int hasTex;
uniform int hasNormal;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(){
    gl_Position = projection * view * model * vec4(position,1.0f);

    oHasColor = hasColor;
    oHasTex = hasTex;
    oHasNormal = hasNormal;

    if(oHasColor>0){
        oColor = color;
    }
    if(oHasTex>0){
        oTex = vec2(tex.x,tex.y);
    }
    if(hasNormal>0){
        oFragPos = vec3(model*vec4(position,1.0));
        oNorm  =  mat3(transpose(inverse(model))) * normal;
    }
}