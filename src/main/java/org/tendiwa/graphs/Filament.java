package org.tendiwa.graphs;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.BasicMutablePolyline;
import org.tendiwa.geometry.MutablePolyline;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.util.ArrayList;
import java.util.List;

/**
 * A filament in a graph.
 */
public class Filament extends BasicMutablePolyline implements MutablePolyline {
	private final Graph2D supergraph;

	public Filament(Graph2D supergraph) {
		super(10);
		this.supergraph = supergraph;
	}

	@Override
	public Segment2D edge(Point2D a, Point2D b) {
		return supergraph.getEdge(a, b);
	}
}
