package org.tendiwa.geometry;

public interface OrientedPolygon extends Polygon {
	boolean isClockwise(Segment2D edge);
}
