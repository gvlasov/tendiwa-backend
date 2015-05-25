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
import org.tendiwa.geometry.extensions.WobblingPolygon;
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

	private final class WobblingDemoPolygon extends WobblingPolygon {

		@Override
		protected Point2D wobble(Point2D p, int part) {
			double angle = Math.PI * 2 / (numberOfParts() / (this.indexOf(p) % 6 + 1)) * part;
			return p.add(vector(Math.cos(angle) * 6, Math.sin(angle) * 6));
		}

		public WobblingDemoPolygon(int parts) {
			super(100, parts);
			new ConvexAndReflexAmoeba().forEach(this::add);
		}
	}

	@Override
	public void run() {
		int parts = 180;
		WobblingPolygon points = new WobblingDemoPolygon(parts);
		Stream<AnimationFrame> frames = IntStream.range(0, parts)
			.mapToObj(i ->
					new DrawableStraightSkeleton(
						new SuseikaStraightSkeleton(
							points.wobbled(i)
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
}
