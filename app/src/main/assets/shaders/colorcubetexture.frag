#version 300 es
precision mediump float;

in vec3 oTex;

out vec4 fragColor;

uniform samplerCube skybox;
void main()
{
    fragColor = texture(skybox, oTex);
}