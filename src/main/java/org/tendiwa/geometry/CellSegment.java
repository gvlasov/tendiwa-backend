package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

import java.util.Iterator;
import java.util.List;

public interface CellSegment extends Iterable<Cell> {
	@Override
	Iterator<Cell> iterator();

	double length();

	List<Cell> asList();
}
