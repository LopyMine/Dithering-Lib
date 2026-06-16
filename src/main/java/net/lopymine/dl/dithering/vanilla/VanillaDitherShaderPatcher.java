package net.lopymine.dl.dithering.vanilla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VanillaDitherShaderPatcher {

	private static final String MARKER = "// dithering_lib:dithering";

	private static final Pattern MAIN_PATTERN = Pattern.compile("void\\s+main\\s*\\(\\s*(?:void)?\\s*\\)\\s*\\{");

	private static final String DITHER_BODY = """
		layout(std140) uniform DitheringLibData {
		    float DitheringLibFar;
		    float DitheringLibNear;
		    float DitheringLibMinValue;
		    float DitheringLibFixedValue;
		    float DitheringLibPixelSize;
		};

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

		// Static mode (far == near == minValue == 0): use fixedValue directly.
		// Otherwise: distance progress between near and far, clamped by minValue, eased.
		float dithering_lib_visibility(float cameraDistance, float far, float near, float minValue, float fixedValue) {
		    if (far == 0.0 && near == 0.0 && minValue == 0.0) {
		        return fixedValue;
		    }
		    float v = clamp(smoothstep(near, far, cameraDistance), minValue, 1.0);
		    return dithering_lib_easeInOutCubic(v);
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
		"\n\tfloat dithering_lib_progress = dithering_lib_visibility(sphericalVertexDistance, DitheringLibFar, DitheringLibNear, DitheringLibMinValue, DitheringLibFixedValue);\n"
			+ "\tdithering_lib_applyDither(dithering_lib_progress, gl_FragCoord.xy, DitheringLibPixelSize);\n";

	public static String patchFragmentShader(String source) {
		if (source == null || source.contains(MARKER)) {
			return source;
		}

		Matcher main = MAIN_PATTERN.matcher(source);
		if (!main.find()) {
			return source;
		}

		String header = MARKER + " begin\n" + DITHER_BODY + MARKER + " end\n\n";

		return source.substring(0, main.start())
			+ header
			+ source.substring(main.start(), main.end())
			+ CALL_BLOCK
			+ source.substring(main.end());
	}
}
