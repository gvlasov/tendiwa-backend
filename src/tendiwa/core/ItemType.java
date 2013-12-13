package tendiwa.core;

public interface ItemType extends TypePlaceableInCell, Resourceable {

public Material getMaterial();

public double getWeight();

public double getVolume();

public boolean isStackable();

}
