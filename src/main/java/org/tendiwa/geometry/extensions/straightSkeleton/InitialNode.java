package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;

class InitialNode extends Node {
	InitialNode(Segment2D edge) {
		super(edge.start);
		TestCanvas.canvas.draw(edge.start, DrawingPoint2D.withColorAndSize(Color.white, 1));
		currentEdge = edge;
		currentEdgeStart = this;
	}
	void setPreviousInitial(InitialNode node) {
		previousEdgeStart = node;
	}

}
