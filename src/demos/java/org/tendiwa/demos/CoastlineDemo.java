package org.tendiwa.demos;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Direction;
import org.tendiwa.core.Directions;
import org.tendiwa.demos.settlements.CityDrawer;
import org.tendiwa.drawing.*;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.CachedCellBufferBorder;
import org.tendiwa.geometry.extensions.ChebyshevDistanceCellBufferBorder;
import org.tendiwa.noise.Noise;
import org.tendiwa.noise.SimpleNoiseSource;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBoundsFactory;
import org.tendiwa.settlements.CityBuilder;
import org.tendiwa.settlements.SettlementGenerationException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

import static java.awt.Color.*;
import static java.util.stream.IntStream.rangeClosed;

public class CoastlineDemo implements Runnable {
    private final SimpleNoiseSource noise;
    @Inject
    @Named("scale2")
    TestCanvas canvas;
    private final int worldWidth = 300;
    private final int worldHeight = 200;

    public static void main(String[] args) {
        Demos.run(
                CoastlineDemo.class,
                new DrawingModule(),
                new LargerScaleCanvasModule()
        );
    }

    public CoastlineDemo() {

        this.noise = (x, y) -> Noise.noise(
                ((double) x) / 32,
                ((double) y) / 32,
                6
        );
    }

    @Override
    public void run() {

        BiFunction<Integer, Integer, Boolean> isWater = (x, y) -> noise.noise(x, y) <= 127;
        drawTerrain(isWater);
        UndirectedGraph<Point2D, Segment2D> cityGraph =
                new CityBoundsFactory(
                        worldWidth,
                        worldHeight,
                        isWater
                ).create(
                        new Cell(100, 100),
                        58,
                        2
                );
        City city = new CityBuilder(cityGraph)
                .withDefaults()
                .withMaxStartPointsPerCycle(3)
                .build();
        canvas.draw(city, new CityDrawer());
    }

    private void drawTerrain(BiFunction<Integer, Integer, Boolean> isWater) {
        DrawingAlgorithm<Cell> grass = DrawingCell.withColor(Color.GREEN);
        DrawingAlgorithm<Cell> water = DrawingCell.withColor(BLUE);
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                canvas.draw(new Cell(i, j), isWater.apply(i, j) ? water : grass);
            }
        }
    }
}
