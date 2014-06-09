package org.tendiwa.demos.geometry;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class StraightSkeletonDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(StraightSkeletonDemo.class);
	}

	@Override
	public void run() {
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
//		TestCanvas canvas = new TestCanvas(1, 200, 200);
		for (int i = 3; i < 100; i++) {
//			canvas.clear();
			System.out.println(i);
			List<Point2D> derivative = new ArrayList<>(points.size());
			int j = 0;
			for (Point2D point : points) {
				double angle = Math.PI * 2 / (60 / (j % 6 + 1)) * i;
				derivative.add(
					new Point2D(
						point.x + Math.cos(angle) * 6,
						point.y + Math.sin(angle) * 6
					)
				);
				j++;
			}
			SuseikaStraightSkeleton skeleton = new SuseikaStraightSkeleton(derivative);
			for (Segment2D edge : skeleton.originalEdges()) {
//				canvas.draw(edge, DrawingSegment2D.withColor(Color.red));
			}
//			canvas.draw(skeleton.graph(), DrawingGraph.withColor(Color.cyan));
			}

//		SuseikaStraightSkeleton skeleton = TwakStraightSkeleton.create(points);
//		for (Segment2D segment : skeleton.graph().edgeSet()) {
//			canvas.draw(segment, DrawingSegment2D.withColor(Color.red));
//		}
//		for (Segment2D segment : skeleton.cap(10).edgeSet()) {
//			canvas.draw(segment, DrawingSegment2D.withColor(Color.blue));
//		}
//		Skeleton.skeleton(points).;


		}

	}
