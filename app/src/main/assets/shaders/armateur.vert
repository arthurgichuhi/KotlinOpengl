#version 300 es

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 pos;
in vec2 tex;
in vec3 normal;
in ivec3 jointIndices;
in vec3 weights;

out vec2 oTex;
out vec3 oNorm;

uniform mat4 jointTransforms[MAX_JOINTS];

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(void){
	
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	for(int i=0;i<MAX_WEIGHTS;i++){
		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(pos, 1.0);
		totalLocalPos += posePosition * weights[i];
		
		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * weights[i];
	}
	
	gl_Position = projection * view * model * totalLocalPos;
	oNorm = totalNormal.xyz;
	oTex = tex;

}