#version 300 es
in vec3 position;

out vec3 oTex;
out vec3 oNorm;
out vec3 oFragPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    oFragPos = vec3(model*vec4(position,1.0));
    vec3 normal = normalize(position);
    oNorm  =  mat3(transpose(inverse(model))) * normal;

    gl_Position = projection*view*model*vec4(position, 1.0);
    oTex = normalize(vec3(position.x, position.y, -position.z));
}