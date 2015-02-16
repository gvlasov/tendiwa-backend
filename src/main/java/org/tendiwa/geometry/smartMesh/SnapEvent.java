package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

import java.util.Optional;

/**
 * Describes how a line snaps to a vertex or an edge of {@link NetworkWithinCycle}.
 */
interface SnapEvent {
	SnapEvent CHANCE_FAILED = new SnapEvent() {
		@Override
		public SnapEvent integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Point2D target() {
			return null;
		}

		@Override
		public Optional<Point2D> nextNewNodePoint() {
			return Optional.empty();
		}

	};

	SnapEvent integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter);

	Point2D target();

	Optional<Point2D> nextNewNodePoint();
}
