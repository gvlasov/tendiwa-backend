package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.smartMesh.OriginalMeshCell;

public interface Mesh2D extends Graph2D {
	ImmutableSet<Cycle2D> meshCells();

	Cycle2D hull();
}
