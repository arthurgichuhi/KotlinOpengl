#version 300 es
in vec3 position;

out vec3 oColor;

uniform vec3 color;

void main(){
    gl_Position = vec4(position,1.0f);
    oColor = color;
}