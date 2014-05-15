package org.tendiwa.geometry.extensions;

import com.google.common.collect.HashBiMap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds a graph where vertices are some {@link org.tendiwa.geometry.CellSet}s, and edges are paths from a Cell in one
 * CellSet to a Cell in another one.
 * <p>
 * Use {@link org.tendiwa.geometry.extensions.IntershapeNetworkBuilder} to build instances of this class.
 */
public class IntershapeNetwork {
    private final CellSet walkableCells;
    private UndirectedGraph<CellSet, CellSegment> graph;

    IntershapeNetwork(CellSet walkableCells, Collection<FiniteCellSet> shapeExitSets) {
        this.walkableCells = walkableCells;
        this.graph = buildGraph(shapeExitSets);
    }

    private UndirectedGraph<CellSet, CellSegment> buildGraph(Collection<FiniteCellSet> exitsSets) {
        Map<Cell, FiniteCellSet> unusedExitCells = new HashMap<>();
        for (FiniteCellSet exitsSet : exitsSets) {
            // Map each cell in exit sets to its exit set.
            for (Cell cell : exitsSet) {
                unusedExitCells.put(cell, exitsSet);
            }
        }

        UndirectedGraph<CellSet, CellSegment> graph = new SimpleGraph<>(this::obtainEdge);
        for (FiniteCellSet exitSet : exitsSets) {
            for (Cell exitCell : exitSet) {
                if (!unusedExitCells.containsKey(exitCell)) {
                    continue;
                }
                for (Cell waveCell : Wave.from(exitCell).goingOver(walkableCells)) {
                    if (unusedExitCells.containsKey(waveCell)) {
                        if (!exitSet.contains(waveCell)) {
                            // Connect only different exitSets
                            FiniteCellSet anotherEndExitSet = unusedExitCells.get(waveCell);
                            graph.addVertex(exitSet);
                            graph.addVertex(anotherEndExitSet);
                            graph.addEdge(exitSet, anotherEndExitSet, new CellSegment(exitCell, waveCell));
                        }
                        unusedExitCells.remove(waveCell);
                    }
                }
            }
        }

        return graph;
    }

    private CellSegment obtainEdge(CellSet set1, CellSet set2) {
        throw new RuntimeException("This method should not be called");
    }

    public UndirectedGraph<CellSet, CellSegment> getGraph() {
        return graph;
    }

    public static IntershapeNetworkBuilder builder() {
        return new IntershapeNetworkBuilder();
    }
}
