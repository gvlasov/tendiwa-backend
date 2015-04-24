package org.tendiwa.demos;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.extensions.DrawableCell;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.noise.Noise;
import org.tendiwa.pathfinding.astar.AStar;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.terrain.BlobArea;
import org.tendiwa.terrain.CellParams;

import java.awt.Color;
import java.util.List;

import static java.awt.Color.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class NoiseDemo implements Runnable {

	@Inject
	Canvas canvas;

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
		Stopwatch time = Stopwatch.createStarted();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int noise;
//			if (y > height /2) {
//			noise = (noise(x, y, 1) + noise(x, y, 7)) / 2;
//			} else {
				noise = noise(x, y, 7);
//			}
				BasicCell point = new BasicCell(x, y);
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
		BasicCell start = new BasicCell(387, 480);
		BasicCell end = new BasicCell(770, 500);
		Stopwatch time = Stopwatch.createStarted();
		List<BasicCell> path = new AStar((cell, neighbor) -> {
			int noise = noise(cell.x(), cell.y(), 7);
			return (double) (noise < 145 && noise > 125 ? 1 : 10000) * cell.diagonalComponent(neighbor);
		}).path(start, end);
		System.out.println("AStar: " + time);
		canvas.drawAll(
			path,
			cell -> new DrawableCell(cell, Color.red)
		);
		canvas.draw(
			new DrawableRectangle(
				start.centerRectangle(5, 5),
				Color.red
			)
		);
		canvas.draw(
			new DrawableRectangle(
				end.centerRectangle(5, 5),
				Color.pink
			)
		);
	}

	private void blob(int width, int height) {
		Stopwatch time = Stopwatch.createStarted();
		final Rectangle maxBound = rectangle(0, 0, width, height);
		BlobArea<TestParams> blob = new BlobArea<>(
			maxBound,
			new PathTable(
				new BasicCell(140, 105),
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
			canvas.draw(
				new DrawableCell(
					cell,
					new Color(value * 255 / 19, 0, 0)
				)
			);
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
