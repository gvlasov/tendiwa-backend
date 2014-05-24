package org.tendiwa.geometry.extensions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.geometry.*;

import java.util.*;

/**
 * Builds a graph where vertices are some {@link org.tendiwa.geometry.FiniteCellSet}s, and edges are paths from a Cell
 * in one cell set to a Cell in another one.
 * <p>
 * Shapes are connected with edges in such a way that:
 * <ol>
 * <li>exit cells on shape border are connected to the closest exit cells on another shape's border</li>
 * <li>two cities are connected with at most one road.</li>
 * </ol>
 */
public class IntershapeNetwork {
	private final CellSet walkableCells;
	private final UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> graph;

	public static StepShapeExits withShapeExits(Collection<FiniteCellSet> shapeExitSets) {
		return new StepShapeExits(shapeExitSets);
	}

	public static class StepShapeExits {
		private Collection<FiniteCellSet> shapeExitSets;

		public StepShapeExits(Collection<FiniteCellSet> shapeExitSets) {
			this.shapeExitSets = shapeExitSets;
		}

		public IntershapeNetwork withWalkableCells(CellSet walkableCells) {
			return new IntershapeNetwork(walkableCells, shapeExitSets);
		}
	}

	IntershapeNetwork(CellSet walkableCells, Collection<FiniteCellSet> shapeExitSets) {
		this.walkableCells = walkableCells;
		this.graph = buildGraph(shapeExitSets);
	}

	private UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> buildGraph(Collection<FiniteCellSet> exitsSets) {
		mapCellsToTheirSets(exitsSets);
		// Maps each cell to its closest possible neighbor in another exit set.
		Collection<CellSegment> connections = findShortestConnections(exitsSets);
		return buildConnectionsGraph(connections, exitsSets);
	}

	private UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> buildConnectionsGraph(
		Collection<CellSegment> connections,
		Collection<FiniteCellSet> exitSets
	) {
		Map<Cell, FiniteCellSet> cellToCellSet = mapCellsToTheirSets(exitSets);
		Table<FiniteCellSet, FiniteCellSet, CellSegment> shortestConnections = HashBasedTable.create();
		for (CellSegment connection : connections) {
			FiniteCellSet sourceExitSet = cellToCellSet.get(connection.start);
			FiniteCellSet targetExitSet = cellToCellSet.get(connection.end);
			if (
				!shortestConnections.contains(sourceExitSet, targetExitSet)
					|| shortestConnections.get(sourceExitSet, targetExitSet).length() > connection.length()
				) {
				shortestConnections.put(sourceExitSet, targetExitSet, connection);
			}
		}
		UndirectedGraph<FiniteCellSet, CellSegment> graph = new SimpleGraph<>((a, b) -> {
			throw new RuntimeException("Edges should not be automatically created in this graph");
		});
		for (FiniteCellSet exitSet : exitSets) {
			graph.addVertex(exitSet);
		}
		for (CellSegment connection : shortestConnections.values()) {
			graph.addEdge(
				cellToCellSet.get(connection.start),
				cellToCellSet.get(connection.end),
				connection
			);
		}
		return new UnmodifiableUndirectedGraph<>(graph);
	}

	/**
	 * For each Cell in exitsSets, find the closest possible cell in some other exitSet.
	 *
	 * @param exitsSets
	 * 	All exit sets.
	 * @return All shortest connections from one shape to another.
	 */
	private Collection<CellSegment> findShortestConnections(Collection<FiniteCellSet> exitsSets) {
		Collection<CellSegment> connections = new ArrayList<>();
		Collection<Cell> unusedExitCells = new HashSet<>();
		for (FiniteCellSet exitsSet : exitsSets) {
			for (Cell cell : exitsSet) {
				unusedExitCells.add(cell);
			}
		}


		for (FiniteCellSet exitSet : exitsSets) {
			for (Cell exitCell : exitSet) {
				if (!unusedExitCells.contains(exitCell)) {
					continue;
				}
				for (Cell waveCell : Wave.from(exitCell).goingOver(walkableCells).in8Directions()) {
					if (unusedExitCells.contains(waveCell)) {
						if (!exitSet.contains(waveCell)) {
							// Connect only different exitSets
							connections.add(new CellSegment(exitCell, waveCell));
//							unusedExitCells.remove(exitCell);
							break;
						}
					}
				}
			}
		}
		return connections;
	}

	private Map<Cell, FiniteCellSet> mapCellsToTheirSets(Collection<FiniteCellSet> exitsSets) {
		Map<Cell, FiniteCellSet> cellToCellSet = new HashMap<>();
		for (FiniteCellSet exitsSet : exitsSets) {
			// Map each cell in exit sets to its exit set.
			for (Cell cell : exitsSet) {
				cellToCellSet.put(cell, exitsSet);
			}
		}

		return cellToCellSet;
	}

	public UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> getGraph() {
		return graph;
	}

	public static IntershapeNetworkBuilder builder() {
		return new IntershapeNetworkBuilder();
	}
}
