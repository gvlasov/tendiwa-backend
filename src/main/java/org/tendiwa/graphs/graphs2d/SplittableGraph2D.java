package org.tendiwa.graphs.graphs2d;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

public interface SplittableGraph2D extends Graph2D {
	void integrateCutSegment(CutSegment2D cutSegment);
}
