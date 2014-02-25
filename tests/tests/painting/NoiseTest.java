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
import org.tendiwa.noise.Noise;
import org.tendiwa.pathfinding.astar.AStar;
import org.tendiwa.pathfinding.astar.MovementCost;

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
	System.out.println(time);
	for (Cell cell : path) {
		canvas.draw(cell, DrawingCell.withColor(Color.RED));
	}

	canvas.draw(Recs.rectangleByCenterPoint(start, 5, 5), DrawingRectangle.withColor(Color.RED));
	canvas.draw(Recs.rectangleByCenterPoint(end, 5, 5), DrawingRectangle.withColor(Color.PINK));
	try {
		Thread.sleep(100000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}

public int noise(int x, int y, int octave) {
	return Noise.noise(
		((double) x) / 32,
		((double) y) / 32,
		octave
	);
}
}
