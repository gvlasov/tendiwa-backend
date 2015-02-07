package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Segment2D;

/**
 * Apart from being a {@link Node}, this class acts as an access point to an original edge of a polygon emanating
 * from this node.
 */
class OriginalEdgeStart extends Node {
	Face face;

	OriginalEdgeStart(Segment2D edge) {
		super(edge.start);
		currentEdge = edge;
		currentEdgeStart = this;
	}

	void setPreviousInitial(OriginalEdgeStart node) {
		previousEdgeStart = node;
	}

	void initFace() {
		this.face = new Face(currentEdgeStart, (OriginalEdgeStart) currentEdgeStart.next());
	}


	@Override
	boolean hasPair() {
		return false;
	}

	protected Face face() {
		return face;
	}

}
