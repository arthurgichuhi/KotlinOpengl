#version 300 es
layout(location=0) in vec3 position;

out vec3 oColor;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(){
    gl_Position = projection * view * model * vec4(position,1.0);
    if(position.y < .1){
        oColor = vec3(0,0,1);
    }
    else{
        oColor = vec3(0,1,1);
    }
}