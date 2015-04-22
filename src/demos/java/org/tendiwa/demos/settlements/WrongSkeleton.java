package org.tendiwa.demos.settlements;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.MagnifierCanvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;
import static org.tendiwa.geometry.GeometryPrimitives.polygon;

final class WrongSkeleton implements Runnable {
	public static void main(String[] args) {
		Demos.run(WrongSkeleton.class);
	}

	@Override
	public void run() {

		TestCanvas.canvas = new MagnifierCanvas(8, 106, 313, 800, 600);
		TestCanvas.canvas.fillBackground(Color.black);
		Polygon polygon = polygon(
			point2D(90.33787304725506, 310.4611836101028),
			point2D(93.01693269911772, 298.8975188016948),
			point2D(107.66497747019191, 300.47261753099804),
			point2D(111.55052422883735, 307.2128517041585),
			point2D(117.65356664607543, 317.7996960075764),
			point2D(100.32645286810238, 327.78829307674016)

//			new Point2D(114.92147159816457, 280.4928994094585),
//			new Point2D(115.92716523888777, 271.73615546145686),
//			new Point2D(132.02828665993084, 259.8722211491459),
//			new Point2D(137.69825894208628, 270.6967641831246),
//			new Point2D(141.30839937476287, 277.58876829577804),
//			new Point2D(132.1868076554181, 280.6293434807123),
//			new Point2D(122.33475591835601, 283.9133607263997)
		);
		TestCanvas.canvas.draw(
			new DrawablePolygon.Thin(
				polygon,
				Color.red
			)
		);
		new SuseikaStraightSkeleton(polygon).cap(3.3);
	}
}
