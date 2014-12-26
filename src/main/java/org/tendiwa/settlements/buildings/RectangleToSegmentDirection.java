package org.tendiwa.settlements.buildings;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.*;

import java.util.ArrayList;
import java.util.List;

final class RectangleToSegmentDirection {

	public static final int MAX_AREAS_INTERSECTIONS = 2;

	/**
	 * Returns a {@link org.tendiwa.core.CardinalDirection} you need to go in from {@code rectangle}'s side to get to
	 * {@code segment}.
	 *
	 * @param rectangle
	 * @param segment
	 * @return
	 */
	public static CardinalDirection getDirectionToSegment(Segment2D segment, Rectangle rectangle) {
		if (Recs.rectangleIntersectsSegment(rectangle, segment)) {
			throw new GeometryException("Segment and rectangle should not intersect");
		}
		List<CardinalDirection> intersectedAreas = computeIntersectedCardinalAreas(segment, rectangle);
		if (intersectedAreas.size() == 1) {
			return intersectedAreas.get(0);
		} else if (intersectedAreas.size() == 0) {
			return chooseDirectionForCornerArea(segment, rectangle);
		} else {
			assert intersectedAreas.size() == 2 : intersectedAreas;
			return chooseAreaBySlope(segment, intersectedAreas);
		}
	}

	private static CardinalDirection chooseAreaBySlope(Segment2D segment, List<CardinalDirection> intersectedAreas) {
		assert intersectedAreas.size() == 2;
		assert intersectedAreas.get(0).isHorizontal() != intersectedAreas.get(1).isHorizontal();
		boolean horizontal = isSegmentSlopeHorizontal(segment);
		CardinalDirection first = intersectedAreas.get(0);
		if (first.isHorizontal() != horizontal) {
			return first;
		} else {
			return intersectedAreas.get(1);
		}
	}

	private static CardinalDirection chooseDirectionForCornerArea(Segment2D segment, Rectangle rectangle) {
		Point2D anyPoint = segment.start; // Could as well be segment.end since both ends are in the same corner area
		boolean horizontal = isSegmentSlopeHorizontal(segment);
		// TODO: Can be optimised because not all the cases are necessary to compute
		boolean toTheLeft = anyPoint.x < rectangle.x;
		boolean toTheTop = anyPoint.y < rectangle.y;
		boolean toTheRight = anyPoint.x > rectangle.getMaxX();
		boolean toTheBottom = anyPoint.y > rectangle.getMaxY();
		if (toTheLeft && toTheTop) {
			return horizontal ? CardinalDirection.N : CardinalDirection.W;
		} else if (toTheRight && toTheTop) {
			return horizontal ? CardinalDirection.N : CardinalDirection.E;
		} else if (toTheRight && toTheBottom) {
			return horizontal ? CardinalDirection.S : CardinalDirection.E;
		} else {
			assert toTheLeft && toTheBottom;
			return horizontal ? CardinalDirection.S : CardinalDirection.W;
		}
	}

	private static boolean isSegmentSlopeHorizontal(Segment2D segment) {
		double dx = segment.dx();
		if (dx == 0) {
			return false;
		} else {
			double dy = segment.dy();
			if (dy == 0) {
				return true;
			} else if (dx == dy) {
				return (Double.doubleToLongBits(segment.start.x) & 1) == 1;
			} else {
				return Math.abs(dx) > Math.abs(dy);
			}
		}
	}

	private static List<CardinalDirection> computeIntersectedCardinalAreas(Segment2D segment, Rectangle rectangle) {
		ArrayList<CardinalDirection> answer = new ArrayList<>(MAX_AREAS_INTERSECTIONS);
		for (CardinalDirection dir : CardinalDirection.values()) {
			if (intersectsArea(segment, rectangle, dir)) {
				answer.add(dir);
			}
		}
		return answer;
	}

	/**
	 * <a href="http://math.stackexchange.com/q/1080161/133365">Question on Stackexchange describing the problem</a>
	 *
	 * @param segment
	 * @param rectangle
	 * 	A rectangle near {@code segment}.
	 * @param dir
	 * 	Side of {@code rectangle}.
	 * @return
	 */
	private static boolean intersectsArea(
		Segment2D segment,
		Rectangle rectangle,
		CardinalDirection dir
	) {
		ComponentWiseInequality inequality = new ComponentWiseInequality(rectangle, dir);
		Vector startComponents = getComponentsForPoint(segment.start, inequality);
		boolean[] startResults = comparingResults(inequality, startComponents);
		if (areAllComponentsSatisfied(startResults)) {
			return true;
		}
		Vector endComponents = getComponentsForPoint(segment.end, inequality);
		boolean[] endResults = comparingResults(inequality, endComponents);
		if (areAllComponentsSatisfied(endResults)) {
			return true;
		}
		return isEachComponentSatisfiedForAtLeastOnePoint(startResults, endResults);
	}

	private static Vector getComponentsForPoint(Point2D point, ComponentWiseInequality inequality) {
		return inequality.signs.multiply(new BasicVector(new double[]{
			point.x,
			point.y
		}));
	}

	private static boolean areAllComponentsSatisfied(boolean[] startResults) {
		return startResults[0] && startResults[1] && startResults[2];
	}

	private static boolean[] comparingResults(ComponentWiseInequality inequality, Vector startComponents) {
		return new boolean[]{
			startComponents.get(0) >= inequality.constraints.get(0),
			startComponents.get(1) >= inequality.constraints.get(1),
			startComponents.get(2) >= inequality.constraints.get(2)
		};
	}

	private static boolean isEachComponentSatisfiedForAtLeastOnePoint(boolean[] startResults, boolean[] endResults) {
		return (startResults[0] ^ endResults[0])
			&& (startResults[1] ^ endResults[1])
			&& (startResults[2] ^ endResults[2]);
	}

	private static class ComponentWiseInequality {
		private final Vector constraints;
		private final Matrix signs;

		ComponentWiseInequality(Rectangle rectangle, CardinalDirection dir) {
			if (dir == CardinalDirection.N) {
				constraints = new BasicVector(new double[]{
					rectangle.x,
					-rectangle.getMaxX(),
					-rectangle.y
				});
				signs = new Basic2DMatrix(new double[][]{
					{1, 0},
					{-1, 0},
					{0, -1}
				});
			} else if (dir == CardinalDirection.E) {
				constraints = new BasicVector(new double[]{
					rectangle.y,
					-rectangle.getMaxY(),
					rectangle.getMaxX()
				});
				signs = new Basic2DMatrix(new double[][]{
					{0, 1},
					{0, -1},
					{1, 0}
				});
			} else if (dir == CardinalDirection.S) {
				constraints = new BasicVector(new double[]{
					rectangle.x,
					-rectangle.getMaxX(),
					rectangle.getMaxY()
				});
				signs = new Basic2DMatrix(new double[][]{
					{1, 0},
					{-1, 0},
					{0, 1}
				});
			} else {
				assert dir == CardinalDirection.W;
				constraints = new BasicVector(new double[]{
					rectangle.y,
					-rectangle.getMaxY(),
					-rectangle.x
				});
				signs = new Basic2DMatrix(new double[][]{
					{0, 1},
					{0, -1},
					{-1, 0}
				});
			}
		}
	}
}
