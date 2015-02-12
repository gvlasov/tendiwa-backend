package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingMinimalCycle;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;

import static org.tendiwa.drawing.extensions.DrawingMinimalCycle.withColor;

public class MinimumCycleBasisDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(MinimumCycleBasisDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		UndirectedGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			.cycleOfVertices(new ConvexAndReflexAmoeba())
			.graph();
		canvas.draw(graph, DrawingGraph.withColorAndVertexSize(Color.red, 1));
		PlanarGraphs.minimumCycleBasis(graph)
			.minimalCyclesSet()
			.forEach(cycle -> canvas.draw(
				cycle,
				withColor(Color.green, Point2DVertexPositionAdapter.get())
			));
	}
}
