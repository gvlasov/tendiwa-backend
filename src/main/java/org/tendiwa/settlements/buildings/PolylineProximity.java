package org.tendiwa.settlements.buildings;

import com.google.common.collect.*;
import org.tendiwa.collections.StreamIterable;
import org.tendiwa.geometry.*;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;
import org.tendiwa.geometry.Chain2D;

import java.util.*;

/**
 * Finds out which lots are on which {@link Street}. If a lot belongs to a {@link Street}, it is said that the lot has
 * a facade on that street, hence the name of this class. A lot can have multiple facades, i.e. it can belong to
 * multiple streets.
 * <p>
 * A building place can be on more than one street as a result of this algorithm, and deciding the unique street a
 * lot belongs to in not up to this class.
 */
public final class PolylineProximity {

	private final Multimap<RectangleWithNeighbors, Segment2D> lotsToStreetSegments = LinkedHashMultimap.create();
	// TODO: Try to use IdentityHashMap here (makes ordering non-deterministic?)
	private final Map<Segment2D, Chain2D> segmentsToStreets = new LinkedHashMap<>();
	private final Map<Chain2D, Set<RectangleWithNeighbors>> streetsToLots = new LinkedHashMap<>();
	private final Map<RectangleWithNeighbors, Set<Chain2D>> lotsToStreets = new LinkedHashMap<>();
	private final double streetsWidth;
	private final Set<Segment2D> allStreetSegments;

	public PolylineProximity(
		Set<Chain2D> streets,
		Iterable<RectangleWithNeighbors> lots,
		double streetsWidth
	) {
		Objects.requireNonNull(streets);
		if (streetsWidth <= 0) {
			throw new IllegalArgumentException("street width must be > 0");
		}
		this.streetsWidth = streetsWidth;
		allStreetSegments = segmentsToStreets.keySet();

		for (Chain2D street : streets) {
			for (Segment2D segment : new StreamIterable<>(street.asSegmentStream())) {
				segmentsToStreets.put(segment, street);
			}
			streetsToLots.put(street, new LinkedHashSet<>());
		}
		lots.forEach(this::addLot);
	}

	public ImmutableCollection<RectangleWithNeighbors> getLots() {
		return ImmutableSet.copyOf(lotsToStreets.keySet());
	}

	/**
	 * Assigns streets to a lot.
	 *
	 * @param lot
	 * 	A lot to assign streets to.
	 */
	private void addLot(RectangleWithNeighbors lot) {
		Objects.requireNonNull(lot);
		lot.allRectangles()
			.forEach(rectangle -> findSegmentsForLot(lot, rectangle));
		Set<Chain2D> streetsForLot = collectStreetsForLot(lot);
		for (Chain2D street : streetsForLot) {
			streetsToLots.get(street).add(lot);
		}
		lotsToStreets.put(lot, streetsForLot);
	}

	/**
	 * Maps lots to their nearby street segments by putting lot->segment entries in {@link #lotsToStreetSegments}
	 *
	 * @param lot
	 * 	A lot to map to some segments.
	 * @param rectangle
	 */
	private void findSegmentsForLot(RectangleWithNeighbors lot, Rectangle rectangle) {
		BasicRectangle2D extendedRec = Recs2D.stretch(rectangle, streetsWidth + Vectors2D.EPSILON);
		Rectangle2DWithMaxCoordinates extendedRecWithMaxCoordinates =
			new Rectangle2DWithMaxCoordinates(extendedRec);
		allStreetSegments.stream()
			.filter(extendedRecWithMaxCoordinates::intersectsSegmentHull)
			.filter(extendedRec::intersectsSegment)
			.forEach(segment -> lotsToStreetSegments.put(lot, segment));
	}

	private Set<Chain2D> collectStreetsForLot(RectangleWithNeighbors lot) {
		Set<Chain2D> answer = new LinkedHashSet<>();
		for (Segment2D segment : lotsToStreetSegments.get(lot)) {
			Chain2D street = segmentsToStreets.get(segment);
			answer.add(street);
		}
		return answer;
	}

	/**
	 * Just a {@link org.tendiwa.geometry.BasicRectangle2D} with precomputed
	 * {@link org.tendiwa.geometry.BasicRectangle2D#getMaxX()}
	 * and {@link org.tendiwa.geometry.BasicRectangle2D#getMaxX()}.
	 */
	private class Rectangle2DWithMaxCoordinates {
		private final double maxX;
		private final double maxY;
		private final BasicRectangle2D rectangle;

		Rectangle2DWithMaxCoordinates(BasicRectangle2D rectangle) {
			this.rectangle = rectangle;
			maxX = rectangle.getMaxX();
			maxY = rectangle.getMaxY();
		}

		/**
		 * Does a weaker and simpler check than
		 * {@link org.tendiwa.geometry.BasicRectangle2D#intersectsSegment(org.tendiwa.geometry.Segment2D)}.
		 * <p>
		 * Checks intersection of a rectangle with segment's hull, which is much cheaper than checking intersection
		 * with the segment itself.
		 */
		private boolean intersectsSegmentHull(Segment2D segment) {
			double segmentMinX = Math.min(segment.start.x, segment.end.x) - streetsWidth;
			double segmentMaxX = Math.max(segment.start.x, segment.end.x) + streetsWidth;
			double segmentMinY = Math.min(segment.start.y, segment.end.y) - streetsWidth;
			double segmentMaxY = Math.max(segment.start.y, segment.end.y) + streetsWidth;
			// http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
			return rectangle.x < segmentMaxX && maxX > segmentMinX && rectangle.y < segmentMaxY && maxY > segmentMinY;
		}
	}

	Set<RectangleWithNeighbors> getLotsOnStreet(Street street) {
		Objects.requireNonNull(street);
		return Collections.unmodifiableSet(streetsToLots.get(street.chain()));
	}

	public Set<Chain2D> getStreetsForLot(RectangleWithNeighbors where) {
		Objects.requireNonNull(where);
		return lotsToStreets.get(where);
	}

	/**
	 * Checks if there are any streets assigned to a building place.
	 *
	 * @param buildingPlace
	 * 	A building place to check for.
	 * @return True if there are any streets to which this building place is assigned, false otherwise.
	 */
	public boolean hasStreets(RectangleWithNeighbors buildingPlace) {
		Objects.requireNonNull(buildingPlace);
		return !lotsToStreets.get(buildingPlace).isEmpty();
	}

	/**
	 * Returns all street segments that are near a lot.
	 *
	 * @param lot
	 * 	A lot.
	 * @return All street segments that are near a lot.
	 */
	public Collection<Segment2D> getSegmentsForLot(RectangleWithNeighbors lot) {
		Objects.requireNonNull(lot);
		return lotsToStreetSegments.get(lot);
	}

	/**
	 * Constructs new immutable collection containing all streets known to this {@link
	 * PolylineProximity}
	 *
	 * @return
	 */
	public ImmutableCollection<Chain2D> getStreets() {
		return ImmutableSet.copyOf(streetsToLots.keySet());
	}

	/**
	 * Returns a street that contains a segment.
	 *
	 * @param segment
	 * @return A street, or null if not street contains that segment.
	 */
	public Chain2D getStreetForSegment(Segment2D segment) {
		Objects.requireNonNull(segment);
		return segmentsToStreets.get(segment);
	}
}
