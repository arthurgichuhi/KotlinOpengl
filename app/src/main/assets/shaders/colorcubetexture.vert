#version 300 es
in vec3 position;

out vec3 oTex;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    vec4 pos = projection*view*model*vec4(position, 1.0f);
    gl_Position = vec4(pos.x, pos.y, pos.w, pos.w);

    oTex = normalize(vec3(position.x, position.y, -position.z));
}