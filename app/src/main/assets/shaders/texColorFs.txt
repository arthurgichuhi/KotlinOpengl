#version 300 es
precision mediump float;

in vec3 oColor;
in vec2 oTex;

out vec4 fragColor;

uniform sampler2D tex1;

void main(){
    fragColor = texture(tex1,oTex);
}