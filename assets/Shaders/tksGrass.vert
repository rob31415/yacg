uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;


void main(void)
{
 //gl_Position     = gl_ModelViewProjectionMatrix * gl_Vertex;
 //gl_TexCoord[0]  = gl_MultiTexCoord0;
 
  gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
