package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;

class OriginalEdgeStart extends Node {
	Face face;

	OriginalEdgeStart(Segment2D edge) {
		super(edge.start);
		TestCanvas.canvas.draw(edge.start, DrawingPoint2D.withColorAndSize(Color.white, 1));
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
