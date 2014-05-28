package org.tendiwa.demos;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import org.tendiwa.drawing.*;
import org.tendiwa.drawing.extensions.DrawingCell;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.noise.Noise;
import org.tendiwa.pathfinding.astar.AStar;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.terrain.BlobArea;
import org.tendiwa.terrain.CellParams;

import java.awt.*;
import java.util.List;

import static java.awt.Color.GREEN;
import static java.awt.Color.RED;

public class NoiseDemo implements Runnable {

	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(NoiseDemo.class);
	}

	@Override
	public void run() {
		int width = 800;
		int height = 600;
		terrain(width, height);
		astar();
		blob(width, height);
	}

	private void terrain(int width, int height) {
		canvas = Demos.createCanvas();
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
					Color lighterGrey = new Color((int) (noise * 1.2), (int) (noise * 1.2), (int) (noise * 0.2));
					canvas.drawCell(point, lighterGrey);
				} else if (noise > 125) {
					canvas.drawCell(point, GREEN);
				} else {
					Color darkerGrey = new Color((int) (noise * 0.3), (int) (noise * 0.4), (int) (noise * 0.4));
					canvas.drawCell(point, darkerGrey);
				}
			}
		}
		System.out.println("Terrain draw: " + time);
	}

	private void astar() {
		Cell start = new Cell(387, 480);
		Cell end = new Cell(770, 500);
		Stopwatch time = Stopwatch.createStarted();
		List<Cell> path = new AStar((cell, neighbor) -> {
			int noise = noise(cell.getX(), cell.getY(), 7);
			return (double) (noise < 145 && noise > 125 ? 1 : 10000) * cell.diagonalComponent(neighbor);
		}).path(start, end);
		System.out.println("AStar: " + time);
		for (Cell cell : path) {
			canvas.drawCell(cell, RED);
		}

		canvas.draw(Recs.rectangleByCenterPoint(start, 5, 5), DrawingRectangle.withColor(RED));
		canvas.draw(Recs.rectangleByCenterPoint(end, 5, 5), DrawingRectangle.withColor(Color.PINK));
	}

	private void blob(int width, int height) {
		Stopwatch time = Stopwatch.createStarted();
		final Rectangle maxBound = new Rectangle(0, 0, width, height);
		BlobArea<TestParams> blob = new BlobArea<>(
			maxBound,
			new PathTable(
				140,
				105,
				(x, y) -> {
					if (!maxBound.contains(x, y)) {
						return false;
					}
					int noise = noise(x, y, 7);
					return noise < 145 && noise > 125;
				},
				200
			).computeFull(),
			(x, y) -> new TestParams((x + y) % 19)
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
