package org.tendiwa.core.worlds;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.extensions.TimeProfiler;

public interface GenesisToolbox<GenesisConfig> {

	public GenesisConfig config();

	public Canvas canvas();

	public TimeProfiler profiler();
}
