package net.lopymine.dl.dithering.iris;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IrisShaderPatcher {

	private static final String MARKER = "// dithering_lib:dithering";

	private static final Pattern MAIN_PATTERN = Pattern.compile("void\\s+main\\s*\\(\\s*(?:void)?\\s*\\)\\s*\\{");

	private static final String DITHER_BODY = """
		const mat4 DITHERING_LIB_MAT = mat4(
		    1.0 / 17.0,  9.0 / 17.0,  3.0 / 17.0,  11.0 / 17.0,
		    13.0 / 17.0, 5.0 / 17.0,  15.0 / 17.0, 7.0 / 17.0,
		    4.0 / 17.0,  12.0 / 17.0, 2.0 / 17.0,  10.0 / 17.0,
		    16.0 / 17.0, 8.0 / 17.0,  14.0 / 17.0, 6.0 / 17.0
		);

		float dithering_lib_easeInOutCubic(float x) {
		    return x < 0.5 ?
		        4.0 * x * x * x :
		        1.0 - pow(-2.0 * x + 2.0, 3.0) / 2.0;
		}

		float dithering_lib_visibility(float cameraDistance, float far, float near, float minValue, float fixedValue) {
		    if (far == 0.0 && near == 0.0 && minValue == 0.0) {
		        return fixedValue;
		    }
		    float v = clamp(smoothstep(near, far, cameraDistance), minValue, 1.0);
		    return dithering_lib_easeInOutCubic(v);
		}

		float dithering_lib_cameraDistance() {
		    vec3 ndc = vec3(gl_FragCoord.xy / vec2(viewWidth, viewHeight), gl_FragCoord.z) * 2.0 - 1.0;
		    vec4 viewPos = gbufferProjectionInverse * vec4(ndc, 1.0);
		    return length(viewPos.xyz / viewPos.w);
		}

		void dithering_lib_applyDither(float progress, vec2 fragCoord, float ditherPixelSize) {
		    vec2 cell = fragCoord / ditherPixelSize;
		    int x = int(cell.x);
		    int y = int(cell.y);
		    if (progress < DITHERING_LIB_MAT[x % 4][y % 4]) {
		        discard;
		    }
		}
		""";

	private static final String CALL_BLOCK =
		"\n\tfloat dithering_lib_progress = dithering_lib_visibility(dithering_lib_cameraDistance(), DitheringLibFar, DitheringLibNear, DitheringLibMinValue, DitheringLibFixedValue);\n"
			+ "\tdithering_lib_applyDither(dithering_lib_progress, gl_FragCoord.xy, DitheringLibPixelSize);\n";

	public static String patchFragmentShader(String source) {
		if (source == null || isPatched(source)) {
			return source;
		}
		Matcher main = MAIN_PATTERN.matcher(source);
		if (!main.find()) {
			return source;
		}

		StringBuilder header = new StringBuilder();
		header.append(MARKER).append(" begin\n");

		header.append("uniform float DitheringLibFar;\n");
		header.append("uniform float DitheringLibNear;\n");
		header.append("uniform float DitheringLibMinValue;\n");
		header.append("uniform float DitheringLibFixedValue;\n");
		header.append("uniform float DitheringLibPixelSize;\n");

		if (!source.contains("viewWidth")) {
			header.append("uniform float viewWidth;\n");
		}
		if (!source.contains("viewHeight")) {
			header.append("uniform float viewHeight;\n");
		}
		if (!source.contains("gbufferProjectionInverse")) {
			header.append("uniform mat4 gbufferProjectionInverse;\n");
		}
		header.append(DITHER_BODY);
		header.append(MARKER).append(" end\n\n");

		int mainStart = main.start();
		int braceEnd = main.end();

		return source.substring(0, mainStart)
			+ header
			+ source.substring(mainStart, braceEnd)
			+ CALL_BLOCK
			+ source.substring(braceEnd);
	}

	public static boolean isPatched(String source) {
		return source != null && source.contains(MARKER);
	}
}
