package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.GeometryPrimitives;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.MinimalCycle;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.graph2D;
import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;

public class MinimumCycleBasisDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(MinimumCycleBasisDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		Graph2D graph =
			graph2D(
				graphConstructor()
					.cycleOfVertices(new ConvexAndReflexAmoeba())
					.graph()
			);
		canvas.draw(
			new DrawableGraph2D.CircleVertices(
				graph,
				Color.red,
				1
			)
		);
		canvas.drawAll(
			PlanarGraphs
				.minimumCycleBasis(graph)
				.minimalCyclesSet()
				.stream()
				.map(MinimalCycle::vertexList)
				.map(GeometryPrimitives::polygon),
			polygon -> new DrawablePolygon(polygon, Color.blue)
		);
	}
}
