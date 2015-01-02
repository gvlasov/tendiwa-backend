package org.tendiwa.geometry;

import org.jgrapht.UndirectedGraph;

import java.util.List;
import java.util.Set;

public interface StraightSkeleton {
	UndirectedGraph<Point2D, Segment2D> graph();

	Set<Polygon> cap(double depth);

	Set<Polygon> faces();

	List<Segment2D> originalEdges();
}
