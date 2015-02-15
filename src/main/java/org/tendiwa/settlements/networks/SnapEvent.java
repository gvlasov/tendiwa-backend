package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.Point2D;

/**
 * Describes how a line snaps to a vertex or an edge of {@link org.tendiwa.settlements.networks.NetworkWithinCycle}.
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
	};

	SnapEvent integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter);

	Point2D target();
}
