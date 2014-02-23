package org.tendiwa.core;

import org.tendiwa.geometry.RectangleSidePiece;

/**
 * <p>Contains a pair of {@link org.tendiwa.geometry.Rectangle} and {@link CardinalDirection}. Represents a neighbor of some other
 * place.</P> <p>This class is usually used in {@code for (E  : Iterable<E>)} loops when that "some place is
 * known".</p>
 */
public class LocationNeighborship {
private final LocationPlace place;
private final CardinalDirection direction;
private final RectangleSidePiece commonSidePiece;

LocationNeighborship(LocationPlace place, CardinalDirection direction, RectangleSidePiece commonSidePiece) {
	this.place = place;
	this.direction = direction;
	this.commonSidePiece = commonSidePiece;
}

/**
 * Returns the neighbor place.
 *
 * @return Neighbor place
 */
public LocationPlace getPlace() {
	return place;
}
public int getLength() {
	return commonSidePiece.getSegment().length;
}

/**
 * Returns the side the neighbor place is from relatively to another place.
 *
 * @return Side from which neighbor place lies relatively to another place.
 */
public CardinalDirection getSide() {
	return direction;
}
}
