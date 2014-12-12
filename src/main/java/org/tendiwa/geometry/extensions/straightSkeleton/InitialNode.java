package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;

class InitialNode extends Node {
	Face face;

	InitialNode(Segment2D edge) {
		super(edge.start);
		TestCanvas.canvas.draw(edge.start, DrawingPoint2D.withColorAndSize(Color.white, 1));
		currentEdge = edge;
		currentEdgeStart = this;
	}

	void setPreviousInitial(InitialNode node) {
		previousEdgeStart = node;
	}

	void initFace() {
		this.face = new Face(currentEdgeStart, (InitialNode) currentEdgeStart.next());
	}


	@Override
	boolean isSplitRightNode() {
		return false;
	}

	@Override
	boolean isSplitLeftNode() {
		return false;
	}

	protected Face face() {
		return face;
	}
}
