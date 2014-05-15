package org.tendiwa.demos;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.settlements.CityDrawer;
import org.tendiwa.drawing.*;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshevDistanceCellBufferBorder;
import org.tendiwa.geometry.extensions.IntershapeNetwork;
import org.tendiwa.geometry.extensions.IntershapeNetworkBuilder;
import org.tendiwa.noise.Noise;
import org.tendiwa.noise.SimpleNoiseSource;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBoundsFactory;
import org.tendiwa.settlements.CityBuilder;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

import static java.awt.Color.BLUE;
import static org.tendiwa.geometry.DSL.rectangle;

public class CoastlineDemo implements Runnable {
    int maxCityRadius = 35;
    int minDistanceFromCityBorderToWater = 3;
    int minDistanceBetweenCityCenters = maxCityRadius * 3;
    int minDistanceFromCoastToCityCenter = 20;
    SimpleNoiseSource noise = (x, y) -> Noise.noise(
            ((double) x) / 50,
            ((double) y) / 50,
            6
    );
    Rectangle worldSize = rectangle(400, 300);
    CellSet water = (x, y) -> noise.noise(x, y) <= 127;
    CachedCellSet borderWithCityCenters = new CachedCellSet(
            new ChebyshevDistanceCellBufferBorder(
                    minDistanceFromCoastToCityCenter,
                    (x, y) -> worldSize.contains(x, y) && water.contains(x, y)
            ),
            worldSize
    );
    DistantCellsFinder cityCenters = new DistantCellsFinder(
            borderWithCityCenters,
            minDistanceBetweenCityCenters
    );
    @Inject
    @Named("scale2")
    public TestCanvas canvas;
    DrawingAlgorithm<Cell> grassColor = DrawingCell.withColor(Color.GREEN);
    DrawingAlgorithm<Cell> waterColor = DrawingCell.withColor(BLUE);

    CityBoundsFactory boundsFactory = new CityBoundsFactory(worldSize, water);

    public static void main(String[] args) {
        Demos.run(
                CoastlineDemo.class,
                new DrawingModule(),
                new LargerScaleCanvasModule()
        );
    }

    @Override
    public void run() {
        drawTerrain();
//        canvas.draw(borderWithCityCenters, DrawingCellSet.withColor(Color.RED));
        Collection<FiniteCellSet> shapeExits = new HashSet<>();
        for (Cell cell : cityCenters) {
            int maxCityRadius = this.maxCityRadius + cell.x % 30 - 15;
            Rectangle cityBoundRec = Recs
                    .rectangleByCenterPoint(cell, maxCityRadius * 2 + 1, maxCityRadius * 2 + 1)
                    .intersectionWith(worldSize)
                    .get();
            CachedCellSet coast = new CachedCellSet(
                    new ChebyshevDistanceCellBufferBorder(minDistanceFromCityBorderToWater, water),
                    cityBoundRec
            );
            BoundedCellSet cityShape = new PathTable(
                    cell.x,
                    cell.y,
                    (x, y) -> worldSize.contains(x, y) && !coast.contains(x, y),
                    maxCityRadius
            ).computeFull();
//            canvas.draw(cell, DrawingCell.withColorAndSize(Color.black, 6));
//            canvas.draw(cityShape, DrawingCellSet.withColor(Color.BLACK));
            UndirectedGraph<Point2D, Segment2D> cityBounds = boundsFactory.create(
                    cityShape,
                    cell,
                    maxCityRadius
            );
//            canvas.draw(cityBounds, DrawingGraph.withColorAndVertexSize(RED, 2));
            City city = new CityBuilder(cityBounds)
                    .withDefaults()
                    .withRoadsFromPoint(3)
                    .withSecondaryRoadNetworkDeviationAngle(0.6)
                    .withRoadSegmentLength(5, 13)
                    .withConnectivity(0.2)
                    .withMaxStartPointsPerCycle(3)
                    .build();
            canvas.draw(city, new CityDrawer());
        }
        IntershapeNetwork network = IntershapeNetwork.builder()
                .withShapeExits(shapeExits)
                .withWalkableCells((x, y) -> !water.contains(x, y))
                .build();
        for (CellSegment segment : network.getGraph().edgeSet()) {
            canvas.draw(segment, DrawingCellSegment.withColor(Color.RED));
        }

    }

    private void drawTerrain() {
        for (int i = 0; i < worldSize.width; i++) {
            for (int j = 0; j < worldSize.height; j++) {
                canvas.draw(new Cell(i, j), water.contains(i, j) ? waterColor : grassColor);
            }
        }
    }
}
