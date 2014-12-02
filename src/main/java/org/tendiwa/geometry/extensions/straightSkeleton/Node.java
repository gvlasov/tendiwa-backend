package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.*;

import static org.tendiwa.geometry.Vectors2D.perpDotProduct;

class Node implements Iterable<Node> {
	Bisector bisector;
	private boolean isProcessed = false; // As said in 1a in [Obdrzalek 1998, paragraph 2.1]
	boolean isReflex;
	Segment2D previousEdge;
	Segment2D currentEdge;
	Node next;
	Node previous;
	final Point2D vertex;
	static DrawableInto canvas;

	Node(Segment2D previousEdge, Segment2D currentEdge, Point2D point) {
		assert !previousEdge.equals(currentEdge);
		assert !(previousEdge.end.equals(currentEdge.start) && previousEdge.isParallel(currentEdge));
		this.previousEdge = previousEdge;
		this.currentEdge = currentEdge;
		this.vertex = point;
	}

	/**
	 * Remembers that this point is processed, that is, it is not a part of some LAV anymore.
	 */
	void setProcessed() {
		isProcessed = true;
	}

	boolean isProcessed() {
		return isProcessed;
	}

	public void computeReflexAndBisector() {
		assert bisector == null;
		isReflex = isReflex(
			previous.vertex,
			vertex,
			vertex,
			next.vertex
		);
		bisector = new Bisector(previousEdge, currentEdge, vertex, isReflex);
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


	void connectWithPrevious(Node previous) {
		assert previous != this;
		assert previous != null;
		this.previous = previous;
		previous.next = this;
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
					drawLav();
					canvas.draw(node.vertex, DrawingPoint2D.withColorAndSize(Color.green, 2));
					canvas.draw(start.vertex, DrawingPoint2D.withColorAndSize(Color.yellow, 2));
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
	public boolean isInTheSameLav(Node node) {
		// TODO: This is too heavy
//		assert ImmutableList.copyOf(node).contains(this) == ImmutableList.copyOf(this).contains(node);
		return ImmutableList.copyOf(this).contains(node);
	}

}
