#version 300 es
in vec3 position;
in vec3 color;
in vec2 tex;

out vec3 oColor;
out vec2 oTex;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(){
    gl_Position = projection * view * model * vec4(position,1.0f);
    oColor = color;
    oTex = vec2(tex.x,tex.y);
}