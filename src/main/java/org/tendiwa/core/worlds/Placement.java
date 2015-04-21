package org.tendiwa.core.worlds;

import org.tendiwa.core.TypePlaceableInCell;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.BasicCell;

interface Placement<T extends TypePlaceableInCell> extends BoundedCellSet {

	T contentAt(BasicCell coordinate);
}
