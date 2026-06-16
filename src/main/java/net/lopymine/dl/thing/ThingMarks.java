package net.lopymine.dl.thing;

import lombok.*;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
public class ThingMarks<T> {

	public static final ThreadLocal<ThingMarks<Boolean>> DITHERING_ENABLED = ThreadLocal.withInitial(ThingMarks::new);

	private boolean enabled;
	@Nullable
	private T value;

}
