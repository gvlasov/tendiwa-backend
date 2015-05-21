package org.tendiwa.graphs;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.BasicMutablePolyline;
import org.tendiwa.geometry.BasicSegment2D;
import org.tendiwa.geometry.MutablePolyline;
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
	public final ImmutableList<Segment2D> toSegments() {
		List<Segment2D> segments = new ArrayList<>(size());
		int last = size() - 1;
		for (int i = 0; i < last; i++) {
			segments.add(supergraph.getEdge(get(i), get(i + 1)));
		}
		return ImmutableList.copyOf(segments);
	}
}
