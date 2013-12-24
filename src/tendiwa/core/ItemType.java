package tendiwa.core;

import org.tendiwa.lexeme.Localizable;

public interface ItemType extends TypePlaceableInCell, Resourceable, Localizable {

public Material getMaterial();

public double getWeight();

public double getVolume();

public boolean isStackable();

}
