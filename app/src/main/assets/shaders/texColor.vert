#version 300 es
in vec3 position;
in vec3 color;
in vec2 tex;

out vec3 oColor;
out vec2 oTex;
flat out int oHasColor;
flat out int oHasTex;

uniform int hasColor;
uniform int hasTex;
uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(){
    gl_Position = projection * view * model * vec4(position,1.0f);
    oHasColor = hasColor;
    oHasTex = hasTex;
    if(oHasColor>0){
        oColor = color;
    }
    if(oHasTex>0){
        oTex = vec2(tex.x,tex.y);
    }
}