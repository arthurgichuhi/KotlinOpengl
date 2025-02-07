#version 300 es
precision mediump float;

in vec3 oTex;
in vec3 oNorm;
in vec3 oFragPos;

struct Material{
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};
struct Light{
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform vec3 cameraPos;
uniform Material material;
uniform Light light;

out vec4 fragColor;

uniform samplerCube skybox;
void main()
{
    fragColor = texture(skybox, oTex);

    //ambient
    vec3 ambient = light.ambient * material.ambient;

    //diffuse
    vec3 norm = normalize(oNorm);
    vec3 lightDir = normalize(light.position - oFragPos);
    float diff = max(dot(norm,lightDir),0.0);
    vec3 diffuse = light.diffuse * (diff * material.diffuse);

    //specular
    vec3 viewDir = normalize(cameraPos - oFragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * (spec * material.specular);

    vec3 phongOut = ambient + diffuse + specular;

    fragColor = fragColor * vec4(phongOut,1);
}