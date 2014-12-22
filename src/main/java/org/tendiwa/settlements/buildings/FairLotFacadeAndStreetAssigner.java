package org.tendiwa.settlements.buildings;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Multimap;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPointTrail;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.LotStreetAssigner;

import java.awt.Color;
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
 * "unfortunate" streets could also belong to other streets).
 */
public final class FairLotFacadeAndStreetAssigner implements LotFacadeAssigner, LotStreetAssigner {
	private final StreetEntranceSystem lotsAndStreets;
	private final Map<RectangleWithNeighbors, CardinalDirection> facades;
	private final Map<RectangleWithNeighbors, List<Point2D>> lotToStreet;
	private final Multimap<List<Point2D>, RectangleWithNeighbors> streetToLots;
	private final IdentityHashMap<List<Point2D>, Double> streetLengths;
	private final IdentityHashMap<List<Point2D>, Double> streetThirstsForLot;
	private final ImmutableCollection<List<Point2D>> streets;
	private final ImmutableCollection<RectangleWithNeighbors> lots;

	private FairLotFacadeAndStreetAssigner(StreetEntranceSystem streetEntranceSystem) {
		this.lotsAndStreets = streetEntranceSystem;
		streets = lotsAndStreets.getStreets();
		lots = lotsAndStreets.getLots();
		int streetsSize = streets.size();
		int lotsSize = lots.size();
		streetLengths = new IdentityHashMap<>(streetsSize);
		streetThirstsForLot = new IdentityHashMap<>(streetsSize);
		// TODO: Keys are are Lists, that means their hashing is O(n), which is slow for long streets. IdentityMultimap
		// TODO: would be great here, but there's no such thing in Guava.
		streetToLots = HashMultimap.create(streetsSize, lotsSize / streetsSize);
		facades = new HashMap<>(lotsSize);
		lotToStreet = new LinkedHashMap<>(lotsSize); // LinkedHashMap used to preserve iteration order.
		for (List<Point2D> street : streets) {
			TestCanvas.canvas.draw(street, DrawingPointTrail.withColorThin(Color.red));
		}

		assignSingleStreetBuildings();
		fairlyAssignTheRestOfBuildings();
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
	 * @param streetEntranceSystem
	 * 	Which streets are near which building lots.
	 * @return
	 */
	public static FairLotFacadeAndStreetAssigner create(StreetEntranceSystem streetEntranceSystem) {
		return new FairLotFacadeAndStreetAssigner(streetEntranceSystem);
	}

	/**
	 * Assigns a street to lots. For each lot, algorithms selects a street that has the greatest <i>thirst for
	 * lot</i>. Thirst for lot is {@code length(street)/numberOfLotsAlreadyAssignedTo(street)} and may change with each
	 * iteration.
	 */
	private void fairlyAssignTheRestOfBuildings() {
		initStreetsLengthsAndThirsts();
		for (RectangleWithNeighbors lot : lots) {
			if (facades.containsKey(lot)) {
				continue;
			}
			if (!lotsAndStreets.hasStreets(lot)) {
				continue;
			}
			Thirstiest thirstiest = searchForThirstiestSegmentAndStreet(lot);
			lotToStreet.put(lot, thirstiest.street);
			if (Recs.rectangleIntersectsSegment(lot.rectangle, thirstiest.segment)) {
				// TODO: This should not happen!
				continue;
			}
			facades.put(
				lot,
				RectangleToSegmentDirection.getDirectionToSegment(thirstiest.segment, lot.rectangle)
			);
			streetToLots.put(thirstiest.street, lot);
			updateThirst(thirstiest.street);
		}
	}


	private Thirstiest searchForThirstiestSegmentAndStreet(RectangleWithNeighbors lot) {
		double maxThirst = -1;
		List<Point2D> thirstiestStreet = null;
		Segment2D thirstiestSegment = null;
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
		return new Thirstiest(thirstiestStreet, thirstiestSegment);
	}

	private final class Thirstiest {

		private final List<Point2D> street;
		private final Segment2D segment;

		private Thirstiest(List<Point2D> street, Segment2D segment) {
			this.street = street;
			this.segment = segment;
		}
	}

	private void initStreetsLengthsAndThirsts() {
		for (List<Point2D> street : streets) {
			streetLengths.put(street, computeStreetLength(street));
			// Initially all streets have effectively infinite thirst (because they h
			if (!streetThirstsForLot.containsKey(street)) {
				streetThirstsForLot.put(street, Double.MAX_VALUE);
			}
		}
	}

	private void updateThirst(List<Point2D> street) {
		streetThirstsForLot.put(street, streetLengths.get(street) / streetToLots.get(street).size());
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
					facades.put(lot, RectangleToSegmentDirection.getDirectionToSegment(segment, lot.rectangle));
					List<Point2D> street = streetsForLot.iterator().next();
					lotToStreet.put(lot, street);
					streetToLots.put(street, lot);
//					updateThirst(street);
				}
			}
		}
	}

	@Override
	public CardinalDirection getFacadeDirection(RectangleWithNeighbors lot) {
		if (facades.containsKey(lot)) {
			return facades.get(lot);
		} else {
			return CardinalDirection.E;
		}
	}

	@Override
	public List<Point2D> getStreet(RectangleWithNeighbors lot) {
		assert lotToStreet.containsKey(lot);
		return lotToStreet.get(lot);
	}
}
