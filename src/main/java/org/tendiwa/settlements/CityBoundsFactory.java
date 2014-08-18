package org.tendiwa.settlements;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.Direction;
import org.tendiwa.core.Directions;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshevDistanceBufferBorder;
import org.tendiwa.pathfinding.dijkstra.PathTable;

/**
 * Creates graphs used as a base for a {@link PathGeometry}.
 */
public class CityBoundsFactory {
	private final CellSet water;

	public CityBoundsFactory(CellSet water) {
		this.water = water;
	}

	/**
	 * Culls the cells that will produce intersecting bounding roads.
	 *
	 * @param bufferBorder
	 * 	Coastal road's cells.
	 * @param start
	 * 	The point from which the City originated.
	 * @return A path table whose bounds end on bufferBorder's cells, but not past them.
	 */
	private PathTable cullIntersectingBoundingRoadsCells(
		CellSet bufferBorder,
		Cell start,
		Rectangle boundingRec,
		int radius
	) {
		return new PathTable(
			start.x,
			start.y,
			(x, y) -> boundingRec.contains(x, y) && !bufferBorder.contains(x, y),
			radius
		).computeFull();
	}

	private UndirectedGraph<Point2D, Segment2D> computeCityBoundingRoads(
		BoundedCellSet cityShape,
		Cell startCell,
		int radius
	) {
		CachedCellSet bufferBorder = new CachedCellSet(
			new ChebyshevDistanceBufferBorder(
				1,
				(x, y) -> !cityShape.getBounds().contains(x, y) || !cityShape.contains(x, y)
			),
			cityShape.getBounds()
		).computeAll();
		PathTable culledTable = cullIntersectingBoundingRoadsCells(
			bufferBorder,
			startCell,
			cityShape.getBounds(),
			radius
		);
		CachedCellSet culledBufferBorder = new CachedCellSet(
			new ChebyshevDistanceBufferBorder(
				1,
				culledTable::isCellComputed
			),
			culledTable.getBounds()
		).computeAll();
		return bufferBorderToGraph(culledBufferBorder);
	}

	private UndirectedGraph<Point2D, Segment2D> bufferBorderToGraph(CachedCellSet bufferBorder) {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(org.tendiwa.geometry.extensions.PlanarGraphs.getEdgeFactory());
		BiMap<Cell, Point2D> cell2PointMap = HashBiMap.create();
		ImmutableSet<Cell> borderCells = bufferBorder.toSet();
		for (Cell cell : borderCells) {
			cell2PointMap.put(cell, new Point2D(cell.x, cell.y));
		}
		for (Cell cell : borderCells) {
			graph.addVertex(cell2PointMap.get(cell));
		}
		for (Cell cell : borderCells) {
			for (Direction dir : Directions.CARDINAL_DIRECTIONS) {
				Point2D neighbour = cell2PointMap.get(cell.moveToSide(dir));
				if (graph.containsVertex(neighbour)) {
					graph.addEdge(cell2PointMap.get(cell), neighbour);
				}
			}
		}
		new EdgeReducer(graph, cell2PointMap).reduceEdges();
		return graph;
	}

	/**
	 * Creates a new graph that can be used as a base for {@link PathGeometry}.
	 *
	 * @param startCell
	 * 	A cell from which a City originates. Roughly denotes its final position.
	 * @param maxCityRadius
	 * 	A maximum radius of a Rectangle containing resulting City.
	 * @return A new graph that can be used as a base for {@link PathGeometry}.
	 * @see CityGeometryBuilder
	 */
	public UndirectedGraph<Point2D, Segment2D> create(
		BoundedCellSet cityShape,
		Cell startCell,
		int maxCityRadius
	) {
		if (water.contains(startCell.x, startCell.y)) {
			throw new IllegalArgumentException(
				"Start cell " + startCell + " must be a ground cell, not water cell"
			);
		}
		return computeCityBoundingRoads(cityShape, startCell, maxCityRadius);
	}
}
