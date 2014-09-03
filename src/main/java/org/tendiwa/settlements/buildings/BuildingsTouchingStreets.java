package org.tendiwa.settlements.buildings;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;

import java.util.*;

/**
 * Finds out which building places are one which streets. A building place can be on more than one street as a result
 * of this algorithm, and deciding the address of a lot is not up to this class.
 */
public final class BuildingsTouchingStreets {

	private final Multimap<RectangleWithNeighbors, Segment2D> lotsToStreetSegments = HashMultimap.create();
	// TODO: Try to use IdentityHashMap here (makes ordering non-deterministic?)
	private final Map<Segment2D, List<Point2D>> segmentsToStreets = new LinkedHashMap<>();
	private final Map<List<Point2D>, Set<RectangleWithNeighbors>> streetsToLots = new LinkedHashMap<>();
	private final Map<RectangleWithNeighbors, Set<List<Point2D>>> lotsToStreets = new LinkedHashMap<>();
	private final double streetsWidth;

	public BuildingsTouchingStreets(
		Set<ImmutableList<Point2D>> streets,
		double streetsWidth
	) {
		this.streetsWidth = streetsWidth;
		for (List<Point2D> street : streets) {
			int lastButOne = street.size() - 1;
			for (int i = 0; i < lastButOne; i++) {
				segmentsToStreets.put(
					new Segment2D(
						street.get(i),
						street.get(i + 1)
					),
					street
				);
			}
			streetsToLots.put(street, new LinkedHashSet<>());
		}
	}

	public Collection<RectangleWithNeighbors> getLots() {
		return lotsToStreets.keySet();
	}

	/**
	 * Assigns a street to a lot.
	 *
	 * @param lot
	 * 	A lot to assign a street to.
	 */
	public void addLot(RectangleWithNeighbors lot) {
		int maxX = lot.rectangle.getMaxX();
		int maxY = lot.rectangle.getMaxY();
		Rectangle extendedLot = lot.rectangle.stretch((int) Math.ceil(streetsWidth));
		segmentsToStreets.keySet().stream()
			.filter(segment -> {
				double roadMinX = Math.min(segment.start.x, segment.end.x) - streetsWidth;
				double roadMaxX = Math.max(segment.start.x, segment.end.x) + streetsWidth;
				double roadMinY = Math.min(segment.start.y, segment.end.y) - streetsWidth;
				double roadMaxY = Math.max(segment.start.y, segment.end.y) + streetsWidth;
				// http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
				return lot.rectangle.x < roadMaxX && maxX > roadMinX && lot.rectangle.y < roadMaxY && maxY > roadMinY;
			})
			.filter(segment -> Recs.rectangleIntersectsSegment(extendedLot, segment))
			.forEach(segment -> lotsToStreetSegments.put(lot, segment));
		Set<List<Point2D>> streets1 = collectStreetsForLot(lot);
		for (List<Point2D> street : streets1) {
			streetsToLots.get(street).add(lot);
		}
		lotsToStreets.put(lot, streets1);
	}

	private Set<List<Point2D>> collectStreetsForLot(RectangleWithNeighbors buildingPlace) {
		Set<List<Point2D>> answer = new LinkedHashSet<>();
		for (Segment2D segment : lotsToStreetSegments.get(buildingPlace)) {
			List<Point2D> street = segmentsToStreets.get(segment);
			answer.add(street);
		}
		return answer;
	}

	Set<RectangleWithNeighbors> getLotsOnStreet(Street street) {
		return Collections.unmodifiableSet(streetsToLots.get(street.getPoints()));
	}

	public Set<List<Point2D>> getStreetsForLot(RectangleWithNeighbors where) {
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
		return lotsToStreetSegments.get(lot);
	}
}
