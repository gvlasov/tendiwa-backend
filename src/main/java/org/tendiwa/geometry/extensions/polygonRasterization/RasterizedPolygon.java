package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.geometry.ArrayBackedCellSet;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.MutableBoundedCellSet;
import org.tendiwa.geometry.Polygon;

public interface RasterizedPolygon extends BoundedCellSet, ArrayBackedCellSet {
	Polygon polygon();
}
