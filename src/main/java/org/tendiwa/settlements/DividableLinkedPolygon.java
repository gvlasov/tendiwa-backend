package org.tendiwa.settlements;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Polygon;

public interface DividableLinkedPolygon extends Polygon {
	/**
	 * [Kelly 2008 Figure 54]
	 * <p>
	 * Consecutively splits a block into lesser blocks until blocks are small enough.
	 *
	 * @param lotWidth
	 * 	Dimension of a lot along the road.
	 * @param lotDepth
	 * 	Another dimension of a lot, perpendicular to {@code lotWidth}.
	 * @param lotDeviance
	 * 	Coefficient for possible lot width and depth deviance.
	 * 	Actual lot widths and depths (sizes) may be [size*(1-deviance/2); size*(1+deviance/2)]. So deviance of 0
	 * 	means that lots will try to have exactly the width and height specified in corresponding arguments,
	 * 	and any other deviance value means lots can be up to 1.5 times larger or up to 0 size,
	 * 	but never exactly 1.5 times larger or 0.
	 * @return A set of lots subdivided from this one.
	 */
	ImmutableSet<DividableLinkedPolygon> subdivideLots(double lotWidth, double lotDepth, double lotDeviance);
}
