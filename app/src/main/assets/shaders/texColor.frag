#version 300 es
precision mediump float;

in vec3 oColor;
in vec2 oTex;
flat in int oHasColor;
flat in int oHasTex;

out vec4 fragColor;

uniform sampler2D tex1;

void main(){
    fragColor = vec4(1.0f,1.0f,1.0f,1.0f);
    if(oHasColor > 0){
        fragColor *= vec4(oColor,1.0f);
    }
    if(oHasTex > 0){
        fragColor *= texture(tex1,oTex);
    }
    fragColor = vec4(vec3(fragColor),1.0f);
}