#version 300 es
in vec3 position;
out vec3 oColor;

uniform vec3 color;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main(){
    gl_Position = projection * view * model * vec4(position,1.0f);

    oColor=color;
}