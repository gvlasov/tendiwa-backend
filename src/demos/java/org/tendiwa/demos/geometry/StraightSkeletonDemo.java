package org.tendiwa.demos.geometry;

import org.tendiwa.demos.Demos;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.drawing.AnimationFrame;
import org.tendiwa.drawing.extensions.DrawableStraightSkeleton;
import org.tendiwa.drawing.extensions.DrawableText;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.Gif;
import org.tendiwa.files.FileInHome;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;

import java.awt.Color;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public class StraightSkeletonDemo implements Runnable {

	public static void main(String[] args) {
		Demos.run(StraightSkeletonDemo.class, new DrawingModule());
	}

	@Override
	public void run() {

		Polygon points = new ConvexAndReflexAmoeba();
		Stream<AnimationFrame> frames = IntStream.range(0, 180)
			.mapToObj(i ->
					new DrawableStraightSkeleton(
						new SuseikaStraightSkeleton(
							ithVersionOfPolygon(points, i)
						),
						Color.red,
						Color.cyan
					).andThen(
						new DrawableText(
							String.valueOf(i),
							point2D(40, 15),
							Color.lightGray
						)
					)
			)
			.map(AnimationFrame::new);
		new Gif(
			new FileInHome("skeleton_demo.gif"),
			rectangle(200, 200),
			30,
			frames
		).save();
	}

	private List<Point2D> ithVersionOfPolygon(Polygon points, int i) {
		return points.stream().map(p -> {
			double angle = Math.PI * 2 / (180 / (points.indexOf(p) % 6 + 1)) * i;
			return p.add(vector(Math.cos(angle) * 6, Math.sin(angle) * 6));
		}).collect(toList());
	}
}
