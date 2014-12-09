package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.tendiwa.geometry.Vectors2D.perpDotProduct;

/**
 * A node in a circular list of active vertices.
 */
abstract class Node implements Iterable<Node> {
	Bisector bisector;
	private boolean isProcessed = false; // As said in 1a in [Obdrzalek 1998, paragraph 2.1]
	boolean isReflex;
	private Node next;
	private Node previous;
	protected InitialNode currentEdgeStart;
	protected InitialNode previousEdgeStart;
	protected Segment2D currentEdge;
	final Point2D vertex;
	static DrawableInto canvas;

	Node(Point2D point, InitialNode previousEdgeStart, InitialNode currentEdgeStart) {
		this(point);
		currentEdge = currentEdgeStart.currentEdge;
		this.currentEdgeStart = currentEdgeStart;
		this.previousEdgeStart = previousEdgeStart;
		assert !previousEdge().equals(currentEdge()) : previousEdge();
		assert !(previousEdge().equals(currentEdge())
			&& previousEdge().isParallel(previousEdge()));
	}

	protected Node(Point2D vertex) {
		this.vertex = vertex;
	}

	Node next() {
		assert next != null;
		return next;
	}

	Node previous() {
		assert previous != null;
		return previous;
	}

	Segment2D previousEdge() {
		return previousEdgeStart.currentEdge;
	}

	public Segment2D currentEdge() {
		return currentEdgeStart.currentEdge;
	}

	/**
	 * Remembers that this point is processed, that is, it is not a part of some LAV anymore.
	 */
	void setProcessed() {
		isProcessed = true;
	}

	public boolean isProcessed() {
		return isProcessed;
	}
	abstract boolean isSplitRightNode();
	abstract boolean isSplitLeftNode();

	public void computeReflexAndBisector() {
		assert bisector == null;
		isReflex = isReflex(
			previous.vertex,
			vertex,
			vertex,
			next.vertex
		);
		bisector = new Bisector(previousEdgeStart.currentEdge, currentEdgeStart.currentEdge, vertex, isReflex);
	}

	/**
	 * Finds if two edges going counter-clockwise make a convex or a reflex angle.
	 *
	 * @param a1
	 * 	Start of the first edge.
	 * @param a2
	 * 	End of the first edge.
	 * @param b1
	 * 	Start of the second edge.
	 * @param b2
	 * 	End of the second edge.
	 * @return True if the angle to the left between two edges > Math.PI (reflex), false otherwise (convex).
	 */
	private boolean isReflex(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
		return perpDotProduct(
			new double[]{a2.x - a1.x, a2.y - a1.y},
			new double[]{b2.x - b1.x, b2.y - b1.y}
		) > 0;
	}


	public void setPreviousInLav(Node previous) {
		assert previous != this;
		assert previous != null;
		this.previous = previous;
		previous.next = this;
	}

	public boolean isInLavOf2Nodes() {
		return next.next == this;
	}

	/**
	 * Iterates over the current LAV of the node. All the nodes iterated upon are non-processed.
	 */
	@Override
	public Iterator<Node> iterator() {
		if (isProcessed) {
			assert false;
		}
		return new Iterator<Node>() {
			List<Point2D> points = new ArrayList<>(100);
			Node start = Node.this;
			Node node = Node.this.previous;
			int i = 0;

			@Override
			public boolean hasNext() {
				return node.next != start || i == 0;
			}

			@Override
			public Node next() {
				node = node.next;
				if (node.isProcessed()) {
					Node current = start;
					do {
						canvas.draw(new Segment2D(current.vertex, current.next.vertex), DrawingSegment2D
							.withColorDirected(Color.cyan, 0.5));
						current = current.next;
					} while (current != node);
					throw new RuntimeException("Node not in lav");
				}
				if (++i > 100) {
					drawLav();
					throw new RuntimeException("Too many iterations");
				}
				points.add(node.vertex);
				return node;
			}

			private void drawLav() {
				Iterator<Color> colors = Iterators.cycle(Color.darkGray, Color.gray, Color.lightGray, Color.white);
				for (int i = 0; i < points.size() - 1; i++) {
					canvas.draw(new Segment2D(points.get(i), points.get(i + 1)), DrawingSegment2D.withColorThin(colors.next()));
				}
				canvas.draw(start.vertex, DrawingPoint2D.withColorAndSize(Color.yellow, 2));
			}
		};
	}

	/**
	 * Checks if this node and another node are in the same LAV.
	 *
	 * @param node
	 * 	Another node.
	 * @return true if this node and another node are in the same LAV, false otherwise.
	 */
	boolean isInTheSameLav(Node node) {
		return Iterables.contains(this, node);
	}

}
