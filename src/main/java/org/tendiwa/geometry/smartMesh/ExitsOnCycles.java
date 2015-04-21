package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.ShreddedSegment2D;

import java.util.*;
import java.util.stream.Stream;

final class ExitsOnCycles {
	private final Map<Segment2D, List<Point2D>> whereBranchesStuckIntoCycles;
	private final NetworkGenerationParameters config;

	ExitsOnCycles(NetworkGenerationParameters config) {
		this.config = config;
		this.whereBranchesStuckIntoCycles = new LinkedHashMap<>();
	}

	void addOnSegment(Point2D branchEnd, Segment2D segment) {
		whereBranchesStuckIntoCycles
			.computeIfAbsent(segment, this::obtainCollection)
			.add(branchEnd);
	}

	Stream<CutSegment2D> getPartitionedSegments() {
		return whereBranchesStuckIntoCycles
			.entrySet()
			.stream()
			.map(this::toCutSegment);
	}

	private CutSegment2D toCutSegment(Map.Entry<Segment2D, List<Point2D>> e) {
		return new ShreddedSegment2D(e.getKey(), e.getValue());
	}

	private List<Point2D> obtainCollection(Segment2D segment) {
		return new ArrayList<>(goodNumberOfParts(segment));
	}

	private int goodNumberOfParts(Segment2D segment) {
		return (int) Math.ceil(segment.length() / config.snapSize);
	}
}
