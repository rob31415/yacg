//uniform sampler2D texSamplerTMU0;
//uniform sampler2D texSamplerTMU1;
 
 
//void main(void)
//{
    //gl_FragColor = texture2D(texSamplerTMU0, vec2(gl_TexCoord[0])) *
     //              texture2D(texSamplerTMU1, vec2(gl_TexCoord[0]));
  //   gl_FragColor = vec4(0.6, 0.2, 0.3, 1.0);
   
//}



// borrowed from http://glsl.heroku.com/e#18315.0

uniform float g_Time;
uniform vec2 resolution;

const float nbDiv = 1.0;
const float speed = 1.5;

void main(void)
{
	vec3 col;
//	vec2 p = (gl_FragCoord.xy / min(resolution.x, resolution.y)) - 0.5;
	vec2 p = (gl_FragCoord.xy / 6.0) - 0.5;
	vec2 p2 = vec2(fract(p.x * nbDiv), fract(p.y * nbDiv));
	float offset = abs(sin(g_Time * speed) / 1.9);
	if (p2.x > offset && p2.x < 1.0 - offset && p2.y > offset && p2.y < 1.0 - offset)
		col = vec3(0.0);
	else
		col = vec3(1.0);
	gl_FragColor = vec4(col, 1.0);
}


//uniform float g_Time;
//void main(void)
//{
//	gl_FragColor = vec4(g_Time, g_Time, g_Time, 1.0);
//}
