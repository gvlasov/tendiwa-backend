package org.tendiwa.core.worlds;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.extensions.TimeProfiler;

public interface GenesisToolbox<GenesisConfig> {

	public GenesisConfig config();

	public DrawableInto canvas();

	public TimeProfiler profiler();
}
