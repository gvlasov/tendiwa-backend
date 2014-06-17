package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.GifBuilderFactory;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class StraightSkeletonDemo implements Runnable {
	@Inject
	GifBuilderFactory factory;

	public static void main(String[] args) {
		Demos.run(StraightSkeletonDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		Config config = new Config();
		config.saveGif = false;
		config.drawToCanvas = false;
		config.startIteration = 52;
		config.numberOfIterations = 1;
		config.gifPath = System.getProperty("user.home") + "/test.gif";
		config.drawEdges = false;

		List<Point2D> points = new ArrayList<Point2D>() {{
			add(new Point2D(11, 14));
			add(new Point2D(26, 61));
			add(new Point2D(12, 92));
			add(new Point2D(78, 102));
			add(new Point2D(8, 166));
			add(new Point2D(62, 161));
			add(new Point2D(93, 185));
			add(new Point2D(125, 168));
			add(new Point2D(177, 186));
			add(new Point2D(160, 138));
			add(new Point2D(193, 122));
			add(new Point2D(142, 101));
			add(new Point2D(179, 91));
			add(new Point2D(147, 59));
			add(new Point2D(178, 6));
			add(new Point2D(89, 54));
			add(new Point2D(100, 13));
		}};
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
			gifBuilder = factory.create(canvas, 30);
		}
		int endIteration = config.startIteration + config.numberOfIterations;
		for (int i = config.startIteration; i < endIteration; i++) {
			if (config.drawToCanvas) {
				assert canvas != null;
				canvas.clear();
			}
			if (config.printDebugInfo) {
				System.out.println("Iteration " + i);
			}
			SuseikaStraightSkeleton skeleton = computeSkeleton(points, i);
			if (config.drawToCanvas) {
				if (config.drawEdges) {
					for (Segment2D edge : skeleton.originalEdges()) {
						assert canvas != null;
						canvas.draw(edge, DrawingSegment2D.withColor(Color.red));
					}
				}
				assert canvas != null;
				canvas.drawString(String.valueOf(i), 100, 100, Color.black);
				canvas.draw(skeleton.graph(), DrawingGraph.withColor(Color.cyan));
				if (config.saveGif) {
					gifBuilder.saveFrame();
				}
			}
		}

		if (config.saveGif) {
			assert gifBuilder != null;
			gifBuilder.saveAnimation(config.gifPath);
		}
//		SuseikaStraightSkeleton skeleton = TwakStraightSkeleton.create(points);
//		for (Segment2D segment : skeleton.graph().edgeSet()) {
//			canvas.draw(segment, DrawingSegment2D.withColor(Color.red));
//		}
//		for (Segment2D segment : skeleton.cap(10).edgeSet()) {
//			canvas.draw(segment, DrawingSegment2D.withColor(Color.blue));
//		}
	}

	private SuseikaStraightSkeleton computeSkeleton(List<Point2D> points, int i) {
		List<Point2D> derivative = new ArrayList<>(points.size());
		int j = 0;
		for (Point2D point : points) {
			double angle = Math.PI * 2 / (180 / (j % 6 + 1)) * i;
			derivative.add(
				new Point2D(
					point.x + Math.cos(angle) * 6,
					point.y + Math.sin(angle) * 6
				)
			);
			j++;
		}
		return new SuseikaStraightSkeleton(derivative);
	}

	private static class Config {
		public boolean saveGif = false;
		public String gifPath = System.getProperty("user.home") + "/test.gif";
		public int startIteration = 0;
		public int numberOfIterations = 120;
		public boolean printDebugInfo = true;
		public boolean drawToCanvas = true;
		public boolean drawEdges = true;
	}

}
