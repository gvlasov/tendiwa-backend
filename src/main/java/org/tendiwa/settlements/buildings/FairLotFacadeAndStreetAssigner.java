package org.tendiwa.settlements.buildings;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Multimap;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.LotStreetAssigner;

import java.util.*;

/**
 * Decides the
 * <ol>
 * <li>direction building lots face and</li>
 * <li>streets building lots are assigned to</li>
 * </ol>
 * based on the information of which streets are near those building lots.
 * <p>
 * The algorithm will ensure that, if possible, each street will have a fair number of buildings assigned to it
 * (without this ensuring, some streets could have been left without buildings since all buildings along such
 * "unfortunate" streets will also belong to other streets).
 */
public final class FairLotFacadeAndStreetAssigner implements LotFacadeAssigner, LotStreetAssigner {
	private final LotsTouchingStreets lotsAndStreets;
	private final Map<RectangleWithNeighbors, CardinalDirection> facades;
	private final Map<RectangleWithNeighbors, List<Point2D>> lotToStreet;
	private final Multimap<List<Point2D>, RectangleWithNeighbors> streetToLots;
	private final IdentityHashMap<List<Point2D>, Double> streetLengths;
	private final IdentityHashMap<List<Point2D>, Double> streetThirstsForLot;
	private final ImmutableCollection<List<Point2D>> streets;
	private ImmutableCollection<RectangleWithNeighbors> lots;

	private FairLotFacadeAndStreetAssigner(LotsTouchingStreets lotsTouchingStreets) {
		this.lotsAndStreets = lotsTouchingStreets;
		streets = lotsAndStreets.getStreets();
		lots = lotsAndStreets.getLots();
		int streetsSize = streets.size();
		int lotsSize = lots.size();
		streetLengths = new IdentityHashMap<>(streetsSize);
		streetThirstsForLot = new IdentityHashMap<>(streetsSize);
		// TODO: Keys are are Lists, that means their hashing is O(n), which is slow for long streets. IdentityMultimap
		// would be great here, but there's no such thing in Guava.
		streetToLots = HashMultimap.create(streetsSize, lotsSize / streetsSize);
		facades = new HashMap<>(lotsSize);
		lotToStreet = new LinkedHashMap<>(lotsSize); // LinkedHashMap used to preserve iteration order.
		assignSingleStreetBuildings();
		fairlyAssignTheRestOfBuildings();
	}

	/**
	 * Assigns a street to lots. For each lot, algorithms selects a street that has the greatest <i>thirst for
	 * lot</i>. Thirst for lot is {@code length(street)/numberOfLotsAlreadyAssignedTo(street)} and may change with each
	 * iteration.
	 */
	private void fairlyAssignTheRestOfBuildings() {
		for (List<Point2D> street : streets) {
			streetLengths.put(street, computeStreetLength(street));
			// Initially all streets have effectively infinite thirst (because they h
			if (!streetThirstsForLot.containsKey(street)) {
				streetThirstsForLot.put(street, Double.MAX_VALUE);
			}
		}
		for (RectangleWithNeighbors lot : lots) {
			if (facades.containsKey(lot)) {
				continue;
			}
			List<Point2D> thirstiestStreet = null;
			Segment2D thirstiestSegment = null;
			double maxThirst = -1;
			for (Segment2D segment : lotsAndStreets.getSegmentsForLot(lot)) {
				List<Point2D> street = lotsAndStreets.getStreetForSegment(segment);
				double thirst = streetThirstsForLot.get(street);
				if (thirst > maxThirst) {
					thirstiestStreet = street;
					maxThirst = thirst;
					thirstiestSegment = segment;
				}
			}
			assert thirstiestStreet != null;
			assert thirstiestSegment != null;
			lotToStreet.put(lot, thirstiestStreet);
			facades.put(lot, getDirectionToSegment(thirstiestSegment, lot.rectangle));
			streetToLots.put(thirstiestStreet, lot);
			updateThirst(thirstiestStreet);
		}
	}

	private void updateThirst(List<Point2D> street) {
		streetThirstsForLot.put(street, streetLengths.get(street) / streetToLots.get(street).size());
	}

	private static double computeStreetLength(List<Point2D> street) {
		int size = street.size();
		double sum = 0;
		for (int i = 1; i < size; i++) {
			sum += street.get(i).distanceTo(street.get(i - 1));
		}
		return sum;
	}

	/**
	 * Assign facade directions and streets to those buildings that have only a single street near them according to
	 * {@link #lotsAndStreets}.
	 */
	private void assignSingleStreetBuildings() {
		for (RectangleWithNeighbors lot : lotsAndStreets.getLots()) {
			Set<List<Point2D>> streetsForLot = lotsAndStreets.getStreetsForLot(lot);
			if (streetsForLot.size() == 1) {
				Collection<Segment2D> segmentsForLot = lotsAndStreets.getSegmentsForLot(lot);
				for (Segment2D segment : segmentsForLot) {
					facades.put(lot, getDirectionToSegment(segment, lot.rectangle));
					List<Point2D> street = streetsForLot.iterator().next();
					lotToStreet.put(lot, street);
					streetToLots.put(street, lot);
					updateThirst(street);
				}
			}
		}
	}


	/**
	 * Returns a direction you need to go from {@code rectangle}'s side to get to {@code segment}.
	 *
	 * @param rectangle
	 * @param segment
	 * @return
	 */
	static CardinalDirection getDirectionToSegment(Segment2D segment, Rectangle rectangle) {
		assert !Recs.rectangleIntersectsSegment(rectangle, segment);
		// For line equation y=k*x+b
		double dy = segment.dy();
		// If segment is parallel to one of axes, then we need a separate case.
		if (dy == 0) {
			// Parallel to x axis
			if (Math.abs(segment.start.y - rectangle.y) < Math.abs(segment.start.y - rectangle.getMaxY())) {
				return CardinalDirection.N;
			} else {
				return CardinalDirection.S;
			}
		}
		double dx = segment.dx();
		if (dx == 0) {
			// Parallel to y axis
			if (Math.abs(segment.start.x - rectangle.x) < Math.abs(segment.start.x - rectangle.getMaxX())) {
				return CardinalDirection.W;
			} else {
				return CardinalDirection.E;
			}
		}
		Point2D closerEnd = getCloserEnd(segment, rectangle);
		if (pointIsFacingRectangleSide(closerEnd, rectangle)) {
			return getCardinalDirectionToPoint(closerEnd, rectangle);
		}
		Point2D fartherEnd = closerEnd == segment.start ? segment.end : segment.start;
		if (pointIsFacingRectangleSide(fartherEnd, rectangle)) {
			return getCardinalDirectionToPoint(fartherEnd, rectangle);
		}
		// Find k for y=k*x+b
		double k = Math.abs(dy / segment.dx());
		// Find b for y=k*x+b
		double b = segment.start.y - k * segment.start.x;
		if (k > 1) {
			// Near-vertical segment case
			// Pick any y on vertical sides of a rectangle
			double recY = rectangle.getCenterY();
			// Find x where segment intersects line y=recY
			double segmentX = (recY - b) / k;
			// Find which side is closer
			if (Math.abs(segmentX - rectangle.x) < Math.abs(segmentX - rectangle.getMaxX())) {
				return CardinalDirection.W;
			} else {
				return CardinalDirection.E;
			}
		} else {
			// Near-horizontal segment case
			double recX = rectangle.getCenterX();
			// Find y on segment's line at recX
			double segmentY = k * recX + b;
			if (Math.abs(segmentY - rectangle.y) < Math.abs(segmentY - rectangle.getMaxY())) {
				return CardinalDirection.N;
			} else {
				return CardinalDirection.S;
			}
		}
	}

	private static CardinalDirection getCardinalDirectionToPoint(Point2D point, Rectangle rectangle) {
		if (point.x < rectangle.x) {
			return CardinalDirection.W;
		}
		if (point.x > rectangle.getMaxX()) {
			return CardinalDirection.E;
		}
		if (point.y < rectangle.y) {
			return CardinalDirection.N;
		}
		assert point.y > rectangle.getMaxY();
		return CardinalDirection.S;
	}

	/**
	 * Checks if a point is inside the area
	 * bounded by an infinite cross of axis-parallel lines
	 * coming from an axis-parallel rectangle's sides,
	 * excluding the rectangle itself.
	 * <p>
	 * This method returns true for the green point, false for cyan points:
	 * <p>
	 * The area is in red, the rectangle defining area is in blue. This method returns true for the green point,
	 * and false for the black points:
	 * <p>
	 * <img src="http://tendiwa.org/doc-illustrations/points-in-front-of-axis-parallel-rectangle-sides.png" />
	 *
	 * @param point
	 * 	A point to check.
	 * @param rectangle
	 * 	A rectangle that defines area.
	 * @return
	 */
	private static boolean pointIsFacingRectangleSide(
		Point2D point,
		Rectangle rectangle
	) {
		if (rectangle.containsDoubleStrict(point.x, point.y)) {
			return false;
		}
		return point.y >= rectangle.y && point.y <= rectangle.getMaxY()
			|| point.x >= rectangle.x && point.x <= rectangle.getMaxY();
	}

	/**
	 * Returns the end of a {@code segment} that is closer to a {@code rectangle}'s center in Chebyshov metric.
	 * <p>
	 * It might seem that in in cases where this method is used a proximity to rectangle's <i>border</i> would be a
	 * better criteria, but actually Chebyshov distance is enough here (and most importantly, it computes quicker).
	 *
	 * @param segment
	 * 	A segment to find closer end of.
	 * @param rectangle
	 * 	A rectangle for which we compute distance to segments' ends.
	 * @return
	 */
	private static Point2D getCloserEnd(Segment2D segment, Rectangle rectangle) {
		Point2D rectangleCenter = rectangle.getCenterPoint();
		if (segment.start.chebyshovDistanceTo(rectangleCenter) < segment.end.chebyshovDistanceTo(rectangleCenter)) {
			return segment.start;
		} else {
			return segment.end;
		}
	}

	/**
	 * @param lotsTouchingStreets
	 * 	Which streets are near which building lots.
	 * @return
	 */
	public static FairLotFacadeAndStreetAssigner create(LotsTouchingStreets lotsTouchingStreets) {
		return new FairLotFacadeAndStreetAssigner(lotsTouchingStreets);
	}

	@Override
	public CardinalDirection getFacadeDirection(RectangleWithNeighbors lot) {
		return null;
	}

	@Override
	public List<Point2D> getStreet(RectangleWithNeighbors buildingPlace) {
		return null;
	}
}
