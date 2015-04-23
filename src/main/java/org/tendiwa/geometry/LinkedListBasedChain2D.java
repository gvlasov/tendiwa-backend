package org.tendiwa.geometry;

import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.collections.IterableToStream;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableSegment2D;

import java.awt.Color;
import java.util.stream.Stream;

public final class LinkedListBasedChain2D implements Chain2D {

	private final DoublyLinkedNode<Segment2D> chainStart;

	public LinkedListBasedChain2D(DoublyLinkedNode<Segment2D> chainStart) {
		assert chainStart.isStartOfAChain();
		this.chainStart = chainStart;
	}

	@Override
	public Stream<Segment2D> asSegmentStream() {
		return IterableToStream.stream(chainStart.iterator());
	}

	@Override
	public Stream<Point2D> asPointStream() {
		Stream.Builder<Point2D> builder = Stream.builder();
		Point2D lastPoint = firstPoint(chainStart);
		builder.add(lastPoint);
		for (Segment2D segment : chainStart) {
			lastPoint = segment.anotherEnd(lastPoint);
			builder.add(lastPoint);
		}
		return builder.build();
	}

	private Point2D firstPoint(DoublyLinkedNode<Segment2D> chainStart) {
		assert chainStart == this.chainStart;
		assert chainStart.isStartOfAChain();
		Segment2D currentSegment = chainStart.getPayload();
		if (!chainStart.hasNext()) {
			TestCanvas.canvas.draw(
				new DrawableSegment2D.Thin(
					currentSegment,
					Color.cyan
				)
			);
		}
		Segment2D nextSegment = chainStart.getNext().getPayload();
		Point2D firstPoint;
		if (nextSegment.oneOfEndsIs(currentSegment.start())) {
			firstPoint = currentSegment.end();
		} else {
			assert nextSegment.oneOfEndsIs(currentSegment.end());
			firstPoint = currentSegment.start();
		}
		assert chainStart.getPayload().oneOfEndsIs(firstPoint);
		return firstPoint;
	}
}
