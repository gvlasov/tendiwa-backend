package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

import java.util.TreeSet;

final class FaceNodesSorter extends TreeSet<Node> {
	FaceNodesSorter(Point2D edgeStart, Point2D edgeEnd) {
		super(
			(Node o1, Node o2) -> {
				if (o1 == o2) {
					return 0;
				}
				if (o1.isPair(o2)) {
					SplitNode o1s = (SplitNode) o1;
					SplitNode o2s = (SplitNode) o2;
					assert o1s.isLeft() != o2s.isLeft();
					return o1s.isLeft() ? 1 : -1;
				} else {
					double projection1 = projectionOnEdge(o1, edgeStart, edgeEnd);
					double projection2 = projectionOnEdge(o2, edgeStart, edgeEnd);
					assert projection1 != projection2;
					return (int) Math.signum(projection1 - projection2);
				}
			}
		);
	}

	private static double projectionOnEdge(Node node, Point2D edgeStart, Point2D edgeEnd) {
//		return Vector2D
//			.fromStartToEnd(edge.start, vertex)
//			.dotProduct(edgeVector)
//			/ edgeVector.magnitude()
//			/ edgeVector.magnitude();
		double edx = edgeEnd.x - edgeStart.x;
		double edy = edgeEnd.y - edgeStart.y;
		return (
			(node.vertex.x - edgeStart.x) * edx
				+ (node.vertex.y - edgeStart.y) * edy
		) / (edx * edx + edy * edy);
	}
}
