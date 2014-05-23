package org.tendiwa.graphs;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EnclosedCycleFilter  implements Predicate<MinimalCycle<Point2D, Segment2D>> {
    private final Collection<Geometry> geometries;
    private final GeometryFactory factory = new GeometryFactory();

    public EnclosedCycleFilter(Collection<MinimalCycle<Point2D, Segment2D>> cycles) {
        geometries = new ArrayList<>(cycles.size());
        for (MinimalCycle<Point2D, Segment2D> cycle : cycles) {
            geometries.add(toGeometry(cycle));
        }
    }

    private Geometry toGeometry(MinimalCycle<Point2D, Segment2D> cycle) {
        List<Point2D> points = cycle.vertexList();
        Coordinate[] coordinates = points
                .stream()
                .map(point -> new Coordinate(point.x, point.y))
                .collect(Collectors.toList())
                .toArray(new Coordinate[points.size() + 1]);
        coordinates[coordinates.length - 1] = coordinates[0];
        return factory.createPolygon(coordinates);
    }

    @Override
    public boolean test(MinimalCycle<Point2D, Segment2D> cycle) {
        Point2D anyPointOfCycle3 = cycle.iterator().next().start;
        Coordinate coordinate = new Coordinate(anyPointOfCycle3.x, anyPointOfCycle3.y);
        return !geometries.stream().anyMatch(g -> factory.createPoint(coordinate).within(g));
    }
}
