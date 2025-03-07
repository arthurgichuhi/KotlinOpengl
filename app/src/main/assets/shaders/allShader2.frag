#version 300 es
precision mediump float;

in vec3 oColor;
in vec2 oTex;
in vec3 oNorm;
in vec3 oFragPos;

flat in int oHasTex;
flat in int oHasColor;
flat in int oHasNormal;

uniform sampler2D texture1;

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

void main(){
    fragColor = vec4(1.0F,1.0F,1.0F,1.0F);

    if(oHasColor>0){
        fragColor *= vec4(oColor,1.0f);
    }

    if(oHasTex>0){
        fragColor *= texture(texture1,oTex);
    }

    fragColor = vec4(vec3(fragColor),1.0f);

    if(oHasNormal>0){
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

}