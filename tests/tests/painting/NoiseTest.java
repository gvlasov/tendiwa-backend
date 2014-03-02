package tests.painting;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.*;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.noise.Noise;
import org.tendiwa.pathfinding.astar.AStar;
import org.tendiwa.pathfinding.astar.MovementCost;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.pathfinding.dijkstra.PathWalker;
import org.tendiwa.terrain.BlobArea;
import org.tendiwa.terrain.CellParams;
import org.tendiwa.terrain.CellParamsFactory;

import java.awt.*;
import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class NoiseTest {
@Inject
TestCanvas canvas;

@Test
public void draw() {
	int width = 800;
	int height = 600;
	terrain(width, height);
	astar();
	blob(width, height);
	try {
		Thread.sleep(100000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}

private void terrain(int width, int height) {
	Stopwatch time = Stopwatch.createStarted();
	for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			int noise;
//			if (y > height /2) {
//			noise = (noise(x, y, 1) + noise(x, y, 7)) / 2;
//			} else {
			noise = noise(x, y, 7);
//			}
			Cell point = new Cell(x, y);
			if (noise > 145) {
				DrawingAlgorithm<Cell> greyscale = DrawingCell.withColor(new Color((int) (noise * 1.2), (int) (noise * 1.2), (int) (noise * 0.2)));
				canvas.draw(point, greyscale);
			} else if (noise > 125) {
				DrawingAlgorithm<Cell> green = DrawingCell.withColor(Color.GREEN);
				canvas.draw(point, green);
			} else {
				canvas.draw(point, DrawingCell.withColor(new Color((int) (noise * 0.3), (int) (noise * 0.4), (int) (noise * 0.4))));
			}
		}
	}
	System.out.println("Terrain draw: " + time);
}

private void astar() {
	Cell start = new Cell(387, 480);
	Cell end = new Cell(770, 500);
	Stopwatch time = Stopwatch.createStarted();
	List<Cell> path = new AStar(new MovementCost() {
		@Override
		public int cost(Cell cell, Cell neighbor) {
			int noise = noise(cell.getX(), cell.getY(), 7);
			return noise < 145 && noise > 125 ?
//				(Math.abs(cell.getX()-neighbor.getX()) == 1 && Math.abs(cell.getY()-neighbor.getY()) == 1 ? 14 : 10 )
				1
				: Integer.MAX_VALUE;
		}
	}).path(start, end);
	System.out.println("AStar: " + time);
	for (Cell cell : path) {
		canvas.draw(cell, DrawingCell.withColor(Color.RED));
	}

	canvas.draw(Recs.rectangleByCenterPoint(start, 5, 5), DrawingRectangle.withColor(Color.RED));
	canvas.draw(Recs.rectangleByCenterPoint(end, 5, 5), DrawingRectangle.withColor(Color.PINK));
}

private void blob(int width, int height) {
	Stopwatch time = Stopwatch.createStarted();
	final Rectangle maxBound = new Rectangle(0, 0, width, height);
	BlobArea<TestParams> blob = new BlobArea<>(
		maxBound,
		new PathTable(140, 105, new PathWalker() {
			@Override
			public boolean canStepOn(int x, int y) {
				if (!maxBound.contains(x, y)) {
					return false;
				}
				int noise = noise(x, y, 7);
				return noise < 145 && noise > 125;
			}
		}, 200).computeFull(),
		new CellParamsFactory<TestParams>() {
			@Override
			public TestParams create(int x, int y) {
				return new TestParams((x + y) % 19);
			}
		}
	);
	System.out.println("Blob: " + time);
	for (Cell cell : blob) {
		int value = blob.get(cell).value;
		canvas.draw(cell, DrawingCell.withColor(new Color(value * 255 / 19, 0, 0)));
	}
}

public int noise(int x, int y, int octave) {
	return Noise.noise(
		((double) x) / 32,
		((double) y) / 32,
		octave
	);
}

private class TestParams implements CellParams {
	private final int value;

	TestParams(int value) {
		this.value = value;
	}
}
}
