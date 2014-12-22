package org.tendiwa.geometry;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.List;

public class JTSUtils {
	public static final GeometryFactory DEFAULT_GEOMETRY_FACTORY = new GeometryFactory();

	// TODO: http://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order
	public static boolean isYDownCCW(List<Point2D> vertices) {

		int l = vertices.size();
		Coordinate[] coordinates = new Coordinate[l + 1];
		int i = 0;
		for (Point2D point : vertices) {
			coordinates[i++] = new Coordinate(point.x, point.y);
		}
		coordinates[l] = coordinates[0];
		// JTS's isCCW assumes y-up
		return !CGAlgorithms.isCCW(coordinates);
	}
}
