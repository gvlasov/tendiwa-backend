package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Finds which cycles are enclosed within which ones.
 */
final class EnclosedCycleDetector {
	private final Map<MinimalCycle<Point2D, Segment2D>, Geometry> geometries;
	private final GeometryFactory factory = new GeometryFactory();
	private final Collection<MinimalCycle<Point2D, Segment2D>> cycles;
	private Map<MinimalCycle<Point2D, Segment2D>, ImmutableSet<MinimalCycle<Point2D, Segment2D>>> enclosingToEnclosed;
	private Set<MinimalCycle<Point2D, Segment2D>> enclosed;

	public EnclosedCycleDetector(Collection<MinimalCycle<Point2D, Segment2D>> cycles) {
		this.cycles = cycles;
		geometries = new IdentityHashMap<>(cycles.size());
		cycles.forEach(c -> geometries.put(c, toGeometry(c)));
		detectEnclosingCycles();
	}

	private Geometry toGeometry(MinimalCycle<Point2D, Segment2D> cycle) {
		List<Point2D> points = cycle.vertexList();
		Coordinate[] coordinates = points
			.stream()
			.map(point -> new Coordinate(point.x(), point.y()))
			.collect(Collectors.toList())
			.toArray(new Coordinate[points.size() + 1]);
		coordinates[coordinates.length - 1] = coordinates[0];
		return factory.createPolygon(factory.createLinearRing(coordinates), null);
	}

	private MinimalCycle<Point2D, Segment2D> findEnclosingCycle(MinimalCycle<Point2D, Segment2D> cycle) {
		Point2D anyPointOfCycle = cycle.asVertices().iterator().next();
		Coordinate coordinate = new Coordinate(
			anyPointOfCycle.x(),
			anyPointOfCycle.y()
		);
		for (MinimalCycle<Point2D, Segment2D> c : geometries.keySet()) {
			if (factory.createPoint(coordinate).within(geometries.get(c))) {
				return c;
			}
		}
		return null;
	}

	/**
	 * @return A map from enclosing cycles to enclosed cycles.
	 */
	private void detectEnclosingCycles() {
		enclosingToEnclosed = new LinkedHashMap<>(cycles.size());
		enclosed = new HashSet<>(cycles.size());
		Map<MinimalCycle<Point2D, Segment2D>, ImmutableSet.Builder<MinimalCycle<Point2D, Segment2D>>> builders =
			new LinkedHashMap<>();
		cycles.forEach(c -> builders.put(c, ImmutableSet.builder()));
		cycles.forEach(c -> {
			MinimalCycle<Point2D, Segment2D> enclosingCycle = findEnclosingCycle(c);
			if (enclosingCycle != null) {
				enclosed.add(c);
				builders
					.get(enclosingCycle)
					.add(c);
			}
		});
		builders.entrySet().forEach(e -> enclosingToEnclosed.put(e.getKey(), e.getValue().build()));
	}

	public boolean isEnclosed(MinimalCycle<Point2D, Segment2D> cycle) {
		return enclosed.contains(cycle);
	}

	public Set<MinimalCycle<Point2D, Segment2D>> cyclesEnclosedIn(MinimalCycle<Point2D, Segment2D> cycle) {
		return enclosingToEnclosed.get(cycle);
	}
}
