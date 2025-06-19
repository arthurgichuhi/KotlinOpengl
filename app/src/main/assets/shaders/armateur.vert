#version 300 es

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 4;//max number of joints that can affect a vertex

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tex;
layout(location = 2) in vec3 normal;
layout(location = 3) in ivec4 jointIndices;
layout(location = 4) in vec4 weights;

out vec2 oTex;
out vec3 oNorm;

uniform mat4 jointTransforms[MAX_JOINTS];

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main(){
	
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

	for(int i=0;i<MAX_WEIGHTS;i++){
		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * weights[i];

		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * weights[i];
	}
	
	gl_Position = projection * view * model * totalLocalPos;
	oNorm = totalNormal.xyz;
	oTex = tex;

}