package org.tendiwa.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LineCircleIntersection {

	public static List<Point2D> findIntersections(
		Point2D segmentStart,
		Point2D segmentEnd,
		Point2D circleCenter,
		double circleRadius
	) {
		double baX = segmentEnd.x() - segmentStart.x();
		double baY = segmentEnd.y() - segmentStart.y();
		double caX = circleCenter.x() - segmentStart.x();
		double caY = circleCenter.y() - segmentStart.y();

		double a = baX * baX + baY * baY;
		double bBy2 = baX * caX + baY * caY;
		double c = caX * caX + caY * caY - circleRadius * circleRadius;

		double pBy2 = bBy2 / a;
		double q = c / a;

		double disc = pBy2 * pBy2 - q;
		if (disc < 0) {
			return Collections.emptyList();
		}
		// if disc == 0 ... dealt with later
		double tmpSqrt = Math.sqrt(disc);
		double abScalingFactor1 = -pBy2 + tmpSqrt;
		double abScalingFactor2 = -pBy2 - tmpSqrt;

		Point2D p1 = new BasicPoint2D(
			segmentStart.x() - baX * abScalingFactor1,
			segmentStart.y() - baY * abScalingFactor1
		);
		if (disc == 0) { // abScalingFactor1 == abScalingFactor2
			return Collections.singletonList(p1);
		}
		Point2D p2 = new BasicPoint2D(
			segmentStart.x() - baX * abScalingFactor2,
			segmentStart.y() - baY * abScalingFactor2
		);
		List<Point2D> answer = new ArrayList<>(2);
		answer.add(p1);
		answer.add(p2);
		return answer;
	}
}
