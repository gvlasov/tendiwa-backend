package demos.settlements;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityCell;
import org.tendiwa.settlements.SecondaryRoad;

import java.awt.*;

public class CityDrawer extends DrawingAlgorithm<City> {
    private final Color highLevelGraphColor = Color.BLUE;
    private final Color lowLevelGraphColor = Color.RED;
    private final DrawingAlgorithm<Cell> highLevelVertexDrawingAlgorithm =
            DrawingCell.withColorAndSize(
                    highLevelGraphColor, 7
            );
    private final DrawingAlgorithm<Cell> lowLevelVertexDrawingAlgorithm =
            DrawingCell.withColorAndSize(
                    lowLevelGraphColor, 4
            );

    @Override
    public void draw(City city) {
        for (Line2D edge : city.getHighLevelRoadGraph().edgeSet()) {
//					drawLine(
//						edge.start.x,
//						edge.start.y,
//						edge.end.x,
//						edge.end.y,
//						highLevelGraphColor
//					);
        }
        for (Point2D vertex : city.getHighLevelRoadGraph().vertexSet()) {
//					canvas.draw(
//						new Cell((int) vertex.x, (int) vertex.y),
//						highLevelVertexDrawingAlgorithm
//					);
        }
        for (Line2D roadSegment : city.getLowLevelRoadGraph().edgeSet()) {
            drawLine(
                    roadSegment.start.x,
                    roadSegment.start.y,
                    roadSegment.end.x,
                    roadSegment.end.y,
                    lowLevelGraphColor
            );
        }
        for (Point2D vertex : city.getLowLevelRoadGraph().vertexSet()) {
//					canvas.draw(
//						new Cell((int) vertex.x, (int) vertex.y),
//						lowLevelVertexDrawingAlgorithm
//					);
        }
        for (CityCell cityCell : city.getCells()) {
            for (SecondaryRoad road : cityCell.secRoadNetwork.edgeSet()) {
                Line2D line = road.toLine();
                if (!road.start.isDeadEnd || !road.end.isDeadEnd) {
                    drawLine(
                            line.start.x,
                            line.start.y,
                            line.end.x,
                            line.end.y,
                            Color.GREEN
                    );
                }
            }
//					for (Point2D point : cityCell.roadCycle.vertexSet()) {
//						canvas.draw(new Cell((int) point.x, (int) point.y), lowLevelVertexDrawingAlgorithm);
//					}

        }

    }
    public CityDrawer() {

    }
}
