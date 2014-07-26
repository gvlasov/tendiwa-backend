package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.TwakStraightSkeleton;
import org.tendiwa.graphs.GraphConstructor;

import java.awt.Color;
import java.util.ArrayList;

public class TwakStraightSkeletonDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(TwakStraightSkeletonDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		TestCanvas.canvas = canvas;
		ArrayList<Point2D> outline = new ArrayList<Point2D>() {
			{
				add(new Point2D(180.72952748831898, 191.4373363442016));
				add(new Point2D(188.01480425711347, 191.43733634420164));
				add(new Point2D(188.0148042571135, 177.9677366760435));
				add(new Point2D(180.729527488319, 177.9677366760435));

//				add(new Point2D(275.37223087024483, 277.1526705023358)); // 0
//				add(new Point2D(275.37223087024483, 316.14435311884364));// 1
//				add(new Point2D(293.0464609167674, 316.6129529989434));// 2
//				add(new Point2D(320.26854938614287, 304.0051022952947));// 3
//				add(new Point2D(350.0, 300.0));// 4
//				add(new Point2D(356.27549177582637, 270.6637050231026));// 5
//				add(new Point2D(357.31057830102753, 240.68156707589563));// 6
//				add(new Point2D(359.40958310169833, 224.7357942727199));// 7
//				add(new Point2D(361.5085879023691, 208.79002146954417));// 8
//				add(new Point2D(357.4027219307088, 179.07231858687234));// 9
//				add(new Point2D(350.0, 150.0));// 10
//				add(new Point2D(322.20965020190454, 161.30028575300184));// 11
//				add(new Point2D(292.80445772954346, 155.35598969178497));// 12
//				add(new Point2D(276.1203992634727, 156.76178932405404));// 13
//				add(new Point2D(276.1203992634727, 196.9391442724032));// 14
//				add(new Point2D(313.20804206277865, 196.93914427240318));// 15
//				add(new Point2D(320.0013656408967, 223.5184245102361)); // 16
//				add(new Point2D(295.50228599445984, 223.5184245102361));// 17
//				add(new Point2D(282.2565383027814, 223.5184245102361));// 18
//				add(new Point2D(281.75493326225904, 242.8029669422194));// 19
//				add(new Point2D(315.2513492934395, 242.80296694221937));// 20
//				add(new Point2D(320.0013656408967, 242.80296694221937));// 21
//				add(new Point2D(320.0013656408967, 263.70896808474635));// 22
//				add(new Point2D(313.7756574073829, 263.70896808474635));// 23
//				add(new Point2D(281.2111528958208, 263.70896808474635));// 24
			}
		};
		UndirectedGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			.cycleOfVertices(outline)
//			.edge(7, 16)
			.graph();
		canvas.draw(TwakStraightSkeleton.create(outline).cap(3), DrawingGraph.basis(Color.gray, Color.red, Color.black));


	}
}
