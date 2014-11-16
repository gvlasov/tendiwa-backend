package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.VertexPositionAdapter;

import java.awt.Color;
import java.util.List;

public class DrawingMinimalCycle {
	public static <V, E> DrawingAlgorithm<MinimalCycle<V, E>> withColor(
		Color color,
		VertexPositionAdapter<V> adapter
	) {
		return (what, canvas) -> {
			List<V> list = what.vertexList();
			V previous = list.get(list.size() - 1);
			DrawingAlgorithm<Segment2D> how = DrawingSegment2D.withColorThin(color);
			for (V v : list) {
				canvas.draw(new Segment2D(
					new Point2D(
						adapter.getX(previous),
						adapter.getY(previous)
					),
					new Point2D(
						adapter.getX(v),
						adapter.getY(v)
					)
				), how);
				previous = v;
			}
		};
	}
}
