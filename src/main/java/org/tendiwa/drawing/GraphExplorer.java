package org.tendiwa.drawing;

import com.google.common.collect.ImmutableSet;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.tendiwa.geometry.BasicRectangle;
import org.tendiwa.geometry.BasicRectangle2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class GraphExplorer {

	public static final Dimension DEFAULT_SIZE = new Dimension(800, 600);
	private final int vertexSize;
	private final JGraphModelAdapter<Point2D, Segment2D> adapter;

	public GraphExplorer(UndirectedGraph<Point2D, Segment2D> graph, int width, int height, int vertexSize) {
		this.vertexSize = vertexSize;
		graph = PlanarGraphs.copyGraph(graph);
		adapter = new JGraphModelAdapter<>(graph);
		JGraph jgraph = new JGraph(adapter);
		for (Point2D vertex : graph.vertexSet()) {
			positionVertex(vertex);
		}
		for (Segment2D edge : ImmutableSet.copyOf(graph.edgeSet())) {
			disableLabel(edge);
		}
		jgraph.setEdgeLabelsMovable(false);

		adjustDisplaySettings(jgraph);
		JFrame frame = new JFrame("Hello");
		jgraph.setPreferredSize(new Dimension(width, height));
		JScrollPane scrollpane = new JScrollPane(jgraph);
		frame.getContentPane().add(scrollpane);
		frame.setPreferredSize(DEFAULT_SIZE);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}

	private void disableLabel(Segment2D edge) {
		DefaultEdge edgeCell = adapter.getEdgeCell(edge);
		AttributeMap attributes = edgeCell.getAttributes();
		GraphConstants.setEditable(attributes, true);
		GraphConstants.setLabelEnabled(attributes, false);
		GraphConstants.setLabelAlongEdge(attributes, false);
		Map cellAttr = new HashMap<>();
		cellAttr.put(edgeCell, attributes);
		adapter.edit(cellAttr, null, null, null);
	}

	private void adjustDisplaySettings(JGraph jg) {
		jg.setPreferredSize(DEFAULT_SIZE);
		Color c = Color.white;
		jg.setBackground(c);
	}

	private void positionVertex(Point2D vertex) {
		DefaultGraphCell cell = adapter.getVertexCell(vertex);
		AttributeMap attributes = cell.getAttributes();
		GraphConstants.setBounds(
			attributes,
			new Rectangle(
				(int) vertex.x() - vertexSize / 2,
				(int) vertex.y() - vertexSize / 2,
				vertexSize,
				vertexSize
			)
		);
		GraphConstants.setEditable(attributes, true);
		GraphConstants.setMoveable(attributes, true);
		GraphConstants.setLabelEnabled(attributes, false);
		GraphConstants.setLabelAlongEdge(attributes, false);
		GraphConstants.setResize(attributes, false);
		Map cellAttr = new HashMap<>();
		cellAttr.put(cell, attributes);
		adapter.edit(cellAttr, null, null, null);
	}
}
