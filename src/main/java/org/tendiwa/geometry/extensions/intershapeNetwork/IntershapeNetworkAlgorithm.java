package org.tendiwa.geometry.extensions.intershapeNetwork;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.*;

import java.util.*;

final class IntershapeNetworkAlgorithm {

	private final CellSet walkableCells;
	private final UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> graph;

	IntershapeNetworkAlgorithm(CellSet walkableCells, Collection<FiniteCellSet> shapeExitSets) {
		this.walkableCells = walkableCells;
		this.graph = buildGraph(shapeExitSets);
	}

	private UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> buildGraph(Collection<FiniteCellSet> exitsSets) {
		mapCellsToTheirSets(exitsSets);
		Collection<CellSegment> connections = findShortestConnections(exitsSets);
		return buildConnectionsGraph(connections, exitsSets);
	}

	private UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> buildConnectionsGraph(
		Collection<CellSegment> connections,
		Collection<FiniteCellSet> exitSets
	) {
		UndirectedGraph<FiniteCellSet, CellSegment> graph = new SimpleGraph<>((a, b) -> {
			throw new RuntimeException("Edges should not be automatically created in this graph");
		});
		for (FiniteCellSet exitSet : exitSets) {
			graph.addVertex(exitSet);
		}

		Map<Cell, FiniteCellSet> cellsToSets = mapCellsToTheirSets(exitSets);
		Table<FiniteCellSet, FiniteCellSet, CellSegment> shortestConnections = HashBasedTable.create();
		for (CellSegment connection : connections) {
			FiniteCellSet sourceExitSet = cellsToSets.get(connection.start());
			FiniteCellSet targetExitSet = cellsToSets.get(connection.end());
			if (
				!shortestConnections.contains(sourceExitSet, targetExitSet)
					|| shortestConnections.get(sourceExitSet, targetExitSet).length() > connection.length()
				) {
				// Replaces previously found shortest path
				shortestConnections.put(sourceExitSet, targetExitSet, connection);
			}
		}
		for (CellSegment connection : shortestConnections.values()) {
			graph.addEdge(
				cellsToSets.get(connection.start()),
				cellsToSets.get(connection.end()),
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
		Collection<Cell> exitCells = new HashSet<>();
		for (FiniteCellSet exitsSet : exitsSets) {
			for (Cell cell : exitsSet) {
				exitCells.add(cell);
			}
		}

		for (FiniteCellSet exitSet : exitsSets) {
			for (Cell exitCell : exitSet) {
				if (!exitCells.contains(exitCell)) {
					continue;
				}
				for (Cell waveCell : Wave.from(exitCell).goingOver(walkableCells).in8Directions()) {
					if (exitCells.contains(waveCell)) {
						if (!exitSet.contains(waveCell)) {
							// Connect only different exitSets
							connections.add(new BasicCellSegment(exitCell, waveCell));
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
}
