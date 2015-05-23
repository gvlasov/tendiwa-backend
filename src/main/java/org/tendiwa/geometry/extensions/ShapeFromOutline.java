package org.tendiwa.geometry.extensions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.jgrapht.alg.ConnectivityInspector;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.polygonRasterization.MutableRasterizedPolygon;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.GraphChainTraversal;
import org.tendiwa.graphs.MinimalCycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ShapeFromOutline {
	private ShapeFromOutline() {

	}

	public static BoundedCellSet from(Graph2D outline) {
		List<Set<Point2D>> components = new ConnectivityInspector<>(outline.toJgrapht()).connectedSets();
		List<Polygon> polygons = new ArrayList<>(components.size());
		for (Set<Point2D> component : components) {
			List<Point2D> polygon = new ArrayList<>(component.size());
			GraphChainTraversal
				.traverse(outline.toJgrapht())
				.startingWith(component.iterator().next())
				.stream()
				.map(GraphChainTraversal.NeighborsTriplet::current)
				.forEach(polygon::add);
			polygons.add(new BasicPolygon(polygon));
		}

		CellSet shape = polygons.stream()
			.map(polygon -> (CellSet) new MutableRasterizedPolygon(polygon))
			.reduce((a, b) -> a.xor(b))
			.get();
		return new CachedCellSet(
			shape,
			outline.integerBounds()
		);

//		FiniteCellSet edgeCells = requireNonNull(outline)
//			.edgeSet()
//			.segmentStream()
//			.map(Segment2D.toCellList())
//			.flatMap(a -> a.segmentStream())
//			.distinct()
//			.collect(CellSet.toCellSet());
//		Rectangle graphBounds = Recs.boundsOfCells(edgeCells);
//		return new CachedCellSet(
//			new MinimumCycleBasis<>(outline, Point2DVertexPositionAdapter.get())
//				.minimalCyclesSet()
//				.segmentStream()
//				.map(ShapeFromOutline::polygonCells)
//				.reduce(edgeCells, (a, b) -> a.or(b)),
//			graphBounds
//		);


	}


	/**
	 * @param cycle
	 * 	A minimal cycle.
	 * @return A cell that is within a cycle.
	 */
	private static CellSet polygonCells(MinimalCycle cycle) {
		GeometryFactory gf = new GeometryFactory();
		Coordinate[] coordinates = cycle
			.stream()
			.map(p -> new Coordinate(p.x(), p.y()))
			.collect(Collectors.toList())
			.toArray(new Coordinate[cycle.size() + 1]);
		coordinates[coordinates.length - 1] = coordinates[0];

		com.vividsolutions.jts.geom.Polygon polygon = gf.createPolygon(gf.createLinearRing(coordinates), null);
		return (x, y) -> polygon.contains(gf.createPoint(new Coordinate(x, y)));
	}
}
