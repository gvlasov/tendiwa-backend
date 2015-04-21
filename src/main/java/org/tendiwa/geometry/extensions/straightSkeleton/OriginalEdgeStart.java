package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.GeometryPrimitives;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

/**
 * Apart from being a {@link Node}, this class acts as an access point to an original edge of a polygon emanating
 * from this node.
 */
final class OriginalEdgeStart extends Node {
	private Face face;

	OriginalEdgeStart(Segment2D edge) {
		super(edge.start());
		currentEdge = edge;
		currentEdgeStart = this;
	}

	void setPreviousInitial(OriginalEdgeStart node) {
		previousEdgeStart = node;
	}

	void initFace() {
		this.face = new IncompleteFace(currentEdgeStart, (OriginalEdgeStart) currentEdgeStart.next());
	}


	@Override
	boolean hasPair() {
		return false;
	}

	Face face() {
		return face;
	}

	void integrateSplitNodes(Node parent, LeftSplitNode leftNode, RightSplitNode rightNode) {
		Node leftLavNextNode, rightLavPreviousNode;
		assert !face.isClosed();
		leftLavNextNode = face.getNodeFromLeft(leftNode);
		rightLavPreviousNode = face.getNodeFromRight(rightNode);

		leftNode.setPreviousInLav(parent.previous());
		leftLavNextNode.setPreviousInLav(leftNode);

		rightNode.setPreviousInLav(rightLavPreviousNode);
		parent.next().setPreviousInLav(rightNode);

		parent.setProcessed();

		parent.growRightFace(rightNode);
		parent.growLeftFace(leftNode);
		face.addLink(leftNode, rightNode);
	}

	OriginalEdgeStart findAnotherOppositeEdgeStart(Node parent) {
		Node leftLavNextNode, rightLavPreviousNode;
		Segment2D oppositeInClosed = findClosestIntersectedSegment(parent.bisector);
		Node oneNode = null, anotherNode = null;
		for (Node node : face) {
			if (node.vertex.equals(oppositeInClosed.start())
				|| node.vertex.equals(oppositeInClosed.end())) {
				if (oneNode == null) {
					oneNode = node;
				} else {
					assert anotherNode == null;
					anotherNode = node;
				}
			}
		}
		assert oneNode != null && anotherNode != null;
		if (parent.bisector.isLeftOfRay(oneNode.vertex)) {
			leftLavNextNode = oneNode;
			rightLavPreviousNode = anotherNode;
		} else {
			leftLavNextNode = anotherNode;
			rightLavPreviousNode = oneNode;
		}
		while (leftLavNextNode.isProcessed()) {
			leftLavNextNode = leftLavNextNode.next();
		}
		while (rightLavPreviousNode.isProcessed()) {
			rightLavPreviousNode = rightLavPreviousNode.previous();
		}
		return leftLavNextNode.previousEdgeStart;
	}

	private Stream<Segment2D> asSegmentStream(Face face) {
		Collection<Segment2D> segments = new ArrayList<>();
		SuccessiveTuples.forEachLooped(face, (a, b) -> {
			if (a.vertex != b.vertex) {
				segments.add(segment2D(a.vertex, b.vertex));
			}
		});
		return segments.stream();
	}

	private Segment2D findClosestIntersectedSegment(Segment2D ray) {
		assert face.isClosed();
		return asSegmentStream(face)
			.filter(s -> {
				double r = new RayIntersection(s, ray).r;
				return r < 1. && r > 0.;
			})
			.min((a, b) -> (int) Math.signum(new RayIntersection(ray, a).r - new RayIntersection(ray, b).r))
			.get();
	}
}
