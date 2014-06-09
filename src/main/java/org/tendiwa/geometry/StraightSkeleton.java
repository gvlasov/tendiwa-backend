package org.tendiwa.geometry;

import org.jgrapht.UndirectedGraph;

import java.util.List;

public interface StraightSkeleton {
	public UndirectedGraph<Point2D, Segment2D> graph();

	public UndirectedGraph<Point2D, Segment2D> cap(double height);

	public List<Segment2D> originalEdges();
}
