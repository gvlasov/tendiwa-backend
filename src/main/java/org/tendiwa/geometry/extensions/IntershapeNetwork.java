package org.tendiwa.geometry.extensions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellSegment;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Builds a graph where vertices are some {@link org.tendiwa.geometry.CellSet}s, and edges are paths from a Cell in
 * one CellSet to a Cell in another one.
 * <p>
 * Use {@link org.tendiwa.geometry.extensions.IntershapeNetworkBuilder} to build instances of this class.
 */
public class IntershapeNetwork {
    private final CellSet walkableCells;
    private UndirectedGraph<CellSet, CellSegment> graph;

    IntershapeNetwork(CellSet walkableCells, Collection<BoundedCellSet> shapeExitSets) {
        this.walkableCells = walkableCells;
        this.graph = buildGraph(shapeExitSets);
    }

    private UndirectedGraph<CellSet, CellSegment> buildGraph(Collection<BoundedCellSet> vertices) {
        UndirectedGraph<CellSet, CellSegment> graph = new SimpleGraph<>(this::obtainEdge);
        for (BoundedCellSet cellSet : vertices) {
            Stream<BoundedCellSet> otherShapeExitSets = vertices.stream().filter(a -> a != cellSet);
            for (Cell cell : cellSet) {
                if (otherShapeExitSets.anyMatch(a -> a.contains(cell))) {

                }
                if ( walkableCells.contains(cell)) {

                }
            }
        }

        return graph;
    }

    private CellSegment obtainEdge(CellSet set1, CellSet set2) {
        throw new RuntimeException();
    }


    public UndirectedGraph<CellSet, CellSegment> getGraph() {
        return graph;
    }


}
