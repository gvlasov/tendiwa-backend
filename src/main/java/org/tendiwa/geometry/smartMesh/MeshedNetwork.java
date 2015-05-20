package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.PerforatedCycle2D;

public interface MeshedNetwork extends Graph2D {

	ImmutableSet<? extends PerforatedCycle2D> cycles();

	default Graph2D outerHull() {
		return new OuterHull(this).graph();
	}
}
