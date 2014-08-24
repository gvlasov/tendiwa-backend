package org.tendiwa.settlements.buildings;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.DrawingSegment;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.*;

/**
 * Finds out which building places are one which streets. A building place can be on more than one street,
 * and deciding the final address of a building place is not up to this class.
 */
public final class StreetAssigner {

	private final Multimap<Rectangle, Segment2D> lotsToStreetSegments = HashMultimap.create();
	// TODO: Try to use IdentityHashMap here
	private final Map<Segment2D, List<Point2D>> segmentsToStreets = new LinkedHashMap<>();
	private final Map<List<Point2D>, Set<Rectangle>> streetsToPlaces = new LinkedHashMap<>();
	private final Map<Rectangle, Set<List<Point2D>>> placesToStreets = new LinkedHashMap<>();
	private final double streetsWidth;

	public StreetAssigner(
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
			streetsToPlaces.put(street, new LinkedHashSet<>());
		}
	}

	/**
	 * Assigns a street to a building.
	 *
	 * @param lot
	 * 	A building to assign a street to.
	 */
	public void addBuilding(Rectangle lot) {
		int maxX = lot.getMaxX();
		int maxY = lot.getMaxY();
		Rectangle extendedLot = lot.stretch((int) Math.ceil(streetsWidth));
		segmentsToStreets.keySet().stream()
			.filter(segment -> {
				double roadMinX = Math.min(segment.start.x, segment.end.x) - streetsWidth;
				double roadMaxX = Math.max(segment.start.x, segment.end.x) + streetsWidth;
				double roadMinY = Math.min(segment.start.y, segment.end.y) - streetsWidth;
				double roadMaxY = Math.max(segment.start.y, segment.end.y) + streetsWidth;
				// http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
				return lot.x < roadMaxX && maxX > roadMinX && lot.y < roadMaxY && maxY > roadMinY;
			})
			.filter(segment -> Recs.rectangleIntersectsSegment(extendedLot, segment))
			.forEach(segment -> {
				lotsToStreetSegments.put(lot, segment);
			});
		Set<List<Point2D>> streets1 = collectStreetsForBuildingPlace(lot);
		for (List<Point2D> street : streets1) {
			streetsToPlaces.get(street).add(lot);
		}
		placesToStreets.put(lot, streets1);
	}

	private Set<List<Point2D>> collectStreetsForBuildingPlace(Rectangle buildingPlace) {
		Set<List<Point2D>> answer = new LinkedHashSet<>();
		for (Segment2D segment : lotsToStreetSegments.get(buildingPlace)) {
			List<Point2D> street = segmentsToStreets.get(segment);
			answer.add(street);
		}
		return answer;
	}

	Set<Rectangle> getPlacesOnStreet(Street street) {
		return Collections.unmodifiableSet(streetsToPlaces.get(street));
	}

	Set<List<Point2D>> getStreetsForBuildingPlace(Rectangle where) {
		return placesToStreets.get(where);
	}

	/**
	 * Checks if there are any streets assigned to a building place.
	 *
	 * @param buildingPlace
	 * 	A building place to check for.
	 * @return True if there are any streets to which this building place is assigned, false otherwise.
	 */
	public boolean hasStreets(Rectangle buildingPlace) {
		return !placesToStreets.get(buildingPlace).isEmpty();
	}
}
