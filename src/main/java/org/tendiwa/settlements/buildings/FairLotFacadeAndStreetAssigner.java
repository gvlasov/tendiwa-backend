package org.tendiwa.settlements.buildings;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Multimap;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableChain2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.LotStreetAssigner;
import org.tendiwa.geometry.Chain2D;

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
	private final PolylineProximity lotsAndStreets;
	private final Map<RectangleWithNeighbors, CardinalDirection> facades;
	private final Map<RectangleWithNeighbors, Chain2D> lotToStreet;
	private final Multimap<Chain2D, RectangleWithNeighbors> streetToLots;
	private final IdentityHashMap<Chain2D, Double> streetLengths;
	private final IdentityHashMap<Chain2D, Double> streetThirstsForLot;
	private final ImmutableCollection<Chain2D> streets;
	private final ImmutableCollection<RectangleWithNeighbors> lots;

	private FairLotFacadeAndStreetAssigner(PolylineProximity polylineProximity) {
		this.lotsAndStreets = polylineProximity;
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
		lotToStreet = new LinkedHashMap<>(lotsSize);
		TestCanvas.canvas.drawAll(
			streets,
			street -> new DrawableChain2D.Thin(street, Color.red)
		);
		assignSingleStreetBuildings();
		fairlyAssignTheRestOfBuildings();
	}

	private static double computeStreetLength(Chain2D street) {
		return street.asSegmentStream()
			.mapToDouble(Segment2D::length)
			.sum();
	}

	/**
	 * @param polylineProximity
	 * 	Which streets are near which building lots.
	 * @return
	 */
	public static FairLotFacadeAndStreetAssigner create(PolylineProximity polylineProximity) {
		return new FairLotFacadeAndStreetAssigner(polylineProximity);
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
			if (lot.rectangle.intersects(thirstiest.segment)) {
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
		Chain2D thirstiestStreet = null;
		Segment2D thirstiestSegment = null;
		for (Segment2D segment : lotsAndStreets.getSegmentsForLot(lot)) {
			Chain2D street = lotsAndStreets.getStreetForSegment(segment);
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

		private final Chain2D street;
		private final Segment2D segment;

		private Thirstiest(Chain2D street, Segment2D segment) {
			this.street = street;
			this.segment = segment;
		}
	}

	private void initStreetsLengthsAndThirsts() {
		for (Chain2D street : streets) {
			streetLengths.put(street, computeStreetLength(street));
			// Initially all streets have effectively infinite thirst (because they h
			if (!streetThirstsForLot.containsKey(street)) {
				streetThirstsForLot.put(street, Double.MAX_VALUE);
			}
		}
	}

	private void updateThirst(Chain2D street) {
		streetThirstsForLot.put(street, streetLengths.get(street) / streetToLots.get(street).size());
	}

	/**
	 * Assign facade directions and streets to those buildings that have only a single street near them according to
	 * {@link #lotsAndStreets}.
	 */
	private void assignSingleStreetBuildings() {
		for (RectangleWithNeighbors lot : lotsAndStreets.getLots()) {
			Set<Chain2D> streetsForLot = lotsAndStreets.getStreetsForLot(lot);
			if (streetsForLot.size() == 1) {
				Collection<Segment2D> segmentsForLot = lotsAndStreets.getSegmentsForLot(lot);
				for (Segment2D segment : segmentsForLot) {
					facades.put(lot, RectangleToSegmentDirection.getDirectionToSegment(segment, lot.rectangle));
					Chain2D street = streetsForLot.iterator().next();
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
			// TODO: This is wrong
			return CardinalDirection.E;
		}
	}

	@Override
	public Chain2D getStreet(RectangleWithNeighbors lot) {
		assert lotToStreet.containsKey(lot);
		return lotToStreet.get(lot);
	}
}
