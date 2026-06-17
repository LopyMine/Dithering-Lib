package net.lopymine.dl.thing;

import lombok.*;

@Setter
@Getter
public class RenderingMarker {

	public static final ThreadLocal<RenderingMarker> DITHERING_ENABLED = ThreadLocal.withInitial(RenderingMarker::new);

	private boolean enabled;

}
