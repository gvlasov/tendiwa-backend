package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.demos.geometry.polygons.CutUpRing;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.GifBuilderFactory;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.StraightSkeleton;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.extensions.straightSkeleton.KendziStraightSkeleton;
import org.tendiwa.geometry.extensions.straightSkeleton.PolygonShrinker;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.TwakStraightSkeleton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class StraightSkeletonDemo implements Runnable {
	@Inject
	GifBuilderFactory factory;

	public static void main(String[] args) {
		Demos.run(StraightSkeletonDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		Config config = new Config();
		config.saveGif = true;
		config.drawToCanvas = true;
		config.startIteration = 0;
		config.numberOfIterations = 1;
		config.gifPath = System.getProperty("user.home") + "/test.gif";
		config.drawEdges = true;
		config.fps = 30;

		List<Point2D> points =
//			new ConvexAndReflexAmoeba();

//			new PointTrail(40, 40)
//			.moveBy(20, 50)
//			.moveBy(-70,-10)
//			.points();

			new ArrayList<Point2D>() {
				{
					add(new Point2D(60.024228029196934, 298.052050179887));
					add(new Point2D(78.24086294622803, 269.8634092602251));
					add(new Point2D(93.96578423739712, 288.1795873192004));
				}
			};


//			new PointTrail(80, 20)
//			.moveBy(20, 0)
//			.moveBy(0, 20)
//			.moveBy(20, 0)
//			.moveBy(0, 40)
//			.moveBy(-60, 0)
//			.moveBy(0, -40)
//			.moveBy(20, 0)
//			.points();
		buildSkeleton(config, points);
	}

	private void buildSkeleton(Config config, List<Point2D> points) {
		TestCanvas canvas = null;
		GifBuilder gifBuilder = null;
		if (config.saveGif) {
			config.drawToCanvas = true;
		}
		if (config.drawToCanvas) {
			canvas = new TestCanvas(1, 200, 200);
			gifBuilder = factory.create(canvas, config.fps);
			PolygonShrinker.canvas = canvas;
		}
		int endIteration = config.startIteration + config.numberOfIterations;
		PrimitiveIterator.OfInt shrunkDepth = IntStream.generate(new IntSupplier() {
			boolean forward = true;
			int i = 14;
			int maxI = 30;

			@Override
			public int getAsInt() {
				if (forward) {
					i++;
				} else {
					i--;
				}
				if (i == maxI || i == 1) {
					forward = !forward;
				}
				return i;
			}
		}).iterator();
		for (int i = config.startIteration; i < endIteration; i++) {
			if (config.drawToCanvas) {
				assert canvas != null;
				canvas.clear();
			}
			if (config.printDebugInfo) {
				System.out.println("Iteration " + i);
			}
			final int iteration = i;
			StraightSkeleton skeleton = new SuseikaStraightSkeleton(
				points.stream().map(p -> {
					double angle = Math.PI * 2 / (180 / (points.indexOf(p) % 6 + 1)) * iteration;
					return new Point2D(
						p.x/* + Math.cos(angle) * 6,*/,
						p.y /*+ Math.sin(angle) * 6*/
					);
				}).collect(toList())
			);
			if (config.drawToCanvas) {
				if (config.drawEdges) {
					for (Segment2D edge : skeleton.originalEdges()) {
						assert canvas != null;
						canvas.draw(edge, DrawingSegment2D.withColor(Color.red));
					}
				}
				assert canvas != null;
				canvas.drawString(String.valueOf(i), 40, 15, Color.lightGray);
//				canvas.draw(skeleton.graph(), DrawingGraph.withColor(Color.cyan));
				canvas.draw(skeleton.cap(shrunkDepth.next()), DrawingGraph.withColor(Color.green));
				if (config.saveGif) {
					gifBuilder.saveFrame();
				}
			}
		}

		if (config.saveGif) {
			assert gifBuilder != null;
			gifBuilder.saveAnimation(config.gifPath);
		}
	}

	private static class Config {
		public boolean saveGif = false;
		public String gifPath = System.getProperty("user.home") + "/test.gif";
		public int startIteration = 0;
		public int numberOfIterations = 180;
		public boolean printDebugInfo = true;
		public boolean drawToCanvas = true;
		public boolean drawEdges = true;
		public int fps = 30;
	}

}
