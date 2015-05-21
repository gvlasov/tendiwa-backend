package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Ray;
import org.tendiwa.geometry.Segment2D;

import java.util.Optional;

interface PropagationEvent extends PropagationStep {
	Segment2D addedSegment();

	Optional<Segment2D> splitSegmentMaybe();

	default Ray createNextRay() {
		return new Ray(
			target(),
			source().angleTo(target())
		);
	}
}