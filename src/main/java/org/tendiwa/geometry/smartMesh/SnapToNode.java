package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public class SnapToNode implements PropagationEvent {
	private final Point2D source;
	private final Point2D target;

	SnapToNode(Point2D source, Point2D target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public void integrateInto(AppendableNetworkPart networkPart) {
		assert fullNetwork.graph().containsVertex(target);
		segmentInserter.addSecondaryNetworkEdge(source, target);
		networkPart.appendSegment(source.segmentTo(target ));
	}

	@Override
	public boolean createsNewSegment() {
		return true;
	}

	@Override
	public Point2D target() {
		return target;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

}
