package org.tendiwa.demos;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.settlements.CityDrawer;
import org.tendiwa.drawing.*;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshevDistanceBuffer;
import org.tendiwa.geometry.extensions.ChebyshevDistanceBufferBorder;
import org.tendiwa.geometry.extensions.IntershapeNetwork;
import org.tendiwa.noise.Noise;
import org.tendiwa.noise.SimpleNoiseSource;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBoundsFactory;
import org.tendiwa.settlements.CityBuilder;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;

import static java.awt.Color.*;
import static org.tendiwa.geometry.DSL.rectangle;

public class CoastlineDemo implements Runnable {
    int maxCityRadius = 35;
    int minDistanceFromCoastToCityBorder = 3;
    int minDistanceBetweenCityCenters = maxCityRadius * 3;
    int minDistanceFromCoastToCityCenter = 20;
    SimpleNoiseSource noise = (x, y) -> Noise.noise(
            ((double) x) / 50,
            ((double) y) / 50,
            6
    );
    Rectangle worldSize = new Rectangle(20, 20, 200, 200);
    CellSet water = (x, y) -> noise.noise(x, y) <= 128;
    CachedCellSet borderWithCityCenters = new CachedCellSet(
            new ChebyshevDistanceBufferBorder(
                    minDistanceFromCoastToCityCenter,
                    (x, y) -> worldSize.contains(x, y) && water.contains(x, y)
            ),
            worldSize
    );
    CachedCellSet cellsCloseToCoast = new CachedCellSet(
            new ChebyshevDistanceBuffer(
                    minDistanceFromCoastToCityBorder,
                    (x, y) -> worldSize.contains(x, y) && water.contains(x, y)
            ),
            worldSize
    );
    DistantCellsFinder cityCenters = new DistantCellsFinder(
            borderWithCityCenters,
            minDistanceBetweenCityCenters
    );
    //    @Inject
//    @Named("scale2")
    public TestCanvas canvas = new TestCanvas(2, worldSize.x+worldSize.getMaxX(), worldSize.y+worldSize.getMaxY());
    DrawingAlgorithm<Cell> grassColor = DrawingCell.withColor(Color.GREEN);
    DrawingAlgorithm<Cell> waterColor = DrawingCell.withColor(BLUE);

    CityBoundsFactory boundsFactory = new CityBoundsFactory(water, this);
    private Rectangle worldSizeStretchedBy1 = worldSize.stretch(1);

    public static void main(String[] args) {
        Demos.run(
                CoastlineDemo.class,
                new DrawingModule(),
                new LargerScaleCanvasModule()
        );
    }

    @Override
    public void run() {
        System.out.println(worldSize+" "+worldSize.getMaxX());
        drawTerrain();
//        canvas.draw(borderWithCityCenters, DrawingCellSet.withColor(Color.RED));
        Collection<FiniteCellSet> shapeExitsSets = new HashSet<>();
        MutableCellSet citiesCells = new ScatteredMutableCellSet();
        for (Cell cell : cityCenters) {
            int maxCityRadius = this.maxCityRadius + cell.x % 30 - 15;
            Rectangle cityBoundRec = Recs
                    .rectangleByCenterPoint(cell, maxCityRadius * 2 + 1, maxCityRadius * 2 + 1)
                    .intersectionWith(worldSize)
                    .get();
            CachedCellSet coast = new CachedCellSet(
                    new ChebyshevDistanceBufferBorder(minDistanceFromCoastToCityBorder, water),
                    cityBoundRec
            );
            BoundedCellSet cityShape = new PathTable(
                    cell.x,
                    cell.y,
                    (x, y) -> worldSizeStretchedBy1.contains(x, y) && !coast.contains(x, y),
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
                    .withRoadsFromPoint(4)
                    .withSecondaryRoadNetworkDeviationAngle(0.1)
                    .withRoadSegmentLength(10)
                    .withConnectivity(1)
                    .withMaxStartPointsPerCycle(3)
                    .build();
            citiesCells.addAll(cityShape);
            canvas.draw(city, new CityDrawer());
            FiniteCellSet exitCells = city
                    .getCells()
                    .stream()
                    .flatMap(c -> c
                            .exitsOnCycles()
                            .stream()
                            .filter(p -> c
                                    .secondaryRoadNetwork()
                                    .edgeSet()
                                    .stream()
                                    .anyMatch(e -> e.start.equals(p) || e.end.equals(p))
                            )
                            .map(Point2D::toCell)
                    )
                    .collect(CellSet.toCellSet());
            shapeExitsSets.add(exitCells);
        }

        CellSet spaceBetweenCities = (x, y) ->
                worldSize.contains(x, y)
                        && !water.contains(x, y)
                        && !citiesCells.contains(x, y)
                        && !cellsCloseToCoast.contains(x, y);
        IntershapeNetwork network = IntershapeNetwork.builder()
                .withShapeExits(shapeExitsSets)
                .withWalkableCells(spaceBetweenCities
                )
                .build();
        Wave wave = Wave.from(new Cell(177, 119)).goingOver(spaceBetweenCities).in8Directions();
//        for (Cell cell : wave) {
//            canvas.draw(cell, DrawingCell.withColor(Color.DARK_GRAY));
//        }
//        canvas.draw(citiesCells, DrawingCellSet.withColor(Color.DARK_GRAY));
        for (FiniteCellSet exits : shapeExitsSets) {
            canvas.draw(exits, DrawingCellSet.withColor(Color.RED));
        }

        for (CellSegment segment : network.getGraph().edgeSet()) {
            canvas.draw(segment, DrawingCellSegment.withColor(Color.RED));
        }
//        canvas.draw(cellsCloseToCoast, DrawingCellSet.withColor(Color.PINK));

    }

    private void drawTerrain() {
        for (int i = worldSize.x; i <= worldSize.getMaxX(); i++) {
            for (int j = worldSize.y; j <= worldSize.getMaxY(); j++) {
                canvas.draw(new Cell(i, j), water.contains(i, j) ? waterColor : grassColor);
            }
        }
    }
}
