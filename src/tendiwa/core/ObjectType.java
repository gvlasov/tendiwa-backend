package tendiwa.core;

/**
 * Describes a certain type of inanimate objects that are large enough to be treated as {@link Item}s: trees, furniture,
 * wall segments
 */
public interface ObjectType extends TypePlaceableInCell, Resourceable {

public Passability getPassability();


public ObjectClass getObjectClass();

}
