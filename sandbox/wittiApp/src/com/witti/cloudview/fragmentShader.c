//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

//This isn't a file that is used directly in the project
//It is for convienence of editing the shaders. The shaders
//Must be stored as strings in the artists.

//String strFShader = 
precision mediump float;
uniform sampler2D u_texture;
varying vec4 v_color;
void main(){
    vec4 tex = texture2D(u_texture, gl_PointCoord);
    gl_FragColor = v_color * tex;
}