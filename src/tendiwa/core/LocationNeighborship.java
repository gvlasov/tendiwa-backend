package tendiwa.core;

/**
 * <p>Contains a pair of {@link EnhancedRectangle} and {@link CardinalDirection}. Represents a neighbor of some other
 * place.</P> <p>This class is usually used in {@code for (E  : Iterable<E>)} loops when that "some place is
 * known".</p>
 */
public class LocationNeighborship {
private final LocationPlace place;
private final CardinalDirection direction;

LocationNeighborship(LocationPlace place, CardinalDirection direction) {
	this.place = place;
	this.direction = direction;
}

/**
 * Returns the neighbor place.
 *
 * @return Neighbor place
 */
public LocationPlace getPlace() {
	return place;
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
