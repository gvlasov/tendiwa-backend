package org.tendiwa.settlements.buildings;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;

import java.util.*;

final class StreetAssigner {

	private final Multimap<Rectangle, Segment2D> lotsToStreetSegments = HashMultimap.create();
	private final Map<Segment2D, Street> segmentsToStreets = new LinkedHashMap<>();
	private final Map<Street, Set<Rectangle>> streetsToPlaces = new LinkedHashMap<>();
	private final Map<Rectangle, Set<Street>> placesToStreets = new LinkedHashMap<>();

	StreetAssigner(Set<Rectangle> buildingPlaces, Set<Street> streets, double streetsWidth) {
		for (Street street : streets) {
			int size = street.points.size() - 1;
			for (int i = 0; i < size; i++) {
				segmentsToStreets.put(
					new Segment2D(
						street.points.get(i),
						street.points.get(i + 1)
					),
					street
				);
			}
			streetsToPlaces.put(street, new LinkedHashSet<>());
		}
		for (Rectangle lot : buildingPlaces) {
			int maxX = lot.getMaxX();
			int maxY = lot.getMaxY();
			segmentsToStreets.keySet().stream()
				.filter(segment -> {
					double roadMinX = Math.min(segment.start.x, segment.end.x) - streetsWidth;
					double roadMaxX = Math.max(segment.start.x, segment.end.x) + streetsWidth;
					double roadMinY = Math.min(segment.start.y, segment.end.y) - streetsWidth;
					double roadMaxY = Math.max(segment.start.y, segment.end.y) + streetsWidth;
					// http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
					return lot.x < roadMaxX && maxX > roadMinX && lot.y < roadMaxY && maxY > roadMinY;
				})
				.filter(segment -> Recs.rectangleIntersectsSegment(lot, segment))
				.forEach(segment -> lotsToStreetSegments.put(lot, segment));
			Set<Street> streets1 = computeStreetsForBuildingPlace(lot);
			for (Street street : streets1) {
				streetsToPlaces.get(street).add(lot);
			}
			placesToStreets.put(lot, streets1);
		}
	}

	private Set<Street> computeStreetsForBuildingPlace(Rectangle buildingPlace) {
		Set<Street> answer = new LinkedHashSet<>();
		for (Segment2D segment : lotsToStreetSegments.get(buildingPlace)) {
			Street street = segmentsToStreets.get(segment);
			answer.add(street);
		}
		return answer;
	}

	Set<Rectangle> getPlacesOnStreet(Street street) {
		return Collections.unmodifiableSet(streetsToPlaces.get(street));
	}

	Set<Street> getStreetsForBuildingPlace(Rectangle where) {
		return placesToStreets.get(where);
	}
}
