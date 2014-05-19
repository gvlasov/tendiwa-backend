package org.tendiwa.settlements;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Direction;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//TODO: This class mutates constructor argument. Check that out.
class EdgeReducer {

    private final CardinalDirection[] growingDirs = {CardinalDirection.N, CardinalDirection.E};
    private UndirectedGraph<Point2D, Segment2D> graph;
    private BiMap<Cell, Point2D> map;

    public EdgeReducer(UndirectedGraph<Point2D, Segment2D> graph, BiMap<Cell, Point2D> map) {

        this.graph = graph;
        this.map = map;
    }

    private Collection<Cell> findIntersectionCells() {
        Collection<Cell> answer = new HashSet<>();
        for (Cell cell : map.keySet()) {
            int neighbourCells = 0;
            for (Direction dir : CardinalDirection.values()) {
                if (map.containsKey(cell.moveToSide(dir))) {
                    neighbourCells++;
                }
            }
            if (neighbourCells > 2) {
                answer.add(cell);
            }
        }
        return answer;
    }

    public void reduceEdges() {
        boolean changesMade;
        Collection<Point2D> finalVertices = new HashSet<>();
        Set<Point2D> vertices = ImmutableSet.copyOf(graph.vertexSet());
        do {
            changesMade = false;
            Collection<Cell> intersectionCells = findIntersectionCells();
            for (Point2D point : vertices) {
                if (!graph.containsVertex(point) || finalVertices.contains(point)) {
                    continue;
                }
                Cell graphCell = map.inverse().get(point);
                for (CardinalDirection dir : growingDirs) {
                    int combinedEdgeLength = 1;
                    Cell movedCell = graphCell;
                    while (true) {
                        movedCell = movedCell.moveToSide(dir);
                        if (map.containsKey(movedCell) && graph.containsVertex(map.get(movedCell))) {
                            combinedEdgeLength++;
                            if (intersectionCells.contains(movedCell)) {
                                break;
                            }
                        } else {
                            movedCell = movedCell.moveToSide(dir.opposite());
                            break;
                        }
                    }
                    Cell oppositeMovedCell = graphCell;
                    while (true) {
                        oppositeMovedCell = oppositeMovedCell.moveToSide(dir.opposite());
                        if (map.containsKey(oppositeMovedCell) && graph.containsVertex(map.get(oppositeMovedCell))) {
                            combinedEdgeLength++;
                            if (intersectionCells.contains(oppositeMovedCell)) {
                                break;
                            }
                        } else {
                            oppositeMovedCell = oppositeMovedCell.moveToSide(dir);
                            break;
                        }
                    }
                    if (combinedEdgeLength > 2) {
                        for (
                                Cell cell = movedCell.moveToSide(dir.opposite());
                                !cell.equals(oppositeMovedCell);
                                cell = cell.moveToSide(dir.opposite())
                                ) {
                            graph.removeVertex(map.get(cell));
                        }
                        changesMade = true;
//                            canvas.drawCell(oppositeMovedCell, YELLOW);
//                            canvas.drawCell(movedCell, RED);
                        graph.addEdge(map.get(movedCell), map.get(oppositeMovedCell));
                        finalVertices.add(map.get(movedCell));
                        finalVertices.add(map.get(oppositeMovedCell));
                    } else if (combinedEdgeLength == 2) {
//                            if (!graph.containsEdge(map.get(movedCell), map.get(oppositeMovedCell))) {
//                                canvas.draw(movedCell, DrawingCell.withColorAndSize(Color.BLACK, 1));
//                                canvas.draw(oppositeMovedCell, DrawingCell.withColorAndSize(Color.BLACK, 1));
//                                throw new AssertionError(movedCell + " " + oppositeMovedCell);
//                            }
                    }
                }
            }

        } while (changesMade);
    }
}
