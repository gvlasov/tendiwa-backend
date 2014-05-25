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
import java.util.concurrent.TimeUnit;

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
		PieChartTest chart = new PieChartTest(400);
		int maxCityRadius = 35;
		int minDistanceFromCoastToCityBorder = 3;
		int minDistanceBetweenCityCenters = maxCityRadius * 3;
		int minDistanceFromCoastToCityCenter = 20;
		SimpleNoiseSource noise = (x, y) -> Noise.noise(
			((double) x + 0) / 50,
			((double) y + 0) / 40,
			7
		);
		Rectangle worldSize = new Rectangle(20, 20, 400, 400);
		CellSet water = (x, y) -> noise.noise(x, y) <= 110;
		chart.add("Constants", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
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
		chart.add("City centers", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
		CachedCellSet cellsCloseToCoast = new CachedCellSet(
			new ChebyshevDistanceBuffer(
				minDistanceFromCoastToCityBorder,
				(x, y) -> worldSize.contains(x, y) && water.contains(x, y)
			),
			worldSize
		);
		chart.add("Cells close to coast", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
		DistantCellsFinder cityCenters = new DistantCellsFinder(
			borderWithCityCenters,
			minDistanceBetweenCityCenters
		);
		chart.add("Distant cells", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
		//    @Inject
//    @Named("scale2")
		DrawingAlgorithm<Cell> grassColor = DrawingCell.withColor(Color.GREEN);
		DrawingAlgorithm<Cell> waterColor = DrawingCell.withColor(BLUE);

		CityBoundsFactory boundsFactory = new CityBoundsFactory(water, this);
		Rectangle worldSizeStretchedBy1 = worldSize.stretch(1);
		canvas = new TestCanvas(2, worldSize.x + worldSize.getMaxX(), worldSize.y + worldSize.getMaxY());
		canvas.draw(borderWithCityCenters, DrawingCellSet.withColor(Color.PINK));
		drawTerrain(worldSize, water, waterColor, grassColor);
		chart.add("Draw terrain", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
//        canvas.draw(borderWithCityCenters, DrawingCellSet.withColor(Color.RED));
		Collection<FiniteCellSet> shapeExitsSets = new HashSet<>();
		MutableCellSet citiesCells = new ScatteredMutableCellSet();
		for (Cell cell : cityCenters) {
			chart.add("0", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
			int maxCityRadiusModified = maxCityRadius + cell.x % 30 - 15;
			Rectangle cityBoundRec = Recs
				.rectangleByCenterPoint(cell, maxCityRadiusModified * 2 + 1, maxCityRadiusModified * 2 + 1)
				.intersectionWith(worldSize)
				.get();
			CachedCellSet coast = new CachedCellSet(
				new ChebyshevDistanceBufferBorder(minDistanceFromCoastToCityBorder, water),
				cityBoundRec
			);
			chart.add("1", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
			BoundedCellSet cityShape = new PathTable(
				cell.x,
				cell.y,
				(x, y) -> worldSizeStretchedBy1.contains(x, y) && !coast.contains(x, y),
				maxCityRadiusModified
			).computeFull();
			chart.add("2", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
//            canvas.draw(cell, DrawingCell.withColorAndSize(Color.black, 6));
//            canvas.draw(cityShape, DrawingCellSet.withColor(Color.BLACK));
			UndirectedGraph<Point2D, Segment2D> cityBounds = boundsFactory.create(
				cityShape,
				cell,
				maxCityRadiusModified
			);
			chart.add("3", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
//            canvas.draw(cityBounds, DrawingGraph.withColorAndVertexSize(RED, 2));
			City city = new CityBuilder(cityBounds)
				.withDefaults()
				.withRoadsFromPoint(4)
				.withSecondaryRoadNetworkDeviationAngle(0.1)
				.withRoadSegmentLength(10)
				.withConnectivity(1)
				.withMaxStartPointsPerCycle(3)
				.build();
			chart.add("4", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
			citiesCells.addAll(ShapeFromOutline.from(city.getLowLevelRoadGraph()));
			chart.add("5", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
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
			chart.add("6", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
			shapeExitsSets.add(exitCells);
			chart.add("7", watch.elapsed(TimeUnit.MILLISECONDS));
			watch.reset().start();
		}
		watch.reset().start();
		CellSet shapeExitsCombined = shapeExitsSets
			.stream()
			.map(a -> (CellSet) a)
			.reduce(CellSet.empty(), (a, b) -> a.or(b));
		chart.add("Combined sets", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();


		CellSet spaceBetweenCities = new CachedCellSet(
			(x, y) ->
				worldSize.contains(x, y)
					&& (!water.contains(x, y)
					&& !citiesCells.contains(x, y)
					&& !cellsCloseToCoast.contains(x, y) || shapeExitsCombined.contains(x, y)),
			worldSize
		);
		chart.add("Space between cities", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
		IntershapeNetwork network = IntershapeNetwork
			.withShapeExits(shapeExitsSets)
			.withWalkableCells(spaceBetweenCities);
		chart.add("Network", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
//        for (Cell cell : wave) {
//            canvas.draw(cell, DrawingCell.withColor(Color.DARK_GRAY));
//        }
//		canvas.draw(citiesCells, DrawingCellSet.withColor(Color.DARK_GRAY));
//		for (FiniteCellSet exits : shapeExitsSets) {
//			canvas.draw(exits, DrawingCellSet.withColor(Color.RED));
//		}

		for (CellSegment segment : network.getGraph().edgeSet()) {
//            canvas.draw(segment, DrawingCellSegment.withColor(Color.RED));
			List<Cell> path = new AStar(
				(cell, neighbor) -> spaceBetweenCities.contains(neighbor) ? 1 : 100000000
			).path(segment.start, segment.end);
			path.stream().forEach(c -> canvas.draw(c, DrawingCell.withColor(Color.RED)));
		}
		chart.add("Final drawing", watch.elapsed(TimeUnit.MILLISECONDS));
		watch.reset().start();
//        canvas.draw(cellsCloseToCoast, DrawingCellSet.withColor(Color.PINK));
		chart.draw();

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
