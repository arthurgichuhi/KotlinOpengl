#version 300 es
layout(location=0)in vec3 position;
layout(location=1)in vec2 tex;
layout(location=5)in vec3 color;

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