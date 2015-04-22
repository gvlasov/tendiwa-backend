package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.BasicSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.ArrayList;
import java.util.List;

public class PolygonSegments {
	/**
	 * Transform a polygon defined by a list of points to a list of segments.
	 *
	 * @param polygon
	 * 	A list of points.
	 * @return A list of segments from i'th to i+1'th points.
	 */
	public static List<Segment2D> toSegments(List<Point2D> polygon) {
		int prelast = polygon.size() - 1;
		List<Segment2D> answer = new ArrayList<>(polygon.size());
		for (int i = 0; i < prelast; i++) {
			addSegment(answer, polygon.get(i), polygon.get(i + 1));
		}
		addSegment(answer, polygon.get(prelast), polygon.get(0));
		return answer;
	}

	private static void addSegment(List<Segment2D> answer, Point2D start, Point2D end) {
		answer.add(new BasicSegment2D(start, end));
	}
}
