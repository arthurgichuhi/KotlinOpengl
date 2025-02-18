#version 300 es

const vec2 lightBias = vec2(0.7, 0.6);//just indicates the balance between diffuse and ambient lighting

in vec2 oTex;
in vec3 oNorm;

out vec4 out_colour;

uniform sampler2D diffuseMap;
uniform vec3 lightDirection;

void main(void){
	
	vec4 diffuseColour = texture(diffuseMap, oTex);		
	vec3 unitNormal = normalize(oNorm);
	float diffuseLight = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
	out_colour = diffuseColour * diffuseLight;
	
}