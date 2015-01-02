package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Iterators;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import javax.annotation.Nullable;
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
	protected Node next;
	private Node previous;
	/**
	 * Along with {@link #previousEdgeStart}, determines the direction of {@link #bisector} as well as two faces that
	 * this Node divides.
	 */
	protected OriginalEdgeStart currentEdgeStart;
	protected OriginalEdgeStart previousEdgeStart;
	protected Segment2D currentEdge;
	final Point2D vertex;
	static DrawableInto canvas;

	Node(Point2D point, OriginalEdgeStart previousEdgeStart, OriginalEdgeStart currentEdgeStart) {
		this(point);
		if (previousEdgeStart == currentEdgeStart) {
			assert false;
		}
		currentEdge = currentEdgeStart.currentEdge;
		this.currentEdgeStart = currentEdgeStart;
		this.previousEdgeStart = previousEdgeStart;
		if (previousEdge().equals(currentEdge())) {
			assert false;
		}
		assert !(previousEdge().equals(currentEdge())
			&& previousEdge().isParallel(previousEdge()));
	}

	/**
	 * Adds {@code newNode} to faces at {@link #currentEdgeStart} and {@link #previousEdgeStart} <i>if</i> it is
	 * necessary.
	 */
	void growAdjacentFaces(Node newNode) {
		growLeftFace(newNode);
		growRightFace(newNode);
	}

	void growRightFace(Node newNode) {
		growFace(newNode, currentEdgeStart);
	}

	void growLeftFace(Node newNode) {
		growFace(newNode, previousEdgeStart);
	}

	private void growFace(Node newNode, OriginalEdgeStart faceStart) {
		faceStart.face.addLink(this, newNode);
		assert faceStart.face.startHalfface.firstSkeletonNode() == faceStart;
	}

	protected Node(Point2D vertex) {
		this.vertex = vertex;
	}

	abstract boolean hasPair();

	SplitNode getPair() {
		throw new RuntimeException(this.getClass().getName() + " can't have a pair; only SplitNode can");
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
		assert !isProcessed;
		isProcessed = true;
	}

	public boolean isProcessed() {
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
		bisector = new Bisector(previousEdgeStart.currentEdge, currentEdgeStart.currentEdge, vertex, isReflex);
	}

	/*
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
				checkLavCorrectness();
				points.add(node.vertex);
				return node;
			}

			private void checkLavCorrectness() {
				if (++i > 1000) {
					drawLav();
					throw new RuntimeException("Too many iterations");
				}
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
	 * A usual node is never a pair for some other node. Only a {@link SplitNode} may be a pair to another {@link
	 * SplitNode}.
	 *
	 * @param node
	 * 	Another node.
	 * @return true if this node and {@code node} were created by the same {@link org.tendiwa.geometry.extensions
	 * .straightSkeleton.SplitEvent}, false otherwise.
	 */
	public boolean isPair(Node node) {
		return false;
	}

	@Nullable
	protected SkeletonEvent computeNearerBisectorsIntersection() {
		// Non-convex 1c
		RayIntersection nextIntersection = bisector.intersectionWith(next().bisector);
		EdgeEvent sameLineIntersection = trySameLineIntersection(nextIntersection, this, next());
		if (sameLineIntersection != null) {
			return sameLineIntersection;
		}

		RayIntersection previousIntersection = bisector.intersectionWith(previous().bisector);
		sameLineIntersection = trySameLineIntersection(previousIntersection, this, previous());
		if (sameLineIntersection != null) {
			return sameLineIntersection;
		}

		Point2D nearer = null;
		Node va = null;
		Node vb = null;
		if (nextIntersection.r > 0 || previousIntersection.r > 0) {
			if (previousIntersection.r < 0 && nextIntersection.r > 0 || nextIntersection.r > 0 && nextIntersection.r <= previousIntersection.r) {
				if (next().bisector.intersectionWith(bisector).r > 0 && nextIntersection.r > 0) {
					nearer = nextIntersection.getLinesIntersectionPoint();
					va = this;
					vb = next();
				}
			} else if (nextIntersection.r < 0 && previousIntersection.r > 0 || previousIntersection.r > 0 && previousIntersection.r <= nextIntersection.r) {
				if (previous().bisector.intersectionWith(bisector).r > 0 && previousIntersection.r > 0) {
					nearer = previousIntersection.getLinesIntersectionPoint();
					va = previous();
					vb = this;
				}
			}
		}
		if (isReflex) {
			SkeletonEvent splitPoint = findSplitEvent();
			if (
				nearer == null
					|| splitPoint != null
					&& vertex.distanceTo(splitPoint.point) < vertex.distanceTo(nearer)
				) {
				return splitPoint;
			}
		}
		assert nearer == null || va != null && vb != null;
		assert va == null && vb == null || va.next() == vb;
		if (nearer == null) {
			return null;
		}
		return new EdgeEvent(nearer, va, vb);
	}

	@Nullable
	private static EdgeEvent trySameLineIntersection(RayIntersection intersection, Node current, Node target) {
		if (Double.isInfinite(intersection.r)) {
			return new EdgeEvent(
				new Point2D(
					(target.vertex.x + current.vertex.x) / 2,
					(target.next().vertex.y + current.vertex.y) / 2
				),
				current,
				target
			);
		}
		return null;
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, figure 4]
	 * <p>
	 * Computes the point where a split event occurs.
	 *
	 * @return The point where split event occurs, or null if there is no split event emanated from {@code reflexNode}.
	 */
	@Nullable
	private SplitEvent findSplitEvent() {
		assert isReflex;
		Point2D splitPoint = null;
		Node originalEdgeStart = null;
		for (Node node : this) {
			if (nodeIsAppropriate(node)) {
				Point2D point = computeSplitPoint(node.currentEdge());
				if (isPointInAreaBetweenEdgeAndItsBisectors(point, node)) {
					if (newSplitPointIsBetter(splitPoint, point)) {
						splitPoint = point;
						originalEdgeStart = node;
					}
				}
			}
		}
		if (splitPoint == null) {
			return null;
		}
		return new SplitEvent(
			splitPoint,
			this,
			originalEdgeStart.currentEdgeStart
		);
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Computes point B_i.
	 *
	 * @param oppositeEdge
	 * 	The tested line segment.
	 * @return Intersection between the bisector at {@code currentNode} and the axis of the angle between one of the
	 * edges starting at {@code currentNode} and the tested line segment {@code oppositeEdge}.
	 */
	private Point2D computeSplitPoint(Segment2D oppositeEdge) {
		assert isReflex;
		Point2D bisectorStart;
		if (previousEdge().isParallel(oppositeEdge)) {
			bisectorStart = new RayIntersection(currentEdge, oppositeEdge).getLinesIntersectionPoint();
		} else {
			bisectorStart = new RayIntersection(previousEdge(), oppositeEdge).getLinesIntersectionPoint();
		}
		Bisector anotherBisector = new Bisector(
			new Segment2D(
				vertex,
				bisectorStart
			),
			new Segment2D(
				bisectorStart,
				new RayIntersection(
					bisector.segment,
					oppositeEdge
				).getLinesIntersectionPoint()
			),
			bisectorStart,
			false
		);
		return anotherBisector.intersectionWith(bisector).getLinesIntersectionPoint();
	}

	private boolean nodeIsAppropriate(Node node) {
		return !(nodeIsNeighbor(node)
			|| intersectionIsBehindReflexNode(node)
			|| previousEdgeIntersectsInFrontOfOppositeEdge(node)
			|| currentEdgeIntersectsInFrontOfOppositeEdge(node));
	}

	private boolean newSplitPointIsBetter(Point2D oldSplitPoint, Point2D newSplitPoint) {
		return oldSplitPoint == null
			|| vertex.distanceTo(oldSplitPoint) > vertex.distanceTo(newSplitPoint);
	}

	private boolean nodeIsNeighbor(Node node) {
		return node == this || node == previous() || node == next();
	}

	private boolean currentEdgeIntersectsInFrontOfOppositeEdge(Node oppositeEdgeStartCandidate) {
		return new RayIntersection(
			currentEdge.reverse(),
			oppositeEdgeStartCandidate.currentEdge
		).r <= 1;
	}

	private boolean previousEdgeIntersectsInFrontOfOppositeEdge(Node oppositeEdgeStartCandidate) {
		return new RayIntersection(
			previousEdge(),
			oppositeEdgeStartCandidate.currentEdge
		).r <= 1;
	}

	private boolean intersectionIsBehindReflexNode(Node anotherRay) {
		return new RayIntersection(
			bisector.segment,
			anotherRay.currentEdge
		).r <= Vectors2D.EPSILON;
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Checks if a point (namely point B coming from a reflex vertex) is located in an area bounded by an edge and
	 * bisectors coming from start and end nodes of this edge.
	 *
	 * @param point
	 * 	The point to test.
	 * @param currentNode
	 * 	A node at which starts the area-forming edge.
	 * @return true if the point is located within the area marked by an edge and edge's bisectors, false otherwise.
	 */
	private static boolean isPointInAreaBetweenEdgeAndItsBisectors(Point2D point, Node currentNode) {
		Bisector currentBisector = currentNode.bisector;
		Bisector nextBisector = currentNode.next().bisector;
		Point2D a = currentBisector.segment.end;
		Point2D b = currentNode.currentEdge.start;
		Point2D c = currentNode.currentEdge.end;
		Point2D d = nextBisector.segment.end;
		return isPointNonConvex(a, point, b) && isPointNonConvex(b, point, c) && isPointNonConvex(c, point, d);
	}

	/**
	 * Given 3 counter-clockwise points of a polygon, check if the middle one is convex or reflex.
	 *
	 * @param previous
	 * 	Beginning of vector 1.
	 * @param point
	 * 	End of vector 1 and beginning of vector 2.
	 * @param next
	 * 	End of vector 2.
	 * @return true if {@code point} is non-convex, false if it is convex.
	 */
	private static boolean isPointNonConvex(Point2D previous, Point2D point, Point2D next) {
		return perpDotProduct(
			new double[]{point.x - previous.x, point.y - previous.y},
			new double[]{next.x - point.x, next.y - point.y}
		) >= 0;
	}
}
