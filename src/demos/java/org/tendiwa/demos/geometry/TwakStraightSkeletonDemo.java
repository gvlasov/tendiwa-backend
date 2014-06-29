package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.BlockRegion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class TwakStraightSkeletonDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(TwakStraightSkeletonDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
//		UndirectedGraph<Point2D, Segment2D> cap = TwakStraightSkeleton.create(
//			new PointTrail(20, 20)
//				.moveBy(22, 0)
//				.moveBy(0, 20)
//				.moveBy(-20, 0)
//				.points()
//		).cap(1);
//		canvas.draw(cap, DrawingGraph.basis(Color.cyan, Color.red, Color.blue));

		ArrayList<Point2D> outline = new ArrayList<Point2D>() {
			{
				add(new Point2D(100,100));
				add(new Point2D(170,100));
				add(new Point2D(170,110));
				add(new Point2D(100,110));
			}
		};
		BlockRegion.canvas = canvas;
		BlockRegion originalBlock = new BlockRegion(outline, new Random(0));
		canvas.draw(originalBlock, DrawingEnclosedBlock.withColor(Color.blue));
		Set<BlockRegion> blocks = originalBlock.subdivideLots(7, 7, 1);
		for (BlockRegion block : blocks) {
			canvas.draw(block, DrawingEnclosedBlock.withColor(Color.red));
		}
	}
}
