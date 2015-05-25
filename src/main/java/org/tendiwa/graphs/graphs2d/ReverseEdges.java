package org.tendiwa.graphs.graphs2d;

import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.util.HashSet;

final class ReverseEdges extends HashSet<Segment2D> {
	ReverseEdges(Polygon polygon) {
		SuccessiveTuples.forEachLooped(
			polygon,
			(a, b) -> {
				Segment2D edge = polygon.edge(a, b);
				if (edge.start() == b) {
					assert edge.end() == a;
					this.add(edge);
				} else {
					assert edge.start() == a && edge.end() == b;
				}
			}
		);
	}

	void replaceReverseEdge(CutSegment2D cutSegment) {
		Segment2D originalSegment = cutSegment.originalSegment();
		Vector2D originalVector = originalSegment.asVector();
		boolean isSplitEdgeAgainst = isAgainstCycleDirection(cutSegment.originalSegment());
		cutSegment.segmentStream()
			.filter(segment -> isSplitEdgeAgainst ^ originalVector.dotProduct(segment.asVector()) < 0)
			.forEach(this::setReverse);
		this.forgetReverse(originalSegment);
	}

	private void setReverse(Segment2D edge) {
		assert !this.contains(edge);
		this.add(edge);
	}

	private void forgetReverse(Segment2D edge) {
		this.remove(edge);
	}


	boolean isAgainstCycleDirection(Segment2D edge) {
		return this.contains(edge);
	}
}
