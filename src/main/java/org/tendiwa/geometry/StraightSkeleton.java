package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;

import java.util.List;
import java.util.Set;

public interface StraightSkeleton {
	UndirectedGraph<Point2D, Segment2D> graph();

	ImmutableSet<Polygon> cap(double depth);

	Set<Polygon> faces();

	List<Segment2D> originalEdges();
}
