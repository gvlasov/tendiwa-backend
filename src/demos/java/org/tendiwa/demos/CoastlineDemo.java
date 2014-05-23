package org.tendiwa.demos;

import com.google.common.base.Stopwatch;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.settlements.CityDrawer;
import org.tendiwa.drawing.*;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.*;
import org.tendiwa.noise.Noise;
import org.tendiwa.noise.SimpleNoiseSource;
import org.tendiwa.pathfinding.astar.AStar;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBoundsFactory;
import org.tendiwa.settlements.CityBuilder;
import org.tendiwa.settlements.NetworkWithinCycle;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.awt.Color.*;

public class CoastlineDemo implements Runnable {
    static Stopwatch watch = Stopwatch.createStarted();
    public TestCanvas canvas;

    public static void main(String[] args) {
        Demos.run(
                CoastlineDemo.class,
                new DrawingModule(),
                new LargerScaleCanvasModule()
        );
    }

    @Override
    public void run() {
        int maxCityRadius = 35;
        int minDistanceFromCoastToCityBorder = 3;
        int minDistanceBetweenCityCenters = maxCityRadius * 3;
        int minDistanceFromCoastToCityCenter = 20;
        SimpleNoiseSource noise = (x, y) -> Noise.noise(
                ((double) x + 500) / 50,
                ((double) y + 800) / 40,
                7
        );
        Rectangle worldSize = new Rectangle(20, 20, 400, 400);
        CellSet water = (x, y) -> noise.noise(x, y) <= 110;
        System.out.println("constants: " + watch);
        CellSet reducingMask = (x, y) -> (x + y) % 20 == 0;
        ChebyshevDistanceBufferBorder cityCenterBorder = new ChebyshevDistanceBufferBorder(
                minDistanceFromCoastToCityCenter,
                (x, y) -> worldSize.contains(x, y) && water.contains(x, y)
        );
        FiniteCellSet borderWithCityCenters = new ScatteredCellSet(
                reducingMask.and(cityCenterBorder),
                worldSize
        );
        System.out.println(borderWithCityCenters.toSet().size());
        System.out.println("City centers: " + watch);
        CachedCellSet cellsCloseToCoast = new CachedCellSet(
                new ChebyshevDistanceBuffer(
                        minDistanceFromCoastToCityBorder,
                        (x, y) -> worldSize.contains(x, y) && water.contains(x, y)
                ),
                worldSize
        );
        System.out.println("Cells close to coast: " + watch);
        DistantCellsFinder cityCenters = new DistantCellsFinder(
                borderWithCityCenters,
                minDistanceBetweenCityCenters
        );
        System.out.println("Distant cells: " + watch);
        //    @Inject
//    @Named("scale2")
        DrawingAlgorithm<Cell> grassColor = DrawingCell.withColor(Color.GREEN);
        DrawingAlgorithm<Cell> waterColor = DrawingCell.withColor(BLUE);

        CityBoundsFactory boundsFactory = new CityBoundsFactory(water, this);
        Rectangle worldSizeStretchedBy1 = worldSize.stretch(1);
        canvas = new TestCanvas(2, worldSize.x + worldSize.getMaxX(), worldSize.y + worldSize.getMaxY());
        canvas.draw(borderWithCityCenters, DrawingCellSet.withColor(Color.PINK));
        drawTerrain(worldSize, water, waterColor, grassColor);
        System.out.println("draw terrain: " + watch);
//        canvas.draw(borderWithCityCenters, DrawingCellSet.withColor(Color.RED));
        Collection<FiniteCellSet> shapeExitsSets = new HashSet<>();
        MutableCellSet citiesCells = new ScatteredMutableCellSet();
        for (Cell cell : cityCenters) {
            System.out.println("0 " + watch);
            int maxCityRadiusModified = maxCityRadius + cell.x % 30 - 15;
            Rectangle cityBoundRec = Recs
                    .rectangleByCenterPoint(cell, maxCityRadiusModified * 2 + 1, maxCityRadiusModified * 2 + 1)
                    .intersectionWith(worldSize)
                    .get();
            CachedCellSet coast = new CachedCellSet(
                    new ChebyshevDistanceBufferBorder(minDistanceFromCoastToCityBorder, water),
                    cityBoundRec
            );
            System.out.println("1 " + watch);
            BoundedCellSet cityShape = new PathTable(
                    cell.x,
                    cell.y,
                    (x, y) -> worldSizeStretchedBy1.contains(x, y) && !coast.contains(x, y),
                    maxCityRadiusModified
            ).computeFull();
            System.out.println("2 " + watch);
//            canvas.draw(cell, DrawingCell.withColorAndSize(Color.black, 6));
//            canvas.draw(cityShape, DrawingCellSet.withColor(Color.BLACK));
            UndirectedGraph<Point2D, Segment2D> cityBounds = boundsFactory.create(
                    cityShape,
                    cell,
                    maxCityRadiusModified
            );
            System.out.println("3 " + watch);
//            canvas.draw(cityBounds, DrawingGraph.withColorAndVertexSize(RED, 2));
            City city = new CityBuilder(cityBounds)
                    .withDefaults()
                    .withRoadsFromPoint(4)
                    .withSecondaryRoadNetworkDeviationAngle(0.1)
                    .withRoadSegmentLength(10)
                    .withConnectivity(1)
                    .withMaxStartPointsPerCycle(3)
                    .build();
            System.out.println("4 " + watch);
            citiesCells.addAll(ShapeFromOutline.from(city.getLowLevelRoadGraph()));
            System.out.println("5 " + watch);
            canvas.draw(city, new CityDrawer());
            FiniteCellSet exitCells = null;
            try {
                exitCells = city
                        .getCells()
                        .stream()
                        .flatMap(c -> c
                                .exitsOnCycles()
                                .stream()
                                .filter(p -> c
                                        .network()
                                        .edgeSet()
                                        .stream()
                                        .anyMatch(e -> e.start.equals(p) || e.end.equals(p))
                                )
                                .map(Point2D::toCell)
                        )
                        .collect(CellSet.toCellSet());
            } catch (Exception exc) {
                TestCanvas cvs = new TestCanvas(2, worldSize.x + worldSize.getMaxX(),
                        worldSize.y + worldSize.getMaxY());
                System.out.println("aaaaaaaaaaaaa" + city.getCells().size());
                for (NetworkWithinCycle net : city.getCells()) {
                    cvs.draw(net.cycle().asGraph(), DrawingGraph.withColorAndAntialiasing(Color.BLACK));
                }
                throw new RuntimeException();
            }
            System.out.println("6 " + watch);
            shapeExitsSets.add(exitCells);
            System.out.println("7 " + watch);
        }
        System.out.println("for loop: " + watch);
        CellSet shapeExitsCombined = shapeExitsSets
                .stream()
                .map(a -> (CellSet) a)
                .reduce(CellSet.empty(), (a, b) -> a.or(b));
        System.out.println("combined sets: " + watch);


        CellSet spaceBetweenCities = new CachedCellSet(
                (x, y) ->
                worldSize.contains(x, y)
                        && (!water.contains(x, y)
                        && !citiesCells.contains(x, y)
                        && !cellsCloseToCoast.contains(x, y) || shapeExitsCombined.contains(x, y)),
                worldSize
        );
        System.out.println("space between cities: " + watch);
        IntershapeNetwork network = IntershapeNetwork.builder()
                .withShapeExits(shapeExitsSets)
                .withWalkableCells(spaceBetweenCities)
                .build();
        System.out.println("network: " + watch);
//        for (Cell cell : wave) {
//            canvas.draw(cell, DrawingCell.withColor(Color.DARK_GRAY));
//        }
        canvas.draw(citiesCells, DrawingCellSet.withColor(Color.DARK_GRAY));
        for (FiniteCellSet exits : shapeExitsSets) {
            canvas.draw(exits, DrawingCellSet.withColor(Color.RED));
        }

        AtomicInteger i = new AtomicInteger(0);
        for (CellSegment segment : network.getGraph().edgeSet()) {
//            canvas.draw(segment, DrawingCellSegment.withColor(Color.RED));
            List<Cell> path = new AStar(
                    (cell, neighbor) -> {
//                        canvas.draw(neighbor, DrawingCell.withColor(Color.CYAN));
                        return spaceBetweenCities.contains(neighbor) ? 1 : 100000000;
                    }
            ).path(segment.start, segment.end);
            path.stream().forEach(c -> canvas.draw(c, DrawingCell.withColor(Color.RED)));
        }
        System.out.println("final drawing: " + watch);
//        canvas.draw(cellsCloseToCoast, DrawingCellSet.withColor(Color.PINK));

    }

    private void drawTerrain(
            Rectangle worldSize,
            CellSet water,
            DrawingAlgorithm<Cell> waterColor,
            DrawingAlgorithm<Cell> grassColor
    ) {
        for (int i = worldSize.x; i <= worldSize.getMaxX(); i++) {
            for (int j = worldSize.y; j <= worldSize.getMaxY(); j++) {
                canvas.draw(new Cell(i, j), water.contains(i, j) ? waterColor : grassColor);
            }
        }
    }
}
